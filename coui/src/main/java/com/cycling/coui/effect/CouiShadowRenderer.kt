package com.cycling.coui.effect

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Mesh
import android.graphics.MeshSpecification
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** 普通投影的 caster, 与组件材质的裁剪轮廓分开。 */
enum class CouiShadowOutline { Oval, SmoothCapsule, RoundRect }

/**
 * API 34+ 保留原 Skia 网格 / A8 表 / inverse clip 承载；
 * API 33 走独立的 Path + RenderNode + RenderEffect 后端。Mesh、MeshSpecification、
 * Canvas.drawMesh 是 API 34 平台类，Canvas.clipShader 是 API 35，低版本不可用。
 */
@RequiresApi(33)
class CouiShadowRenderer(surfaceCount: Int = 1) {
    private val meshAvailable = Build.VERSION.SDK_INT >= 34

    private val specifications = if (meshAvailable) Array(4) { index ->
        MeshSpecification.make(arrayOf(
            MeshSpecification.Attribute(MeshSpecification.TYPE_FLOAT2, 0, "position"),
            MeshSpecification.Attribute(MeshSpecification.TYPE_FLOAT3, 8, "shadow")), 20,
            arrayOf(MeshSpecification.Varying(MeshSpecification.TYPE_FLOAT3, "shadow")),
            ShadowVertexShader, shadowFragmentShader(fast = index < 2, oval = index % 2 == 0))
    } else null
    private val lut = Bitmap.createBitmap(128, 1, Bitmap.Config.ALPHA_8).apply {
        val bytes = ByteBuffer.allocate(rowBytes)
        repeat(128) { i ->
            val d = 1f - i / 127f
            bytes.put(floor((exp(-4f * d * d) - 0.018f) * 255f + 0.5f).toInt().toByte())
        }
        bytes.rewind()
        copyPixelsFromBuffer(bytes)
    }
    private val lutShader = BitmapShader(lut, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val clipShaders = if (meshAvailable) arrayOf(
        RuntimeShader(shadowClipShader(oval = true, image = true)),
        RuntimeShader(shadowClipShader(oval = false, image = true))) else null
    private val materialClipSource = shadowClipShader(oval = false, image = false)
    private val slots = Array(surfaceCount) { arrayOfNulls<ProjectionDraw>(2) }
    private val legacySlots = Array(surfaceCount) { arrayOfNulls<LegacyDraw>(2) }

    init {
        // 创建时检查裁剪片段, 不把失败延迟到首次绘制; API 33 后端不编译 mesh 片段。
        if (meshAvailable) RuntimeShader(materialClipSource)
    }

    /** 长度均为像素; 网格包含轴向缩放, Canvas 只承接平移, alpha 不再由外层重复相乘。 */
    fun draw(canvas: Canvas, slot: Int, width: Float, height: Float, sx: Float, sy: Float,
        windowX: Float, windowY: Float, alpha: Float, color: Int, elevation: Float,
        lightY: Float, lightZ: Float, lightRadius: Float, outline: CouiShadowOutline,
        radius: Float = min(width, height) * 0.5f, clipping: Boolean = true,
        blurRadius: Float? = null) {
        if (alpha <= 0f || width <= 0f || height <= 0f || sx <= 0f || sy <= 0f || elevation <= 0f) return
        require(!clipping || outline != CouiShadowOutline.RoundRect) { "普通圆角投影尚未提供 inverse clip" }
        val fast = isFastShadowGeometry(width, height, sx, sy, outline == CouiShadowOutline.Oval)
        for (index in 0..1) {
            // override spot 不乘 renderer 的全局 spot alpha; ambient 先转原厂 JNI 字节值。
            val multiplier = if (index == 0) 9f / 255f else 1f
            val opacity = ((alpha * multiplier) * Color.alpha(color)).toInt().coerceIn(0, 255)
            if (opacity == 0) continue
            val argb = (color and 0xFFFFFF) or (opacity shl 24)
            // 光源随主体平移; ambient 与 fast spot 用局部原点复用资源, generic spot 保留量化前坐标。
            val localOrigin = index == 0 || fast
            val key = ShadowKey(width, height, sx, sy,
                if (localOrigin) 0f else windowX, if (localOrigin) 0f else windowY, alpha < 1f,
                elevation, lightY, lightZ, lightRadius, outline,
                radius.coerceIn(0f, min(width, height) * 0.5f), clipping, blurRadius)
            if (!meshAvailable) {
                drawLegacy(canvas, slot, index, key, index == 1, argb, blurRadius)
                continue
            }
            val draw = slots[slot][index]?.takeIf { it.key == key } ?: makeDraw(key, index == 1).also {
                slots[slot][index] = it
            }
            updateColor(draw, argb)
            draw.node?.let { canvas.drawRenderNode(it) } ?: draw.mesh?.let {
                canvas.save()
                canvas.translate(draw.projection.offset.x, draw.projection.offset.y)
                // 裁剪独立使用局部坐标, Mesh 返回的 LUT 坐标只交给 Paint。
                if (Build.VERSION.SDK_INT >= 35) draw.clipShader?.let { shader -> canvas.clipShader(shader) }
                canvas.drawMesh(it, BlendMode.MODULATE, draw.paint)
                canvas.restore()
            }
        }
    }

    /**
     * API 33 阴影后端：只用 API 29/31/33 的 Path、RenderNode、RenderEffect。
     * 几何与模糊量沿用 floatingShadowGeometry 的公式，以 Skia 高斯模糊替代 A8 LUT 网格；
     * 不提供原厂 inverse clip，投影只做形状与模糊。
     */
    private fun drawLegacy(canvas: Canvas, slot: Int, index: Int, key: ShadowKey,
        spot: Boolean, argb: Int, blurRadius: Float? = null) {
        val oval = key.outline == CouiShadowOutline.Oval
        val ratio = (key.elevation / (key.lightZ - key.elevation)).coerceIn(0f, 0.95f)
        val scale = (key.lightZ / (key.lightZ - key.elevation)).coerceIn(1f, 1.95f)
        val blurPx = blurRadius ?: if (spot) key.lightRadius * ratio else min(key.elevation * 0.5f, 150f)
        val sigma = if (blurPx > 0f) blurPx * 0.57735f + 0.5f else 0f
        val rect = RectF(0f, 0f, key.width * key.sx, key.height * key.sy)
        val source = RectF(rect)
        if (spot) {
            source.left = -ratio * key.sx * key.width * 0.5f
            source.top = -ratio * key.sy * key.lightY
            source.right = source.left + rect.width() * scale
            source.bottom = source.top + rect.height() * scale
        }
        // 投影必须先比主体大，衰减与 inverse clip 才有铺开空间；否则抠掉主体内部后什么都不剩。
        source.inset(-blurPx, -blurPx)
        val draw = legacySlots[slot][index]?.takeIf { it.key == key } ?: run {
            val path = Path().apply {
                if (oval) addOval(source, Path.Direction.CW)
                else addRoundRect(source, key.radius * key.sx * scale + blurPx,
                    key.radius * key.sy * scale + blurPx,
                    if (key.outline == CouiShadowOutline.RoundRect) Path.Direction.CW else Path.Direction.CCW)
            }
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val range = 3f * sigma + 1f
            val bounds = RectF(source).apply { inset(-ceil(range), -ceil(range)) }
            val node = legacyNode(bounds, rect, oval, sigma, 1f / max(range, 1f), key.clipping)
            LegacyDraw(key, node, path, paint).also { legacySlots[slot][index] = it }
        }
        // 系统可在后台丢弃 DisplayList, 同色不代表录制内容仍然有效。
        if (draw.color != argb || !draw.node.hasDisplayList()) {
            draw.color = argb
            draw.paint.color = argb
            val recording = draw.node.beginRecording()
            recording.translate(-draw.node.left.toFloat(), -draw.node.top.toFloat())
            recording.drawPath(draw.path, draw.paint)
            draw.node.endRecording()
        }
        canvas.drawRenderNode(draw.node)
    }

    private fun legacyNode(bounds: RectF, caster: RectF, oval: Boolean, sigma: Float,
        invRange: Float, clipping: Boolean): RenderNode {
        val left = floor(bounds.left).toInt()
        val top = floor(bounds.top).toInt()
        val right = ceil(bounds.right).toInt()
        val bottom = ceil(bounds.bottom).toInt()
        // createRuntimeShaderEffect 没有带 input 的重载；作为 chain 的 outer 时输入/坐标空间不可靠。
        // 改用 createBlurEffect 的显式 inputEffect：falloff 先作用于原始内容，再整体模糊。
        val shader = RuntimeShader(legacyShadowFalloffShader(oval)).apply {
            setFloatUniform("u_rect", caster.left - left, caster.top - top,
                caster.right - left, caster.bottom - top)
            setFloatUniform("u_invRange", invRange)
            setFloatUniform("u_clipEnabled", if (clipping) 1f else 0f)
        }
        val falloff = RenderEffect.createRuntimeShaderEffect(shader, "image")
        val effect = if (sigma > 0f)
            RenderEffect.createBlurEffect(sigma, sigma, falloff, Shader.TileMode.DECAL) else falloff
        return RenderNode("COUI shadow legacy").apply {
            setPosition(left, top, right, bottom)
            setClipToBounds(false)
            setRenderEffect(effect)
        }
    }

    private fun makeDraw(key: ShadowKey, spot: Boolean): ProjectionDraw {
        val oval = key.outline == CouiShadowOutline.Oval
        val projection = floatingShadowGeometry(key.width, key.height, key.sx, key.sy, key.x, key.y,
            key.elevation, key.lightY, key.lightZ, key.lightRadius, key.transparent, oval, spot,
            key.radius, key.outline == CouiShadowOutline.RoundRect, overrideBlur = key.blurRadius)
        val rect = RectF(-projection.offset.x, -projection.offset.y,
            key.width * key.sx - projection.offset.x, key.height * key.sy - projection.offset.y)
        val data = projection.data
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply { color = Color.WHITE }
        if (data == null) return blurDraw(key, projection, oval, spot, paint)
        if (data.indices.isEmpty()) return ProjectionDraw(key, projection, paint)
        val bounds = RectF(data.vertices[0], data.vertices[1], data.vertices[0], data.vertices[1])
        for (i in data.vertices.indices step 5) {
            bounds.left = min(bounds.left, data.vertices[i])
            bounds.top = min(bounds.top, data.vertices[i + 1])
            bounds.right = max(bounds.right, data.vertices[i])
            bounds.bottom = max(bounds.bottom, data.vertices[i + 1])
        }
        // 防御：legacy 路径不会进入 makeDraw，低版本也不触碰任何 Mesh 平台类。
        if (!meshAvailable) return ProjectionDraw(key, projection, paint)
        val spec = specifications!![(if (projection.fast) 0 else 2) + if (oval && key.clipping) 0 else 1]
        val mesh = Mesh(spec, Mesh.TRIANGLES, FloatBuffer.wrap(data.vertices), data.vertices.size / 5,
            ShortBuffer.wrap(data.indices), bounds)
        if (oval && key.clipping) mesh.setFloatUniform("u_rect", rect.left, rect.top, rect.right, rect.bottom)
        if (!oval && key.clipping && projection.fast && Build.VERSION.SDK_INT < 35) {
            // Android 14 没有公开的 Shader 裁剪入口, 保留每路独立滤镜。
            val node = filteredNode(bounds, rect, oval, 0f, key.clipping)
            paint.shader = lutShader
            return ProjectionDraw(key, projection, paint, mesh = mesh, node = node)
        }
        val capsuleClip = if (!oval && key.clipping) RuntimeShader(materialClipSource).apply {
            setFloatUniform("u_rect", rect.left, rect.top, rect.right, rect.bottom)
        } else null
        paint.shader = if (projection.fast) lutShader else capsuleClip
        return ProjectionDraw(key, projection, paint, mesh = mesh,
            clipShader = capsuleClip.takeIf { projection.fast })
    }

    private fun blurDraw(key: ShadowKey, projection: ShadowProjection, oval: Boolean,
        spot: Boolean, paint: Paint): ProjectionDraw {
        val r = key.radius
        val rect = RectF(0f, 0f, key.width * key.sx, key.height * key.sy)
        val source = RectF(rect)
        if (spot) {
            val ratio = (key.elevation / (key.lightZ - key.elevation)).coerceIn(0f, 0.95f)
            source.left = -ratio * key.sx * key.width * 0.5f
            source.top = -ratio * key.sy * key.lightY
            source.right = source.left + rect.width() * projection.scale
            source.bottom = source.top + rect.height() * projection.scale
        }
        val path = Path().apply {
            if (oval) addOval(source, Path.Direction.CW)
            else addRoundRect(source, r * key.sx * projection.scale, r * key.sy * projection.scale,
                if (key.outline == CouiShadowOutline.RoundRect) Path.Direction.CW else Path.Direction.CCW)
        }
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = max(projection.stroke, 0f)
        val sigma = if (projection.blur > 0f) projection.blur * 0.57735f + 0.5f else 0f
        val bounds = RectF(source).apply { inset(-ceil(3f * sigma + paint.strokeWidth + 1f), -ceil(3f * sigma + paint.strokeWidth + 1f)) }
        val node = filteredNode(bounds, rect, oval, sigma, key.clipping)
        return ProjectionDraw(key, projection, paint, node = node, path = path)
    }

    private fun updateColor(draw: ProjectionDraw, color: Int) {
        // 系统可在后台丢弃 DisplayList, 同色不代表录制内容仍然有效。
        if (draw.color == color && draw.node?.hasDisplayList() != false) return
        draw.color = color
        draw.mesh?.let { mesh ->
            val a = Color.alpha(color) / 255f
            fun channel(value: Int): Float {
                val premultiplied = value * a
                return if (draw.projection.fast) floor(premultiplied + 0.5f) / 255f else premultiplied / 255f
            }
            mesh.setFloatUniform("u_color", channel(Color.red(color)), channel(Color.green(color)),
                channel(Color.blue(color)), a)
        }
        draw.node?.let { node ->
            // uniform 和颜色录制为快照; 仅重录命令, 不因淡入淡出重建网格。
            val recording = node.beginRecording()
            recording.translate(-node.left.toFloat(), -node.top.toFloat())
            draw.mesh?.let { recording.drawMesh(it, BlendMode.MODULATE, draw.paint) }
                ?: draw.path?.let { draw.paint.color = color; recording.drawPath(it, draw.paint) }
            node.endRecording()
        }
    }

    private fun filteredNode(bounds: RectF, caster: RectF, oval: Boolean, sigma: Float, clipping: Boolean): RenderNode {
        val left = floor(bounds.left).toInt()
        val top = floor(bounds.top).toInt()
        val right = ceil(bounds.right).toInt()
        val bottom = ceil(bounds.bottom).toInt()
        // RenderEffect 保存 uniform 快照, 两路投影可复用同一 Shader 程序。
        val clip = if (clipping) clipShaders?.get(if (oval) 0 else 1)?.apply {
            setFloatUniform("u_rect", caster.left - left, caster.top - top, caster.right - left, caster.bottom - top)
        }?.let { RenderEffect.createRuntimeShaderEffect(it, "image") } else null
        val blur = if (sigma > 0f) RenderEffect.createBlurEffect(sigma, sigma, Shader.TileMode.DECAL) else null
        val effect = when {
            clip != null && blur != null -> RenderEffect.createChainEffect(clip, blur)
            clip != null -> clip
            else -> blur
        }
        return RenderNode("COUI shadow").apply {
            setPosition(left, top, right, bottom)
            setClipToBounds(false)
            setRenderEffect(effect)
        }
    }

    private data class ShadowKey(val width: Float, val height: Float, val sx: Float, val sy: Float,
        val x: Float, val y: Float, val transparent: Boolean, val elevation: Float,
        val lightY: Float, val lightZ: Float, val lightRadius: Float, val outline: CouiShadowOutline,
        val radius: Float, val clipping: Boolean, val blurRadius: Float?)

    private class ProjectionDraw(val key: ShadowKey, val projection: ShadowProjection, val paint: Paint,
        val mesh: Mesh? = null, val node: RenderNode? = null, val path: Path? = null,
        val clipShader: RuntimeShader? = null) {
        var color: Int? = null
    }

    /** API 33 后端的每路缓存，不持有任何 API 34 平台类型。 */
    private class LegacyDraw(val key: ShadowKey, val node: RenderNode, val path: Path,
        val paint: Paint) {
        var color: Int? = null
    }
}

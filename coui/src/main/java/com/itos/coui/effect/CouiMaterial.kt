package com.itos.coui.effect

import android.graphics.BlendMode
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * 材质混色的一层：常量色 + 混合模式，叠在上一层的输出上。
 *
 * 对应原厂 `COUIMaterialBlurEffect.setViewBlurEffect` 里的
 * `RenderEffect.createColorFilterEffect(BlendModeColorFilter(color, mode), 上一层)`。
 * 背景采样为不透明时，Skia 的 `Cr = Sa·B(Dc,Sc) + (1-Sa)·Dc` 等价于 `mix(dst, blend(src,dst), src.a)`，
 * 这里按该等价式在 Shader 内一次完成，不额外申请两层 RenderEffect。
 */
@Immutable
data class CouiMaterialLayer(val color: Color, val blendMode: BlendMode) {
    init {
        require(blendMode == BlendMode.SRC_OVER || blendMode == BlendMode.OVERLAY ||
            blendMode == BlendMode.COLOR_DODGE || blendMode == BlendMode.LUMINOSITY) {
            "材质层只支持 SRC_OVER / OVERLAY / COLOR_DODGE / LUMINOSITY，收到 $blendMode"
        }
    }
}

/**
 * 材质参数：模糊半径与两层混色。
 *
 * `blurRadiusPx` 与原厂 `BlurEffectParams.blurRadius` 同单位——原厂把表里的数值直接交给
 * `RenderEffect.createBlurEffect(radius, radius, MIRROR)`，不做 density 换算，因此这里是像素。
 */
@Immutable
data class CouiMaterialPreset(
    val blurRadiusPx: Float,
    val first: CouiMaterialLayer,
    val second: CouiMaterialLayer,
) {
    init { require(blurRadiusPx.isFinite() && blurRadiusPx >= 0f) { "模糊半径必须是有限的非负像素值" } }

    /** 原厂 `isNoEffect`：半径为零且两层颜色都是 ARGB 0。 */
    val isNoEffect: Boolean
        get() = blurRadiusPx == 0f && first.color.isArgbZero() && second.color.isArgbZero()
}

private fun Color.isArgbZero(): Boolean =
    alpha == 0f && red == 0f && green == 0f && blue == 0f

/** 两层材质的渲染效果；顺序固定为先 [first] 后 [second]。 */
@RequiresApi(33)
@Composable
fun rememberCouiMaterialEffect(first: CouiMaterialLayer, second: CouiMaterialLayer): RenderEffect =
    remember(first, second) { materialEffect(first, second) }

/** 无效果预设返回 null，与原厂把 `setBackgroundRenderEffect` 置空的分支一致。 */
@RequiresApi(33)
@Composable
fun rememberCouiMaterialEffect(preset: CouiMaterialPreset): RenderEffect? = remember(preset) {
    if (preset.isNoEffect) null else materialEffect(preset.first, preset.second)
}

@RequiresApi(33)
private fun materialEffect(first: CouiMaterialLayer, second: CouiMaterialLayer): RenderEffect {
    val shader = RuntimeShader(CouiMaterialShader)
    shader.setFloatUniform("uFirstColor", first.color.red, first.color.green, first.color.blue, first.color.alpha)
    shader.setFloatUniform("uSecondColor", second.color.red, second.color.green, second.color.blue, second.color.alpha)
    shader.setIntUniform("uFirstMode", blendModeId(first.blendMode))
    shader.setIntUniform("uSecondMode", blendModeId(second.blendMode))
    return RenderEffect.createRuntimeShaderEffect(shader, "content")
}

private fun blendModeId(mode: BlendMode): Int = when (mode) {
    BlendMode.SRC_OVER -> 0
    BlendMode.OVERLAY -> 1
    BlendMode.COLOR_DODGE -> 2
    BlendMode.LUMINOSITY -> 3
    else -> error("材质层只支持 SRC_OVER / OVERLAY / COLOR_DODGE / LUMINOSITY，收到 $mode")
}

internal const val CouiMaterialShaderFunctions = """
    float lum(float3 color) {
        return dot(color, float3(0.3, 0.59, 0.11));
    }

    float3 luminosity(float3 source, float3 destination) {
        float destinationLum = lum(destination);
        float sourceLum = lum(source);
        float3 color = destination + (sourceLum - destinationLum);
        float minColor = min(min(color.x, color.y), color.z);
        float maxColor = max(max(color.x, color.y), color.z);
        if (minColor < 0.0) {
            return sourceLum + ((color - sourceLum) * sourceLum) / (sourceLum - minColor);
        } else if (maxColor > 1.0) {
            return sourceLum + ((color - sourceLum) * (1.0 - sourceLum)) / (maxColor - sourceLum);
        }
        return color;
    }

    float overlay(float source, float destination) {
        return destination < 0.5
            ? 2.0 * source * destination
            : 1.0 - 2.0 * (1.0 - source) * (1.0 - destination);
    }

    float3 overlay(float3 source, float3 destination) {
        return float3(
            overlay(source.x, destination.x),
            overlay(source.y, destination.y),
            overlay(source.z, destination.z)
        );
    }

    // 描边链的固定两段混色：先 LUMINOSITY 再 OVERLAY。
    // CouiEdge 与 CouiEdgeColorFunctions.lightEdgeReferenceColor 使用；该函数被拼进
    // 融球、菜单、通知描边所有 shader，删除会让整条链的 AGSL 编译失败。
    half4 couiMaterial(half4 inColor, float4 mixColor, float4 blendColor) {
        float3 luminosityColor = luminosity(mixColor.rgb, inColor.rgb);
        inColor.rgb = mix(inColor.rgb, luminosityColor, mixColor.a);
        float3 overlayColor = overlay(blendColor.rgb, inColor.rgb);
        inColor.rgb = mix(inColor.rgb, overlayColor, blendColor.a);
        return inColor;
    }

    float3 couiBlend(int mode, float3 source, float3 destination) {
        if (mode == 1) return overlay(source, destination);
        // MaterialFilter::appendColorDodge：不提前限幅；在最终 SDR 输出处合成。
        if (mode == 2) return destination / (1.0 - source);
        if (mode == 3) return luminosity(source, destination);
        return source;
    }
"""

private const val CouiMaterialShader = CouiMaterialShaderFunctions + """
    uniform shader content;
    uniform float4 uFirstColor;
    uniform float4 uSecondColor;
    uniform int uFirstMode;
    uniform int uSecondMode;

    half4 main(float2 coordinate) {
        half4 color = content.eval(coordinate);
        color.rgb = mix(color.rgb, couiBlend(uFirstMode, uFirstColor.rgb, color.rgb), uFirstColor.a);
        color.rgb = mix(color.rgb, couiBlend(uSecondMode, uSecondColor.rgb, color.rgb), uSecondColor.a);
        return color;
    }
"""

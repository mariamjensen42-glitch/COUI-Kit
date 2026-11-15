package com.cycling.coui.effect

import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb

/** 横向融合主体; 中心、尺寸和圆角均为绘制节点本地像素。零尺寸表示收缩后的主体。 */
data class CouiMetaballBody(
    val center: Offset,
    val size: Size,
    val cornerRadius: Float,
) {
    init {
        require(center.x.isFinite() && center.y.isFinite()) { "融合主体中心必须是有限值" }
        require(size.width.isFinite() && size.height.isFinite() && size.width >= 0f && size.height >= 0f) {
            "融合主体尺寸必须是有限的非负值"
        }
        require(cornerRadius.isFinite() && cornerRadius >= 0f) { "融合圆角必须是有限的非负值" }
    }
}

/** 原厂只在 cornerType=CONIC 时做 corner * 1.5 与 weight = 8w / (3w + 3), 取 w = 1.1。 */
private const val CouiMetaballCornerScale = 1.5f
private const val CouiMetaballWeight = 8f * 1.1f / (3f * 1.1f + 3f)

/**
 * 8 槽球参数, 与 [floatingPair] 的 u_ballNArray 同义。
 * [6] / [7] 是缩放因子倒数, 本渲染器没有独立缩放通道, 固定 1。
 * 圆角先乘 1.5 再夹到半宽半高: 顺序反过来会让钝角主体永远走贝塞尔分支, 直边消失。
 */
private fun CouiMetaballBody.toBallArray(): FloatArray {
    val halfWidth = size.width * 0.5f
    val halfHeight = size.height * 0.5f
    return floatArrayOf(
        center.x, center.y,
        halfWidth, halfHeight,
        minOf(cornerRadius * CouiMetaballCornerScale, minOf(halfWidth, halfHeight)),
        CouiMetaballWeight,
        1f, 1f,
    )
}

/**
 * 同色双主体的横向 CONIC 融合场, 不采样背景或处理输入。
 * 每个绘制节点独立持有 renderer; Color.White 配合 DstIn 可使用同一算法绘制内容遮罩。
 *
 * 几何预算与 `Modifier.couiFloatingSurface` 共用 [floatingPair], 绘制路径不复用: 这里是
 * ShaderBrush 直接画矩形、Y 轴不翻转、固定像素抗锯齿带、双主体取 min 距离;
 * 浮动表面走 RenderEffect 链, 带 invScale、导数抗锯齿、三主体分层与描边光效。
 * 轮廓固定 CONIC, 不参与浮动表面的 cornerType 分支。
 */
@RequiresApi(33)
class CouiMetaballRenderer {
    private val shader = RuntimeShader(CouiMetaballShaderSource)
    private val brush = ShaderBrush(shader)

    /** size 决定输出区域; 两个主体、融合范围与输出区域使用同一坐标系, 镜像和位移由宿主处理。 */
    fun draw(
        scope: DrawScope,
        first: CouiMetaballBody,
        second: CouiMetaballBody,
        color: Color,
        blendRangePx: Float,
        fusionEnabled: Boolean = true,
        secondAlpha: Float = 1f,
        blendMode: BlendMode = BlendMode.SrcOver,
        size: Size = scope.size,
    ) {
        require(blendRangePx.isFinite() && blendRangePx >= 0f) { "融合范围必须是有限的非负值" }
        require(secondAlpha in 0f..1f) { "第二主体透明度必须在 0 到 1 之间" }
        require(size.width.isFinite() && size.height.isFinite() && size.width >= 0f && size.height >= 0f) {
            "融合输出尺寸必须是有限的非负值"
        }
        val firstBall = first.toBallArray()
        val secondBall = second.toBallArray()
        // blendRangePx 就是原厂 extension: 旧实现两侧各传一次 blendShape, 与这里的 extension * 2 同构。
        val pre = if (fusionEnabled) floatingPair(firstBall, secondBall, blendRangePx) else FloatArray(24)
        shader.setFloatUniform("u_ball1Array", firstBall)
        shader.setFloatUniform("u_ball2Array", secondBall)
        shader.setFloatUniform("u_preCmpArray12", pre)
        shader.setFloatUniform("actionAlpha", secondAlpha)
        shader.setColorUniform("bodyColor", color.toArgb())
        scope.drawRect(brush = brush, size = size, blendMode = blendMode)
    }
}

/**
 * COUI 17 的 CONIC 距离场与逐对预计算混合。
 * 球参数、融合方向与强度全部来自 [floatingPair] 写入的 pre[24], shader 只保留仍在求值的常量;
 * 已搬到 Kotlin 的弧范围 12.5、强度平滑 26、强度 25、颜色速度 20、形状变化 5 不要加回这里。
 * 抗锯齿取基底字段 ANTIALIASING = 1.5, 旧版独立常量 2.0 已废弃。
 */
private const val CouiMetaballShaderSource = """
uniform float u_ball1Array[8];
uniform float u_ball2Array[8];
uniform float u_preCmpArray12[24];
uniform float actionAlpha;
layout(color) uniform half4 bodyColor;

const float EPS = 1e-6;
const float ANTIALIASING = 1.5;
const float BLEND_EDGE_FADE = 50.0;
const float BLEND_STRENGTH_SMOOTH = 26.0;
const float BLEND_STRENGTH = 25.0;
const float BLEND_COLOR_RANGE = 50.0;
const float BLEND_COLOR_FADE = 0.883;

struct RectCorner {vec2 center;vec2 halfSize;float coner;float weight;};

float RBox(float2 p,float2 b,float r){
        float2 q=abs(p)-b+r;
        return min(max(q.x,q.y),0.)+length(max(q,0.))-r;
    }

float cubicBezierDistance(vec2 p, vec2 p0, vec2 p1, vec2 p2, vec2 p3) {

        vec2 c0 = p0;
        vec2 c1 = 3.0 * (p1 - p0);
        vec2 c2 = 3.0 * (p2 - 2.0 * p1 + p0);
        vec2 c3 = p3 - 3.0 * p2 + 3.0 * p1 - p0;
        float t = 0.5;
        for (int i = 0; i < 10; i++) {
            vec2 Bt   = ((c3 * t + c2) * t + c1) * t + c0;
            vec2 dBt  = (3.0 * c3 * t + 2.0 * c2) * t + c1;
            vec2 ddBt = 6.0 * c3 * t + 2.0 * c2;
            vec2 r    = Bt - p;

            float f1 = dot(dBt, r);
            float f2 = dot(ddBt, r) + dot(dBt, dBt);

            float denom = max(f2, 1e-4);
            t = clamp(t - f1 / denom, 0.0, 1.0);
        }
        vec2 Bt = ((c3 * t + c2) * t + c1) * t + c0;
        vec2 re = Bt - p;
        vec2 tangent = (3.0 * c3 * t + 2.0 * c2) * t + c1;
        vec2 normal = vec2(-tangent.y, tangent.x);
        vec2 normalizedNormal = normalize(normal);
        float dis = length(re);
        return dot(re, normalizedNormal) > 0.0 ? dis : -dis;
    }

float sdBezierDistance(vec2 p, vec2 b, float corner, float weight) {
        float sdf = 0.0;
        float cc = corner;
        if(any(lessThanEqual(abs(p), b - cc))) {
            sdf = RBox(p, b, cc);
        }else{
            float point  = max(cc*weight*0.5,0.001);
            sdf = cubicBezierDistance(abs(p)-b+cc,vec2(cc, 0.0), vec2(cc, point), vec2(point, cc),vec2(0.0, cc));
        }
        return sdf;
    }

float gain(float x, float k) {
        float a = 0.5*pow(2.0*((x<0.5)?x:1.0-x), k);
        return (x<0.5)?a:1.0-a;
    }

// 原厂 BlendPair: 形状先按 pre 的横向/纵向范围位移, 再按预计算强度做双主体混合。
void blendPair(float2 p, float2 point1, float2 point2, RectCorner rc1, RectCorner rc2,
               inout float raw0, inout float raw1,
               inout float sdf0, inout float sdf1,
               inout float minDist, float pre[24], inout float colorRangeC) {
        if(!all(greaterThanEqual(float4(rc1.halfSize, rc2.halfSize), float4(EPS)))) return;
        float2 prePoint = float2(pre[0], pre[1]);
        float2 preDir = float2(pre[2], pre[3]);
        float2 preRectCenter = float2(pre[4], pre[5]);
        float2 preRectHalfSize = float2(pre[6], pre[7]);
        float preMinCorner = pre[8];
        float preParaShapeY1 = pre[9], preParaShapeX1 = pre[10], preParaShapeStrength1 = pre[11];
        float preParaShapeY2 = pre[12], preParaShapeX2 = pre[13], preParaShapeStrength2 = pre[14];
        float preArc = pre[15];
        float preParaEdge12 = pre[16], preParaBlendStrength12 = pre[17];
        float preDiffBetween = pre[18];
        colorRangeC = pre[19];
        float preShapeChange = pre[20];
        float2 pt1 = point1, pt2 = point2;
        float hasChanged1 = 0.0, hasChanged2 = 0.0;
        if(preShapeChange > EPS) {
            float2 pointDir = p - prePoint;
            float distanceDirY = abs(dot(pointDir, preDir));
            float distanceDirX = abs(dot(pointDir, float2(preDir.y, -preDir.x)));
            float rangeDistance1 = smoothstep(preParaShapeY1, 0.0, distanceDirY) *
                gain(smoothstep(preParaShapeX1, 0.0, distanceDirX), preArc);
            float smoothStrength1 = preParaShapeStrength1 * rangeDistance1;
            if(smoothStrength1 > EPS) {
                pt1 += normalize(-preDir) * smoothStrength1;
                hasChanged1 = 1.0;
            }
            float rangeDistance2 = smoothstep(preParaShapeY2, 0.0, distanceDirY) *
                gain(smoothstep(preParaShapeX2, 0.0, distanceDirX), preArc);
            float smoothStrength2 = preParaShapeStrength2 * rangeDistance2;
            if(smoothStrength2 > EPS) {
                pt2 += normalize(preDir) * smoothStrength2;
                hasChanged2 = 1.0;
            }
        }
        float distance1 = hasChanged1 < 0.5 ? raw0 : sdBezierDistance(pt1, rc1.halfSize, rc1.coner, rc1.weight);
        float distance2 = hasChanged2 < 0.5 ? raw1 : sdBezierDistance(pt2, rc2.halfSize, rc2.coner, rc2.weight);
        float shapeDistance = RBox(p - preRectCenter, preRectHalfSize, preMinCorner);
        float mask = 1.0 - smoothstep(BLEND_EDGE_FADE, 0.0, abs(shapeDistance)) * preParaEdge12;
        float finalBlendStrength = BLEND_STRENGTH_SMOOTH > EPS
            ? preParaBlendStrength12
            : (BLEND_STRENGTH * step(0.0, preDiffBetween));
        finalBlendStrength = finalBlendStrength * mask;
        float blendedDistance;
        if(finalBlendStrength > EPS) {
            float h = clamp(0.5 + 0.5 * (distance1 - distance2) / finalBlendStrength, 0.0, 1.0);
            blendedDistance = mix(distance1, distance2, h) - finalBlendStrength * h * (1.0 - h);
        } else {
            blendedDistance = min(distance1, distance2);
        }
        sdf0 = min(sdf0, distance1);
        sdf1 = min(sdf1, distance2);
        minDist = min(minDist, blendedDistance);
    }

half4 main(float2 coords) {
    RectCorner card = RectCorner(float2(u_ball1Array[0], u_ball1Array[1]),
        float2(u_ball1Array[2], u_ball1Array[3]), u_ball1Array[4], u_ball1Array[5]);
    RectCorner action = RectCorner(float2(u_ball2Array[0], u_ball2Array[1]),
        float2(u_ball2Array[2], u_ball2Array[3]), u_ball2Array[4], u_ball2Array[5]);
    float2 p0 = coords - card.center;
    float2 p1 = coords - action.center;
    const float2 eps2 = float2(EPS);
    const float farSide = 100.0;
    float sdf0 = farSide, sdf1 = farSide;
    float raw0 = farSide, raw1 = farSide;
    float minDistance = farSide;
    if (all(greaterThanEqual(card.halfSize, eps2))) {
        sdf0 = sdBezierDistance(p0, card.halfSize, card.coner, card.weight);
        raw0 = sdf0;
        minDistance = min(sdf0, minDistance);
    }
    // 第二主体透明时保持旧实现的排除路径: 不参与距离场与融合, fadeRange 取满量程。
    bool hasAction = actionAlpha > 0.0 && all(greaterThanEqual(action.halfSize, eps2));
    if (hasAction) {
        sdf1 = sdBezierDistance(p1, action.halfSize, action.coner, action.weight);
        raw1 = sdf1;
        minDistance = min(sdf1, minDistance);
    }
    float colorRange = 0.0;
    if (hasAction && u_preCmpArray12[21] > 0.5) {
        blendPair(coords, p0, p1, card, action, raw0, raw1, sdf0, sdf1, minDistance, u_preCmpArray12, colorRange);
    }
    float coverage = smoothstep(0.0, -ANTIALIASING, minDistance);
    // 双主体退化的 colorRangeValue: 另一主体在其外部时取满量程, 在其内部时取预计算的 colorRange。
    float colorRangeValue = (sdf1 > 0.0) ? 1.0 : colorRange;
    float edgeFade = pow(smoothstep(max(BLEND_COLOR_RANGE * colorRangeValue, EPS), 0.0, sdf0), BLEND_COLOR_FADE);
    // 删除按钮主体由自身距离场与 actionAlpha 决定, 两层同色叠加避免卡片右边缘出现硬台阶。
    float actionBody = (sdf0 > 0.0 && sdf1 <= 0.0) ? actionAlpha : 0.0;
    float opacity = 1.0 - (1.0 - edgeFade) * (1.0 - actionBody);
    half alpha = bodyColor.a * half(coverage * opacity);
    return half4(bodyColor.rgb * alpha, alpha);
}
"""

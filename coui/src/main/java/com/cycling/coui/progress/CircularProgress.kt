package com.cycling.coui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import com.cycling.coui.theme.CouiTheme

/**
 * 环形进度。progress 为 0..1 时按比例绘制，越界值先夹紧；传 null 时显示不确定态旋转。
 * 尺寸与描边取 `Widget.COUI.COUICircularProgressBar`：large 40dp / 5dp，medium 30dp / 3dp。
 * 原厂把确定态与加载态分成两个控件：确定态弧色为 `couiColorHintNeutral`、轨道为 `couiColorDivider`；
 * 小号加载环 `COUICircleProgressBar` 的弧色为 `?couiColorPrimary`、底色为 `?couiColorSecondary`。
 * 因此省略 [color] 时按状态取色：确定态用前景 26% / 深色 30%，不确定态用主题强调色。
 *
 * [gapDegrees] 在 [startAngleDegrees] 之前留出缺口，进度从起点顺时针填充，缺口不参与比例换算；
 * [glow] 在弧外叠加同色光晕，环半径按 [CouiProgressDefaults.circularGlowRingScale] 内收以容纳光晕，
 * 因此开启光晕时 [size] 是含光晕的整体尺寸，不是环外径。
 * [arcGradient] 非空时弧线改用竖直渐变，第一项是环顶、最后一项是环底，[color] 只用于光晕与取色基线。
 * 原厂分数环的弧正是竖直渐变：顶部满饱和、底部偏淡，色相与明度基本不变，只有饱和度下降
 * （手机管家首页沿弧采样 R 通道 0→41、B 通道 16→40，G 通道几乎不变）。端点色随页面而异
 * （首页 `#00D616` → `#29CF28`，清理存储 `#2ACB1B` → `#58C358`），所以端点不进入默认值，
 * 由调用方按页面给出；渐变按环的上下边界铺满描边带，换 [size] 不改变端点颜色。
 * [content] 在环心居中绘制，用于分数、容量等中心读数，不参与环的测量。
 */
@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    dark: Boolean = CouiTheme.isDark,
    color: Color = Color.Unspecified,
    trackColor: Color = CouiTheme.colorSchemeFor(dark).outlineVariant,
    strokeWidth: Dp = CouiProgressDefaults.circularStrokeWidth,
    size: Dp = CouiProgressDefaults.circularSize,
    gapDegrees: Float = 0f,
    startAngleDegrees: Float = -90f,
    glow: Boolean = false,
    arcGradient: List<Color>? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val value = progress
    val scheme = CouiTheme.colorSchemeFor(dark)
    val arcColor = if (color.isSpecified) color else if (value == null) scheme.accent
        else scheme.onSurface.copy(alpha = if (dark) .3f else .26f)
    // 渐变弧只用在强调色环上, 光晕跟着强调色走, 否则中性前景会在彩色弧外面发光。
    val glowColor = if (arcGradient != null && !color.isSpecified) scheme.accent else arcColor
    val indeterminate = value == null
    val phase = rememberCouiPhase(indeterminate, CouiProgressDefaults.indeterminatePeriodMillis)
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val extent = this.size
            val stroke = strokeWidth.toPx().coerceIn(0f, extent.minDimension / 2f)
            if (stroke <= 0f) return@Canvas
            val gap = gapDegrees.coerceIn(0f, CouiProgressDefaults.maxCircularGapDegrees)
            val sweep = 360f - gap
            val start = startAngleDegrees + gap / 2f
            // 光晕画在环外, 环按比例内收后仍有完整绘制空间。
            val scale = if (glow) CouiProgressDefaults.circularGlowRingScale else 1f
            val diameter = (extent.minDimension - stroke) * scale
            if (diameter <= 0f) return@Canvas
            val topLeft = Offset((extent.width - diameter) / 2f, (extent.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            val arcSweep = if (indeterminate) CouiProgressDefaults.indeterminateSweep.coerceAtMost(sweep)
                else sweep * value.coerceIn(0f, 1f)
            val arcStart = if (indeterminate) start + phase.value * 360f else start
            if (glow && glowColor.alpha > 0f && arcSweep > 0f) {
                repeat(CouiProgressDefaults.circularGlowLayers) { index ->
                    val extra = stroke * CouiProgressDefaults.circularGlowSpread * (index + 1)
                    val alpha = CouiProgressDefaults.circularGlowAlpha / (index + 1)
                    drawArc(glowColor.copy(alpha = glowColor.alpha * alpha), arcStart, arcSweep, false,
                        topLeft, arcSize, style = Stroke(stroke + extra, cap = StrokeCap.Round))
                }
            }
            if (trackColor.alpha > 0f) {
                drawArc(trackColor, start, sweep, false, topLeft, arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round))
            }
            if (arcSweep > 0f) {
                if (arcGradient != null && arcGradient.isNotEmpty()) {
                    drawArcGradient(arcGradient, arcStart, arcSweep, topLeft, diameter, stroke)
                } else {
                    drawArc(arcColor, arcStart, arcSweep, false, topLeft, arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round))
                }
            }
        }
        content?.invoke(this)
    }
}

/** 整圈时直接用椭圆描边，避免 `Path.arcTo` 在 360° 上退化成空路径。 */
private const val FullArcDegrees = 360f

/**
 * 用竖直渐变画一段圆弧。`DrawScope.drawArc` 只接受纯色，渐变必须走 Path + `drawPath`；
 * 端部圆头由 `Stroke` 的 `StrokeCap.Round` 给出，与原厂截图里弧端收圆一致。
 * 渐变从环的外缘顶端到外缘底端，与描边带同宽。
 */
private fun DrawScope.drawArcGradient(
    colors: List<Color>,
    arcStart: Float,
    arcSweep: Float,
    topLeft: Offset,
    diameter: Float,
    stroke: Float,
) {
    val brush = Brush.verticalGradient(
        colors = colors,
        startY = topLeft.y - stroke / 2f,
        endY = topLeft.y + diameter + stroke / 2f,
    )
    val cap = Stroke(width = stroke, cap = StrokeCap.Round)
    if (arcSweep >= FullArcDegrees) {
        drawOval(brush, topLeft, Size(diameter, diameter), style = cap)
        return
    }
    val bounds = Rect(topLeft, Size(diameter, diameter))
    val path = Path().apply { arcTo(bounds, arcStart, arcSweep, true) }
    drawPath(path, brush, style = cap)
}

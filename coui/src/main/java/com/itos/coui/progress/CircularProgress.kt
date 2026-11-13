package com.itos.coui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import com.itos.coui.theme.CouiTheme

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
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val value = progress
    val scheme = CouiTheme.colorSchemeFor(dark)
    val arcColor = if (color.isSpecified) color else if (value == null) scheme.accent
        else scheme.onSurface.copy(alpha = if (dark) .3f else .26f)
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
            if (glow && arcColor.alpha > 0f && arcSweep > 0f) {
                repeat(CouiProgressDefaults.circularGlowLayers) { index ->
                    val extra = stroke * CouiProgressDefaults.circularGlowSpread * (index + 1)
                    val alpha = CouiProgressDefaults.circularGlowAlpha / (index + 1)
                    drawArc(arcColor.copy(alpha = arcColor.alpha * alpha), arcStart, arcSweep, false,
                        topLeft, arcSize, style = Stroke(stroke + extra, cap = StrokeCap.Round))
                }
            }
            if (trackColor.alpha > 0f) {
                drawArc(trackColor, start, sweep, false, topLeft, arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round))
            }
            if (arcSweep > 0f) {
                drawArc(arcColor, arcStart, arcSweep, false, topLeft, arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round))
            }
        }
        content?.invoke(this)
    }
}

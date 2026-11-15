package com.itos.coui.textfield

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.theme.CouiTheme
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 验证码格子的度量。格子宽高按 `min(屏宽dp, 360) / 360` 缩放，间距在可用宽度里重算，
 * 都取自 `coui_code_input_*`。
 */
@Immutable
data class CouiCodeInputMetrics(
    val cellWidth: Dp = 42.dp,
    val cellHeight: Dp = 46.dp,
    val minCellSpacing: Dp = 2.dp,
    val maxCellSpacing: Dp = 8.dp,
    val containerPadding: Dp = 16.dp,
    val cornerRadius: Dp = 8.dp,
    val strokeWidth: Dp = 1.6.dp,
    val dotRadius: Dp = 5.dp,
)

/**
 * 验证码格子的配色。底色取卡片色、描边取强调色、数字取正文色；
 * 安全模式的圆点在日间是 85% 黑、夜间是 85% 白。
 */
@Immutable
data class CouiCodeInputColors(
    val cellBackground: Color,
    val cellStroke: Color,
    val number: Color,
    val dot: Color,
)

/** 验证码输入的固定度量、动画参数与配色。 */
object CouiCodeInputDefaults {
    /** 默认格子数，对应 `couiCodeInputCount` 的缺省值。 */
    const val CellCount = 6

    /** 尺寸缩放的设计宽度。 */
    const val DesignWidthDp = 360

    val metrics = CouiCodeInputMetrics()

    /** 数字出现与消失的时长，`NUMBER_APPEAR_ANIMATOR_DURATION`。 */
    val numberDurationMillis = 100

    /** 数字消失的起始延迟，`NUMBER_DELAY_ANIMATOR_DURATION`。 */
    val numberDeleteDelayMillis = 33

    /** 描边出现与消失的时长，`INBOX_APPEAR_ANIMATOR_DURATION`。 */
    val strokeDurationMillis = 100

    /** 描边出现的起始延迟，`INBOX_DELAY_ANIMATOR_DURATION`。 */
    val strokeAppearDelayMillis = 33

    /** 数字出现时的起始缩放，`NUMBER_SCALE_START`。 */
    const val numberScaleStart = 0.6f

    /** 禁用态整体淡化强度，与主题禁用文字保持同一档。 */
    const val disabledAlpha = 0.3f

    /** `COUIMoveEaseInterpolator`：PathInterpolator(0.3, 0, 0.1, 1)。 */
    val moveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiCodeInputColors {
        val theme = CouiTheme.colorSchemeFor(dark)
        return CouiCodeInputColors(cellBackground = theme.card, cellStroke = theme.accent,
            number = theme.onSurface, dot = if (dark) Color(0xD8FFFFFF) else Color(0xD8000000))
    }

    /** 格子里的数字：30sp、常规字重，与 `coui_code_input_cell_text_size` 一致。 */
    fun textStyle(): TextStyle = TextStyle(fontSize = 30.sp)

    /** `min(屏宽dp, 360) / 360`，窄屏按比例缩小格子。 */
    internal fun cellScale(screenWidthDp: Int): Float =
        min(screenWidthDp, DesignWidthDp).toFloat() / DesignWidthDp

    /** 格子间距：`(可用宽 - 格子总宽 - 两侧留白) / (2 * 格子数 - 2)`，再夹在最小与最大值之间。 */
    internal fun cellSpacing(available: Float, cellWidth: Float, cells: Int,
        metrics: CouiCodeInputMetrics, density: Density): Dp {
        if (cells <= 1) return 0.dp
        val padding = with(density) { metrics.containerPadding.toPx() }
        val raw = (available - cells * cellWidth - padding * 2) / (cells * 2 - 2)
        val minimum = with(density) { metrics.minCellSpacing.toPx() }
        val maximum = with(density) { metrics.maxCellSpacing.toPx() }
        return with(density) { raw.coerceIn(minimum, maximum).roundToInt().toDp() }
    }
}

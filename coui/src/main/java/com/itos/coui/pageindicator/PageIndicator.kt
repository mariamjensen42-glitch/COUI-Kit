package com.itos.coui.pageindicator

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** 页码圆点配色；stroke 为未选中点的可选描边，透明表示不描边。 */
@Immutable
data class CouiPageIndicatorColors(
    val dot: Color,
    val selectedDot: Color,
    val stroke: Color = Color.Transparent,
)

/** 页码圆点的固定参数，尺寸与描边取自 `Widget.COUI.COUIPageIndicator` 引用的 dimens。 */
object CouiPageIndicatorDefaults {
    /** `coui_page_indicator_dot_size`：当前页直径，也是插值的第一档半径 `dotSize / 2`。 */
    val dotSize = 6.dp

    /** `coui_page_indicator_dot_size_medium`：插值的第二档半径 `medium / 2`。 */
    val dotMediumSize = 4.5.dp

    /** `coui_page_indicator_dot_size_small`：插值的第三档半径 `small / 2`。 */
    val dotSmallSize = 2.5.dp

    /** `coui_page_indicator_dot_spacing`：每个点两侧的留白，相邻点净距为其两倍。 */
    val spacing = 3.dp

    /** 原厂 `onMeasure` 按 `min(6, count)` 计算宽度：超过 6 个点时只排版 6 个。 */
    const val maxDots = 6

    /** `coui_page_indicator_dot_stroke_width`：未选中点描边宽度。 */
    val strokeWidth = 1.dp

    /**
     * 默认配色。原厂 `dotColor` 取 `?couiColorHintNeutral`（日间 26% 黑、夜间 30% 白），
     * `traceDotColor` 取 `coui_color_label_primary`（日间 #E6000000、夜间 #E6FFFFFF）；
     * 这里按同一透明度从前景色派生，宿主覆盖色板时保持相对对比度。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiPageIndicatorColors {
        val onSurface = CouiTheme.colorSchemeFor(dark).onSurface
        return CouiPageIndicatorColors(dot = onSurface.copy(alpha = if (dark) .3f else .26f),
            selectedDot = onSurface)
    }
}

/**
 * 页码圆点指示器，宿主通过 [selectedIndex] 控制当前页；传入 [onDotClick] 时点按圆点会回传被点的下标。
 *
 * 半径不是按距离分档，而是原厂 `COUIPageIndicator` 的连续插值：点与当前页的距离落在四段距离带
 * （起点 `{0, -1, -2, -3}`、带宽 `{3, 5, 7, 9}`，点数不少于 6 时首段带宽 3、少于 6 个点时放宽到 5）里，
 * 用 `COUIEaseInterpolator`（三次贝塞尔 0.33, 0, 0.67, 1）在相邻两档半径
 * `[dotSize/2, medium/2, small/2, 0]` 之间插值，超出最后一段按 0。距离以 `mMaskOffset` 为原点，
 * 当前点固定在 `d = 3`，因此 6 个点时窗口会随当前页滑动，最多显示 6 个点。
 *
 * 每个点占 `dotSize + 2 × spacing`，圆点按半径依次累加（相邻净距恒为 `2 × spacing`）后整体居中，
 * 切换当前页不改变组件占位；`count <= 0` 不占位，越界 [selectedIndex] 先夹紧。
 */
@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    count: Int,
    selectedIndex: Int,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageIndicatorColors = CouiPageIndicatorDefaults.colors(dark),
    onDotClick: ((Int) -> Unit)? = null,
) {
    if (count <= 0) return
    // 原厂 onMeasure 用 min(6, count) 计算宽度：超过 6 个点时只按 6 个点排版。
    val visible = count.coerceAtMost(CouiPageIndicatorDefaults.maxDots)
    val selected = selectedIndex.coerceIn(0, count - 1)
    val dot = CouiPageIndicatorDefaults.dotSize
    val gap = CouiPageIndicatorDefaults.spacing
    // 原厂每个点占 (dotSize + 2 × spacing)：spacing 是点两侧的留白，相邻点净距为 2 × spacing。
    val slot = dot + gap * 2
    // 回调可能随重组变化，手势协程里读最新值，避免重启 pointerInput 打断进行中的按压。
    val currentOnDotClick by rememberUpdatedState(onDotClick)
    // 原厂在 layoutDirection == RTL 时翻转绘制矩阵，点的顺序跟着镜像。
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val clickable = if (onDotClick == null) modifier else modifier.pointerInput(count, selected, rtl) {
        awaitEachGesture {
            // 原厂 onTouchEvent 不拦截按下，只在 ACTION_DOWN 到 ACTION_UP 不超过 tapTimeout 时算点击。
            val down = awaitFirstDown(requireUnconsumed = false)
            val up = waitForUpOrCancellation() ?: return@awaitEachGesture
            if (up.uptimeMillis - down.uptimeMillis > TapTimeoutMillis) return@awaitEachGesture
            val dots = layoutDots(count, selected, size.width.toFloat(), dot.toPx(),
                CouiPageIndicatorDefaults.dotMediumSize.toPx(),
                CouiPageIndicatorDefaults.dotSmallSize.toPx(), gap.toPx(), rtl)
            val hit = hitIndex(dots, up.position.x)
            if (hit >= 0) currentOnDotClick?.invoke(hit)
        }
    }
    Canvas(clickable.size(slot * visible, dot)) {
        val dotPx = dot.toPx()
        val dots = layoutDots(count, selected, size.width, dotPx,
            CouiPageIndicatorDefaults.dotMediumSize.toPx(),
            CouiPageIndicatorDefaults.dotSmallSize.toPx(), gap.toPx(), rtl)
        val centerY = size.height / 2f
        val strokePx = CouiPageIndicatorDefaults.strokeWidth.toPx()
        for (geometry in dots) {
            val radius = geometry.radius
            if (radius <= 0f) continue
            val center = Offset(geometry.centerX, centerY)
            if (geometry.index == selected) {
                drawCircle(colors.selectedDot, radius, center)
            } else {
                drawCircle(colors.dot, radius, center)
                // 描边画在圆内，不改变点的外径；描边色透明时保持纯填充。
                if (colors.stroke.alpha > 0f) {
                    val ring = strokePx.coerceAtMost(radius)
                    if (ring > 0f) drawCircle(colors.stroke, radius - ring / 2f, center, style = Stroke(ring))
                }
            }
        }
    }
}

/** 原厂 `ViewConfiguration.getTapTimeout()`，固定 100ms。 */
private const val TapTimeoutMillis = 100L

/** 原厂 `c.f16622e`：四段距离带的起点，第 i 段覆盖 `[起点, 起点 + 带宽]`。 */
private val BandStarts = floatArrayOf(0f, -1f, -2f, -3f)

/** 原厂 `COUIEaseInterpolator`：`com.coui.appcompat.animation.b` 的 PathInterpolator(0.33, 0, 0.67, 1)。 */
private val EaseInterpolator = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** 一个圆点在一次排版里的几何：绝对下标、水平圆心与半径（px）。 */
private class DotGeometry(val index: Int, val centerX: Float, val radius: Float)

/**
 * 复刻原厂 `c.m(float offset, int index)` 的半径插值：距离落在第 i 段带内就取相邻两档之间的一档，
 * 正向用 `(带右端 - 距离)` 收档、反向用 `(距离 - 带左端)` 升档，两处都乘 2 再夹到上一档，
 * 与 `Math.min/Math.max` 的夹紧写法一致；没有任何一段命中时半径为 0。
 */
private fun radiusAt(distance: Float, widths: FloatArray, levels: FloatArray): Float {
    for (level in BandStarts.indices) {
        val start = BandStarts[level]
        val end = start + widths[level]
        if (distance < start || distance > end) continue
        if (level == 0) return levels[0]
        val upper = levels[level - 1]
        val current = levels[level]
        if (distance >= BandStarts[0]) {
            // 正向：当前点右侧，从带右端开始向上一档收敛。
            val span = end - BandStarts[level - 1] - widths[level - 1]
            if (span <= 0f) return current
            val factor = EaseInterpolator.transform(((end - distance) / span).coerceIn(0f, 1f))
            return max(current, upper - (1f - factor) * (upper - current) * 2f)
        }
        // 反向：当前点左侧，从带左端开始向上一档收敛。
        val leftSpan = BandStarts[level - 1] - start
        if (leftSpan <= 0f) return current
        val leftFactor = EaseInterpolator.transform(((distance - start) / leftSpan).coerceIn(0f, 1f))
        return min(upper, current + leftFactor * (upper - current) * 2f)
    }
    return 0f
}

/**
 * 原厂 `c.m()` 的排版部分：先算每个点的半径，再按半径依次累加位置（相邻净距 `2 × spacing`），
 * 最后把整条内容在可用宽度内居中。可见窗口由 `mMaskOffset` 决定，当前点固定在 `d = 3`。
 */
private fun layoutDots(
    count: Int,
    selected: Int,
    width: Float,
    dotPx: Float,
    mediumPx: Float,
    smallPx: Float,
    gapPx: Float,
    rtl: Boolean,
): List<DotGeometry> {
    val maxDots = CouiPageIndicatorDefaults.maxDots
    // 原厂 c.n(boolean)：点数少于 6 时首段带宽放宽到 5，此时窗口不滑动、所有点都是满半径。
    val widths = floatArrayOf(if (count < maxDots) 5f else 3f, 5f, 7f, 9f)
    val levels = floatArrayOf(dotPx / 2f, mediumPx / 2f, smallPx / 2f, 0f)
    // 原厂 mMaskOffset = 当前位置 - 首段带宽：当前点固定在 d = 3，6 个点的窗口随之滑动。
    val maskOffset = (selected - widths[0]).coerceAtLeast(0f)
    val windowStart = if (count < maxDots) 0
    else (maskOffset - 1f).toInt().coerceIn(0, count - maxDots)
    val indices = windowStart until min(windowStart + maxDots, count)
    val radii = indices.map { radiusAt(it - maskOffset, widths, levels) }
    // 内容宽度含首尾各一个 spacing 的留白，与原厂 onMeasure 的 (dotSize + 2 × spacing) × n 对齐。
    val total = radii.sum() * 2f + gapPx * 2f * radii.size
    var x = (width - total) / 2f + gapPx
    return indices.mapIndexed { position, index ->
        val radius = radii[position]
        val centerX = x + radius
        // RTL 时按可用宽度镜像圆心，点的顺序跟着翻转；命中判定仍按绝对坐标比较。
        val geometry = DotGeometry(index, if (rtl) width - centerX else centerX, radius)
        x += radius * 2f + gapPx * 2f
        geometry
    }
}

/**
 * 原厂 `c.f(float x, float y)` 的命中换算：落在某个点的外接方块内就取该点，否则取水平方向最近的点。
 * 所有点同心且视图高度等于点直径，纵向必然落在方块内，只需比较水平距离。列表为空时返回 -1。
 */
private fun hitIndex(dots: List<DotGeometry>, x: Float): Int {
    if (dots.isEmpty()) return -1
    val inside = dots.firstOrNull { abs(x - it.centerX) <= it.radius }
    if (inside != null) return inside.index
    return dots.minByOrNull { abs(x - it.centerX) }?.index ?: -1
}

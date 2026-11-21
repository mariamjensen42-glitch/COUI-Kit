package com.cycling.coui.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * 滑动窗口折线图，对应 ColorOS 17 手机管家「网络安全检测」页的
 * `NetworkSlidingWindowLineView`。
 *
 * [values] 按时间顺序排列，只绘制最后 [windowSize] 个点；窗口未满时按当前点数均分宽度，
 * 满了以后固定按 `windowSize - 1` 等分，新的点把旧点挤出左侧。相邻点用三次贝塞尔连接，
 * 两个控制点的 x 都取两点中点、y 分别取两侧点的 y，因此每个数据点处切线水平（原厂
 * `buildPathForPoint` 的同一构造），连线不会过冲。
 *
 * 线下方是自上而下淡出的面积，顶部为 [areaColor] 的 [CouiLineChartDefaults.areaTopAlpha]，
 * 底部完全透明（原厂 `shadowStartColor` / `shadowEndColor` 的 alpha 51 与 0）。
 *
 * 只在点数 +1 时为最后一段做 300ms 线性入场（原厂 `startEntryAnimation`）；窗口已满时逐点替换
 * 不再动画。纵向值域由数据决定，最大值与最小值之差小于 [CouiLineChartDefaults.minValueRange]
 * 时改为「中值 ± 0.5」，避免平坦数据被纵向拉满。
 *
 * 原厂 `drawCompleteChart()` 的「超过 40 点按 σ=(n/4) 高斯权重降采样」属于另一条整段数据入口，
 * 未移植到本组件：需要长序列平滑时由宿主导入降采样后的数据。
 */
@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    values: List<Float>,
    windowSize: Int = CouiLineChartDefaults.windowSize,
    lineColor: Color = CouiLineChartDefaults.lineColor,
    areaColor: Color = lineColor,
    strokeWidth: Dp = CouiLineChartDefaults.strokeWidth,
    height: Dp = CouiLineChartDefaults.height,
) {
    require(windowSize >= 2) { "windowSize 至少为 2" }
    val data = remember(values, windowSize) { values.takeLast(windowSize) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    // 只在点数 +1（窗口未满）时播放最后一段的入场动画；读取发生在绘制阶段，不触发重组。
    val entry = remember { Animatable(1f) }
    val lastSize = remember { mutableIntStateOf(data.size) }
    LaunchedEffect(data) {
        val count = data.size
        val animate = count == lastSize.intValue + 1 && count <= windowSize
        lastSize.intValue = count
        if (animate) {
            entry.snapTo(0f)
            entry.animateTo(1f, tween(CouiLineChartDefaults.entryDurationMillis, easing = LinearEasing))
        } else {
            entry.snapTo(1f)
        }
    }

    Canvas(modifier.fillMaxWidth().height(height)) {
        val count = data.size
        if (count < 2) return@Canvas
        val lowest = data.minOrNull() ?: 0f
        val highest = data.maxOrNull() ?: 0f
        val low: Float
        val high: Float
        if (highest - lowest < CouiLineChartDefaults.minValueRange) {
            val center = (highest + lowest) / 2f
            low = center - .5f
            high = center + .5f
        } else {
            low = lowest
            high = highest
        }
        val range = (high - low).coerceAtLeast(CouiLineChartDefaults.leastNormalizedRange)
        val chartHeight = (1f - CouiLineChartDefaults.topPaddingRatio -
            CouiLineChartDefaults.bottomPaddingRatio) * size.height
        val topPadding = size.height * CouiLineChartDefaults.topPaddingRatio
        // 窗口未满时按实际点数均分；满了以后固定按窗口等分（原厂 onDraw 里的同一条分支）。
        val spacing = if (count >= windowSize) size.width / (windowSize - 1)
        else size.width / (count - 1)
        fun xAt(index: Int) = if (rtl) size.width - index * spacing else index * spacing
        fun yAt(index: Int) = (1f - (data[index] - low) / range) * chartHeight + topPadding

        val progress = entry.value
        val line = Path()
        line.moveTo(xAt(0), yAt(0))
        var tipX = xAt(0)
        for (index in 1 until count) {
            val previousX = xAt(index - 1)
            val previousY = yAt(index - 1)
            val x = xAt(index)
            val y = yAt(index)
            if (index == count - 1) {
                // 最后一段从上一个点长到当前点，未完成时按进度收拢同时插值 x 与 y。
                tipX = previousX + (x - previousX) * progress
                val tipY = previousY + (y - previousY) * progress
                val middle = (tipX + previousX) / 2f
                line.cubicTo(middle, previousY, middle, tipY, tipX, tipY)
            } else {
                val middle = (x + previousX) / 2f
                line.cubicTo(middle, previousY, middle, y, x, y)
            }
        }

        val area = Path().apply { addPath(line) }
        area.lineTo(tipX, size.height)
        area.lineTo(if (rtl) size.width else 0f, size.height)
        area.close()
        drawPath(
            area,
            Brush.verticalGradient(
                colors = listOf(
                    areaColor.copy(alpha = CouiLineChartDefaults.areaTopAlpha),
                    areaColor.copy(alpha = CouiLineChartDefaults.areaBottomAlpha),
                ),
                startY = 0f,
                endY = size.height,
            ),
        )
        drawPath(
            line,
            lineColor,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

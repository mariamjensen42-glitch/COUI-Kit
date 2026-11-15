package com.cycling.coui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cycling.coui.layout.CouiPageColors
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import kotlin.math.min

/** 柱状图的高度、柱间距、圆角与网格线。 */
object CouiChartDefaults {
    val height = 160.dp
    val barSpacing = 4.dp
    val barCorner = 3.dp
    val axisSpacing = 8.dp
    val gridWidth = 1.dp
}

/** 单根柱：取值与可选配色；配色为 null 时使用图表的默认柱色。 */
@Immutable
data class CouiBar(val value: Float, val color: Color? = null)

/**
 * 圆角柱状图，对应 ColorOS 电池页的「电量使用情况」。
 *
 * [bars] 按从左到右的顺序绘制，柱高 = `value / maxValue`；逐柱配色用于区分时段（例如充电时段）。
 * [axisLabels] 自上而下排布在右侧，与顶部、中部、底部对齐，同时作为水平网格线的位置；
 * 省略时只画柱，不画网格。图表不做滚动、缩放或交互，只负责按传入数据绘制。
 */
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    bars: List<CouiBar>,
    maxValue: Float = 1f,
    colors: CouiPageColors = CouiPageDefaults.themeColors(),
    barColor: Color = colors.accent,
    axisLabels: List<String> = emptyList(),
    showGrid: Boolean = true,
) {
    require(maxValue > 0f) { "maxValue 必须是正数" }
    Row(modifier.fillMaxWidth().height(CouiChartDefaults.height)) {
        Canvas(Modifier.weight(1f).fillMaxHeight()) {
            val spacing = CouiChartDefaults.barSpacing.toPx()
            val count = bars.size
            if (count > 0) {
                val barWidth = ((size.width - spacing * (count - 1)) / count).coerceAtLeast(1f)
                bars.forEachIndexed { index, bar ->
                    val ratio = (bar.value / maxValue).coerceIn(0f, 1f)
                    val barHeight = ratio * size.height
                    if (barHeight <= 0f) return@forEachIndexed
                    val radius = min(min(CouiChartDefaults.barCorner.toPx(), barWidth * 0.5f), barHeight * 0.5f)
                    drawRoundRect(
                        color = bar.color ?: barColor,
                        topLeft = Offset(index * (barWidth + spacing), size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(radius, radius),
                    )
                }
            }
            if (showGrid && axisLabels.size > 1) {
                val stroke = CouiChartDefaults.gridWidth.toPx()
                axisLabels.indices.forEach { index ->
                    val y = size.height * index / (axisLabels.size - 1)
                    drawLine(colors.divider, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
                }
            }
        }
        if (axisLabels.isNotEmpty()) {
            Column(Modifier.fillMaxHeight().padding(start = CouiChartDefaults.axisSpacing),
                verticalArrangement = Arrangement.SpaceBetween) {
                axisLabels.forEach { label ->
                    Text(text = label, kind = CouiTextKind.Body, color = colors.summary, maxLines = 1)
                }
            }
        }
    }
}

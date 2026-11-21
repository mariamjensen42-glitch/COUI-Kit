package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.chart.BarChart
import com.cycling.coui.chart.CouiBar
import com.cycling.coui.chart.CouiLineChartDefaults
import com.cycling.coui.chart.LineChart
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Group
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.delay
import kotlin.math.sin

/** 柱状图：逐柱配色区分充电时段，右侧刻度同时作为水平网格线位置。 */
@Composable
internal fun BarChartSample(dark: Boolean) {
    val colors = CouiPageDefaults.themeColors(dark)
    val accent = CouiTheme.colorScheme.accent
    // 固定数据：高低起伏 + 后段三根换成强调色，模拟充电时段。
    val values = remember {
        listOf(.18f, .34f, .52f, .41f, .63f, .48f, .72f, .55f, .38f, .61f, .77f, .58f,
            .44f, .66f, .82f, .71f, .53f, .68f, .86f, .74f, .6f, .79f, .91f, .83f)
    }
    val bars = remember(values, accent, colors) {
        values.mapIndexed { index, value ->
            CouiBar(value, if (index >= values.lastIndex - 2) accent else colors.accent.copy(alpha = .45f))
        }
    }
    Group(title = stringResource(R.string.bar_chart_title)) {
        Box(Modifier.padding(16.dp)) {
            BarChart(bars = bars, maxValue = 1f, colors = colors,
                axisLabels = listOf(stringResource(R.string.bar_chart_axis_high),
                    stringResource(R.string.bar_chart_axis_low)))
        }
    }
}

/**
 * 折线图：按原厂 200ms 的数据节奏逐点推进，窗口满了以后新点把最旧的点挤出左侧，
 * 最后一段做 300ms 生长入场。数据用三条不同周期的正弦叠加，避免随机数导致每次演示不同。
 */
@Composable
internal fun LineChartSample(dark: Boolean) {
    val colors = CouiPageDefaults.themeColors(dark)
    var values by remember { mutableStateOf(List(CouiLineChartDefaults.windowSize) { sampleSpeed(it) }) }
    LaunchedEffect(Unit) {
        var index = CouiLineChartDefaults.windowSize
        while (true) {
            delay(200)
            index++
            // 多留 10 个点交给组件按窗口截断，演示真实滑动。
            values = (values + sampleSpeed(index)).takeLast(CouiLineChartDefaults.windowSize + 10)
        }
    }
    Group(title = stringResource(R.string.line_chart_title)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "%.0f".format(values.last() * 100), kind = CouiTextKind.TopBar, color = colors.title)
                Text(text = stringResource(R.string.line_chart_unit_mbps), kind = CouiTextKind.Body,
                    color = colors.summary)
            }
            LineChart(values = values)
        }
    }
}

private fun sampleSpeed(index: Int): Float =
    .5f + .28f * sin(index / 5.7f) + .14f * sin(index / 2.1f) + .07f * sin(index / 1.3f)

package com.itos.coui.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.chart.BarChart
import com.itos.coui.chart.CouiBar
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Group
import com.itos.coui.theme.CouiTheme

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

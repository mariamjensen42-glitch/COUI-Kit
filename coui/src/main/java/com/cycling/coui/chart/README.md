# Chart

## Overview
图表包。`BarChart` 按传入数据绘制圆角柱状图，可选择显示右侧轴标签与水平网格线；对应 ColorOS 电池页「电量使用情况」的静态图表，不提供滚动、缩放或交互。`LineChart` 是滑动窗口折线，对应手机管家「网络安全检测」页的 `NetworkSlidingWindowLineView`：只画最近 `windowSize` 个点，线下面积向下淡出。

## Interface
- `public object CouiChartDefaults`：图表高度、柱间距、柱圆角、轴间距与网格线宽 [BarChart.kt:CouiChartDefaults]
- `public data class CouiBar(value, color)`：单根柱的取值与可选配色 [BarChart.kt:CouiBar]
- `public @Composable fun BarChart(modifier, bars, maxValue, colors, barColor, axisLabels, showGrid)` [BarChart.kt:BarChart]
- `public object CouiLineChartDefaults`：窗口点数、线宽、默认高度、上下留白比例、值域下限、入场时长与线色 [CouiLineChartDefaults.kt:CouiLineChartDefaults]
- `public @Composable fun LineChart(modifier, values, windowSize, lineColor, areaColor, strokeWidth, height)` [LineChart.kt:LineChart]

## Invariants
- `require(maxValue > 0f)`：`maxValue` 非正数直接抛异常 [BarChart.kt:BarChart]
- 柱高比例 `coerceIn(0f, 1f)`，柱高为 0 的柱不绘制 [BarChart.kt:BarChart]
- 网格线仅在 `showGrid && axisLabels.size > 1` 时绘制，位置与轴标签等分对齐 [BarChart.kt:BarChart]
- `axisLabels` 自上而下排布并作为网格线位置；省略时只画柱 [BarChart.kt:BarChart]
- 单柱配色 `bar.color` 优先，否则回退 `barColor` [BarChart.kt:BarChart]
- LineChart `require(windowSize >= 2)`；只取 `values` 的最后 `windowSize` 个点，点数少于 2 时不绘制 [LineChart.kt:LineChart]
- 相邻点使用控制点横向取中点、纵向取两侧点 y 的三次贝塞尔，数据点处切线水平 [LineChart.kt:LineChart]
- 值域差小于 `minValueRange` 时改为「中值 ± 0.5」，归一化分母下限 0.01 [LineChart.kt:LineChart]
- 仅当点数 +1 且未满窗口时播放 `entryDurationMillis` 线性入场，动画只作用于最后一段 [LineChart.kt:LineChart]

## Side Effects
- 绘制：`Canvas` 上使用 `drawRoundRect` 与 `drawLine` [BarChart.kt:BarChart]
- 绘制：`Canvas` 上用 `Path.cubicTo` 画线，`Path` + 竖直渐变填充面积，`Stroke` 圆头描边 [LineChart.kt:LineChart]
- 读取 `CouiPageDefaults.themeColors()` 作为默认配色来源 [BarChart.kt:BarChart]
- `LaunchedEffect(data)` 写 `Animatable` 入场进度；绘制阶段读取其值，不逐帧重组 [LineChart.kt:LineChart]

## Verification
- Run: `./gradlew :coui:lintDebug`

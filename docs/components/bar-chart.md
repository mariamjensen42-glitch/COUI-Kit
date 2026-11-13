# 📊柱状图

`BarChart` 按传入的数据绘制圆角柱，用于电量使用情况这类按时间分段的读数。柱高等于 `value / maxValue`，可以逐柱指定配色区分时段。

图表只负责绘制，不做滚动、缩放、动画或交互；坐标轴刻度由 `axisLabels` 提供，自上而下排在右侧，同时作为水平网格线的位置。

## 最小调用

~~~kotlin
BarChart(
    bars = listOf(
        CouiBar(.42f),
        CouiBar(.68f),
        CouiBar(.91f, color = accent),   // 充电时段
    ),
    maxValue = 1f,
    axisLabels = listOf("100%", "50%"),
)
~~~

## 参数

- `bars`：柱序列，从左到右绘制；`CouiBar.value` 超出 `maxValue` 时按满高裁剪。
- `maxValue`：必须为正数，作为满高基准。
- `barColor`：默认柱色，`CouiBar.color` 优先。
- `axisLabels`：右侧刻度文字，自上而下；数量大于 1 且 `showGrid` 为真时同时绘制水平网格线。
- `colors`：网格线与刻度文字的颜色，默认取当前主题的页面配色。

高度、柱间距、圆角与刻度间距在 `CouiChartDefaults`。柱宽由可用宽度和柱数均分，柱数很多时柱宽会收窄；需要横向滚动时由宿主外层容器负责。

源码见 [BarChart](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/chart/BarChart.kt)。

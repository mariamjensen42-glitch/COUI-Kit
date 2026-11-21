# 📈折线图

`LineChart` 把一串读数画成滑动窗口折线，对应 ColorOS 17 手机管家「网络安全检测」页的 `NetworkSlidingWindowLineView`：只保留最近 `windowSize` 个点，新点从右侧进入、旧点从左侧挤出，线下方的面积自上而下淡出。

相邻点用三次贝塞尔连接，两个控制点的横向坐标都取两点中点、纵向分别取两侧点的 y，因此每个数据点处切线水平。这与原厂 `buildPathForPoint()` 的构造相同，曲线不会在数据点之间过冲。

## 最小调用

~~~kotlin
var values by remember { mutableStateOf(List(50) { sample(it) }) }

LineChart(values = values)
~~~

高度默认 160dp，由 `height` 参数或外层 `Modifier` 覆盖；组件只负责绘制，不处理坐标轴、图例、点击与缩放。

## 参数

- `values`：按时间顺序排列的读数，只取最后 `windowSize` 个。窗口未满时按当前点数均分宽度，窗口满了以后按 `windowSize - 1` 等分，新值把最左的点挤掉。
- `windowSize`：窗口点数，默认 `CouiLineChartDefaults.windowSize`（50，取自原厂 `DEFAULT_MAX_DATA_POINTS`），至少为 2。
- `lineColor` / `strokeWidth`：折线颜色与线宽，默认 `#00BD13` 与 1dp，端点和拐角都是圆头。
- `areaColor`：面积底色，顶部取 `CouiLineChartDefaults.areaTopAlpha`（0.2）、底部完全透明，与原厂 `shadowStartColor` / `shadowEndColor` 的 alpha 一致；默认跟随 `lineColor`。
- `height`：图表高度，默认 160dp。

纵向值域由当前窗口的极值决定。最大值与最小值之差小于 `CouiLineChartDefaults.minValueRange`（0.1）时改为「中值 ± 0.5」，避免平坦数据被纵向拉满；归一化分母的下限为 0.01。

点数比上一次多 1 且窗口未满时，最后一段做 300ms 线性生长（原厂 `startEntryAnimation`）；窗口已满后逐点替换不再播放动画。

## 与原厂的差异

- 原厂 `drawCompleteChart()` 的「超过 40 点按 σ=(n/4) 的高斯权重降采样」是另一条整段数据入口，本组件未移植；需要长序列平滑时先把数据降采样再传入。
- 原厂 `addDataPoint()` 只追加不裁剪，窗口靠调用方控制；本组件按 `windowSize` 截断，越界部分不绘制。
- 原厂用 `PathMeasure` 取动画端点再向下闭合面积；本组件直接由最后一段的插值坐标闭合，结果相同，少一次路径测量。
- 原厂只支持 `setColors(line, shadowStart, shadowEnd)` 三个颜色；本组件用 `areaColor` + 两端 alpha 表达同一渐变。

## Demo

完整示例见 [DemoChartSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoChartSamples.kt)：按 200ms 追加一个点，演示窗口滑动与入场动画。

源码见 [LineChart](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/chart/LineChart.kt)。

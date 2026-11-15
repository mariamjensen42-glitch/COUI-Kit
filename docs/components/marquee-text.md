# 🏃跑马灯文本

`MarqueeText` 是横向滚动的单行文本，对应原厂 `COUIMarqueeTextView`。文本宽度超过可用宽度时自动滚动，并在两端渐隐。

## 最小调用

~~~kotlin
MarqueeText(
    modifier = Modifier.fillMaxWidth(),
    text = "这是一条很长的提示文案，超出容器宽度时会自动滚动并在两端渐隐。",
)
~~~

组件位于 `com.cycling.coui.text`。

## 行为

- 文本宽度不超过可用宽度时按静态单行文本绘制，既不滚动也不渐隐。
- 超出时先静止显示 1 秒（原厂 `DEFAULT_SCROLL_DELAY_DURATION`），再开始滚动。
- 滚动时按 `coui_top_tips_scroll_text_interval` 的空档重复拼接，滚过一段后按同一空档回绕，视觉上无缝连续。
- 滚动速度为每秒 `coui_top_tips_scroll_speed`（25dp），与刷新率无关。
- 左右两端按 `coui_top_tips_fading_edge_size`（26dp）渐隐。
- 文本含 RTL 字符时滚动方向与 LTR 相反，与原厂按字符方向判定方向的规则一致。

固定参数在 `CouiMarqueeTextDefaults` 中，按需可在组件参数外复用。

## 排版

字号与字重取 `CouiTextKind`，与库内其他文字一致：

~~~kotlin
MarqueeText(
    text = "标题级跑马灯",
    kind = CouiTextKind.ItemTitle,
)
~~~

## 关闭滚动

`enabled = false` 时按静态单行文本绘制，不滚动也不渐隐：

~~~kotlin
MarqueeText(text = message, enabled = false)
~~~

## 无障碍

组件的无障碍描述固定为原始文本，滚动期间不会重复朗读拼接后的内容。

## 与原厂的差异

原厂用 `TextView` 的 `StaticLayout` 判定字符方向，这里沿用同一判定来源；字号、字重按库内 `CouiTextKind` 换算，滚动速度由「每帧固定步距」改为按时间推进，两者每秒位移相同。渐隐由原厂 `TextView` 的 horizontal fading edge 改为等宽区间的 `DST_IN` 线性渐变遮罩，机制不同，观感需真机同背景对照。原厂另有 `stopRoll()` / `onDetachedFromWindow()` 的命令式收尾，这里由 `enabled` 与 `LaunchedEffect` 的取消代替。

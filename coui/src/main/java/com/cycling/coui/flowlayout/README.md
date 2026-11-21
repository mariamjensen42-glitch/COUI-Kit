# FlowLayout

## Overview
标签流式布局包。`FlowLayout` 将 `CouiFlowItem` 按行排列，超出 `maxRowsFolded` 的行折叠并以淡出/裁剪隐藏，由尾部控件请求展开；对应 OEM `COUIFlowLayout` 的搜索历史行为。

## Interface
- `public @Immutable data class CouiFlowItem(key, label)` [CouiFlowLayoutDefaults.kt:CouiFlowItem]
- `public @Immutable data class CouiFlowLayoutColors(chipContainer, chipContent, control)` [CouiFlowLayoutDefaults.kt:CouiFlowLayoutColors]
- `public object CouiFlowLayoutDefaults`：项/行间距、胶囊尺寸、折叠行数常量、展开动画时长、回退宽度与 `colors(dark)` [CouiFlowLayoutDefaults.kt:CouiFlowLayoutDefaults]
- `internal val CouiFlowLayoutDefaults.MoveEase`：所有淡入淡出共用的缓动曲线 [CouiFlowLayoutDefaults.kt:MoveEase]
- `public @Composable fun FlowLayout(modifier, items, expanded, onExpandedChange, onItemClick, maxRowsFolded, maxRowsUnfolded, dark, colors, expandContentDescription, collapseContentDescription, expandContent, collapseContent)` [FlowLayout.kt:FlowLayout]

## Invariants
- `require(maxRowsFolded > 0)` 且 `maxRowsUnfolded >= maxRowsFolded`；`items` 为空时不发射布局 [FlowLayout.kt:FlowLayout]
- `expanded` 由宿主持有，控件只请求 `onExpandedChange(true/false)`，从不自行改写状态 [FlowLayout.kt:FlowControlButton]
- 每个胶囊宽度夹取到可用宽度；仅当 `used + spacing + width <= availablePx` 才并入当前行；超过 `maxRowsUnfolded` 的行不参与布局 [FlowLayout.kt:FlowLayout]
- 隐藏行保持组合并设 `alpha = hiddenAlpha`，由容器高度裁剪；容器高度随 `expanded` 立即切换，透明度单独动画 [FlowLayout.kt:FlowLayout]
- 展开控件仅在第 `maxRowsFolded` 行存在时出现并置于行尾；折叠控件仅在显示行数超过 `maxRowsFolded` 时出现；放不下时丢弃该行最后一个胶囊 [FlowLayout.kt:FlowLayout]
- `maxWidth` 非有限或非正数时回退 `fallbackWidth`（360dp）[FlowLayout.kt:FlowLayout]

## Side Effects
- 帧动画：`animateFloatAsState` 驱动 `hiddenAlpha`（进 400ms/延迟 100ms，出 300ms）与 `controlAlpha`（展开 250ms、折叠 300ms）[FlowLayout.kt:FlowLayout]
- 文本测量：`rememberTextMeasurer` + `LocalDensity` 计算胶囊宽度 [FlowLayout.kt:FlowLayout]
- 指针/无障碍：控件 `clickable(Role.Button, indication = null)` 与可选 `contentDescription` [FlowLayout.kt:FlowControlButton]；胶囊按索引回调 [FlowLayout.kt:FlowChipContent]
- 绘制：默认箭头由 `Canvas` 自绘 [FlowLayout.kt:FlowChevron]

## Verification
- Run: `./gradlew :coui:lintDebug`

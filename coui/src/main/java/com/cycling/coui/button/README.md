# Button

## Overview
button 包提供标签重载与内容槽重载的 `Button`、1–2 项的 `ButtonGroup` 与 `IconButton`，以及 `CouiButtonDefaults` / `CouiButtonGroupDefaults` / `CouiIconButtonDefaults`。点击、禁用与加载状态由组件持有；跟手光、触摸/按压形变与按钮蒙层统一复用 effect 与 input 包。

## Interface
- `public @Composable fun Button(modifier, onClick, label, style, enabled, loading, dark, colors, shape, interactionSource, loadingDescription, description)`（标签重载）。`[Button.kt:Button]`
- `public @Composable fun Button(modifier, onClick, style, enabled, loading, dark, colors, shape, interactionSource, loadingDescription, content: @Composable RowScope.() -> Unit)`（内容槽重载）。`[Button.kt:Button]`
- `internal @Composable fun CouiButtonDescLabel(text, description, color, modifier)`。`[Button.kt:CouiButtonDescLabel]`
- `public @Immutable data class CouiButtonGroupItem(label, onClick, description, enabled, style, colors)`。`[ButtonGroup.kt:CouiButtonGroupItem]`
- `public object CouiButtonGroupDefaults`：`maxItems = 2`、按钮宽度/最小高/间距/内缩与 `compactWidthThreshold = 600dp`。`[ButtonGroup.kt:CouiButtonGroupDefaults]`
- `public @Composable fun ButtonGroup(modifier, items, dark, shape)`。`[ButtonGroup.kt:ButtonGroup]`
- `public enum class CouiButtonStyle { Primary, Secondary, Text }`；`public @Immutable data class CouiButtonColors(container, content, disabledContainer, disabledContent)`。`[CouiButtonDefaults.kt:CouiButtonStyle]` `[CouiButtonDefaults.kt:CouiButtonColors]`
- `public object CouiButtonDefaults`：尺寸/内边距/圆点与 `shape`，`primaryColors` / `secondaryColors` / `textColors` / `colors(style, accent, dark, onAccent)`。`[CouiButtonDefaults.kt:CouiButtonDefaults]`
- `public object CouiIconButtonDefaults`（`size`、`smallSize`、`largeSize`、`shape`）；`public @Composable fun IconButton(modifier, onClick, enabled, dark, size, shape, containerColor, contentColor, interactionSource, content)`。`[IconButton.kt:CouiIconButtonDefaults]` `[IconButton.kt:IconButton]`

## Invariants
- `interactive = enabled && !loading` 统一控制点击；加载中不接受点击，但容器与内容色只按 `enabled` 选择，加载不改变配色。`[Button.kt:Button]`
- 加载态把标签 Row 以 `graphicsLayer alpha = 0` 保留占位，再叠加三个 1dp 圆点；`stateDescription` 与 `liveRegion(Polite)` 仅在 `loading && loadingDescription != null` 时写入。`[Button.kt:Button]`
- 标签重载在 `description == null` 时用 `Text(kind = ButtonLabel, maxLines = 2)`；有描述时两行各 `BasicText(maxLines = 1)`，字号缩放上限 `descMaxFontScale = 1.15`。`[Button.kt:Button]` `[Button.kt:CouiButtonDescLabel]`
- 绘制顺序固定为内容后跟手光；`clip` 在背景之前；`Text` 样式关闭按压蒙层（`pressFeedback = style != Text`）。`[Button.kt:Button]`
- 按钮最小 44dp 高、132dp 宽，四周 12dp 留白；`Primary` 使用 `SpotlightPreset.OpaqueMedium1`，其余样式使用 `OpaqueMedium2`。`[Button.kt:Button]`
- 加载圆点由 `withFrameNanos` 循环驱动：`phase = ((now - start) % 800ms) / 800ms`，按 `index / 3` 错相，三角波 0→1→0，alpha 在 0.35..1。`[Button.kt:CouiButtonLoadingDots]`
- `CouiButtonGroupItem` 要求 `label` 非空白；`ButtonGroup` 要求 `items.size` 在 1..2。`[ButtonGroup.kt:CouiButtonGroupItem]` `[ButtonGroup.kt:ButtonGroup]`
- `ButtonGroup` 的换行判据：带 `description` 的项不参与；其余以 `maxIntrinsicWidth(softWrap = false)` 与扣除内缩后的可用宽比较，任一项超出即整体竖排。`[ButtonGroup.kt:ButtonGroup]`
- `compact = LocalConfiguration.screenWidthDp < 600dp`，无上界时 `layoutWidth` 回退 600dp；竖排按 `items.reversed()` 渲染（首项在底部）；横排在 compact 时按原顺序 `weight(1f)`，否则使用固定 `mediumButtonWidth`（152dp）。`[ButtonGroup.kt:ButtonGroup]`
- `CouiButtonDefaults.shape` 仅在 `width == height` 时取圆形，否则取胶囊；禁用颜色为显式取值，不由正常色推导。`[CouiButtonDefaults.kt:CouiButtonDefaults]` `[CouiButtonDefaults.kt:CouiButtonColors]`
- `IconButton` 先应用 `minimumInteractiveComponentSize()` 再 `size(size)`，40dp/32dp 仍保底 48dp 命中区；禁用内容 alpha 0.38，`contentColor` 未指定时回退 `onSurface`。`[IconButton.kt:IconButton]`
- `interactionSource` 为空时各自 `remember`；传入源同时驱动 `clickable` 与按钮反馈。`[Button.kt:Button]` `[IconButton.kt:IconButton]`

## Side Effects
- 通过 `ProvideCouiContent` 在槽内提供内容色。`[Button.kt:Button]` `[IconButton.kt:IconButton]`
- 消费 `CouiTheme.isDark`、`colorSchemeFor`、`contentColor`、`shapes.circle`；`IconButton` 读取 `Coui.defaultStyle.minimumInteractiveComponentSize()`。`[Button.kt:Button]` `[IconButton.kt:IconButton]`
- 跟手光：`rememberCouiSpotlight(interactive, preset, dark, cancelOnConsumedMove = true)` 返回输入 Modifier 与 `drawLight`，在 `drawWithContent` 中绘制。`[Button.kt:Button]`
- 手势与变换：`couiTouchMotion` / `couiPressMotion(cancelOnConsumedMove = true)` 处理指针，`graphicsLayer` 写缩放与平移。`[Button.kt:Button]`
- 交互蒙层：`couiButtonFeedback(dark, source, interactive, pressFeedback)` 由 `MutableInteractionSource` 驱动，`clickable(indication = null, role = Role.Button)`。`[Button.kt:Button]`
- 平台文本：`android.graphics.Typeface.create` 与 text 包的 `couiTextStyle` / `couiAdaptFontSize`，读取 `LocalConfiguration` / `LocalDensity`。`[Button.kt:rememberCouiDescTextStyle]`
- 帧时钟：`LaunchedEffect` + `withFrameNanos` 驱动加载点，`Canvas.drawCircle` 绘制。`[Button.kt:CouiButtonLoadingDots]`
- `ButtonGroup` 使用 `rememberTextMeasurer` 测量文案，读取 `LocalConfiguration.screenWidthDp` 与 `LocalDensity`，在 `BoxWithConstraints` 内取约束。`[ButtonGroup.kt:ButtonGroup]`

## Verification
- Run: `./gradlew :coui:lintDebug`

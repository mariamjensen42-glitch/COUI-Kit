# ButtonBar

## Overview
buttonBar 包提供 1–3 项的操作按钮条：恰好两项时比较文案宽度决定横排或竖排，一项或三项恒竖排。按钮高度按文字行数取 44dp/56dp 下限，内容超出父高时按钮条独立纵向滚动。每项委托 button 包的 `Button` 渲染。

## Interface
- `public @Immutable data class CouiButtonBarItem(text, onClick, enabled = true, style = Secondary, colors = null)`。`[ButtonBar.kt:CouiButtonBarItem]`
- `public object CouiButtonBarDefaults`：`maxItems = 3`、按钮内边距/最小高/间距、外层 `edgePadding` / `insidePadding` / `parentVerticalPadding` / `parentBottomPadding`、`maxWidth = 392dp`。`[ButtonBar.kt:CouiButtonBarDefaults]`
- `public @Composable fun ButtonBar(modifier, items, dark, shape)`。`[ButtonBar.kt:ButtonBar]`
- `internal @Composable fun <T> CouiButtonBarLayout(items, modifier, horizontalItems, text, enabled, textStyle, contentVerticalPadding, onStacked, content)`。`[ButtonBar.kt:CouiButtonBarLayout]`

## Invariants
- `ButtonBar` 要求 `items.size` 在 1..3；`CouiButtonBarItem` 要求 `text` 非空白。`[ButtonBar.kt:ButtonBar]` `[ButtonBar.kt:CouiButtonBarItem]`
- `stacked = horizontalItems.size != 2 || 任一标签 maxIntrinsicWidth(softWrap = false) > available`；即一项或三项恒竖排，只有恰好两项才比较宽度。`[ButtonBar.kt:CouiButtonBarLayout]`
- `available = cellWidth - (2 * buttonHorizontalPadding + edgePadding + insidePadding)`，`cellWidth = min(parentWidth, 392dp) / horizontalItems.size`；`verticalAvailable = parentWidth - 2 * edgePadding - 2 * buttonHorizontalPadding`，下限 0。`[ButtonBar.kt:CouiButtonBarLayout]`
- 单按钮高度 = `max(文本高度(最多 2 行、省略、按可用宽约束) + 2 * contentVerticalPadding, 44dp 或 56dp(多行))`。`[ButtonBar.kt:CouiButtonBarLayout]`
- `naturalHeight`：竖排为各按钮高加 `2 * parentVerticalPadding` 求和，再加 `parentBottomPadding`、减一次 `parentVerticalPadding`；横排为最大按钮高加 `parentVerticalPadding + parentBottomPadding`。`[ButtonBar.kt:CouiButtonBarLayout]`
- 仅当 `naturalHeight > constraints.maxHeight` 时挂 `verticalScroll`，因为 `verticalScroll` 即使不可滚也会裁剪。`[ButtonBar.kt:CouiButtonBarLayout]`
- `onStacked` 通过 `SideEffect` 在每次重组时按当前 `stacked` 回调，且仅当回调非空。`[ButtonBar.kt:CouiButtonBarLayout]`
- 横排按 `horizontalItems` 顺序 `weight(1f)`，首尾外边 16dp、内部 4dp；竖排按 `items` 顺序，左右 16dp、相邻上下 4dp、末项下沿 16dp。`[ButtonBar.kt:CouiButtonBarLayout]`
- 布局顺序即按钮顺序，横排与竖排都不重排。`[ButtonBar.kt:CouiButtonBarItem]`
- 竖排的 `multiline` 取自 `textWidths` 与 `verticalAvailable` 比较，仅测量 `horizontalItems`；项不在 `horizontalItems` 中时默认 false。`[ButtonBar.kt:CouiButtonBarLayout]`
- `CouiButtonBarLayout` 声明 `enabled` 参数但函数体未引用，竖排判断不区分禁用项。`[ButtonBar.kt:CouiButtonBarLayout]`
- `ButtonBar` 向布局传 `contentVerticalPadding = CouiButtonDefaults.verticalPadding`，每项用 `Button` 渲染并加 `fillMaxWidth` 与 `heightIn(min)`。`[ButtonBar.kt:ButtonBar]`

## Side Effects
- 消费 `CouiTheme.isDark` / `colorSchemeFor(dark)` 生成默认配色。`[ButtonBar.kt:ButtonBar]`
- 读取 `LocalDensity`，使用 `rememberTextMeasurer` 测量文字、`rememberScrollState` / `verticalScroll` 处理超高。`[ButtonBar.kt:CouiButtonBarLayout]`
- `SideEffect` 调用宿主 `onStacked` 回调，上报竖排状态。`[ButtonBar.kt:CouiButtonBarLayout]`
- 每项委托 button 包的 `Button`，继承其 spotlight、`couiTouchMotion` / `couiPressMotion`、`couiButtonFeedback` 与 `clickable` 的指针和绘制副作用。`[ButtonBar.kt:ButtonBar]`
- `BoxWithConstraints` 读取父约束，无界宽时回退 392dp。`[ButtonBar.kt:CouiButtonBarLayout]`

## Verification
- Run: `./gradlew :coui:lintDebug`

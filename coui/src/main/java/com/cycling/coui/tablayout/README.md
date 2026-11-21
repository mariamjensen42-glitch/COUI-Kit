# TabLayout

## Overview
tablayout 包提供受控的单行页签条：选中索引与回调归宿主，组件按 segmentbutton 的宽度算法分配每段宽度，并绘制容器胶囊与滑动选中胶囊。`Fixed` 模式在视口内收缩，`Scrollable` 保留自然宽度、可横向滚动并把选中项滚向中间。

## Interface
- `public @Immutable data class CouiTabItem(key, label, enabled = true)`。`[CouiTabLayoutDefaults.kt:CouiTabItem]`
- `public enum class CouiTabLayoutMode { Fixed, Scrollable }`。`[CouiTabLayoutDefaults.kt:CouiTabLayoutMode]`
- `public @Immutable data class CouiTabLayoutColors(selectedText, unselectedText, indicator, container)`。`[CouiTabLayoutDefaults.kt:CouiTabLayoutColors]`
- `public object CouiTabLayoutDefaults`：`height = 40dp`、`textSize = 16sp`、`tabMaxWidth = 128dp`、`containerPadding = 4dp`、`tabMinWidth = 52dp`、`labelPadding = 12dp`、滚动与指示器时长常量、`internal indicatorDurationMillis(distance)`、`colors(dark)`。`[CouiTabLayoutDefaults.kt:CouiTabLayoutDefaults]` `[CouiTabLayoutDefaults.kt:indicatorDurationMillis]`
- `public @Composable fun TabLayout(modifier, tabs, selectedIndex, onSelectedIndexChange, mode, dark, colors)`。`[TabLayout.kt:TabLayout]`

## Invariants
- `tabs` 为空时直接 return，不发出布局。`[TabLayout.kt:TabLayout]`
- 选中状态归宿主：`selectedIndex` 被 `coerceIn(0, tabs.lastIndex)`；重复点击当前项仍回调；禁用项不响应选择但仍占位。`[TabLayout.kt:TabLayout]`
- 文字只测量一次：`maxLines = 1`、`maxWidth = tabMaxWidth`；自然宽 = `max(tabMinWidth, 文字宽 + 2 * labelPadding)`。`[TabLayout.kt:TabLayout]`
- `Fixed` 内容宽 = `min(naturalTotal, viewport)`（视口无界时为 `Float.MAX_VALUE`），`Scrollable` 为 `naturalTotal`；`contentArea = contentWidth - 2 * containerPadding`，最终宽度由 `segmentWidths` 分配。`[TabLayout.kt:TabLayout]`
- 指示器动画分两阶段：首次仅 `snapTo(center, width, selection)`，之后三个 `Animatable` 并行 `animateTo`，时长为 `min(300, 150 + |selected - previousSelected| * 50)`。`[TabLayout.kt:TabLayout]`
- `Animatable.value` 不是 snapshot state，指示器中心、宽度与 `selection` 必须经 `asState()` 读取才会触发重组与重绘。`[TabLayout.kt:TabLayout]`
- 滚动仅在 `Scrollable && contentWidthPx > viewportPx` 时启用：先等待 `snapshotFlow { scrollState.maxValue }.first { it > 0 }`，再把目标中心居中并 `coerceIn(0, maxValue)`，`animateScrollTo` 300ms。`[TabLayout.kt:TabLayout]`
- 文字颜色 = `lerp(unselectedText, selectedText, (1 - |index - selection|).coerceIn(0f, 1f))`；当前项用 `FontWeight.Medium`，其余 `Normal`。`[TabLayout.kt:TabLayout]`
- 自定义 Layout 高 `constrainHeight(40dp)`，子项以 `Constraints.fixed(widths[index], height)` 测量并从 `paddingPx` 起 `placeRelative`，布局宽 `contentWidthPx`。`[TabLayout.kt:TabLayout]`
- 选中胶囊仅在 `width > 0` 且 `indicatorHeight = height - 2 * containerPadding > 0` 时绘制；RTL 中心由 `SegmentGeometry(rtl)` 预先镜像。`[TabLayout.kt:TabLayout]`
- 每项语义为 `selected = (index == selected)` 且 `role = Role.Tab`。`[TabLayout.kt:TabLayout]`
- 测量缓存键固定：`textWidths` 依赖 `(tabs, textStyle, density, tabMaxPx)`，`widths` 依赖 `(naturalWidths, contentAreaPx)`，`geometry` 依赖 `(contentWidthPx, heightPx, paddingPx, widths, rtl)`。`[TabLayout.kt:TabLayout]`

## Side Effects
- 消费 `CouiTheme.isDark` 与 `colorSchemeFor(dark)`。`[CouiTabLayoutDefaults.kt:CouiTabLayoutDefaults]`
- 跨包复用 segmentbutton：`CouiSegmentedControlDefaults.indicatorColor` / `containerColor` 取色，`SegmentGeometry` / `segmentWidths` 生成几何。`[CouiTabLayoutDefaults.kt:CouiTabLayoutDefaults]` `[TabLayout.kt:TabLayout]`
- 读取 `LocalDensity` 与 `LocalLayoutDirection`，`rememberTextMeasurer` 测量文字。`[TabLayout.kt:TabLayout]`
- 指针与无障碍：每项 `clickable(indication = null, role = Role.Tab)`，语义带 `selected`。`[TabLayout.kt:TabLayout]`
- 绘制与滚动：`drawBehind` 画容器与选中胶囊；`Animatable` + `tween` 走帧时钟；`horizontalScroll` + `animateScrollTo` 滚动。`[TabLayout.kt:TabLayout]`
- `BoxWithConstraints` 读取父约束，`defaultMinSize(minHeight = 40dp)`。`[TabLayout.kt:TabLayout]`

## Verification
- Run: `./gradlew :coui:lintDebug`

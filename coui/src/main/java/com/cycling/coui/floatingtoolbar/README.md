# FloatingToolbar

## Overview
胶囊悬浮操作栏包。`FloatingToolbar` 让一排同级操作共享一个玻璃表面、跟手高光与位移形变，材质与阴影复用浮动效果链路；`FloatingToolbarItem` 提供可点击图标项，`CouiFloatingToolbarDefaults` 提供尺寸与样式。

## Interface
- `public data class CouiFloatingToolbarStyle(dark, shape, material, shadow, contentColor, lightColor, contourGlowEnabled)` [CouiFloatingToolbarDefaults.kt:CouiFloatingToolbarStyle]
- `internal val LocalCouiFloatingToolbarStyle: ProvidableCompositionLocal<CouiFloatingToolbarStyle>`：未在栏内使用时以 `error(...)` 失败 [CouiFloatingToolbarDefaults.kt:LocalCouiFloatingToolbarStyle]
- `public object CouiFloatingToolbarDefaults`：`height`/`itemSize`/`iconSize`/`capsuleShape` 与 `@RequiresApi(29) style(dark, themeTint)` [CouiFloatingToolbarDefaults.kt:CouiFloatingToolbarDefaults]
- `public @RequiresApi(33) @Composable fun FloatingToolbar(modifier, backdrop, dark, style, renderer, content: RowScope.() -> Unit)` [FloatingToolbar.kt:FloatingToolbar]
- `public @Composable fun FloatingToolbarItem(modifier, onClick, enabled, content)` [FloatingToolbar.kt:FloatingToolbarItem]

## Invariants
- `FloatingToolbar` 需要显式 `backdrop` 或 `COUI Scaffold` 栏位提供的 `LocalCouiBackdrop`，否则 `checkNotNull` 失败 [FloatingToolbar.kt:FloatingToolbar]
- `FloatingToolbarItem` 必须位于 `FloatingToolbar` 内，否则读取样式 CompositionLocal 直接抛错 [CouiFloatingToolbarDefaults.kt:LocalCouiFloatingToolbarStyle]
- 跟手光、位移回弹与形变由整条胶囊持有；操作项只承担点击与悬停，不各自绘制玻璃表面或高光 [FloatingToolbar.kt:FloatingToolbar]
- 内容与材质读取同一组弹簧值，避免缩放时图标与玻璃错位 [FloatingToolbar.kt:FloatingToolbar]
- 拖动由操作栏接管（`consumeDrag = true`），纵向滚动不会带着胶囊位移 [FloatingToolbar.kt:FloatingToolbar]
- 操作项最小宽度为 `itemSize`；禁用态只以 `0.38` alpha 淡化内容，不改变宿主或图标颜色 [FloatingToolbar.kt:FloatingToolbarItem]
- `contentColor` 默认取 `onSurface` 的 80%；显式传 `Color.Unspecified` 时不提供内容色，图标退回环境内容色 [CouiFloatingToolbarDefaults.kt:CouiFloatingToolbarStyle]、[FloatingToolbar.kt:FloatingToolbarItem]
- 样式材质/阴影构造要求 API 29，`FloatingToolbar` 装配要求 API 33；省略 `renderer` 时创建独立的单主体绘制器 [FloatingToolbar.kt:FloatingToolbar]
- 操作栏使用 20px 背景模糊，与库内搜索胶囊同一档 [CouiFloatingToolbarDefaults.kt:CouiFloatingToolbarDefaults]

## Side Effects
- CompositionLocal 供给 `LocalCouiFloatingToolbarStyle` [FloatingToolbar.kt:FloatingToolbar]
- 指针输入：`couiTouchMotion`（`consumeDrag = true`）、`couiPressMotion` 与 `clickable(Role.Button)` [FloatingToolbar.kt:FloatingToolbarItem]
- 跟手高光：`rememberCouiSpotlight(interactive = true, ...)` 并在 `drawBehind` 绘制 [FloatingToolbar.kt:FloatingToolbar]
- 图形层：`rememberGraphicsLayer` + `drawWithContent { contentLayer.record { ... } }` [FloatingToolbar.kt:FloatingToolbar]
- 玻璃表面：`couiFloatingSurface` 接收 `backdrop`、`renderer`、`material`、`shadow` 与 `shape` [FloatingToolbar.kt:FloatingToolbar]
- 内容色：`ProvideCouiContent(color = style.contentColor)` 下传 [FloatingToolbar.kt:FloatingToolbarItem]
- API 门控：`@RequiresApi(29)` 样式构造与 `@RequiresApi(33)` 组件装配 [CouiFloatingToolbarDefaults.kt:CouiFloatingToolbarDefaults]

## Verification
- Run: `./gradlew :coui:lintDebug`

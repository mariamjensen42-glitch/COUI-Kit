# TopAppBar

## Overview
顶栏包提供固定 52dp 的 `TopAppBar`、由 `CouiCollapsingAppBarState` 驱动的大标题 `LargeTopAppBar` / `CollapsingTopAppBar`、API 33 搜索入场组件（`TopAppBarSearch` / `TopAppBarSearchButton` / `TopAppBarSearchAction` 与内部 `TopAppBarSearchPair`），以及受控的 `UserFollowBar`。`TopAppBarSurface` 负责背景模糊与渐变，并向内容提供内部的 backdrop / overlay CompositionLocal。

## Interface
- `public @Composable fun TopAppBar(modifier, title, navigationIcon, actions, dark, colors, windowInsets, backdrop)`；`public @Composable fun LargeTopAppBar(modifier, title, navigationIcon, actions, scrollBehavior, dark, colors, windowInsets, expandedHeight, collapsedHeight, backdrop, expandedTitleStyle, collapsedTitleStyle)`。`[TopAppBar.kt:TopAppBar]` `[TopAppBar.kt:LargeTopAppBar]`
- `public object CouiCollapsingAppBarDefaults`（`titleHeight`、`titleOnlyHeight`、`collapsedTitleHeight`、标题尺寸与偏移、`resultsStartOffset`；`alphaScrollThreshold` 等为 `internal`）。`[CouiCollapsingAppBarState.kt:CouiCollapsingAppBarDefaults]`
- `public @Stable class CouiCollapsingAppBarState`（`collapsedFraction`、`contentTranslationY`、`contentPadding`、`nestedScrollConnection`）；`public @Composable fun rememberCouiCollapsingAppBarState(canScroll)`。`[CouiCollapsingAppBarState.kt:CouiCollapsingAppBarState]` `[CouiCollapsingAppBarState.kt:rememberCouiCollapsingAppBarState]`
- `public @Stable class CouiTopAppBarScrollBehavior(state)`，只转发 `state.nestedScrollConnection`。`[CouiTopAppBarScrollBehavior.kt:CouiTopAppBarScrollBehavior]`
- `public data class CouiTopAppBarColors`；`public object CouiTopAppBarDefaults`（`height`、`expandedHeight`、`collapsedHeight`、`fadeHeight`、`windowInsets`、`titleTextStyle()`、`colors(...)`、`exitUntilCollapsedScrollBehavior(canScroll)`）。`[CouiTopAppBarDefaults.kt:CouiTopAppBarColors]` `[CouiTopAppBarDefaults.kt:CouiTopAppBarDefaults]`
- `public data class CouiToolbarStyle(dark, circleShape, capsuleShape, material, shadow, contentColor, textStyle, cursorColor, contourGlowEnabled)`。`[CouiToolbarStyle.kt:CouiToolbarStyle]`
- `public @Composable fun TopAppBarSurface(modifier, backdrop, fadeHeight, referenceBlurRadius, bottomExtension, backgroundAlpha, gradientSpeed, gradientColor, dark, topExtension, blurFactor, content)`。`[TopAppBarSurface.kt:TopAppBarSurface]`
- `public @RequiresApi(33) @Composable fun TopAppBarSearch(...)`、`TopAppBarSearchButton(...)`、`TopAppBarSearchAction(...)`。`[TopAppBarSearch.kt:TopAppBarSearch]` `[TopAppBarSearch.kt:TopAppBarSearchButton]` `[TopAppBarSearch.kt:TopAppBarSearchAction]`
- `public @Stable class CouiTopAppBarSearchState`；`public @RequiresApi(33) @Composable fun rememberCouiTopAppBarSearchState(expanded, renderer)`。`[CouiTopAppBarSearchState.kt:CouiTopAppBarSearchState]` `[CouiTopAppBarSearchState.kt:rememberCouiTopAppBarSearchState]`
- `public @RequiresApi(33) @Composable fun TopAppBarButton(...)`；`public @Composable fun TopAppBarButtonGroup(...)`、`TopAppBarActionVisibility(...)`。`[TopAppBarButton.kt:TopAppBarButton]` `[TopAppBarButtonGroup.kt:TopAppBarButtonGroup]` `[TopAppBarButtonGroup.kt:TopAppBarActionVisibility]`
- `public @Composable fun CollapsingTopAppBar(modifier, state, searchState, title, color, backdrop, backgroundColor, dark, searchBar)`。`[CollapsingTopAppBar.kt:CollapsingTopAppBar]`
- `public @Composable fun UserFollowBar(title, modifier, subtitle, following, onFollowingChange, fill, avatar, followLabel, followingLabel, enabled, dark, colors, metrics)`。`[UserFollowBar.kt:UserFollowBar]`
- `public object CouiTopAppBarButtonDefaults`（`style`、`systemMaterial`、`material`、`shadow`）；`public object CouiUserFollowDefaults` 及 `CouiUserFollowMetrics` / `CouiUserFollowColors`。`[CouiTopAppBarButtonDefaults.kt:CouiTopAppBarButtonDefaults]` `[CouiUserFollowDefaults.kt:CouiUserFollowDefaults]` `[CouiUserFollowDefaults.kt:CouiUserFollowMetrics]` `[CouiUserFollowDefaults.kt:CouiUserFollowColors]`
- `internal` 跨文件装配：`LocalCouiToolbarStyle`、`LocalCouiToolbarBackdrop`、`LocalCouiToolbarOverlay`、`LocalCouiToolbarContentColor`、`TopAppBarSearchPair`、`topAppBarSearchPairGeometry`。`[CouiToolbarStyle.kt:LocalCouiToolbarStyle]` `[TopAppBarSurface.kt:LocalCouiToolbarBackdrop]` `[TopAppBarSurface.kt:LocalCouiToolbarOverlay]` `[CouiTopAppBarButtonDefaults.kt:LocalCouiToolbarContentColor]` `[TopAppBarSearchPair.kt:TopAppBarSearchPair]` `[CouiTopAppBarSearchPairGeometry.kt:topAppBarSearchPairGeometry]`

## Invariants
- `LargeTopAppBar` 组合期 `require` 高度有限、`collapsedHeight > 0.dp`、`expandedHeight > collapsedHeight`；每次组合在 `SideEffect` 中先 `updateRange(...)` 再 `updateSearch(false, false)`。`[TopAppBar.kt:LargeTopAppBar]`
- `AppBarLayout` 要求有界宽度且恰有三个子节点（navigation / actions / title）；`slotWidth = width - 2 * min(horizontalPadding, width / 2)`；栏偏移钳制在 `0..(expandedPx - collapsedPx)`。`[TopAppBar.kt:AppBarLayout]`
- `collapsedFraction = (offsetPx / rangePx).coerceIn(0f, 1f)`；`consume` 把目标钳制到 `0..rangePx`，栏移动时 `contentScrollDistancePx` 归零；`onPreScroll` 只处理负增量、`onPostScroll` 只处理正增量，`searchLocked` 时两者均返回零。`[CouiCollapsingAppBarState.kt:consume]` `[CouiCollapsingAppBarState.kt:nestedScrollConnection]`
- `onPostFling` 在 `hasSearchBar && !canScroll()` 时展开到 0，否则在 `0 < logicalOffset < rangePx` 时按半程吸附并总返回 `Velocity.Zero`；`contentPadding` 只把 top 钳制为非负，左右与底部透传。`[CouiCollapsingAppBarState.kt:nestedScrollConnection]` `[CouiCollapsingAppBarState.kt:contentPadding]`
- `updateSearch` 总是写 `searchLocked = active`，展开前记录 `restoreOffset`，收起后回到该值；`updateRange` 在范围变化时先 `dispose` 弹簧，再按旧比例重设 offset 与 `restoreOffset`；状态以 4 个 Float 经 `rememberSaveable` 保存：前两项按 `rangePx` 归一化，第三项为搜索态 0/1，第四项为原始像素 `contentScrollDistancePx`。`[CouiCollapsingAppBarState.kt:updateSearch]` `[CouiCollapsingAppBarState.kt:updateRange]` `[CouiCollapsingAppBarState.kt:rememberCouiCollapsingAppBarState]`
- `TopAppBarSurface` 要求恰有两个子节点且顺序为 background / content；content 决定宽度，background 高为 `topBar.height + topExtensionPx + bottomExtension` 并置于 `y = -topExtensionPx`；`backgroundAlpha` 钳制 0..1，渐变颜色 alpha 为 0 时不发射渐变层。`[TopAppBarSurface.kt:TopAppBarSurface]`
- 背景渲染按 SDK 分流：API 33+ 使用 `drawCouiGradientBackdrop`，以下版本使用固定三层 `CouiCoverageBlurEffect` 并忽略 `blurFactor`；渐变由 65 个 CLAMP 停靠点锚定背景下沿。`[TopAppBarSurface.kt:progressiveBlur]`
- `TopAppBarSearch` 在 `(visible && expanded) || expandedWidth == 0f` 时写入测量宽度；仅 `motion.present && expandedWidth > 0f` 时渲染覆盖层并要求非空 `LocalCouiToolbarOverlay`；收起期间冻结 `expandedCenter`。`[TopAppBarSearch.kt:TopAppBarSearch]`
- `TopAppBarSearchButton` 仅在未呈现或未展开且无 `sourceCenter` 时写入触点中心，展开时禁用点击；`TopAppBarSearchAction` 在默认分支（`visibleWhileSearching = false`）展开时布局宽为 0，且只在 `toolbarAlpha > 0` 时放置内容；`visibleWhileSearching = true` 时改走 `animateBounds` 并无条件组合内容。`[TopAppBarSearch.kt:TopAppBarSearchButton]` `[TopAppBarSearch.kt:TopAppBarSearchAction]`
- `LocalCouiToolbarStyle` 无兜底（默认抛错），`LocalCouiToolbarBackdrop` 与 `LocalCouiToolbarOverlay` 默认 null 且消费者 `checkNotNull`，因此搜索组件必须位于 `TopAppBarSurface` 与其样式 Provider 内。`[CouiToolbarStyle.kt:LocalCouiToolbarStyle]` `[TopAppBarSurface.kt:LocalCouiToolbarBackdrop]`
- `TopAppBarSearchPair` 先测量 action 槽，再测量固定宽度 input 槽，几何在测量期写入；`actionAlpha >= 1f` 前 action 节点不放置，避免提前拦截点击。`[TopAppBarSearchPair.kt:TopAppBarSearchPair]`
- `TopAppBarButton` 的 `capsule` 由实测宽高是否相等决定，首帧未测量时按圆形处理；文字模式至少 60.dp 宽、40.dp 高，图标模式 40.dp 方形，禁用内容 alpha 0.38。`[TopAppBarButton.kt:TopAppBarButton]`
- `TopAppBarButtonGroup` 要求非空 `LocalCouiToolbarOverlay` 并在覆盖层内渲染；`TopAppBarActionVisibility` 在 `animateSize = false` 时仅于过渡期保留内容，图层 outsets 固定 120f/density。`[TopAppBarButtonGroup.kt:TopAppBarButtonGroup]` `[TopAppBarButtonGroup.kt:TopAppBarActionVisibility]`
- `CollapsingTopAppBar` 把偏移钳制到 `[0, titleHeight]`（有搜索栏）或 `[0, titleHeight - collapsedHeight]`，并在每次组合的 `SideEffect` 中先 `updateRange(...)` 再 `updateSearch(...)`；无搜索栏时标题 alpha 恒为 1。`[CollapsingTopAppBar.kt:CollapsingTopAppBar]`
- `UserFollowBar` 的布局要求四个子节点顺序 avatar / title / subtitle / button；组件受控，按钮点击只调用 `onFollowingChange(!following)`，禁用时图层 alpha 0.3。`[UserFollowBar.kt:UserFollowBar]` `[UserFollowBar.kt:FollowButton]`
- `CouiTopAppBarButtonDefaults.style` 从 `LocalCouiToolbarContentColor` 与 `LocalCouiContourGlowEnabled` 取值；`shadow` 的投影参数按 contour 切换，`CouiUserFollowDefaults.colors` 的已关注容器为 accent 0.3（深色）或 0.15（浅色）。`[CouiTopAppBarButtonDefaults.kt:CouiTopAppBarButtonDefaults.style]` `[CouiUserFollowDefaults.kt:CouiUserFollowDefaults.colors]`
- `TopAppBar`、`LargeTopAppBar`、`CollapsingTopAppBar` 的 `backdrop` 默认 `checkNotNull(LocalCouiBackdrop.current)`，必须显式传入或处于 `ProvideCouiBackdrop` 作用域。`[TopAppBar.kt:TopAppBar]` `[CollapsingTopAppBar.kt:CollapsingTopAppBar]`

## Side Effects
- `TopAppBarSurface` 以 `rememberCouiBackdrop(parent = backdrop)` 创建 destination，向内容提供 `LocalCouiToolbarBackdrop` 与当前 `SharedTransitionScope`，并用 `pointerInput` 让空白区域参与命中。`[TopAppBarSurface.kt:TopAppBarSurface]`
- 低版本背景合成使用 `android.graphics.RenderEffect.createOffsetEffect` / `createBlendModeEffect`（先 DST_IN 后 SRC_OVER）；渐变使用 `android.graphics.LinearGradient` 与 `Color.pack`。`[TopAppBarSurface.kt:progressiveBlur]` `[TopAppBarSurface.kt:fadeEffect]`
- `TopAppBar` / `LargeTopAppBar` 提供 `LocalCouiDarkTheme`，经 `LocalCouiToolbarContentColor` 与 `ProvideCouiContent` 注入槽位颜色，并对根布局应用 `windowInsetsPadding(windowInsets)`。`[TopAppBar.kt:TopAppBar]` `[TopAppBar.kt:AppBarLayout]`
- `CouiTopAppBarDefaults.windowInsets` 取 `systemBars` 与 `displayCutout` 的 Top+Horizontal 并集；标题样式来自 `rememberCouiSystemTextStyle`，绑定系统字体缩放。`[CouiTopAppBarDefaults.kt:CouiTopAppBarDefaults]`
- 搜索组件消费 `LocalCouiToolbarStyle`、`LocalCouiToolbarOverlay`、`LocalCouiToolbarBackdrop`、`LocalDensity`、`LocalLayoutDirection` 与 `LocalSoftwareKeyboardController`，经 `renderInSharedTransitionScopeOverlay` 与 `animateBounds` 渲染。`[TopAppBarSearch.kt:TopAppBarSearch]` `[TopAppBarSearch.kt:TopAppBarSearchAction]`
- 搜索模糊按半径重建 `android.graphics.RenderEffect.createBlurEffect(..., DECAL)`，平移图层设置 `LayerOutsets(120f/density)`；输入请求焦点并用 `ImeAction.Search` 提交。`[TopAppBarSearch.kt:TopAppBarSearch]` `[TopAppBarSearch.kt:SearchInput]`
- `SearchSurface` 向 `couiFloatingSurface` 传入 bounds/scale，`rememberCouiSpotlight(..., OpaqueMedium2, dark)` 绘制聚光层，内容录进 `rememberGraphicsLayer`。`[TopAppBarSearch.kt:SearchSurface]`
- `CouiCollapsingAppBarState` 通过 `NestedScrollConnection` 消费滚动与 fling，用 `androidx.dynamicanimation` 的 `SpringAnimation` 写 Compose 状态，并经 `rememberSaveable` 恢复。`[CouiCollapsingAppBarState.kt:CouiCollapsingAppBarState]`
- `UserFollowBar` 用 `stringResource` 默认值加载关注文案，头像以 `CouiCapsuleShape` 裁剪；`FollowButton` 安装 `MutableInteractionSource` 并应用 `couiStateMask` 与无 ripple 的 `clickable`。`[UserFollowBar.kt:UserFollowBar]` `[UserFollowBar.kt:FollowButton]`

## Verification
- Run: `./gradlew :coui:lintDebug`

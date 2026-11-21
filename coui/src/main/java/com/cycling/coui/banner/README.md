# Banner

## Overview
轮播组件包。`Banner` 复刻原厂 `COUIBanner` 的手机形态（`type = 0`）：内部一张 `HorizontalPager`，两侧露出相邻卡片，底部居中叠一排 `PageIndicator`；默认自动翻页并按首尾补页形成无限循环。封面图之外的形态（平板双列 `type = 1`、外部 `PageTransformer`）不在范围内。

## Interface
- `public fun Banner(count, modifier, state, autoLoop, loopDurationMillis, scrollDurationMillis, pageMargin, startItemWidth, endItemWidth, indicator, indicatorColors, onPageChange, pageContent)`：`count` 是真实页数（不含补页），`pageContent` 与 `onPageChange` 拿到的都是从 0 开始的真实下标。
- `public @Composable fun rememberBannerState(count, startIndex = 0, infiniteLoop = CouiBannerDefaults.infiniteLoop): BannerState`：`count` 或 `infiniteLoop` 变化时重建，回到 `startIndex`。
- `public @Stable class BannerState`：`currentIndex`（真实下标）、`suspend animateScrollToIndex(index, animationSpec = tween(950, easing))`、`suspend scrollToIndex(index)`。
- `public object CouiBannerDefaults`：`pageMargin = 24.dp`、`indicatorBottomMargin = 10.dp`、`loopDurationMillis = 5000`、`scrollDurationMillis = 950`、`easing = CubicBezierEasing(0.2f, 0f, 0.1f, 1f)`、`startPosition = 1`、`infiniteLoop = true`、`indicatorColors = CouiPageIndicatorColors(#4DFFFFFF, #E6FFFFFF)`。

## Invariants
- pager 的页数只有两种：循环时 `count + 2`（首尾各补一页），否则 `count`；真实页从 pager 下标 `1` 开始。
- 对外暴露的下标永远是真实下标，`realIndex` 在换算时夹到 `0..count - 1`。
- 露出宽度 = `contentPadding(start = startItemWidth + pageMargin, end = endItemWidth + pageMargin)`，页间间隙 = `pageSpacing = pageMargin`。
- `count <= 0` 时 `Banner` 直接返回，不占位；`count <= 1` 时不补页、不自动轮播。
- 自动轮播的等待时长是 `loopDurationMillis + scrollDurationMillis`，滚动动画用 `easing` 与 `scrollDurationMillis`。

## Side Effects
- 在 `LaunchedEffect` 里收 `snapshotFlow { pagerState.settledPage }`：停在补页 0 或 `count + 1` 时无动画跳回真实页，只读不改宿主状态。
- 自动轮播是一个 `while (true) { delay(...); if (!isScrollInProgress) animateScrollToPage(...) }` 的协程，拖动、`autoLoop = false`、非循环或 `count <= 1` 时整段不启动，`currentIndex` 变化会让它按新参数重启。
- `onPageChange` 在 `currentIndex` 变化时回调（`LaunchedEffect(current)`），首次组合也会回调一次初始页。
- 点圆点调用 `state.animateScrollToIndex`，在一个 `rememberCoroutineScope` 里起协程；不修改宿主传入的 `state`。

## 验证
- `references/apk/coui-src/com/coui/appcompat/banner/COUIBanner.java`：`g()` 里的 `f(left + margin, right + margin)`、`f()` 里的 `setPadding` + `setClipToPadding(false)`、`h()` 里的 `postDelayed(loopDuration × 1000 + q)`、`dispatchTouchEvent` 的按下取消 / 抬手重启、`setInfiniteLoop` 顺带把 `startPosition` 归 0。
- `references/apk/coui-src/com/coui/appcompat/banner/COUIBannerOnPageChangeCallback.java`：停稳后从补页跳回真实页。
- `references/apk/coui-src/com/coui/appcompat/banner/b.java`（JADX 名，原 `COUIBannerUtil`）：`f6247a = dpToPx(24)`，即 `couiPageMargin` 的缺省值。
- `references/apk/phonemanager/decoded/res/values/colors.xml`：`coui_page_indicator_dot_color_in_dark_bg` #4dffffff、`coui_color_label_primary_dark` #e6ffffff。
- `references/apk/phonemanager/decoded/res/layout/coui_banner_content_layout.xml`：指示器 `layout_marginBottom` 10dp 与两个圆点色。
- `gradlew.bat :coui:compileDebugKotlin` 与 `:demo:compileDebugKotlin` 均通过。

## 未做
- `type = 1`（`screenWidthDp >= 600` 时的 `COUIBannerRecyclerView` 双列列表）。
- 外部注入 `PageTransformer`、`CouiLoopShowTimeAnimator` 一类的整体位移动画。
- 运行期行为只经过编译验证，没有在设备上观察过自动轮播与补页跳转的实际时间点。

# PullToRefresh

## Overview
下拉刷新容器包。`PullToRefresh` 通过嵌套滚动把顶部过滚转换为阻尼位移，越过阈值后锁定并调用宿主 `onRefresh`；内置 OEM 风格环形刷新头，刷新状态由宿主持有。

## Interface
- `public object CouiPullToRefreshDefaults`：头部/指示器尺寸、`threshold`、透明度与锁定/回弹/标签动画时长 [PullToRefresh.kt:CouiPullToRefreshDefaults]
- `public @Composable fun CouiPullToRefreshDefaults.Header(progress, refreshing, dark, modifier)`：16dp 环形进度 + 固定 0.26 透明度标签 [PullToRefresh.kt:CouiPullToRefreshDefaults.Header]
- `public @Composable fun PullToRefresh(modifier, refreshing, onRefresh, dark, hapticFeedbackEnabled, header, content)` [PullToRefresh.kt:PullToRefresh]

## Invariants
- `refreshing` 由宿主持有；释放时仅当 `offset >= thresholdPx` 锁定，立即置 `Refreshing`，经 `lockDurationMillis` 动画到阈值后调用 `onTrigger` [PullToRefresh.kt:PullToRefreshState.lockIn]
- `onRefreshingChanged(true)` 仅锁定不触发 `onRefresh`；`onRefreshingChanged(false)` 仅在状态为 `Refreshing` 时按 `settleDurationMillis` 回弹 [PullToRefresh.kt:PullToRefreshState.onRefreshingChanged]
- 只消费 `NestedScrollSource.UserInput`：预滚动处理向上收缩（`available.y < 0`），后滚动将剩余下拉转换并以 `overscrollDistance` 封顶 [PullToRefresh.kt:NestedScrollConnection]
- `onPreFling` 在 `Idle`/`Refreshing` 返回 `Velocity.Zero`；其余状态调用 `release()` 并仅在 `offset > 0` 时返回完整速度 [PullToRefresh.kt:NestedScrollConnection.onPreFling]
- 布局尺寸不变：内容通过 `graphicsLayer` 平移，头部覆盖在 `headerHeight`，`offset <= 0` 时透明 [PullToRefresh.kt:PullToRefresh]
- 前置条件：`content` 必须是可滚动容器，且不能在外层再包一层滚动容器，否则外层先消费下拉 [PullToRefresh.kt:PullToRefresh]

## Side Effects
- 通过 `Modifier.nestedScroll` 在根 `Box` 安装嵌套滚动连接 [PullToRefresh.kt:PullToRefresh]
- 触觉反馈：越过阈值时 `LocalView.performCouiHapticFeedback(CouiHapticFeedback.ScrollEdge)`，受 `hapticFeedbackEnabled` 门控并在 `SideEffect` 重装 [PullToRefresh.kt:PullToRefresh]
- 平台 API：每个视图读取一次 `LocalView.resources.displayMetrics.heightPixels` 作为 `overscrollDistance` [PullToRefresh.kt:PullToRefresh]
- 协程动画：`rememberCoroutineScope` 执行锁定/回弹 tween，以及 `withFrameNanos` + `System.nanoTime` 的弹簧回弹循环 [PullToRefresh.kt:PullToRefreshState.springBack]
- 资源加载：`stringResource(coui_pull_refresh/coui_pull_refreshing)`，延迟 100ms 后 200ms 淡入替换 [PullToRefresh.kt:PullRefreshLabel]
- 绘制：`CircularProgress` 环形进度（刷新时不确定态），通过 `graphicsLayer` 旋转与透明度呈现 [PullToRefresh.kt:CouiPullToRefreshDefaults.Header]

## Verification
- Run: `./gradlew :coui:lintDebug`

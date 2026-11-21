# ScrollBar

## Overview
竖向滚动条包。`ScrollBar` 在宿主 `ScrollState` 之上绘制并拖动一个延迟淡出的拇指，不持有业务滚动状态，也不取消滚动容器已有的 fling。

## Interface
- `public @Immutable data class CouiScrollBarColors(thumb)` [ScrollBar.kt:CouiScrollBarColors]
- `public object CouiScrollBarDefaults`：`width = 14.dp`、`minThumbHeight = 48.dp`、上下边距、`inset`/`pressedInset`、`fadeDelayMillis = 2000L`、`initialFadeDelayMillis = 1200L`、`fadeDurationMillis = 250L` 与 `colors(dark)` [ScrollBar.kt:CouiScrollBarDefaults]
- `public @Composable fun ScrollBar(modifier, state, dark, colors, fadeDelayMillis)` [ScrollBar.kt:ScrollBar]

## Invariants
- 拇指几何 `couiScrollBarThumb`：`maxValue`/`viewport` 非正或可滚范围不大于 0 时返回 `null`；高度取 `max(minThumb, viewport / maxValue * height)` 且不超过高度 [ScrollBar.kt:couiScrollBarThumb]
- 可见性状态机：`(value, isScrollInProgress, dragging)` 变化即 `snapTo(1f)`；首次挂载等待 1200ms，之后等待传入的 `fadeDelayMillis`，再以 250ms tween 淡出；`dragging` 期间保持可见 [ScrollBar.kt:ScrollBar]
- 仅当 `alpha > 0` 且按下点在拇指矩形内时才接管并 `consume`；未命中不消费，留给下层滚动容器 [ScrollBar.kt:ScrollBar]
- 拖动只写 `dragTargetTop`；另一协程把目标换算为比例并通过 `state.scrollBy(delta)` 提交，结束后清空目标 [ScrollBar.kt:ScrollBar]
- 拇指上边加 `marginTopPx`、下边减 `marginBottomPx`；常态内缩 `insetPx`、按下 `pressedInsetPx`，圆角取可见宽高较小值的一半 [ScrollBar.kt:ScrollBar]
- 不取消滚动容器已有的 fling，位移在滚动事务之后应用 [ScrollBar.kt:ScrollBar]

## Side Effects
- 指针输入：`pointerInput` 的 `awaitEachGesture` 处理按下与拖动，并按命中条件消费事件 [ScrollBar.kt:ScrollBar]
- 协程：两个 `LaunchedEffect(state)` 分别驱动延迟淡出与 `scrollBy` 提交 [ScrollBar.kt:ScrollBar]
- 绘制：`Canvas` 上 `drawRoundRect`，透明度为 `colors.thumb.alpha * alpha` [ScrollBar.kt:ScrollBar]
- 读取 `LocalDensity` 与 `LocalLayoutDirection` 决定像素尺寸与左右贴边 [ScrollBar.kt:ScrollBar]
- 读取 `CouiTheme.isDark` 生成日夜拇指色 [ScrollBar.kt:CouiScrollBarDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`

# Scroll

## Overview
滚动越界与滚动物理包。`CouiOverscrollEffect` 复刻 Settings `COUIRecyclerView` 的纵向越界阻尼与回弹，`Modifier.couiVerticalScroll` 提供二级页滚动入口；`CouiScrollPhysics` 实现 SpringOverScroller 轴，`rememberCouiScrollFlingBehavior` 让惯性走同一物理模型。

## Interface
- `public class CouiOverscrollEffect`（internal 构造，`OverscrollEffect`）：`isInProgress`、`node`、`applyToScroll`、`applyToFling`，内部 `absorbFling`/`dispose` [CouiOverscrollEffect.kt:CouiOverscrollEffect]
- `public @Composable fun rememberCouiOverscrollEffect(hapticFeedbackEnabled = true): CouiOverscrollEffect` [CouiOverscrollEffect.kt:rememberCouiOverscrollEffect]
- `public @Composable fun Modifier.couiVerticalScroll(state, enabled, overscrollEffect, interactionSource): Modifier` [CouiOverscrollEffect.kt:couiVerticalScroll]
- `internal @Composable fun rememberCouiScrollFlingBehavior(overscrollEffect): FlingBehavior` [CouiScrollFlingBehavior.kt:rememberCouiScrollFlingBehavior]
- `internal class CouiScrollPhysics(animationLevel, springBackTensionMultiple, scrollView)`：`running`/`current`/`currentVelocity`、`stop`/`fling`/`returnToBounds`/`update` [CouiScrollPhysics.kt:CouiScrollPhysics]

## Invariants
- 每个滚动容器持有独立 `CouiOverscrollEffect`，不得多节点共用；只处理纵向越界，不绘制平台 glow / Compose stretch [CouiOverscrollEffect.kt:CouiOverscrollEffect]
- `applyToScroll` 的 `UserInput` 分支先按整数阻尼恢复到边界，再交还剩余位移，不跨过 0 吞掉正常滚动 [CouiOverscrollEffect.kt:applyToScroll]
- 非用户输入的边界 fling 以 `preserveVelocity = true` 进入 `returnToBounds`，映射为越界位移并只触发一次 `ScrollEdge` 触觉 [CouiOverscrollEffect.kt:applyToScroll]
- `applyToFling` 仅对 `|velocity.y| > 6000` 的相向释放保留速度，其余回弹；`finally` 中旧协程不得重启新手势已接管的回弹 [CouiOverscrollEffect.kt:applyToFling]
- 越界惯性显示映射：`limit = screenHeight * 0.3731f`，使用双曲函数压缩位移 [CouiOverscrollEffect.kt:overFlingTranslation]
- `EdgeNode` 以独立图层平移子内容；`mappingScreenHeight` 仅在重新测量尺寸变化时更新 [CouiOverscrollEffect.kt:EdgeNode]
- 触摸在 `PointerEventPass.Initial` 观察；UP/CANCEL 时排除长按与位移超过 10px 的点击，再回弹并触发触觉 [CouiOverscrollEffect.kt:finishTouch]
- `couiVerticalScroll` 让空页/短页同样可回弹：输入状态强制 `canScrollForward/Backward = true`，内层 `verticalScroll` 禁用输入、只保留焦点与视口自动滚动 [CouiOverscrollEffect.kt:couiVerticalScroll]
- `CouiScrollPhysics` 的 fling 为两遍预测（积分 + 过阻尼插值），回弹用 RK4；产生非有限值触发 `check` 异常 [CouiScrollPhysics.kt:CouiScrollPhysics]
- `rememberCouiScrollFlingBehavior` 要求刷新率有限且为正；触边帧把速度交给 `absorbFling`，被接管时返回 0 [CouiScrollFlingBehavior.kt:rememberCouiScrollFlingBehavior]

## Side Effects
- 帧时钟协程：`withFrameNanos` + `System.nanoTime` 推进积分与回弹 [CouiOverscrollEffect.kt:rebound]
- 指针/布局：`PointerInputModifierNode` 与 `placeWithLayer` 平移 [CouiOverscrollEffect.kt:EdgeNode]
- 触觉反馈：`View.performCouiHapticFeedback(CouiHapticFeedback.ScrollEdge, ...)` [CouiOverscrollEffect.kt:finishTouch]
- 平台读取：`View.display.refreshRate` 与 `resources.displayMetrics.heightPixels` [CouiOverscrollEffect.kt:rememberCouiOverscrollEffect]
- 滚动修饰符：`overscroll`/`scrollable`/`verticalScroll` 与 `MutableInteractionSource` [CouiOverscrollEffect.kt:couiVerticalScroll]
- 数学运算：`sinh`/`cosh`/`exp` 过阻尼插值与 RK4 积分 [CouiScrollPhysics.kt:FlingInterpolator]

## Verification
- Run: `./gradlew :coui:lintDebug`

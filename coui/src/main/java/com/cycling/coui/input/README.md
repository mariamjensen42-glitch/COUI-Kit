# Input

## Overview
输入与交互基础工具包。提供 COUI 触觉反馈码、动画中按钮的窗口坐标点击判定、输入重定位边界，以及 Compose 合成 CANCEL 的识别；供其他组件包复用。

## Interface
- `public enum class CouiHapticFeedback(val code: Int)`：`Switch(302)`、`ScrollEdge(307)`、`GranularShortVibrateSync(308)` [CouiHaptics.kt:CouiHapticFeedback]
- `public fun View.performCouiHapticFeedback(feedback, enabled = true): Boolean` [CouiHaptics.kt:performCouiHapticFeedback]
- `internal val CouiSearchRelocationBoundary: Modifier`：输入重定位边界修饰符 [CouiSearchRelocationBoundary.kt:CouiSearchRelocationBoundary]
- `internal fun Modifier.couiMovingActionClick(source, enabled, animating, onClick)` [CouiMovingActionClick.kt:couiMovingActionClick]
- `internal fun PointerEvent.isCouiTouchCancelled(): Boolean` [PointerEvents.kt:isCouiTouchCancelled]

## Invariants
- 触觉反馈需 `enabled && isAttachedToWindow`，由 ROM 决定是否支持该反馈码；不支持返回 `false` [CouiHaptics.kt:performCouiHapticFeedback]
- `couiMovingActionClick` 仅在 `animating` 时接管手势，按窗口坐标判定；超出 `touchSlop`、多指、已消费或取消即中止 [CouiMovingActionClick.kt:couiMovingActionClick]
- 手势中止时必须补发 `PressInteraction.Cancel`，成功抬手才补发 `Release` [CouiMovingActionClick.kt:couiMovingActionClick]
- `SearchRelocationNode` 不追逐展开时越界的输入坐标，只将祖先滚动到固定根边界 [CouiSearchRelocationBoundary.kt:SearchRelocationNode]
- `isCouiTouchCancelled` 以 `motionEvent == null` 且原地转已消费抬手的组合识别 Compose 合成 CANCEL [PointerEvents.kt:isCouiTouchCancelled]

## Side Effects
- 平台 API：`View.performHapticFeedback(code)` [CouiHaptics.kt:performCouiHapticFeedback]
- 指针输入：`PointerEventPass.Initial` 手势循环、`PressInteraction` 事件发射与 `clickable` 合并 [CouiMovingActionClick.kt:couiMovingActionClick]
- 布局/重定位：实现 `BringIntoViewModifierNode` 的 `bringIntoView` 挂起回调 [CouiSearchRelocationBoundary.kt:SearchRelocationNode]

## Verification
- Run: `./gradlew :coui:lintDebug`

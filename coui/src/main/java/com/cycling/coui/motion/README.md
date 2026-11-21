# Motion

## Overview
手势与动画基础算法包。`CouiSpring` 封装 AndroidX 弹簧通道，`CouiTouchMotion`/`CouiPressMotion` 提供触摸形变与按压缩放，`ProgressiveDragState` 编排渐进式拖动弹簧；参数类型与默认值对齐原厂按压、渐进拖动与橡皮筋语义，供侧滑、底栏、FAB、搜索展开等复用。

## Interface
- `public @Immutable data class CouiPressFeedbackParams`：按下/抬手 response、bounce 与最小可见变化量 [CouiPressMotion.kt:CouiPressFeedbackParams]
- `public object CouiPressFeedbackDefaults`：`pressParams` 与 `oliveLink` 两套原厂参数 [CouiPressMotion.kt:CouiPressFeedbackDefaults]
- `public class CouiPressMotion(scope, density, params)`：`scale`、`press(size)`、`release()` [CouiPressMotion.kt:CouiPressMotion]
- `public @Composable fun rememberCouiPressMotion(params)` 与 `public fun Modifier.couiPressMotion(press, enabled, cancelOnConsumedMove)` [CouiTouchInput.kt:rememberCouiPressMotion]
- `public class CouiTouchMotion(scope, density, deformationEnabled)`：`scaleX/scaleY/translationX/translationY`、`begin/move/release/switchPointer/cancel/reset` [CouiTouchMotion.kt:CouiTouchMotion]
- `public @Composable fun rememberCouiTouchMotion(expanded)` 与 `public fun Modifier.couiTouchMotion(motion, expanded, targetSize)` [CouiTouchInput.kt:rememberCouiTouchMotion]
- `internal fun Modifier.couiTouchMotion(..., cancelOnConsumedMove, consumeDrag)`：可取消/可接管的内部重载 [CouiTouchInput.kt:couiTouchMotion]
- `public enum class CouiProgressivePhase { Idle, Drag, Release, RubberbandDrag, RubberbandRelease }` [CouiProgressiveDefaults.kt:CouiProgressivePhase]
- `public @Immutable data class CouiProgressiveSpring(response, bounce, blendMillis)` [CouiProgressiveDefaults.kt:CouiProgressiveSpring]
- `public @Immutable data class CouiProgressiveConfig`：`dragSpring(distance, span)` 与 `releaseSpring(distance, span, velocity)` [CouiProgressiveDefaults.kt:CouiProgressiveConfig]
- `public @Immutable data class CouiRubberbandConfig(curveRatio, maxDistancePx)` [CouiProgressiveDefaults.kt:CouiRubberbandConfig]
- `public object CouiProgressiveDefaults`：`config`、`rubberband`、`minimumVisibleChange = 0.5f`、`rubberbandOffset(...)` [CouiProgressiveDefaults.kt:CouiProgressiveDefaults]
- `public @Stable class ProgressiveDragState`（internal 构造）：`offsetX`/`rubberbandOffset`/`releaseVelocity`/`phase`/`progress`/`isRunning`、`begin/dragTo/dragRubberband/releaseTo/releaseRubberband/cancel/translation` [ProgressiveDragState.kt:ProgressiveDragState]
- `public @Composable fun rememberProgressiveDragState(config)`、`public fun Modifier.couiProgressiveDrag(state, enabled, triggerIndex, span, onRelease)` [ProgressiveDragState.kt:couiProgressiveDrag]
- `public @Composable fun rememberCouiMetaballFollowingTranslation(distancePx, subjectSizePx, followingDistancePx, reboundSpec)` [CouiMetaballFollowing.kt:rememberCouiMetaballFollowingTranslation]
- `internal class CouiSpring`、`internal class CouiSearchMotion`、`internal class CouiVelocityTracker` [CouiSpring.kt:CouiSpring]

## Invariants
- `CouiPressFeedbackParams` 的 `init` 要求 response 有限正值、bounce 落在 `[0, 1)`、最小可见变化量为正 [CouiPressMotion.kt:CouiPressFeedbackParams]
- `CouiPressMotion`/`CouiTouchMotion` 约定主线程创建与调用；`dispose` 后停止弹簧且不再接收输入 [CouiPressMotion.kt:CouiPressMotion]
- 按压目标缩放按面积 20dp²–150dp² 映射到 0.85–0.97，宽高比 > 2 时取面积/比例均值，最终夹到 `0.4..1` [CouiPressMotion.kt:press]
- `CouiSpring` 运行中重定向保留速度，`cancelComplete` 完整重置速度与动画内部状态；边界交给 AndroidX 积分器 [CouiSpring.kt:CouiSpring]
- `CouiVelocityTracker.releaseVelocity` 使用像素/毫秒并乘 0.3；UP 不作为重复 MOVE 样本 [CouiVelocityTracker.kt:CouiVelocityTracker]
- `CouiProgressiveConfig` 的单行响应步长在超过 `max` 时按 `span` 均分；抬手速度按 12000px/s 归一，缩短 response 0.1 并增加 bounce 0.25 [CouiProgressiveDefaults.kt:CouiProgressiveConfig]
- `rubberbandOffset`：`overDrag <= 0` 返回 0，`maxDistancePx` 非正时取视口一半 [CouiProgressiveDefaults.kt:rubberbandOffset]
- `ProgressiveDragState.dragTo` 在非拖动阶段会先 `begin` 新会话；`releaseTo` 在 `Idle` 直接写目标并回调 `onEnd(true)` [ProgressiveDragState.kt:releaseTo]
- `translation(rowIndex) = offsetX + deviation`，残差只按与 `triggerIndex` 的行距索引 [ProgressiveDragState.kt:translation]
- `rememberProgressiveDragState` 以 `config` 为 key 重建，避免旧弹簧继续跑旧参数 [ProgressiveDragState.kt:rememberProgressiveDragState]
- `couiProgressiveDrag` 在 `enabled = false` 时原样返回修饰符，拖动时消费位移并按速度回调 [ProgressiveDragState.kt:couiProgressiveDrag]
- 融球跟随要求尺寸/距离有限非负且总和为正；距离归零复位，越过跟随终点或达到负向距离后回弹 [CouiMetaballFollowing.kt:rememberCouiMetaballFollowingTranslation]
- `CouiSearchMotion` 运行中只改目标、不取消或清零速度；`finishCollapse` 仅在未展开且弹簧未运行时重建弹簧 [CouiSearchMotion.kt:CouiSearchMotion]

## Side Effects
- AndroidX 动态动画：`SpringAnimation`/`FloatValueHolder`/`SpringForce` 帧驱动 [CouiSpring.kt:CouiSpring]
- 指针输入：`PointerEventPass.Initial`/`Final` 事件循环、`VelocityTracker`、位移消费 [CouiTouchInput.kt:couiTouchMotion]
- 协程与状态：`rememberCoroutineScope`、`Animatable`、`snapshotFlow` 与 `DisposableEffect` 生命周期 [CouiMetaballFollowing.kt:rememberCouiMetaballFollowingTranslation]
- 平台 API：`PathInterpolator`、`MotionEvent`、`VelocityTracker.obtain/recycle` [CouiVelocityTracker.kt:CouiVelocityTracker]
- 可观察状态：`mutableFloatStateOf`/`mutableStateMapOf`/`mutableStateOf` 暴露为 `State` [CouiTouchMotion.kt:CouiTouchMotion]

## Verification
- Run: `./gradlew :coui:lintDebug`

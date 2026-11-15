package com.cycling.coui.motion

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

/**
 * 渐进式手势动画控制器，对齐原厂 `HorizontalProgressiveAnimHelper` 的手势会话与弹簧编排。
 *
 * 父容器位移（[offsetX]）由宿主随手势写入；每个参与行按与触发行的行距拿到一条独立弹簧，
 * 位移恒为「父位移 + 弹簧残差」。拖动时残差向零收敛形成跟随梯度，抬手后残差与父位移一起回落。
 * 越过边界时用 [dragRubberband] / [releaseRubberband] 换成橡皮筋位移，位移由
 * [CouiProgressiveDefaults.rubberbandOffset] 换算。
 *
 * 控制器只持有运动状态，不接管手势，也不绘制；手势入口见 [couiProgressiveDrag]。
 */
@Stable
class ProgressiveDragState internal constructor(
    private val config: CouiProgressiveConfig = CouiProgressiveDefaults.config,
) {
    private val offsetState = mutableFloatStateOf(0f)
    private val rubberbandState = mutableFloatStateOf(0f)
    private val velocityState = mutableFloatStateOf(0f)
    private var phaseState by mutableStateOf(CouiProgressivePhase.Idle)
    private val deviations = mutableStateMapOf<Int, Float>()
    private val springs = mutableMapOf<Int, CouiSpring>()
    private var offsetSpring: CouiSpring? = null
    private var triggerIndex = 0
    private var span = 0
    private var windowStart = 0f
    private var windowEnd = 0f

    /** 父容器当前位移。 */
    val offsetX: Float get() = offsetState.floatValue

    /** 当前橡皮筋位移，未越界时为 0。 */
    val rubberbandOffset: Float get() = rubberbandState.floatValue

    /** 最近一次抬手速度的绝对值。 */
    val releaseVelocity: Float get() = velocityState.floatValue

    var phase: CouiProgressivePhase
        get() = phaseState
        private set(value) {
            phaseState = value
        }

    /** 父位移在 [setProgressWindow] 给出的区间内的进度。 */
    val progress: Float
        get() {
            if (windowEnd == windowStart) return 0f
            return ((offsetX - windowStart) / (windowEnd - windowStart)).coerceIn(0f, 1f)
        }

    /** 会话是否还在动，包含拖动与回落。 */
    val isRunning: Boolean
        get() = phase != CouiProgressivePhase.Idle

    /** 设置 [progress] 的换算区间，对应原厂 `ProgressiveProgressDriver.configure`。 */
    fun setProgressWindow(fromOffsetX: Float, toOffsetX: Float) {
        windowStart = fromOffsetX
        windowEnd = toOffsetX
    }

    /**
     * 按下时开一次会话：[offsetX] 是按下瞬间的父位移，[triggerIndex] 是被按中的行，
     * [span] 是参与动画的最大行距，决定弹簧步长的封顶方式。
     */
    fun begin(offsetX: Float, triggerIndex: Int = 0, span: Int = 0) {
        this.triggerIndex = triggerIndex
        this.span = span.coerceAtLeast(0)
        velocityState.floatValue = 0f
        rubberbandState.floatValue = 0f
        offsetState.floatValue = offsetX
        offsetSpring?.cancelComplete()
        springs.values.forEach { it.snapTo(0f) }
        deviations.clear()
        phase = CouiProgressivePhase.Drag
    }

    /** 拖动到新的父位移，各行的弹簧继续向零残差收敛。 */
    fun dragTo(offsetX: Float) {
        if (phase != CouiProgressivePhase.Drag && phase != CouiProgressivePhase.RubberbandDrag) {
            begin(offsetX, triggerIndex, span)
        }
        phase = CouiProgressivePhase.Drag
        rubberbandState.floatValue = 0f
        offsetState.floatValue = offsetX
        animateDeviations(target = 0f, releaseVelocityPxPerSecond = 0f)
    }

    /** 拖动越界后的阻尼位移；[rubberbandOffset] 由 [CouiProgressiveDefaults.rubberbandOffset] 算出。 */
    fun dragRubberband(rubberbandOffset: Float, offsetX: Float) {
        if (phase != CouiProgressivePhase.RubberbandDrag) {
            phase = CouiProgressivePhase.RubberbandDrag
        }
        rubberbandState.floatValue = rubberbandOffset
        offsetState.floatValue = offsetX
        animateDeviations(target = rubberbandOffset, releaseVelocityPxPerSecond = 0f)
    }

    /**
     * 抬手：父位移回落到 [targetOffsetX]，残差同步归零。
     *
     * [onEnd] 在父位移停稳后回调一次，取消时回调 `false`。
     */
    fun releaseTo(
        targetOffsetX: Float = 0f,
        velocityPxPerSecond: Float = 0f,
        onEnd: ((Boolean) -> Unit)? = null,
    ) {
        if (phase == CouiProgressivePhase.Idle) {
            offsetState.floatValue = targetOffsetX
            onEnd?.invoke(true)
            return
        }
        velocityState.floatValue = abs(velocityPxPerSecond)
        phase = CouiProgressivePhase.Release
        rubberbandState.floatValue = 0f
        animateDeviations(target = 0f, releaseVelocityPxPerSecond = velocityPxPerSecond)
        val offsetParams = config.releaseSpring(distance = 0, span = span, velocityPxPerSecond = velocityPxPerSecond)
        ensureOffsetSpring().animateTo(
            target = targetOffsetX,
            response = offsetParams.response,
            bounce = offsetParams.bounce,
            minimumChange = CouiProgressiveDefaults.minimumVisibleChange,
            velocity = velocityPxPerSecond,
            onEnd = {
                phase = CouiProgressivePhase.Idle
                springs.values.forEach { it.snapTo(0f) }
                deviations.clear()
                onEnd?.invoke(true)
            },
        )
    }

    /** 橡皮筋回弹：父位移保持在当前值，残差回到零。 */
    fun releaseRubberband(onEnd: ((Boolean) -> Unit)? = null) {
        if (!phase.isRubberband) {
            onEnd?.invoke(false)
            return
        }
        phase = CouiProgressivePhase.RubberbandRelease
        animateDeviations(target = 0f, releaseVelocityPxPerSecond = velocityState.floatValue)
        rubberbandState.floatValue = 0f
        ensureOffsetSpring().snapTo(offsetX)
        phase = CouiProgressivePhase.Idle
        onEnd?.invoke(true)
    }

    /** 取消当前会话，位移与残差直接归位。 */
    fun cancel() {
        offsetSpring?.cancelComplete()
        springs.values.forEach { it.cancelComplete() }
        deviations.clear()
        rubberbandState.floatValue = 0f
        velocityState.floatValue = 0f
        phase = CouiProgressivePhase.Idle
    }

    /**
     * 第 [rowIndex] 行当前的位移：与触发行同行为父位移本身，越远跟随越慢。
     *
     * 只在手势会话期间对差值做插值，会话结束后退化为父位移，宿主可直接把它写进 `translationX`。
     */
    fun translation(rowIndex: Int): Float = offsetX + deviation(rowIndex)

    private fun deviation(rowIndex: Int): Float = deviations[distanceOf(rowIndex)] ?: 0f

    private fun distanceOf(rowIndex: Int): Int = abs(rowIndex - triggerIndex)

    private fun animateDeviations(target: Float, releaseVelocityPxPerSecond: Float) {
        val distances = 0..span.coerceAtLeast(0)
        distances.forEach { distance ->
            val params = if (phase.isRelease) {
                config.releaseSpring(distance, span, releaseVelocityPxPerSecond)
            } else {
                config.dragSpring(distance, span)
            }
            springFor(distance).animateTo(
                target = target,
                response = params.response,
                bounce = params.bounce,
                minimumChange = CouiProgressiveDefaults.minimumVisibleChange,
            )
        }
    }

    private fun springFor(distance: Int): CouiSpring = springs.getOrPut(distance) {
        lateinit var spring: CouiSpring
        spring = CouiSpring(
            initial = 0f,
            minimumChange = CouiProgressiveDefaults.minimumVisibleChange,
        ) { deviations[distance] = spring.value }
        spring
    }

    private fun ensureOffsetSpring(): CouiSpring = offsetSpring ?: run {
        lateinit var spring: CouiSpring
        spring = CouiSpring(
            initial = offsetX,
            minimumChange = CouiProgressiveDefaults.minimumVisibleChange,
        ) { offsetState.floatValue = spring.value }
        offsetSpring = spring
        spring
    }
}

/** 记住一个 [ProgressiveDragState]；配置变化时重建，避免旧弹簧继续跑旧参数。 */
@Composable
fun rememberProgressiveDragState(
    config: CouiProgressiveConfig = CouiProgressiveDefaults.config,
): ProgressiveDragState = remember(config) { ProgressiveDragState(config) }

/**
 * 把水平拖动喂给 [state]：按下开会话，位移写父位移，抬手按 [onRelease] 决定目标。
 *
 * [onRelease] 为 null 时回落到父位移 0；需要翻页或吸附时用它算出目标位移再调
 * [ProgressiveDragState.releaseTo]。[triggerIndex] / [span] 的含义与
 * [ProgressiveDragState.begin] 相同。
 */
fun Modifier.couiProgressiveDrag(
    state: ProgressiveDragState,
    enabled: Boolean = true,
    triggerIndex: Int = 0,
    span: Int = 0,
    onRelease: ((velocityPxPerSecond: Float) -> Unit)? = null,
): Modifier = if (!enabled) this else pointerInput(state, enabled, triggerIndex, span, onRelease) {
    val tracker = VelocityTracker()
    detectHorizontalDragGestures(
        onDragStart = {
            tracker.resetTracking()
            state.begin(state.offsetX, triggerIndex, span)
        },
        onDragCancel = { state.cancel() },
        onDragEnd = {
            val velocity = tracker.calculateVelocity().x
            if (onRelease != null) onRelease(velocity) else state.releaseTo(velocityPxPerSecond = velocity)
        },
        onHorizontalDrag = { change, dragAmount ->
            tracker.addPosition(change.uptimeMillis, change.position)
            change.consume()
            state.dragTo(state.offsetX + dragAmount)
        },
    )
}

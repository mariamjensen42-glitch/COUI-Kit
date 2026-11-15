package com.itos.coui.motion

import android.view.animation.PathInterpolator
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * 按压反馈的弹簧参数，对应原厂 `IPressParams` / `PressParams` 的五个字段：
 * 按下与抬手的 response、bounce，以及按压形变的最小可见变化量。
 * response 越大越慢，bounce 越大越弹；`CouiSpring` 的 stiffness 由 `(2π / response)²` 得到，
 * 与原厂 `COUISpringAnimation` 的 response 语义一致。
 */
@Immutable
data class CouiPressFeedbackParams(
    val pressResponse: Float,
    val pressBounce: Float,
    val handUpResponse: Float,
    val handUpBounce: Float,
    val minimumVisibleChange: Float,
) {
    init {
        require(pressResponse.isFinite() && pressResponse > 0f) { "按下 response 必须是有限正值" }
        require(handUpResponse.isFinite() && handUpResponse > 0f) { "抬手 response 必须是有限正值" }
        require(pressBounce.isFinite() && pressBounce in 0f..<1f) { "按下 bounce 必须在 [0, 1) 内" }
        require(handUpBounce.isFinite() && handUpBounce in 0f..<1f) { "抬手 bounce 必须在 [0, 1) 内" }
        require(minimumVisibleChange.isFinite() && minimumVisibleChange > 0f) { "最小可见变化量必须是有限正值" }
    }
}

/** 两套原厂按压参数；组件默认沿用当前生效的 olivelink 弹簧对。 */
object CouiPressFeedbackDefaults {
    /**
     * 原厂 `com.coui.appcompat.pressfeedback.PressParams` 的默认值：
     * 按下 bounce 0 / response 0.15，抬手 bounce 0.5 / response 0.4，最小可见变化 0.001。
     */
    val pressParams = CouiPressFeedbackParams(pressResponse = .15f, pressBounce = 0f,
        handUpResponse = .4f, handUpBounce = .5f, minimumVisibleChange = .001f)

    /**
     * 原厂 `touchanimation.param.olivelink.PressParams` 的 jelly 弹簧对：
     * 按下 bounce 0 / response 0.25，抬手 bounce 0.6 / response 0.5。
     * 这是现有组件（按钮、FAB、开关）实际使用的自适应按压参数，作为默认值保持观感不变。
     */
    val oliveLink = CouiPressFeedbackParams(pressResponse = .25f, pressBounce = 0f,
        handUpResponse = .5f, handUpBounce = .6f, minimumVisibleChange = .001f)
}

/**
 * OlivelinkDefault 普通按压，与同一触摸组合内的形变缩放增量相加。
 * 目标缩放由尺寸面积与宽高比决定（`PressCalc`），弹簧用 [params]；
 * 原厂 candy 分支的按下 response 0.3 与抬手最小变化 0.0001 并入同一组参数。
 * 在主线程创建和调用；scope 使用主线程上下文，取消后停止弹簧且实例不再接收输入。
 * size 使用像素；Compose 中优先使用 rememberCouiPressMotion。
 */
class CouiPressMotion(scope: CoroutineScope, private val density: Float,
    private val params: CouiPressFeedbackParams = CouiPressFeedbackDefaults.oliveLink) {
    private val spring = CouiSpring(1f, 0.001f)
    private var disposed = false
    private var candy = false
    val scale: State<Float> = spring.state
    private val lifetime = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        try {
            awaitCancellation()
        } finally {
            disposed = true
            spring.cancel()
        }
    }

    fun press(size: IntSize) {
        if (disposed || size.width <= 0 || size.height <= 0) return
        val width = size.width.toFloat()
        val height = size.height.toFloat()
        val minArea = 20f * density * 20f * density
        val maxArea = 150f * density * 150f * density
        val areaProgress = ((width * height - minArea) / (maxArea - minArea)).coerceIn(0f, 1f)
        val areaScale = 0.85f + 0.12f * Interpolator.getInterpolation(areaProgress)
        val aspect = max(width, height) / min(width, height)
        candy = aspect > 2f
        val aspectScale = 0.85f + 0.15f * Interpolator.getInterpolation(((aspect - 2f) / 13f).coerceIn(0f, 1f))
        val target = (if (candy) (areaScale + aspectScale) * 0.5f else areaScale).coerceIn(0.4f, 1f)
        spring.animateTo(target, params.pressResponse, params.pressBounce,
            minimumChange = params.minimumVisibleChange, velocity = 0f)
    }

    fun release() {
        if (disposed) return
        spring.animateTo(1f, params.handUpResponse, params.handUpBounce,
            minimumChange = params.minimumVisibleChange)
    }

    internal fun dispose() {
        disposed = true
        spring.cancel()
        lifetime.cancel()
    }

    private companion object {
        val Interpolator = PathInterpolator(0f, 0f, 0f, 1f)
    }
}

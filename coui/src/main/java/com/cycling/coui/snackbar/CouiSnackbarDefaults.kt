package com.cycling.coui.snackbar

import androidx.compose.ui.unit.dp

/**
 * Snackbar 的固定参数，取自 `COUISnackBar` 与 `coui_snack_bar_*` 资源。
 *
 * 原厂 `make()` 把容器挂到最近的可承载父容器，底部留白取 `coui_snack_bar_margin_bottom`；
 * 入场与退场都走 `COUISpringAnimation`，`COUISpringForce` 的 response 分别为 0.3 / 0.25，
 * bounce 固定 0，动画进度映射到容器的 0.8→1 缩放与 0→1 透明度。
 */
object CouiSnackbarDefaults {
    /** `coui_snack_bar_margin_bottom`，由组件自身承担，宿主只需把 [SnackbarHost] 放在底部居中。 */
    val bottomMargin = 32.dp

    /** 原厂 `setDuration(0)` 表示不自动消失。 */
    const val indefiniteMillis = 0L

    /** 常用停留时长；原厂由调用方按业务传入，这里提供与轻提示一致的短档。 */
    const val shortMillis = 2000L

    /** 常用停留时长；与轻提示的长档一致。 */
    const val longMillis = 3500L

    /** `DEFAULT_SNACKBAR_SHOW_SPRING_RESPONSE`。 */
    const val showResponse = 0.3f

    /** `DEFAULT_SNACKBAR_DISMISS_SPRING_RESPONSE`。 */
    const val dismissResponse = 0.25f

    /** `DEFAULT_SNACKBAR_SPRING_BOUNCE`。 */
    const val springBounce = 0f

    /** 入场起点的容器缩放，来自 `setSnackBarProgress` 的 0.8。 */
    const val minScale = 0.8f

    /** 弹簧进度的可见变化阈值；原厂把进度放大到 10000 后积分，这里归一化到 0..1。 */
    internal const val springMinimumChange = 0.0001f
}

package com.itos.coui.snackbar

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.itos.coui.motion.CouiSpring
import com.itos.coui.theme.CouiTheme
import com.itos.coui.toast.CouiToastColors
import com.itos.coui.toast.CouiToastDefaults
import com.itos.coui.toast.CouiToastSurface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 一条待显示的 Snackbar。`id` 由 [CouiSnackbarState] 分配，用于区分相邻两条内容相同的提示。
 *
 * 操作按钮与图标槽在 [CouiSnackbarState.show] 时随文案一起快照；需要更新内容时重新调用 `show`。
 */
@Immutable
data class CouiSnackbarData(
    val id: Long,
    val message: String,
    val durationMillis: Long,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val icon: (@Composable () -> Unit)? = null,
)

/**
 * Snackbar 的单实例状态。宿主调用 [show] 提交一条提示，[SnackbarHost] 负责计时与退场。
 *
 * 原厂 `COUISnackBar.make()` 在父容器已有可见 Snackbar 时不再挂载新实例；这里保持同一语义：
 * 已有提示时 [show] 返回 `false` 且不替换内容。宿主需要排队时，在 [SnackbarHost] 的
 * `onDismissed` 回调里提交下一条，队列仍由宿主持有。
 */
@Stable
class CouiSnackbarState internal constructor() {
    private var nextId = 0L

    /** 当前提示；退场动画结束后才清空。 */
    var current: CouiSnackbarData? by mutableStateOf(null)
        private set

    /** 是否正在退场；由 [SnackbarHost] 读取，避免重复提交。 */
    internal var dismissing: Boolean by mutableStateOf(false)
        private set

    internal var withoutAnimation: Boolean by mutableStateOf(false)
        private set

    /**
     * 提交一条提示。
     *
     * @param durationMillis 停留时长；[CouiSnackbarDefaults.indefiniteMillis] 表示不自动消失。
     * 触摸按下或移动会暂停计时，抬手或取消后按**完整时长**重新计时，不续剩余时长。
     * @param actionLabel 与 [onAction] 同时提供时显示操作按钮；点击后先回调再立即退场。
     * @return 是否已被接受；已有提示未退场时返回 `false`。
     */
    fun show(
        message: String,
        durationMillis: Long = CouiSnackbarDefaults.shortMillis,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        icon: (@Composable () -> Unit)? = null,
    ): Boolean {
        require(durationMillis >= 0L) { "durationMillis 不能为负数" }
        if (current != null) return false
        current = CouiSnackbarData(nextId++, message, durationMillis, actionLabel, onAction, icon)
        dismissing = false
        withoutAnimation = false
        return true
    }

    /**
     * 进入退场动画；[withoutAnimation] 为 true 时立即移除，不等动画。
     * 重复调用不会叠加动画，等待动画期间再次调用也不改变已有决定。
     */
    fun dismiss(withoutAnimation: Boolean = false) {
        if (current == null || dismissing) return
        this.withoutAnimation = withoutAnimation
        dismissing = true
    }

    /** 由 [SnackbarHost] 在退场收尾后调用，只清理指定 id 的条目。 */
    internal fun clear(id: Long) {
        if (current?.id != id) return
        current = null
        dismissing = false
        withoutAnimation = false
    }
}

@Composable
fun rememberCouiSnackbarState(): CouiSnackbarState = remember { CouiSnackbarState() }

/**
 * 显示 [CouiSnackbarState] 里的当前提示。宿主用 `Box` 把它放在页面底部居中，
 * 组件自身按内容测量、不占满父级，底部留白由 [CouiSnackbarDefaults.bottomMargin] 承担。
 *
 * 容器几何、文字、操作按钮、图标与配色复用轻提示的 `coui_snack_bar_*` 绘制节点；
 * 与原厂一致的差异在单实例互斥、任意时长、触摸暂停与弹簧进出场，而不是排布。
 * 只有操作按钮接收点击，提示本体不拦截页面输入；覆盖层上的触摸只用于暂停计时。
 */
@Composable
fun SnackbarHost(
    state: CouiSnackbarState,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiToastColors = CouiToastDefaults.colors(dark),
    onShown: (() -> Unit)? = null,
    onDismissed: (() -> Unit)? = null,
) {
    val data = state.current ?: return
    val spring = remember {
        CouiSpring(0f, CouiSnackbarDefaults.springMinimumChange)
    }
    var paused by remember(data.id) { mutableStateOf(false) }
    var actionTaken by remember(data.id) { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { spring.cancelComplete() } }

    // 入场：response 0.3、bounce 0；进度 0→1 映射到 0.8→1 缩放与 0→1 透明度。
    LaunchedEffect(data.id) {
        spring.animateTo(1f, CouiSnackbarDefaults.showResponse,
            CouiSnackbarDefaults.springBounce, CouiSnackbarDefaults.springMinimumChange)
        onShown?.invoke()
    }

    // 退场：response 0.25；动画结束才清条目，供宿主在 onDismissed 里提交下一条。
    LaunchedEffect(data.id, state.dismissing) {
        if (!state.dismissing) return@LaunchedEffect
        if (state.withoutAnimation) {
            spring.cancelComplete()
            state.clear(data.id)
            onDismissed?.invoke()
        } else {
            spring.animateTo(0f, CouiSnackbarDefaults.dismissResponse,
                CouiSnackbarDefaults.springBounce, CouiSnackbarDefaults.springMinimumChange,
                onEnd = {
                    state.clear(data.id)
                    onDismissed?.invoke()
                })
        }
    }

    // 计时：DOWN/MOVE 暂停，UP/CANCEL 后按完整时长重新计时；0 表示不自动消失。
    LaunchedEffect(data.id, state.dismissing) {
        if (state.dismissing || data.durationMillis == CouiSnackbarDefaults.indefiniteMillis) {
            return@LaunchedEffect
        }
        while (true) {
            val interrupted = withTimeoutOrNull(data.durationMillis) {
                snapshotFlow { paused }.first { it }
            }
            if (state.dismissing) return@LaunchedEffect
            if (interrupted == null) {
                state.dismiss()
                return@LaunchedEffect
            }
            snapshotFlow { paused }.first { !it }
            if (state.dismissing) return@LaunchedEffect
        }
    }

    // 只观察手势，不消费事件：子节点的 clickable 与页面滚动照常收到输入。
    val touchObserver = Modifier.pointerInput(data.id) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            paused = true
            waitForUpOrCancellation(PointerEventPass.Initial)
            paused = false
        }
    }

    Box(
        modifier
            .padding(bottom = CouiSnackbarDefaults.bottomMargin)
            .then(touchObserver)
            .graphicsLayer {
                val f = spring.state.value
                val scale = CouiSnackbarDefaults.minScale +
                    (1f - CouiSnackbarDefaults.minScale) * f
                scaleX = scale
                scaleY = scale
                alpha = f
            },
    ) {
        val action = data.actionLabel?.takeIf { data.onAction != null }
        CouiToastSurface(
            message = data.message,
            actionLabel = action,
            icon = data.icon,
            colors = colors,
            onAction = {
                // 退场动画期间容器仍在，按钮只提交一次业务动作。
                if (!actionTaken) {
                    actionTaken = true
                    data.onAction?.invoke()
                    state.dismiss()
                }
            },
        )
    }
}

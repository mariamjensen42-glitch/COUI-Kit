package com.cycling.coui.pulltorefresh

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.R
import com.cycling.coui.input.CouiHapticFeedback
import com.cycling.coui.input.performCouiHapticFeedback
import com.cycling.coui.progress.CircularProgress
import com.cycling.coui.scroll.CouiScrollPhysics
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.rememberCouiSystemTextStyle
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** 下拉头的度量、文案与默认头。 */
object CouiPullToRefreshDefaults {
    /** 原厂 `common_TF20`：头部高度，也是松手后仍触发刷新的最小下拉距离。 */
    val headerHeight = 40.dp

    /** 原厂 `common_M19`：环的边长。 */
    val indicatorSize = 16.dp

    val indicatorStroke = 2.dp

    /** 原厂 `layout_marginStart="2.0dp"`：环与文案的间距。 */
    val indicatorGap = 2.dp

    /** 原厂 `DefaultHeader`：锁定位置取头部的两倍。 */
    val threshold = headerHeight * 2

    /** 原厂头部整体 0.26 的透明度。 */
    const val indicatorAlpha = 0.26f

    /** 原厂松手后 `startScrollTo(threshold, 800)`。 */
    const val lockDurationMillis = 800

    /** 原厂刷新完成 `startScrollTo(0, 500)`。 */
    const val settleDurationMillis = 500

    /** 文案切换前先等 100ms，再各用 200ms 淡出淡入。 */
    const val labelFadeDelayMillis = 100

    const val labelFadeMillis = 200

    /** 原厂环起始角度，松手后随下拉继续旋转 90° 回到 0°。 */
    private const val startRotation = 270f

    private const val releaseRotation = 90f

    /**
     * 原厂下拉头：16dp 环 + 2dp 间距 + 文案，整体 0.26 透明度。
     *
     * [progress] 是位移相对 [threshold] 的比值，达到 1 表示已越过松开即刷新的位置；
     * 环的旋转按 `DefaultHeader` 的分段公式给出，刷新态交给不确定态自转。
     */
    @Composable
    fun Header(progress: Float, refreshing: Boolean, dark: Boolean = CouiTheme.isDark,
        modifier: Modifier = Modifier) {
        val scheme = CouiTheme.colorSchemeFor(dark)
        val density = LocalDensity.current
        val thresholdPx = with(density) { threshold.toPx() }
        val dragThresholdPx = with(density) { headerHeight.toPx() }
        val offsetPx = progress * thresholdPx
        val rotation = when {
            refreshing -> 0f
            offsetPx >= thresholdPx -> (offsetPx - thresholdPx) / 2f
            else -> startRotation + ((offsetPx - dragThresholdPx) / (thresholdPx - dragThresholdPx))
                .coerceIn(0f, 1f) * releaseRotation
        }
        Row(modifier.fillMaxWidth().height(headerHeight),
            horizontalArrangement = Arrangement.spacedBy(indicatorGap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            // 无 track：原厂环只有一个弧段。
            CircularProgress(progress = if (refreshing) null else progress.coerceIn(0f, 1f), dark = dark,
                color = scheme.onSurface, trackColor = Color.Transparent,
                strokeWidth = indicatorStroke, size = indicatorSize,
                modifier = Modifier.alpha(indicatorAlpha).graphicsLayer { rotationZ = rotation })
            PullRefreshLabel(dark = dark, refreshing = refreshing,
                modifier = Modifier.alpha(indicatorAlpha))
        }
    }
}

/**
 * 下拉刷新：内容到顶后继续下拉由头部接管，松手越过 [CouiPullToRefreshDefaults.headerHeight]
 * 即锁定到 [CouiPullToRefreshDefaults.threshold] 并回调 [onRefresh]。
 *
 * 位移只在本组件持有，内容整体随头部下移，布局尺寸不变，列表滚动位置与 `Scaffold` 内边距都不受影响。
 * 刷新状态由宿主持有：[refreshing] 为真时保持在锁定位置，变回假时用 500ms 收回。
 * 内容必须是可滚动容器，且不要再套在另一层滚动容器里，否则外层会先消耗下拉位移。
 */
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
    hapticFeedbackEnabled: Boolean = true,
    header: @Composable (progress: Float, refreshing: Boolean) -> Unit = { progress, active ->
        CouiPullToRefreshDefaults.Header(progress, active, dark)
    },
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    // 原厂 resolveOverscrollDistance 以屏幕高为准，阻尼随位移接近它而收敛。
    val overscrollDistance = remember(view) {
        view.resources.displayMetrics.heightPixels.coerceAtLeast(1).toFloat()
    }
    val thresholdPx = with(density) { CouiPullToRefreshDefaults.threshold.toPx() }
    val state = remember(thresholdPx, overscrollDistance, scope) {
        PullToRefreshState(scope, thresholdPx, overscrollDistance)
    }
    SideEffect {
        state.onTrigger = onRefresh
        state.onArm = {
            if (hapticFeedbackEnabled) view.performCouiHapticFeedback(CouiHapticFeedback.ScrollEdge)
        }
    }
    LaunchedEffect(refreshing) { state.onRefreshingChanged(refreshing) }
    val connection = remember(state) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 只有向上收的位移在 pre 阶段接管；向下拉先留给列表自己滚，滚到顶后才在 post 阶段变成下拉。
                if (source != NestedScrollSource.UserInput || available.y >= 0f) return Offset.Zero
                return state.consume(available.y)
            }

            override fun onPostScroll(consumed: Offset, available: Offset,
                source: NestedScrollSource): Offset {
                // 列表滚到头后剩余的位移转成下拉，返回消费量以压住平台的拉伸回弹。
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                return state.consume(available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (state.status == PullStatus.Idle || state.status == PullStatus.Refreshing) {
                    return Velocity.Zero
                }
                // 接住过下拉才吃速度，避免回弹与列表 fling 抢位移后又被平台拉伸。
                val holds = state.offset > 0f
                state.release()
                return if (holds) available else Velocity.Zero
            }
        }
    }
    Box(modifier.fillMaxSize().nestedScroll(connection)) {
        Box(Modifier.fillMaxSize().graphicsLayer { translationY = state.offset }) { content() }
        // 原厂头部是容器后加的子 View：叠在内容之上，随下拉下移，位移为 0 时不可见。
        Box(Modifier.fillMaxWidth().height(CouiPullToRefreshDefaults.headerHeight).graphicsLayer {
            translationY = state.offset
            alpha = if (state.offset > 0f) 1f else 0f
        }, contentAlignment = Alignment.Center) {
            header(state.progress, state.status == PullStatus.Refreshing)
        }
    }
}

private enum class PullStatus { Idle, Dragging, Armed, Refreshing }

/** 原厂 `BounceLayout` + `DefaultHeader` 的位移与状态机，像素单位。 */
private class PullToRefreshState(
    private val scope: CoroutineScope,
    private val thresholdPx: Float,
    private val overscrollDistance: Float,
) {
    // 原厂 `VERTICAL_SPRING_BACK_TENSION_MULTIPLE`。
    private val physics = CouiScrollPhysics(1, springBackTensionMultiple = 2.15f, scrollView = false)
    private var animation: Job? = null

    var offset by mutableFloatStateOf(0f)
        private set

    var status by mutableStateOf(PullStatus.Idle)
        private set

    var onTrigger: () -> Unit = {}

    var onArm: () -> Unit = {}

    val progress: Float get() = offset / thresholdPx

    /** 消费一次拖动（[delta] 正数为下拉），返回实际用掉的位移，供嵌套滚动回填。 */
    fun consume(delta: Float): Offset {
        val used = drag(delta)
        return if (used == 0f) Offset.Zero else Offset(0f, used)
    }

    private fun drag(delta: Float): Float {
        if (delta == 0f || status == PullStatus.Refreshing) return 0f
        if (status == PullStatus.Idle) {
            // 只有向下拉才接管，其余留给列表自己的滚动。
            if (delta <= 0f) return 0f
            animation?.cancel()
            status = PullStatus.Dragging
        }
        val next = if (delta > 0f) (offset + damp(delta)).coerceAtMost(overscrollDistance)
        else (offset + delta).coerceAtLeast(0f)
        val used = next - offset
        offset = next
        when {
            status == PullStatus.Dragging && offset >= thresholdPx -> {
                status = PullStatus.Armed
                onArm()
            }
            status == PullStatus.Armed && offset < thresholdPx -> status = PullStatus.Dragging
        }
        if (offset <= 0f && delta < 0f) status = PullStatus.Idle
        return used
    }

    /** 松手：位移到阈值（头部两倍）才锁定刷新，否则按原厂弹簧回弹。 */
    fun release() {
        if (status == PullStatus.Refreshing) return
        if (offset >= thresholdPx) lockIn(trigger = true) else springBack()
    }

    fun onRefreshingChanged(refreshing: Boolean) {
        if (refreshing) {
            if (status != PullStatus.Refreshing) lockIn(trigger = false)
        } else if (status == PullStatus.Refreshing) {
            settle()
        }
    }

    private fun lockIn(trigger: Boolean) {
        status = PullStatus.Refreshing
        animation?.cancel()
        animation = scope.launch {
            animate(offset, thresholdPx,
                animationSpec = tween(CouiPullToRefreshDefaults.lockDurationMillis)) { value, _ ->
                offset = value
            }
            if (trigger) onTrigger()
        }
    }

    private fun settle() {
        status = PullStatus.Idle
        animation?.cancel()
        animation = scope.launch {
            animate(offset, 0f,
                animationSpec = tween(CouiPullToRefreshDefaults.settleDurationMillis)) { value, _ ->
                offset = value
            }
        }
    }

    private fun springBack() {
        status = PullStatus.Idle
        animation?.cancel()
        animation = scope.launch {
            // 与 CouiOverscrollEffect.rebound 同一套坐标：积分器从 -(offset) 回到 0。
            physics.stop()
            var previous = System.nanoTime()
            val started = previous
            physics.returnToBounds(-offset.roundToInt(), 0)
            while (physics.running) {
                val now = withFrameNanos { it }
                physics.update((now - previous) / 1_000_000_000f, (now - started) / 1_000_000f)
                previous = now
                offset = -physics.current.toFloat()
            }
            offset = 0f
        }
    }

    /** 原厂 `COUIPhysicalAnimationUtil.b`：位移越大可用行程越小。 */
    private fun damp(delta: Float): Float =
        ((delta.toInt() * (1f - (abs(offset) / overscrollDistance).coerceAtMost(1f))) / 5f * 2f)
            .toInt().toFloat()
}

/** 刷新态文案随淡出淡入切换，字号按原厂 `TextView` 默认的 14sp。 */
@Composable
private fun PullRefreshLabel(dark: Boolean, refreshing: Boolean, modifier: Modifier = Modifier) {
    val pull = stringResource(R.string.coui_pull_refresh)
    val active = stringResource(R.string.coui_pull_refreshing)
    var text by remember { mutableStateOf(pull) }
    val fade = remember { Animatable(1f) }
    LaunchedEffect(refreshing) {
        if ((text == active) == refreshing) return@LaunchedEffect
        delay(CouiPullToRefreshDefaults.labelFadeDelayMillis.toLong())
        fade.animateTo(0f, tween(CouiPullToRefreshDefaults.labelFadeMillis))
        text = if (refreshing) active else pull
        fade.animateTo(1f, tween(CouiPullToRefreshDefaults.labelFadeMillis))
    }
    BasicText(text, modifier.graphicsLayer { alpha = fade.value },
        style = rememberCouiSystemTextStyle(CouiTextKind.Body, size = 14.sp).copy(
            color = CouiTheme.colorSchemeFor(dark).onSurface, textAlign = TextAlign.Center))
}

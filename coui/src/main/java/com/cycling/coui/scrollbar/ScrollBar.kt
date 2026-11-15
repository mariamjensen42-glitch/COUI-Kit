package com.cycling.coui.scrollbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.roundToInt

/** 滚动条拇指的颜色。 */
@Immutable
data class CouiScrollBarColors(val thumb: Color)

/**
 * 滚动条的固定参数，取自 ColorOS 17 的 `COUIScrollBar` 与 `com.support.scrollbar` 资源：
 * 轨道宽 14dp、拇指最小高 48dp（`coui_scrollbar_wight` / `coui_scrollbar_min_height`），
 * 绘制区上下留白 8dp 与 16dp（`coui_scrollbar_margin_top` / `_bottom`），
 * 常态左右各内缩 5.5dp、按下 4dp（`coui_scrollbar_drawable_default_inset` / `_pressed_inset`），
 * 颜色 `coui_scrollbar_color`（日间 `#29000000`、夜间 `#40FFFFFF`）。
 *
 * 显隐沿用原厂状态机：滚动后停留 2000ms 再在 250ms 内淡出（`ViewConfiguration` 的
 * `SCROLL_BAR_DEFAULT_DELAY * 4` 用于首次挂载，即 1200ms），拖动期间常亮、不淡出。
 */
object CouiScrollBarDefaults {
    val width = 14.dp
    val minThumbHeight = 48.dp
    val marginTop = 8.dp
    val marginBottom = 16.dp
    val inset = 5.5.dp
    val pressedInset = 4.dp

    /** `awakenScrollBars(2000)` 的停留时长。 */
    const val fadeDelayMillis = 2000L

    /** 首次挂载时 `awakenScrollBars(getScrollDefaultDelay() * 4)` 的停留时长。 */
    const val initialFadeDelayMillis = 1200L

    /** `ViewConfiguration.getScrollBarFadeDuration()`。 */
    const val fadeDurationMillis = 250L

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiScrollBarColors =
        CouiScrollBarColors(if (dark) Color(0x40FFFFFF) else Color(0x29000000))
}

private data class CouiScrollBarThumb(val top: Float, val height: Float)

/** 原厂 `updateThumbRect` 的几何：拇指高度按视口占比，位置按可滚范围占比。 */
private fun couiScrollBarThumb(value: Int, maxValue: Int, viewport: Int, height: Float, minThumb: Float): CouiScrollBarThumb? {
    if (maxValue <= 0 || viewport <= 0) return null
    val scrollable = maxValue - viewport
    if (scrollable <= 0) return null
    val thumbHeight = maxOf(minThumb, (viewport.toFloat() / maxValue * height).roundToInt().toFloat())
        .coerceAtMost(height)
    val top = (height - thumbHeight) * (value.toFloat() / scrollable)
    return CouiScrollBarThumb(top.coerceIn(0f, (height - thumbHeight).coerceAtLeast(0f)), thumbHeight)
}

/**
 * 竖向滚动条：叠在与滚动容器同级的 `Box` 中，用 `Modifier.matchParentSize()` 铺满，
 * 读取宿主的 [ScrollState] 计算位置，不持有业务状态。
 *
 * 只有拇指矩形命中且拇指可见时才接管拖动，未命中时把事件留给下方的滚动容器；
 * 拖动按原厂换算把拇指位置写回滚动偏移，RTL 跟随布局方向（原厂按默认 Locale 判断）。
 *
 * 拇指位置以亚像素计算，原厂取整到像素；差异只在小于 1px 的位置偏移。
 *
 * 拖动期间与滚动惯性竞争时，本组件不会取消滚动容器已在进行的 fling，
 * 只等待当前滚动事务结束后再应用位移（Compose 没有取消 fling 的公开入口）。
 */
@Composable
fun ScrollBar(
    modifier: Modifier = Modifier,
    state: ScrollState,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiScrollBarColors = CouiScrollBarDefaults.colors(dark),
    fadeDelayMillis: Long = CouiScrollBarDefaults.fadeDelayMillis,
) {
    val density = LocalDensity.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val widthPx = with(density) { CouiScrollBarDefaults.width.toPx() }
    val minThumbPx = with(density) { CouiScrollBarDefaults.minThumbHeight.toPx() }
    val insetPx = with(density) { CouiScrollBarDefaults.inset.toPx() }
    val pressedInsetPx = with(density) { CouiScrollBarDefaults.pressedInset.toPx() }
    val marginTopPx = with(density) { CouiScrollBarDefaults.marginTop.toPx() }
    val marginBottomPx = with(density) { CouiScrollBarDefaults.marginBottom.toPx() }

    val alpha = remember { Animatable(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var dragTargetTop by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(state) {
        var first = true
        snapshotFlow { Triple(state.value, state.isScrollInProgress, dragging) }
            .collectLatest { (_, _, isDragging) ->
                val delayMs = if (first) CouiScrollBarDefaults.initialFadeDelayMillis else fadeDelayMillis
                first = false
                if (isDragging) {
                    alpha.snapTo(1f)
                    return@collectLatest
                }
                alpha.snapTo(1f)
                delay(delayMs)
                alpha.animateTo(0f, tween(CouiScrollBarDefaults.fadeDurationMillis.toInt()))
            }
    }

    // 拖动只写入目标位置，滚动事务在这里统一提交，避免在输入协程里抢占滚动容器。
    LaunchedEffect(state) {
        snapshotFlow { dragTargetTop }.filterNotNull().collect { target ->
            val current = couiScrollBarThumb(state.value, state.maxValue, state.viewportSize, heightPx, minThumbPx)
            val usable = heightPx - (current?.height ?: 0f)
            if (usable > 0f) {
                val ratio = (target / usable).coerceIn(0f, 1f)
                val delta = (state.maxValue - state.viewportSize) * ratio - state.value
                if (delta != 0f) state.scrollBy(delta)
            }
            if (dragTargetTop == target) dragTargetTop = null
        }
    }

    Box(modifier.onSizeChanged { heightPx = it.height.toFloat() }) {
        Canvas(
            Modifier.matchParentSize().pointerInput(rtl, state) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    // 原厂在滚动条处于 OFF 状态时不接管 DOWN。
                    if (alpha.value <= 0f) return@awaitEachGesture
                    val geo = couiScrollBarThumb(state.value, state.maxValue, state.viewportSize,
                        size.height.toFloat(), minThumbPx) ?: return@awaitEachGesture
                    val left = if (rtl) 0f else size.width - widthPx
                    val inside = down.position.x >= left && down.position.x <= left + widthPx &&
                        down.position.y >= geo.top && down.position.y <= geo.top + geo.height
                    if (!inside) return@awaitEachGesture
                    dragging = true
                    down.consume()
                    val grabOffset = down.position.y - geo.top
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        val current = couiScrollBarThumb(state.value, state.maxValue, state.viewportSize,
                            size.height.toFloat(), minThumbPx) ?: break
                        val usable = size.height - current.height
                        dragTargetTop = (change.position.y - grabOffset).coerceIn(0f, usable.coerceAtLeast(0f))
                        change.consume()
                    }
                    dragging = false
                    dragTargetTop = null
                }
            },
        ) {
            val geo = couiScrollBarThumb(state.value, state.maxValue, state.viewportSize,
                size.height, minThumbPx) ?: return@Canvas
            if (alpha.value <= 0f) return@Canvas
            val inset = if (dragging) pressedInsetPx else insetPx
            val left = if (rtl) 0f else size.width - widthPx
            val top = geo.top + marginTopPx
            val bottom = geo.top + geo.height - marginBottomPx
            val visibleHeight = (bottom - top).coerceAtLeast(0f)
            val visibleWidth = (widthPx - inset * 2).coerceAtLeast(0f)
            if (visibleHeight <= 0f || visibleWidth <= 0f) return@Canvas
            drawRoundRect(
                color = colors.thumb.copy(alpha = colors.thumb.alpha * alpha.value),
                topLeft = Offset(left + inset, top),
                size = Size(visibleWidth, visibleHeight),
                cornerRadius = CornerRadius(minOf(visibleWidth, visibleHeight) / 2f),
            )
        }
    }
}

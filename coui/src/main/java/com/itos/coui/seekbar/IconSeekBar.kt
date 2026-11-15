package com.itos.coui.seekbar

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.itos.coui.icons.CouiIcons
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 图标滑动条，对应原厂 `com.coui.appcompat.seekbar.COUIIconSeekBar`。
 *
 * 胶囊面板左侧是图标、右侧是细轨道：亮度档的太阳图标随进度旋转（每档 2°），
 * 音量档的图标随进度换档。点击轨道任意位置以原厂时长过渡到该档位，
 * 拖动时滑块以 0.4~1 的阻尼跟随手指（越靠近中心越跟手）。
 *
 * 与原厂的差异：
 * - 原厂固定 408×96px 的居中面板，这里面板铺满可用宽度、高度固定 32dp；
 * - 原厂音量档在 `ic_volume_seekbar_close/middle/open` 三张位图之间切换，
 *   项目没有对应位图，统一用单个音量图标并按档位调整不透明度；
 * - 原厂 305/306 触感码是厂商私有常量，这里映射为系统分档触感反馈。
 *
 * @param progress 当前档位，落在 [min]..[max] 内。
 * @param onProgressChange 点击与拖动引起的档位变化。
 * @param max 最大档位，对应原厂 `couiIconSeekBarMax`。
 * @param onProgressChangeFinished 点击过渡或拖动结束后的回调。
 */
@Composable
fun IconSeekBar(
    progress: Int,
    onProgressChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    type: CouiIconSeekBarType = CouiIconSeekBarType.Brightness,
    max: Int = CouiIconSeekBarDefaults.max,
    min: Int = 0,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiIconSeekBarColors = CouiIconSeekBarDefaults.colors(dark),
    onProgressChangeFinished: (() -> Unit)? = null,
) {
    require(max > min) { "max 必须大于 min" }
    val span = max - min
    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val onChange by rememberUpdatedState(onProgressChange)
    val onFinished by rememberUpdatedState(onProgressChangeFinished)
    val icon = rememberVectorPainter(
        if (type == CouiIconSeekBarType.Brightness) CouiIcons.Filled.Sun else CouiIcons.Filled.Volume,
    )

    fun fractionOf(value: Int): Float = ((value - min).toFloat() / span).coerceIn(0f, 1f)

    var visual by remember { mutableFloatStateOf(fractionOf(progress.coerceIn(min, max))) }
    var interacting by remember { mutableStateOf(false) }

    LaunchedEffect(progress, interacting) {
        if (!interacting) visual = fractionOf(progress.coerceIn(min, max))
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(CouiIconSeekBarDefaults.height)
            .semantics {
                if (!enabled) disabled()
                progressBarRangeInfo = ProgressBarRangeInfo(
                    progress.coerceIn(min, max).toFloat(),
                    min.toFloat()..max.toFloat(),
                    0,
                )
                setProgress { target ->
                    val next = target.roundToInt().coerceIn(min, max)
                    if (next != progress) {
                        onChange(next)
                        onFinished?.invoke()
                    }
                    true
                }
            }
            .pointerInput(enabled, min, max, isRtl) {
                if (!enabled) return@pointerInput
                val trackStart = with(density) {
                    (CouiIconSeekBarDefaults.iconCenterInset + CouiIconSeekBarDefaults.iconSize / 2 +
                        CouiIconSeekBarDefaults.iconTrackGap).toPx()
                }
                val trackEndInset = with(density) { CouiIconSeekBarDefaults.trackEndInset.toPx() }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    val trackEnd = size.width.toFloat() - trackEndInset
                    var dragging = false
                    var committed = progress.coerceIn(min, max)
                    var fingerFraction = fractionAt(down.position.x, trackStart, trackEnd, isRtl)
                    while (true) {
                        val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        fingerFraction = fractionAt(change.position.x, trackStart, trackEnd, isRtl)
                        if (!dragging) {
                            if (abs(change.position.x - down.position.x) < 2f) {
                                change.consume()
                                continue
                            }
                            dragging = true
                            interacting = true
                        }
                        val damping = (1f - abs(fingerFraction - 0.5f) * 2f)
                            .coerceIn(CouiIconSeekBarDefaults.minDamping, 1f)
                        visual = (visual + (fingerFraction - visual) * damping).coerceIn(0f, 1f)
                        val next = (min + (visual * span).roundToInt()).coerceIn(min, max)
                        if (next != committed) {
                            committed = next
                            onChange(next)
                            performIconHaptic(view, next, min, max)
                        }
                        change.consume()
                    }
                    interacting = true
                    scope.launch {
                        if (dragging) {
                            val target = fractionOf(committed)
                            animate(visual, target, 0f,
                                tween(CouiIconSeekBarDefaults.settleMillis)) { animated, _ ->
                                visual = animated
                            }
                            visual = target
                        } else {
                            val target = (min + (fingerFraction * span).roundToInt()).coerceIn(min, max)
                            val targetFraction = fractionOf(target)
                            val duration = (abs(targetFraction - visual) *
                                CouiIconSeekBarDefaults.clickDurationFactor).roundToInt()
                                .coerceAtLeast(CouiIconSeekBarDefaults.clickMinMillis)
                            animate(visual, targetFraction, 0f,
                                tween(duration, easing = CouiIconSeekBarDefaults.moveEase)) { animated, _ ->
                                visual = animated
                            }
                            visual = targetFraction
                            if (committed != target) {
                                onChange(target)
                                performIconHaptic(view, target, min, max)
                            }
                        }
                        interacting = false
                        onFinished?.invoke()
                    }
                }
            },
    ) {
        Canvas(Modifier.fillMaxWidth().height(CouiIconSeekBarDefaults.height)) {
            drawIconSeekBar(
                visual = visual,
                isRtl = isRtl,
                type = type,
                progress = (min + (visual * span).roundToInt()).coerceIn(min, max),
                enabled = enabled,
                density = density,
                colors = colors,
                icon = icon,
            )
        }
    }
}

/** 手指位置换算为进度区内的 0f..1f 比例。 */
private fun fractionAt(x: Float, start: Float, end: Float, isRtl: Boolean): Float {
    val usable = end - start
    if (usable <= 0f) return 0f
    val raw = ((x - start) / usable).coerceIn(0f, 1f)
    return if (isRtl) 1f - raw else raw
}

/** 档位变化时的触感反馈。 */
private fun performIconHaptic(view: android.view.View, progress: Int, min: Int, max: Int) {
    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        if (progress == min || progress == max) {
            HapticFeedbackConstants.SEGMENT_TICK
        } else {
            HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
        }
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
    view.performHapticFeedback(code)
}

/** 绘制面板、图标、轨道与进度。 */
private fun DrawScope.drawIconSeekBar(
    visual: Float,
    isRtl: Boolean,
    type: CouiIconSeekBarType,
    progress: Int,
    enabled: Boolean,
    density: Density,
    colors: CouiIconSeekBarColors,
    icon: Painter,
) {
    val panelRadius = with(density) { CouiIconSeekBarDefaults.panelRadius.toPx() }
    val iconSize = with(density) { CouiIconSeekBarDefaults.iconSize.toPx() }
    val iconCenterInset = with(density) { CouiIconSeekBarDefaults.iconCenterInset.toPx() }
    val iconTrackGap = with(density) { CouiIconSeekBarDefaults.iconTrackGap.toPx() }
    val trackEndInset = with(density) { CouiIconSeekBarDefaults.trackEndInset.toPx() }
    val trackHeight = with(density) { CouiIconSeekBarDefaults.trackHeight.toPx() }
    val trackRadius = with(density) { CouiIconSeekBarDefaults.trackRadius.toPx() }

    drawRoundRect(
        color = colors.panel,
        size = size,
        cornerRadius = CornerRadius(panelRadius),
    )

    val iconLeft = iconCenterInset - iconSize / 2f
    val iconTop = size.height / 2f - iconSize / 2f
    val degrees = if (type == CouiIconSeekBarType.Brightness) {
        progress * CouiIconSeekBarDefaults.brightnessDegreePerUnit
    } else {
        0f
    }
    val iconAlpha = if (type == CouiIconSeekBarType.Volume && progress == 0) 0.5f else 1f
    rotate(degrees, pivot = Offset(iconCenterInset, size.height / 2f)) {
        translate(iconLeft, iconTop) {
            with(icon) {
                draw(Size(iconSize, iconSize), alpha = iconAlpha, colorFilter = ColorFilter.tint(colors.icon))
            }
        }
    }

    val trackStart = iconCenterInset + iconSize / 2f + iconTrackGap
    val trackEnd = size.width - trackEndInset
    val usable = trackEnd - trackStart
    if (usable <= 0f) return
    val centerY = size.height / 2f

    drawRoundRect(
        color = colors.track,
        topLeft = Offset(trackStart, centerY - trackHeight / 2f),
        size = Size(usable, trackHeight),
        cornerRadius = CornerRadius(trackRadius),
    )

    val progressWidth = usable * visual
    if (progressWidth > 0f) {
        val left = if (isRtl) trackEnd - progressWidth else trackStart
        drawRoundRect(
            color = if (enabled) colors.progress else colors.disabledProgress,
            topLeft = Offset(left, centerY - trackHeight / 2f),
            size = Size(progressWidth.coerceAtLeast(trackRadius * 2f), trackHeight),
            cornerRadius = CornerRadius(trackRadius),
        )
    }
}

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
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
    val icon = remember(type) {
        PathParser().parsePathString(
            if (type == CouiIconSeekBarType.Brightness) CouiIconSeekBarPaths.sun
            else CouiIconSeekBarPaths.volume,
        ).toPath()
    }

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
    icon: Path,
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
    val iconFactor = iconSize / CouiIconSeekBarPaths.viewport
    rotate(degrees, pivot = Offset(iconCenterInset, size.height / 2f)) {
        translate(iconLeft, iconTop) {
            scale(iconFactor, iconFactor, pivot = Offset.Zero) {
                drawPath(icon, colors.icon, alpha = iconAlpha)
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

/** 亮度与音量图标路径；库不依赖 `coui-icons`，这里内联 24dp viewport 的路径数据。 */
private object CouiIconSeekBarPaths {
    const val viewport = 24f
    const val sun = "M12,18.221C12.386,18.221 12.7,18.534 12.7,18.921V22.297C12.699,22.683 12.386,22.997 12,22.997C11.613,22.997 11.3,22.683 11.299,22.297V18.921C11.299,18.535 11.613,18.221 12,18.221ZM6.611,16.398C6.884,16.125 7.328,16.125 7.601,16.398C7.874,16.672 7.874,17.115 7.601,17.389L5.214,19.775C4.941,20.049 4.497,20.049 4.224,19.775C3.951,19.502 3.951,19.058 4.224,18.785L6.611,16.398ZM16.399,16.398C16.672,16.125 17.116,16.125 17.389,16.398L19.776,18.785C20.049,19.059 20.049,19.502 19.776,19.775C19.503,20.049 19.059,20.049 18.786,19.775L16.399,17.389C16.126,17.115 16.126,16.672 16.399,16.398ZM12.004,7.089C14.716,7.089 16.915,9.287 16.916,11.999C16.916,14.711 14.717,16.91 12.004,16.91C9.293,16.91 7.094,14.711 7.094,11.999C7.094,9.287 9.293,7.089 12.004,7.089ZM22.297,11.3C22.684,11.3 22.997,11.614 22.997,12C22.997,12.387 22.683,12.7 22.296,12.7H18.92C18.534,12.7 18.22,12.386 18.22,12C18.22,11.613 18.535,11.3 18.921,11.3H22.297ZM5.08,11.299C5.466,11.299 5.779,11.613 5.78,11.999C5.78,12.386 5.466,12.699 5.08,12.699H1.704C1.317,12.699 1.003,12.386 1.003,11.999C1.004,11.613 1.317,11.299 1.704,11.299H5.08ZM18.786,4.224C19.059,3.95 19.503,3.951 19.776,4.224C20.049,4.497 20.049,4.941 19.776,5.214L17.389,7.602C17.116,7.875 16.672,7.875 16.399,7.602C16.126,7.328 16.126,6.885 16.399,6.611L18.786,4.224ZM4.224,4.224C4.497,3.951 4.941,3.951 5.214,4.224L7.602,6.61C7.875,6.884 7.875,7.327 7.602,7.601C7.329,7.874 6.885,7.874 6.612,7.601L4.224,5.214C3.951,4.941 3.951,4.497 4.224,4.224ZM12,1.003C12.387,1.003 12.701,1.317 12.701,1.703V5.079C12.701,5.466 12.387,5.779 12,5.779C11.614,5.779 11.3,5.466 11.3,5.079V1.703C11.3,1.317 11.614,1.003 12,1.003Z"
    const val volume = "M11.082,1.73C11.248,1.743 11.408,1.798 11.547,1.888C12.002,2.184 12.002,3.148 12.002,5.076V18.927C12.002,20.853 12.002,21.816 11.547,22.112C11.408,22.203 11.248,22.257 11.082,22.27C10.541,22.313 9.952,21.548 8.775,20.018L7.404,18.235C6.985,17.691 6.775,17.419 6.506,17.219C6.416,17.153 6.323,17.093 6.225,17.04C5.931,16.879 5.595,16.804 4.925,16.654L2.837,16.185C2.061,16.011 1.673,15.925 1.413,15.677C1.328,15.596 1.254,15.504 1.194,15.403C1.009,15.094 1.009,14.697 1.009,13.902V10.098C1.009,9.304 1.009,8.906 1.194,8.597C1.254,8.497 1.328,8.405 1.413,8.324C1.673,8.076 2.061,7.989 2.837,7.815L4.925,7.346C5.596,7.196 5.931,7.12 6.225,6.96C6.323,6.907 6.416,6.847 6.506,6.781C6.775,6.581 6.985,6.31 7.404,5.765L8.775,3.981C9.952,2.452 10.541,1.687 11.082,1.73ZM18.835,4.356C19.108,4.083 19.552,4.083 19.825,4.356C24.046,8.578 24.045,15.423 19.824,19.644C19.551,19.917 19.109,19.917 18.836,19.644C18.562,19.371 18.562,18.927 18.835,18.654C22.509,14.979 22.509,9.021 18.835,5.346C18.561,5.073 18.561,4.63 18.835,4.356ZM16.432,6.481C16.706,6.208 17.149,6.208 17.423,6.481C20.471,9.529 20.47,14.471 17.423,17.519C17.149,17.793 16.706,17.793 16.432,17.519C16.159,17.246 16.16,16.803 16.433,16.53C18.934,14.028 18.934,9.972 16.432,7.47C16.159,7.197 16.159,6.755 16.432,6.481ZM14.198,8.714C14.471,8.44 14.915,8.44 15.188,8.714C17.003,10.529 17.003,13.471 15.188,15.286C14.915,15.559 14.471,15.559 14.198,15.286C13.925,15.012 13.926,14.569 14.199,14.296C15.467,13.027 15.466,10.971 14.198,9.703C13.925,9.43 13.925,8.987 14.198,8.714Z"
}

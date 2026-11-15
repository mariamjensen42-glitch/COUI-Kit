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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
 * 分段滑动条，对应原厂 `com.coui.appcompat.seekbar.COUISectionSeekBar`。
 *
 * 进度按 [valueRange] 的整数档取值，轨道上为每一档画一个段标记，
 * 滑块左侧的标记用未激活色（画在进度填充上），右侧用激活色（画在空轨道上）。
 *
 * 交互按原厂 `SectionAnimationHandler` 移植：
 * - 拖动时滑块以 0.4 的跟手系数收敛到手指位置，进度取最近的档位；
 * - 抬手后以 150ms 吸附到当前档位；
 * - 未发生拖动时，点击轨道以 200ms 过渡到点击位置对应的档位；
 * - 档位变化触发分档触感反馈。
 *
 * 与原厂的差异：
 * - 原厂的 `startDragSnapAnimation` 会在拖动过程中按方向变化做 100ms 的中间吸附，
 *   这里只在抬手时吸附，拖动过程连续跟手；
 * - 原厂支持形变（`couiSeekBarDeformation`）、进度闪光（`couiSeekBarShowGlitterEffect`）
 *   与厂商平滑圆角路径，这些依赖 `com.oplus.graphics` 与私有 AGSL，未移植。
 *
 * @param value 当前档位，必须落在 [valueRange] 内。
 * @param onValueChange 拖动、点击与吸附引起的档位变化。
 * @param valueRange 档位范围，每一档对应轨道上的一个标记。
 * @param onValueChangeFinished 抬手或点击过渡结束后的回调。
 */
@Composable
fun SectionSeekBar(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 0..100,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiSectionSeekBarColors = CouiSectionSeekBarDefaults.colors(dark),
    onValueChangeFinished: (() -> Unit)? = null,
) {
    require(!valueRange.isEmpty()) { "valueRange 不能为空" }
    val first = valueRange.first
    val last = valueRange.last
    val sections = last - first
    require(sections > 0) { "valueRange 至少要包含两个档位" }

    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val onChange by rememberUpdatedState(onValueChange)
    val onFinished by rememberUpdatedState(onValueChangeFinished)

    fun fractionOf(progress: Int): Float = (progress - first).toFloat() / sections

    var visual by remember { mutableFloatStateOf(fractionOf(value.coerceIn(first, last))) }
    var interacting by remember { mutableStateOf(false) }

    LaunchedEffect(value, interacting) {
        if (!interacting) visual = fractionOf(value.coerceIn(first, last))
    }

    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = CouiSectionSeekBarDefaults.minHeight)
            .semantics {
                if (!enabled) disabled()
                progressBarRangeInfo = ProgressBarRangeInfo(
                    value.coerceIn(first, last).toFloat(),
                    first.toFloat()..last.toFloat(),
                    sections - 1,
                )
                setProgress { target ->
                    val next = target.roundToInt().coerceIn(first, last)
                    if (next != value) {
                        onChange(next)
                        onFinished?.invoke()
                    }
                    true
                }
            }
            .pointerInput(enabled, sections, isRtl) {
                if (!enabled) return@pointerInput
                val thumbRadiusPx = CouiSectionSeekBarDefaults.thumbRadius.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    var fingerFraction = fractionAt(down.position.x, size.width.toFloat(), thumbRadiusPx, isRtl)
                    var dragging = false
                    var committed = value.coerceIn(first, last)
                    while (true) {
                        val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                        if (change.pressed) {
                            fingerFraction = fractionAt(change.position.x, size.width.toFloat(), thumbRadiusPx, isRtl)
                            if (!dragging) {
                                if (abs(change.position.x - down.position.x) < 1f &&
                                    abs(change.position.y - down.position.y) < 1f
                                ) {
                                    change.consume()
                                    continue
                                }
                                dragging = true
                                interacting = true
                            }
                            visual += (fingerFraction - visual) * CouiSectionSeekBarDefaults.dragMoveRatio
                            visual = visual.coerceIn(0f, 1f)
                            val next = (first + (visual * sections).roundToInt()).coerceIn(first, last)
                            if (next != committed) {
                                committed = next
                                onChange(next)
                                performSectionHaptic(view)
                            }
                            change.consume()
                        } else {
                            break
                        }
                    }
                    interacting = true
                    scope.launch {
                        if (dragging) {
                            val target = fractionOf(committed)
                            animate(visual, target, 0f,
                                tween(CouiSectionSeekBarDefaults.releaseSnapMillis,
                                    easing = CouiSectionSeekBarDefaults.dragSnapEasing)) { animated, _ ->
                                visual = animated
                            }
                            visual = target
                        } else {
                            val target = (first + (fingerFraction * sections).roundToInt()).coerceIn(first, last)
                            val targetFraction = fractionOf(target)
                            animate(visual, targetFraction, 0f,
                                tween(CouiSectionSeekBarDefaults.progressTransitionMillis,
                                    easing = CouiSectionSeekBarDefaults.moveEase)) { animated, _ ->
                                visual = animated
                            }
                            visual = targetFraction
                            if (committed != target) {
                                onChange(target)
                                performSectionHaptic(view)
                            }
                        }
                        interacting = false
                        onFinished?.invoke()
                    }
                }
            },
    ) {
        Canvas(Modifier.fillMaxWidth().heightIn(min = CouiSectionSeekBarDefaults.minHeight)) {
            drawSectionSeekBar(
                fraction = visual,
                isRtl = isRtl,
                sections = sections,
                enabled = enabled,
                density = density,
                colors = colors,
            )
        }
    }
}

/** 手指位置换算为 0f..1f 的轨道比例，两端各留出一个滑块半径。 */
private fun fractionAt(x: Float, width: Float, thumbRadius: Float, isRtl: Boolean): Float {
    val usable = width - thumbRadius * 2f
    if (usable <= 0f) return 0f
    val raw = ((x - thumbRadius) / usable).coerceIn(0f, 1f)
    return if (isRtl) 1f - raw else raw
}

/** 档位变化时的触感反馈，与原厂 `performVibrationForSection` 的分档震动语义一致。 */
private fun performSectionHaptic(view: android.view.View) {
    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
    view.performHapticFeedback(code)
}

/** 绘制轨道、进度、段标记与滑块。 */
private fun DrawScope.drawSectionSeekBar(
    fraction: Float,
    isRtl: Boolean,
    sections: Int,
    enabled: Boolean,
    density: Density,
    colors: CouiSectionSeekBarColors,
) {
    val thumbRadius = with(density) { CouiSectionSeekBarDefaults.thumbRadius.toPx() }
    val trackHeight = with(density) { CouiSectionSeekBarDefaults.trackHeight.toPx() }
    val trackRadius = with(density) { CouiSectionSeekBarDefaults.trackRadius.toPx() }
    val progressHeight = with(density) { CouiSectionSeekBarDefaults.progressHeight.toPx() }
    val markRadius = with(density) { CouiSectionSeekBarDefaults.markRadius.toPx() }

    val start = thumbRadius
    val end = size.width - thumbRadius
    val usable = end - start
    if (usable <= 0f) return
    val centerY = size.height / 2f
    val thumbX = if (isRtl) end - usable * fraction else start + usable * fraction

    drawRoundRect(
        color = if (enabled) colors.track else colors.disabledTrack,
        topLeft = Offset(start, centerY - trackHeight / 2f),
        size = Size(usable, trackHeight),
        cornerRadius = CornerRadius(trackRadius),
    )

    val progressLeft = if (isRtl) thumbX else start
    val progressWidth = if (isRtl) end - thumbX else thumbX - start
    if (progressWidth > 0f) {
        val radius = progressHeight / 2f
        drawRoundRect(
            color = if (enabled) colors.progress else colors.disabledProgress,
            topLeft = Offset(progressLeft, centerY - progressHeight / 2f),
            size = Size(progressWidth.coerceAtLeast(radius * 2f), progressHeight),
            cornerRadius = CornerRadius(radius),
        )
    }

    val activeMark = if (enabled) colors.activeMark else colors.disabledActiveMark
    val inactiveMark = if (enabled) colors.inactiveMark else colors.disabledInactiveMark
    val thumbLeft = thumbX - thumbRadius
    val thumbRight = thumbX + thumbRadius
    for (index in 0..sections) {
        val position = start + usable * index / sections
        val covered = thumbLeft <= position - markRadius && thumbRight >= position + markRadius
        if (covered) continue
        val passed = if (isRtl) position < thumbX else position > thumbX
        drawCircle(if (passed) activeMark else inactiveMark, markRadius, Offset(position, centerY))
    }

    drawCircle(
        color = if (enabled) colors.thumb else colors.disabledThumb,
        radius = thumbRadius,
        center = Offset(thumbX, centerY),
    )
}

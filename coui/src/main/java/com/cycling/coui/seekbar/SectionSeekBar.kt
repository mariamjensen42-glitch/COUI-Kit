package com.cycling.coui.seekbar

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.roundToInt

/** 原厂 `ValueAnimator` 默认 `AccelerateDecelerateInterpolator` 的同一条曲线。 */
private val AccelerateDecelerateEasing: Easing = object : Easing {
    override fun transform(fraction: Float): Float =
        (cos((fraction + 1f) * PI) / 2.0 + 0.5).toFloat()
}

/**
 * 分段滑动条，对应原厂 `com.coui.appcompat.seekbar.COUISectionSeekBar`。
 *
 * 进度按 [valueRange] 的整数档取值，轨道上为每一档画一个段标记，
 * 滑块左侧的标记用未激活色（画在进度填充上），右侧用激活色（画在空轨道上）。
 *
 * 交互按原厂 `SectionAnimationHandler` 移植：
 * - 按下越过 `touchSlop` 后以按下位置确定拖动锚点；
 * - 拖动时按 0.6 跟随 / 0.4 动画混合收敛，跨档以 100ms 中间吸附，方向反转重新锚定；
 * - 抬手按手指最终位置取最近档位，以 150ms 归位；
 * - 未发生拖动时，点击轨道以 200ms 过渡到点击位置对应的档位；
 * - 档位变化触发分档触感反馈。
 *
 * 与原厂的差异：
 * - 原厂支持形变（`couiSeekBarDeformation`）、进度闪光（`couiSeekBarShowGlitterEffect`）
 *   与厂商平滑圆角路径，这些依赖 `com.oplus.graphics` 与私有 AGSL，未移植。
 *
 * @param value 当前档位，必须落在 [valueRange] 内。
 * @param onValueChange 拖动、点击与吸附引起的档位变化。
 * @param valueRange 档位范围，每一档对应轨道上的一个标记。
 * @param onValueChangeFinished 抬手、点击或吸附结束后的回调。
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
    val animationJobs = remember { SectionAnimationJobs() }
    DisposableEffect(animationJobs) {
        onDispose { animationJobs.cancelAll() }
    }

    LaunchedEffect(value, interacting) {
        if (!interacting) visual = fractionOf(value.coerceIn(first, last))
    }
    LaunchedEffect(enabled) {
        if (!enabled) {
            animationJobs.cancelAll()
            interacting = false
        }
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
                val trackStartPx = thumbRadiusPx
                val trackEndPx = size.width.toFloat() - thumbRadiusPx
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    animationJobs.cancelAll()
                    interacting = false
                    val usable = trackEndPx - trackStartPx
                    if (usable <= 0f) return@awaitEachGesture
                    val step = usable / sections

                    /** 手指横坐标换算为轨道内的进度距离；RTL 从右端起算。 */
                    fun trackX(x: Float): Float =
                        (if (isRtl) trackEndPx - x else x - trackStartPx).coerceIn(0f, usable)

                    val downPx = trackX(down.position.x)
                    var fingerPx = downPx
                    var dragging = false
                    var committed = value.coerceIn(first, last)
                    var thumbPx = (visual * usable).coerceIn(0f, usable)
                    var anchorSection = -1
                    var anchorThumbPx = 0f
                    var direction = 0
                    var lastFingerPx = fingerPx
                    var dragCurrentOffset = 0f
                    var dragMoveValue = 0f
                    var snapStartPx = 0f
                    var snapEndPx = Float.NaN
                    var snapRunning = false
                    var fastMoving = false

                    fun setThumb(nextPx: Float) {
                        thumbPx = nextPx.coerceIn(0f, usable)
                        visual = (thumbPx / usable).coerceIn(0f, 1f)
                    }

                    fun calculateDragProgress() {
                        if (snapEndPx.isNaN() || snapEndPx == snapStartPx) return
                        val selected = if (snapEndPx > snapStartPx) {
                            first + (thumbPx / step).roundToInt()
                        } else {
                            first + ceil(thumbPx / step).toInt()
                        }
                        val next = selected.coerceIn(first, last)
                        if (next != committed) {
                            committed = next
                            onChange(next)
                            performSectionHaptic(view)
                        }
                    }

                    fun snapToNearestSection(target: Int) {
                        val targetPx = (target - first) * step
                        if (committed != target) {
                            committed = target
                            onChange(target)
                        }
                        animationJobs.cancelTransition()
                        if (abs(thumbPx - targetPx) < 0.5f) {
                            setThumb(targetPx)
                            interacting = false
                            return
                        }
                        interacting = true
                        animationJobs.transition = scope.launch {
                            try {
                                animate(thumbPx, targetPx, 0f,
                                    tween(CouiSectionSeekBarDefaults.releaseSnapMillis,
                                        easing = AccelerateDecelerateEasing)) { animated, _ ->
                                    setThumb(animated)
                                }
                                setThumb(targetPx)
                            } finally {
                                if (animationJobs.transition === coroutineContext[Job]) {
                                    interacting = false
                                    animationJobs.transition = null
                                }
                            }
                        }
                    }

                    fun handleFastMoveSnap() {
                        val finger = lastFingerPx
                        anchorSection = -1
                        anchorThumbPx = 0f
                        direction = 0
                        dragCurrentOffset = 0f
                        dragMoveValue = 0f
                        snapStartPx = 0f
                        snapEndPx = Float.NaN
                        val target = (first + (finger / usable * sections).roundToInt()).coerceIn(first, last)
                        snapToNearestSection(target)
                        onFinished?.invoke()
                    }

                    fun startDragSnap(targetThumbPx: Float) {
                        if (thumbPx == targetThumbPx) return
                        if (snapRunning && snapEndPx == targetThumbPx) return
                        snapEndPx = targetThumbPx
                        snapStartPx = anchorThumbPx
                        animationJobs.cancelSnap()
                        // 与 ValueAnimator.isRunning 一致：启动即视为运行，抬手事件同步可见。
                        snapRunning = true
                        animationJobs.snap = scope.launch {
                            try {
                                animate(dragMoveValue, targetThumbPx - anchorThumbPx, 0f,
                                    tween(CouiSectionSeekBarDefaults.dragSnapMillis,
                                        easing = CouiSectionSeekBarDefaults.dragSnapEasing)) { value, _ ->
                                    dragMoveValue = value
                                    val blended = dragCurrentOffset * (1f - CouiSectionSeekBarDefaults.dragMoveRatio) +
                                        value * CouiSectionSeekBarDefaults.dragMoveRatio + snapStartPx
                                    setThumb(blended)
                                    calculateDragProgress()
                                }
                            } finally {
                                if (animationJobs.snap === coroutineContext[Job]) {
                                    snapRunning = false
                                    animationJobs.snap = null
                                }
                            }
                            if (fastMoving) {
                                fastMoving = false
                                handleFastMoveSnap()
                            }
                        }
                    }

                    fun initDragAnchor(currentPx: Float) {
                        val anchor = (first + (downPx / step).roundToInt()).coerceIn(first, last)
                        anchorSection = anchor
                        anchorThumbPx = (anchor - first) * step
                        dragMoveValue = 0f
                        if (committed != anchor) {
                            committed = anchor
                            onChange(anchor)
                            performSectionHaptic(view)
                        }
                        direction = if (currentPx < anchorThumbPx) -1 else 1
                        lastFingerPx = currentPx
                        setThumb(anchorThumbPx)
                    }

                    fun trackTouchEvent(currentPx: Float) {
                        fingerPx = currentPx
                        if (anchorSection < first) {
                            initDragAnchor(currentPx)
                            return
                        }
                        val moveDirection = when {
                            currentPx > lastFingerPx + 0.5f -> 1
                            currentPx < lastFingerPx - 0.5f -> -1
                            else -> 0
                        }
                        if (moveDirection != 0 && direction == 0) {
                            direction = moveDirection
                        } else if (moveDirection != 0 && moveDirection == -direction) {
                            direction = moveDirection
                            if (anchorSection != committed) {
                                anchorSection = committed
                                anchorThumbPx = (committed - first) * step
                                dragMoveValue = 0f
                            }
                            animationJobs.cancelSnap()
                            snapRunning = false
                        }
                        val offset = currentPx - anchorThumbPx
                        val biasedOffset = if (offset >= 0f) {
                            CouiSectionSeekBarDefaults.positionBias + offset
                        } else {
                            offset - CouiSectionSeekBarDefaults.positionBias
                        }
                        val crossed = (biasedOffset / step).toInt()
                        val targetOffset = crossed * step
                        dragCurrentOffset = offset
                        if ((anchorSection + crossed).coerceIn(first, last) != committed) {
                            startDragSnap(anchorThumbPx + targetOffset)
                        } else if (!snapRunning) {
                            setThumb(anchorThumbPx + targetOffset +
                                (offset - targetOffset) * (1f - CouiSectionSeekBarDefaults.dragMoveRatio))
                        }
                        lastFingerPx = currentPx
                    }

                    while (true) {
                        val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                        // 抬手坐标同样写入 fingerPx，供点击目标与终止位置使用。
                        fingerPx = trackX(change.position.x)
                        if (!change.pressed) break
                        if (!dragging) {
                            if (abs(change.position.x - down.position.x) <= viewConfiguration.touchSlop) {
                                change.consume()
                                continue
                            }
                            dragging = true
                            interacting = true
                            initDragAnchor(fingerPx)
                            change.consume()
                            continue
                        }
                        trackTouchEvent(fingerPx)
                        change.consume()
                    }

                    if (dragging) {
                        // 原厂在抬手事件上再执行一次 track，用抬手坐标作为最终位置。
                        trackTouchEvent(fingerPx)
                        if (snapRunning) {
                            fastMoving = true
                        } else {
                            val target = (committed +
                                ((fingerPx - (committed - first) * step) / step).toInt())
                                .coerceIn(first, last)
                            snapToNearestSection(target)
                            onFinished?.invoke()
                        }
                    } else {
                        // 未拖动时以当前显示位置为动画起点，和外部进度回写保持一致。
                        thumbPx = (visual * usable).coerceIn(0f, usable)
                        val target = (first + (fingerPx / step).roundToInt()).coerceIn(first, last)
                        val targetPx = (target - first) * step
                        if (committed != target) {
                            committed = target
                            onChange(target)
                            performSectionHaptic(view)
                        }
                        if (abs(thumbPx - targetPx) < 0.5f) {
                            setThumb(targetPx)
                            onFinished?.invoke()
                        } else {
                            animationJobs.cancelTransition()
                            interacting = true
                            animationJobs.transition = scope.launch {
                                try {
                                    animate(thumbPx, targetPx, 0f,
                                        tween(CouiSectionSeekBarDefaults.progressTransitionMillis,
                                            easing = AccelerateDecelerateEasing)) { animated, _ ->
                                        setThumb(animated)
                                    }
                                    setThumb(targetPx)
                                    onFinished?.invoke()
                                } finally {
                                    if (animationJobs.transition === coroutineContext[Job]) {
                                        interacting = false
                                        animationJobs.transition = null
                                    }
                                }
                            }
                        }
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

/** 保存跨手势的动画任务，新手势进入或组件禁用时统一取消。 */
private class SectionAnimationJobs {
    var snap: Job? = null
    var transition: Job? = null

    fun cancelSnap() {
        snap?.cancel()
        snap = null
    }

    fun cancelTransition() {
        transition?.cancel()
        transition = null
    }

    fun cancelAll() {
        cancelSnap()
        cancelTransition()
    }
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
            size = Size(progressWidth, progressHeight),
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

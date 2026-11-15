package com.cycling.coui.seekbar

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 竖向滑动条，对应原厂 `com.coui.appcompat.seekbar.COUIVerticalSeekBar`。
 *
 * 几何与配色沿用原厂资源：轨道是细圆角竖条，进度自底部向上填充到滑块位置，
 * 滑块是覆盖在轨道上的圆点；拖动开始后轨道与进度同时变宽、圆角同步放大
 * （对应原厂 `mBackgroundEnlargeScale`、`mProgressEnlargeScale` 与 `mTouchEnlargeAnimator`），抬手后复原。
 *
 * 交互与原厂一致的部分：
 * - 拖动时进度跟随手指，位移按原厂 `calculateDamping()` 的 0.4 系数平滑收敛，
 *   手指抖动不会直接反映到进度上。
 * - 抬手时速度超过 100px/s 才继续惯性滑动，衰减系数取原厂 `mFlingLinearDamping` 的 15。
 * - 点击轨道按点击位置跳转，跳转时长取 `|Δ| * 483ms`，最短 150ms。
 * - 进度跨越档位时触发触感反馈，对应原厂 `performFeedback()` 的分档震动。
 * - 无障碍暴露进度范围、当前百分比与禁用态。
 *
 * 与原厂的差异：
 * - 原厂抬手惯性由 `com.oplus.physicsengine` 的 `FlingBehavior` 驱动（频率 2.8、
 *   阻尼比 1.0、线性阻尼 15），该实现不在公开 API 范围内，这里用同一阻尼系数的
 *   单轴衰减复现可见行为，到达边界即停止，不做原厂的边界回弹。
 * - 原厂支持形变（`couiVerticalSeekBarDeformation`）、线性马达分档震动
 *   与厂商平滑圆角路径，这些依赖厂商私有接口，这里只保留按压变宽与普通触感反馈。
 *
 * @param value 当前进度，0f..1f，底部为 0。
 * @param onValueChange 拖动、点击与惯性滑动引起的进度变化。
 * @param label 轨道上方的文字标签，对应原厂 `couiVerticalSeekBarText`。
 */
@Composable
fun VerticalSeekBar(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String? = null,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiVerticalSeekBarColors = CouiVerticalSeekBarDefaults.colors(dark),
) {
    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val fraction = value.coerceIn(0f, 1f)
    val onChange by rememberUpdatedState(onValueChange)
    // 显示值与业务值分离：拖动与动画期间显示值自行推进，外部值变化时再同步。
    var visual by remember { mutableFloatStateOf(fraction) }
    var interacting by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    val enlarge by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(CouiVerticalSeekBarDefaults.enlargeMillis),
        label = "couiVerticalSeekBarEnlarge",
    )

    LaunchedEffect(fraction, interacting) {
        if (!interacting) visual = fraction
    }

    Box(
        modifier
            .width(CouiVerticalSeekBarDefaults.minWidth)
            .heightIn(max = CouiVerticalSeekBarDefaults.maxHeight)
            .semantics {
                if (!enabled) disabled()
                progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
                stateDescription = "${(fraction * 100).roundToInt()}%"
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                val tracker = VelocityTracker.obtain()
                // 原厂 trackTouchEventByFinger 用 0.4 的阻尼系数收敛手指位移，这里沿用同一系数。
                val damping = 0.4f
                try {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val downTime = down.uptimeMillis
                        tracker.clear()
                        tracker.addChange(down, downTime)
                        var smoothedY = down.position.y
                        var upY = down.position.y
                        var dragging = false
                        var hapticStep = (visual * 20).roundToInt()
                        down.consume()
                        while (true) {
                            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                            tracker.addChange(change, downTime)
                            upY = change.position.y
                            if (!change.pressed) break
                            val usable = size.height.toFloat() -
                                VerticalSeekBarGeometry.paddingVertical(density) * 2f
                            if (usable <= 0f) {
                                change.consume()
                                continue
                            }
                            smoothedY -= (smoothedY - change.position.y) * damping
                            dragging = true
                            interacting = true
                            pressed = true
                            val next = VerticalSeekBarGeometry.progressOf(size.height.toFloat(), smoothedY, usable)
                            visual = next
                            onChange(next)
                            // 原厂按进度分档震动，这里按 5% 一档触发，避免逐帧连续震动。
                            val step = (next * 20).roundToInt()
                            if (step != hapticStep) {
                                hapticStep = step
                                performSeekHaptic(view)
                            }
                            change.consume()
                        }
                        tracker.computeCurrentVelocity(1000, 8000f)
                        val velocity = tracker.yVelocity
                        val usable = size.height.toFloat() -
                            VerticalSeekBarGeometry.paddingVertical(density) * 2f
                        if (!dragging) {
                            if (usable > 0f) {
                                // 未发生拖动按点击处理：跳到点击位置，位移越大动画越长。
                                val target = VerticalSeekBarGeometry.progressOf(size.height.toFloat(), upY, usable)
                                val duration = (abs(target - visual) *
                                    CouiVerticalSeekBarDefaults.clickDurationFactor).toInt()
                                    .coerceAtLeast(CouiVerticalSeekBarDefaults.clickMinMillis)
                                scope.launch {
                                    interacting = true
                                    animate(visual, target, 0f, tween(duration)) { animated, _ ->
                                        visual = animated
                                        onChange(animated)
                                    }
                                    interacting = false
                                }
                            }
                        } else if (abs(velocity) >= CouiVerticalSeekBarDefaults.flingThreshold && usable > 0f) {
                            scope.launch {
                                interacting = true
                                fling(velocity, visual, usable) { next ->
                                    visual = next
                                    onChange(next)
                                }
                                interacting = false
                            }
                        } else {
                            interacting = false
                        }
                        pressed = false
                    }
                } finally {
                    tracker.recycle()
                    pressed = false
                    interacting = false
                }
            },
    ) {
        val labelColor = CouiTheme.colorSchemeFor(dark).onSurface
        Canvas(Modifier.fillMaxHeight()) {
            drawVerticalSeekBar(
                fraction = visual,
                pressed = enlarge,
                enabled = enabled,
                density = density,
                colors = colors,
                label = label,
                labelColor = labelColor,
            )
        }
    }
}

/** 轨道几何的统一取值，避免绘制与手势各算一份。 */
private object VerticalSeekBarGeometry {
    fun paddingVertical(density: Density): Float =
        with(density) { CouiVerticalSeekBarDefaults.paddingVertical.toPx() }

    /** 手指位置换算为进度：底部为 0，顶部为 1。 */
    fun progressOf(height: Float, y: Float, usable: Float): Float {
        val padding = (height - usable) / 2f
        return ((height - padding - y) / usable).coerceIn(0f, 1f)
    }
}

/**
 * 抬手惯性。
 *
 * 原厂使用厂商物理引擎的 `FlingBehavior`（频率 2.8、阻尼比 1.0、线性阻尼 15），
 * 公开 API 无法复现同一积分器，这里用相同的线性阻尼系数做单轴衰减，边界处停止。
 */
private suspend fun fling(
    initialVelocity: Float,
    startFraction: Float,
    usable: Float,
    onFrame: (Float) -> Unit,
) {
    var speed = initialVelocity
    var fraction = startFraction
    var previous = 0L
    var running = true
    while (running) {
        withFrameNanos { time ->
            if (previous != 0L) {
                val seconds = (time - previous) / 1_000_000_000f
                speed /= 1f + CouiVerticalSeekBarDefaults.flingDamping * seconds
                // 手指向下为正速度，进度随之下降。
                fraction = (fraction - speed * seconds / usable).coerceIn(0f, 1f)
                onFrame(fraction)
                running = fraction > 0f && fraction < 1f && abs(speed) >= 0.1f
            }
            previous = time
        }
    }
}

/** 进度跨档时的触感反馈，与原厂 `performFeedback()` 的分档震动语义一致。 */
private fun performSeekHaptic(view: View) {
    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
    view.performHapticFeedback(code)
}

/** 绘制轨道、进度、滑块与上方文字。 */
private fun DrawScope.drawVerticalSeekBar(
    fraction: Float,
    pressed: Float,
    enabled: Boolean,
    density: Density,
    colors: CouiVerticalSeekBarColors,
    label: String?,
    labelColor: Color,
) {
    val paddingVertical = with(density) { CouiVerticalSeekBarDefaults.paddingVertical.toPx() }
    val top = paddingVertical
    val bottom = size.height - paddingVertical
    val usable = bottom - top
    if (usable <= 0f) return
    val centerX = size.width / 2f

    val trackEnlarge = 1f + (CouiVerticalSeekBarDefaults.trackEnlargeScale - 1f) * pressed
    val progressEnlarge = 1f + (CouiVerticalSeekBarDefaults.progressEnlargeScale - 1f) * pressed
    val trackWidth = with(density) { CouiVerticalSeekBarDefaults.trackWidth.toPx() } * trackEnlarge
    val progressWidth = with(density) { CouiVerticalSeekBarDefaults.progressWidth.toPx() } * progressEnlarge
    val trackRadius = with(density) { CouiVerticalSeekBarDefaults.trackRadius.toPx() } * trackEnlarge
    val progressRadius = with(density) { CouiVerticalSeekBarDefaults.progressRadius.toPx() } * progressEnlarge
    val thumbSize = with(density) { CouiVerticalSeekBarDefaults.thumbSize.toPx() }

    drawRoundedBar(colors.track, trackWidth, trackRadius, centerX, top, bottom)
    val thumbCenterY = bottom - usable * fraction
    val progressColor = if (enabled) colors.progress else colors.disabledProgress
    val thumbColor = if (enabled) colors.thumb else colors.disabledThumb
    drawRoundedBar(progressColor, progressWidth, progressRadius, centerX, thumbCenterY, bottom)
    drawCircle(thumbColor, thumbSize / 2f, Offset(centerX, thumbCenterY))

    if (label.isNullOrEmpty()) return
    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = labelColor.toArgb()
        textSize = with(density) { 12.sp.toPx() }
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val metrics = textPaint.fontMetrics
    // 原厂把文字画在轨道上方的 marginTop 处，这里同样贴在轨道顶端之上。
    val baseline = top - paddingVertical / 2f - (metrics.ascent + metrics.descent) / 2f
    drawContext.canvas.nativeCanvas.drawText(label, centerX, baseline, textPaint)
}

/** 竖条用圆角矩形绘制；半径随宽度收敛，避免圆角超过半宽。 */
private fun DrawScope.drawRoundedBar(
    color: Color,
    width: Float,
    radius: Float,
    centerX: Float,
    top: Float,
    bottom: Float,
) {
    val half = width / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(centerX - half, top),
        size = Size(width, (bottom - top).coerceAtLeast(0f)),
        cornerRadius = CornerRadius(radius.coerceAtMost(half)),
    )
}

/**
 * PointerInputChange 不是 MotionEvent，采样历史点时按原厂同一口径补齐。
 * `downTime` 必须固定为按下时间，否则 VelocityTracker 会把每个采样点当成新手势起点。
 */
private fun VelocityTracker.addChange(change: PointerInputChange, downTime: Long) {
    for (sample in change.historical) {
        val event = MotionEvent.obtain(downTime, sample.uptimeMillis, MotionEvent.ACTION_MOVE,
            sample.position.x, sample.position.y, 0)
        try {
            addMovement(event)
        } finally {
            event.recycle()
        }
    }
    // 与库内 CouiVelocityTracker 一致：DOWN 按下、UP 抬手、其余为 MOVE。
    val action = when {
        !change.previousPressed -> MotionEvent.ACTION_DOWN
        !change.pressed -> MotionEvent.ACTION_UP
        else -> MotionEvent.ACTION_MOVE
    }
    val event = MotionEvent.obtain(downTime, change.uptimeMillis, action,
        change.position.x, change.position.y, 0)
    try {
        addMovement(event)
    } finally {
        event.recycle()
    }
}

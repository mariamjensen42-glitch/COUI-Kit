package com.cycling.coui.progress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * 分数环，对应 ColorOS 17 手机管家首页的 `ScanCircleView`。
 *
 * 弧长按 [score] / [CouiScanRingDefaults.maxScore] 换算，从 6 点方向顺时针绘制。
 * 弧的填充不是单一颜色，而是沿角度分布的五段 `SweepGradient`：绿色板
 * `#40DC30 → #00D616 → #40DC30`（停位 `.2/.45/.7`），红色板
 * `#FF851B → #FC701F → #FF851B`（停位 `.2/.5/.85`）。低于 [redThreshold] 转红是立即的，
 * 转回绿色按原厂 600ms + 300ms 延迟逐段插值颜色与停位。
 *
 * [scanning] 为真时叠加扫描态：把五阶色板按 `[0,1,2,2,3,2,1,0]` 镜像成八阶，绕环
 * [CouiScanRingDefaults.scanRotationMillis] 一圈匀速旋转。置为假时按剩余角度补完当前一圈
 * 再归零，时长按剩余角度换算并夹在 500–5000ms，曲线为 `PathInterpolator(0.17, 0.17, 0.1, 0.1)`
 * ——与原厂 `showOrDismissSelfRotation` 的停止分支一致，不会半途跳变。
 *
 * [content] 在环心居中绘制，用于分数、单位等读数。
 *
 * 与原厂的差异：原厂的外发光底图、中心图标与外侧扩散波纹是 `main_circle_*.webp` 位图
 * （`ScanCircleWaveView` 逐帧缩放同一张图、`0.8 → 1.3`、末段 600ms 内 alpha `1 → 0.2`），
 * 本组件只绘制环本身，位图部分由宿主在自己的背景层给出。
 */
@Composable
fun ScanRing(
    modifier: Modifier = Modifier,
    score: Int,
    scanning: Boolean = false,
    size: Dp = CouiScanRingDefaults.size,
    strokeWidth: Dp = CouiScanRingDefaults.strokeWidth,
    redThreshold: Int = CouiScanRingDefaults.redThreshold,
    animateScore: Boolean = true,
    greenPalette: CouiScanRingPalette = CouiScanRingDefaults.green,
    redPalette: CouiScanRingPalette = CouiScanRingDefaults.red,
    onScoreChange: ((Int) -> Unit)? = null,
    onScoreAnimationEnd: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    require(greenPalette.colors.size == redPalette.colors.size) { "两套色板的停位数量必须一致" }
    val target = score.coerceIn(0, CouiScanRingDefaults.maxScore)
    val targetGreen = target >= redThreshold
    val currentScoreChange by rememberUpdatedState(onScoreChange)
    val currentScoreEnd by rememberUpdatedState(onScoreAnimationEnd)

    // 分数：默认 2000ms 缓动；目标与当前一致时不动画，也不触发结束回调（原厂 same-score 分支）。
    val animatedScore = remember { Animatable(target.toFloat()) }
    LaunchedEffect(target, animateScore) {
        val to = target.toFloat()
        if (!animateScore) {
            animatedScore.snapTo(to)
            return@LaunchedEffect
        }
        if (animatedScore.value != to) {
            animatedScore.animateTo(
                to,
                tween(CouiScanRingDefaults.scoreAnimationMillis, easing = CouiScanRingDefaults.moveEase),
            )
            currentScoreEnd?.invoke()
        }
    }
    LaunchedEffect(animatedScore) {
        snapshotFlow { animatedScore.value.roundToInt().coerceIn(0, CouiScanRingDefaults.maxScore) }
            .distinctUntilChanged()
            .collect { currentScoreChange?.invoke(it) }
    }

    // 配色：1 = 绿、0 = 红。原厂只在回到绿色时插值，转红立即生效；无动画模式两者都直接落位。
    val colorMix = remember { Animatable(if (targetGreen) 1f else 0f) }
    LaunchedEffect(targetGreen, animateScore) {
        when {
            !animateScore -> colorMix.snapTo(if (targetGreen) 1f else 0f)
            !targetGreen -> colorMix.snapTo(0f)
            colorMix.value != 1f -> colorMix.animateTo(
                1f,
                tween(
                    CouiScanRingDefaults.colorChangeMillis,
                    delayMillis = CouiScanRingDefaults.colorChangeDelayMillis,
                    easing = CouiScanRingDefaults.moveEase,
                ),
            )
        }
    }

    // 扫描相位：0..1 表示一圈的进度。停止时补完剩余角度再归零。
    val scanPhase = remember { Animatable(0f) }
    LaunchedEffect(scanning) {
        if (scanning) {
            while (true) {
                scanPhase.snapTo(0f)
                scanPhase.animateTo(
                    1f,
                    tween(CouiScanRingDefaults.scanRotationMillis, easing = LinearEasing),
                )
            }
        } else if (scanPhase.value > 0f) {
            val remaining = ((1f - scanPhase.value) * CouiScanRingDefaults.scanRotationMillis)
                .roundToInt()
                .coerceIn(CouiScanRingDefaults.scanStopMinMillis, CouiScanRingDefaults.scanStopMaxMillis)
            scanPhase.animateTo(
                1f,
                tween(remaining, easing = CouiScanRingDefaults.settleEase),
            )
            scanPhase.snapTo(0f)
        }
    }

    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val stroke = strokeWidth.toPx().coerceAtMost(this.size.minDimension / 2f)
            if (stroke <= 0f) return@Canvas
            val diameter = this.size.minDimension - stroke
            if (diameter <= 0f) return@Canvas
            val topLeft = Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f,
            )
            val sweep = (animatedScore.value / CouiScanRingDefaults.maxScore * 360f).coerceIn(0f, 360f)
            if (sweep <= 0f) return@Canvas

            val mix = colorMix.value
            val colors = List(greenPalette.colors.size) { index ->
                lerp(redPalette.colors[index], greenPalette.colors[index], mix)
            }
            val positions = List(greenPalette.colors.size) { index ->
                val red = redPalette.positions[index]
                red + (greenPalette.positions[index] - red) * mix
            }
            // 扫描态换成八阶镜像色板，静止态用五阶色板；两者的相位都含画布矩阵的 90° 旋转。
            val scanActive = scanning || scanPhase.value > 0f
            val (stopColors, stopPositions) = if (scanActive) {
                val mirrored = CouiScanRingDefaults.scanPaletteIndices.map { colors[it] }
                val last = (mirrored.size - 1).toFloat().coerceAtLeast(1f)
                mirrored to List(mirrored.size) { it / last }
            } else {
                colors to positions
            }
            val phase = CouiScanRingDefaults.gradientRotationDegrees / 360f +
                if (scanActive) scanPhase.value else 0f
            val brush = Brush.sweepGradient(
                *sweepStops(stopColors, stopPositions, phase).toTypedArray(),
                center = Offset(topLeft.x + diameter / 2f, topLeft.y + diameter / 2f),
            )
            val style = Stroke(width = stroke, cap = StrokeCap.Round)
            if (sweep >= FullCircleDegrees) {
                drawOval(brush, topLeft, Size(diameter, diameter), style = style)
            } else {
                val path = Path().apply {
                    arcTo(
                        Rect(topLeft, Size(diameter, diameter)),
                        CouiScanRingDefaults.startAngleDegrees,
                        sweep,
                        true,
                    )
                }
                drawPath(path, brush, style = style)
            }
        }
        content()
    }
}

private const val FullCircleDegrees = 360f

/**
 * 把角度渐变的停位整体平移 [phase]（0..1 的一圈比例）。原厂旋转的是 `SweepGradient` 的
 * 局部矩阵，这里平移停位得到同一结果：0 与 1 处改用平移后的插值颜色补齐，避免出现接缝。
 * 色板首尾同色，因此平移不改变端点语义。
 */
private fun sweepStops(
    colors: List<Color>,
    positions: List<Float>,
    phase: Float,
): List<Pair<Float, Color>> {
    val shift = phase - floor(phase)
    val boundary = interpolateAt(colors, positions, 1f - shift)
    val stops = ArrayList<Pair<Float, Color>>(colors.size + 2)
    stops += 0f to boundary
    colors.indices.forEach { index ->
        val at = (positions[index] + shift) % 1f
        if (at > 0f && at < 1f) stops += at to colors[index]
    }
    stops += 1f to boundary
    return stops.sortedBy { it.first }.distinctBy { it.first }
}

/** 在色板上按 0..1 位置取插值颜色；用于补齐旋转后的 0/1 端点。 */
private fun interpolateAt(colors: List<Color>, positions: List<Float>, at: Float): Color {
    if (at <= positions.first()) return colors.first()
    if (at >= positions.last()) return colors.last()
    for (index in 1 until positions.size) {
        if (at <= positions[index]) {
            val span = positions[index] - positions[index - 1]
            if (span <= 0f) return colors[index]
            return lerp(colors[index - 1], colors[index], (at - positions[index - 1]) / span)
        }
    }
    return colors.last()
}

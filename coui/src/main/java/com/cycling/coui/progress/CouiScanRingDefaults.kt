package com.cycling.coui.progress

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 分数环的一段渐变色板：沿弧的角度停位与对应颜色。
 *
 * [positions] 必须从 0 递增到 1，且首尾两项颜色相同——原厂 `SweepGradient` 靠这一对称
 * 让旋转时在 0/1 处不出现接缝。
 */
@Immutable
class CouiScanRingPalette(
    val colors: List<Color>,
    val positions: List<Float>,
) {
    init {
        require(colors.size == positions.size && colors.size >= 2) { "颜色与停位数量必须一致且至少两项" }
        require(positions.first() == 0f && positions.last() == 1f) { "停位必须从 0 到 1" }
        require(colors.first() == colors.last()) { "首尾颜色必须相同，旋转时才没有接缝" }
    }
}

/**
 * 分数环的固定参数，取自 ColorOS 17 手机管家首页的
 * `com.oplus.phonemanager.common.view.ScanCircleView`：
 * 172dp 环、22dp 描边、70 分阈值、2000ms 分数动画、5000ms 一圈的扫描色带。
 */
object CouiScanRingDefaults {
    /** `@dimen/main_circle_width`。 */
    val size = 172.dp

    /** `@dimen/circle_stroke_width`。 */
    val strokeWidth = 22.dp

    /** 满分 `MAX_SCORE = 100`。 */
    const val maxScore = 100

    /** `redScoreThreshold` 默认 70：低于该分数转红。 */
    const val redThreshold = 70

    /**
     * 弧的起始角。原厂 `startAngle = 0` 经 `matrix.setRotate(90f, cx, cy)` 旋转画布，
     * 等价于从 6 点方向开始顺时针绘制。
     */
    const val startAngleDegrees = 90f

    /** 画布矩阵对渐变的额外旋转：`DEFAULT_ROTATE_DEGREE = 90f`。 */
    const val gradientRotationDegrees = 90f

    /** `degreeAnimation` 默认时长 2000ms，插值器为 `COUIMoveEaseInterpolator`。 */
    const val scoreAnimationMillis = 2000

    /** 扫描色带一圈 `SELF_ROTATION_ANIM_DURATION = 5000ms`，线性。 */
    const val scanRotationMillis = 5000

    /** 停止扫描后补完剩余角度：时长按剩余角度换算，再夹在 500–5000ms。 */
    const val scanStopMinMillis = 500
    const val scanStopMaxMillis = 5000

    /** 由绿转红的颜色过渡：600ms，延迟 300ms（`CIRCLE_COLOR_CHANGE_ANIMATION_*`）。 */
    const val colorChangeMillis = 600
    const val colorChangeDelayMillis = 300

    /** `com.coui.appcompat.animation.e` = `PathInterpolator(0.3, 0, 0.1, 1)`。 */
    val moveEase = CubicBezierEasing(.3f, 0f, .1f, 1f)

    /** 停止扫描的补完曲线 `PathInterpolator(0.17, 0.17, 0.1, 0.1)`。 */
    val settleEase = CubicBezierEasing(.17f, .17f, .1f, .1f)

    /**
     * 绿色色板：`main_circle_green_color3` / `main_circle_green_color1` 组成的
     * `[#40DC30, #00D616, #00D616, #00D616, #40DC30]`，停位 `GREEN_SWEEP_COLOR_POSITIONS`。
     */
    val green = CouiScanRingPalette(
        colors = listOf(Color(0xFF40DC30), Color(0xFF00D616), Color(0xFF00D616), Color(0xFF00D616), Color(0xFF40DC30)),
        positions = listOf(0f, .2f, .45f, .7f, 1f),
    )

    /**
     * 红色色板：`main_circle_red_color0/1/2` 组成的
     * `[#FF851B, #FF851B, #FC701F, #FC701F, #FF851B]`，停位 `RED_SWEEP_COLOR_POSITIONS`。
     */
    val red = CouiScanRingPalette(
        colors = listOf(Color(0xFFFF851B), Color(0xFFFF851B), Color(0xFFFC701F), Color(0xFFFC701F), Color(0xFFFF851B)),
        positions = listOf(0f, .2f, .5f, .85f, 1f),
    )

    /**
     * 扫描态的 8 阶镜像色板：原厂 `SCAN_SWEEP_FROM_PALETTE_INDEX = [0, 1, 2, 2, 3, 2, 1, 0]`
     * 按索引从 5 阶色板取值，再以 `SweepGradient(colors, null)` 均分到 0..1。
     */
    val scanPaletteIndices = listOf(0, 1, 2, 2, 3, 2, 1, 0)
}

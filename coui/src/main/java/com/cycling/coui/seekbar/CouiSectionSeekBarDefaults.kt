package com.cycling.coui.seekbar

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/**
 * 分段滑动条的固定参数。
 *
 * 取值来自 ColorOS 17 的 `coui_seekbar_*` 与 `COUISectionSeekBar` 样式：
 * - `coui_seekbar_background_height` / `coui_seekbar_progress_height` 是轨道与进度高度；
 * - `coui_seekbar_background_radius` 是轨道圆角；
 * - `coui_seekbar_thumb_out_radius` 是滑块半径；
 * - `coui_section_seekbar_tick_mark_radius` 是段标记半径；
 * - `couiSeekBarMinHeight` 是控件最小高度。
 *
 * 动画常量对应原厂 `SectionAnimationHandler`：拖动跟手系数 0.4、拖动吸附 100ms、
 * 抬手吸附 150ms、点击过渡 200ms。
 */
object CouiSectionSeekBarDefaults {
    /** `couiSeekBarMinHeight`：控件最小高度。 */
    val minHeight: Dp = 36.dp

    /** 轨道高度，对应 `coui_seekbar_background_height`。 */
    val trackHeight: Dp = 20.dp

    /** 轨道圆角，对应 `coui_seekbar_background_radius`。 */
    val trackRadius: Dp = 2.dp

    /** 进度高度，对应 `coui_seekbar_progress_height`。 */
    val progressHeight: Dp = 20.dp

    /** 滑块半径，对应 `coui_seekbar_thumb_out_radius`。 */
    val thumbRadius: Dp = 6.dp

    /** 段标记半径，对应 `coui_section_seekbar_tick_mark_radius`。 */
    val markRadius: Dp = 3.dp

    /** 拖动跟手系数，对应原厂 `DRAG_MOVE_RATIO`。 */
    const val dragMoveRatio = 0.4f

    /** 位置偏置，对应原厂 `POSITION_BIAS`。 */
    const val positionBias = 0.1f

    /** 速度采样时长，对应原厂 `VELOCITY_COMPUTE_TIME`。 */
    const val velocityComputeTime = 100

    /** 拖动吸附时长，对应原厂 `DRAG_SNAP_DURATION`。 */
    const val dragSnapMillis = 100

    /** 抬手吸附时长，对应原厂 `RELEASE_SNAP_DURATION`。 */
    const val releaseSnapMillis = 150

    /** 点击过渡时长，对应原厂 `PROGRESS_TRANSITION_DURATION`。 */
    const val progressTransitionMillis = 200

    /** 拖动吸附曲线，对应原厂 `PathInterpolator(0, 0, 0.25, 1)`。 */
    val dragSnapEasing: CubicBezierEasing = CubicBezierEasing(0f, 0f, 0.25f, 1f)

    /** 点击过渡曲线，对应原厂 `COUIMoveEaseInterpolator`。 */
    val moveEase: CubicBezierEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /**
     * 日间与夜间配色。
     *
     * 原厂 `COUISectionSeekBar` 沿用 `COUISeekBar` 样式：轨道取 `?couiColorContainer12`，
     * 进度取 `?couiColorContainerTheme`，滑块取 `coui_color_white`；
     * 段标记的 `coui_seekbar_active_mark_selector` 是 `?couiColorLabelOnColor`，
     * `coui_seekbar_inactive_mark_selector` 是 `?couiColorLabelTertiary`。
     *
     * 原厂的激活标记画在滑块右侧（空轨道上），未激活标记画在滑块左侧（进度填充上），
     * 两者的差别只是与底色的对比度，这里保持同一分工。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiSectionSeekBarColors {
        val accent = CouiTheme.colorSchemeFor(dark).accent
        return CouiSectionSeekBarColors(
            track = if (dark) Color(0x33FFFFFF) else Color(0x1F000000),
            progress = accent,
            thumb = Color.White,
            activeMark = Color.White,
            inactiveMark = if (dark) Color(0x4DFFFFFF) else Color(0x42000000),
            disabledTrack = if (dark) Color(0x33FFFFFF) else Color(0x1F000000),
            disabledProgress = accent.copy(alpha = 0.3f),
            disabledThumb = Color.White.copy(alpha = 0.3f),
            disabledActiveMark = if (dark) Color(0x29FFFFFF) else Color(0x8AFFFFFF),
            disabledInactiveMark = if (dark) Color(0x29FFFFFF) else Color(0x42000000),
        )
    }
}

/** 分段滑动条的轨道、进度、滑块与段标记配色。 */
@Immutable
data class CouiSectionSeekBarColors(
    /** 轨道底色，对应 `?couiColorContainer12`。 */
    val track: Color,
    /** 进度色，对应 `?couiColorContainerTheme`。 */
    val progress: Color,
    /** 滑块色，对应 `coui_color_white`。 */
    val thumb: Color,
    /** 滑块右侧段标记色，对应 `coui_seekbar_active_mark_selector`。 */
    val activeMark: Color,
    /** 滑块左侧段标记色，对应 `coui_seekbar_inactive_mark_selector`。 */
    val inactiveMark: Color,
    /** 禁用态轨道色。 */
    val disabledTrack: Color,
    /** 禁用态进度色，对应 `?couiColorDisable`。 */
    val disabledProgress: Color,
    /** 禁用态滑块色，对应 `coui_seekbar_thumb_disable_color`。 */
    val disabledThumb: Color,
    /** 禁用态激活标记色，对应 `coui_seekbar_active_mark_disable_color`。 */
    val disabledActiveMark: Color,
    /** 禁用态未激活标记色，对应 `coui_seekbar_inactive_mark_disable_color`。 */
    val disabledInactiveMark: Color,
)

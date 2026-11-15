package com.itos.coui.seekbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/**
 * 竖向滑动条的固定参数。
 *
 * 取值来自 ColorOS 17 的 `coui_vertical_seekbar_*` 与 `coui_seekbar_*` 资源，
 * 对应原厂 `com.coui.appcompat.seekbar.COUIVerticalSeekBar`：
 * - `coui_vertical_seekbar_view_min_width` 是控件最小宽度；
 * - `coui_vertical_seekbar_view_max_height` 是控件最大高度；
 * - `coui_vertical_seekbar_progress_padding_vertical` 是轨道上下留白；
 * - `coui_seekbar_background_radius` 与 `coui_seekbar_progress_radius` 是轨道与进度圆角，
 *   宽度取圆角的两倍，与原厂 `mBackgroundWidth = mBackgroundRadius * 2` 一致；
 * - `mThumbOutWidth = mProgressWidth * mProgressEnlargeScale` = 16dp，
 *   `mThumbOutRadius = mProgressRadius * mProgressRadiusEnlargeScale` = 8dp，
 *   圆角等于半宽，因此滑块画成直径 16dp 的圆，与原厂 `drawRoundRect` 视觉一致。
 */
object CouiVerticalSeekBarDefaults {
    /** `coui_vertical_seekbar_view_min_width`。 */
    val minWidth: Dp = 36.dp

    /** `coui_vertical_seekbar_view_max_height`。 */
    val maxHeight: Dp = 480.dp

    /** `coui_vertical_seekbar_progress_padding_vertical`：轨道上下留白。 */
    val paddingVertical: Dp = 10.dp

    /** `coui_seekbar_background_radius`：轨道圆角。 */
    val trackRadius: Dp = 2.dp

    /** `coui_seekbar_progress_radius`：进度圆角。 */
    val progressRadius: Dp = 2.dp

    /** 轨道宽度：原厂 `mBackgroundWidth = mBackgroundRadius * 2`。 */
    val trackWidth: Dp = 4.dp

    /** 进度宽度：原厂 `mProgressWidth = mProgressRadius * 2`。 */
    val progressWidth: Dp = 4.dp

    /** 滑块直径，原厂 `mThumbOutWidth`。 */
    val thumbSize: Dp = 16.dp

    /** 按下时轨道宽度的放大倍率，对应原厂 `BACKGROUND_RADIUS_SCALE`。 */
    const val trackEnlargeScale = 6f

    /** 按下时进度宽度的放大倍率，对应原厂 `PROGRESS_RADIUS_SCALE`。 */
    const val progressEnlargeScale = 4f

    /** 抬手后仍按惯性滑动的最小速度，对应原厂 `PHYSICAL_VELOCITY_LIMIT` 附近的 100px/s 判定。 */
    const val flingThreshold = 100f

    /** 惯性衰减，对应原厂 `mFlingLinearDamping`。 */
    const val flingDamping = 15f

    /** 点击跳转的最短时长，对应原厂 `DURATION_150`。 */
    const val clickMinMillis = 150

    /** 点击跳转的时长系数，对应原厂 `DURATION_483`。 */
    const val clickDurationFactor = 483f

    /** 按压变宽与回弹的时长，对应原厂 `TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION`。 */
    const val enlargeMillis = 183

    /**
     * 日间与夜间配色。
     *
     * 原厂 style `COUIVerticalSeekBar` 把背景与进度指向
     * `coui_seekbar_background_selector` 与 `coui_seekbar_progress_selector`：
     * - 启用态背景解析为 `?couiColorContainer12`（`#1F000000` / 夜间 `#33FFFFFF`）；
     * - 启用态进度与滑块解析为 `?couiColorContainerTheme`（主题强调色），
     *   深色 style 用 `coui_color_container_theme_blue`，浅色 style 用 `?couiColorPrimary`；
     * - 禁用态进度用 `coui_color_disable_blue`（`#4D0080FF` / 夜间 `#4D1A8CFF`），
     *   即强调色 30% alpha，背景在 selector 中两态同色。
     *
     * 因此进度跟随当前主题的 [CouiTheme.colorScheme] 强调色，宿主换主题时无需逐个传参。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiVerticalSeekBarColors {
        val accent = CouiTheme.colorSchemeFor(dark).accent
        return CouiVerticalSeekBarColors(
            track = if (dark) Color(0x33FFFFFF) else Color(0x1F000000),
            progress = accent,
            thumb = accent,
            disabledProgress = accent.copy(alpha = 0.3f),
            disabledThumb = accent.copy(alpha = 0.3f),
        )
    }
}

/** 竖向滑动条的轨道、进度与滑块配色。 */
@Immutable
data class CouiVerticalSeekBarColors(
    /** 轨道底色，对应 `couiColorContainer12`。 */
    val track: Color,
    /** 进度色，对应 `couiColorContainerTheme` / `couiColorPrimary`。 */
    val progress: Color,
    /** 滑块色，原厂 style 与进度共用同一个 selector。 */
    val thumb: Color,
    /** 禁用态进度色，对应 `couiColorDisable` / `coui_color_disable_blue`。 */
    val disabledProgress: Color,
    /** 禁用态滑块色，与进度同源。 */
    val disabledThumb: Color,
)

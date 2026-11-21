package com.cycling.coui.seekbar

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/** 图标滑动条的图标类型，对应原厂 `couiIconSeekBarType`。 */
enum class CouiIconSeekBarType {
    /** 亮度：太阳图标随进度旋转。 */
    Brightness,

    /** 音量：图标随进度换档。 */
    Volume,
}

/**
 * 图标滑动条的固定参数。
 *
 * 原厂 `COUIIntentSeekBar` 的图标滑条以 408×96px 为基准绘制：
 * 图标区从面板左侧 36px 开始、图标 72px，进度区从左侧 132px 起、到右侧 36px 止。
 * 这里按 3 倍密度折算成 dp，控件高度 32dp、图标 24dp。
 */
object CouiIconSeekBarDefaults {
    /** 控件高度，对应原厂 96px。 */
    val height: Dp = 32.dp

    /** 面板圆角，对应原厂 90px（大于半高，视觉上是胶囊）。 */
    val panelRadius: Dp = 16.dp

    /** 图标尺寸，对应原厂 72px。 */
    val iconSize: Dp = 24.dp

    /** 图标中心到面板左边缘的距离，对应原厂 36px + 36px。 */
    val iconCenterInset: Dp = 24.dp

    /** 图标右侧到进度区起点的间距，对应原厂 24px。 */
    val iconTrackGap: Dp = 8.dp

    /** 进度区右侧留白，对应原厂 36px。 */
    val trackEndInset: Dp = 12.dp

    /** 进度与轨道高度。 */
    val trackHeight: Dp = 4.dp

    /** 进度与轨道圆角，对应 `coui_icon_seekbar_progress_radius`。 */
    val trackRadius: Dp = 2.dp

    /** 默认最大值，对应 `couiIconSeekBarMax`。 */
    const val max = 100

    /** 亮度图标每档旋转的角度，对应原厂 `progress * 2`。 */
    const val brightnessDegreePerUnit = 2f

    /** 拖动阻尼下限，对应原厂 `mMinDamping`。 */
    const val minDamping = 0.4f

    /**
     * 项目适配：受控 `progress` 回写时把滑块平滑回档的时长。
     *
     * 原厂 `COUIIconSeekBar` 拖动结束不做吸附，滑块停在阻尼位置；
     * Compose 受控状态会把视觉位置拉回整数档，这里用 80ms 过渡避免瞬时跳变。
     */
    const val settleMillis = 80

    /** 点击过渡最短时长。 */
    const val clickMinMillis = 150

    /** 点击过渡时长系数，对应原厂 `DURATION_483`。 */
    const val clickDurationFactor = 483f

    /** 点击过渡曲线，对应原厂 `PathInterpolator(0.3, 0, 0.1, 1)`。 */
    val moveEase: CubicBezierEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /**
     * 日间与夜间配色。
     *
     * 原厂取 `coui_icon_seekbar_background`（面板）、`coui_icon_seekbar_background_color_normal`
     * （未激活轨道）与 `coui_icon_seekbar_def_progress_color`（激活进度），图标固定白色；
     * 夜间只替换面板与未激活轨道。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiIconSeekBarColors = CouiIconSeekBarColors(
        panel = if (dark) Color(0x26FFFFFF) else Color(0x4E000000),
        track = if (dark) Color(0x1AFFFFFF) else Color(0x0D000000),
        progress = Color(0xD8FFFFFF),
        icon = Color.White,
        disabledProgress = Color(0xFFE6E6E6),
    )
}

/** 图标滑动条的面板、轨道、进度与图标配色。 */
@Immutable
data class CouiIconSeekBarColors(
    /** 面板底色，对应 `coui_icon_seekbar_background`。 */
    val panel: Color,
    /** 未激活轨道色，对应 `coui_icon_seekbar_background_color_normal`。 */
    val track: Color,
    /** 激活进度色，对应 `coui_icon_seekbar_def_progress_color`。 */
    val progress: Color,
    /** 图标色，原厂固定白色。 */
    val icon: Color,
    /** 禁用态进度色，对应 `coui_icon_seekbar_progress_color_disabled`。 */
    val disabledProgress: Color,
)

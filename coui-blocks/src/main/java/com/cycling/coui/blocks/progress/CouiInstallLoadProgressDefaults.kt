package com.cycling.coui.blocks.progress

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme
import kotlin.math.PI

/** 安装 / 下载进度按钮的样式，对应原厂 `LOAD_STYLE_DEFAULT` / `LOAD_STYLE_BIG_ROUND`。 */
enum class CouiInstallLoadProgressStyle { Small, Large }

/** 安装 / 下载进度按钮的状态，对应原厂 `COUILoadProgress` 的 `couiState`。 */
enum class CouiLoadProgressState { Idle, InProgress, Waiting, Failed }

/** 安装 / 下载进度按钮的固定度量，取自 `coui_install_download_progress_*` 资源。 */
@Immutable
data class CouiInstallLoadProgressMetrics(
    val width: Dp,
    val height: Dp,
    val cornerRadius: Dp,
    val radiusOffset: Dp = .5.dp,
    val textPadding: Dp = 6.dp,
    val textSize: TextStyle,
    val wideLocaleExtraWidth: Dp = 30.dp,
) {
    val shape: Shape
        get() = RoundedCornerShape(cornerRadius)
}

/** 安装 / 下载进度按钮的配色。 */
@Immutable
data class CouiInstallLoadProgressColors(
    val primary: Color,
    val secondary: Color,
    val content: Color,
    val surpassContent: Color,
    val disabled: Color,
)

/** 安装 / 下载进度按钮的固定参数与配色。 */
object CouiInstallLoadProgressDefaults {
    /** `coui_install_download_progress_width` / `_height` / `_round_border_radius_small`。 */
    val smallMetrics = CouiInstallLoadProgressMetrics(
        width = 52.dp,
        height = 28.dp,
        cornerRadius = 13.5.dp,
        textSize = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    )

    /** `coui_install_download_progress_width_large` / `_height_large` / `_round_border_radius`。 */
    val largeMetrics = CouiInstallLoadProgressMetrics(
        width = 220.dp,
        height = 44.dp,
        cornerRadius = 21.5.dp,
        textSize = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    )

    fun metrics(style: CouiInstallLoadProgressStyle): CouiInstallLoadProgressMetrics =
        if (style == CouiInstallLoadProgressStyle.Large) largeMetrics else smallMetrics

    /** 按下亮度 `DEFAULT_BRIGHTNESS_MAX_VALUE`：底色亮度乘以该系数。 */
    const val pressedBrightness = .8f

    /** 按下文字缩放 `DEFAULT_NARROW_FINAL_VALUE`。 */
    const val pressedTextScale = .92f

    /** 按下时四周内缩比例 `DEFAULT_SCALE_PARAMETER`。 */
    const val pressedInsetFraction = .05f

    /** 按下动画 200ms、抬手回弹 340ms，对应 `PRESS_ANIMATOR_DURATION` / `NORMAL_ANIMATOR_DURATION`。 */
    const val pressDurationMillis = 200
    const val releaseDurationMillis = 340

    /** 原厂 `COUIMoveEaseInterpolator`：`PathInterpolator(0.3, 0, 0.1, 1)`。 */
    val moveEase = CubicBezierEasing(.3f, 0f, .1f, 1f)

    /** 进度弹簧的 response 1.0、bounce 0，对应 `COUISpringForce.setResponse(1.0f)`。 */
    const val progressResponse = 1f

    fun stiffness(response: Float = progressResponse): Float = ((2f * PI.toFloat()) / response).let { it * it }

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiInstallLoadProgressColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiInstallLoadProgressColors(
            primary = scheme.accent,
            secondary = scheme.container,
            content = scheme.accent,
            surpassContent = Color.White,
            disabled = scheme.onSurface.copy(alpha = .3f),
        )
    }
}

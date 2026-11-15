package com.cycling.coui.topappbar

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.rememberCouiSystemTextStyle
import com.cycling.coui.theme.CouiTheme

/** 用户主页顶栏的固定度量，取自原厂 `COUIUserFollowView` 的约束与 `Widget.COUI.Button.Small`。 */
@Immutable
data class CouiUserFollowMetrics(
    val height: Dp = 52.dp,
    val horizontalPadding: Dp = 8.dp,
    val avatarSize: Dp = 24.dp,
    val avatarBorderWidth: Dp = 1.dp,
    val avatarGap: Dp = 8.dp,
    val textGap: Dp = 8.dp,
    val buttonWidth: Dp = 52.dp,
    val buttonHeight: Dp = 28.dp,
    val followTextSize: TextUnit = 14.sp,
    val followingTextSize: TextUnit = 12.sp,
)

/** 用户主页顶栏的配色。[buttonContainer] 是未关注态的实心按钮，[buttonFollowedContainer] 是已关注态的浅色底。 */
@Immutable
data class CouiUserFollowColors(
    val title: Color,
    val subtitle: Color,
    val avatarBackground: Color,
    val avatarBorder: Color,
    val buttonContainer: Color,
    val buttonContent: Color,
    val buttonFollowedContainer: Color,
    val buttonFollowedContent: Color,
)

object CouiUserFollowDefaults {
    val metrics = CouiUserFollowMetrics()

    /** `MaterialCardViewHelper.DEFAULT_FADE_ANIM_DURATION`：原厂场景过渡时长。 */
    const val transitionDurationMillis = 300

    /** 原厂由 `MotionLayout` 的场景插值器驱动，这里取项目的移动曲线。 */
    val transitionEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /** `couiTextHeadlineS`：18sp medium 的主标题。 */
    @Composable
    fun titleTextStyle(): TextStyle = rememberCouiSystemTextStyle(CouiTextKind.TopBar)

    /** `coui_toolbar_subtitle_text_size`：13sp 常规字重的副标题。 */
    @Composable
    fun subtitleTextStyle(): TextStyle = rememberCouiSystemTextStyle(CouiTextKind.ItemSummary)

    /** `couiTextAppearanceButton`：按钮文字随关注态在 14sp 与 12sp 之间过渡。 */
    @Composable
    fun buttonTextStyle(size: TextUnit = metrics.followTextSize): TextStyle =
        rememberCouiSystemTextStyle(CouiTextKind.ButtonLabel, size = size.value).copy(
            fontSize = size,
            fontWeight = FontWeight.Medium,
        )

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiUserFollowColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiUserFollowColors(
            title = scheme.onSurface,
            subtitle = scheme.onSurfaceVariant,
            avatarBackground = if (dark) Color(0x1AFFFFFF) else Color(0xFFEBEBEB),
            avatarBorder = if (dark) Color(0x33FFFFFF) else Color(0x1F000000),
            buttonContainer = scheme.accent,
            buttonContent = scheme.onAccent,
            buttonFollowedContainer = scheme.accent.copy(alpha = if (dark) 0.3f else 0.15f),
            buttonFollowedContent = scheme.accent,
        )
    }
}

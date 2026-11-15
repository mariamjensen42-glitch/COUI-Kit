package com.cycling.coui.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme

/** 轻提示的停留时长；退出动画另计，不计入停留。 */
enum class CouiToastDuration(val millis: Long) {
    Short(2000L),
    Long(3500L),
}

/** 轻提示的容器、文字与操作按钮色。 */
@Immutable
data class CouiToastColors(
    val container: Color,
    val content: Color,
    /** 操作按钮文字；缺省跟随 [content]，与只有文案的旧用法一致。 */
    val action: Color = content,
)

/**
 * 轻提示的固定参数，取自 ColorOS 17 应用内嵌的 `coui_snack_bar_*` 资源。
 *
 * COUI 没有独立的 Toast 组件，短暂的底部反馈由 SnackBar 承担：容器日间 `#FFFFFF`、夜间 `#242424`，
 * 圆角 `coui_snack_bar_background_radius` 12.67dp，内留白水平 16dp、单行垂直 10dp，
 * 文字取 `coui_snack_bar_item.xml` 的 `couiTextAppearanceButton` 14sp（随系统字号缩放）。
 * 操作按钮、图标与多行分支按 `coui_snack_bar_item.xml` 的排布取值。
 */
object CouiToastDefaults {
    /**
     * `coui_snack_bar_background_radius`。
     * 原厂背景是九宫格图片，四角为等半径标准圆角，不是超椭圆；这里用标准圆角矩形还原。
     */
    val shape: Shape = RoundedCornerShape(12.67.dp)

    /** `coui_snack_bar_child_margin_horizontal_start` / `_end`；有操作按钮时末端让位给它。 */
    val horizontalPadding = 16.dp

    /** `coui_snack_bar_child_margin_vertical`，单行上下同值。 */
    val verticalPadding = 10.dp

    /** `coui_snack_bar_child_margin_vertical_multi_lines`。 */
    val multiLineTopPadding = 12.dp

    /** `coui_snack_bar_child_margin_vertical_multi_lines_bottom`。 */
    val multiLineBottomPadding = 8.dp

    /** `couiTextAppearanceButton` 直接声明 `lineSpacingMultiplier` 1.1（不是 `coui_spacing_multiplier_button_m`）。 */
    val lineHeightMultiplier = 1.1f

    /** `coui_snack_bar_max_width`。 */
    val maxWidth = 328.dp
    val spacing = 8.dp

    /** 同时可见的上限；超出时移除最早的一条。 */
    const val maxVisible = 3

    val enterMillis = 160L

    /** 退出动画时长。队列在动画结束后才移除该项，两者必须一致。 */
    val exitMillis = 160L

    /**
     * 原厂九宫格 `coui_snack_bar_background.9.png` 把阴影烘焙进图片：xxhdpi 下容器外扩约 14dp（上）、
     * 21dp（左右）、26dp（下），峰值不透明度约 5%，`coui_snack_bar_shadow_size` 26dp 是它的总跨度。
     * 平台投影把模糊跨度与不透明度绑在同一个 elevation 上，这里按「26dp 跨度 + 5% 黑色」还原同一组观感参数。
     */
    val shadowElevation = 26.dp

    /** 与 [shadowElevation] 配套的投影色；取原厂九宫格的峰值不透明度（#0D，约 5%）。 */
    val shadowColor = Color.Black.copy(alpha = .05f)

    /** `coui_snack_bar_text_size` 不存在；文案字号来自 `coui_snack_bar_item.xml` 的 `@style/couiTextAppearanceButton`，14sp。 */
    val textSize = 14.sp

    /** `coui_snack_bar_icon_width` / `coui_snack_bar_icon_height`；图标槽按此方框居中。 */
    val iconSize = 30.dp

    /** `coui_snack_bar_context_margin_start_with_icon`，图标到容器左缘。 */
    val iconStartPadding = 16.dp

    /**
     * `coui_snack_bar_icon_margin_top_horizontal`，原厂上下同值 16dp，使有图标时最小高度为 62dp。
     * 单行文案只有 15dp 高，沿用 16dp 会在图标上下留出大片空白；这里收到与文案垂直留白一致，
     * 容器高度随之降到 50dp，图标与文案仍垂直居中。
     */
    val iconVerticalPadding = 10.dp

    /** `coui_snack_bar_action_max_width`。 */
    val actionMaxWidth = 110.dp

    /** 文案与操作按钮的间距：17 的 `coui_snack_bar_item.xml` 由文案的 `coui_snack_bar_child_margin_horizontal_end` 16dp 承担。 */
    val actionStartPadding = 16.dp

    /** `coui_snack_bar_action_margin_horizontal_end`，操作按钮到容器右缘。 */
    val actionEndPadding = 4.dp

    /** `coui_snack_bar_action_margin_vertical`，单行上下同值。 */
    val actionVerticalPadding = 10.dp

    /** `coui_snack_bar_action_margin_top_multi_lines`。 */
    val actionMultiLineTopPadding = 8.dp

    /** `coui_snack_bar_action_margin_bottom_multi_lines`。 */
    val actionMultiLineBottomPadding = 9.dp

    /** `coui_snack_bar_action_text_max_lines`，超出以省略号结尾。 */
    const val actionMaxLines = 2

    /** `couiTextAppearanceButton` 的 14sp；与文案同源，随系统字号缩放。 */
    val actionTextSize = 14.sp

    /** `coui_snack_bar_item.xml` 中操作按钮的 `lineSpacingMultiplier`。 */
    val actionLineHeightMultiplier = 1.15f

    /**
     * `coui_snack_bar_background`：日间白色、夜间 #242424；内容取当前前景色，
     * 操作按钮取 `?couiColorPrimaryTextOnPopup`（默认主题映射到 `couiBlueTintControlNormal`，即强调色）。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiToastColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiToastColors(
            container = if (dark) Color(0xFF242424) else Color.White,
            content = scheme.onSurface,
            action = scheme.accentOnSurface,
        )
    }
}

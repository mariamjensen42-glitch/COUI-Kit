package com.cycling.coui.chip

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme
import kotlin.math.PI

/**
 * 原厂 `Widget.COUI.Chip` 的四个实际样式：基础、可选、建议（可关闭）与标签输入组内样式。
 * 每个样式对应一组尺寸与内留白，取值见 [CouiChipDefaults.metrics]。
 */
enum class CouiChipStyle { Default, Selection, Suggestion, Input }

/** 单个 Chip 的度量；全部来自原厂 style 与 `coui_chip_*` dimens。 */
@Immutable
data class CouiChipMetrics(
    val startPadding: Dp,
    val endPadding: Dp,
    val minHeight: Dp,
    val minWidth: Dp,
    val maxWidth: Dp = 300.dp,
    /** `chipTextMaxWidth`：文本可用宽度的上限，超出按 ellipsize 处理。 */
    val textMaxWidth: Dp = 200.dp,
    /** `chipIconSize`；为空表示由图标自身固有尺寸决定（原厂 `INVALID_VALUE`）。 */
    val iconSize: Dp? = null,
    val iconStartPadding: Dp = 0.dp,
    val iconEndPadding: Dp = 0.dp,
    val textStartPadding: Dp = 0.dp,
    val textEndPadding: Dp = 0.dp,
    val closeIconSize: Dp = 0.dp,
    val closeIconStartPadding: Dp = 0.dp,
    val closeIconEndPadding: Dp = 0.dp,
    /** `closeIconTouchBoundsSize`：关闭图标的命中区域边长，可大于图标本身。 */
    val closeIconTouchBoundsSize: Dp = 0.dp,
    val countMarginStart: Dp = 0.dp,
    val countMarginEnd: Dp = 0.dp,
)

/**
 * 原厂 `TYPE_*` 四态配色：未选中 / 选中 × 可用 / 禁用，加上数量文案色。
 * `checkedBackground` 取主题强调色，`countText` 取 `couiColorLabelSecondary`。
 */
@Immutable
data class CouiChipColors(
    val uncheckedBackground: Color,
    val checkedBackground: Color,
    val uncheckedDisabledBackground: Color,
    val checkedDisabledBackground: Color,
    val uncheckedContent: Color,
    val checkedContent: Color,
    val uncheckedDisabledContent: Color,
    val checkedDisabledContent: Color,
    val countText: Color,
)

/**
 * Chip 族固定参数。
 *
 * 文本外观取 `couiTextBodyXS`（12sp，基础 / 建议）与 `couiTextBodyM`（14sp，可选），
 * 内留白取 `coui_chip_*_style_*` dimens；弹簧取 `TintAnimation` / `CloseIconAnimation`
 * 的 response 0.3、bounce 0，标签组宽高变化取 response 0.35、bounce 0。
 */
object CouiChipDefaults {
    /** 选中染色与关闭图标宽度动画的弹簧响应；bounce 恒为 0。 */
    const val tintResponse = .3f

    /** 标签组宽高变化动画的弹簧响应；bounce 恒为 0。 */
    const val groupResponse = .35f

    /** `coui_chip_group_default_spacing_horizontal`。 */
    val groupSpacingHorizontal = 8.dp

    /** `coui_chip_group_default_spacing_vertical`。 */
    val groupSpacingVertical = 8.dp

    /** `coui_chip_group_collapsable_button_size`。 */
    val collapsableButtonSize = 28.dp

    /** 折叠态默认保留行数。 */
    const val defaultCollapsedMaxRows = 3

    /** 红点直径取 `coui_hint_red_dot_medium_reddot_size`。 */
    val redDotSize = 8.dp

    /** `coui_chip_red_dot_offset_horizontal` / `_vertical`。 */
    val redDotOffset = DpOffset(2.dp, 6.dp)

    /** 标签输入框的最小宽度与文本外观（`couiTextAppearanceBodyL`，15sp）。 */
    val inputMinWidth = 10.dp
    val inputTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(includeFontPadding = true),
    )

    /** 原厂 `(2π / response)²` 的刚度换算，供 Compose 弹簧直接使用。 */
    fun stiffness(response: Float): Float {
        require(response.isFinite() && response > 0f) { "弹簧 response 必须是有限正值" }
        return ((2.0 * PI / response) * (2.0 * PI / response)).toFloat()
    }

    fun metrics(style: CouiChipStyle): CouiChipMetrics = when (style) {
        CouiChipStyle.Default -> CouiChipMetrics(
            startPadding = 0.dp, endPadding = 0.dp,
            minHeight = 28.dp, minWidth = 52.dp,
        )
        CouiChipStyle.Selection -> CouiChipMetrics(
            startPadding = 12.dp, endPadding = 12.dp,
            minHeight = 32.dp, minWidth = 52.dp,
            iconSize = 16.dp, iconStartPadding = 0.dp, iconEndPadding = 4.dp,
        )
        CouiChipStyle.Suggestion -> CouiChipMetrics(
            startPadding = 12.dp, endPadding = 12.dp,
            minHeight = 28.dp, minWidth = 52.dp,
            iconSize = 14.dp, iconStartPadding = 12.dp, iconEndPadding = 4.dp,
            closeIconSize = 12.dp, closeIconStartPadding = 8.dp,
            closeIconEndPadding = 8.dp, closeIconTouchBoundsSize = 24.dp,
        )
        CouiChipStyle.Input -> CouiChipMetrics(
            startPadding = 12.dp, endPadding = 12.dp,
            minHeight = 32.dp, minWidth = 52.dp,
            iconSize = 24.dp, iconStartPadding = 4.dp, iconEndPadding = 4.dp,
            closeIconSize = 14.dp, closeIconStartPadding = 8.dp,
            closeIconEndPadding = 9.dp, closeIconTouchBoundsSize = 28.dp,
        )
    }

    fun textStyle(style: CouiChipStyle): TextStyle = when (style) {
        CouiChipStyle.Selection -> TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        )
        else -> TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        )
    }

    /** 数量文案取 `countTextAppearance`（13sp）；未配置时原厂回落到 12sp。 */
    val countTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )

    /**
     * `Widget.COUI.Chip` 的配色：未选中底为 `couiColorContainer8`，选中底为主题强调色，
     * 文字与图标分别为 `couiColorLabelPrimary` / `couiColorLabelOnColor`，
     * 禁用色取 `chip_checked_text_disable_color` 与 `chip_unchecked_text_disable_color`。
     */
    fun colors(dark: Boolean, accent: Color, onAccent: Color = Color.White): CouiChipColors = CouiChipColors(
        uncheckedBackground = if (dark) Color(0x1AFFFFFF) else Color(0x14000000),
        checkedBackground = accent,
        uncheckedDisabledBackground = if (dark) Color(0x1AFFFFFF) else Color(0x14000000),
        checkedDisabledBackground = accent.copy(alpha = .3f),
        uncheckedContent = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000),
        checkedContent = onAccent,
        uncheckedDisabledContent = if (dark) Color(0x29FFFFFF) else Color(0x42000000),
        checkedDisabledContent = if (dark) Color(0x29FFFFFF) else Color(0x8AFFFFFF),
        countText = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000),
    )
}

/** 折叠按钮与关闭图标的矢量路径，取自原厂 `ic_chip_collapse` / `ic_chip_expand` / `ic_chip_close`。 */
internal object CouiChipPaths {
    const val collapse = "M12,10L8,6L4,10"
    const val expand = "M4,6L8,10L12,6"
    const val close =
        "M12.286,2.54C12.4,2.426 12.4,2.242 12.286,2.128L11.874,1.715C11.76,1.601 11.575,1.601 " +
            "11.461,1.715L7.001,6.176L2.54,1.715C2.426,1.601 2.242,1.601 2.128,1.715L1.715,2.128C1.601," +
            "2.242 1.601,2.426 1.715,2.54L6.176,7.001L1.715,11.461C1.601,11.575 1.601,11.76 1.715," +
            "11.874L2.128,12.286C2.242,12.4 2.426,12.4 2.54,12.286L7.001,7.826L11.461,12.286C11.575," +
            "12.4 11.76,12.4 11.874,12.286L12.286,11.874C12.4,11.76 12.4,11.575 12.286,11.461L7.826," +
            "7.001L12.286,2.54Z"

    /** 关闭图标与折叠图标的原厂 vector viewport（14 / 16）。 */
    const val closeViewport = 14f
    const val collapseViewport = 16f
}

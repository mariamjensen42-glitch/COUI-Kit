package com.cycling.coui.statement

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 全屏隐私声明的固定参数，取自 ColorOS 17 内嵌 COUI 的
 * `com.coui.appcompat.statement.COUIFullPageStatement` 与 `coui_full_page_statement.xml`：
 * 标题 16sp 居中、正文 13sp 次要色、正文最大高度 248dp、确认按钮宽 174dp。
 */
object CouiStatementDefaults {
    /** 标题上下留白：`coui_full_page_statement_text_button_padding` 与 `content_margin`。 */
    val titleTopMargin = 12.dp
    val titleBottomMargin = 12.dp

    /** 内容左右留白 `coui_full_page_statement_padding_horizontal`，标题与正文共用。 */
    val horizontalPadding = 24.dp

    /** 标题滚动区上限；原厂用 `couiButtonScrollView` 风格的最大高度容器限制标题。 */
    val titleMaxHeight = 90.dp

    /** 正文滚动区上限 `coui_full_page_statement_scroll_max_height`。 */
    val statementMaxHeight = 248.dp

    /** 正文内边距 `coui_full_page_statement_padding_top` / `_padding_bottom`。 */
    val statementPaddingTop = 12.dp
    val statementPaddingBottom = 14.dp

    /** 确认按钮 `coui_full_page_statement_button_width`，上下留白 20dp / 16dp。 */
    val confirmButtonWidth = 174.dp
    val confirmTopMargin = 20.dp
    val confirmBottomMargin = 16.dp

    /** 退出文字按钮：最小高度 32dp、底部留白 24dp、16sp `sans-serif-medium`。 */
    val exitMinHeight = 32.dp
    val exitBottomMargin = 24.dp

    /**
     * 横屏双按钮栏（`button_layout_land`）：按钮高 44dp，左右外边距 37dp，
     * 两个按钮之间各 4dp。
     */
    val landButtonHeight = 44.dp
    val landEdgeMargin = 37.dp
    val landCenterGap = 4.dp
    val landTopPadding = 16.dp
    val landBottomPadding = 24.dp

    /**
     * 小屏横屏判定阈值：原厂 `isSmallScreen()` 取 `smallestScreenWidthDp < 480`，
     * 且当前是横屏时改用横排按钮。
     */
    const val compactLandscapeSmallestWidth = 480

    /** 标题 `?couiTextHeadlineXS`：16sp、行距 1.05、`sans-serif-medium`。 */
    val titleStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 16.8f.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.SansSerif,
    )

    /** 正文 `?couiTextBodyS`：13sp、行距 1.1。 */
    val bodyStyle = TextStyle(
        fontSize = 13.sp,
        lineHeight = 14.3f.sp,
        fontFamily = FontFamily.SansSerif,
    )

    /** 退出文字 `coui_full_page_statement_button_text_size`：16sp、`sans-serif-medium`。 */
    val exitStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.SansSerif,
    )
}

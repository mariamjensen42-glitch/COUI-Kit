package com.cycling.coui.textfield

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme

/**
 * 多行输入卡片的配色：卡片底色与正文沿用输入框角色，提示比正文淡一档，
 * 字数到达上限时短暂切到错误色。
 */
@Immutable
data class CouiMultiInputColors(
    val container: Color,
    val text: Color,
    val hint: Color,
    val counter: Color,
    val cursor: Color,
    val error: Color,
    val disabled: Color,
)

/**
 * 多行输入卡片的固定度量：152dp 的卡片、32dp 横向留白、13dp 上下留白，
 * 空态提示最多 3 行、正文最多 5 行。
 */
object CouiMultiInputDefaults {
    /** `coui_card_multi_input_height`。 */
    val minHeight = 152.dp

    /** 卡片左右 32dp、上下 `coui_card_multi_edittext_padding`。 */
    val contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 13.dp, bottom = 13.dp)

    /** 字数与正文之间留出的高度，`coui_input_edit_count_margin_bottom`。 */
    val counterSpacing = 12.dp

    /** 空态提示的最大行数 `couiEditTextHintLines`。 */
    const val hintMaxLines = 3

    /** 正文最大行数，`Widget.COUI.MultiInput` 的 `android:maxLines`。 */
    const val maxLines = 5

    /** 字数切换到错误色的时长 `ERROR_COUNT_COLOR_ANIMATOR_TIME`。 */
    val counterColorDurationMillis = 250

    /** 到达上限后错误色的回落延迟 `ERROR_COUNT_COLOR_DELAY_TIME`。 */
    val counterResetDelayMillis = 1000L

    val shape: Shape = CouiTextFieldDefaults.shape

    /** 正文排版与输入框一致：bodyL、16sp。 */
    @Composable
    fun textStyle(): TextStyle = CouiTextFieldDefaults.textStyle()

    /** 字数说明：12sp，与 `couiTextBodyXS` 同档。 */
    fun counterTextStyle(): TextStyle = TextStyle(fontSize = 12.sp)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiMultiInputColors {
        val theme = CouiTheme.colorSchemeFor(dark)
        return CouiMultiInputColors(container = theme.card, text = theme.onSurface,
            // coui_color_label_tertiary：日间 26%、夜间 30% 的正文色。
            hint = theme.onSurface.copy(alpha = if (dark) .3f else .26f),
            counter = theme.onSurface.copy(alpha = if (dark) .3f else .26f),
            cursor = theme.accent, error = CouiTextFieldDefaults.errorColor(dark),
            disabled = theme.onSurface.copy(alpha = .3f))
    }
}

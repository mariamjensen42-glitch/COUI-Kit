package com.cycling.coui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import com.cycling.coui.theme.CouiTheme

/** 链接文本的三种可表达状态色。 */
@Immutable
data class CouiTextLinkColors(val normal: Color, val pressed: Color, val disabled: Color)

/**
 * 链接文本的配色预设，取自原厂 `color/coui_clickable_text_color` 选择器：
 * 常规态 `?couiColorLink`（`?couiColorLabelTheme`，日间 `#0080FF`、夜间 `#1A8CFF`），
 * 按下态与选中态同为 `#4D2660F5`，禁用态日间 `#26000000`、夜间 `#26FFFFFF`；四态都不加下划线。
 *
 * Compose 的 `TextLinkStyles` 只有按下、聚焦与悬停三个状态，没有禁用态：
 * 链接不可用时应由宿主改用 [CouiTextLinkColors.disabled] 的普通 span，不挂 `LinkAnnotation`。
 */
object CouiTextLinkDefaults {

    private val Pressed = Color(0x4D2660F5)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTextLinkColors = CouiTextLinkColors(
        normal = CouiTheme.colorSchemeFor(dark).accent,
        pressed = Pressed,
        disabled = if (dark) Color(0x26FFFFFF) else Color(0x26000000),
    )

    /** 直接作为 `LinkAnnotation.Clickable` 的 `styles` 传入。 */
    @Composable
    fun textLinkStyles(dark: Boolean = CouiTheme.isDark): TextLinkStyles {
        val colors = colors(dark)
        val normal = SpanStyle(color = colors.normal, textDecoration = TextDecoration.None)
        val active = SpanStyle(color = colors.pressed, textDecoration = TextDecoration.None)
        return TextLinkStyles(style = normal, focusedStyle = active, hoveredStyle = active, pressedStyle = active)
    }
}

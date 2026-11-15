package com.cycling.coui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.cycling.coui.effect.CouiMaskDefaults
import com.cycling.coui.theme.CouiTheme

/** 状态效果表面的四层目标色：悬停、焦点、按压蒙层与焦点描边。 */
@Immutable
data class CouiStateEffectColors(
    val hover: Color,
    val focus: Color,
    val press: Color,
    val outline: Color,
)

/**
 * 状态效果表面的固定参数，取值对齐原厂 `state/` 包。
 *
 * 悬停、焦点与按压三层蒙层沿用 [CouiMaskDefaults] 的目标色与 0.3 响应弹簧；
 * 焦点描边的可见宽度是 `default_focus_stroke_radius` 的两倍。
 */
object CouiStateEffectDefaults {
    /** 原厂 COUIStrokeDrawable 的 `DEFAULT_SPRING_RESPONSE`。 */
    const val focusSpringResponse = .3f

    /** 原厂蒙层按压通道的弹簧响应。 */
    const val pressSpringResponse = .3f

    /** 焦点描边的笔宽，外半笔宽可见 3dp。 */
    val focusStrokeWidth: Dp = CouiMaskDefaults.FocusStrokeWidth

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiStateEffectColors {
        val mask = CouiMaskDefaults.colors(dark)
        return CouiStateEffectColors(
            hover = mask.hover,
            focus = mask.focus,
            press = mask.press,
            outline = CouiTheme.colorSchemeFor(dark).accent,
        )
    }
}

package com.cycling.coui.checkbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.cycling.coui.shape.CouiShape
import com.cycling.coui.shape.CouiShapes

object CouiCheckboxDefaults {
    /** 原生基线；组件默认读取 `CouiTheme.shapes.extraSmall`。 */
    val shape: Shape = CouiShapes.Default.extraSmall
    val borderShape = CouiShape(3.2.dp)
    fun colors(checked: Color, dark: Boolean): CouiCheckboxColors = CouiCheckboxColors(
        checked = checked,
        unchecked = Color(if (dark) 0x4dffffff else 0x42000000),
        disabledChecked = checked.copy(alpha = 0.3f),
        disabledUnchecked = Color(if (dark) 0x26ffffff else 0x14000000),
        checkmark = Color.White,
        disabledCheckmark = Color(if (dark) 0x29ffffff else 0x8affffff),
    )
}

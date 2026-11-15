package com.cycling.coui.demo

import androidx.compose.runtime.Composable
import com.cycling.coui.menu.CouiMenuDefaults
import com.cycling.coui.menu.CouiMenuStyle
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.topappbar.CouiToolbarStyle

@Composable
internal fun demoMenuStyle(dark: Boolean): CouiMenuStyle {
    return CouiMenuDefaults.style(dark).copy(
        shape = DemoShapes.menu,
        itemShape = DemoShapes.menuItem,
        checkboxShape = DemoShapes.checkbox,
        checkboxBorderShape = DemoShapes.checkboxBorder,
    )
}

@Composable
internal fun CouiToolbarStyle.withDemoThemeTint(): CouiToolbarStyle {
    val tint = CouiTheme.materialTintFor(dark)
    return copy(material = { capsule -> material(capsule).copy(themeTint = tint) })
}

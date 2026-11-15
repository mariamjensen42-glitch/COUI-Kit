package com.cycling.coui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cycling.coui.theme.CouiColor
import com.cycling.coui.theme.CouiTheme

/** Demo 页面补充的语义色。核心色板只表达组件角色，错误提示等页面语义由 Demo 自己装配。 */
internal object DemoPalette {
    /** 错误提示文字；取 COUI 固定日夜红。 */
    val error: Color
        @Composable get() = if (CouiTheme.isDark) CouiColor.Night.RED else CouiColor.Day.RED

    /** 模糊与材质演示的装饰图案色；只用于采样源，不参与组件材质。 */
    val illustration: List<Color>
        @Composable get() = if (CouiTheme.isDark) listOf(CouiColor.Night.BLUE, CouiColor.Night.GREEN, CouiColor.Night.ORANGE)
        else listOf(CouiColor.Day.BLUE, CouiColor.Day.GREEN, CouiColor.Day.ORANGE)
}

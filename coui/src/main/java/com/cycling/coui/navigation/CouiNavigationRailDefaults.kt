package com.cycling.coui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme

/**
 * 竖向导航轨的固定尺寸，取自 `coui_navigation_rail_*` 与 `coui_side_navigation_*` 维度：
 * 菜单宽 64dp、两侧间距各 4dp、图标 24dp、文字 10dp 且最多两行。
 */
@Immutable
data class CouiNavigationRailMetrics(
    val menuWidth: Dp = 64.dp,
    val itemHorizontalMargin: Dp = 4.dp,
    val iconSize: Dp = 24.dp,
    val iconTopPadding: Dp = 12.dp,
    val labelTopPadding: Dp = 2.dp,
    val labelBottomPadding: Dp = 12.dp,
    val itemCornerRadius: Dp = 8.dp,
    val itemSpacing: Dp = 8.dp,
    val dividerWidth: Dp = Dp.Hairline,
    val dividerOffset: Dp = 8.dp,
)

/** 导航轨配色。选中项用强调色作前景，选中底为强调色的低透明度衬底。 */
@Immutable
data class CouiNavigationRailColors(
    val container: Color,
    val content: Color,
    val selectedContent: Color,
    val selectedContainer: Color,
    val divider: Color,
)

object CouiNavigationRailDefaults {
    val metrics = CouiNavigationRailMetrics()

    /** 项间距的下限，原厂 `coui_navigation_rail_root_view_divider_height` 8dp。 */
    val minItemSpacing: Dp = 8.dp

    /** 菜单块顶部占容器高度的比例，原厂按 `高度 * 0.4` 反算项间距。 */
    const val menuHeightFactor: Float = 0.4f

    val labelTextStyle: TextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W500)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiNavigationRailColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiNavigationRailColors(
            container = scheme.surface,
            content = scheme.onSurface,
            selectedContent = scheme.accent,
            selectedContainer = scheme.accent.copy(alpha = if (dark) 0.18f else 0.10f),
            divider = scheme.outlineVariant,
        )
    }
}

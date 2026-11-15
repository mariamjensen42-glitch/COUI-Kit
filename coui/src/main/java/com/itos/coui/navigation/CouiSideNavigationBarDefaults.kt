package com.itos.coui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/**
 * 侧边导航的固定尺寸。抽屉态宽度取 `coui_side_navigation_bar_min_width` 300dp；
 * 停靠态宽度取 `min(coui_side_navigation_bar_max_width 280dp, 可用宽度 * 0.382)`。
 * 项高、圆角与间距取自同一套 `coui_side_navigation_*` 维度。
 */
@Immutable
data class CouiSideNavigationMetrics(
    val drawerWidth: Dp = 300.dp,
    val dockedMaxWidth: Dp = 280.dp,
    val dockedWidthFraction: Float = 0.382f,
    val panelHorizontalPadding: Dp = 12.dp,
    val panelVerticalPadding: Dp = 16.dp,
    val itemHeight: Dp = 48.dp,
    val itemCornerRadius: Dp = 8.dp,
    val itemHorizontalPadding: Dp = 12.dp,
    val iconSize: Dp = 24.dp,
    val iconLabelSpacing: Dp = 12.dp,
    val itemSpacing: Dp = 4.dp,
    val dividerWidth: Dp = Dp.Hairline,
    val enterDurationMillis: Int = 250,
)

/** 侧边导航配色。选中项用强调色作前景，选中底为强调色的低透明度衬底。 */
@Immutable
data class CouiSideNavigationColors(
    val container: Color,
    val content: Color,
    val selectedContent: Color,
    val selectedContainer: Color,
    val divider: Color,
    val scrim: Color,
)

object CouiSideNavigationBarDefaults {
    val metrics = CouiSideNavigationMetrics()

    /** 停靠与抽屉的切换阈值，取 `WindowWidthSizeClass` 的 Compact 上界。 */
    val dockedMinWidth: Dp = 600.dp

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiSideNavigationColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiSideNavigationColors(
            container = scheme.surface,
            content = scheme.onSurface,
            selectedContent = scheme.accent,
            selectedContainer = scheme.accent.copy(alpha = if (dark) 0.18f else 0.10f),
            divider = scheme.outlineVariant,
            scrim = Color.Black.copy(alpha = 0.4f),
        )
    }
}

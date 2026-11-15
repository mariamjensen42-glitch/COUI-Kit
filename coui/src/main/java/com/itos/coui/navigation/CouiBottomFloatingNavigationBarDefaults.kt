package com.itos.coui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/**
 * 悬浮底栏的固定尺寸，取自 `coui_bottom_floating_navigation_bar_*` 维度：
 * 栏高 56dp、项高 48dp、项最大宽 70dp、项内边距 12dp、项间距 8dp、两侧附件间距 12dp。
 */
@Immutable
data class CouiBottomFloatingNavigationMetrics(
    val height: Dp = 56.dp,
    val horizontalPadding: Dp = 16.dp,
    val itemHeight: Dp = 48.dp,
    val itemMaxWidth: Dp = 70.dp,
    val itemPadding: Dp = 12.dp,
    val itemSpacing: Dp = 8.dp,
    val itemIconLabelSpacing: Dp = 4.dp,
    val itemCornerRadius: Dp = 24.dp,
    val iconSize: Dp = 24.dp,
    val accessorySize: Dp = 48.dp,
    val accessorySpacing: Dp = 12.dp,
    val elevation: Dp = 8.dp,
)

/** 悬浮底栏配色。选中项用强调色作前景，选中底为强调色的低透明度衬底。 */
@Immutable
data class CouiBottomFloatingNavigationColors(
    val container: Color,
    val content: Color,
    val selectedContent: Color,
    val selectedContainer: Color,
)

object CouiBottomFloatingNavigationBarDefaults {
    val metrics = CouiBottomFloatingNavigationMetrics()

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiBottomFloatingNavigationColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiBottomFloatingNavigationColors(
            container = scheme.card,
            content = scheme.onSurface,
            selectedContent = scheme.accent,
            selectedContainer = scheme.accent.copy(alpha = if (dark) 0.18f else 0.10f),
        )
    }
}

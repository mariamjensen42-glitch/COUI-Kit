package com.itos.coui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/**
 * 附件栏的固定尺寸与动效参数，取自原厂 `COUIBottomFloatingNavigationBar` 的
 * `LOW_END_*` 常量：透明度 0.2 响应、缩放与位移 0.5 响应配 0.3 回弹、最小缩放 0.5。
 */
@Immutable
data class CouiAccessoryNavigationMetrics(
    val buttonSize: Dp = 40.dp,
    val iconSize: Dp = 24.dp,
    val spacing: Dp = 12.dp,
    val minScale: Float = 0.5f,
    val alphaResponse: Float = 0.2f,
    val scaleResponse: Float = 0.5f,
    val scaleBounce: Float = 0.3f,
    val translationResponse: Float = 0.5f,
    val translationBounce: Float = 0.3f,
)

/** 附件栏配色。 */
@Immutable
data class CouiAccessoryNavigationColors(
    val container: Color,
    val content: Color,
    val disabledContent: Color,
)

object CouiAccessoryNavigationBarDefaults {
    val metrics = CouiAccessoryNavigationMetrics()

    /** 收起时保留可见的附件数量。 */
    const val collapsedVisibleCount: Int = 1

    /** 原厂把弹簧响应换算成刚度的公式，与 `COUISpringAnimation` 一致。 */
    fun stiffness(response: Float): Float = (2f * Math.PI.toFloat() / response).let { it * it }

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiAccessoryNavigationColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiAccessoryNavigationColors(
            container = scheme.card,
            content = scheme.onSurface,
            disabledContent = scheme.onSurface.copy(alpha = 0.3f),
        )
    }
}

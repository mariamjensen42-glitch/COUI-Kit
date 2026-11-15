package com.itos.coui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiIcon as Icon
import com.itos.coui.theme.CouiTheme
import androidx.compose.ui.util.lerp

/** 单个附件按钮。[enabled] 为 false 时只显示且不响应点击。 */
data class CouiAccessoryAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * 附件栏：悬浮底栏一侧的圆形按钮组，展开时按 0.5 响应弹簧逐个推出，
 * 收起时只保留 [collapsedVisibleCount] 个。展开状态与开关按钮都由宿主持有。
 */
@Composable
fun AccessoryNavigationBar(
    actions: List<CouiAccessoryAction>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    collapsedVisibleCount: Int = CouiAccessoryNavigationBarDefaults.collapsedVisibleCount,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiAccessoryNavigationColors = CouiAccessoryNavigationBarDefaults.colors(dark),
    metrics: CouiAccessoryNavigationMetrics = CouiAccessoryNavigationBarDefaults.metrics,
) {
    require(actions.isNotEmpty()) { "附件栏至少需要一个按钮" }
    require(collapsedVisibleCount >= 0) { "收起数量不能为负" }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(metrics.spacing),
    ) {
        actions.forEachIndexed { index, action ->
            val visible = expanded || index < collapsedVisibleCount
            val alpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = 1f,
                    stiffness = CouiAccessoryNavigationBarDefaults.stiffness(metrics.alphaResponse),
                ),
                label = "accessoryAlpha",
            )
            val motion by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = 1f - metrics.scaleBounce,
                    stiffness = CouiAccessoryNavigationBarDefaults.stiffness(metrics.scaleResponse),
                ),
                label = "accessoryMotion",
            )
            if (visible || alpha > 0.001f) {
                AccessoryButton(
                    action = action,
                    colors = colors,
                    metrics = metrics,
                    modifier = Modifier.graphicsLayer {
                        this.alpha = alpha
                        val scale = lerp(metrics.minScale, 1f, motion)
                        scaleX = scale
                        scaleY = scale
                        translationX = (1f - motion) * size.width * if (rtl) 1f else -1f
                    },
                )
            }
        }
    }
}

@Composable
private fun AccessoryButton(
    action: CouiAccessoryAction,
    colors: CouiAccessoryNavigationColors,
    metrics: CouiAccessoryNavigationMetrics,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier.size(metrics.buttonSize)
            .clip(CouiCapsuleShape)
            .background(colors.container)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = action.enabled,
                role = Role.Button,
                onClick = action.onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(metrics.iconSize),
            imageVector = action.icon,
            contentDescription = action.contentDescription,
            tint = if (action.enabled) colors.content else colors.disabledContent,
        )
    }
}

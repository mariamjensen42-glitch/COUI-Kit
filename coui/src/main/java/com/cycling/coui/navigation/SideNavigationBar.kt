package com.cycling.coui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.cycling.coui.badge.Badge
import com.cycling.coui.effect.couiStateMask
import com.cycling.coui.theme.CouiIcon as Icon
import com.cycling.coui.theme.CouiText
import com.cycling.coui.theme.CouiTheme

/** [selectedIcon] 用于选中态，省略时两种状态共用 [icon]。[badge] 为 null 时不显示角标。 */
data class CouiSideNavigationItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val badge: Int? = null,
)

/**
 * 侧边导航容器：可用宽度不小于 [CouiSideNavigationBarDefaults.dockedMinWidth] 时停靠展示，
 * 更窄时改为带遮罩的抽屉。展开状态、选中项和提交都由宿主持有。
 * 抽屉态不接管返回键与侧滑，关闭请求统一走 [onExpandedChange]。
 */
@Composable
fun SideNavigationBar(
    items: List<CouiSideNavigationItem>,
    selectedIndex: Int,
    onSelection: (Int) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    docked: Boolean? = null,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiSideNavigationColors = CouiSideNavigationBarDefaults.colors(dark),
    metrics: CouiSideNavigationMetrics = CouiSideNavigationBarDefaults.metrics,
    content: @Composable () -> Unit = {},
) {
    require(items.isNotEmpty()) { "侧边导航至少需要一项" }
    require(selectedIndex in items.indices) { "选中下标超出导航项范围" }
    BoxWithConstraints(modifier) {
        val isDocked = docked ?: (maxWidth >= CouiSideNavigationBarDefaults.dockedMinWidth)
        val panelWidth = if (isDocked) {
            minOf(metrics.dockedMaxWidth, maxWidth * metrics.dockedWidthFraction)
        } else {
            metrics.drawerWidth
        }
        if (isDocked) {
            Row(Modifier.fillMaxSize()) {
                SideNavigationPanel(
                    modifier = Modifier.width(panelWidth).fillMaxHeight(),
                    items = items,
                    selectedIndex = selectedIndex,
                    onSelection = onSelection,
                    header = header,
                    footer = footer,
                    dark = dark,
                    colors = colors,
                    metrics = metrics,
                )
                Box(Modifier.width(metrics.dividerWidth).fillMaxHeight().background(colors.divider))
                Box(Modifier.weight(1f).fillMaxHeight()) { content() }
            }
        } else {
            val progress by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                animationSpec = tween(metrics.enterDurationMillis),
                label = "sideNavigationPanel",
            )
            val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
            Box(Modifier.fillMaxSize()) {
                content()
                if (progress > 0f) {
                    val scrimInteraction = remember { MutableInteractionSource() }
                    Box(
                        Modifier.fillMaxSize()
                            .background(colors.scrim.copy(alpha = colors.scrim.alpha * progress))
                            .clickable(interactionSource = scrimInteraction, indication = null) {
                                onExpandedChange?.invoke(false)
                            },
                    )
                }
                if (progress > 0.001f) {
                    SideNavigationPanel(
                        modifier = Modifier.width(panelWidth).fillMaxHeight()
                            .graphicsLayer {
                                translationX = (1f - progress) * panelWidth.toPx() * if (rtl) 1f else -1f
                            },
                        items = items,
                        selectedIndex = selectedIndex,
                        onSelection = onSelection,
                        header = header,
                        footer = footer,
                        dark = dark,
                        colors = colors,
                        metrics = metrics,
                    )
                }
            }
        }
    }
}

@Composable
private fun SideNavigationPanel(
    modifier: Modifier,
    items: List<CouiSideNavigationItem>,
    selectedIndex: Int,
    onSelection: (Int) -> Unit,
    header: (@Composable ColumnScope.() -> Unit)?,
    footer: (@Composable ColumnScope.() -> Unit)?,
    dark: Boolean,
    colors: CouiSideNavigationColors,
    metrics: CouiSideNavigationMetrics,
) {
    Column(
        modifier = modifier.background(colors.container)
            .padding(horizontal = metrics.panelHorizontalPadding, vertical = metrics.panelVerticalPadding),
    ) {
        header?.invoke(this)
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(metrics.itemSpacing),
        ) {
            items.forEachIndexed { index, item ->
                SideNavigationRow(
                    item = item,
                    selected = index == selectedIndex,
                    dark = dark,
                    colors = colors,
                    metrics = metrics,
                    onClick = { onSelection(index) },
                )
            }
        }
        footer?.invoke(this)
    }
}

@Composable
private fun SideNavigationRow(
    item: CouiSideNavigationItem,
    selected: Boolean,
    dark: Boolean,
    colors: CouiSideNavigationColors,
    metrics: CouiSideNavigationMetrics,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(metrics.itemCornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(metrics.itemHeight)
            .clip(shape)
            .background(if (selected) colors.selectedContainer else Color.Transparent)
            .couiStateMask(interactionSource, dark = dark, shape = shape, selected = selected)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(horizontal = metrics.itemHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(metrics.iconSize),
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = null,
            tint = if (selected) colors.selectedContent else colors.content,
        )
        Spacer(Modifier.width(metrics.iconLabelSpacing))
        CouiText(
            text = item.label,
            color = if (selected) colors.selectedContent else colors.content,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        val badge = item.badge
        if (badge != null) Badge(count = badge, dark = dark)
    }
}

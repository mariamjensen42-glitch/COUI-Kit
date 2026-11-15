package com.itos.coui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.Layout
import com.itos.coui.effect.couiStateMask
import com.itos.coui.theme.CouiIcon as Icon
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 平板与横屏窗口用的竖向导航轨。菜单块按原厂规则整体居中，项与项之间的间距由
 * `max((高度 * 0.4 - 全部项高度) / (项数 + 1), 8dp)` 反算，项数越多间距越小。
 * 选中项和提交都由宿主持有。
 */
@Composable
fun NavigationRail(
    items: List<CouiNavigationItem>,
    selectedIndex: Int,
    onSelection: (Int) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiNavigationRailColors = CouiNavigationRailDefaults.colors(dark),
    metrics: CouiNavigationRailMetrics = CouiNavigationRailDefaults.metrics,
) {
    require(items.isNotEmpty()) { "导航轨至少需要一项" }
    require(selectedIndex in items.indices) { "选中下标超出导航项范围" }
    val shape = RoundedCornerShape(metrics.itemCornerRadius)
    val minSpacing = CouiNavigationRailDefaults.minItemSpacing
    Row(
        modifier = modifier.fillMaxHeight().background(colors.container)
            .width(metrics.menuWidth + metrics.itemHorizontalMargin * 2),
    ) {
        Column(Modifier.fillMaxHeight()) {
            header?.invoke()
            Box(Modifier.weight(1f)) {
                Layout(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                item = item,
                                selected = index == selectedIndex,
                                dark = dark,
                                colors = colors,
                                metrics = metrics,
                                shape = shape,
                                onClick = { onSelection(index) },
                            )
                        }
                    },
                ) { measurables, constraints ->
                    val widthPx = (metrics.menuWidth + metrics.itemHorizontalMargin * 2).roundToPx()
                    val placeables = measurables.map {
                        it.measure(constraints.copy(minWidth = widthPx, maxWidth = widthPx))
                    }
                    val totalHeight = placeables.sumOf { it.height }
                    val unbounded = constraints.maxHeight == Constraints.Infinity
                    val available = if (unbounded) totalHeight else constraints.maxHeight
                    val spacing = max(
                        ((available * CouiNavigationRailDefaults.menuHeightFactor - totalHeight) /
                            (placeables.size + 1)).roundToInt(),
                        minSpacing.roundToPx(),
                    )
                    val blockHeight = totalHeight + spacing * (placeables.size + 1)
                    val containerHeight = if (unbounded) blockHeight else available
                    layout(constraints.maxWidth, containerHeight) {
                        var y = (containerHeight - blockHeight) / 2
                        placeables.forEach { placeable ->
                            y += spacing
                            placeable.place((constraints.maxWidth - placeable.width) / 2, y)
                            y += placeable.height
                        }
                    }
                }
            }
            footer?.invoke()
        }
        Spacer(Modifier.width(metrics.dividerOffset))
        Box(Modifier.width(metrics.dividerWidth).fillMaxHeight().background(colors.divider))
    }
}

@Composable
private fun NavigationRailItem(
    item: CouiNavigationItem,
    selected: Boolean,
    dark: Boolean,
    colors: CouiNavigationRailColors,
    metrics: CouiNavigationRailMetrics,
    shape: Shape,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .width(metrics.menuWidth + metrics.itemHorizontalMargin * 2)
            .padding(horizontal = metrics.itemHorizontalMargin)
            .clip(shape)
            .background(if (selected) colors.selectedContainer else Color.Transparent)
            .couiStateMask(interactionSource, dark = dark, shape = shape, selected = selected)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(metrics.iconTopPadding))
        Icon(
            modifier = Modifier.size(metrics.iconSize),
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = null,
            tint = if (selected) colors.selectedContent else colors.content,
        )
        Spacer(Modifier.height(metrics.labelTopPadding))
        CouiText(
            text = item.title,
            color = if (selected) colors.selectedContent else colors.content,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = CouiNavigationRailDefaults.labelTextStyle,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        Spacer(Modifier.height(metrics.labelBottomPadding))
    }
}

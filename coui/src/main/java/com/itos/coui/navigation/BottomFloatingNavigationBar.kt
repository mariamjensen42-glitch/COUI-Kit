package com.itos.coui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.itos.coui.effect.couiStateMask
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiIcon as Icon
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme

/**
 * 悬浮底栏：胶囊容器内横向排列图标加文案的导航项，两端可放圆形附件。
 * 项数超出可用宽度时整条内容横向滚动。选中项和提交都由宿主持有。
 */
@Composable
fun BottomFloatingNavigationBar(
    items: List<CouiNavigationItem>,
    selectedIndex: Int,
    onSelection: (Int) -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiBottomFloatingNavigationColors = CouiBottomFloatingNavigationBarDefaults.colors(dark),
    metrics: CouiBottomFloatingNavigationMetrics = CouiBottomFloatingNavigationBarDefaults.metrics,
) {
    require(items.isNotEmpty()) { "悬浮底栏至少需要一项" }
    require(selectedIndex in items.indices) { "选中下标超出导航项范围" }
    Row(
        modifier = modifier.heightIn(min = metrics.height)
            .shadow(metrics.elevation, CouiCapsuleShape, clip = false)
            .background(colors.container, CouiCapsuleShape)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = metrics.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(metrics.itemSpacing),
    ) {
        leading?.invoke()
        items.forEachIndexed { index, item ->
            BottomFloatingNavigationItem(
                item = item,
                selected = index == selectedIndex,
                dark = dark,
                colors = colors,
                metrics = metrics,
                onClick = { onSelection(index) },
            )
        }
        trailing?.invoke()
    }
}

@Composable
private fun BottomFloatingNavigationItem(
    item: CouiNavigationItem,
    selected: Boolean,
    dark: Boolean,
    colors: CouiBottomFloatingNavigationColors,
    metrics: CouiBottomFloatingNavigationMetrics,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(metrics.itemCornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier.heightIn(min = metrics.itemHeight)
            .widthIn(max = metrics.itemMaxWidth)
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
            .padding(horizontal = metrics.itemPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(metrics.iconSize),
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = null,
            tint = if (selected) colors.selectedContent else colors.content,
        )
        Spacer(Modifier.width(metrics.itemIconLabelSpacing))
        CouiText(
            text = item.title,
            color = if (selected) colors.selectedContent else colors.content,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 悬浮底栏附件的圆形按钮外框尺寸，供 [BottomFloatingNavigationBar] 的 `leading`、`trailing` 使用。 */
@Composable
fun BottomFloatingAccessory(
    modifier: Modifier = Modifier,
    metrics: CouiBottomFloatingNavigationMetrics = CouiBottomFloatingNavigationBarDefaults.metrics,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiBottomFloatingNavigationColors = CouiBottomFloatingNavigationBarDefaults.colors(dark),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.size(metrics.accessorySize).clip(CouiCapsuleShape).background(colors.selectedContainer),
        contentAlignment = Alignment.Center,
    ) { content() }
}

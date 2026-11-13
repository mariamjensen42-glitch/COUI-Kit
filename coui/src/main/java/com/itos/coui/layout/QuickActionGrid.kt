package com.itos.coui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/**
 * 网格快捷动作的一项：彩色图标、单行标题和点击回调。
 * 图标由调用方提供，库不假设它是单色矢量，因此不做 tint；宿主负责可访问性描述。
 */
@Stable
class CouiQuickAction(
    val label: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    /** 标题最多显示的行数；原厂「常用信息与密码」这类入口占两行。 */
    val labelMaxLines: Int = 2,
)

/** 快捷入口的两种外观：[Card] 为 24dp 圆角卡片，[Capsule] 为实用工具页的全圆胶囊条目。 */
enum class CouiQuickActionStyle { Card, Capsule }

/**
 * 两列图标快捷卡片，对应 ColorOS 实用工具页的分组入口形态。
 * 每行按 [columns] 等分，末行不足时用空位补齐，卡片宽度不随内容变化。
 * [CouiQuickActionStyle.Card] 沿用 [CouiPageDefaults.shape] 与 [CouiPageColors.card]；
 * [CouiQuickActionStyle.Capsule] 改用 [CouiPageDefaults.quickActionCapsuleShape]、60dp 行高、8dp 间距与 14sp 标签。
 * 整张卡片承担点击，图标与文字不单独响应；禁用时只降低内容不透明度，仍保留占位。
 */
@Composable
fun QuickActionGrid(
    modifier: Modifier = Modifier,
    actions: List<CouiQuickAction>,
    columns: Int = CouiPageDefaults.quickActionColumns,
    style: CouiQuickActionStyle = CouiQuickActionStyle.Card,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
) {
    require(columns > 0) { "列数必须是正整数" }
    val spacing = if (style == CouiQuickActionStyle.Capsule) CouiPageDefaults.quickActionCapsuleSpacing
        else CouiPageDefaults.quickActionSpacing
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing)) {
        actions.chunked(columns).forEach { rowActions ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing)) {
                rowActions.forEach { action ->
                    QuickActionCell(Modifier.weight(1f), action, style, dark, colors)
                }
                repeat(columns - rowActions.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun QuickActionCell(
    modifier: Modifier,
    action: CouiQuickAction,
    style: CouiQuickActionStyle,
    dark: Boolean,
    colors: CouiPageColors,
) {
    val source = remember { MutableInteractionSource() }
    val contentColor = if (action.enabled) colors.title else colors.disabled
    val capsule = style == CouiQuickActionStyle.Capsule
    Row(
        modifier.defaultMinSize(minHeight = if (capsule) CouiPageDefaults.quickActionCapsuleMinHeight
            else CouiPageDefaults.quickActionMinHeight)
            .clip(if (capsule) CouiPageDefaults.quickActionCapsuleShape else CouiPageDefaults.shape)
            .background(colors.card)
            .couiButtonFeedback(dark, source, action.enabled, list = true)
            .clickable(source, null, action.enabled, role = Role.Button, onClick = action.onClick)
            .padding(horizontal = CouiPageDefaults.quickActionPadding, vertical = CouiPageDefaults.contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(CouiPageDefaults.iconSize), contentAlignment = Alignment.Center) { action.icon() }
        Spacer(Modifier.width(if (capsule) CouiPageDefaults.quickActionCapsuleIconSpacing
            else CouiPageDefaults.quickActionIconSpacing))
        Text(text = action.label,
            kind = if (capsule) CouiTextKind.ItemValue else CouiTextKind.ItemTitle,
            color = contentColor,
            maxLines = action.labelMaxLines, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
    }
}

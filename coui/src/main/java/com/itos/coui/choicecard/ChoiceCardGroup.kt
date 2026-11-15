package com.itos.coui.choicecard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.shape.CouiShapes
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 选择卡的轮廓、留白与图标尺寸；三张卡等分宽度时高度取 [minHeight]。 */
object CouiChoiceCardDefaults {
    /** 原生基线；组件默认读取 `CouiTheme.shapes.extraLarge`。 */
    val shape: Shape = CouiShapes.Default.extraLarge
    val padding = 12.dp
    val iconSize = 24.dp
    val iconSpacing = 6.dp
    val spacing = 12.dp
    val minHeight = 92.dp
    val borderWidth = 1.dp
}

/** 一张模式卡：图标、主文字与可选副文字。图标由调用方提供，库不做 tint。 */
@Stable
class CouiChoiceCard(
    val label: String,
    val icon: @Composable () -> Unit,
    val summary: String? = null,
)

/**
 * 横向等分的模式选择卡组，对应 ColorOS 电池页的「均衡 / 省电 / 高性能」。
 *
 * 单选由宿主持有：[selectedIndex] 与 [onSelect] 成对使用，整卡承担点击并暴露 RadioButton 语义，
 * 选中态只叠加强调色描边和强调色文字，不改变卡片高度。`SegmentedControl` 是胶囊分段，
 * 不带图标与副标题，两者不互相替代。
 */
@Composable
fun ChoiceCardGroup(
    modifier: Modifier = Modifier,
    items: List<CouiChoiceCard>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
) {
    require(items.isNotEmpty()) { "选择卡组至少需要一项" }
    Row(modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(CouiChoiceCardDefaults.spacing)) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            ChoiceCardCell(Modifier.weight(1f), item, selected, dark, colors) { onSelect(index) }
        }
    }
}

@Composable
private fun ChoiceCardCell(
    modifier: Modifier,
    item: CouiChoiceCard,
    selected: Boolean,
    dark: Boolean,
    colors: CouiPageColors,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val contentColor = if (selected) colors.accent else colors.title
    val shape = CouiTheme.shapes.extraLarge
    Column(modifier.defaultMinSize(minHeight = CouiChoiceCardDefaults.minHeight)
        .clip(shape)
        .background(colors.card)
        .then(if (selected) Modifier.border(CouiChoiceCardDefaults.borderWidth, colors.accent,
            shape) else Modifier)
        .couiButtonFeedback(dark, source)
        .selectable(selected = selected, interactionSource = source, indication = null,
            role = Role.RadioButton, onClick = onClick)
        .padding(CouiChoiceCardDefaults.padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(CouiChoiceCardDefaults.iconSize), contentAlignment = Alignment.Center) { item.icon() }
        Text(text = item.label, kind = CouiTextKind.ItemSummary, color = contentColor,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = CouiChoiceCardDefaults.iconSpacing))
        if (!item.summary.isNullOrEmpty()) {
            Text(text = item.summary, kind = CouiTextKind.Body,
                color = if (selected) colors.accent else colors.summary,
                textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

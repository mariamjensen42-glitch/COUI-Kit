package com.itos.coui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.checkbox.Checkbox
import com.itos.coui.checkbox.CouiCheckboxDefaults
import com.itos.coui.effect.couiStateMask
import com.itos.coui.radiobutton.CouiRadioButtonDefaults
import com.itos.coui.radiobutton.RadioButton
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme

/** 列表项。单选项与多选项可附带次要说明。 */
@Immutable
data class CouiListItem(
    val text: String,
    val summary: String? = null,
    val enabled: Boolean = true,
)

/**
 * 列表弹窗：纯文本操作列表、单选列表与多选列表。
 *
 * 每项最矮 50dp，左右 20dp、上下 16dp；分隔线为 1 像素高并在左右各缩进 20dp，末项不画。
 * 多选可设上限，达到上限后继续勾选只回调 [onMaxChecked]，不改变选择。
 */
@Composable
fun ListDialog(
    items: List<CouiListItem>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    selection: CouiListSelection = CouiListSelection.None,
    onItemClick: ((Int) -> Unit)? = null,
    checkedIndex: Int = -1,
    onCheckedIndexChange: ((Int) -> Unit)? = null,
    checkedIndices: Set<Int> = emptySet(),
    onCheckedIndicesChange: ((Set<Int>) -> Unit)? = null,
    maxChecked: Int? = null,
    onMaxChecked: ((Int) -> Unit)? = null,
    confirmAction: CouiDialogAction? = null,
    dismissAction: CouiDialogAction? = null,
    placement: CouiDialogPlacement = CouiDialogPlacement.Bottom,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiDialogColors = CouiDialogDefaults.themeColors(dark),
    accent: Color = CouiTheme.colorSchemeFor(dark).accent,
    shape: Shape = CouiTheme.shapes.dialog,
    cornerRadius: Dp = Dp.Unspecified,
    material: CouiDialogMaterial? = null,
    backdrop: CouiBackdrop? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    visible: Boolean? = null,
) {
    require(items.isNotEmpty()) { "列表弹窗至少需要一项" }
    require(maxChecked == null || maxChecked > 0) { "多选上限必须为正数" }
    require(checkedIndex in -1 until items.size) { "单选下标超出列表范围" }
    require(checkedIndices.all { it in items.indices }) { "多选下标超出列表范围" }
    CouiDialogWindow(modifier, onDismissRequest, colors, dark, placement, shape, cornerRadius, properties,
        material, backdrop, visible) {
        val configuration = LocalConfiguration.current
        val dividerColor = CouiTheme.colorSchemeFor(dark).outlineVariant
        var stacked by remember { mutableIntStateOf(0) }
        Column(Modifier.fillMaxWidth()) {
            if (!title.isNullOrEmpty()) Box(Modifier.fillMaxWidth().padding(
                start = CouiDialogDefaults.contentPadding, end = CouiDialogDefaults.contentPadding,
                top = CouiDialogDefaults.titleTop, bottom = CouiDialogDefaults.titleBottom)) {
                DialogText(text = title, kind = DialogTextKind.Title, color = colors.title,
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(max = CouiDialogDefaults.titleMaxHeight(configuration.screenHeightDp))
                        .verticalScroll(rememberScrollState()))
            }
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(top = CouiListDialogDefaults.panelTopPadding,
                    bottom = CouiListDialogDefaults.listBottomPadding)) {
                items.forEachIndexed { index, item ->
                    val selected = when (selection) {
                        CouiListSelection.None -> false
                        CouiListSelection.Single -> checkedIndex == index
                        CouiListSelection.Multi -> index in checkedIndices
                    }
                    val activate: (() -> Unit)? = when (selection) {
                        CouiListSelection.None -> onItemClick?.let { callback -> { callback(index) } }
                        CouiListSelection.Single -> {
                            val callback = onCheckedIndexChange ?: onItemClick
                            callback?.let { { it(index) } }
                        }
                        CouiListSelection.Multi -> onCheckedIndicesChange?.let { callback ->
                            {
                                val limit = maxChecked
                                if (selected) callback(checkedIndices - index)
                                else if (limit == null || checkedIndices.size < limit) callback(checkedIndices + index)
                                else {
                                    onMaxChecked?.invoke(limit)
                                    Unit
                                }
                            }
                        }
                    }
                    ListRow(
                        item = item,
                        selection = selection,
                        selected = selected,
                        dark = dark,
                        colors = colors,
                        accent = accent,
                        onClick = if (item.enabled) activate else null,
                    )
                    if (index != items.lastIndex) Box(Modifier.fillMaxWidth()
                        .padding(horizontal = CouiListDialogDefaults.dividerHorizontalPadding)
                        .height(CouiListDialogDefaults.dividerThickness)
                        .background(dividerColor))
                }
            }
            if (confirmAction != null) {
                DialogActions(confirmAction, dismissAction, null, colors, dark, material != null) {
                    val next = if (it) 1 else 0
                    if (stacked != next) stacked = next
                }
            }
        }
    }
}

@Composable
private fun ListRow(
    item: CouiListItem,
    selection: CouiListSelection,
    selected: Boolean,
    dark: Boolean,
    colors: CouiDialogColors,
    accent: Color,
    onClick: (() -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = CouiListDialogDefaults.itemMinHeight)
            .couiStateMask(interactionSource, dark = dark, enabled = onClick != null,
                shape = CouiTheme.shapes.extraSmall)
            .clickable(interactionSource = interactionSource, indication = null, enabled = onClick != null,
                role = if (selection == CouiListSelection.None) Role.Button else Role.Checkbox) {
                onClick?.invoke()
            }
            .padding(horizontal = CouiListDialogDefaults.itemHorizontalPadding,
                vertical = CouiListDialogDefaults.itemVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            CouiText(text = item.text, color = if (item.enabled) colors.title else colors.message,
                fontSize = 16.sp, fontWeight = FontWeight.W500)
            val summary = item.summary
            if (!summary.isNullOrEmpty()) CouiText(text = summary, color = colors.message,
                fontSize = 12.sp)
        }
        when (selection) {
            CouiListSelection.None -> Unit
            CouiListSelection.Single -> RadioButton(selected = selected, enabled = item.enabled,
                colors = CouiRadioButtonDefaults.colors(accent, dark))
            CouiListSelection.Multi -> Checkbox(checked = selected, enabled = item.enabled,
                colors = CouiCheckboxDefaults.colors(accent, dark))
        }
    }
}

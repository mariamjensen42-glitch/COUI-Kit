package com.itos.coui.searchhistory

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.itos.coui.flowlayout.CouiFlowItem
import com.itos.coui.flowlayout.CouiFlowLayoutColors
import com.itos.coui.flowlayout.CouiFlowLayoutDefaults
import com.itos.coui.flowlayout.FlowLayout
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/**
 * 搜索历史区：标题、清空按钮与可折叠的[换行标签布局][FlowLayout]。
 *
 * 条目、展开态、点击与清空动作都由宿主持有。点击清空先按原厂 `COUISearchHistoryView` 的
 * 300ms 整块淡出，动画结束后才回调 [onDelete]；宿主清空 [items] 后整块不再占位。
 * 展开后最多显示 `CouiFlowLayoutDefaults.searchHistoryMaxRowsUnfolded` 行，
 * 折叠行数由 [maxRowsFolded] 指定（原厂默认不折叠，实际页面通常取 2）。
 */
@Composable
fun SearchHistory(
    modifier: Modifier = Modifier,
    title: String,
    items: List<CouiFlowItem>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (Int) -> Unit,
    onDelete: () -> Unit,
    deleteContentDescription: String,
    maxRowsFolded: Int = CouiFlowLayoutDefaults.maxRowsFolded,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiFlowLayoutColors = CouiFlowLayoutDefaults.colors(dark),
    expandContentDescription: String? = null,
    collapseContentDescription: String? = null,
    deleteContent: @Composable () -> Unit = {
        SearchHistoryDeleteGlyph(color = colors.control)
    },
) {
    if (items.isEmpty()) return
    val deleteAlpha = remember { Animatable(1f) }
    // Animatable.value 不是 snapshot state；淡出帧要经 asState() 才能触发重组。
    val deleteAlphaState = deleteAlpha.asState()
    var deleting by remember { mutableStateOf(false) }
    LaunchedEffect(deleting) {
        if (!deleting) return@LaunchedEffect
        deleteAlpha.animateTo(0f, tween(
            durationMillis = CouiSearchHistoryDefaults.deleteDurationMillis,
            easing = CouiFlowLayoutDefaults.MoveEase,
        ))
        onDelete()
        deleting = false
        deleteAlpha.snapTo(1f)
    }

    Column(
        modifier
            .alpha(deleteAlphaState.value)
            .padding(
                start = CouiSearchHistoryDefaults.paddingStart,
                top = CouiSearchHistoryDefaults.paddingTop,
                end = CouiSearchHistoryDefaults.paddingEnd,
                bottom = CouiSearchHistoryDefaults.paddingBottom,
            ),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = CouiSearchHistoryDefaults.titleMarginVertical),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                kind = CouiTextKind.ButtonLabel,
                color = CouiTheme.colorSchemeFor(dark).onSurface,
                modifier = Modifier.weight(1f),
            )
            val interaction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(CouiSearchHistoryDefaults.deleteTouchTargetSize)
                    .semantics { contentDescription = deleteContentDescription }
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                    ) { if (!deleting) deleting = true },
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(CouiSearchHistoryDefaults.deleteIconSize)) { deleteContent() }
            }
        }
        FlowLayout(
            modifier = Modifier.padding(top = CouiSearchHistoryDefaults.flowMarginTop),
            items = items,
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            onItemClick = onItemClick,
            maxRowsFolded = maxRowsFolded,
            maxRowsUnfolded = CouiFlowLayoutDefaults.searchHistoryMaxRowsUnfolded,
            dark = dark,
            colors = colors,
            expandContentDescription = expandContentDescription,
            collapseContentDescription = collapseContentDescription,
        )
    }
}

/** 默认清空标记；核心不依赖图标模块，用 Canvas 自绘垃圾桶轮廓。 */
@Composable
private fun SearchHistoryDeleteGlyph(color: Color) {
    Canvas(Modifier.size(CouiSearchHistoryDefaults.deleteIconSize)) {
        val w = size.width
        val h = size.height
        val stroke = 1.2.dp.toPx()
        drawLine(color, Offset(w * .16f, h * .25f), Offset(w * .84f, h * .25f), strokeWidth = stroke)
        drawLine(color, Offset(w * .34f, h * .25f), Offset(w * .34f, h * .17f), strokeWidth = stroke)
        drawLine(color, Offset(w * .66f, h * .25f), Offset(w * .66f, h * .17f), strokeWidth = stroke)
        drawLine(color, Offset(w * .34f, h * .17f), Offset(w * .66f, h * .17f), strokeWidth = stroke)
        drawLine(color, Offset(w * .25f, h * .33f), Offset(w * .31f, h * .86f), strokeWidth = stroke)
        drawLine(color, Offset(w * .31f, h * .86f), Offset(w * .69f, h * .86f), strokeWidth = stroke)
        drawLine(color, Offset(w * .69f, h * .86f), Offset(w * .75f, h * .33f), strokeWidth = stroke)
        drawLine(color, Offset(w * .25f, h * .33f), Offset(w * .75f, h * .33f), strokeWidth = stroke)
    }
}

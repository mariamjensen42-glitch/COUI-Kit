package com.cycling.coui.flowlayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.text.rememberCouiTextStyle
import com.cycling.coui.theme.CouiTheme

/** 一次换行测量后的标签几何，宽度为 px。 */
private data class FlowChip(val index: Int, val item: CouiFlowItem, val width: Float)

/**
 * 按内容宽度自动换行的标签布局，可折叠到指定行数。
 *
 * [expanded] 与 [onExpandedChange] 由宿主持有；组件只在点击展开/折叠按钮时请求切换，
 * 不修改状态。折叠时超出行数的标签按原厂 `COUIFlowLayout` 处理：仍在布局里，但 alpha 置 0
 * 并被容器高度裁剪，展开时延迟 100ms 在 400ms 内淡入；容器高度随状态立即切换。
 *
 * 每行放不下的标签换到下一行；超过 [maxRowsUnfolded] 的标签不再排版。第 [maxRowsFolded] 行
 * 末尾放展开按钮，放不下时先让该行最后一个标签退位；折叠按钮放在最后一行末尾。
 * 按钮内容由 [expandContent] / [collapseContent] 提供，默认绘制向下/向上的折线。
 */
@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    items: List<CouiFlowItem>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: ((Int) -> Unit)? = null,
    maxRowsFolded: Int = CouiFlowLayoutDefaults.maxRowsFolded,
    maxRowsUnfolded: Int = CouiFlowLayoutDefaults.maxRowsUnfolded,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiFlowLayoutColors = CouiFlowLayoutDefaults.colors(dark),
    expandContentDescription: String? = null,
    collapseContentDescription: String? = null,
    expandContent: @Composable () -> Unit = {
        FlowChevron(down = true, color = colors.control)
    },
    collapseContent: @Composable () -> Unit = {
        FlowChevron(down = false, color = colors.control)
    },
) {
    require(maxRowsFolded > 0) { "maxRowsFolded 至少为 1" }
    require(maxRowsUnfolded >= maxRowsFolded) { "maxRowsUnfolded 不能小于 maxRowsFolded" }
    if (items.isEmpty()) return

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val textStyle = rememberCouiTextStyle(CouiTextKind.Body)
        val availableWidth: Dp = if (maxWidth.value.isFinite() && maxWidth.value > 0f) maxWidth
        else CouiFlowLayoutDefaults.fallbackWidth
        val availablePx = with(density) { availableWidth.toPx() }
        val spacingPx = with(density) { CouiFlowLayoutDefaults.itemSpacing.toPx() }
        val horizontalPaddingPx = with(density) { CouiFlowLayoutDefaults.chipHorizontalPadding.toPx() }
        val minWidthPx = with(density) { CouiFlowLayoutDefaults.chipMinWidth.toPx() }
        val maxWidthPx = with(density) { CouiFlowLayoutDefaults.chipMaxWidth.toPx() }
        val controlPx = with(density) { CouiFlowLayoutDefaults.controlSize.toPx() }

        // 与原厂 Line.canAdd 相同：首个标签独占一行时按可用宽度夹紧，其余按「已用 + 间距 + 宽度」判断。
        val measured = items.mapIndexed { index, item ->
            val textWidth = textMeasurer.measure(
                text = item.label, style = textStyle, maxLines = 1,
            ).size.width.toFloat()
            FlowChip(index, item,
                (textWidth + horizontalPaddingPx * 2).coerceIn(minWidthPx, maxWidthPx)
                    .coerceAtMost(availablePx))
        }
        val allLines = mutableListOf<MutableList<FlowChip>>()
        for (chip in measured) {
            val line = allLines.lastOrNull()
            when {
                line == null -> allLines.add(mutableListOf(chip))
                line.isEmpty() || line.sumOf { it.width.toDouble() }.toFloat() +
                    spacingPx + chip.width <= availablePx -> line.add(chip)
                allLines.size >= maxRowsUnfolded -> Unit
                else -> allLines.add(mutableListOf(chip))
            }
        }

        var displayLines: List<List<FlowChip>> = allLines.map { it.toList() }
        val controlRow = maxRowsFolded - 1
        val expandControl = displayLines.getOrNull(controlRow)?.let { row ->
            // 展开按钮占第 maxRowsFolded 行末位；该行放不下时先移除最后一个标签。
            val used = row.sumOf { it.width.toDouble() }.toFloat() + spacingPx * row.size
            if (used + controlPx > availablePx && row.isNotEmpty()) {
                displayLines = displayLines.toMutableList().also { it[controlRow] = row.dropLast(1) }
            }
            true
        } ?: false
        val collapseControl = displayLines.size > maxRowsFolded
        if (collapseControl) {
            val last = displayLines.lastIndex
            val row = displayLines[last]
            val used = row.sumOf { it.width.toDouble() }.toFloat() + spacingPx * row.size
            if (used + controlPx > availablePx && row.isNotEmpty()) {
                displayLines = displayLines.toMutableList().also { it[last] = row.dropLast(1) }
            }
        }

        val hiddenAlpha by animateFloatAsState(
            targetValue = if (expanded) 1f else 0f,
            animationSpec = tween(
                durationMillis = if (expanded) CouiFlowLayoutDefaults.hiddenViewsAppearDuration
                else CouiFlowLayoutDefaults.hiddenViewsDisappearDuration,
                delayMillis = if (expanded) CouiFlowLayoutDefaults.hiddenViewsAppearDelay else 0,
                easing = CouiFlowLayoutDefaults.MoveEase,
            ),
            label = "flowHiddenAlpha",
        )
        val controlAlpha by animateFloatAsState(
            targetValue = if (expanded) 0f else 1f,
            animationSpec = tween(
                durationMillis = if (expanded) CouiFlowLayoutDefaults.expandButtonAppearMillis
                else CouiFlowLayoutDefaults.hiddenViewsDisappearDuration,
                easing = CouiFlowLayoutDefaults.MoveEase,
            ),
            label = "flowExpandAlpha",
        )

        val visibleRows = if (expanded) displayLines.size
        else displayLines.size.coerceAtMost(maxRowsFolded)
        val chipHeight = CouiFlowLayoutDefaults.chipHeight
        val lineSpacing = CouiFlowLayoutDefaults.lineSpacing
        val containerHeight = chipHeight * visibleRows + lineSpacing * (visibleRows - 1).coerceAtLeast(0)

        Box(Modifier.height(containerHeight).clipToBounds()) {
            Column(verticalArrangement = Arrangement.spacedBy(lineSpacing)) {
                displayLines.forEachIndexed { rowIndex, line ->
                    val hidden = rowIndex >= maxRowsFolded
                    Row(
                        modifier = Modifier.alpha(if (hidden) hiddenAlpha else 1f),
                        horizontalArrangement = Arrangement.spacedBy(CouiFlowLayoutDefaults.itemSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        line.forEach { chip ->
                            FlowChipContent(chip, colors, onItemClick)
                        }
                        if (expandControl && rowIndex == controlRow) {
                            FlowControlButton(controlAlpha, onExpandedChange, true,
                                expandContentDescription, expandContent)
                        }
                        if (collapseControl && rowIndex == displayLines.lastIndex) {
                            FlowControlButton(hiddenAlpha, onExpandedChange, false,
                                collapseContentDescription, collapseContent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowControlButton(
    alpha: Float,
    onExpandedChange: (Boolean) -> Unit,
    expand: Boolean,
    contentDescription: String?,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier.size(CouiFlowLayoutDefaults.controlSize)
            .alpha(alpha)
            .then(
                if (contentDescription == null) Modifier else Modifier.semantics {
                    this.contentDescription = contentDescription
                },
            )
            .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                onExpandedChange(expand)
            },
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun FlowChipContent(
    chip: FlowChip,
    colors: CouiFlowLayoutColors,
    onItemClick: ((Int) -> Unit)?,
) {
    val interaction = remember { MutableInteractionSource() }
    val chipModifier = Modifier
        .widthIn(min = CouiFlowLayoutDefaults.chipMinWidth, max = CouiFlowLayoutDefaults.chipMaxWidth)
        .height(CouiFlowLayoutDefaults.chipHeight)
        .background(colors.chipContainer, RoundedCornerShape(50))
        .then(
            if (onItemClick == null) Modifier else Modifier.clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
            ) { onItemClick(chip.index) },
        )
    Box(
        chipModifier.padding(horizontal = CouiFlowLayoutDefaults.chipHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = chip.item.label,
            kind = CouiTextKind.Body,
            color = colors.chipContent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 默认展开/折叠标记；核心不依赖图标模块，用 Canvas 自绘一条折线。 */
@Composable
private fun FlowChevron(down: Boolean, color: Color) {
    Canvas(Modifier.size(CouiFlowLayoutDefaults.controlSize)) {
        val half = 4.dp.toPx()
        val rise = 2.dp.toPx()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val dir = if (down) 1f else -1f
        val strokeWidth = 1.5.dp.toPx()
        drawLine(color, Offset(cx - half, cy - rise * dir), Offset(cx, cy + rise * dir),
            strokeWidth = strokeWidth)
        drawLine(color, Offset(cx, cy + rise * dir), Offset(cx + half, cy - rise * dir),
            strokeWidth = strokeWidth)
    }
}

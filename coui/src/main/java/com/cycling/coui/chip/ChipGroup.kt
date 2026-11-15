package com.cycling.coui.chip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.effect.couiPressRipple
import com.cycling.coui.motion.couiPressMotion
import com.cycling.coui.motion.rememberCouiPressMotion
import com.cycling.coui.shape.CouiCapsuleShape
import com.cycling.coui.theme.CouiText
import com.cycling.coui.theme.CouiTheme
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 一组标签，按可用宽度自动换行。
 *
 * 行距与列距取 [spacingHorizontal] / [spacingVertical]；[singleLine] 时不换行。
 * [collapsable] 为真时末尾跟一个折叠按钮：折起后只保留 [collapsedMaxRows] 行，
 * 其余标签不参与布局，行高变化按原厂 response 0.35、bounce 0 的弹簧过渡。
 *
 * 单选与必选语义由宿主维护——[content] 里每个 [Chip] 的选中值、关闭与点击都是受控的。
 */
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    collapsable: Boolean = false,
    collapsed: Boolean = false,
    onCollapsedChange: ((Boolean) -> Unit)? = null,
    collapsedMaxRows: Int = CouiChipDefaults.defaultCollapsedMaxRows,
    spacingHorizontal: Dp = CouiChipDefaults.groupSpacingHorizontal,
    spacingVertical: Dp = CouiChipDefaults.groupSpacingVertical,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val spacingHPx = with(density) { spacingHorizontal.toPx() }
    val spacingVPx = with(density) { spacingVertical.toPx() }
    val targetHeight = remember { mutableFloatStateOf(0f) }
    val animatedHeight = remember { Animatable(0f) }

    LaunchedEffect(targetHeight.floatValue) {
        val target = targetHeight.floatValue
        if (target <= 0f) return@LaunchedEffect
        if (animatedHeight.value <= 0f) {
            animatedHeight.snapTo(target)
        } else if (animatedHeight.value != target) {
            animatedHeight.animateTo(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = 1f,
                    stiffness = CouiChipDefaults.stiffness(CouiChipDefaults.groupResponse),
                ),
            )
        }
    }

    Layout(
        content = {
            content()
            if (collapsable) {
                CollapsableChipButton(
                    collapsed = collapsed,
                    enabled = onCollapsedChange != null,
                    onClick = { onCollapsedChange?.invoke(!collapsed) },
                )
            }
        },
        modifier = modifier.clipToBounds(),
    ) { measurables, constraints ->
        val chipMeasurables = if (collapsable) measurables.dropLast(1) else measurables
        val button = if (collapsable) measurables.last().measure(Constraints()) else null
        val chips = chipMeasurables.map { it.measure(Constraints(maxWidth = constraints.maxWidth)) }
        val widths = chips.map { it.width.toFloat() }
        val heights = chips.map { it.height.toFloat() }
        val available = if (constraints.maxWidth == Constraints.Infinity) {
            widths.sum() + spacingHPx * max(0, widths.size - 1)
        } else constraints.maxWidth.toFloat()
        val limitRows = when {
            singleLine -> 1
            collapsable && collapsed -> max(1, collapsedMaxRows)
            else -> Int.MAX_VALUE
        }

        val buttonWidth = button?.width?.toFloat() ?: 0f
        val buttonHeight = button?.height?.toFloat() ?: 0f
        var flow = flowChips(widths, heights, available, spacingHPx, spacingVPx, limitRows)
        if (button != null && flow.visibleCount < widths.size) {
            flow = flowChips(
                widths, heights, available, spacingHPx, spacingVPx, limitRows,
                lastRowCapacity = available - buttonWidth - spacingHPx,
            )
        }

        var naturalHeight = flow.totalHeight
        var buttonX = 0f
        var buttonY = 0f
        if (button != null) {
            val fitsOnLastRow = flow.lastRowTop >= 0f &&
                flow.lastRowEndX + buttonWidth <= available
            if (fitsOnLastRow) {
                buttonX = flow.lastRowEndX
                buttonY = flow.lastRowTop + (flow.lastRowHeight - buttonHeight) / 2f
            } else {
                buttonX = 0f
                buttonY = flow.totalHeight + spacingVPx
                naturalHeight = buttonY + buttonHeight
            }
        }

        if (targetHeight.floatValue != naturalHeight) targetHeight.floatValue = naturalHeight
        val animated = animatedHeight.value
        val heightPx = (if (animated <= 0f) naturalHeight else animated).roundToInt()
            .let { if (constraints.maxHeight == Constraints.Infinity) it else it.coerceIn(0, constraints.maxHeight) }
        val widthPx = if (constraints.maxWidth == Constraints.Infinity) {
            flow.maxRight.roundToInt()
        } else constraints.maxWidth

        layout(widthPx, heightPx) {
            chips.forEachIndexed { index, placeable ->
                val placement = flow.placements.getOrNull(index) ?: return@forEachIndexed
                placeable.placeRelative(placement.x.roundToInt(), placement.y.roundToInt())
            }
            button?.placeRelative(buttonX.roundToInt(), buttonY.roundToInt())
        }
    }
}

/**
 * 标签输入组：已有标签后跟一个内联输入框，输入框随文字变宽，放不下时换到下一行。
 *
 * 输入内容由宿主持有；[onSubmit] 在键盘确认键触发，宿主据此新增标签并清空 [text]。
 */
@Composable
fun ChipGroupEditText(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    spacingHorizontal: Dp = CouiChipDefaults.groupSpacingHorizontal,
    spacingVertical: Dp = CouiChipDefaults.groupSpacingVertical,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val spacingHPx = with(density) { spacingHorizontal.toPx() }
    val spacingVPx = with(density) { spacingVertical.toPx() }
    val inputMinPx = with(density) { CouiChipDefaults.inputMinWidth.toPx() }
    val rowMinPx = with(density) { CouiChipDefaults.metrics(CouiChipStyle.Input).minHeight.toPx() }
    val colors = CouiChipDefaults.colors(
        CouiTheme.isDark, CouiTheme.colorScheme.accent, CouiTheme.colorScheme.onAccent,
    )

    Layout(
        content = {
            content()
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.widthIn(min = CouiChipDefaults.inputMinWidth),
                enabled = enabled,
                textStyle = CouiChipDefaults.inputTextStyle.copy(color = colors.uncheckedContent),
                singleLine = true,
                cursorBrush = SolidColor(CouiTheme.colorScheme.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (text.isNotEmpty()) onSubmit(text) }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty() && placeholder != null) {
                            CouiText(
                                text = placeholder,
                                color = colors.uncheckedContent.copy(alpha = .4f),
                                style = CouiChipDefaults.inputTextStyle,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val chipMeasurables = measurables.dropLast(1)
        val fieldMeasurable = measurables.last()
        val chips = chipMeasurables.map { it.measure(Constraints(maxWidth = constraints.maxWidth)) }
        val widths = chips.map { it.width.toFloat() }
        val heights = chips.map { it.height.toFloat() }
        val available = if (constraints.maxWidth == Constraints.Infinity) {
            widths.sum() + spacingHPx * max(0, widths.size - 1) + inputMinPx
        } else constraints.maxWidth.toFloat()
        val flow = flowChips(widths, heights, available, spacingHPx, spacingVPx, Int.MAX_VALUE)

        val remaining = available - flow.lastRowEndX
        val fieldOnLastRow = widths.isNotEmpty() && remaining >= inputMinPx
        val fieldX = if (fieldOnLastRow) flow.lastRowEndX else 0f
        val fieldTop = if (fieldOnLastRow) flow.lastRowTop else flow.totalHeight + if (widths.isEmpty()) 0f else spacingVPx
        val fieldMaxWidth = if (fieldOnLastRow) remaining else available
        val field = fieldMeasurable.measure(
            Constraints(
                minWidth = inputMinPx.roundToInt(),
                maxWidth = max(inputMinPx, fieldMaxWidth).roundToInt(),
            ),
        )
        val rowHeight = max(flow.lastRowHeight.takeIf { fieldOnLastRow } ?: 0f, max(rowMinPx, field.height.toFloat()))
        val totalHeight = if (fieldOnLastRow) {
            max(flow.totalHeight, flow.lastRowTop + rowHeight)
        } else {
            fieldTop + rowHeight
        }
        val widthPx = if (constraints.maxWidth == Constraints.Infinity) {
            flow.maxRight.roundToInt()
        } else constraints.maxWidth

        layout(widthPx, totalHeight.roundToInt()) {
            chips.forEachIndexed { index, placeable ->
                val placement = flow.placements.getOrNull(index) ?: return@forEachIndexed
                placeable.placeRelative(placement.x.roundToInt(), placement.y.roundToInt())
            }
            val offsetY = ((rowHeight - field.height) / 2f).roundToInt()
            field.placeRelative(fieldX.roundToInt(), fieldTop.roundToInt() + offsetY)
        }
    }
}

/** 折叠按钮：`Widget.COUI.Chip.Suggestion.Collapsable`，28×28 的胶囊底色加描边箭头。 */
@Composable
private fun CollapsableChipButton(collapsed: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val colors = CouiChipDefaults.colors(
        CouiTheme.isDark, CouiTheme.colorScheme.accent, CouiTheme.colorScheme.onAccent,
    )
    val press = rememberCouiPressMotion()
    val interactionSource = remember { MutableInteractionSource() }
    val path = remember(collapsed) {
        PathParser().parsePathString(
            if (collapsed) CouiChipPaths.expand else CouiChipPaths.collapse,
        ).toPath()
    }
    Box(
        Modifier
            .size(CouiChipDefaults.collapsableButtonSize)
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .couiPressMotion(press, enabled = enabled)
            .couiPressRipple(interactionSource, enabled = enabled, shape = CouiCapsuleShape)
            .clip(CouiCapsuleShape)
            .background(colors.uncheckedBackground)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(16.dp)) {
            val factor = size.width / CouiChipPaths.collapseViewport
            scale(factor, factor, pivot = Offset.Zero) {
                drawPath(
                    path = path,
                    color = colors.uncheckedContent,
                    style = Stroke(width = .933333f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

internal data class ChipPlacement(val x: Float, val y: Float, val width: Float, val height: Float)

internal class CouiFlow(
    val placements: List<ChipPlacement>,
    val visibleCount: Int,
    val totalHeight: Float,
    val maxRight: Float,
    val lastRowTop: Float,
    val lastRowEndX: Float,
    val lastRowHeight: Float,
)

/**
 * 按原厂 `COUIChipGroup` 的换行规则排布：行内累加宽度与水平间距，
 * 放不下就换行；到 [maxRows] 上限后剩余项不参与布局（visibleCount 之外）。
 * [lastRowCapacity] 用于给最后一行的折叠按钮预留宽度。
 */
internal fun flowChips(
    widths: List<Float>,
    heights: List<Float>,
    available: Float,
    spacingHorizontal: Float,
    spacingVertical: Float,
    maxRows: Int,
    lastRowCapacity: Float = available,
): CouiFlow {
    val placements = ArrayList<ChipPlacement>(widths.size)
    var x = 0f
    var y = 0f
    var row = 0
    var rowHeight = 0f
    var maxRight = 0f
    for (index in widths.indices) {
        val width = widths[index]
        val height = heights[index]
        val capacity = if (row == maxRows - 1) lastRowCapacity else available
        if (x > 0f && x + width > capacity) {
            if (row + 1 >= maxRows) break
            row++
            y += rowHeight + spacingVertical
            x = 0f
            rowHeight = 0f
        }
        placements.add(ChipPlacement(x, y, width, height))
        rowHeight = max(rowHeight, height)
        maxRight = max(maxRight, x + width)
        x += width + spacingHorizontal
    }
    val totalHeight = if (placements.isEmpty()) 0f else y + rowHeight
    return CouiFlow(
        placements = placements,
        visibleCount = placements.size,
        totalHeight = totalHeight,
        maxRight = maxRight,
        lastRowTop = y,
        lastRowEndX = if (placements.isEmpty()) 0f else x - spacingHorizontal,
        lastRowHeight = rowHeight,
    )
}

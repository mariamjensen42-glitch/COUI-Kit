package com.cycling.coui.blocks.quicksetting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme
import kotlin.math.abs

/** 一格的落位：列与行都从 0 开始。 */
@Immutable
internal data class CouiDeformationPlacement(val column: Int, val row: Int)

/** 按跨度把磁贴排进固定列数，返回每格落位与总行数；放不下的 2×1 磁贴整体换行。 */
internal fun flowDeformationSpans(
    spans: List<CouiDeformationSpan>,
    columns: Int,
): Pair<List<CouiDeformationPlacement>, Int> {
    require(columns > 0) { "columns 必须为正数" }
    val placements = ArrayList<CouiDeformationPlacement>(spans.size)
    var column = 0
    var row = 0
    spans.forEach { span ->
        val width = span.columns.coerceAtMost(columns)
        if (column + width > columns) {
            column = 0
            row++
        }
        placements += CouiDeformationPlacement(column, row)
        column += width
    }
    return placements to if (spans.isEmpty()) 0 else row + 1
}

/**
 * 形变磁贴网格：磁贴在 `1×1` 与 `2×1` 之间切换跨度，网格按跨度重排，尺寸与位置用弹簧过渡。
 *
 * 编辑态下每块磁贴叠一圈描边，右缘出现竖向拖动把手：横向拖动超过一格宽度的
 * [CouiDeformationTileDefaults.spanToggleFraction] 即切换跨度并回调 [onSpanChange]，
 * 其余磁贴随即重排。对应 SystemUI 的 `DeformationFrameLayout` 与 `DragBarView`。
 *
 * [tile] 由宿主绘制磁贴内容，`editing` 为真时可自绘按下态；容器只负责底色、描边与排布。
 */
@Composable
fun <T> DeformationTileGrid(
    items: List<T>,
    spanOf: (T) -> CouiDeformationSpan,
    modifier: Modifier = Modifier,
    columns: Int = CouiDeformationTileDefaults.columns,
    editing: Boolean = false,
    gap: Dp = CouiDeformationTileDefaults.gap,
    cornerRadius: Dp = CouiDeformationTileDefaults.cornerRadius,
    colors: CouiDeformationTileColors = CouiDeformationTileDefaults.colors(CouiTheme.isDark),
    onSpanChange: (T, CouiDeformationSpan) -> Unit = { _, _ -> },
    tile: @Composable (T, CouiDeformationSpan, Boolean) -> Unit,
) {
    require(columns > 0) { "columns 必须为正数" }
    val spans = items.map(spanOf)
    val (placements, rows) = flowDeformationSpans(spans, columns)
    val reshapeSpring = spring<Dp>(
        dampingRatio = 1f,
        stiffness = CouiDeformationTileDefaults.stiffness(),
        visibilityThreshold = .5.dp,
    )

    BoxWithConstraints(modifier) {
        val cell = (maxWidth - gap * (columns - 1)) / columns
        val gridHeight by animateDpAsState(
            targetValue = if (rows == 0) 0.dp else cell * rows + gap * (rows - 1),
            animationSpec = reshapeSpring,
        )
        Box(Modifier.height(gridHeight)) {
            items.forEachIndexed { index, item ->
                key(index) {
                    val placement = placements[index]
                    val span = spans[index]
                    val spanColumns = span.columns.coerceAtMost(columns)
                    val width by animateDpAsState(
                        targetValue = cell * spanColumns + gap * (spanColumns - 1),
                        animationSpec = reshapeSpring,
                    )
                    val x by animateDpAsState(
                        targetValue = cell * placement.column + gap * placement.column,
                        animationSpec = reshapeSpring,
                    )
                    val y by animateDpAsState(
                        targetValue = cell * placement.row + gap * placement.row,
                        animationSpec = reshapeSpring,
                    )
                    Box(
                        Modifier
                            .offset(x = x, y = y)
                            .size(width = width, height = cell)
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(colors.container),
                    ) {
                        tile(item, span, editing)
                        if (editing) {
                            DeformationTileFrame(
                                cornerRadius = cornerRadius,
                                colors = colors,
                                onSpanChange = { onSpanChange(item, toggled(span)) },
                                cellSize = cell,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun toggled(span: CouiDeformationSpan): CouiDeformationSpan =
    if (span == CouiDeformationSpan.OneByOne) CouiDeformationSpan.TwoByOne else CouiDeformationSpan.OneByOne

@Composable
private fun BoxScope.DeformationTileFrame(
    cornerRadius: Dp,
    colors: CouiDeformationTileColors,
    onSpanChange: () -> Unit,
    cellSize: Dp,
) {
    var dragging by remember { mutableStateOf(false) }
    var dragged by remember { mutableFloatStateOf(0f) }
    val strokeWidth by animateDpAsState(
        targetValue = if (dragging) {
            CouiDeformationTileDefaults.strokeWidthPressed
        } else {
            CouiDeformationTileDefaults.strokeWidth
        },
        animationSpec = tween(
            durationMillis = 120,
            easing = CouiDeformationTileDefaults.moveEase,
        ),
    )
    Canvas(Modifier.fillMaxSize()) {
        val stroke = strokeWidth.toPx()
        drawRoundRect(
            color = colors.stroke,
            topLeft = Offset(stroke / 2f, stroke / 2f),
            size = Size(size.width - stroke, size.height - stroke),
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = stroke),
        )
    }
    val threshold = cellSize * CouiDeformationTileDefaults.spanToggleFraction
    Box(
        Modifier
            .align(Alignment.CenterEnd)
            .width(CouiDeformationTileDefaults.handleWidth)
            .height(CouiDeformationTileDefaults.handleHeight)
            .clip(RoundedCornerShape(CouiDeformationTileDefaults.handleRadius))
            .background(colors.handle)
            .pointerInput(threshold) {
                detectHorizontalDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = { dragging = false; dragged = 0f },
                    onDragCancel = { dragging = false; dragged = 0f },
                ) { change, dragAmount ->
                    change.consume()
                    dragged += dragAmount
                    if (abs(dragged) > threshold.toPx()) {
                        dragged = 0f
                        onSpanChange()
                    }
                }
            },
    )
}

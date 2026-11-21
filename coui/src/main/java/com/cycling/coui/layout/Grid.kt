package com.cycling.coui.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 网格的默认列数与间距。 */
object CouiGridDefaults {
    val columns = 2
    val horizontalSpacing = 12.dp
    val verticalSpacing = 12.dp
}

/**
 * 等宽网格：按 [columns] 列排布子项，列宽由可用宽度均分，行高取该行最高子项。
 *
 * 只做排布，不裁剪、不铺底、不建立滚动；静态的入口卡、缩略图、设置分区用它，长列表仍用
 * `LazyVerticalGrid`。需要按内容宽度换行时用 [FlowLayout]。
 *
 * 父级必须给出确定宽度；在可滚动容器里无界时退化为单列，避免除零。
 */
@Composable
fun Grid(
    modifier: Modifier = Modifier,
    columns: Int = CouiGridDefaults.columns,
    horizontalSpacing: Dp = CouiGridDefaults.horizontalSpacing,
    verticalSpacing: Dp = CouiGridDefaults.verticalSpacing,
    content: @Composable () -> Unit,
) {
    require(columns > 0) { "columns 必须为正数" }
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val bounded = constraints.maxWidth != Constraints.Infinity
        val actualColumns = if (bounded) columns else 1
        val horizontalGap = if (bounded) horizontalSpacing.roundToPx() else 0
        val verticalGap = verticalSpacing.roundToPx()
        val width = if (bounded) constraints.maxWidth else 0
        val cellWidth = if (bounded) {
            ((width - horizontalGap * (columns - 1)) / columns).coerceAtLeast(0)
        } else {
            0
        }
        val placeables = measurables.map { measurable ->
            if (bounded) measurable.measure(Constraints.fixedWidth(cellWidth))
            else measurable.measure(constraints)
        }
        val rows = placeables.chunked(actualColumns)
        val rowHeights = rows.map { row -> row.maxOfOrNull { it.height } ?: 0 }
        val contentHeight = rowHeights.sum() + verticalGap * (rows.size - 1).coerceAtLeast(0)
        val contentWidth = if (bounded) width else placeables.maxOfOrNull { it.width } ?: 0
        layout(contentWidth, contentHeight.coerceAtMost(constraints.maxHeight)) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { columnIndex, placeable ->
                    placeable.place(cellWidth * columnIndex + horizontalGap * columnIndex, y)
                }
                y += rowHeights[rowIndex] + verticalGap
            }
        }
    }
}

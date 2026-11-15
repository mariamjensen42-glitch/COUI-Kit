package com.cycling.coui.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.LocalCouiContentColor

/**
 * Demo 内部图标，替代 Material `Icon`。
 * `tint` 未指定时保留图标原色，用于多色和渐变图标。
 * `size` 供需要填满固定槽位的场景使用，默认沿用 24dp。
 */
@Composable
internal fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalCouiContentColor.current,
    size: Dp = 24.dp,
) {
    Image(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = if (tint.isSpecified) ColorFilter.tint(tint) else null,
    )
}

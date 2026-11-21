package com.cycling.coui.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.LocalCouiContentColor

/** 图标的默认边长。 */
object CouiIconDefaults {
    val size = 24.dp
}

/**
 * 显示一个矢量图标，固定 [size] 边长并按 [tint] 着色。
 *
 * [tint] 默认取当前内容色：放在按钮、顶栏、Chip 等提供 `LocalCouiContentColor` 的插槽里会自动跟随；
 * 在普通布局中未提供内容色时保留图标原色，多色与功能色图标因此可以直接使用。
 * [contentDescription] 为 `null` 表示装饰图标，不建立额外的无障碍节点。
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalCouiContentColor.current,
    size: Dp = CouiIconDefaults.size,
) {
    Icon(rememberVectorPainter(imageVector), contentDescription, modifier, tint, size)
}

/** [Icon] 的 [Painter] 入口，用于位图或自定义绘制。 */
@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalCouiContentColor.current,
    size: Dp = CouiIconDefaults.size,
) {
    // Image 的默认图片语义与 COUI 图标不同：装饰图标不建立节点，有描述的图标声明 Image 角色。
    val semantics = if (contentDescription == null) Modifier else Modifier.semantics {
        this.contentDescription = contentDescription
        role = Role.Image
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(size).then(semantics),
        colorFilter = if (tint.isSpecified) ColorFilter.tint(tint) else null,
    )
}

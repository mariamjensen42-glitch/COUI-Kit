package com.itos.coui.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * 顶栏圆底玻璃按钮。
 *
 * 库的浮动材质（`couiFloatingSurface` / `TopAppBarButton`）要求 API 34，
 * 而 ColorOS 17 顶栏这类按钮在更低版本同样呈现半透明磨砂 + 边缘高光，
 * 因此这里用绘制还原玻璃观感：竖向渐变的半透明底、顶部更亮的描边、底端内阴影。
 * 真实背景模糊仍由库的采样材质承担，此实现不做离屏采样。
 */
@Composable
internal fun GlassIconButton(
    modifier: Modifier = Modifier,
    dark: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    Box(modifier.size(44.dp).clip(CircleShape).glassCircle(dark)
        .semantics { this.contentDescription = contentDescription }
        .clickable(source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) { content() }
}

/** 玻璃质感：底色与描边都是竖向渐变，光源在顶部。 */
internal fun Modifier.glassCircle(dark: Boolean): Modifier = drawBehind {
    val radius = size.minDimension / 2f
    // 深色下降低透明度让圆面更实, 只保留背景色的轻微透出。
    val topAlpha = if (dark) .20f else .55f
    val midAlpha = if (dark) .15f else .40f
    val bottomAlpha = if (dark) .11f else .30f
    drawCircle(brush = Brush.verticalGradient(
        0f to Color.White.copy(alpha = topAlpha),
        .55f to Color.White.copy(alpha = midAlpha),
        1f to Color.White.copy(alpha = bottomAlpha),
    ))
    // 底端内阴影, 让圆面有厚度而不是一片平色。
    drawCircle(brush = Brush.verticalGradient(
        .68f to Color.Transparent,
        1f to Color.Black.copy(alpha = if (dark) .26f else .07f),
    ))
    // 边缘高光: 顶部最亮, 到底部收细, 形成玻璃切面。
    val stroke = 1.dp.toPx()
    drawCircle(brush = Brush.verticalGradient(
        0f to Color.White.copy(alpha = if (dark) .46f else .75f),
        .42f to Color.White.copy(alpha = if (dark) .18f else .35f),
        1f to Color.White.copy(alpha = if (dark) .07f else .18f),
    ), radius = radius - stroke / 2f, style = Stroke(width = stroke, cap = StrokeCap.Round))
}

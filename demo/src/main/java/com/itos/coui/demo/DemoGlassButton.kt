package com.itos.coui.demo

import android.os.Build
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.topappbar.CouiTopAppBarButtonDefaults
import com.itos.coui.topappbar.TopAppBarButton

/**
 * 顶栏圆底玻璃按钮。
 *
 * API 34 及以上直接装配库的 `TopAppBarButton`：采样真实背景、跟随光与形变都走 `couiFloatingSurface`，
 * 观感与详情页顶栏一致。传入 [backdrop]（只含页面背景的采样源）时按钮才可能拿到模糊，因此自定义顶栏
 * 需把背景层单独挂成源、内容层放在源之外，避免按钮采样到自身。
 *
 * 低版本（库的浮动材质要求 API 34）保留绘制兜底：竖向渐变的半透明底、顶部更亮的描边、底端内阴影，
 * 不做离屏采样。
 */
@Composable
internal fun GlassIconButton(
    modifier: Modifier = Modifier,
    dark: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    backdrop: CouiBackdrop? = null,
    content: @Composable () -> Unit,
) {
    // 采样源是否可用、系统版本都在页面生命周期内恒定, 分支不会在组合中反复切换。
    val source = remember { MutableInteractionSource() }
    if (Build.VERSION.SDK_INT >= 33 && backdrop != null) {
        val density = LocalDensity.current.density
        val contour = LocalCouiContourGlowEnabled.current
        TopAppBarButton(
            modifier = modifier.semantics { this.contentDescription = contentDescription },
            onClick = onClick,
            backdrop = backdrop,
            style = CouiTopAppBarButtonDefaults.style(dark).copy(
                material = { capsule -> CouiTopAppBarButtonDefaults.systemMaterial(dark, capsule) },
                shadow = { capsule ->
                    CouiTopAppBarButtonDefaults.shadow(dark, capsule, density, contour, alphaScale = 1f)
                },
            ).withDemoThemeTint(),
        ) { content() }
    } else {
        Box(modifier.size(44.dp).clip(CircleShape).glassCircle(dark)
            .semantics { this.contentDescription = contentDescription }
            .clickable(source, indication = null, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center) { content() }
    }
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

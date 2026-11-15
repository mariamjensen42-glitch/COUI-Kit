package com.cycling.coui.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.cycling.coui.theme.CouiTheme
import kotlin.math.abs

/**
 * 内容占位块，用于在数据到达前保持布局。形状与高度由调用方给出，未指定时使用 8dp 圆角与 16dp 最小高度。
 * 底色取 color 自身的透明度，动画只在其上按 [CouiProgressDefaults.skeletonMinFactor] 到 1 波动；
 * animated 为 false 时保持静止，不做逐帧绘制。原厂没有骨架屏组件，形状与脉冲按库内卡片圆角
 * 与加载视图的透明度区间推演。
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: Shape = CouiTheme.shapes.small,
    color: Color = CouiTheme.colorScheme.onSurface.copy(alpha = CouiProgressDefaults.skeletonAlpha),
    animated: Boolean = true,
) {
    val phase = rememberCouiPhase(animated, CouiProgressDefaults.skeletonPeriodMillis)
    Box(modifier.defaultMinSize(minHeight = CouiProgressDefaults.skeletonMinHeight).clip(shape).drawBehind {
        val wave = if (animated) 1f - abs(phase.value * 2f - 1f) else 1f
        val factor = CouiProgressDefaults.skeletonMinFactor +
            (1f - CouiProgressDefaults.skeletonMinFactor) * wave
        drawRect(color.copy(alpha = color.alpha * factor))
    })
}

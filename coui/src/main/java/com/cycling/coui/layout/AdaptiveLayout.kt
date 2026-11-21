package com.cycling.coui.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 可用宽度档位，断点与 Material 3 的窗口宽度档位一致。
 *
 * 原厂参考设备（一加 Ace 5，1264×2780 @560dpi ≈ 361dp）属于 [Compact]，
 * 平板、展开态折叠屏与桌面窗口落在 [Medium] 及以上。库内页面纵向滚动，
 * 不按高度分档，因此只有宽度一维。
 */
@Immutable
enum class CouiWindowSizeClass {
    /** 小于 600dp：手机竖屏、分屏窄窗。 */
    Compact,

    /** 600dp 至 839dp：折叠屏展开态、手机横屏、平板竖屏。 */
    Medium,

    /** 840dp 及以上：平板横屏、桌面窗口。 */
    Expanded;

    companion object {
        /** 中档起点 600dp。 */
        val mediumMinWidth = 600.dp

        /** 大档起点 840dp。 */
        val expandedMinWidth = 840.dp

        /** 按可用宽度取档位；零与负值归入 [Compact]。 */
        fun of(width: Dp): CouiWindowSizeClass = when {
            width >= expandedMinWidth -> Expanded
            width >= mediumMinWidth -> Medium
            else -> Compact
        }
    }
}

/**
 * [AdaptiveLayout] 提供的档位。未包裹时按 [CouiWindowSizeClass.Compact] 处理，
 * 即库的原生参考设备尺寸。
 */
val LocalCouiWindowSizeClass = staticCompositionLocalOf { CouiWindowSizeClass.Compact }

/**
 * 自适应判定入口：测量自身可用宽度，向下提供 [LocalCouiWindowSizeClass]。
 *
 * 按约束而不是窗口取宽，因此在侧栏、分栏等局部容器里拿到的也是该容器的真实可用宽度；
 * 约束无界（例如放在横向滚动容器内）时回退到窗口宽度。容器按内容尺寸布局，
 * 不额外撑满父级，最终尺寸仍由 [modifier] 决定。
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (CouiWindowSizeClass) -> Unit,
) {
    BoxWithConstraints(modifier) {
        // 有界时用自身的可用宽度（BoxWithConstraintsScope.maxWidth 已是 Dp），
        // 无界时回退窗口宽度，窗口宽度是像素，需要按当前 density 换算。
        val windowWidthPx = LocalWindowInfo.current.containerSize.width
        val width = if (constraints.hasBoundedWidth) {
            maxWidth
        } else {
            with(LocalDensity.current) { windowWidthPx.toDp() }
        }
        val sizeClass = CouiWindowSizeClass.of(width)
        CompositionLocalProvider(LocalCouiWindowSizeClass provides sizeClass) {
            content(sizeClass)
        }
    }
}

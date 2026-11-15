package com.cycling.coui.quicksetting

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme
import kotlin.math.PI

/** 磁贴占用的列数：编辑态可在 1×1 与 2×1 之间切换，对应原厂 `SpanSize` 的 1×1 / 2×1。 */
enum class CouiDeformationSpan(val columns: Int) { OneByOne(1), TwoByOne(2) }

/** 形变磁贴的边框与拖动把手配色。 */
@Immutable
data class CouiDeformationTileColors(
    val container: Color,
    val stroke: Color,
    val handle: Color,
)

/** 形变磁贴网格的固定参数；尺寸与描边取自 SystemUI 的 `qs_tile_*` 与 `qs_edit_tile_stroke_width_*`。 */
object CouiDeformationTileDefaults {
    val columns = 4
    val gap = 8.dp
    val cornerRadius = 20.dp
    val strokeWidth = 2.dp
    val strokeWidthPressed = 4.dp
    val handleWidth = 12.dp
    val handleHeight = 28.dp
    val handleRadius = 6.dp

    /** 拖动把手横向移动超过一格的该比例即切换列数。 */
    const val spanToggleFraction = .35f

    /** 形变与重排的弹簧 response；原厂 `BoundsTransition` 走同一档弹簧。 */
    const val reshapeResponse = .35f

    /** 原厂编辑态描边与把手的缓动 `PathInterpolator(0.3, 0, 0.1, 1)`。 */
    val moveEase = CubicBezierEasing(.3f, 0f, .1f, 1f)

    fun stiffness(response: Float = reshapeResponse): Float = ((2f * PI.toFloat()) / response).let { it * it }

    val shape: Shape
        get() = RoundedCornerShape(cornerRadius)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiDeformationTileColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiDeformationTileColors(
            container = scheme.card,
            stroke = scheme.accent,
            handle = scheme.accent,
        )
    }
}

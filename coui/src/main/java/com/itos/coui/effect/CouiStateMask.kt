package com.itos.coui.effect

import android.animation.ArgbEvaluator
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/** 原厂 COUIMaskEffectDrawable 的 mMaskType：HoverPress 只绘悬停与按压，Full 增加焦点与选中。 */
enum class CouiMaskType {
    /** 对应 mMaskType == 0，焦点与选中不产生蒙层。 */
    HoverPress,

    /** 对应 mMaskType == 1，悬停、焦点（含选中）与按压三层按顺序叠加。 */
    Full,
}

/** 原厂 COUIStrokeDrawable 的描边类型：Inner 裁掉形状内部，Outer 裁掉形状外部，只留内半笔宽。 */
enum class CouiStrokeType { Inner, Outer }

/** 一层状态蒙层的三个目标色；进度与合成顺序由 [couiStateMask] 持有。 */
data class CouiMaskColors(
    val hover: Color,
    val focus: Color,
    val press: Color,
)

/** 状态蒙层与焦点描边的固定参数，取值对齐 COUIMaskEffectDrawable 与 COUIStrokeDrawable。 */
object CouiMaskDefaults {
    /** 原厂 mMinProgressForTouchEnterAnimation 默认 0.7，对应短按退场的最低进度。 */
    const val MinimumPressProgress: Float = 0.7f

    /** 原厂 default_focus_stroke_radius 的两倍：外半笔宽 3dp，向内可见 3dp。 */
    val FocusStrokeWidth: Dp = 6.dp

    /** 浅色与深色目标色，取自 COUI 的 couiColorHover / couiColorFocus / couiColorPress。 */
    fun colors(dark: Boolean): CouiMaskColors = if (dark) {
        CouiMaskColors(hover = Color(0x26FFFFFF), focus = Color(0x331A8CFF), press = Color(0x33FFFFFF))
    } else {
        CouiMaskColors(hover = Color(0x14000000), focus = Color(0x1F0080FF), press = Color(0x1F000000))
    }
}

/**
 * 普通表面的状态蒙层：悬停、焦点（含选中）与按压，几何跟随传入的 [shape]。
 *
 * 三条通道各自持有 [CouiStateProgress]，语义与 COUIMaskEffectDrawable 一致：按压走
 * 0..10000 的弹簧进度并在短按时先到满再退场；[CouiMaskType] 决定焦点通道是否存在；
 * [selected] 复用焦点通道。宿主将同一 [interactionSource] 交给唯一的点击 owner，
 * 外部裁剪与无障碍语义仍由宿主提供。
 *
 * @param dark 深色配色，默认取当前主题。
 * @param enabled 关闭时立即清空三层进度，不保留按压尾巴。
 * @param type 蒙层类型，HoverPress 用于过滤焦点的材质表面。
 * @param shape 蒙层轮廓，默认矩形。
 * @param selected 选中态，等效原厂 mEnableSelectedState 把选中并入焦点通道。
 * @param hoverLocked 悬停锁定，用于弹出层展开期间保持行高亮。
 * @param press 是否由本 Modifier 跟踪按压；列表最低进度由 [pressProgress] 接管时传 false。
 * @param pressProgress 按压通道的进度读取器，0..1，用于列表等外部驱动。
 * @param colors 三层目标色。
 */
@Composable
fun Modifier.couiStateMask(
    interactionSource: MutableInteractionSource,
    dark: Boolean = CouiTheme.isDark,
    enabled: Boolean = true,
    type: CouiMaskType = CouiMaskType.Full,
    shape: Shape = RectangleShape,
    selected: Boolean = false,
    hoverLocked: Boolean = false,
    press: Boolean = true,
    pressProgress: (() -> Float)? = null,
    colors: CouiMaskColors = CouiMaskDefaults.colors(dark),
): Modifier {
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val hover = rememberMaskChannel(enabled && (hovered || hoverLocked))
    val focus = rememberMaskChannel(enabled && type == CouiMaskType.Full && (focused || selected))
    val pressEffect = rememberMaskChannel(enabled && press && pressed)
    if (!enabled) {
        // 原厂 onStateChange 发现 disabled 时 reset 三条通道，直接归零而不回弹。
        SideEffect {
            hover.reset()
            focus.reset()
            pressEffect.reset()
        }
    }
    return hoverable(interactionSource, enabled).drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        drawMaskShape(outline, maskColor(hover.value, colors.hover))
        if (type == CouiMaskType.Full) drawMaskShape(outline, maskColor(focus.value, colors.focus))
        drawMaskShape(outline, maskColor(pressProgress?.invoke() ?: pressEffect.value, colors.press))
    }
}

/**
 * 焦点描边：沿 [shape] 画 [strokeWidth] 宽的描边，[CouiStrokeType] 决定保留形状内侧还是外侧。
 *
 * 对应 COUIStrokeDrawable；Outer 用差集裁掉形状外部，因此可见宽度为笔宽的一半。
 */
@Composable
fun Modifier.couiFocusOutline(
    interactionSource: MutableInteractionSource,
    shape: Shape,
    color: Color,
    enabled: Boolean = true,
    strokeWidth: Dp = CouiMaskDefaults.FocusStrokeWidth,
    type: CouiStrokeType = CouiStrokeType.Outer,
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    val focus = rememberMaskProgress(enabled && focused)
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }
        // 原厂用 getDimensionPixelOffset(default_focus_stroke_radius) 先取整成像素再乘二。
        val stroke = Stroke(width = (strokeWidth / 2).toPx().toInt() * 2f)
        onDrawBehind {
            if (!enabled) return@onDrawBehind
            val tint = maskColor(focus.value, color)
            if (tint.alpha == 0f) return@onDrawBehind
            if (type == CouiStrokeType.Inner) {
                clipPath(path) { drawPath(path, tint, style = stroke) }
            } else {
                clipPath(path, ClipOp.Difference) { drawPath(path, tint, style = stroke) }
            }
        }
    }
}

/** 单条蒙层通道的 0..1 进度；active 变化时更新目标，销毁时停止弹簧。 */
@Composable
internal fun rememberMaskProgress(active: Boolean): State<Float> = rememberMaskChannel(active).progress

/** 持有通道本体，供禁用时直接 reset；进度仍经 [CouiStateProgress.progress] 读取。 */
@Composable
private fun rememberMaskChannel(active: Boolean): CouiStateProgress {
    val effect = remember { CouiStateProgress() }
    SideEffect { effect.setActive(active) }
    DisposableEffect(effect) { onDispose { effect.dispose() } }
    return effect
}

/** 0..1 的进度换算为目标色；原厂 StateEffectAnimator 用 ArgbEvaluator 从透明插值到目标色。 */
private fun maskColor(progress: Float, target: Color): Color =
    Color(MaskEvaluator.evaluate(progress.coerceIn(0f, 1f), 0, target.toArgb()) as Int)

private val MaskEvaluator = ArgbEvaluator()

/** 蒙层轮廓走原厂分支：矩形直接填，圆角与自定义路径转 Path 绘制。 */
private fun DrawScope.drawMaskShape(outline: Outline, color: Color) {
    if (color.alpha == 0f) return
    when (outline) {
        is Outline.Rectangle -> drawRect(color, topLeft = outline.rect.topLeft, size = outline.rect.size)
        else -> drawPath(Path().apply { addOutline(outline) }, color)
    }
}

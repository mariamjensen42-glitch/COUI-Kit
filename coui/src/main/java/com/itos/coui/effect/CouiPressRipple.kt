package com.itos.coui.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme
import kotlin.math.sqrt

/**
 * 水波纹分支的常量。颜色取自 ColorOS 17 `coui_color_ripple_press_background`（浅色 #99B4B4B4、
 * 深色 #80FFFFFF）与 `coui_color_press_background`（#14000000 / #26FFFFFF）；
 * `COUIPressRippleDrawable` 在 API 34 起读 `couiColorPressBackground`（#14000000 / #26FFFFFF）、
 * 旧版读 `couiColorRipplePressBackground`；波纹颜色取后者，与原厂默认分支一致。
 * 扩散与淡入淡出按平台 `RippleDrawable` 的节奏换算：20ms 进入延迟、75ms 淡入、150ms 淡出、
 * 200–400ms 随距离线性扩散。原厂直接使用平台实现，毫秒值由系统常量决定，这里只对齐量级与顺序。
 */
object CouiPressRippleDefaults {
    /** `text_ripple_bg_padding_horizontal`。 */
    val horizontalPadding = 12.dp

    /** `text_ripple_bg_padding_vertical`。 */
    val verticalPadding = 4.dp

    const val enterDelayMillis = 20
    const val fadeInMillis = 75
    const val fadeOutMillis = 150
    const val minExpandMillis = 200
    const val maxExpandMillis = 400

    /** 扩散距离每多 1dp 增加的时长，把最远角距离映射到 200–400ms 之间。 */
    private const val expandMillisPerDp = 1.4f

    fun rippleColor(dark: Boolean): Color =
        if (dark) Color(0x80FFFFFF) else Color(0x99B4B4B4)

    internal fun expansionMillis(maxRadiusPx: Float, density: Float): Int =
        (minExpandMillis + maxRadiusPx / density * expandMillisPerDp).toInt()
            .coerceIn(minExpandMillis, maxExpandMillis)
}

/** 触点（已在绘制区的本地坐标）到绘制区四角的最远距离，即波纹需要覆盖的最大半径。 */
private fun maxRadius(center: Offset, width: Float, height: Float): Float = sqrt(
    maxOf(center.x, width - center.x).let { it * it } +
        maxOf(center.y, height - center.y).let { it * it },
)

/**
 * `COUIPressRippleDrawable` 的水波纹分支：按下时从触点扩散一枚圆形波纹并淡入，抬手或取消时淡出。
 * 绘制范围由 [shape] 裁剪，与原厂把 `COUIPressMaskDrawable` 当作遮罩一致；[padded] 对应原厂
 * `initPadding` 的 12dp / 4dp 外扩，绘制会超出节点边界，需要父容器允许越界绘制。
 *
 * 蒙层分支由 [couiStateMask]（普通表面）与 [couiButtonFeedback]（按钮与列表）承担；
 * 两者与波纹同时使用会叠出两份按压反馈，只能选其一。
 * 宿主把同一个 `interactionSource` 交给唯一的 clickable / toggleable owner。
 */
@Composable
fun Modifier.couiPressRipple(
    interactionSource: MutableInteractionSource,
    dark: Boolean = CouiTheme.isDark,
    enabled: Boolean = true,
    shape: Shape = CouiTheme.shapes.capsule,
    padded: Boolean = false,
    color: Color = CouiPressRippleDefaults.rippleColor(dark),
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val density = LocalDensity.current
    val horizontal = with(density) { if (padded) CouiPressRippleDefaults.horizontalPadding.toPx() else 0f }
    val vertical = with(density) { if (padded) CouiPressRippleDefaults.verticalPadding.toPx() else 0f }
    var origin by remember { mutableStateOf(Offset.Zero) }
    var touched by remember { mutableStateOf(false) }
    var bounds by remember { mutableStateOf(IntSize.Zero) }
    val expansion = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(pressed, enabled, bounds, horizontal, vertical) {
        if (pressed && enabled && bounds != IntSize.Zero) {
            // 无障碍点击与键盘激活没有触点，从中心扩散。
            val center = if (touched) origin
            else Offset(bounds.width / 2f, bounds.height / 2f)
            val radius = maxRadius(Offset(center.x + horizontal, center.y + vertical),
                bounds.width + horizontal * 2f, bounds.height + vertical * 2f)
            expansion.snapTo(0f)
            alpha.animateTo(1f, tween(CouiPressRippleDefaults.fadeInMillis,
                delayMillis = CouiPressRippleDefaults.enterDelayMillis))
            expansion.animateTo(1f,
                tween(CouiPressRippleDefaults.expansionMillis(radius, density.density), easing = LinearEasing))
        } else {
            touched = false
            alpha.animateTo(0f, tween(CouiPressRippleDefaults.fadeOutMillis))
            expansion.snapTo(0f)
        }
    }
    val observer = if (enabled) Modifier.pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            origin = down.position
            touched = true
            waitForUpOrCancellation()
        }
    } else Modifier
    return this.onSizeChanged { bounds = it }.then(observer).drawBehind {
        if (alpha.value <= 0f) return@drawBehind
        val width = size.width + horizontal * 2f
        val height = size.height + vertical * 2f
        // 外扩区从 (-horizontal, -vertical) 开始；把绘制整体平移回来再用形状裁剪。
        val outline = shape.createOutline(Size(width, height), layoutDirection, this)
        translate(-horizontal, -vertical) {
            val center = Offset(origin.x + horizontal, origin.y + vertical)
            val radius = maxRadius(center, width, height) * expansion.value
            val rippleAlpha = color.alpha * alpha.value
            when (outline) {
                is Outline.Rectangle -> clipRect(0f, 0f, width, height) {
                    drawCircle(color.copy(alpha = rippleAlpha), radius, center)
                }
                is Outline.Rounded -> clipPath(Path().apply { addRoundRect(outline.roundRect) }) {
                    drawCircle(color.copy(alpha = rippleAlpha), radius, center)
                }
                is Outline.Generic -> clipPath(outline.path) {
                    drawCircle(color.copy(alpha = rippleAlpha), radius, center)
                }
            }
        }
    }
}

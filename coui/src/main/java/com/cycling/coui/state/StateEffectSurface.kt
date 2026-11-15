package com.cycling.coui.state

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.cycling.coui.effect.CouiMaskColors
import com.cycling.coui.effect.CouiMaskType
import com.cycling.coui.effect.couiFocusOutline
import com.cycling.coui.effect.couiStateMask
import com.cycling.coui.motion.couiPressMotion
import com.cycling.coui.motion.rememberCouiPressMotion
import com.cycling.coui.theme.CouiTheme

/**
 * 状态效果表面：把原厂 `COUIStateEffectDrawable` 的图层组合与状态跟踪收进一个容器。
 *
 * 图层顺序对齐原厂的 LayerDrawable：先铺 [containerColor] 底色，再叠悬停、焦点与按压蒙层，
 * 焦点态可选叠一圈描边，最后是 [content]。按压时整块按原厂 `enableScaleEffect` 的路径缩放，
 * 禁用时蒙层与描边一起归零而不是保留尾巴。
 *
 * @param onClick 提供时整块可点击并暴露 [Role.Button]；为 null 时只做绘制，语义由宿主提供。
 * @param selected 选中态，等效原厂 `COUIStateEffectDrawable.setTouchSelected`，并入焦点通道。
 * @param maskType 蒙层类型，`HoverPress` 只绘悬停与按压。
 * @param focusOutline 是否在焦点态画描边，对应原厂叠加 `COUIStrokeDrawable` 的用法。
 * @param scaleFeedback 是否在按压时缩放，对应 `enableScaleEffect`。
 */
@Composable
fun StateEffectSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    containerColor: Color = Color.Transparent,
    shape: Shape = CouiTheme.shapes.card,
    maskType: CouiMaskType = CouiMaskType.Full,
    focusOutline: Boolean = false,
    scaleFeedback: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiStateEffectColors = CouiStateEffectDefaults.colors(dark),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val press = rememberCouiPressMotion()
    val scale = if (scaleFeedback) press.scale.value else 1f
    val surface = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(if (scaleFeedback) Modifier.couiPressMotion(press, enabled) else Modifier)
        .clip(shape)
        .background(containerColor)
        .couiStateMask(
            interactionSource = source,
            dark = dark,
            enabled = enabled,
            type = maskType,
            shape = shape,
            selected = selected,
            colors = CouiMaskColors(hover = colors.hover, focus = colors.focus, press = colors.press),
        )
        .then(
            if (focusOutline) {
                Modifier.couiFocusOutline(
                    interactionSource = source,
                    shape = shape,
                    color = colors.outline,
                    enabled = enabled,
                )
            } else {
                Modifier
            },
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
            } else {
                Modifier
            },
        )
    Box(surface, content = content)
}

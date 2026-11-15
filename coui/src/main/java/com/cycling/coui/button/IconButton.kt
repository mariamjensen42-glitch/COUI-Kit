package com.cycling.coui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.effect.SpotlightPreset
import com.cycling.coui.effect.couiButtonFeedback
import com.cycling.coui.effect.rememberCouiSpotlight
import com.cycling.coui.motion.couiPressMotion
import com.cycling.coui.motion.couiTouchMotion
import com.cycling.coui.motion.rememberCouiPressMotion
import com.cycling.coui.motion.rememberCouiTouchMotion
import com.cycling.coui.shape.CouiShapes
import com.cycling.coui.theme.Coui
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.theme.ProvideCouiContent

/**
 * 图标按钮的默认值。
 *
 * 原厂没有独立的图标按钮控件类，界面中的图标按钮一律是 `android.widget.ImageButton`
 * 加自定义背景：最近任务头部的「更多」测量为 147×147px（@560dpi 约 42dp），
 * 控制中心磁贴的 `icon_frame` 为 217×217px 而内部图标 98×98px。因此尺寸按
 * 布局树里的命中区取值，与顶栏按钮共用 40dp 一档。
 */
object CouiIconButtonDefaults {
    /** 常规命中区，与顶栏按钮一致。 */
    val size = 40.dp

    /** 紧凑命中区，用于列表行尾部与标签内。 */
    val smallSize = 32.dp

    /** 强调命中区，用于页面主操作。 */
    val largeSize = 48.dp

    /** 原生基线；组件默认读取 `CouiTheme.shapes.circle`。 */
    val shape: Shape = CouiShapes.Default.circle
}

/**
 * 只有图标的按钮。图标由调用方提供，库不做 tint；按下形变与 hover、focus、press 蒙层复用
 * [Button] 的同一条反馈链，所以与文字按钮的观感一致。
 *
 * 可点区域由主题的最小交互尺寸兜底，[size] 小于 48dp 时仍能命中。默认底色透明，
 * 需要容器色时传入 [containerColor]。按钮只承载单次操作，不表达选中状态。
 */
@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    size: Dp = CouiIconButtonDefaults.size,
    shape: Shape = CouiTheme.shapes.circle,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val motion = rememberCouiTouchMotion(enabled)
    val press = rememberCouiPressMotion()
    // 透明按钮与次要按钮共用同一档轮廓光, 不额外提高强度。
    val (lightInput, drawLight) = rememberCouiSpotlight(
        interactive = enabled,
        preset = SpotlightPreset.OpaqueMedium2,
        dark = dark,
        cancelOnConsumedMove = true,
    )
    val resolvedContentColor = contentColor.takeOrElse { CouiTheme.colorSchemeFor(dark).onSurface }
    // 禁用态与顶栏按钮、浮动操作栏、菜单同档: 内容色乘 0.38, 否则禁用与可用长得一样。
    val contentAlpha = if (enabled) 1f else CouiDisabledContentAlpha
    Box(
        modifier = modifier
            .then(Coui.defaultStyle.minimumInteractiveComponentSize())
            .size(size)
            .graphicsLayer {
                scaleX = motion.scaleX.value + press.scale.value - 1f
                scaleY = motion.scaleY.value + press.scale.value - 1f
                translationX = motion.translationX.value
                translationY = motion.translationY.value
            }
            .couiTouchMotion(motion, enabled, cancelOnConsumedMove = true)
            .couiPressMotion(press, enabled, cancelOnConsumedMove = true)
            .then(lightInput)
            .clip(shape)
            .drawWithContent {
                drawContent()
                drawLight?.invoke(this)
            }
            .background(containerColor, shape)
            .couiButtonFeedback(dark, source, enabled)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ProvideCouiContent(
            color = resolvedContentColor.copy(alpha = resolvedContentColor.alpha * contentAlpha),
        ) {
            content()
        }
    }
}

/** 禁用态内容透明度, 与顶栏按钮、浮动操作栏、菜单保持一致。 */
private const val CouiDisabledContentAlpha = 0.38f

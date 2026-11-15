package com.itos.coui.button

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.itos.coui.effect.SpotlightPreset
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.effect.rememberCouiSpotlight
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.couiTouchMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.motion.rememberCouiTouchMotion
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.text.couiAdaptFontSize
import com.itos.coui.text.couiTextStyle
import com.itos.coui.theme.CouiTheme
import com.itos.coui.theme.ProvideCouiContent
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 带文字标签的按钮，按 Settings 17 的 COUIButton 操作按钮分支实现：
 * 44dp 高度、12dp / 4dp 内留白、等宽高画圆、其他尺寸平滑胶囊，
 * 触摸形变、按压缩放、跟手光与 hover/focus/press 蒙层复用原厂同一条链。
 * 点击与禁用由组件持有，业务状态仍归宿主；项目没有加载态的 COUIButton 分支，
 * loading 期间不接受点击，标签淡出、原位换成三个 1dp 圆点（间距 2dp），按钮宽度不跳动。
 * description 对应原厂 `DescButtonTextSpan`：主文案在上、描述在下，各自单行居中省略。
 */
@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    style: CouiButtonStyle = CouiButtonStyle.Secondary,
    enabled: Boolean = true,
    loading: Boolean = false,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiButtonColors = CouiTheme.colorSchemeFor(dark).let {
        CouiButtonDefaults.colors(style, it.accent, dark, it.onAccent)
    },
    shape: Shape = CouiButtonDefaults.shape,
    interactionSource: MutableInteractionSource? = null,
    loadingDescription: String? = null,
    description: String? = null,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        style = style,
        enabled = enabled,
        loading = loading,
        dark = dark,
        colors = colors,
        shape = shape,
        interactionSource = interactionSource,
        loadingDescription = loadingDescription,
    ) {
        if (description == null) {
            Text(text = label, kind = CouiTextKind.ButtonLabel, textAlign = TextAlign.Center,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        } else {
            CouiButtonDescLabel(text = label, description = description)
        }
    }
}

/**
 * 原厂 `DescButtonTextSpan` 的两行文案：主文案 `coui_btn_desc_text_size`（16sp）在上，
 * 描述 `coui_btn_desc_sub_text_size`（10sp）在下，间距 `coui_btn_desc_top_margin`（0dp），
 * 两行各自单行省略后居中，宽度取两者较大者。
 * 字号按 `COUIChangeTextUtil.getSuitableFontSize(size, fontScale, 2)` 的 1.15 倍封顶收敛；
 * 原厂用 `canvas.drawText` 组合两组 `FontMetricsInt` 基线，移植交给 Compose 文本排版。
 */
@Composable
internal fun CouiButtonDescLabel(text: String, description: String, color: Color = CouiTheme.contentColor,
    modifier: Modifier = Modifier) {
    val main = rememberCouiDescTextStyle(CouiTextKind.ButtonDescLabel)
    val sub = rememberCouiDescTextStyle(CouiTextKind.ButtonDescSubLabel)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CouiButtonDefaults.descTextMargin)) {
        BasicText(text, style = main.copy(color = color, textAlign = TextAlign.Center),
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        BasicText(description, style = sub.copy(color = color, textAlign = TextAlign.Center),
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun rememberCouiDescTextStyle(kind: CouiTextKind): TextStyle {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val typeface = remember(kind) {
        Typeface.create(Typeface.create(kind.family, Typeface.NORMAL), kind.weight, false)
    }
    return remember(kind, configuration, density, typeface) {
        val pixels = couiAdaptFontSize(with(density) { kind.size.sp.toPx() }.roundToInt().toFloat(),
            configuration, CouiButtonDefaults.descMaxFontScale)
        couiTextStyle(typeface, kind.weight, pixels, 1f, includeFontPadding = false, density = density)
    }
}

/**
 * 内容槽版本，用于图标加文字或自定义内容。槽内文字与图标继承按钮的内容色，
 * 显式颜色和 tint 仍优先。外观、状态与标签版本一致。
 */
@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    style: CouiButtonStyle = CouiButtonStyle.Secondary,
    enabled: Boolean = true,
    loading: Boolean = false,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiButtonColors = CouiTheme.colorSchemeFor(dark).let {
        CouiButtonDefaults.colors(style, it.accent, dark, it.onAccent)
    },
    shape: Shape = CouiButtonDefaults.shape,
    interactionSource: MutableInteractionSource? = null,
    loadingDescription: String? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interactive = enabled && !loading
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val motion = rememberCouiTouchMotion(interactive)
    val press = rememberCouiPressMotion()
    val (lightInput, drawLight) = rememberCouiSpotlight(
        interactive = interactive,
        preset = if (style == CouiButtonStyle.Primary) SpotlightPreset.OpaqueMedium1 else SpotlightPreset.OpaqueMedium2,
        dark = dark,
        cancelOnConsumedMove = true,
    )
    val container = if (enabled) colors.container else colors.disabledContainer
    val contentColor = if (enabled) colors.content else colors.disabledContent
    Box(
        modifier.heightIn(min = CouiButtonDefaults.minHeight)
            .widthIn(min = CouiButtonDefaults.minWidth).graphicsLayer {
            scaleX = motion.scaleX.value + press.scale.value - 1f
            scaleY = motion.scaleY.value + press.scale.value - 1f
            translationX = motion.translationX.value
            translationY = motion.translationY.value
        }.couiTouchMotion(motion, interactive, cancelOnConsumedMove = true)
            .couiPressMotion(press, interactive, cancelOnConsumedMove = true)
            .then(lightInput)
            .clip(shape)
            // 跟手光叠加在已完成绘制的按钮表面上，与弹窗操作按钮保持同一层序。
            .drawWithContent {
                drawContent()
                drawLight?.invoke(this)
            }
            .background(container, shape)
            // 文字按钮没有容器，按压不叠蒙层，与无表面按钮的观感一致。
            .couiButtonFeedback(dark, source, interactive, pressFeedback = style != CouiButtonStyle.Text)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = interactive,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = CouiButtonDefaults.horizontalPadding,
                vertical = CouiButtonDefaults.verticalPadding)
            .semantics {
                if (loading && loadingDescription != null) {
                    stateDescription = loadingDescription
                    liveRegion = LiveRegionMode.Polite
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.graphicsLayer { alpha = if (loading) 0f else 1f },
            horizontalArrangement = Arrangement.spacedBy(CouiButtonDefaults.iconSpacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProvideCouiContent(color = contentColor) { content() }
        }
        if (loading) {
            CouiButtonLoadingDots(contentColor)
        }
    }
}

/**
 * 加载态的三圆点，取自 `Widget.COUI.Button` 的 `coui_loading_btn_circle_radius`（1dp）与
 * `coui_loading_btn_circle_spacing`（2dp）。原厂没有为它单独提供动画资源，
 * 这里按同一套加载字形的 800ms 周期做依次脉冲：三个点的透明度错开三分之一周期。
 */
@Composable
private fun CouiButtonLoadingDots(color: Color) {
    val period = CouiButtonDefaults.loadingPeriodMillis
    var phase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var start = 0L
        while (true) {
            withFrameNanos { now ->
                if (start == 0L) start = now
                phase = ((now - start) % period).toFloat() / period
            }
        }
    }
    val diameter = CouiButtonDefaults.loadingDotRadius * 2
    val gap = CouiButtonDefaults.loadingDotSpacing
    Canvas(Modifier.size(diameter * 3 + gap * 2, diameter)) {
        val radius = diameter.toPx() / 2f
        val step = radius * 2f + gap.toPx()
        for (index in 0 until 3) {
            val local = (phase + index / 3f) % 1f
            // 三角波：0 → 1 → 0，三点依次亮起。
            val wave = 1f - abs(local * 2f - 1f)
            val alpha = .35f + .65f * wave
            drawCircle(color.copy(alpha = color.alpha * alpha), radius, Offset(radius + index * step, size.height / 2f))
        }
    }
}

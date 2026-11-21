package com.cycling.coui.blocks.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.cycling.coui.theme.CouiTheme

/**
 * 安装 / 下载进度按钮：底色 + 进度填充 + 居中文字，按下时整体内缩、变暗、文字缩小。
 *
 * [state] 为 [CouiLoadProgressState.Idle] 时只画底色与文字，[CouiLoadProgressState.InProgress] 与
 * [CouiLoadProgressState.Waiting] 画进度填充，[CouiLoadProgressState.Failed] 用强调色铺满整颗按钮。
 * 进度覆盖到的文字以 [CouiInstallLoadProgressColors.surpassContent] 二次绘制，与原厂
 * `COUIInstallLoadProgress#onDrawText` 的裁剪方式一致。
 */
@Composable
fun InstallLoadProgress(
    progress: Int,
    state: CouiLoadProgressState,
    text: String,
    modifier: Modifier = Modifier,
    max: Int = 100,
    style: CouiInstallLoadProgressStyle = CouiInstallLoadProgressStyle.Small,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiInstallLoadProgressColors = CouiInstallLoadProgressDefaults.colors(dark),
    metrics: CouiInstallLoadProgressMetrics = CouiInstallLoadProgressDefaults.metrics(style),
    onClick: (() -> Unit)? = null,
) {
    require(max > 0) { "max 必须为正数" }
    val value = progress.coerceIn(0, max)
    val target = value / max.toFloat()
    val visual by animateFloatAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = 1f,
            stiffness = CouiInstallLoadProgressDefaults.stiffness(),
        ),
    )

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (pressed) {
                CouiInstallLoadProgressDefaults.pressDurationMillis
            } else {
                CouiInstallLoadProgressDefaults.releaseDurationMillis
            },
            easing = CouiInstallLoadProgressDefaults.moveEase,
        ),
    )

    val language = LocalConfiguration.current.locales[0].language
    val width = if (style == CouiInstallLoadProgressStyle.Small && language != "zh") {
        metrics.width + metrics.wideLocaleExtraWidth
    } else {
        metrics.width
    }

    val measurer = rememberTextMeasurer()
    val textStyle = metrics.textSize
    val showsProgress = state == CouiLoadProgressState.InProgress || state == CouiLoadProgressState.Waiting

    val interaction = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Canvas(
        modifier
            .size(width = width, height = metrics.height)
            .then(interaction)
            .semantics {
                contentDescription = text
                if (onClick != null) role = Role.Button
                if (showsProgress) progressBarRangeInfo = ProgressBarRangeInfo(value.toFloat(), 0f..max.toFloat())
                if (!enabled) disabled()
            },
    ) {
        val fraction = press
        val insetX = size.width * CouiInstallLoadProgressDefaults.pressedInsetFraction * fraction
        val insetY = size.height * CouiInstallLoadProgressDefaults.pressedInsetFraction * fraction
        val left = insetX
        val top = insetY
        val right = size.width - insetX
        val bottom = size.height - insetY
        val radius = ((bottom - top) / 2f - metrics.radiusOffset.toPx()).coerceAtLeast(0f)
        val highlight = 1f - (1f - CouiInstallLoadProgressDefaults.pressedBrightness) * fraction

        val primary = if (enabled) dim(colors.primary, highlight) else colors.disabled
        val secondary = if (enabled) dim(colors.secondary, highlight) else colors.disabled
        val content = when {
            !enabled -> colors.disabled
            state == CouiLoadProgressState.Failed -> colors.surpassContent
            else -> dim(colors.content, highlight)
        }
        val surpass = if (enabled) colors.surpassContent else colors.disabled

        fun background(color: Color) = drawRoundRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            cornerRadius = CornerRadius(radius),
        )

        background(if (state == CouiLoadProgressState.Failed) primary else secondary)

        val fillRight = if (showsProgress) {
            left + (right - left) * visual
        } else {
            left
        }
        if (showsProgress && fillRight > left) {
            clipRect(left = left, top = top, right = fillRight, bottom = bottom) {
                background(primary)
            }
        }

        val layout = measurer.measure(
            text = text,
            style = textStyle,
            maxLines = 1,
        )
        val textX = metrics.textPadding.toPx() +
            ((right - left) - layout.size.width - metrics.textPadding.toPx() * 2f) / 2f
        val textY = (bottom - top - layout.size.height) / 2f
        drawText(layout, color = content, topLeft = Offset(left + textX, top + textY))
        if (showsProgress && fillRight > left) {
            clipRect(left = left, top = top, right = fillRight, bottom = bottom) {
                drawText(layout, color = surpass, topLeft = Offset(left + textX, top + textY))
            }
        }
    }
}

/** 按 HSL 亮度缩放颜色，对应原厂 `getCurrentColor(int)` 的亮度衰减。 */
private fun dim(color: Color, factor: Float): Color {
    if (factor >= 1f) return color
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val lightness = (max + min) / 2f
    if (max == min) {
        val scaled = (lightness * factor).coerceIn(0f, 1f)
        return Color(scaled, scaled, scaled, color.alpha)
    }
    val delta = max - min
    val saturation = if (lightness > .5f) delta / (2f - max - min) else delta / (max + min)
    val hue = when (max) {
        r -> ((g - b) / delta + if (g < b) 6f else 0f) / 6f
        g -> ((b - r) / delta + 2f) / 6f
        else -> ((r - g) / delta + 4f) / 6f
    }
    val scaledLightness = (lightness * factor).coerceIn(0f, 1f)
    val q = if (scaledLightness < .5f) {
        scaledLightness * (1f + saturation)
    } else {
        scaledLightness + saturation - scaledLightness * saturation
    }
    val p = 2f * scaledLightness - q
    return Color(
        red = hueToRgb(p, q, hue + 1f / 3f),
        green = hueToRgb(p, q, hue),
        blue = hueToRgb(p, q, hue - 1f / 3f),
        alpha = color.alpha,
    )
}

private fun hueToRgb(p: Float, q: Float, t: Float): Float {
    var value = t
    if (value < 0f) value += 1f
    if (value > 1f) value -= 1f
    return when {
        value < 1f / 6f -> p + (q - p) * 6f * value
        value < .5f -> q
        value < 2f / 3f -> p + (q - p) * (2f / 3f - value) * 6f
        else -> p
    }
}

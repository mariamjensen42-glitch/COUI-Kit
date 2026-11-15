package com.itos.coui.textfield

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.util.lerp
import com.itos.coui.theme.CouiTheme

/**
 * 验证码输入：一排圆角格子，整段验证码由一个不可见的输入框承接键盘与粘贴。
 *
 * 受控组件：[code] 是完整验证码，超过 [length] 的部分被截断；粘贴多字符时按顺序填满格子，
 * 输入到满 [length] 位触发 [onComplete]。当前输入位置描边取强调色，格子里的数字由
 * 0.6 缩放到 1 淡入（100ms），删除时数字在原位淡出（100ms、延迟 33ms）。
 *
 * [secure] 为 true 时已填格只画圆点，不显示数字，也不做数字动画。
 * 透明输入框覆盖整行，点击任意格子都会聚焦并定位光标；组件不主动抢焦点，也不自弹键盘。
 */
@Composable
fun CodeInputView(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = CouiCodeInputDefaults.CellCount,
    secure: Boolean = false,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiCodeInputColors = CouiCodeInputDefaults.colors(dark),
    metrics: CouiCodeInputMetrics = CouiCodeInputDefaults.metrics,
    textStyle: TextStyle = CouiCodeInputDefaults.textStyle(),
    onComplete: ((String) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    require(length > 0) { "length 必须大于零" }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scale = CouiCodeInputDefaults.cellScale(configuration.screenWidthDp)
    val cellWidth = metrics.cellWidth * scale
    val cellHeight = metrics.cellHeight * scale
    val value = code.take(length)
    val activeIndex = if (value.length >= length) length - 1 else value.length
    val currentOnCodeChange by rememberUpdatedState(onCodeChange)
    val currentOnComplete by rememberUpdatedState(onComplete)
    BoxWithConstraints(modifier.heightIn(min = cellHeight)) {
        val cellWidthPx = with(density) { cellWidth.toPx() }
        val availablePx = with(density) { maxWidth.toPx() }
        val spacing = CouiCodeInputDefaults.cellSpacing(availablePx, cellWidthPx, length, metrics, density)
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)) {
            repeat(length) { index ->
                CodeInputCell(number = value.getOrNull(index)?.toString().orEmpty(),
                    secure = secure, active = enabled && index == activeIndex, enabled = enabled,
                    colors = colors, metrics = metrics, textStyle = textStyle,
                    modifier = Modifier.size(cellWidth, cellHeight))
            }
        }
        BasicTextField(
            value = value,
            onValueChange = { raw ->
                val next = raw.take(length)
                currentOnCodeChange(next)
                if (next.length == length) currentOnComplete?.invoke(next)
            },
            modifier = Modifier.matchParentSize().graphicsLayer { alpha = 0f },
            enabled = enabled,
            singleLine = true,
            textStyle = textStyle.copy(color = Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions.Default,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Color.Transparent),
        )
    }
}

/**
 * 单个格子：底色、当前输入位置的描边与数字，都是独立绘制节点。
 * 数字消失期间仍画上一个字符，保证淡出时有内容可见。
 */
@Composable
private fun CodeInputCell(
    number: String,
    secure: Boolean,
    active: Boolean,
    enabled: Boolean,
    colors: CouiCodeInputColors,
    metrics: CouiCodeInputMetrics,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    var shown by remember { mutableStateOf(number) }
    var fromScale by remember { mutableFloatStateOf(CouiCodeInputDefaults.numberScaleStart) }
    val appear = remember { Animatable(if (number.isEmpty()) 0f else 1f) }
    val stroke = remember { Animatable(if (active) 1f else 0f) }
    LaunchedEffect(number) {
        when {
            number.isEmpty() && shown.isNotEmpty() -> {
                fromScale = 1f
                appear.animateTo(0f, tween(CouiCodeInputDefaults.numberDurationMillis,
                    delayMillis = CouiCodeInputDefaults.numberDeleteDelayMillis,
                    easing = CouiCodeInputDefaults.moveEase))
                shown = ""
            }
            number.isNotEmpty() && number != shown -> {
                shown = number
                fromScale = CouiCodeInputDefaults.numberScaleStart
                appear.snapTo(0f)
                appear.animateTo(1f, tween(CouiCodeInputDefaults.numberDurationMillis,
                    easing = CouiCodeInputDefaults.moveEase))
            }
        }
    }
    LaunchedEffect(active) {
        if (active) {
            stroke.animateTo(1f, tween(CouiCodeInputDefaults.strokeDurationMillis,
                delayMillis = CouiCodeInputDefaults.strokeAppearDelayMillis,
                easing = CouiCodeInputDefaults.moveEase))
        } else {
            stroke.animateTo(0f, tween(CouiCodeInputDefaults.strokeDurationMillis,
                easing = CouiCodeInputDefaults.moveEase))
        }
    }
    Canvas(modifier) {
        val alpha = if (enabled) 1f else CouiCodeInputDefaults.disabledAlpha
        val radius = metrics.cornerRadius.toPx()
        drawRoundRect(color = colors.cellBackground, cornerRadius = CornerRadius(radius), alpha = alpha)
        if (stroke.value > 0f) {
            val width = metrics.strokeWidth.toPx()
            val inset = width / 2f
            drawRoundRect(color = colors.cellStroke, topLeft = Offset(inset, inset),
                size = Size(size.width - width, size.height - width),
                cornerRadius = CornerRadius((radius - inset).coerceAtLeast(0f)),
                alpha = stroke.value * alpha, style = Stroke(width))
        }
        val char = shown
        if (char.isEmpty()) return@Canvas
        if (secure) {
            drawCircle(color = colors.dot, radius = metrics.dotRadius.toPx(), center = center, alpha = alpha)
            return@Canvas
        }
        if (appear.value <= 0f) return@Canvas
        val layout = measurer.measure(AnnotatedString(char), style = textStyle)
        val topLeft = Offset((size.width - layout.size.width) / 2f, (size.height - layout.size.height) / 2f)
        val currentScale = lerp(fromScale, 1f, appear.value)
        scale(currentScale, currentScale, pivot = center) {
            drawText(layout, color = colors.number, topLeft = topLeft, alpha = appear.value * alpha)
        }
    }
}

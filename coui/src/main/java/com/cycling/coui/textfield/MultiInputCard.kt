package com.cycling.coui.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.delay

/**
 * 多行输入卡片：顶部提示、最多 5 行正文与右下角字数。
 *
 * 受控组件，文本由宿主传入 [value]；给了 [maxLength] 时超出部分被截断，字数以 `n/max` 显示，
 * 到达上限后的 1 秒内字数切到错误色（250ms 过渡）再回落，与 `COUICardMultiInputView` 一致。
 * 空态提示最多 3 行并以省略号收尾，卡片最小高度 152dp，正文到 5 行后内部滚动。
 * 点击卡片任意位置聚焦输入区，`enabled = false` 时既不聚焦也不接受输入。
 */
@Composable
fun MultiInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    maxLength: Int? = null,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiMultiInputColors = CouiMultiInputDefaults.colors(dark),
    shape: Shape = CouiMultiInputDefaults.shape,
    contentPadding: PaddingValues = CouiMultiInputDefaults.contentPadding,
    textStyle: TextStyle = CouiMultiInputDefaults.textStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    require(maxLength == null || maxLength > 0) { "maxLength 必须大于零" }
    val text = if (maxLength != null) value.take(maxLength) else value
    val limitReached = maxLength != null && text.length >= maxLength
    var emphasized by remember { mutableStateOf(false) }
    // 每次触发上限都重新计时：超限后保持错误色 1 秒再回落。
    LaunchedEffect(text.length) {
        if (limitReached) {
            emphasized = true
            delay(CouiMultiInputDefaults.counterResetDelayMillis)
            emphasized = false
        } else {
            emphasized = false
        }
    }
    val counterColor by animateColorAsState(
        targetValue = if (emphasized) colors.error else colors.counter,
        animationSpec = tween(CouiMultiInputDefaults.counterColorDurationMillis),
        label = "couiMultiInputCounterColor",
    )
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val focusRequester = remember { FocusRequester() }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Column(
        modifier = modifier.defaultMinSize(minHeight = CouiMultiInputDefaults.minHeight)
            .clip(shape)
            .background(colors.container)
            .clickable(interactionSource = source, indication = null, enabled = enabled) {
                focusRequester.requestFocus()
            }
            .padding(contentPadding),
    ) {
        BasicTextField(
            value = text,
            onValueChange = { currentOnValueChange(if (maxLength != null) it.take(maxLength) else it) },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            enabled = enabled,
            textStyle = textStyle.copy(color = if (enabled) colors.text else colors.disabled),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = CouiMultiInputDefaults.maxLines,
            interactionSource = source,
            cursorBrush = SolidColor(colors.cursor),
            decorationBox = { inner ->
                Box {
                    if (text.isEmpty() && hint != null) {
                        BasicText(hint, maxLines = CouiMultiInputDefaults.hintMaxLines,
                            overflow = TextOverflow.Ellipsis,
                            style = textStyle.copy(color = if (enabled) colors.hint else colors.disabled))
                    }
                    inner()
                }
            },
        )
        if (maxLength != null) {
            BasicText("${text.length}/$maxLength",
                modifier = Modifier.align(Alignment.End).padding(top = CouiMultiInputDefaults.counterSpacing),
                style = CouiMultiInputDefaults.counterTextStyle().copy(color = counterColor))
        }
    }
}

package com.itos.coui.textfield

import android.graphics.Typeface
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import com.itos.coui.text.couiTextStyle
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

/**
 * COUI 输入框，对应原厂 `Widget.COUI.EditText.HintAnim.Rectangle` 分支：10dp 圆角描边容器，
 * 最小高度 62dp，内留白水平 16dp / 10dp、垂直 12dp。
 *
 * value 与 onValueChange 由宿主完全持有，组件不保存草稿、不做格式校验；输入法组合区、
 * 光标和选区由 [BasicTextField] 管理。[placeholder] 可省略，省略或为空时框内不显示提示文字；传入非空文字时
 * 它是框内浮动标签：内容为空且未聚焦时与输入文字同位，聚焦或有内容后上浮到框内左上并缩到 10sp。
 * [title] 是框外标题，与浮动标签互不影响；[error] 只改变描边与提示文字的颜色，不拦截输入。
 */
@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    title: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiTextFieldColors = CouiTextFieldDefaults.colors(dark),
) {
    val textStyle = rememberTextFieldTextStyle(CouiTextFieldDefaults.textSize, CouiTextFieldDefaults.textWeight,
        CouiTextFieldDefaults.textLineSpacing)
    val titleStyle = rememberTextFieldTextStyle(CouiTextFieldDefaults.titleSize, CouiTextFieldDefaults.titleWeight,
        CouiTextFieldDefaults.titleLineSpacing)
    val errorStyle = rememberTextFieldTextStyle(CouiTextFieldDefaults.errorTextSize, CouiTextFieldDefaults.errorWeight,
        CouiTextFieldDefaults.errorLineSpacing)
    var focused by remember { mutableStateOf(false) }
    val errorText = error?.takeIf { it.isNotEmpty() }
    val hasError = errorText != null
    val floated = focused || value.isNotEmpty()
    val labelProgress by animateFloatAsState(
        targetValue = if (floated) 1f else 0f,
        animationSpec = tween(CouiTextFieldDefaults.labelAnimationDuration,
            easing = CouiTextFieldDefaults.labelAnimationEasing),
        label = "textFieldLabel",
    )
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val shape = CouiTextFieldDefaults.shape
    val borderColor = when {
        !enabled -> colors.disabledLine
        hasError -> colors.error
        focused -> colors.focusedLine
        else -> colors.line
    }
    val borderWidth = if (enabled && (focused || hasError)) CouiTextFieldDefaults.focusedLineWidth
    else CouiTextFieldDefaults.lineWidth
    Column(modifier.fillMaxWidth()) {
        if (title != null) BasicText(title,
            Modifier.fillMaxWidth().padding(bottom = CouiTextFieldDefaults.titleSpacing),
            style = titleStyle.copy(color = if (enabled) colors.title else colors.disabledText),
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Box(
            Modifier.fillMaxWidth()
                .heightIn(min = CouiTextFieldDefaults.minHeight)
                // 描边在裁剪之前绘制，避免被 clip 切掉外侧一半。
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .padding(
                    start = CouiTextFieldDefaults.startPadding,
                    end = CouiTextFieldDefaults.endPadding,
                    top = CouiTextFieldDefaults.topPadding,
                    bottom = CouiTextFieldDefaults.bottomPadding,
                ),
            // 内容在框内垂直居中：62dp 的最小高度减去上下留白后仍有余量，靠上排会让底部发空。
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
                enabled = enabled,
                singleLine = singleLine,
                textStyle = textStyle.copy(color = if (enabled) colors.text else colors.disabledText),
                cursorBrush = SolidColor(if (enabled) colors.cursor else colors.disabledText),
            )
            if (placeholder.isNotEmpty()) BasicText(
                text = placeholder,
                modifier = Modifier.graphicsLayer {
                    val scale = lerp(1f, CouiTextFieldDefaults.floatingLabelScale, labelProgress)
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0f)
                    // 原厂矩形模式以 rectModePaddingTop 为中轴：浮标上浮到上半区，文字留在下半区。
                    translationY = -CouiTextFieldDefaults.labelFloatOffset.toPx() * labelProgress
                },
                style = textStyle.copy(
                    color = if (enabled) lerp(colors.placeholder, colors.floatingLabel, labelProgress)
                    else colors.disabledText,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (errorText != null) BasicText(errorText,
            Modifier.fillMaxWidth().padding(top = CouiTextFieldDefaults.errorSpacing),
            style = errorStyle.copy(color = colors.error), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** 输入框各状态的颜色；描边、文字、标签与错误色均由宿主显式给出。 */
@Immutable
data class CouiTextFieldColors(
    /** 已输入文字。 */
    val text: Color,
    /** 框外标题。 */
    val title: Color,
    /** 空内容时的浮动标签。 */
    val placeholder: Color,
    /** 标签上浮后的颜色。 */
    val floatingLabel: Color,
    /** 常态描边。 */
    val line: Color,
    /** 聚焦描边。 */
    val focusedLine: Color,
    /** 错误描边与错误文字。 */
    val error: Color,
    val disabledText: Color,
    val disabledLine: Color,
    val cursor: Color,
)

/** 输入框尺寸、字号与动画预设；数值取自 `Widget.COUI.EditText.HintAnim.Rectangle` 与 `coui_*` 尺寸。 */
object CouiTextFieldDefaults {
    /** 文字取 `couiColorPrimaryNeutral`，标签取 `couiColorLabelSecondary` / `couiColorSecondNeutral`，
     * 描边取 `couiColorDivider`，聚焦与光标取主题强调色，错误取 `couiColorError`。 */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTextFieldColors {
        val theme = CouiTheme.colorSchemeFor(dark)
        return CouiTextFieldColors(
            text = theme.onSurface,
            title = theme.onSurface,
            placeholder = theme.onSurfaceVariant,
            floatingLabel = theme.onSurfaceVariant,
            line = theme.outlineVariant,
            focusedLine = theme.accent,
            error = if (dark) Color(0xFFFF6C61) else Color(0xFFDB382C),
            disabledText = if (dark) Color(0x4DFFFFFF) else Color(0x42000000),
            disabledLine = if (dark) Color(0x4DFFFFFF) else Color(0x42000000),
            cursor = theme.accent,
        )
    }

    /** `couiTextAppearanceHeadline6` 的 16sp / sans-serif-regular。 */
    val textSize = 16.sp
    internal const val textWeight = 400
    internal const val textLineSpacing = 1.05f

    /** `coui_textinput_hint_text_size`。 */
    val floatingLabelSize = 10.sp

    /** `coui_textinput_corner_radius`。 */
    val cornerRadius = 10.dp
    val shape = RoundedCornerShape(cornerRadius)

    /** `coui_edit_text_height_min`。 */
    val minHeight = 62.dp

    /** `Widget.COUI.EditText.HintAnim.Rectangle` 的 `paddingLeft` / `paddingRight`。 */
    val startPadding = 16.dp
    val endPadding = 10.dp

    /** `paddingBottom`。 */
    val bottomPadding = 12.dp

    /** 同一分支的 `rectModePaddingTop`，是浮标与文字之间的中轴位置。 */
    val rectModePaddingTop = 12.dp

    /** 浮标上浮后的高度，等于 10sp 的行高。 */
    val floatedLabelHeight = (floatingLabelSize.value * textLineSpacing).dp

    /** 输入文字的行高，等于 16sp × 1.05。 */
    val textLineHeight = (textSize.value * textLineSpacing).dp

    /**
     * 原厂 `COUIEditText.getModePaddingTop()`：矩形模式下 `paddingTop = rectModePaddingTop + 文字行高 / 2`。
     * 文字从 [rectModePaddingTop] 下方半个行高处开始，浮标上浮到它上方半个浮标高处，两者以中轴分开。
     */
    val topPadding = rectModePaddingTop + textLineHeight / 2

    /** 浮标从文字顶部上浮到中轴上方所需的位移。 */
    val labelFloatOffset = topPadding - rectModePaddingTop + floatedLabelHeight / 2

    /** `coui_textinput_stroke_width` / `coui_textinput_focus_stroke_width`。 */
    val lineWidth = 0.33.dp
    val focusedLineWidth = 1.dp

    /** `COUIInputTitleStyle` 继承的 `couiTextAppearanceHeadline6`。 */
    val titleSize = 16.sp
    internal const val titleWeight = 500
    internal const val titleLineSpacing = 1.05f

    /** 框外标题与输入框之间的距离，取 `coui_input_preference_title_padding_bottom`。 */
    val titleSpacing = 4.dp

    /** 错误提示沿用 `couiTextAppearanceCaption` 的 12sp，距输入框 4dp。 */
    val errorTextSize = 12.sp
    internal const val errorWeight = 400
    internal const val errorLineSpacing = 1f
    val errorSpacing = 4.dp

    /** 标签上浮后的缩放，等于 10sp / 16sp。 */
    internal val floatingLabelScale = floatingLabelSize.value / textSize.value

    /** 提示上浮：`COUIMoveEaseInterpolator` = PathInterpolator(0.3, 0, 0.1, 1)，时长 200ms。 */
    const val labelAnimationDuration = 200
    val labelAnimationEasing = CubicBezierEasing(.3f, 0f, .1f, 1f)
}

/** 按原厂字体度量生成文字样式；字号按 sp 换算像素，行距按倍数折算。 */
@Composable
private fun rememberTextFieldTextStyle(size: TextUnit, weight: Int, lineSpacing: Float): TextStyle {
    val density = LocalDensity.current
    return remember(size, weight, lineSpacing, density) {
        val typeface = Typeface.create(Typeface.create("sans-serif", Typeface.NORMAL), weight, false)
        val pixels = with(density) { size.toPx().roundToInt().toFloat() }
        couiTextStyle(typeface, weight, pixels, lineSpacing, includeFontPadding = true, density = density)
    }
}

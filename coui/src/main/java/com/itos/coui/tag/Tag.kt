package com.itos.coui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/** Tag 的容器与内容配色。 */
@Immutable
data class CouiTagColors(val container: Color, val content: Color, val stroke: Color)

/**
 * 标签的固定参数，取自 ColorOS 17 的 `COUITagBackgroundView` 与 `COUITagView`：
 * 容器左右各 3dp 内留白，左图标 5×10dp、上下各 3dp 外边距、右侧 2dp 间距，
 * 文本 10dp（`coui_default_tag_textsize`）`sans-serif-medium`、单行省略。
 *
 * 轮廓默认直角（`couiTagCornerRadius` 默认 0），四角可分别覆盖；描边默认宽 0（`couiTagStrokeWidth`）。
 * 容器色属性 `?attr/couiColorBackgroundWithTag` 在 `Theme.COUI` 中没有赋值，文本默认色则是
 * `coui_color_white`；这里保留原厂 attr 默认的「中性薄填充 + 白字」组合，实际外观由宿主显式传入。
 */
object CouiTagDefaults {
    /** `couiTagCornerRadius` 默认 0，四角半径。 */
    val cornerRadius = 0.dp
    val shape: Shape = RoundedCornerShape(cornerRadius)

    /** 布局根节点的左右内留白。 */
    val horizontalPadding = 3.dp

    /** 左图标尺寸与位置：`coui_tag_view_layout.xml` 的 5×10dp 与上下 3dp。 */
    val leftIconWidth = 5.dp
    val leftIconHeight = 10.dp
    val leftIconVerticalPadding = 3.dp

    /** 左图标与文本之间的间距。 */
    val leftIconSpacing = 2.dp

    /** 原厂字号以 dp 声明，不随系统字号缩放。 */
    val textSize = 10.dp

    /** `couiTagStrokeWidth` 默认 0。 */
    val strokeWidth = 0.dp

    /** `couiColorContainer8` 的日夜实值，用作中立薄填充；实心标签由宿主传入强调色等容器。 */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTagColors =
        CouiTagColors(if (dark) Color(0x1AFFFFFF) else Color(0x14000000), Color.White, Color.Transparent)
}

/**
 * 标签：可选左图标、单行文本与可选右图标，容器按 [shape] 裁剪子节点。
 *
 * 只表达状态，不处理点击；需要点击时由宿主在标签外层提供交互。
 * 四角独立的轮廓可直接传 `RoundedCornerShape(topStart = …, topEnd = …, bottomStart = …, bottomEnd = …)`，
 * 对应原厂 `couiTagTLCornerRadius` 等四个属性。
 */
@Composable
fun Tag(
    modifier: Modifier = Modifier,
    text: String? = null,
    leftIcon: (@Composable () -> Unit)? = null,
    rightIcon: (@Composable () -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiTagColors = CouiTagDefaults.colors(dark),
    shape: Shape = CouiTagDefaults.shape,
    strokeWidth: Dp = CouiTagDefaults.strokeWidth,
    textSize: TextUnit? = null,
    contentPadding: Dp = CouiTagDefaults.horizontalPadding,
) {
    val density = LocalDensity.current
    val size = textSize ?: with(density) { CouiTagDefaults.textSize.toSp() }
    val style = TextStyle(
        color = colors.content,
        fontSize = size,
        lineHeight = size,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )
    val background = modifier.clip(shape).background(colors.container, shape)
    val bordered = if (strokeWidth > 0.dp) background.border(strokeWidth, colors.stroke, shape) else background
    Row(bordered.padding(horizontal = contentPadding), verticalAlignment = Alignment.CenterVertically) {
        if (leftIcon != null) {
            Box(
                Modifier.padding(top = CouiTagDefaults.leftIconVerticalPadding,
                    bottom = CouiTagDefaults.leftIconVerticalPadding,
                    end = CouiTagDefaults.leftIconSpacing)
                    .size(CouiTagDefaults.leftIconWidth, CouiTagDefaults.leftIconHeight)) { leftIcon() }
        }
        if (text != null) {
            BasicText(text, style = style, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (rightIcon != null) Box(contentAlignment = Alignment.Center) { rightIcon() }
    }
}

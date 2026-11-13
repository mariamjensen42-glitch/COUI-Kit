package com.itos.coui.demo.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import com.itos.coui.theme.CouiTheme
import com.itos.coui.theme.LocalCouiContentColor
import com.itos.coui.theme.LocalCouiTextStyle

/**
 * Demo 内部文字，替代 Material `Text`。
 * 排版基线取当前 [LocalCouiTextStyle]，内容色取 [LocalCouiContentColor]；两者都由 `CouiTheme` 提供。
 */
@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalCouiTextStyle.current,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    val resolvedColor = color.takeOrElse {
        LocalCouiContentColor.current.takeOrElse { CouiTheme.colorScheme.onSurface }
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(
            color = resolvedColor,
            textAlign = textAlign ?: style.textAlign,
            fontWeight = fontWeight ?: style.fontWeight,
            fontSize = if (fontSize.isSpecified) fontSize else style.fontSize,
        ),
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
    )
}

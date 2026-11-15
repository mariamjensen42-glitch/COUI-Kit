package com.itos.coui.toptips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.MarqueeText
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/**
 * 顶部提示条的默认内容排布，对应原厂 `layout/coui_default_toptips.xml`。
 *
 * 原厂布局是一行约束布局：可选图标、可选标题、跑马灯正文、可选操作按钮与关闭按钮。
 * 这里按同一顺序装配，并把图标与关闭按钮的槽位固定为
 * [CouiTopTipsDefaults.iconSize]，正文使用 [MarqueeText]，
 * 标题最多两行并按尾部省略。
 *
 * @param content 正文文案，跑马灯滚动。
 * @param title 标题；为空时不占位。
 * @param icon 图标槽；为空时不显示。
 * @param action 操作按钮槽；为空时不显示。
 * @param close 关闭按钮槽；为空时不显示。
 */
@Composable
fun TopTipsContent(
    modifier: Modifier = Modifier,
    content: String,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    close: (@Composable () -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CouiTopTipsDefaults.contentStartMargin),
    ) {
        icon?.let { slot ->
            Row(Modifier.size(CouiTopTipsDefaults.iconSize), verticalAlignment = Alignment.CenterVertically) {
                slot()
            }
        }
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                kind = CouiTextKind.ItemTitle,
                color = scheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        MarqueeText(
            text = content,
            kind = CouiTextKind.Body,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        action?.let { slot ->
            Row(verticalAlignment = Alignment.CenterVertically) { slot() }
        }
        close?.let { slot ->
            Row(Modifier.size(CouiTopTipsDefaults.iconSize), verticalAlignment = Alignment.CenterVertically) {
                slot()
            }
        }
    }
}

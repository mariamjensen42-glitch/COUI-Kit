package com.itos.coui.actioncard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text

/** 操作卡的内留白、图标与操作区间距。 */
object CouiActionCardDefaults {
    val shape = CouiPageDefaults.shape
    val padding = 16.dp
    val iconSize = CouiPageDefaults.iconSize
    val iconSpacing = 12.dp
    val titleSummarySpacing = 4.dp
    val actionTopSpacing = 12.dp
    val actionSpacing = 20.dp
}

/**
 * 页面内的一张操作卡：图标、可选标题与说明，右下角是调用方提供的操作组。
 *
 * 省略 [title] 时退化为提示卡形态（单段说明 + 操作），对应 ColorOS 手机管家首页顶部的通知卡；
 * 提供 [title] 时是推荐卡形态（标题 + 副标题 + 操作），对应「应用管理」这类推荐优化卡。
 *
 * 两种形态的说明字号不同：提示卡的说明本身承担主文案，用 15sp 的 [CouiTextKind.BodyLarge]；
 * 推荐卡的说明是标题的补充，用 13sp 的 [CouiTextKind.ItemSummary]。
 * 整卡不承担点击，交互只由 [actions] 里的子控件提供；图标由调用方提供，库不做 tint。
 */
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    summary: String,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    colors: CouiPageColors = CouiPageDefaults.themeColors(),
    actions: @Composable RowScope.() -> Unit = {},
) {
    val notice = title.isNullOrEmpty()
    Column(modifier.fillMaxWidth().clip(CouiActionCardDefaults.shape).background(colors.card)
        .padding(CouiActionCardDefaults.padding)) {
        Row(verticalAlignment = Alignment.Top) {
            if (icon != null) {
                Box(Modifier.padding(end = CouiActionCardDefaults.iconSpacing)
                    .size(CouiActionCardDefaults.iconSize), contentAlignment = Alignment.Center) {
                    icon()
                }
            }
            Column(Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(CouiActionCardDefaults.titleSummarySpacing)) {
                if (!notice) {
                    Text(text = title.orEmpty(), kind = CouiTextKind.ItemTitle, color = colors.title)
                }
                Text(text = summary,
                    kind = if (notice) CouiTextKind.BodyLarge else CouiTextKind.ItemSummary,
                    color = colors.summary)
            }
        }
        Row(Modifier.align(Alignment.End).padding(top = CouiActionCardDefaults.actionTopSpacing),
            horizontalArrangement = Arrangement.spacedBy(CouiActionCardDefaults.actionSpacing),
            verticalAlignment = Alignment.CenterVertically,
            content = actions)
    }
}

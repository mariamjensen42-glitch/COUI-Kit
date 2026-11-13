package com.itos.coui.notificationcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.shape.CouiShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/**
 * 通知卡的轮廓、留白与行距。数值取自 ColorOS 17 通知中心的真机布局树
 * （一加 Ace 5，1264×2780 @560dpi，3.5px/dp）。
 */
object CouiNotificationCardDefaults {
    /** 通知卡的圆角比页面分组卡片略大，原厂单条通知与分组头共用同一轮廓。 */
    val shape = CouiShape(24.dp, 3f)
    val padding = 16.dp
    val iconSize = 24.dp
    /** 图标到文字的间距：内容左起点 58dp = 16 + 24 + 18。 */
    val iconSpacing = 18.dp
    val titleBodySpacing = 2.dp
    val titleTimeSpacing = 12.dp
    /** 单条紧凑通知的最小高度，实测 211px ≈ 60dp。 */
    val minHeight = 60.dp
}

/**
 * 一条通知：图标、标题、相对时间与正文，对应 ColorOS 17 通知中心的单条通知卡。
 *
 * 只负责布局与排版，**不自带材质**：[containerColor] 默认取页面卡片色，宿主需要凝光玻璃时
 * 传入 `Color.Transparent`，再用 `Modifier.couiFloatingSurface` 包裹本组件并传入同一 [shape]，
 * 材质、模糊与轮廓光全部由既有浮动表面链路承担，不在这里复制一份。
 *
 * 图标由调用方提供，库不做 tint；[time] 与 [icon] 都可省略。
 */
@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    time: String? = null,
    icon: (@Composable () -> Unit)? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
    containerColor: Color = colors.card,
    shape: Shape = CouiNotificationCardDefaults.shape,
    bodyMaxLines: Int = 2,
) {
    Row(modifier.fillMaxWidth()
        .defaultMinSize(minHeight = CouiNotificationCardDefaults.minHeight)
        .clip(shape)
        .background(containerColor)
        .padding(CouiNotificationCardDefaults.padding),
        verticalAlignment = Alignment.Top,
    ) {
        if (icon != null) {
            Box(Modifier.padding(end = CouiNotificationCardDefaults.iconSpacing)
                .size(CouiNotificationCardDefaults.iconSize), contentAlignment = Alignment.Center) {
                icon()
            }
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, kind = CouiTextKind.ItemTitle, color = colors.title,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (!time.isNullOrEmpty()) {
                    Spacer(Modifier.width(CouiNotificationCardDefaults.titleTimeSpacing))
                    Text(text = time, kind = CouiTextKind.Body, color = colors.summary, maxLines = 1)
                }
            }
            Spacer(Modifier.height(CouiNotificationCardDefaults.titleBodySpacing))
            Text(text = body, kind = CouiTextKind.ItemSummary, color = colors.summary,
                maxLines = bodyMaxLines, overflow = TextOverflow.Ellipsis)
        }
    }
}

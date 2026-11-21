package com.cycling.coui.surface

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.theme.LocalCouiContentColor

/**
 * 通用表面：铺底色、按 [shape] 裁剪、可选描边，并把 [contentColor] 提供给内容。
 *
 * 用于弹层、浮层、自定义卡片等需要「一块带轮廓的背景」的场景；需要标题、说明与尾部槽位时用
 * [com.cycling.coui.card.Card]，需要分组语义时用 `Group`。表面不处理点击、选中或阴影，
 * 也不读取背景源；投影由 `Modifier.couiShadow` 等效果在外层叠加。
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = CouiTheme.shapes.card,
    color: Color = CouiTheme.colorScheme.card,
    contentColor: Color = CouiTheme.colorScheme.onSurface,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .clip(shape)
            .background(color)
            .then(if (border != null) Modifier.border(border, shape) else Modifier),
    ) {
        CompositionLocalProvider(LocalCouiContentColor provides contentColor) { content() }
    }
}

package com.itos.coui.toptips

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.itos.coui.theme.CouiTheme

/**
 * 顶部提示条容器，对应原厂 `com.coui.appcompat.tips.COUICustomTopTips`。
 *
 * 原厂是一个 `COUICardView` 子类：容器圆角取 `couiRoundCornerMRadius`，
 * 底色取主题属性 `couiColorFillThin`，显示与消失各 250ms，只改 `scaleY`，不改 alpha。
 * 这里保持同一语义：
 * - [visible] 为 true 时按 250ms 展开，为 false 时按 250ms 收起；
 * - 收起后不占位，也不拦截触摸。
 *
 * 内容由宿主装配。原厂默认布局 `coui_default_toptips.xml` 是图标、可选标题、
 * 跑马灯正文、可选操作按钮与关闭按钮，可用 [TopTipsContent] 按同一顺序装配。
 *
 * @param visible 是否展开。
 * @param dark 取日间还是夜间配色，覆盖主题外观时显式传入。
 * @param content 内容插槽。
 */
@Composable
fun TopTips(
    modifier: Modifier = Modifier,
    visible: Boolean,
    shape: Shape = CouiTheme.shapes.medium,
    dark: Boolean = CouiTheme.isDark,
    containerColor: Color = CouiTopTipsDefaults.containerColor(dark),
    content: @Composable BoxScope.() -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(CouiTopTipsDefaults.animationMillis),
        label = "couiTopTipsScale",
    )
    if (!visible && scale <= 0f) return
    Box(
        modifier
            .graphicsLayer {
                // 原厂只做纵向缩放，不做淡入淡出。
                scaleY = scale
            }
            .clip(shape)
            .background(containerColor)
            .defaultMinSize(minHeight = CouiTopTipsDefaults.contentMinHeight)
            .padding(
                start = CouiTopTipsDefaults.iconStartMargin,
                end = CouiTopTipsDefaults.actionEndMargin,
                top = CouiTopTipsDefaults.contentVerticalPadding,
                bottom = CouiTopTipsDefaults.contentVerticalPadding,
            ),
        content = content,
    )
}

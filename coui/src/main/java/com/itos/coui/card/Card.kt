package com.itos.coui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.shape.CouiShapes
import com.itos.coui.theme.CouiTheme

/**
 * 卡片容器的默认值。
 *
 * 原厂布局树里没有卡片控件类，成组的界面内容一律是带圆角的 `FrameLayout`
 * （`references/screenshots` 的 35 份布局树中共 525 个），圆角取自 `coui_round_corner_*` 的
 * 24dp 一档。因此这里只固定轮廓、底色与内留白，不引入 elevation 或描边。
 */
object CouiCardDefaults {
    /** 原生基线；组件默认读取 `CouiTheme.shapes.card`。 */
    val shape: Shape = CouiShapes.Default.card

    /** 卡片四边内留白，与分组的标题行留白对齐。 */
    val padding = 16.dp
}

/**
 * 通用卡片容器：圆角、底色与内留白。
 *
 * 只提供容器，不规定内部布局——表格、图表、说明文字都直接放进 [content]。
 * 传入 [onClick] 时整卡可点，并复用列表行的按压蒙层；不传时卡片纯静态，
 * 交互交给内部的子控件。`Group` 已经提供带标题与说明的成组卡片，
 * 需要单张卡片或自定义内部结构时使用本组件。
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    shape: Shape = CouiTheme.shapes.card,
    containerColor: Color = CouiTheme.colorSchemeFor(dark).card,
    contentPadding: Dp = CouiCardDefaults.padding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val source = if (onClick != null) {
        interactionSource ?: remember { MutableInteractionSource() }
    } else null
    val interaction = if (onClick != null && source != null) {
        Modifier
            // 列表形蒙层带最低按压进度与取消规则, 与 Group 内的行一致; 蒙层不裁剪圆角, 由外层 clip 负责。
            .couiButtonFeedback(dark, source, enabled, list = true)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
    } else {
        Modifier
    }
    Column(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .then(interaction)
            .padding(contentPadding),
        content = content,
    )
}

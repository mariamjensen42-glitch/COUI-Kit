package com.itos.coui.accordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 折叠面板的标题行留白、箭头尺寸与内容缩进。 */
object CouiAccordionDefaults {
    val arrowSize = 14.dp
    val arrowStroke = 1.6.dp
    val arrowStartSpacing = 8.dp
    val contentBottomPadding = 12.dp
}

/**
 * 可展开分组：标题行常驻，点击切换 [expanded]，内容用展开/收起动画挂载。
 *
 * 展开状态由宿主持有（受控），组件只负责切换回调与动画；`Group` 是静态卡片，
 * 这里补的是「同一张卡片里按需展开更多设置项」的场景。
 *
 * 箭头由组件自绘（两段折线随展开旋转），不依赖图标库，也不受宿主图标配色影响。
 * 标题行的按压反馈跟随 [dark]，与列表项一致。
 */
@Composable
fun Accordion(
    modifier: Modifier = Modifier,
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    summary: String? = null,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
    content: @Composable ColumnScope.() -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "accordion-arrow")
    val titleColor = if (enabled) colors.title else colors.disabled
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()
            .couiButtonFeedback(dark, source, enabled, list = true)
            .clickable(interactionSource = source, indication = null, enabled = enabled, role = Role.Button,
                onClick = { onExpandedChange(!expanded) })
            .padding(horizontal = CouiPageDefaults.rowPadding, vertical = CouiPageDefaults.contentPadding),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(CouiPageDefaults.summarySpacing)) {
                Text(text = title, kind = CouiTextKind.ItemTitle, color = titleColor)
                if (!summary.isNullOrEmpty()) {
                    Text(text = summary, kind = CouiTextKind.ItemSummary, color = colors.summary)
                }
            }
            AccordionArrow(Modifier.padding(start = CouiAccordionDefaults.arrowStartSpacing)
                .graphicsLayer { rotationZ = arrowRotation }, color = colors.summary)
        }
        AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column(Modifier.fillMaxWidth().padding(bottom = CouiAccordionDefaults.contentBottomPadding),
                content = content)
        }
    }
}

/** 展开箭头：两段折线，展开时整体旋转 180 度。 */
@Composable
private fun AccordionArrow(modifier: Modifier, color: Color) {
    val stroke = CouiAccordionDefaults.arrowStroke
    Box(modifier.size(CouiAccordionDefaults.arrowSize).drawBehind {
        val w = size.width
        val h = size.height
        val px = stroke.toPx()
        val centerY = h * 0.42f
        drawLine(color, Offset(w * 0.18f, centerY - h * 0.12f), Offset(w * 0.5f, centerY + h * 0.16f),
            strokeWidth = px, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.5f, centerY + h * 0.16f), Offset(w * 0.82f, centerY - h * 0.12f),
            strokeWidth = px, cap = StrokeCap.Round)
    })
}

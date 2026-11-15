package com.cycling.coui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.cycling.coui.button.CouiIconButtonDefaults
import com.cycling.coui.button.IconButton
import com.cycling.coui.card.Card
import com.cycling.coui.card.CouiCardDefaults
import com.cycling.coui.layout.CouiAdaptiveLayout
import com.cycling.coui.sidepane.CouiSidePaneSide
import com.cycling.coui.sidepane.SidePane
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/**
 * 图标按钮的三种命中区与可用状态。
 *
 * 预览不引用 `coui-icons`：那个模块可选依赖，`coui` 本身没有图标资源，
 * 因此这里用最小的自绘字形占位，只验证命中区、轮廓光与蒙层。
 */
@CouiLightDarkPreview
@Composable
private fun IconButtonPreview() {
    CouiPreviewSurface {
        val colors = CouiTheme.colorScheme
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { PlusGlyph(colors.onSurface) }
                IconButton(onClick = {}, size = CouiIconButtonDefaults.smallSize) {
                    DotsGlyph(colors.onSurface)
                }
                IconButton(onClick = {}, size = CouiIconButtonDefaults.largeSize) {
                    CrossGlyph(colors.onSurface)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, containerColor = colors.accent,
                    contentColor = colors.onAccent) { PlusGlyph(colors.onAccent) }
                // 禁用态的压暗由组件自己按 0.38 处理, 这里传常规内容色即可, 不要重复压暗。
                IconButton(onClick = {}, enabled = false) { PlusGlyph(colors.onSurface) }
            }
        }
    }
}

/** 卡片的静态、可点、自定义轮廓与整宽形态。 */
@CouiLightDarkPreview
@Composable
private fun CardPreview() {
    CouiPreviewSurface {
        val colors = CouiTheme.colorScheme
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card {
                Text(text = "Static card", kind = CouiTextKind.ItemTitle, color = colors.onSurface)
                Text(text = "Card fill with the native 24dp corner.",
                    kind = CouiTextKind.ItemSummary, color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Card(onClick = {}) {
                Text(text = "Clickable card", kind = CouiTextKind.ItemTitle, color = colors.onSurface)
            }
            Card(shape = CouiTheme.shapes.medium, containerColor = colors.container) {
                Text(text = "Override shape and fill", kind = CouiTextKind.ItemSummary,
                    color = colors.onSurfaceVariant)
            }
            Card(modifier = Modifier.fillMaxWidth(), contentPadding = CouiCardDefaults.padding) {
                Text(text = "Full-width card", kind = CouiTextKind.ItemSummary,
                    color = colors.onSurfaceVariant)
            }
        }
    }
}

/** 侧栏展开态的左、右两侧贴边。内容槽留空即可，预览只看面板与遮罩。 */
@CouiLightDarkPreview
@Composable
private fun SidePanePreview() {
    CouiPreviewSurface {
        // 竖排而不是并排: 预览默认宽度装不下两块面板, 并排会被裁掉一半。
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PanePreviewBox(CouiSidePaneSide.Start)
            PanePreviewBox(CouiSidePaneSide.End)
        }
    }
}

@Composable
private fun PanePreviewBox(side: CouiSidePaneSide) {
    val colors = CouiTheme.colorScheme
    SidePane(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        visible = true,
        onDismissRequest = {},
        side = side,
        paneTitle = "Side pane",
        pane = {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Side pane", kind = CouiTextKind.ItemTitle, color = colors.onSurface)
                Text(text = "Pane content", kind = CouiTextKind.ItemSummary,
                    color = colors.onSurfaceVariant)
            }
        },
    ) {
        Text(text = "Underlying page", kind = CouiTextKind.ItemSummary,
            color = colors.onSurfaceVariant, modifier = Modifier.padding(16.dp))
    }
}

/** 三种断点下的同一份内容排布。 */
@CouiBreakpointPreview
@Composable
private fun AdaptiveLayoutPreview() {
    CouiPreviewSurface {
        CouiAdaptiveLayout { sizeClass ->
            val colors = CouiTheme.colorScheme
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Size class: ${sizeClass.name}", kind = CouiTextKind.ItemTitle,
                    color = colors.onSurface)
                Card {
                    Text(text = "Content reflows with the window width.",
                        kind = CouiTextKind.ItemSummary, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

/** 加号字形。预览占位用，不进入发布 API。 */
@Composable
private fun PlusGlyph(tint: Color) {
    Canvas(Modifier.size(20.dp)) {
        val w = 1.6.dp.toPx()
        val c = center
        drawLine(tint, Offset(c.x, w), Offset(c.x, size.height - w), w, StrokeCap.Round)
        drawLine(tint, Offset(w, c.y), Offset(size.width - w, c.y), w, StrokeCap.Round)
    }
}

/** 叉号字形。 */
@Composable
private fun CrossGlyph(tint: Color) {
    Canvas(Modifier.size(20.dp)) {
        val w = 1.6.dp.toPx()
        val pad = 3.dp.toPx()
        drawLine(tint, Offset(pad, pad), Offset(size.width - pad, size.height - pad), w, StrokeCap.Round)
        drawLine(tint, Offset(size.width - pad, pad), Offset(pad, size.height - pad), w, StrokeCap.Round)
    }
}

/** 三点字形。 */
@Composable
private fun DotsGlyph(tint: Color) {
    Canvas(Modifier.size(20.dp)) {
        val r = 1.8.dp.toPx()
        val step = size.width / 4f
        drawCircle(tint, r, Offset(step, center.y))
        drawCircle(tint, r, Offset(center.x, center.y))
        drawCircle(tint, r, Offset(step * 3f, center.y))
    }
}

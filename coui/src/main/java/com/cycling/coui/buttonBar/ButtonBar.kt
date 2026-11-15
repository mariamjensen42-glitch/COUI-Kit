package com.cycling.coui.buttonBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonColors
import com.cycling.coui.button.CouiButtonDefaults
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.rememberCouiTextStyle
import com.cycling.coui.theme.CouiTheme

/** 按钮条的一项。顺序即布局顺序，横竖排都不重排。 */
@Immutable
data class CouiButtonBarItem(
    val text: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val style: CouiButtonStyle = CouiButtonStyle.Secondary,
    val colors: CouiButtonColors? = null,
) {
    init { require(text.isNotBlank()) { "按钮文案不能为空" } }
}

/**
 * 按钮条固定参数，取自 ColorOS 17 `com.support.dialog` 资源：
 * `coui_alert_dialog_button_horizontal_padding` 12dp、`coui_alert_dialog_button_height` 44dp、
 * `coui_alert_dialog_button_min_height_multiline` 56dp、
 * `coui_alert_dialog_button_parent_padding_horizontal` 16dp、
 * `coui_alert_dialog_button_parent_padding_horizontal_inside` 4dp、
 * `coui_alert_dialog_button_parent_padding_vertical` 4dp、
 * `coui_alert_dialog_button_parent_padding_bottom` 16dp、`coui_dialog_max_width` 392dp。
 */
object CouiButtonBarDefaults {
    /** `COUIButtonBarLayout.BUTTON_INDEX_COUNT`：正、负、中性三个槽位。 */
    const val maxItems = 3

    val buttonHorizontalPadding = 12.dp
    val buttonMinHeight = 44.dp
    val multilineButtonMinHeight = 56.dp
    val buttonVerticalPadding = 4.dp
    val edgePadding = 16.dp
    val insidePadding = 4.dp
    val parentVerticalPadding = 4.dp
    val parentBottomPadding = 16.dp
    val maxWidth = 392.dp
}

/**
 * 一到三个按钮的按钮条，按 `COUIButtonBarLayout` 的行为装配：只有恰好两项时要先比较文案宽度与
 * `getHorizontalButtonContentWidth`，任一放不下就整体转竖排；单项或三项恒竖排。
 * 横排按钮均分父宽，左右两侧留给父容器 16dp、中间 4dp；竖排按钮占满宽度，相邻按钮各留 4dp、末项下沿 16dp。
 * 多个按钮的实际高度由文案行数决定：单行 44dp、双行 56dp。内容超高时按钮条独立滚动。
 */
@Composable
fun ButtonBar(
    modifier: Modifier = Modifier,
    items: List<CouiButtonBarItem>,
    dark: Boolean = CouiTheme.isDark,
    shape: Shape = CouiButtonDefaults.shape,
) {
    require(items.size in 1..CouiButtonBarDefaults.maxItems) { "按钮条只支持 1 到 3 个按钮" }
    val scheme = CouiTheme.colorSchemeFor(dark)
    val textStyle = rememberCouiTextStyle(CouiTextKind.ButtonLabel)
    CouiButtonBarLayout(
        items = items,
        modifier = modifier,
        text = { it.text },
        enabled = { it.enabled },
        textStyle = textStyle,
        contentVerticalPadding = CouiButtonDefaults.verticalPadding,
    ) { item, _, buttonModifier ->
        Button(
            modifier = buttonModifier,
            onClick = item.onClick,
            label = item.text,
            style = item.style,
            enabled = item.enabled,
            dark = dark,
            colors = item.colors ?: CouiButtonDefaults.colors(item.style, scheme.accent, dark, scheme.onAccent),
            shape = shape,
        )
    }
}

/**
 * 按钮条的布局与测量，按钮外观由调用方给出。
 * [items] 是竖排顺序，[horizontalItems] 是横排顺序；`COUIButtonBarLayout` 只在两个槽位之间比较宽度，
 * 其余情形直接竖排。[textStyle] 与实际绘制文字一致，`contentVerticalPadding` 是按钮自身的上下内留白。
 * 回调 [onStacked] 在竖排状态变化时通知宿主，供正文区调整留白。
 */
@Composable
internal fun <T> CouiButtonBarLayout(
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalItems: List<T> = items,
    text: (T) -> String,
    enabled: (T) -> Boolean = { true },
    textStyle: TextStyle,
    contentVerticalPadding: Dp,
    onStacked: ((Boolean) -> Unit)? = null,
    content: @Composable (item: T, multiline: Boolean, modifier: Modifier) -> Unit,
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val scroll = rememberScrollState()
    // 文字视口只裁剪自己的内容；按钮运动绘制在文字之上。
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val parentWidth = if (constraints.hasBoundedWidth) with(density) { maxWidth.roundToPx() }
            else with(density) { CouiButtonBarDefaults.maxWidth.roundToPx() }
        // onMeasure：needSetButVertical 与横向内容宽都用 min(coui_dialog_max_width, 实测宽)。
        val measureWidth = minOf(parentWidth, with(density) { CouiButtonBarDefaults.maxWidth.roundToPx() })
        // getHorizontalButtonContentWidth：先均分父宽，再减按钮和父容器留白。
        val cellWidth = measureWidth / horizontalItems.size.coerceAtLeast(1)
        val available = cellWidth - with(density) {
            (CouiButtonBarDefaults.buttonHorizontalPadding * 2 + CouiButtonBarDefaults.edgePadding +
                CouiButtonBarDefaults.insidePadding).toPx()
        }
        val textWidths = horizontalItems.associateWith {
            measurer.measure(text(it), textStyle, softWrap = false).multiParagraph.maxIntrinsicWidth
        }
        // needSetButVertical：按钮数不为 2 时直接竖排，只有两项时才比较文案宽度。
        val stacked = horizontalItems.size != 2 || textWidths.values.any { it > available }
        if (onStacked != null) SideEffect { onStacked(stacked) }
        val verticalAvailable = with(density) {
            parentWidth - CouiButtonBarDefaults.edgePadding.roundToPx() * 2 -
                CouiButtonBarDefaults.buttonHorizontalPadding.roundToPx() * 2
        }.coerceAtLeast(0)
        fun buttonHeight(item: T, width: Int, multiline: Boolean): Int {
            val textHeight = measurer.measure(text(item), textStyle, maxLines = 2, overflow = TextOverflow.Ellipsis,
                constraints = Constraints(maxWidth = width.coerceAtLeast(0))).size.height
            return with(density) {
                maxOf(textHeight + contentVerticalPadding.roundToPx() * 2,
                    (if (multiline) CouiButtonBarDefaults.multilineButtonMinHeight
                    else CouiButtonBarDefaults.buttonMinHeight).roundToPx())
            }
        }
        val naturalHeight = with(density) {
            if (stacked) items.sumOf { item ->
                buttonHeight(item, verticalAvailable, textWidths[item]?.let { it > verticalAvailable } ?: false) +
                    CouiButtonBarDefaults.parentVerticalPadding.roundToPx() * 2
            } + CouiButtonBarDefaults.parentBottomPadding.roundToPx() -
                CouiButtonBarDefaults.parentVerticalPadding.roundToPx()
            else horizontalItems.maxOf { item ->
                buttonHeight(item,
                    cellWidth - CouiButtonBarDefaults.edgePadding.roundToPx() -
                        CouiButtonBarDefaults.insidePadding.roundToPx() -
                        CouiButtonBarDefaults.buttonHorizontalPadding.roundToPx() * 2, false)
            } + CouiButtonBarDefaults.parentVerticalPadding.roundToPx() +
                CouiButtonBarDefaults.parentBottomPadding.roundToPx()
        }
        // verticalScroll 即使无法滚动也会裁剪，只有实际超高时才需要这个视口。
        Box(if (naturalHeight > constraints.maxHeight) Modifier.verticalScroll(scroll) else Modifier) {
            if (stacked) {
                Column {
                    items.forEachIndexed { index, item ->
                        val multiline = textWidths[item]?.let { it > verticalAvailable } ?: false
                        Box(Modifier.padding(start = CouiButtonBarDefaults.edgePadding,
                            end = CouiButtonBarDefaults.edgePadding,
                            top = CouiButtonBarDefaults.parentVerticalPadding,
                            bottom = if (index == items.lastIndex) CouiButtonBarDefaults.parentBottomPadding
                            else CouiButtonBarDefaults.parentVerticalPadding)) {
                            content(item, multiline, Modifier.fillMaxWidth()
                                .heightIn(min = if (multiline) CouiButtonBarDefaults.multilineButtonMinHeight
                                else CouiButtonBarDefaults.buttonMinHeight))
                        }
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    horizontalItems.forEachIndexed { index, item ->
                        Box(Modifier.weight(1f).padding(
                            start = if (index == 0) CouiButtonBarDefaults.edgePadding
                            else CouiButtonBarDefaults.insidePadding,
                            end = if (index == horizontalItems.lastIndex) CouiButtonBarDefaults.edgePadding
                            else CouiButtonBarDefaults.insidePadding,
                            top = CouiButtonBarDefaults.parentVerticalPadding,
                            bottom = CouiButtonBarDefaults.parentBottomPadding)) {
                            content(item, false, Modifier.fillMaxWidth()
                                .heightIn(min = CouiButtonBarDefaults.buttonMinHeight))
                        }
                    }
                }
            }
        }
    }
}

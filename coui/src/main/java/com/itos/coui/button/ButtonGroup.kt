package com.itos.coui.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.rememberCouiTextStyle
import com.itos.coui.theme.CouiTheme

/** 按钮组的一项；description 对应原厂 `DescButtonTextSpan` 的两行文案。 */
@Immutable
data class CouiButtonGroupItem(
    val label: String,
    val onClick: () -> Unit,
    val description: String? = null,
    val enabled: Boolean = true,
    val style: CouiButtonStyle = CouiButtonStyle.Secondary,
    val colors: CouiButtonColors? = null,
) {
    init { require(label.isNotBlank()) { "按钮文案不能为空" } }
}

/**
 * 按钮组固定参数，取自 ColorOS 17 `com.support.button` / `com.support.appcompat` 资源：
 * `coui_medium_btn_width` 152dp、`coui_larger_btn_width` 174dp、`coui_medium_btn_height` 44dp、
 * `coui_horizontal_btn_padding` 4dp、`coui_vertical_btn_margin_top` 16dp、
 * `coui_horizontal_btn_margin` 16dp、`coui_horizontal_single_btn_margin` 32dp。
 * 横排两按钮各自带 4dp 内侧留白，视觉间隔因此是 8dp，与 `SimpleButtonGroupCtrl.setHorizontalButtonMargin` 一致；
 * 双行下限取 `coui_alert_dialog_button_min_height_multiline` 56dp。
 */
object CouiButtonGroupDefaults {
    /** `SimpleButtonGroupCtrl.MAX_SIZE`：组内最多两个按钮。 */
    const val maxItems = 2

    val mediumButtonWidth = 152.dp
    val verticalButtonWidth = 174.dp
    val buttonMinHeight = 44.dp
    val multilineButtonMinHeight = 56.dp
    val horizontalGap = 8.dp
    val verticalGap = 16.dp
    val horizontalLayoutPadding = 16.dp
    val verticalLayoutPadding = 32.dp

    /** `WindowWidthSizeClass.Compact`：窗口宽小于 600dp 视为小屏，按钮改为占满并均分。 */
    val compactWidthThreshold = 600.dp
}

/**
 * 一到两个按钮的按钮组，按 Settings 17 的 `SimpleButtonGroupCtrl` 行为装配：
 * 测量每个按钮文案与可用列宽，任一项放不下就整体转竖排（第一项排在下方，与原厂 `addView` 顺序一致），
 * 小屏横排时按钮均分父宽，大屏横排保持 152dp。竖排按钮取 174dp（大屏）或占满（小屏）。
 * 形变、按压、跟手光与蒙层复用普通 [Button] 的链路。
 */
@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    items: List<CouiButtonGroupItem>,
    dark: Boolean = CouiTheme.isDark,
    shape: Shape = CouiButtonDefaults.shape,
) {
    require(items.size in 1..CouiButtonGroupDefaults.maxItems) { "按钮组只支持 1 到 2 个按钮" }
    val density = LocalDensity.current
    val compact = LocalConfiguration.current.screenWidthDp < CouiButtonGroupDefaults.compactWidthThreshold.value
    val textStyle = rememberCouiTextStyle(CouiTextKind.ButtonLabel)
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val layoutWidth = if (constraints.hasBoundedWidth) with(density) { maxWidth.roundToPx() }
            else with(density) { CouiButtonGroupDefaults.compactWidthThreshold.roundToPx() }
        // getButtonLineCount：描述型按钮恒为单行；其余按文案宽度与列宽比较。
        val available = with(density) {
            val slot = if (compact) {
                (layoutWidth - (CouiButtonGroupDefaults.horizontalLayoutPadding * 2 +
                    CouiButtonGroupDefaults.horizontalGap).roundToPx()) / 2
            } else {
                CouiButtonGroupDefaults.mediumButtonWidth.roundToPx()
            }
            slot - CouiButtonDefaults.horizontalPadding.roundToPx() * 2
        }.coerceAtLeast(0)
        val multiline = items.any { item ->
            item.description == null &&
                measurer.measure(item.label, textStyle, softWrap = false).multiParagraph.maxIntrinsicWidth > available
        }
        if (multiline) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiButtonGroupDefaults.verticalLayoutPadding),
                verticalArrangement = Arrangement.spacedBy(CouiButtonGroupDefaults.verticalGap),
            ) {
                items.reversed().forEach { item ->
                    GroupButton(item, dark, shape,
                        if (compact) Modifier.fillMaxWidth()
                        else Modifier.width(CouiButtonGroupDefaults.verticalButtonWidth),
                        CouiButtonGroupDefaults.multilineButtonMinHeight)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = CouiButtonGroupDefaults.horizontalLayoutPadding),
                horizontalArrangement = Arrangement.spacedBy(CouiButtonGroupDefaults.horizontalGap),
            ) {
                items.forEach { item ->
                    GroupButton(item, dark, shape,
                        if (compact) Modifier.weight(1f)
                        else Modifier.width(CouiButtonGroupDefaults.mediumButtonWidth),
                        CouiButtonDefaults.minHeight)
                }
            }
        }
    }
}

@Composable
private fun GroupButton(item: CouiButtonGroupItem, dark: Boolean, shape: Shape,
    modifier: Modifier, minHeight: Dp) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    Button(
        modifier = modifier.heightIn(min = minHeight),
        onClick = item.onClick,
        label = item.label,
        style = item.style,
        enabled = item.enabled,
        dark = dark,
        colors = item.colors ?: CouiButtonDefaults.colors(item.style, scheme.accent, dark, scheme.onAccent),
        shape = shape,
        description = item.description,
    )
}

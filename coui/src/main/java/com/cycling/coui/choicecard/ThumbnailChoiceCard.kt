package com.cycling.coui.choicecard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.cycling.coui.effect.couiButtonFeedback
import com.cycling.coui.layout.CouiPageColors
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.radiobutton.RadioButton
import com.cycling.coui.shape.CouiShape
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/**
 * 缩略图卡的尺寸、留白与圆角。数值取自 ColorOS 17 设置页的真机布局树
 * （一加 Ace 5，1264×2780 @560dpi，即 3.5px/dp），像素到 dp 均为整数换算。
 */
object CouiThumbnailChoiceDefaults {
    /** 单选型（两列）对应「显示与亮度」：两卡无缝并排，贴在同一分组表面上，卡片不自绘底色。 */
    val selectionHeight = 219.dp
    val selectionThumbnailSize = DpSize(70.dp, 142.dp)
    val selectionLabelSpacing = 24.dp
    val selectionRadioSpacing = 10.dp

    /** 导航型（三列）对应「通知与控制中心 - 分类设置」：每张卡自带底色、圆角与 8dp 间距。 */
    val navigationShape = CouiShape(20.dp, 3f)
    val navigationPadding = 16.dp
    val navigationSpacing = 8.dp
    val navigationThumbnailSize = DpSize(48.dp, 102.dp)
    val navigationLabelSpacing = 8.dp
    val navigationValueSpacing = 4.dp
    val navigationValueGap = 4.dp
    val navigationChevronSize = DpSize(6.dp, 11.dp)
    val navigationChevronStroke = 1.5.dp
}

/**
 * 一张缩略图卡：缩略图、主文字，导航型另带右侧当前值。
 * 缩略图由调用方提供，库只负责槽位尺寸与对齐，不假设它是图片还是绘制结果。
 */
@Stable
class CouiThumbnailChoice(
    val label: String,
    val thumbnail: @Composable () -> Unit,
    val value: String? = null,
)

/**
 * 两列缩略图单选卡，对应 ColorOS 17「设置 - 显示与亮度」的亮色 / 暗色模式预览。
 *
 * 两卡无缝并排且**不自绘底色**，直接贴在外层 [com.cycling.coui.layout.Group] 或页面表面上；
 * 每卡自上而下为缩略图、主文字与 [RadioButton]，三者共用同一条内容轴。
 * 选中由宿主持有：[selectedIndex] 与 [onSelect] 成对使用，整卡承担点击并暴露 RadioButton 语义。
 */
@Composable
fun ThumbnailChoiceGroup(
    modifier: Modifier = Modifier,
    items: List<CouiThumbnailChoice>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    colors: CouiPageColors = CouiPageDefaults.themeColors(CouiTheme.isDark),
) {
    require(items.isNotEmpty()) { "缩略图选择卡组至少需要一项" }
    Row(modifier.fillMaxWidth().selectableGroup()) {
        items.forEachIndexed { index, item ->
            ThumbnailChoiceCell(Modifier.weight(1f), item, index == selectedIndex, colors) { onSelect(index) }
        }
    }
}

/**
 * 三列缩略图导航卡，对应 ColorOS 17「设置 - 通知与控制中心 - 分类设置」。
 *
 * 每卡自带 [CouiThumbnailChoiceDefaults.navigationShape] 轮廓与分组卡片底色，间距 8dp；
 * 内容为缩略图、主文字、当前值与箭头。整卡承担点击，值文字只读。
 */
@Composable
fun ThumbnailNavigationGroup(
    modifier: Modifier = Modifier,
    items: List<CouiThumbnailChoice>,
    onSelect: (Int) -> Unit,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
) {
    require(items.isNotEmpty()) { "缩略图导航卡组至少需要一项" }
    Row(modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CouiThumbnailChoiceDefaults.navigationSpacing)) {
        items.forEachIndexed { index, item ->
            ThumbnailNavigationCell(Modifier.weight(1f), item, dark, colors) { onSelect(index) }
        }
    }
}

@Composable
private fun ThumbnailChoiceCell(
    modifier: Modifier,
    item: CouiThumbnailChoice,
    selected: Boolean,
    colors: CouiPageColors,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    Column(modifier.height(CouiThumbnailChoiceDefaults.selectionHeight)
        .selectable(selected = selected, interactionSource = source, indication = null,
            role = Role.RadioButton, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(CouiThumbnailChoiceDefaults.selectionThumbnailSize), Alignment.Center) { item.thumbnail() }
        Spacer(Modifier.height(CouiThumbnailChoiceDefaults.selectionLabelSpacing))
        Text(text = item.label, kind = CouiTextKind.ItemTitle, color = colors.title,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(CouiThumbnailChoiceDefaults.selectionRadioSpacing))
        RadioButton(selected = selected)
    }
}

@Composable
private fun ThumbnailNavigationCell(
    modifier: Modifier,
    item: CouiThumbnailChoice,
    dark: Boolean,
    colors: CouiPageColors,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    Column(modifier.clip(CouiThumbnailChoiceDefaults.navigationShape)
        .background(colors.card)
        .couiButtonFeedback(dark, source)
        .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
        .padding(CouiThumbnailChoiceDefaults.navigationPadding),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(CouiThumbnailChoiceDefaults.navigationThumbnailSize), Alignment.Center) { item.thumbnail() }
        Spacer(Modifier.height(CouiThumbnailChoiceDefaults.navigationLabelSpacing))
        Text(text = item.label, kind = CouiTextKind.ItemValue, color = colors.title,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(CouiThumbnailChoiceDefaults.navigationValueSpacing))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val value = item.value
            if (!value.isNullOrEmpty()) {
                Text(text = value, kind = CouiTextKind.Body, color = colors.summary,
                    textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.size(CouiThumbnailChoiceDefaults.navigationValueGap))
            }
            Chevron(colors.summary)
        }
    }
}

/** 6×11dp 的右向箭头；核心模块不依赖 coui-icons，与 Accordion 的箭头同为本地绘制。 */
@Composable
private fun Chevron(color: Color) {
    Canvas(Modifier.size(CouiThumbnailChoiceDefaults.navigationChevronSize)) {
        val stroke = CouiThumbnailChoiceDefaults.navigationChevronStroke.toPx()
        val inset = stroke / 2f
        val path = Path().apply {
            moveTo(inset, inset)
            lineTo(size.width - inset, size.height / 2f)
            lineTo(inset, size.height - inset)
        }
        drawPath(path, color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

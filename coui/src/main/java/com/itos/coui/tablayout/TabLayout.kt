package com.itos.coui.tablayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 页签条：文字标签配一条跟随选中项滑动的底部指示器。
 *
 * [selectedIndex] 与 [onSelectedIndexChange] 由宿主持有，组件不保存选中状态；
 * 重复点击当前项仍会回调，禁用项不响应但仍占位移。
 *
 * 排布按原厂 `COUISlidingTabStrip.onMeasure`：每项占「文字宽 + [CouiTabLayoutDefaults.tabSpacing]」，
 * 其中一半间距落在文字两侧，因此项中心与文字中心重合、整项都可点击。
 * [CouiTabLayoutMode.Scrollable] 以 `edgeMargin - tabSpacing / 2` 收边并可横向滚动，选中项滚向中间；
 * [CouiTabLayoutMode.Fixed] 先按内容宽度 `(页签数 - 1) × 间距 + 两侧外边距 + 文字总宽` 判断：
 * 不超过 [CouiTabLayoutDefaults.contentMinWidth] 时把临界宽度按「页签数 + 1」均分剩余间距，
 * 不超过视口时退化为固定间距居中，再宽则按最小外边距排布并裁切。
 *
 * 指示器宽度取文字宽度加两侧 [CouiTabLayoutDefaults.indicatorPadding]，不小于
 * [CouiTabLayoutDefaults.indicatorMinWidth]，高度 [CouiTabLayoutDefaults.indicatorHeight] 贴在本文本行底部；
 * 切换时长按 `min(300, 距离 × 50 + 150)` 计算；文字颜色用同一进度在选中色与未选中色之间插值，
 * 选中项切换为 medium 字重。
 */
@Composable
fun TabLayout(
    modifier: Modifier = Modifier,
    tabs: List<CouiTabItem>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    mode: CouiTabLayoutMode = CouiTabLayoutMode.Fixed,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiTabLayoutColors = CouiTabLayoutDefaults.colors(dark),
    dividerVisible: Boolean = false,
) {
    if (tabs.isEmpty()) return
    val selected = selectedIndex.coerceIn(0, tabs.lastIndex)

    BoxWithConstraints(modifier.defaultMinSize(minHeight = CouiTabLayoutDefaults.height)) {
        val density = LocalDensity.current
        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val textMeasurer = rememberTextMeasurer()
        val textStyle = remember {
            TextStyle(
                fontSize = CouiTabLayoutDefaults.textSize,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
        }
        val viewportPx = with(density) { maxWidth.toPx() }
        val spacingPx = with(density) { CouiTabLayoutDefaults.tabSpacing.toPx() }
        val edgePx = with(density) { CouiTabLayoutDefaults.edgeMargin.toPx() }
        val contentMinPx = with(density) { CouiTabLayoutDefaults.contentMinWidth.toPx() }
        val tabMaxPx = with(density) { CouiTabLayoutDefaults.tabMaxWidth.toPx() }
        val indicatorPaddingPx = with(density) { CouiTabLayoutDefaults.indicatorPadding.toPx() }
        val indicatorMinWidthPx = with(density) { CouiTabLayoutDefaults.indicatorMinWidth.toPx() }
        val indicatorHeightPx = with(density) { CouiTabLayoutDefaults.indicatorHeight.toPx() }

        val textWidths = remember(tabs, textStyle, density, tabMaxPx) {
            tabs.map { tab ->
                textMeasurer.measure(
                    text = tab.label,
                    style = textStyle,
                    maxLines = 1,
                    constraints = Constraints(maxWidth = tabMaxPx.roundToInt()),
                ).size.width.toFloat()
            }
        }
        val textTotal = textWidths.sum()
        val scrollEdge = (edgePx - spacingPx / 2f).coerceAtLeast(0f)
        val (gapPx, startPx) = when (mode) {
            CouiTabLayoutMode.Fixed -> {
                val needed = (tabs.size - 1) * spacingPx + edgePx * 2 + textTotal
                when {
                    needed <= contentMinPx -> {
                        // 临界宽度以下按实际视口均分，超过时以 contentMinWidth 为准并把余量居中。
                        val basis = min(viewportPx, contentMinPx)
                        val gap = max((basis - textTotal) / (tabs.size + 1), spacingPx)
                        gap to (viewportPx - basis + gap) / 2f
                    }
                    needed <= viewportPx -> spacingPx to ((viewportPx - textTotal) - spacingPx * tabs.size) / 2f
                    else -> spacingPx to scrollEdge
                }
            }
            CouiTabLayoutMode.Scrollable -> spacingPx to scrollEdge
        }
        val lefts = FloatArray(tabs.size)
        var cursor = startPx
        for (index in tabs.indices) {
            lefts[index] = cursor
            cursor += textWidths[index] + gapPx
        }
        val contentWidthPx = cursor + startPx
        val targetCenter = lefts[selected] + (textWidths[selected] + gapPx) / 2f
        val indicatorWidthPx = (textWidths[selected] + indicatorPaddingPx * 2)
            .coerceAtLeast(indicatorMinWidthPx)

        val indicatorCenter = remember { Animatable(0f) }
        val indicatorWidth = remember { Animatable(0f) }
        val selection = remember { Animatable(selected.toFloat()) }
        // Animatable.value 不是 snapshot state；动画帧要经 asState() 才能触发重组与重绘。
        val indicatorCenterState = indicatorCenter.asState()
        val indicatorWidthState = indicatorWidth.asState()
        val selectionState = selection.asState()
        var initialized by remember { mutableStateOf(false) }
        var previousSelected by remember { mutableIntStateOf(selected) }
        LaunchedEffect(targetCenter, indicatorWidthPx, selected) {
            val duration = CouiTabLayoutDefaults.indicatorDurationMillis(abs(selected - previousSelected))
            previousSelected = selected
            if (!initialized) {
                indicatorCenter.snapTo(targetCenter)
                indicatorWidth.snapTo(indicatorWidthPx)
                selection.snapTo(selected.toFloat())
                initialized = true
            } else {
                launch { indicatorCenter.animateTo(targetCenter, tween(duration)) }
                launch { indicatorWidth.animateTo(indicatorWidthPx, tween(duration)) }
                launch { selection.animateTo(selected.toFloat(), tween(duration)) }
            }
        }

        val scrollState = rememberScrollState()
        LaunchedEffect(selected, mode, viewportPx, contentWidthPx) {
            if (mode != CouiTabLayoutMode.Scrollable || contentWidthPx <= viewportPx) return@LaunchedEffect
            // 滚动上限来自首次布局，maxValue 到位前不能夹紧目标。
            snapshotFlow { scrollState.maxValue }.first { it > 0 }
            val target = (targetCenter - viewportPx / 2f).roundToInt().coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(target, tween(CouiTabLayoutDefaults.scrollAnimationMillis))
        }

        // 指示器画在内容坐标系里，随滚动一起位移，RTL 下与 placeRelative 的镜像保持一致。
        val drawIndicator: DrawScope.() -> Unit = {
            val center = if (rtl) size.width - indicatorCenterState.value else indicatorCenterState.value
            val width = indicatorWidthState.value
            drawRect(
                color = colors.indicator,
                topLeft = Offset(center - width / 2f, size.height - indicatorHeightPx),
                size = Size(width, indicatorHeightPx),
            )
        }

        val rowModifier = if (mode == CouiTabLayoutMode.Fixed) {
            Modifier.fillMaxWidth().clipToBounds()
        } else {
            Modifier.fillMaxWidth().horizontalScroll(scrollState)
        }
        Layout(
            modifier = rowModifier.drawBehind(drawIndicator),
            content = {
                tabs.forEachIndexed { index, tab ->
                    val interaction = remember(tab.key) { MutableInteractionSource() }
                    Box(
                        Modifier.semantics { this.selected = index == selected }
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                enabled = tab.enabled,
                                role = Role.Tab,
                            ) { onSelectedIndexChange(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        val fraction = (1f - abs(index - selectionState.value)).coerceIn(0f, 1f)
                        BasicText(
                            text = tab.label,
                            style = textStyle.copy(
                                color = lerp(colors.unselectedText, colors.selectedText, fraction),
                                fontWeight = if (index == selected) FontWeight.Medium else FontWeight.Normal,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
        ) { measurables, constraints ->
            val height = constraints.constrainHeight(CouiTabLayoutDefaults.height.roundToPx())
            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(Constraints.fixed((textWidths[index] + gapPx).roundToInt(), height))
            }
            val width = if (mode == CouiTabLayoutMode.Fixed && constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                contentWidthPx.roundToInt()
            }
            layout(width, height) {
                placeables.forEachIndexed { index, placeable -> placeable.placeRelative(lefts[index].roundToInt(), 0) }
            }
        }
        if (dividerVisible) {
            Box(
                Modifier.align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(CouiTabLayoutDefaults.dividerHeight)
                    .background(colors.divider),
            )
        }
    }
}

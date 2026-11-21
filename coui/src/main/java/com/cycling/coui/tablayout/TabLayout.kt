package com.cycling.coui.tablayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
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
import com.cycling.coui.segmentbutton.SegmentGeometry
import com.cycling.coui.segmentbutton.segmentWidths
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 页签条：胶囊容器里的一排文字页签，选中项是一枚跟随切换滑动的胶囊填充。
 *
 * [selectedIndex] 与 [onSelectedIndexChange] 由宿主持有，组件不保存选中状态；
 * 重复点击当前项仍会回调，禁用项不响应但仍占位移。
 *
 * 几何取自分段控件：容器高 [CouiTabLayoutDefaults.height]、内容内缩
 * [CouiTabLayoutDefaults.containerPadding]，每段自然宽 `max(tabMinWidth, 文字宽 + 2 × labelPadding)`。
 * 容器包裹内容并在宿主给的宽度里居中；[CouiTabLayoutMode.Fixed] 在内容超宽时按原厂水位算法收缩，
 * [CouiTabLayoutMode.Scrollable] 保留自然宽度、可横向滚动，选中项滚向中间。
 *
 * 选中胶囊取整段宽度、高为容器减去上下留白；切换时长按 `min(300, 距离 × 50 + 150)`
 * 补间中心与宽度，文字颜色用同一进度在选中色与未选中色之间插值，选中项切换为 medium 字重。
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
) {
    if (tabs.isEmpty()) return
    val selected = selectedIndex.coerceIn(0, tabs.lastIndex)

    BoxWithConstraints(
        modifier = modifier.defaultMinSize(minHeight = CouiTabLayoutDefaults.height),
        contentAlignment = Alignment.Center,
    ) {
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
        val heightPx = with(density) { CouiTabLayoutDefaults.height.toPx() }
        val paddingPx = with(density) { CouiTabLayoutDefaults.containerPadding.toPx() }
        val labelPaddingPx = with(density) { CouiTabLayoutDefaults.labelPadding.toPx() }
        val minTabPx = with(density) { CouiTabLayoutDefaults.tabMinWidth.toPx() }
        val tabMaxPx = with(density) { CouiTabLayoutDefaults.tabMaxWidth.toPx() }
        val viewportPx = if (constraints.hasBoundedWidth) with(density) { maxWidth.toPx() } else Float.MAX_VALUE

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
        val naturalWidths = remember(textWidths, minTabPx, labelPaddingPx) {
            textWidths.map { max(minTabPx, it + labelPaddingPx * 2f) }
        }
        val naturalTotal = naturalWidths.sum() + paddingPx * 2f
        val contentWidthPx = when (mode) {
            CouiTabLayoutMode.Fixed -> min(naturalTotal, viewportPx)
            CouiTabLayoutMode.Scrollable -> naturalTotal
        }
        val contentAreaPx = (contentWidthPx - paddingPx * 2f).roundToInt().coerceAtLeast(0)
        val widths = remember(naturalWidths, contentAreaPx) { segmentWidths(naturalWidths, contentAreaPx) }
        val geometry = remember(contentWidthPx, heightPx, paddingPx, widths, rtl) {
            SegmentGeometry(contentWidthPx, heightPx, paddingPx, widths, rtl, false)
        }
        val targetCenter = geometry.center(selected)
        val targetWidth = widths[selected].toFloat()

        val indicatorCenter = remember { Animatable(0f) }
        val indicatorWidth = remember { Animatable(0f) }
        val selection = remember { Animatable(selected.toFloat()) }
        // Animatable.value 不是 snapshot state；动画帧要经 asState() 才能触发重组与重绘。
        val indicatorCenterState = indicatorCenter.asState()
        val indicatorWidthState = indicatorWidth.asState()
        val selectionState = selection.asState()
        var initialized by remember { mutableStateOf(false) }
        var previousSelected by remember { mutableIntStateOf(selected) }
        LaunchedEffect(targetCenter, targetWidth, selected) {
            val duration = CouiTabLayoutDefaults.indicatorDurationMillis(abs(selected - previousSelected))
            previousSelected = selected
            if (!initialized) {
                indicatorCenter.snapTo(targetCenter)
                indicatorWidth.snapTo(targetWidth)
                selection.snapTo(selected.toFloat())
                initialized = true
            } else {
                launch { indicatorCenter.animateTo(targetCenter, tween(duration)) }
                launch { indicatorWidth.animateTo(targetWidth, tween(duration)) }
                launch { selection.animateTo(selected.toFloat(), tween(duration)) }
            }
        }

        val scrollState = rememberScrollState()
        val scrollable = mode == CouiTabLayoutMode.Scrollable && contentWidthPx > viewportPx
        LaunchedEffect(selected, scrollable, viewportPx, contentWidthPx) {
            if (!scrollable) return@LaunchedEffect
            // 滚动上限来自首次布局，maxValue 到位前不能夹紧目标。
            snapshotFlow { scrollState.maxValue }.first { it > 0 }
            val target = (targetCenter - viewportPx / 2f).roundToInt().coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(target, tween(CouiTabLayoutDefaults.scrollAnimationMillis))
        }

        // 容器与选中胶囊画在内容坐标系里，随滚动一起位移；RTL 的中心已由 SegmentGeometry 镜像。
        val drawSurface: DrawScope.() -> Unit = {
            drawRoundRect(
                color = colors.container,
                size = size,
                cornerRadius = CornerRadius(size.height / 2f),
            )
            val indicatorHeight = (size.height - paddingPx * 2f).coerceAtLeast(0f)
            val width = indicatorWidthState.value
            if (width > 0f && indicatorHeight > 0f) {
                drawRoundRect(
                    color = colors.indicator,
                    topLeft = Offset(indicatorCenterState.value - width / 2f, paddingPx),
                    size = Size(width, indicatorHeight),
                    cornerRadius = CornerRadius(indicatorHeight / 2f),
                )
            }
        }

        val rowModifier = if (scrollable) {
            Modifier.horizontalScroll(scrollState)
        } else {
            Modifier
        }
        Layout(
            modifier = rowModifier.drawBehind(drawSurface),
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
            val height = constraints.constrainHeight(heightPx.roundToInt())
            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(Constraints.fixed(widths[index], height))
            }
            layout(contentWidthPx.roundToInt(), height) {
                var start = paddingPx
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(start.roundToInt(), 0)
                    start += widths[index]
                }
            }
        }
    }
}

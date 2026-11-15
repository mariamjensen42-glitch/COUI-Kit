package com.itos.coui.tablayout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
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
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 页签条：文字标签配一条跟随选中项滑动的底部指示器。
 *
 * [selectedIndex] 与 [onSelectedIndexChange] 由宿主持有，组件不保存选中状态；
 * 重复点击当前项仍会回调，禁用项不响应但仍占位移。[CouiTabLayoutMode.Fixed] 等分整条宽度，
 * [CouiTabLayoutMode.Scrollable] 按文字宽度排布并把选中项滚向中间。
 *
 * 指示器宽度取文字宽度加两侧 [CouiTabLayoutDefaults.indicatorPadding]，不小于
 * [CouiTabLayoutDefaults.indicatorMinWidth]，切换时长按 `min(300, 距离 × 50 + 150)` 计算；
 * 文字颜色用同一进度在选中色与未选中色之间插值，选中项切换为 medium 字重。
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
        val textMeasurer = rememberTextMeasurer()
        val baseTextStyle = remember {
            TextStyle(
                fontSize = CouiTabLayoutDefaults.textSize,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )
        }
        val viewportPx = with(density) { maxWidth.toPx() }
        val tabPaddingPx = with(density) { CouiTabLayoutDefaults.tabHorizontalPadding.toPx() }
        val tabMinWidthPx = with(density) { CouiTabLayoutDefaults.tabMinWidth.toPx() }
        val indicatorPaddingPx = with(density) { CouiTabLayoutDefaults.indicatorPadding.toPx() }
        val indicatorMinWidthPx = with(density) { CouiTabLayoutDefaults.indicatorMinWidth.toPx() }
        val indicatorHeightPx = with(density) { CouiTabLayoutDefaults.indicatorHeight.toPx() }

        val textWidths = remember(tabs, baseTextStyle) {
            tabs.map { tab ->
                textMeasurer.measure(tab.label, style = baseTextStyle, maxLines = 1)
                    .size.width.toFloat()
            }
        }
        val tabWidths = when (mode) {
            CouiTabLayoutMode.Fixed -> List(tabs.size) { viewportPx / tabs.size }
            CouiTabLayoutMode.Scrollable -> textWidths.map {
                (it + tabPaddingPx * 2).coerceAtLeast(tabMinWidthPx)
            }
        }
        val lefts = FloatArray(tabs.size)
        var cursor = 0f
        for (index in tabs.indices) {
            lefts[index] = cursor
            cursor += tabWidths[index]
        }
        val indicatorWidthPx = (textWidths[selected] + indicatorPaddingPx * 2)
            .coerceAtLeast(indicatorMinWidthPx)
        val targetLeft = lefts[selected] + (tabWidths[selected] - indicatorWidthPx) / 2f

        val indicatorLeft = remember { Animatable(0f) }
        val indicatorWidth = remember { Animatable(0f) }
        val selection = remember { Animatable(selected.toFloat()) }
        // Animatable.value 不是 snapshot state；动画帧要经 asState() 才能触发重组与重绘。
        val indicatorLeftState = indicatorLeft.asState()
        val indicatorWidthState = indicatorWidth.asState()
        val selectionState = selection.asState()
        var initialized by remember { mutableStateOf(false) }
        var previousSelected by remember { mutableIntStateOf(selected) }
        LaunchedEffect(targetLeft, indicatorWidthPx, selected) {
            val duration = CouiTabLayoutDefaults.indicatorDurationMillis(abs(selected - previousSelected))
            previousSelected = selected
            if (!initialized) {
                indicatorLeft.snapTo(targetLeft)
                indicatorWidth.snapTo(indicatorWidthPx)
                selection.snapTo(selected.toFloat())
                initialized = true
            } else {
                launch { indicatorLeft.animateTo(targetLeft, tween(duration)) }
                launch { indicatorWidth.animateTo(indicatorWidthPx, tween(duration)) }
                launch { selection.animateTo(selected.toFloat(), tween(duration)) }
            }
        }

        val scrollState = rememberScrollState()
        LaunchedEffect(selected, mode, tabs.size, viewportPx) {
            if (mode != CouiTabLayoutMode.Scrollable) return@LaunchedEffect
            val target = (lefts[selected] + tabWidths[selected] / 2f - viewportPx / 2f)
                .roundToInt().coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(target, tween(CouiTabLayoutDefaults.scrollAnimationMillis))
        }

        val rowModifier = if (mode == CouiTabLayoutMode.Fixed) {
            Modifier.fillMaxWidth().fillMaxHeight()
        } else {
            Modifier.fillMaxWidth().fillMaxHeight().horizontalScroll(scrollState)
        }
        Row(rowModifier) {
            tabs.forEachIndexed { index, tab ->
                val interaction = remember { MutableInteractionSource() }
                val itemModifier = if (mode == CouiTabLayoutMode.Fixed) {
                    Modifier.weight(1f)
                } else {
                    Modifier.widthIn(min = CouiTabLayoutDefaults.tabMinWidth)
                        .padding(horizontal = CouiTabLayoutDefaults.tabHorizontalPadding)
                }
                Box(
                    itemModifier
                        .semantics { this.selected = index == selected }
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
                        style = baseTextStyle.copy(
                            color = lerp(colors.unselectedText, colors.selectedText, fraction),
                            fontWeight = if (index == selected) FontWeight.Medium else FontWeight.Normal,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Canvas(Modifier.matchParentSize()) {
            val scrollX = if (mode == CouiTabLayoutMode.Scrollable) scrollState.value.toFloat() else 0f
            drawRect(
                color = colors.indicator,
                topLeft = Offset(indicatorLeftState.value - scrollX, size.height - indicatorHeightPx),
                size = Size(indicatorWidthState.value, indicatorHeightPx),
            )
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

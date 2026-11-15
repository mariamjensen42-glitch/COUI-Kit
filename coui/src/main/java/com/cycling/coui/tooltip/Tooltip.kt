package com.cycling.coui.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.cycling.coui.shape.CouiShapes
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.theme.ProvideCouiContent

/** 气泡的容器与文字色。 */
@Immutable
data class CouiTooltipColors(val container: Color, val content: Color)

/** 气泡固定参数，取自 ColorOS 17 手机管家内嵌的 `COUIToolTips` 与 `coui_tooltips_*` 尺寸。 */
object CouiTooltipDefaults {
    /** `couiRoundCornerM` = 12dp，与 `coui_tool_tips_background` 的 corners 一致。 */
    /** 原生基线；组件默认读取 `CouiTheme.shapes.medium`。 */
    val shape: Shape = CouiShapes.Default.medium
    val gap = 8.dp

    /** 原厂四边留白均为 13dp。 */
    val padding = 13.dp
    val maxWidth = 240.dp

    /** `couiToolTipsBackgroundColor` = `?couiColorTips`：日间 #0080FF、夜间 #1A8CFF，内容为白色。 */
    fun colors(accent: Color): CouiTooltipColors = CouiTooltipColors(accent, Color.White)
}

/**
 * 挂在内容上的气泡提示。可见性由宿主控制：visible 为 true 时在 anchor 上方弹出，
 * 空间不足时翻到下方并夹紧在窗口内；点击外部或返回键回调 onDismissRequest。
 * 组件不安装长按、悬停或点击手势，避免抢占子节点的输入；宿主用 combinedClickable、
 * 长按或悬停自行触发。气泡不获取焦点，也不改变页面焦点顺序。
 * 容器、留白与圆角取原厂 `COUIToolTips` 默认分支；定位与夹紧规则由本库实现。
 */
@Composable
fun Tooltip(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    text: String,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiTooltipColors = CouiTooltipDefaults.colors(CouiTheme.colorSchemeFor(dark).accent),
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        if (visible) {
            val gap = with(LocalDensity.current) { CouiTooltipDefaults.gap.roundToPx() }
            Popup(
                popupPositionProvider = remember(gap) { CouiTooltipPositionProvider(gap) },
                onDismissRequest = onDismissRequest,
                properties = remember {
                    PopupProperties(
                        focusable = false,
                        dismissOnClickOutside = true,
                        dismissOnBackPress = true,
                        usePlatformDefaultWidth = false,
                    )
                },
            ) {
                Box(
                    Modifier.widthIn(max = CouiTooltipDefaults.maxWidth)
                        .clip(CouiTheme.shapes.medium)
                        .background(colors.container, CouiTheme.shapes.medium)
                        .padding(CouiTooltipDefaults.padding),
                ) {
                    ProvideCouiContent(color = colors.content) {
                        Text(text = text, kind = CouiTextKind.ItemSummary, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

/** 优先放在 anchor 上方并水平居中；上方放不下时改放下方，两侧都按窗口夹紧。 */
private class CouiTooltipPositionProvider(private val gap: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val x = (anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2).coerceIn(0, maxX)
        val above = anchorBounds.top - popupContentSize.height - gap
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val y = if (above >= 0) above else (anchorBounds.bottom + gap).coerceIn(0, maxY)
        return IntOffset(x, y)
    }
}

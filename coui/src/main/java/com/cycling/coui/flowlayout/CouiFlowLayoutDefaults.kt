package com.cycling.coui.flowlayout

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/** 换行布局里的一项；[key] 在重排时保持稳定，[label] 是标签上显示的文案。 */
@Immutable
data class CouiFlowItem(val key: Any, val label: String)

/** 换行布局的标签、文字与展开控件配色。 */
@Immutable
data class CouiFlowLayoutColors(
    val chipContainer: Color,
    val chipContent: Color,
    val control: Color,
)

/**
 * 换行布局的固定参数，取自 `coui_component_search_history_*` 与 `Widget.COUI.Chip.Suggestion`。
 *
 * 原厂 `COUIFlowLayout` 按测量宽度逐行装填，折叠时只保留前 [maxRowsFolded] 行，
 * 展开后最多 [maxRowsUnfolded] 行；超出部分不是删除，而是 alpha 置 0 并被容器高度裁剪。
 */
object CouiFlowLayoutDefaults {
    /** `coui_component_search_history_flow_item_spacing`。 */
    val itemSpacing = 8.dp

    /** `coui_component_search_history_flow_line_spacing`。 */
    val lineSpacing = 8.dp

    /** `coui_chip_suggest_style_height`。 */
    val chipHeight = 28.dp

    /** `coui_chip_default_min_width`。 */
    val chipMinWidth = 52.dp

    /** `coui_chip_default_max_width`。 */
    val chipMaxWidth = 300.dp

    /** `coui_chip_suggest_style_chip_horizontal_padding`，标签左右各一份。 */
    val chipHorizontalPadding = 12.dp

    /** 展开/折叠按钮与标签同高，来自原厂 `getBaseExpandButton()` 读取的 chipMinHeight。 */
    val controlSize = 28.dp

    /** 原厂 `COUIFlowLayout` 的 `maxRowFolded` 默认值；实际页面通常指定 2。 */
    const val maxRowsFolded = Int.MAX_VALUE

    /** 原厂 `COUIFlowLayout` 的 `maxRowUnfolded` 默认值。 */
    const val maxRowsUnfolded = Int.MAX_VALUE

    /** `COUISearchHistoryView` 构造里给 FlowLayout 设的 `maxRowUnfolded`。 */
    const val searchHistoryMaxRowsUnfolded = 8

    /** 父级给出无界宽度时的排版宽度；原厂测量按父级宽度执行，不处理无界。 */
    val fallbackWidth = 360.dp

    /** `EXPAND_BUTTON_APPEAR_DURATION`：展开时箭头淡出。 */
    const val expandButtonAppearMillis = 250

    /** `HIDDEN_VIEWS_APPEAR_DELAY`：展开时隐藏项延迟出现。 */
    const val hiddenViewsAppearDelay = 100

    /** `HIDDEN_VIEWS_APPEAR_DURATION`：展开时隐藏项淡入。 */
    const val hiddenViewsAppearDuration = 400

    /** `HIDDEN_VIEWS_DISAPPEAR_DURATION`：折叠时隐藏项淡出。 */
    const val hiddenViewsDisappearDuration = 300

    /** `COUIMoveEaseInterpolator`：PathInterpolator(0.3, 0, 0.1, 1)。 */
    internal val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /**
     * 标签底色按 `?couiColorContainer8` 的口径从前景色派生，文字取 `?couiColorLabelPrimary`，
     * 展开控件取次要文字色；宿主可在 [CouiFlowLayoutColors] 里显式覆盖。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiFlowLayoutColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiFlowLayoutColors(
            chipContainer = scheme.onSurface.copy(alpha = .08f),
            chipContent = scheme.onSurface,
            control = scheme.onSurfaceVariant,
        )
    }
}

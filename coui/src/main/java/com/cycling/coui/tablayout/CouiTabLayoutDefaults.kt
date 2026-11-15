package com.cycling.coui.tablayout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.theme.CouiTheme

/** 页签条的一项；[key] 在重排时保持稳定，[enabled] 为 false 时不响应选择但保留占位。 */
@Immutable
data class CouiTabItem(val key: Any, val label: String, val enabled: Boolean = true)

/** 页签条的排布方式，对应 `COUITabLayout` 的 `MODE_FIXED` / `MODE_SCROLLABLE`。 */
enum class CouiTabLayoutMode {
    /** 按可用宽度分配间距，不滚动。 */
    Fixed,

    /** 每项按文字宽度加 [CouiTabLayoutDefaults.tabSpacing] 排布，可横向滚动，选中项滚向中间。 */
    Scrollable,
}

/** 页签条的文字、指示器与底部分隔线配色。 */
@Immutable
data class CouiTabLayoutColors(
    val selectedText: Color,
    val unselectedText: Color,
    val indicator: Color,
    val divider: Color,
)

/**
 * 页签条的固定参数，取自 `SettingsSmallTabLayoutStyle`（父 `COUISmallTabLayoutStyle`）、
 * `COUISmallTabViewStyle` / `COUIMiddleTabViewStyle` 与 `tablayout_*` / `coui_tab_layout_*` 资源。
 *
 * 高度与字号取页面实际使用的 Small / Middle / Largest 样式，而不是 `COUITabLayout` 未被样式覆盖时的
 * 基类默认值（48dp、14sp）。每项占「文字宽 + [tabSpacing]」，[tabSpacing] 一半落在文字两侧；
 * [edgeMargin] 是内容两侧的最小外边距，滚动模式的容器内边距按 `edgeMargin - tabSpacing / 2` 得出。
 * 固定模式在内容不超过 [contentMinWidth] 时把剩余宽度均分为「页签数 + 1」段间距。
 *
 * 指示器宽度跟随文字宽度（两侧各加 [indicatorPadding]），最小 [indicatorMinWidth]，
 * 高度 [indicatorHeight]，绘制在容器底部；切换动画时长为 `min(300, 相邻距离 × 50 + 150)` 毫秒，
 * 滚动到选中项用 300ms。
 */
object CouiTabLayoutDefaults {
    /** `tablayout_small_layout_height` / `tablayout_large_layout_height` 的页面实际高度。 */
    val height = 40.dp

    /** `tablayout_selected_indicator_height` / `couiTabIndicatorHeight`。 */
    val indicatorHeight = 2.dp

    /** `DEFAULT_MIN_INDICATOR`，`setIndicatorPosition` 的下限。 */
    val indicatorMinWidth = 32.dp

    /** `coui_tablayout_indicator_padding`。 */
    val indicatorPadding = 0.dp

    /** 底部 1px 分隔线，按 1dp 还原。 */
    val dividerHeight = 1.dp

    /** `COUISmallTabViewStyle` / `COUIMiddleTabViewStyle` → `tablayout_large_text_size`。 */
    val textSize = 16.sp

    /** `coui_tab_layout_small_tab_spacing`，相邻项间距，两侧各取一半作为文字留白。 */
    val tabSpacing = 20.dp

    /** `coui_tab_layout_small_horizontal_margin`，内容两侧的最小外边距。 */
    val edgeMargin = 16.dp

    /** `coui_tab_layout_content_min_width`，固定模式按剩余宽度均分间距的临界内容宽度。 */
    val contentMinWidth = 360.dp

    /** `couiTabMaxWidth` → `tablayout_large_tab_max_width`，单页签文字的最大宽度。 */
    val tabMaxWidth = 128.dp

    /** `ANIMATION_DURATION`，滚动到选中项的时长。 */
    const val scrollAnimationMillis = 300

    /** `getIndicatorAnimTime` 的基础时长与每项增量。 */
    const val indicatorBaseMillis = 150
    const val indicatorStepMillis = 50
    const val indicatorMaxMillis = 300

    internal fun indicatorDurationMillis(distance: Int): Int =
        (indicatorBaseMillis + distance * indicatorStepMillis).coerceAtMost(indicatorMaxMillis)

    /**
     * `Widget.COUI.TabLayout.Middle` 的 `couiTabIndicatorColor` 取 `?couiColorPrimaryNeutral`；
     * 选中文字与指示器同色，未选中文字与底部分隔线按次要前景色派生。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTabLayoutColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiTabLayoutColors(
            selectedText = scheme.onSurface,
            unselectedText = scheme.onSurfaceVariant,
            indicator = scheme.onSurface,
            divider = scheme.onSurface.copy(alpha = .08f),
        )
    }
}

package com.cycling.coui.tablayout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.segmentbutton.CouiSegmentedControlDefaults
import com.cycling.coui.theme.CouiTheme

/** 页签条的一项；[key] 在重排时保持稳定，[enabled] 为 false 时不响应选择但保留占位。 */
@Immutable
data class CouiTabItem(val key: Any, val label: String, val enabled: Boolean = true)

/** 页签条的排布方式：`Fixed` 在视口内收缩，`Scrollable` 保留自然宽度并可横向滚动。 */
enum class CouiTabLayoutMode {
    /** 内容超出可用宽度时按分段宽度分配到视口内，不滚动。 */
    Fixed,

    /** 保留每段自然宽度，可横向滚动，选中项滚向中间。 */
    Scrollable,
}

/** 页签条的容器、选中胶囊与文字配色。 */
@Immutable
data class CouiTabLayoutColors(
    val selectedText: Color,
    val unselectedText: Color,
    val indicator: Color,
    val container: Color,
)

/**
 * 页签条的固定参数，取自分段控件的 `coui_segment_*` 资源与 `tablayout_*` 文字样式。
 *
 * 容器是一条包裹内容的胶囊，高度 [height]，内容内缩 [containerPadding]，因此选中胶囊高
 * `height - 2 × containerPadding`。每段宽度为 `max(tabMinWidth, 文字宽 + 2 × labelPadding)`，
 * 宽度不足时按原厂分段控件的水位算法收缩。
 *
 * 切换动画时长为 `min(300, 相邻距离 × 50 + 150)` 毫秒，滚动到选中项用 300ms。
 */
object CouiTabLayoutDefaults {
    /** 页签条与分段控件一致的高度。 */
    val height = 40.dp

    /** `tablayout_large_text_size`。 */
    val textSize = 16.sp

    /** `couiTabMaxWidth` → `tablayout_large_tab_max_width`，单页签文字的最大宽度。 */
    val tabMaxWidth = 128.dp

    /** `coui_segment_padding`，容器与选中胶囊之间的留白。 */
    val containerPadding = 4.dp

    /** `coui_segment_min_width`，单段的最小宽度。 */
    val tabMinWidth = 52.dp

    /** `coui_segment_btn_padding_horizontal`，文字两侧的留白。 */
    val labelPadding = 12.dp

    /** `ANIMATION_DURATION`，滚动到选中项的时长。 */
    const val scrollAnimationMillis = 300

    /** `getIndicatorAnimTime` 的基础时长与每项增量。 */
    const val indicatorBaseMillis = 150
    const val indicatorStepMillis = 50
    const val indicatorMaxMillis = 300

    internal fun indicatorDurationMillis(distance: Int): Int =
        (indicatorBaseMillis + distance * indicatorStepMillis).coerceAtMost(indicatorMaxMillis)

    /**
     * 容器与选中胶囊直接取分段控件的原厂颜色，两处控件外观保持一致；
     * 选中与未选中文字沿用页签条的前景色与次要前景色。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTabLayoutColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiTabLayoutColors(
            selectedText = scheme.onSurface,
            unselectedText = scheme.onSurfaceVariant,
            indicator = CouiSegmentedControlDefaults.indicatorColor(dark),
            container = CouiSegmentedControlDefaults.containerColor(dark),
        )
    }
}

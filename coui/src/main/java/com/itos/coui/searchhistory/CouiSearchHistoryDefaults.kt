package com.itos.coui.searchhistory

import androidx.compose.ui.unit.dp

/**
 * 搜索历史的固定参数，取自 `COUISearchHistoryView` 与 `coui_component_search_history_*`。
 *
 * 标题栏上下各留 7dp，删除图标 18dp；历史区与标题间隔 8dp，整块内留白为
 * 起始/结束 16dp、顶部 16dp、底部 32dp。
 */
object CouiSearchHistoryDefaults {
    /** `coui_component_search_history_padding_start`。 */
    val paddingStart = 16.dp

    /** `coui_component_search_history_padding_end`。 */
    val paddingEnd = 16.dp

    /** `coui_component_search_history_padding_top`。 */
    val paddingTop = 16.dp

    /** `coui_component_search_history_padding_bottom`。 */
    val paddingBottom = 32.dp

    /** `coui_component_search_history_title_margin_vertical`，标题栏上下各一份。 */
    val titleMarginVertical = 7.dp

    /** `coui_component_search_history_delete_icon_size`。 */
    val deleteIconSize = 18.dp

    /** 删除图标的命中区；原厂 `coui_component_icon_bg` 提供更大的点击背景。 */
    val deleteTouchTargetSize = 48.dp

    /** `coui_component_search_history_flow_margin_top`。 */
    val flowMarginTop = 8.dp

    /** `SEARCH_VIEW_DISAPPEAR_DURATION`：删除时整块 300ms 淡出。 */
    const val deleteDurationMillis = 300
}

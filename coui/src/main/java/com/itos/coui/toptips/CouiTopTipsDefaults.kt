package com.itos.coui.toptips

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 顶部提示条的固定参数。
 *
 * 取自 ColorOS 17 的 `coui_toptips_*` 与 `coui_round_corner_*` 资源，
 * 对应原厂 `com.coui.appcompat.tips.COUICustomTopTips`：
 * - 圆角取 `coui_round_corner_m`（12dp），即原厂 `couiRoundCornerMRadius` / `couiRoundCornerM`；
 * - 底色取 `couiColorFillThin`，日间 `coui_color_fill_thin`（`#0A000000`）、
 *   夜间 `coui_color_fill_thin_dark`（`#14FFFFFF`）；
 * - 显示与消失动画各 250ms（原厂 `CLIP_ANIMATION_DURATION`），只改 `scaleY`，不改 alpha。
 */
object CouiTopTipsDefaults {
    /** 显示与消失动画的时长，对应原厂 `CLIP_ANIMATION_DURATION`。 */
    const val animationMillis = 250

    /** 内容区上下留白，对应 `coui_toptips_view_content_top_margin`。 */
    val contentVerticalPadding = 12.dp

    /** 内容区最小高度，对应 `coui_toptips_view_content_min_height`。 */
    val contentMinHeight = 20.dp

    /** 图标槽边长，对应 `coui_toptips_view_icon_btn_size`。 */
    val iconSize = 24.dp

    /** 图标到容器起点的留白，对应 `coui_toptips_view_icon_start_margin`。 */
    val iconStartMargin = 16.dp

    /** 内容到图标起点的留白，对应 `coui_toptips_view_content_start_margin`。 */
    val contentStartMargin = 8.dp

    /** 尾部按钮到容器终点的留白，对应 `coui_toptips_view_btn_margin`。 */
    val actionEndMargin = 16.dp

    /**
     * 底色。
     *
     * 原厂读取主题属性 `couiColorFillThin`，日间与夜间各一套半透明填充色。
     */
    fun containerColor(dark: Boolean): Color = if (dark) Color(0x14FFFFFF) else Color(0x0A000000)
}

package com.cycling.coui.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 折线图的固定参数，取自 ColorOS 17 手机管家「网络安全检测」页的
 * `com.oplus.phonemanager.networkdetect.view.NetworkSlidingWindowLineView`：
 * 窗口 50 点、线宽 1dp 圆头、上下各留 0.1 高、值域下限 0.1、入场 300ms 线性。
 */
object CouiLineChartDefaults {
    /** `DEFAULT_MAX_DATA_POINTS = 50`。窗口已满后新的点从左侧挤出。 */
    const val windowSize = 50

    /** 线宽 `dip2px(1.0f)`。 */
    val strokeWidth = 1.dp

    /**
     * 默认高度。原厂 `NetworkSlidingWindowLineView` 没有内建高度，由布局决定；
     * 这里取与 [CouiChartDefaults.height] 一致的 160dp，宿主可通过 `height` 覆盖。
     */
    val height = 160.dp

    /** `paddingTopRatio` / `paddingBottomRatio`，上下各占高度的 0.1。 */
    const val topPaddingRatio = .1f
    const val bottomPaddingRatio = .1f

    /**
     * 值域下限。原厂 `MIN_VALUE_RANGE = 0.1`：数据最大值与最小值之差小于该值时
     * 改为「中值 ± 0.5」，避免近乎水平的数据被放大成剧烈抖动。
     */
    const val minValueRange = .1f

    /** 归一化分母的下限，对应原厂 `LEAST_NORMALIZED_VALUE = 0.01`。 */
    const val leastNormalizedRange = .01f

    /** 单点入场动画时长，`ENTRY_ANIMATION_DURATION_MS = 300`，线性。 */
    const val entryDurationMillis = 300

    /** `lineColor = Color.parseColor("#00BD13")`。 */
    val lineColor = Color(0xFF00BD13)

    /**
     * 面积渐变顶部的透明度。原厂 `shadowStartColor = argb(51, 0, 189, 19)`，51/255 = 0.2；
     * 底部 `argb(0, …)` 完全透明。
     */
    const val areaTopAlpha = .2f

    /** 面积渐变底部透明度，对应 `shadowEndColor` 的 alpha 0。 */
    const val areaBottomAlpha = 0f
}

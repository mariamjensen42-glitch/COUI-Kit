package com.cycling.coui.stepper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/** 环形步进徽标的取色：轨道、进度弧与中间数字。 */
@Immutable
data class CouiStepRingColors(val track: Color, val progress: Color, val text: Color)

/**
 * 环形步进徽标的固定参数，取自 ColorOS 17 手机管家「应用内清理」页的
 * `com.oplus.phonemanager.clear.inappclear.InAppClearStepIndicatorView`。
 */
object CouiStepRingDefaults {
    /** `clear_in_app_clear_step_indicator_size`。 */
    val size = 24.dp

    /**
     * `clear_in_app_clear_step_indicator_stroke` 的 1.6299744dp。资源值来自资源缩放，
     * 源码里的兜底常量是 `FALLBACK_STROKE_DP = 1.63f`，这里取整到 1.63dp。
     */
    val strokeWidth = 1.63.dp

    /** 设计基准尺寸：文字按「实际尺寸 / 该值」等比缩放，对应 `onSizeChanged` 的换算。 */
    val designSize = 24.dp

    /** `clear_in_app_clear_step_indicator_text_size`，在 24dp 徽标上取 18dp。 */
    val designTextSize = 18.dp

    /** 轨道色 `?couiColorContainer8`（日 #14000000 / 夜 #1AFFFFFF）。 */
    private val trackLight = Color(0x14000000)
    private val trackDark = Color(0x1AFFFFFF)

    /** 进度弧与数字都取 `?couiColorPrimary`；未选中时为容器色。 */
    @Composable
    fun colors(dark: Boolean): CouiStepRingColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiStepRingColors(
            track = if (dark) trackDark else trackLight,
            progress = scheme.accent,
            text = scheme.accent,
        )
    }
}

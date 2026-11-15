package com.cycling.coui.dialog

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.cycling.coui.shape.CouiShape
import kotlin.math.PI

/**
 * 引导弹窗尺寸与动画参数。
 *
 * 取自原厂 `coui_guide_page_item.xml` 与 `COUIGuideLayoutContentView`：
 * 图片默认 328dp、紧凑样式 240dp，指示器下留 20dp，按钮区左右各 16dp 且底部留 8dp。
 * 根节点裁剪半径取 `coui_dialog_guide_clip_radius_root` 32dp，比普通弹窗的 45dp 小。
 */
object CouiGuideDialogDefaults {
    val cornerRadius = 32.dp
    val shape: Shape = CouiShape(32.dp, 3f)
    val imageHeight = 328.dp
    val compactImageHeight = 240.dp
    val imageBottomPadding = 16.dp
    val titleTopPadding = 24.dp
    val titleBottomPadding = 6.dp
    val textHorizontalPadding = 24.dp
    val pagerBottomMargin = 16.dp
    val singlePageBottomMargin = 20.dp
    val indicatorBottomMargin = 20.dp
    val buttonHorizontalPadding = 16.dp
    val buttonSpacing = 8.dp
    val buttonBottomMargin = 8.dp

    /** 程序化翻页时长；原厂使用弹簧插值器跑满该时长。 */
    const val scrollDurationMillis = 650

    /** 按钮宽度弹簧与次级按钮淡出终点。 */
    const val buttonSpringResponse = 0.15f
    const val secondaryFadeEndProgress = 0.6f

    val scrollEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /** 原厂 COUIMoveEaseInterpolator：PathInterpolator(0.3, 0, 0.1, 1)。 */
    internal val moveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /** 原厂把距末页进度先经 ease 插值再交给弹簧，避免直接跟随滚动位置。 */
    internal fun ease(fraction: Float): Float = moveEase.transform(fraction)

    internal fun stiffness(response: Float): Float {
        val period = 2f * PI.toFloat() / response
        return period * period
    }
}

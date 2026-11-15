package com.itos.coui.progress

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.itos.coui.shape.CouiShapes

/** 进度与占位的固定参数；环形尺寸取自 ColorOS 17 手机管家内嵌的 COUI 资源。 */
object CouiProgressDefaults {
    /** `Widget.COUI.COUICircularProgressBar` 的 large_size：40dp、5dp 描边。 */
    val circularSize = 40.dp
    val circularStrokeWidth = 5.dp

    /** 同一资源的 medium 档：30dp、3dp 描边。 */
    val circularMediumSize = 30.dp
    val circularMediumStrokeWidth = 3.dp

    /** `Widget.COUI.COUICircleProgressBar` 的小号加载环：18dp。 */
    val loadingSize = 18.dp

    /**
     * 不确定态的固定弧长（角度）。原厂环形进度只有确定态，弧长按同一套加载字形推演：
     * `drawable/coui_icon_loading.xml` 的路径从右侧顺时针画到下方，覆盖 270°、缺口 90°，
     * 这里沿用同一个弧长与缺口方向。
     */
    const val indeterminateSweep = 270f

    /**
     * 不确定态一圈的时间。取原厂 COUISwitch 加载分支的旋转资源：
     * 360° / 800ms、LinearInterpolator，见 `CouiSwitchLoadingMotion`。
     */
    const val indeterminatePeriodMillis = 800L

    /**
     * 线性进度的高度。手机管家电量条与存储占用条按截图比例取 8dp，
     * 页面上的大号电量条放大到 12dp；两者共用同一形状与端部圆角。
     * 该值尚未与原厂资源核对，见 `2026-11-13-linear-progress-and-quick-actions`。
     */
    val linearHeight = 8.dp
    val linearLargeHeight = 12.dp

    /** 线性进度沿用库内胶囊族形状，轨道与进度共用同一轮廓。原生基线；组件默认读取 `CouiTheme.shapes.capsule`。 */
    val linearShape: Shape = CouiShapes.Default.capsule

    /**
     * 环形进度的缺口上限。原厂环形进度没有缺口分支，这里限制在 350° 以内，
     * 保证轨道至少保留一段可见弧长，避免参数退化为空环。
     */
    const val maxCircularGapDegrees = 350f

    /** 开启外发光时环半径的内收比例，为光晕留出绘制空间。 */
    const val circularGlowRingScale = .82f

    /** 外发光的叠层数量；每层加宽并按序号衰减透明度，形成柔和光晕。 */
    const val circularGlowLayers = 3

    /** 单层光晕相对描边的额外宽度与基础透明度。 */
    const val circularGlowSpread = .8f
    const val circularGlowAlpha = .22f

    /** 原生基线；组件默认读取 `CouiTheme.shapes.small`。 */
    val skeletonShape: Shape = CouiShapes.Default.small
    val skeletonMinHeight = 16.dp

    /** 占位底色相对内容色的透明度，动画在此基础上按 [skeletonMinFactor] 到 1 波动。 */
    const val skeletonAlpha = 0.1f
    const val skeletonMinFactor = 0.5f
    const val skeletonPeriodMillis = 1400L
}

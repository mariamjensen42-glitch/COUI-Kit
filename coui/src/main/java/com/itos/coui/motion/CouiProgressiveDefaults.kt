package com.itos.coui.motion

import androidx.compose.runtime.Immutable
import kotlin.math.abs

/**
 * 渐进式手势动画的阶段，对齐原厂 `AnimationPhase`。
 *
 * [Drag] 与 [Release] 是常规拖动与回落；[RubberbandDrag] 与 [RubberbandRelease] 是越过边界后的
 * 阻尼拖拽与回弹，两者的目标位移都换成橡皮筋位移而不是零。
 */
enum class CouiProgressivePhase {
    Idle,
    Drag,
    Release,
    RubberbandDrag,
    RubberbandRelease;

    val isRelease: Boolean get() = this == Release || this == RubberbandRelease

    val isRubberband: Boolean get() = this == RubberbandDrag || this == RubberbandRelease
}

/** 一条渐进式弹簧通道的插值参数。[blendMillis] 是原厂在运行中换参数时的混合时长。 */
@Immutable
data class CouiProgressiveSpring(
    val response: Float,
    val bounce: Float,
    val blendMillis: Long,
)

/**
 * 渐进式拖动的弹簧配置，字段与默认值对齐原厂 `HorizontalProgressiveConfig`。
 *
 * 响应从 [dragBaseResponse] / [releaseResponse] 起，按与触发行的距离每个单位增加一步，
 * 并用 [maxDragResponse] / [maxReleaseResponse] 封顶；回落时速度越快响应越短、回弹越大。
 */
@Immutable
data class CouiProgressiveConfig(
    val dragBaseResponse: Float = 0.15f,
    val dragBounce: Float = 0.25f,
    val dragResponseStep: Float = 0.04f,
    val maxDragResponse: Float = 5.85f,
    val releaseResponse: Float = 0.5f,
    val releaseBounce: Float = 0.2f,
    val releaseResponseStep: Float = 0.04f,
    val maxReleaseResponse: Float = Float.MAX_VALUE,
    val maxReleaseVelocityPxPerSecond: Float = 12000f,
    val releaseResponseVelocityDelta: Float = 0.1f,
    val releaseBounceVelocityDelta: Float = 0.25f,
    val dragBlendDuration: Float = 0.25f,
    val releaseBlendDuration: Float = 0.25f,
) {
    /** 拖动阶段的弹簧参数；[distance] 是当前行与触发行的行距，[span] 是参与动画的最大行距。 */
    fun dragSpring(distance: Int, span: Int): CouiProgressiveSpring = CouiProgressiveSpring(
        response = dragBaseResponse + distance.coerceAtLeast(0) * step(
            base = dragBaseResponse,
            step = dragResponseStep,
            max = maxDragResponse,
            span = span,
        ),
        bounce = dragBounce,
        blendMillis = dragBlendMillis,
    )

    /** 回落阶段的弹簧参数；[velocityPxPerSecond] 参与缩短响应与加回弹。 */
    fun releaseSpring(
        distance: Int,
        span: Int,
        velocityPxPerSecond: Float = 0f,
    ): CouiProgressiveSpring {
        val topVelocity = maxReleaseVelocityPxPerSecond.coerceAtLeast(1f)
        val velocityRatio = (abs(velocityPxPerSecond).coerceAtMost(topVelocity) / topVelocity).coerceIn(0f, 1f)
        val response = (releaseResponse - releaseResponseVelocityDelta.coerceAtLeast(0f) * velocityRatio)
            .coerceAtLeast(0.01f)
        val bounce = releaseBounce + releaseBounceVelocityDelta.coerceAtLeast(0f) * velocityRatio
        return CouiProgressiveSpring(
            response = response + distance.coerceAtLeast(0) * step(
                base = releaseResponse,
                step = releaseResponseStep,
                max = maxReleaseResponse,
                span = span,
            ),
            bounce = bounce,
            blendMillis = releaseBlendMillis,
        )
    }

    val dragBlendMillis: Long get() = (dragBlendDuration * 1000f).toLong().coerceAtLeast(0L)

    val releaseBlendMillis: Long get() = (releaseBlendDuration * 1000f).toLong().coerceAtLeast(0L)

    /** 单行步长；整段行程超过上限时按 [span] 均分剩余额度。 */
    private fun step(base: Float, step: Float, max: Float, span: Int): Float {
        val raw = step.coerceAtLeast(0f)
        if (span <= 0) return raw
        val reach = span * raw + base
        return if (reach > max) (max - base).coerceAtLeast(0f) / span else raw
    }
}

/** 橡皮筋参数，对齐原厂 `RubberbandConfig`；[maxDistancePx] 为非正数时按视口的一半计算。 */
@Immutable
data class CouiRubberbandConfig(
    val curveRatio: Float = 0.55f,
    val maxDistancePx: Float = -1f,
)

object CouiProgressiveDefaults {
    val config: CouiProgressiveConfig = CouiProgressiveConfig()

    val rubberband: CouiRubberbandConfig = CouiRubberbandConfig()

    /** 原厂 `SingleProperty.getMinChange`，低于该位移不再驱动帧。 */
    const val minimumVisibleChange: Float = 0.5f

    /**
     * 越界拖拽换算成橡皮筋位移：`(1 - 1 / (curveRatio * over / maxDistance + 1)) * maxDistance`。
     *
     * [overDragPx] 是越过边界的正值，[viewportPx] 用于 [CouiRubberbandConfig.maxDistancePx] 非正数时取一半。
     */
    fun rubberbandOffset(
        overDragPx: Float,
        viewportPx: Float,
        config: CouiRubberbandConfig = rubberband,
    ): Float {
        if (overDragPx <= 0f) return 0f
        val maxDistance = if (config.maxDistancePx > 0f) config.maxDistancePx else viewportPx / 2f
        if (maxDistance <= 0f) return 0f
        return (1f - 1f / (config.curveRatio * overDragPx / maxDistance + 1f)) * maxDistance
    }
}

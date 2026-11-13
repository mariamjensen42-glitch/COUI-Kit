package com.itos.coui.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos

/**
 * 按帧推进的 0..1 锯齿相位，供不确定态旋转和占位脉冲共用。
 * 只在 active 时运行；节点离开组合后协程随 LaunchedEffect 取消，不残留计时。
 * 相位由绘制阶段读取，不产生逐帧重组。
 */
@Composable
internal fun rememberCouiPhase(active: Boolean, periodMillis: Long): State<Float> {
    val phase = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(active, periodMillis) {
        if (!active) return@LaunchedEffect
        var origin = 0L
        while (true) {
            withFrameNanos { now ->
                if (origin == 0L) origin = now
                phase.floatValue = ((now - origin) % periodMillis).toFloat() / periodMillis.toFloat()
            }
        }
    }
    return phase
}

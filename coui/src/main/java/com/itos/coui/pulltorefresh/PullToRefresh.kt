package com.itos.coui.pulltorefresh

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.itos.coui.progress.CircularProgress
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

/** 下拉刷新的触发距离、指示器尺寸与最大拉伸比例。 */
object CouiPullToRefreshDefaults {
    val threshold = 72.dp
    val maxPullFactor = 1.6f
    val indicatorSize = 28.dp
    val indicatorStroke = 3.dp
}

/**
 * 下拉刷新容器：在内容到顶后继续下拉时累积位移，松手超过阈值触发 [onRefresh]。
 *
 * 位移只由本组件持有，业务状态由 [refreshing] 与 [onRefresh] 控制。内容通过 `graphicsLayer`
 * 整体下移，不改变布局尺寸，因此内部列表的滚动位置和 `Scaffold` 内边距都不受影响。
 *
 * [indicator] 收到 0..1 的下拉进度与刷新态，默认是环形进度：下拉时按进度填充，刷新时转为不确定态旋转。
 * 内容必须是可滚动容器，否则没有 overscroll 事件，下拉不会触发。
 */
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
    indicator: @Composable (progress: Float, refreshing: Boolean) -> Unit = { progress, active ->
        CircularProgress(progress = if (active) null else progress, dark = dark,
            strokeWidth = CouiPullToRefreshDefaults.indicatorStroke,
            size = CouiPullToRefreshDefaults.indicatorSize)
    },
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { CouiPullToRefreshDefaults.threshold.toPx() }
    val maxPull = thresholdPx * CouiPullToRefreshDefaults.maxPullFactor
    var offset by remember { mutableFloatStateOf(0f) }
    val connection = remember(thresholdPx, refreshing) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // consumed 为 0 且仍向下：列表已经到顶，剩余位移转成下拉。
                if (refreshing || available.y <= 0f || consumed.y != 0f) return Offset.Zero
                val next = (offset + available.y * 0.5f).coerceIn(0f, maxPull)
                if (next <= offset) return Offset.Zero
                offset = next
                return Offset(0f, available.y)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!refreshing && offset >= thresholdPx) onRefresh()
                return Velocity.Zero
            }
        }
    }
    // 刷新态把位移固定到阈值，结束后回弹；两者都由这里统一收尾，避免松手动画与状态动画互相覆盖。
    LaunchedEffect(refreshing) {
        val target = if (refreshing) thresholdPx else 0f
        if (offset != target) animate(offset, target) { value, _ -> offset = value }
    }
    Box(modifier.fillMaxSize().nestedScroll(connection)) {
        Box(Modifier.align(Alignment.TopCenter).fillMaxWidth()
            .height(CouiPullToRefreshDefaults.threshold)
            .offset { IntOffset(0, (offset - thresholdPx).roundToInt()) },
            contentAlignment = Alignment.Center) {
            indicator((offset / thresholdPx).coerceIn(0f, 1f), refreshing)
        }
        Box(Modifier.fillMaxSize().graphicsLayer { translationY = offset }) { content() }
    }
}

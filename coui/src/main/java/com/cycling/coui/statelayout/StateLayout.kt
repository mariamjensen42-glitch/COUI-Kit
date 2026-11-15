package com.cycling.coui.statelayout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.cycling.coui.emptystate.CouiEmptyPageSize
import com.cycling.coui.emptystate.CouiEmptyStateDefaults
import com.cycling.coui.emptystate.LocalCouiEmptyPageSize
import com.cycling.coui.progress.CircularProgress
import kotlin.math.roundToInt

/** 页面的四种内容状态：加载中、就绪、空、失败。 */
@Immutable
sealed interface CouiLoadState {
    object Loading : CouiLoadState
    object Ready : CouiLoadState
    object Empty : CouiLoadState

    /** 失败可携带宿主给的说明，`null` 表示只显示失败槽本身。 */
    data class Failed(val message: String? = null) : CouiLoadState
}

/**
 * 按 [state] 在加载、内容、空与失败之间切换的统一容器。
 *
 * 只做状态分发与占位排版，不内置文案、不发起请求、不缓存上一次的内容：文案与重试入口由宿主在
 * [empty] / [failed] 里给出，`EmptyState`、`Skeleton` 都可以直接放进去。就绪态的内容按整块区域布局，
 * 加载态居中，空态与失败态按原厂 `COUIEmptyStateView` 的整页规则摆放。
 *
 * 空态与失败态的整页规则（原厂 `COUIEmptyStateView` 的 `onMeasure` / `onLayout`）：
 * - 尺寸类型由可用尺寸判定，[emptyPageSize] 可显式指定；判定结果通过 `LocalCouiEmptyPageSize`
 *   传给槽内的 `EmptyState`，[CouiEmptyPageSize.Small] 时原厂把插图区缩到 0 × 0，`EmptyState` 会跳过图标槽。
 * - 槽内容的顶部留白 = (容器高 - 槽高) × 0.45；[CouiEmptyPageSize.Small] 时为 0.5。
 * - 容器高由外部固定（如 `fillMaxSize`）时用容器高；未固定时容器收拢到槽内容高度，顶部留白为 0。
 *
 * 切换状态时内容会离开组合，列表滚动位置由宿主自行保存（例如用 `rememberSaveable` 的 `LazyListState`）。
 */
@Composable
fun StateLayout(
    modifier: Modifier = Modifier,
    state: CouiLoadState,
    empty: @Composable () -> Unit,
    failed: @Composable (message: String?) -> Unit,
    loading: @Composable () -> Unit = { CircularProgress() },
    emptyPageSize: CouiEmptyPageSize? = null,
    content: @Composable () -> Unit,
) {
    when (state) {
        CouiLoadState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { loading() }
        CouiLoadState.Ready -> Box(modifier.fillMaxSize()) { content() }
        CouiLoadState.Empty -> EmptyPageSlot(modifier, emptyPageSize) { empty() }
        is CouiLoadState.Failed -> EmptyPageSlot(modifier, emptyPageSize) { failed(state.message) }
    }
}

/**
 * 空态与失败态的整页摆位，复刻原厂 `COUIEmptyStateView`：
 * 先按可用尺寸判定尺寸类型并下发，再把槽内容水平居中、顶部留白按剩余高度乘以高度比例摆放。
 */
@Composable
private fun EmptyPageSlot(
    modifier: Modifier,
    requestedPageSize: CouiEmptyPageSize?,
    slot: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else Constraints.Infinity
        val maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else Constraints.Infinity
        val pageSize = requestedPageSize ?: resolvePageSize(maxWidth, maxHeight)

        val placeable = subcompose(Unit) {
            CompositionLocalProvider(LocalCouiEmptyPageSize provides pageSize) { slot() }
        }.first().measure(Constraints(maxWidth = maxWidth, maxHeight = maxHeight))

        // 原厂 onMeasure：高度不是 EXACTLY 时把自身高度收拢到内容组高度；否则用测量到的高度。
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else placeable.width
        val height = if (constraints.hasFixedHeight) constraints.maxHeight else placeable.height
        val placedTop = if (constraints.hasFixedHeight) {
            val ratio = if (pageSize == CouiEmptyPageSize.Small) {
                CouiEmptyStateDefaults.pageMarginTopRatioSmall
            } else {
                CouiEmptyStateDefaults.pageMarginTopRatio
            }
            ((height - placeable.height) * ratio).roundToInt().coerceAtLeast(0)
        } else {
            0
        }

        layout(width, height) {
            placeable.place(
                x = ((width - placeable.width) / 2).coerceAtLeast(0),
                y = placedTop,
            )
        }
    }
}

/**
 * 原厂 `getEmptyStateGroupSizeType`：高度小于 small 阈值即 [CouiEmptyPageSize.Small]，
 * 宽度或高度小于 medium 阈值为 [CouiEmptyPageSize.Medium]，其余为 [CouiEmptyPageSize.Large]。
 * 高度无界时按原厂 UNSPECIFIED 的 0 处理。
 */
private fun Density.resolvePageSize(width: Int, height: Int): CouiEmptyPageSize {
    val safeWidth = if (width == Constraints.Infinity) 0 else width
    val safeHeight = if (height == Constraints.Infinity) 0 else height
    return when {
        safeHeight < CouiEmptyStateDefaults.pageHeightThresholdSmall.roundToPx() -> CouiEmptyPageSize.Small
        safeWidth < CouiEmptyStateDefaults.pageWidthThresholdMedium.roundToPx() ||
            safeHeight < CouiEmptyStateDefaults.pageHeightThresholdMedium.roundToPx() ->
            CouiEmptyPageSize.Medium
        else -> CouiEmptyPageSize.Large
    }
}

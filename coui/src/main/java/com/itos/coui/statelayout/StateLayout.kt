package com.itos.coui.statelayout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itos.coui.progress.CircularProgress

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
 * 其余状态居中，因此列表滚动位置不会因为切换状态被重置（内容被移出组合时由宿主自行保存）。
 */
@Composable
fun StateLayout(
    modifier: Modifier = Modifier,
    state: CouiLoadState,
    empty: @Composable () -> Unit,
    failed: @Composable (message: String?) -> Unit,
    loading: @Composable () -> Unit = { CircularProgress() },
    content: @Composable () -> Unit,
) {
    when (state) {
        CouiLoadState.Loading -> StateSlot(modifier) { loading() }
        CouiLoadState.Ready -> Box(modifier.fillMaxSize()) { content() }
        CouiLoadState.Empty -> StateSlot(modifier) { empty() }
        is CouiLoadState.Failed -> StateSlot(modifier) { failed(state.message) }
    }
}

@Composable
private fun StateSlot(modifier: Modifier, content: @Composable () -> Unit) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

package com.cycling.coui.banner

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.pageindicator.CouiPageIndicatorColors
import com.cycling.coui.pageindicator.PageIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 轮播的固定参数，全部取自原厂 `COUIBanner` 的默认值与 `coui_banner_content_layout`。 */
object CouiBannerDefaults {
    /**
     * `COUIBannerUtil`（`b.f6247a`）的 `dpToPx(24)`：`couiPageMargin` 的默认值。
     * 在原厂它既做 pager 的左右留白，也做页与页之间的间隙；这里同样一处取值。
     */
    val pageMargin = 24.dp

    /** `coui_banner_content_layout` 中指示器的 `layout_marginBottom`。 */
    val indicatorBottomMargin = 10.dp

    /** `couiLoopDuration` 默认 5（秒）：两次自动翻页之间的等待时长。 */
    const val loopDurationMillis = 5_000

    /** `COUIBanner.q` 默认 950ms：自动翻页的滚动时长，也加在循环等待之后。 */
    const val scrollDurationMillis = 950

    /** 原厂 `PathInterpolator(0.2f, 0f, 0.1f, 1f)`：先快后慢的翻页曲线。 */
    val easing: Easing = CubicBezierEasing(0.2f, 0f, 0.1f, 1f)

    /** `COUIBanner.f6235h` 默认 1：补页方案下第一张真实页的下标。 */
    const val startPosition = 1

    /** `COUIBanner.f6241n` 默认 true：首尾各补一页，形成无限循环。 */
    const val infiniteLoop = true

    /**
     * 指示器配色取自布局里的 `dotColor` / `traceDotColor`：`coui_page_indicator_dot_color_in_dark_bg`
     * #4DFFFFFF 与 `coui_color_label_primary_dark` #E6FFFFFF。图片上的指示器固定用深底配色，不跟随主题。
     */
    val indicatorColors = CouiPageIndicatorColors(dot = Color(0x4DFFFFFF), selectedDot = Color(0xE6FFFFFF))
}

/**
 * 轮播状态，宿主用它读写当前页。下标一律是真实页下标（从 0 开始），不含原厂首尾补的两页。
 *
 * 原厂 `COUIBanner` 在首尾各补一页并让 pager 停在补页上时无动画跳回真实页，形成无限循环；
 * [currentIndex] 已把补页换算回真实页，滑到补页的那一帧不会读到越界值。
 */
@Stable
class BannerState internal constructor(
    internal val pagerState: PagerState,
    /** 真实页数（不含补页）。 */
    internal val count: Int,
    /** 是否真的补了页：`count <= 1` 时不补，循环与自动轮播都没有意义。 */
    internal val looping: Boolean,
) {
    /** 当前真实页下标。 */
    val currentIndex: Int
        get() = realIndex(pagerState.currentPage)

    /** pager 下标 → 真实页下标；补页按首尾夹紧到 `0..count - 1`。 */
    internal fun realIndex(page: Int): Int {
        val real = if (looping) page - CouiBannerDefaults.startPosition else page
        return real.coerceIn(0, (count - 1).coerceAtLeast(0))
    }

    /** 真实页下标 → pager 下标；无穷循环时整体后移补页。 */
    internal fun pageIndex(index: Int): Int {
        val clamped = index.coerceIn(0, (count - 1).coerceAtLeast(0))
        return if (looping) clamped + CouiBannerDefaults.startPosition else clamped
    }

    /** 带滚动动画跳到 [index]（真实页下标）。 */
    suspend fun animateScrollToIndex(
        index: Int,
        animationSpec: AnimationSpec<Float> = tween(
            CouiBannerDefaults.scrollDurationMillis,
            easing = CouiBannerDefaults.easing,
        ),
    ) {
        if (count <= 0) return
        pagerState.animateScrollToPage(pageIndex(index), animationSpec = animationSpec)
    }

    /** 无动画跳到 [index]（真实页下标）。 */
    suspend fun scrollToIndex(index: Int) {
        if (count <= 0) return
        pagerState.scrollToPage(pageIndex(index))
    }
}

/** 记住一个轮播状态；[count] 或 [infiniteLoop] 变化时重建（回到 [startIndex]），避免补页数与页数对不上。 */
@Composable
fun rememberBannerState(
    count: Int,
    startIndex: Int = 0,
    infiniteLoop: Boolean = CouiBannerDefaults.infiniteLoop,
): BannerState = key(count.coerceAtLeast(0), infiniteLoop) {
    val realCount = count.coerceAtLeast(0)
    val looping = infiniteLoop && realCount > 1
    val start = startIndex.coerceIn(0, (realCount - 1).coerceAtLeast(0))
    // 原厂 adapter 数量是 realCount + 2：首页前面补最后一页、末页后面补第一页。
    val pagerState = rememberPagerState(if (looping) start + CouiBannerDefaults.startPosition else start) {
        if (looping) realCount + 2 else realCount
    }
    remember(pagerState, realCount, looping) { BannerState(pagerState, realCount, looping) }
}

/**
 * 轮播，一屏一张卡片、两侧露出相邻卡片的边，底部居中显示页码圆点。
 *
 * 原厂 `COUIBanner` 的 `type = 0`（手机形态）在这里对应 pager + 指示器；`type = 1`（平板双列列表）
 * 未移植，宿主在宽屏下自行选择版式。`@param count` 为真实页数，`pageContent` 收到的下标从 0 开始。
 */
@Composable
fun Banner(
    count: Int,
    modifier: Modifier = Modifier,
    state: BannerState = rememberBannerState(count),
    autoLoop: Boolean = true,
    loopDurationMillis: Int = CouiBannerDefaults.loopDurationMillis,
    scrollDurationMillis: Int = CouiBannerDefaults.scrollDurationMillis,
    pageMargin: Dp = CouiBannerDefaults.pageMargin,
    startItemWidth: Dp = 0.dp,
    endItemWidth: Dp = 0.dp,
    indicator: Boolean = count > 1,
    indicatorColors: CouiPageIndicatorColors = CouiBannerDefaults.indicatorColors,
    onPageChange: ((Int) -> Unit)? = null,
    pageContent: @Composable (index: Int) -> Unit,
) {
    if (count <= 0) return
    val pagerState = state.pagerState
    val scope = rememberCoroutineScope()
    val spec = tween<Float>(scrollDurationMillis, easing = CouiBannerDefaults.easing)

    // 原厂 COUIBannerOnPageChangeCallback：滑到首尾补页后（滚动停止时）无动画跳回真实页。
    LaunchedEffect(pagerState, state.looping, count) {
        if (!state.looping) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }.collect { page ->
            when (page) {
                0 -> pagerState.scrollToPage(count)
                count + 1 -> pagerState.scrollToPage(CouiBannerDefaults.startPosition)
            }
        }
    }

    val dragging by pagerState.interactionSource.collectIsDraggedAsState()
    val current = state.realIndex(pagerState.currentPage)
    LaunchedEffect(current) { onPageChange?.invoke(current) }

    // 原厂 dispatchTouchEvent：按下移除循环任务，抬起或取消后重新计时；
    // 这里用「是否正在被拖动」判断，自动翻页自身造成的滚动不会打断计时。
    LaunchedEffect(pagerState, autoLoop, state.looping, count, dragging, loopDurationMillis, scrollDurationMillis) {
        if (!autoLoop || !state.looping || count <= 1 || dragging) return@LaunchedEffect
        while (true) {
            delay(loopDurationMillis.toLong() + scrollDurationMillis)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1, animationSpec = spec)
            }
        }
    }

    Box(modifier) {
        HorizontalPager(
            state = pagerState,
            // 原厂 pager 的 padding = 露边宽度 + pageMargin，clipToPadding 关闭，相邻卡片因此露在两侧。
            contentPadding = PaddingValues(start = startItemWidth + pageMargin, end = endItemWidth + pageMargin),
            pageSpacing = pageMargin,
            beyondViewportPageCount = 1,
            key = { page -> page },
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            pageContent(state.realIndex(page))
        }
        if (indicator) {
            PageIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = CouiBannerDefaults.indicatorBottomMargin),
                count = count,
                selectedIndex = current,
                colors = indicatorColors,
                onDotClick = { index -> scope.launch { state.animateScrollToIndex(index, spec) } },
            )
        }
    }
}

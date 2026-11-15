package com.itos.coui.bottomsheet

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.itos.coui.shape.CouiShapes
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 底部面板配色。
 *
 * @property container 面板底色，取原厂 `?couiColorSurface`。
 * @property scrim 遮罩色，取原厂 `coui_color_mask`；组件自行绘制，不再叠加系统窗口压暗。
 * @property handle 顶部拖拽指示条颜色，取原厂 `coui_panel_drag_view_color`。
 */
@Immutable
data class CouiBottomSheetColors(
    val container: Color,



    val scrim: Color,
    val handle: Color,
)

/**
 * 底部面板固定参数。
 *
 * 圆角取 `coui_default_panel_bg_without_shadow` 的 `?couiRoundCornerXXXXL`，即
 * `coui_round_corner_xxxxl` 32dp；`coui_bottom_sheet_bg_top_corner_radius`
 * 是带阴影/跟手分支使用的另一档，本项目贴边面板按背景 drawable 取值。
 */
object CouiBottomSheetDefaults {
    /** `?couiRoundCornerXXXXL` → `coui_round_corner_xxxxl`：32dp。 */
    val cornerRadius = 32.dp

    /** 只有顶部两角；底部由 [followHandMargin] 与屏幕留出空隙。 */
    /** 原生基线；组件默认读取 CouiTheme.shapes.panel。 */
    val shape: Shape = CouiShapes.Default.panel

    /** `coui_bottom_sheet_dialog_elevation`。 */
    val elevation = 17.dp

    /** `coui_bottom_sheet_margin_top_default`。 */
    val marginTop = 40.dp

    /** `coui_bottom_sheet_margin_bottom_default`，手势导航时的底部留白。 */
    val marginBottom = 64.dp

    /** `coui_bottom_sheet_margin_bottom_with_navigationbar`，三键导航时叠加在导航栏之上。 */
    val marginBottomWithNavigationBar = 8.dp

    /** `coui_panel_drag_view` 的固有宽度。 */
    val handleWidth = 36.dp

    /**
     * `coui_bottom_sheet_dialog_follow_hand_margin_left` / `_right` / `_bottom`：
     * 跟手面板四周离屏 8dp，面板像一张浮起的卡片而不是贴满屏幕。
     */
    val followHandMargin = 8.dp

    /** `coui_panel_drag_view` 的固有高度。 */
    val handleHeight = 4.dp

    /** 指示条到面板顶边的距离：原厂 `coui_panel_drag_view_shadow_margin_top` 4dp 加拖拽层的 `coui_panel_drag_view_padding_vertical` 8dp。 */
    val handleTopPadding = 12.dp

    /** `anim/coui_bottom_dialog_enter` 的 `android:duration`。 */
    const val enterDuration = 250

    /** `anim/coui_bottom_dialog_exit` 的 `android:duration`。 */
    const val exitDuration = 250

    /** `anim/android_alert_dialog_enter` 的 pathInterpolator(0.3, 0, 0.1, 1)。 */
    internal val enterEasing = CubicBezierEasing(.3f, 0f, .1f, 1f)

    /** `anim/android_alert_dialog_exit` 的 pathInterpolator(0.3, 0, 1, 1)。 */
    internal val exitEasing = CubicBezierEasing(.3f, 0f, 1f, 1f)

    /**
     * 向下甩动关闭的速度阈值，px/s。
     *
     * 原厂 `COUIBottomSheetBehavior` 的 `onViewReleased` 用 `f6 > 500.0f` 判定向下甩动，
     * 并要求 `|vx| < |vy|`，见 `dragCallback` 的 `onViewReleased`。
     */
    const val hideVelocity = 500f

    /**
     * 松手回弹：原厂 `startSettleRunnable` 用 spring force 把面板送回目标位置，这里取
     * 同量级的无回弹弹簧（`Spring.DampingRatioNoBouncy` + `StiffnessMediumLow`），
     * 避免越过展开位后底部露出空隙。
     */
    internal val settleSpec = spring<Float>(dampingRatio = 1f, stiffness = 400f)

    /**
     * 面板高度上限 `coui_panel_max_height`。原厂另有 `values-sw600dp` 的 800dp 与
     * `coui_panel_max_height_tiny_screen` 720dp 两档，本组件按手机形态取基值。
     */
    val maxPanelHeight = 2000.dp

    /**
     * 面板最大高度：屏幕高度减去顶部 [marginTop] 与底部留白，再按 [maxPanelHeight] 夹紧。
     *
     * 原厂在手势导航下留 [marginBottom]，三键导航下导航栏本身占位，只再留
     * [marginBottomWithNavigationBar]。两者合计的可用区基本一致。
     *
     * @param screenHeight 面板所在窗口的可用高度。
     * @param hasNavigationBar 三键导航为 true；手势导航为 false。
     */
    fun maxHeight(screenHeight: Dp, hasNavigationBar: Boolean): Dp {
        val bottom = if (hasNavigationBar) marginBottomWithNavigationBar else marginBottom
        return (screenHeight - marginTop - bottom).coerceIn(0.dp, maxPanelHeight)
    }

    /** 深浅色预设；面板底色对应 `coui_color_surface`，遮罩对应 `coui_color_mask`。 */
    fun colors(dark: Boolean) = CouiBottomSheetColors(
        container = if (dark) Color(0xFF1E1E1E) else Color.White,
        scrim = if (dark) Color(0x99000000) else Color(0x33000000),
        handle = if (dark) Color(0xFF575757) else Color(0x29000000),
    )
}

/**
 * 从底部升起的面板，承载一组与当前页面相关的操作或内容。
 *
 * 可见性受控：宿主持有 [visible]，点击遮罩与返回键都只回调 [onDismissRequest]，
 * 面板不会自行关闭。退场动画结束后才销毁窗口；退场期间 `visible` 已为 false 时组件
 * 仍保留窗口，因此调用点必须留在组合中，不要用 `if (visible)` 包住本组件。
 *
 * 面板只有顶部两角为圆角、底部贴边，内容超过 [CouiBottomSheetDefaults.maxHeight]
 * 时在面板内滚动。窗口不使用系统压暗与窗口动画，遮罩与滑入由组件自行绘制，便于和
 * 宿主自己的窗口动画统一。
 *
 * 面板可以上下跟手拖动：向上回到展开位、向下超过面板一半或向下甩动都走
 * [onDismissRequest]，由宿主把 [visible] 置 false 后播放退场动画。内容未滚到顶部时
 * 下拉先交给内容滚动，到顶后继续下拉才拖动面板（原厂 `onNestedPreScroll` 的顺序）。
 *
 * 与 [Dialog] 的区别：本组件不带标题、正文与按钮协议，内容完全由 [content] 决定；
 * 也不提供居中位置、背景材质与输入弹窗能力。
 *
 * @param modifier 作用于面板本身，位于默认尺寸与形状之前。
 * @param visible 受控可见性；false 时先播放退场动画再销毁窗口。
 * @param onDismissRequest 遮罩点击与返回键的统一出口，由宿主决定是否置 false。
 * @param dark 解析默认配色使用的深浅色模式；显式传入 [colors] 时不再参与绘制。
 * @param colors 面板、遮罩与指示条颜色。
 * @param content 面板内容，按 [ColumnScope] 纵向排列，不自动附加内边距。
 */
@Composable
@OptIn(ExperimentalLayoutApi::class)
fun BottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiBottomSheetColors = CouiBottomSheetDefaults.colors(dark),
    content: @Composable ColumnScope.() -> Unit,
) {
    val progress = remember { Animatable(0f) }
    // 退场动画期间窗口仍在，动画结束后才让 Dialog 离开组合。
    var retained by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val dismiss by rememberUpdatedState(onDismissRequest)
    // 跟手位移，像素；0 为展开位，向下为正。退场动画叠加在它之上，松手关闭时不跳变。
    var dragOffset by remember { mutableFloatStateOf(0f) }
    // 面板实测高度，决定可拖动距离、结算阈值与遮罩的露出比例。
    var panelHeight by remember { mutableFloatStateOf(0f) }
    var settleJob by remember { mutableStateOf<Job?>(null) }

    /**
     * 松手结算，对应原厂 `dragCallback.onViewReleased` 在 `fitToContents` + `hideable`
     * 且没有折叠档（`collapsedOffset` 即完全收起）时的分支：
     * 1. 向上速度（`f6 < 0`）回展开位；
     * 2. 向下速度超过 [CouiBottomSheetDefaults.hideVelocity] 且大于横向速度，或超过
     *    `releasedLow` 的 `(展开位 + 容器高) / 2`（本组件无底边距，即拖过面板一半），收起；
     * 3. 其余按位置就近，不到一半回展开位。
     *
     * @param velocityY 竖直速度，像素/秒，向下为正（ViewDragHelper 的 `f6` 语义）。
     * @param velocityX 横向速度，像素/秒，用于排除横向甩动。
     */
    val settle: (velocityY: Float, velocityX: Float) -> Unit = { velocityY, velocityX ->
        settleJob?.cancel()
        settleJob = null
        val height = panelHeight
        if (height <= 0f) {
            dragOffset = 0f
        } else {
            val hide = when {
                velocityY < 0f -> false
                velocityY > CouiBottomSheetDefaults.hideVelocity &&
                    abs(velocityY) > abs(velocityX) -> true
                else -> dragOffset > height / 2f
            }
            if (hide) {
                // 位移保留到退场动画：visible 变 false 后，面板从手指位置继续下落。
                dismiss()
            } else {
                settleJob = scope.launch {
                    animate(dragOffset, 0f, velocityY, CouiBottomSheetDefaults.settleSpec) { value, _ ->
                        dragOffset = value.coerceAtLeast(0f)
                    }
                }
            }
        }
    }

    // 原厂 onNestedPreScroll：向上先把面板从下拉位移送回展开位，再把剩余 delta 交给内容；
    // 向下时内容还能往回滚就交给内容，到顶后由面板接管。
    val nestedScrollConnection = remember(visible, scrollState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!visible || source != NestedScrollSource.UserInput) return Offset.Zero
                val dy = available.y
                if (dy > 0f) {
                    if (dragOffset <= 0f) return Offset.Zero
                    settleJob?.cancel()
                    settleJob = null
                    val consumed = min(dy, dragOffset)
                    dragOffset -= consumed
                    return Offset(0f, consumed)
                }
                // 内容未到顶部（还能向上滚动）时不能抢滚动；等价于原厂 canScrollVertically(-1)。
                if (scrollState.value != 0) return Offset.Zero
                val room = panelHeight - dragOffset
                if (room <= 0f) return Offset.Zero
                settleJob?.cancel()
                settleJob = null
                val consumed = max(dy, -room)
                dragOffset -= consumed
                return Offset(0f, consumed)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!visible || dragOffset <= 0f) return Velocity.Zero
                // 滚动速度正值为手指向上，与面板位移方向相反。
                settle(-available.y, available.x)
                return available
            }
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            // 上一次关闭时保留的下拉位移不能带进新的展开位。
            settleJob?.cancel()
            settleJob = null
            dragOffset = 0f
            retained = true
            progress.animateTo(1f, tween(CouiBottomSheetDefaults.enterDuration,
                easing = CouiBottomSheetDefaults.enterEasing))
        } else if (retained) {
            // 结算中的回弹停在当前位置，退场动画从这里继续。
            settleJob?.cancel()
            settleJob = null
            progress.animateTo(0f, tween(CouiBottomSheetDefaults.exitDuration,
                easing = CouiBottomSheetDefaults.exitEasing))
            retained = false
        }
    }
    if (!visible && !retained) return
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            // 窗口铺满屏幕，没有可点击的窗口外侧；遮罩由组件自己承担。
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val hostView = LocalView.current
        SideEffect {
            (hostView.parent as? DialogWindowProvider)?.window?.let { window ->
                window.setGravity(Gravity.BOTTOM)
                window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                window.setWindowAnimations(0)
                // 遮罩由 Compose 绘制，窗口只负责承载；撤销标记后系统不会再有淡出压暗。
                window.setDimAmount(0f)
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                // 窗口不随键盘缩放，IME 由面板底部的 inset 自行避让。
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            }
        }
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val gestureNavigation = WindowInsets.tappableElementIgnoringVisibility.getBottom(density) == 0
            // 键盘与导航栏取并集，避免面板底部重复留白。
            val bottomInset = WindowInsets.ime.union(WindowInsets.navigationBars).getBottom(density)
            val maxPanelHeight = CouiBottomSheetDefaults.maxHeight(maxHeight, hasNavigationBar = !gestureNavigation)
            Box(Modifier.fillMaxSize().drawBehind {
                // 下拉时遮罩随面板露出比例变淡；原厂 calculatePanelOutsideAlpha 用
                // 1 - (top - 展开位) / 容器高，这里取面板自身的露出比例，收起时正好到 0。
                val exposed = if (panelHeight > 0f) {
                    (1f - dragOffset / panelHeight).coerceIn(0f, 1f)
                } else {
                    1f
                }
                drawRect(colors.scrim, alpha = colors.scrim.alpha * progress.value * exposed)
            }.pointerInput(Unit) {
                detectTapGestures { dismiss() }
            })
            Column(modifier.align(Alignment.BottomCenter).fillMaxWidth()
                // 跟手面板四周留 8dp：先留出边距，后面的阴影、圆角与底色都画在缩进后的范围内。
                .padding(
                    start = CouiBottomSheetDefaults.followHandMargin,
                    end = CouiBottomSheetDefaults.followHandMargin,
                    bottom = CouiBottomSheetDefaults.followHandMargin,
                )
                .heightIn(max = maxPanelHeight)
                .onSizeChanged { panelHeight = it.height.toFloat() }
                .nestedScroll(nestedScrollConnection)
                // 只读取动画与拖动值，不产生逐帧重组。
                // 多走一个 elevation，避免面板刚离场时阴影还留在屏幕底部。
                .graphicsLayer {
                    translationY = (1f - progress.value) *
                        (size.height + CouiBottomSheetDefaults.elevation.toPx()) + dragOffset
                }
                .shadow(CouiBottomSheetDefaults.elevation, CouiTheme.shapes.panel, clip = false)
                .clip(CouiTheme.shapes.panel)
                .background(colors.container)
                // 面板只作为命中目标挡住身后的遮罩，不消费事件，子节点的点击与拖动照常生效。
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) awaitPointerEvent()
                    }
                }
                // 面板空白区与顶部指示条区域直接跟手；内容区的拖动由内容滚动和
                // nestedScroll 接管，内容消费后这里不会重复拖动。
                .draggable(
                    state = rememberDraggableState { delta ->
                        settleJob?.cancel()
                        settleJob = null
                        dragOffset = (dragOffset + delta).coerceIn(0f, panelHeight)
                    },
                    orientation = Orientation.Vertical,
                    enabled = visible,
                    onDragStopped = { velocity -> settle(velocity, 0f) },
                )
                .padding(bottom = with(density) { bottomInset.toDp() })) {
                Box(Modifier.fillMaxWidth().padding(top = CouiBottomSheetDefaults.handleTopPadding),
                    contentAlignment = Alignment.Center) {
                    Box(Modifier.size(CouiBottomSheetDefaults.handleWidth, CouiBottomSheetDefaults.handleHeight)
                        .clip(CircleShape).background(colors.handle))
                }
                Column(Modifier.weight(1f, fill = false).verticalScroll(scrollState)) { content() }
            }
        }
    }
}

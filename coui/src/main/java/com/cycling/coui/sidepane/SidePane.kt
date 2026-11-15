package com.cycling.coui.sidepane

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/** 侧栏贴靠的一侧。从右到左的布局里 [Start] 与 [End] 的角与滑动方向自动镜像。 */
enum class CouiSidePaneSide { Start, End }

/** 侧栏的容器色与遮罩色。 */
@Immutable
data class CouiSidePaneColors(
    val container: Color,
    val scrim: Color,
)

/**
 * 侧栏的默认值。
 *
 * 原厂 `com.coui.appcompat.sidepane.COUISidePaneLayout` 在未展开状态下只表现为包住整页的
 * 全屏容器，布局树里读不到侧栏自身的宽度、遮罩透明度或转场数值。因此这里沿用与其它
 * 浮层同一套转场时长和遮罩配比，宽度取可容纳分组列表与说明文字的常规值，
 * 不声明与原厂逐像素对应。
 */
object CouiSidePaneDefaults {
    /** 常规宽度；窄窗口下按 [maxWidthFraction] 收窄。 */
    val width = 280.dp

    /** 侧栏外缘圆角，与弹层统一。 */
    val cornerRadius = 32.dp

    /** 窄窗口下侧栏最多占容器宽度的比例。 */
    const val maxWidthFraction = 0.85f

    /** 展开时长，与底部弹层一致。 */
    const val enterDuration = 250

    /** 收起时长，比展开略快。 */
    const val exitDuration = 200

    internal val enterEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
    internal val exitEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    /**
     * 外缘圆角形状。[side] 一侧贴边，所以只圆外缘的两个角；
     * 角名用相对方向，从右到左的布局里自动镜像。
     */
    fun shape(side: CouiSidePaneSide, radius: Dp = cornerRadius): Shape = if (side == CouiSidePaneSide.Start) {
        RoundedCornerShape(topEnd = radius, bottomEnd = radius)
    } else {
        RoundedCornerShape(topStart = radius, bottomStart = radius)
    }

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiSidePaneColors = if (dark) {
        CouiSidePaneColors(container = Color(0xFF1E1E1E), scrim = Color(0x99000000))
    } else {
        CouiSidePaneColors(container = Color.White, scrim = Color(0x33000000))
    }
}

/**
 * 侧栏容器：页面内容常驻，侧栏在 [visible] 为真时从 [side] 滑出，点击遮罩触发 [onDismissRequest]。
 *
 * 只承载结构，不绑定导航。库没有注册返回键的入口，宿主应在自己的导航层里把返回手势
 * 接成 `onDismissRequest`，并保证同一时刻只有一层浮层响应返回。收起动画播完才把侧栏
 * 移出组合，因此调用点可以直接写 `visible = open`，不需要额外的保留状态。
 *
 * [content] 始终参与布局，可以直接包住整页。遮罩和侧栏都以本组件的位置与尺寸为基准，
 * 而不是窗口：本组件要和整页一样大，遮罩才会盖满整页。宿主应该把它放在页面根部
 * （`Scaffold` 之外或整页外层），并让它拿到有界的整页尺寸——塞进列表、分组卡片或滚动区
 * 的一个格子里，遮罩就只会盖住那一小块。侧栏不跟手拖动：需要边缘滑动展开时，
 * 由宿主在 [modifier] 外层接手势；库只保证开合转场与遮罩的一致性。
 */
@Composable
fun SidePane(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
    side: CouiSidePaneSide = CouiSidePaneSide.Start,
    width: Dp = CouiSidePaneDefaults.width,
    colors: CouiSidePaneColors = CouiSidePaneDefaults.colors(dark),
    paneTitle: String? = null,
    pane: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    // 收起动画播完之前保留侧栏, 否则会看到瞬移。
    var retained by remember { mutableStateOf(visible) }
    val progress = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        if (visible) retained = true
        progress.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(
                durationMillis = if (visible) CouiSidePaneDefaults.enterDuration else CouiSidePaneDefaults.exitDuration,
                easing = if (visible) CouiSidePaneDefaults.enterEasing else CouiSidePaneDefaults.exitEasing,
            ),
        )
        if (!visible) retained = false
    }
    // 语义赋值单独取一次, 避免与参数同名导致自赋值。
    val paneTitleValue = paneTitle
    // 贴边侧用相对方向, 收起时的位移方向要跟着布局方向镜像, 否则从右到左会滑到屏幕中间。
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val collapseToLeft = (side == CouiSidePaneSide.Start) != rtl

    Box(modifier = modifier.fillMaxSize()) {
        content()
        if (visible || retained) {
            val scrimSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        drawRect(colors.scrim, alpha = colors.scrim.alpha * progress.value)
                    }
                    .clickable(
                        interactionSource = scrimSource,
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            )
            BoxWithConstraints(modifier = Modifier.matchParentSize().clipToBounds()) {
                val paneWidth = width.coerceAtMost(maxWidth * CouiSidePaneDefaults.maxWidthFraction)
                Column(
                    modifier = Modifier
                        .align(if (side == CouiSidePaneSide.Start) Alignment.CenterStart else Alignment.CenterEnd)
                        .width(paneWidth)
                        .fillMaxHeight()
                        .graphicsLayer {
                            // 贴边侧收起时退回屏幕外, 所以位移量就是自身宽度。
                            val travel = paneWidth.toPx()
                            translationX = (1f - progress.value) * travel *
                                if (collapseToLeft) -1f else 1f
                        }
                        .clip(CouiSidePaneDefaults.shape(side))
                        .background(colors.container)
                        // 侧栏自身的空白区域要吃掉点击, 否则会穿透到下层遮罩直接关闭。
                        .pointerInput(Unit) { detectTapGestures { } }
                        .then(
                            if (paneTitleValue != null) {
                                Modifier.semantics { this.paneTitle = paneTitleValue }
                            } else {
                                Modifier
                            },
                        ),
                    content = pane,
                )
            }
        }
    }
}

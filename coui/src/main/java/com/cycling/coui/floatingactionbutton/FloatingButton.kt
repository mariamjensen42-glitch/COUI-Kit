package com.cycling.coui.floatingactionbutton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.coui.motion.couiTouchMotion
import com.cycling.coui.motion.rememberCouiTouchMotion
import com.cycling.coui.shape.CouiShapes
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiIcon
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 展开式悬浮按钮的一项，对应 `COUIFloatingButtonItem`：图标、标签文案、可否展开与配色。
 * [location] 保留原厂的位置语义，用于宿主排序；组件按传入顺序自下而上排布。
 */
@Immutable
data class CouiFloatingButtonItem(
    val icon: ImageVector,
    val label: String? = null,
    val location: Int = 0,
    val enabled: Boolean = true,
    val expandable: Boolean = true,
    val contentDescription: String? = null,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val labelBackgroundColor: Color = Color.Transparent,
    val labelColor: Color? = null,
)

/**
 * 展开式悬浮按钮的固定参数，取自 ColorOS 17 `com.support.floatingactionbutton` 资源与
 * `COUIFloatingButton` / `COUIFloatingButtonLabel` 常量：
 * `coui_floating_button_size` 56dp、`coui_floating_button_fab_side_margin` 12dp、
 * `coui_floating_button_item_first_bottom_margin` 32dp、
 * `coui_floating_button_item_normal_bottom_margin` 16dp、标签 12sp、宽上限 220dp、最多 2 行、
 * 内留白 8dp / 4dp；按压 1.0→0.9 用 200ms、抬手 340ms，插值器为 `COUIMoveEaseInterpolator`
 * 的 `PathInterpolator(0.3, 0, 0.1, 1)`；展开弹簧 stiffness 500 / damping 0.8，条目 0.6→1 缩放与
 * 0→1 透明度用 300ms、标签卡 350ms，逐项错开 50ms；收起 150ms、标签卡淡出 200ms；主按钮旋转 45°。
 */
object CouiFloatingButtonDefaults {
    /** `COUIFloatingButton.MAX_COLOR_FLOATING_BUTTON_SIZE`。 */
    const val maxItems = 6

    val buttonSize = 56.dp
    val iconSize = 24.dp
    val sideMargin = 12.dp
    val firstItemBottomMargin = 32.dp
    val itemBottomMargin = 16.dp
    val labelMaxWidth = 220.dp
    val labelHorizontalPadding = 8.dp
    val labelVerticalPadding = 4.dp
    const val labelMaxLines = 2

    /** `support_shadow_size_level_three` / `support_shadow_size_level_two`。 */
    val labelElevation = 12.dp
    val buttonElevation = 4.dp

    const val pressDurationMillis = 200
    const val releaseDurationMillis = 340
    const val pressScale = 0.9f

    val easing = CubicBezierEasing(.3f, 0f, .1f, 1f)

    const val expandDurationMillis = 300
    const val expandLabelAlphaDurationMillis = 350
    const val closeDurationMillis = 150
    const val closeLabelAlphaDurationMillis = 200
    const val itemStaggerMillis = 50L
    const val itemScaleFrom = 0.6f
    const val itemSpringStiffness = 500f
    const val itemSpringDamping = 0.8f

    const val rotateDegrees = 45f
    const val rotateDurationMillis = 250
    const val rotateDurationNoItemMillis = 300

    /** `DELAY_TIME_NO_ACTION_SLIDE_OUT`。 */
    const val autoCollapseMillis = 5000L

    /** 收起位移：`getTotalLabelHeight(i) = dp(i * 72 + 88)`。 */
    fun itemDistance(index: Int) = (index * 72 + 88).dp

    /** 原生基线；组件默认读取 `CouiTheme.shapes.extraSmall`（`coui_round_corner_xs` 4dp）。 */
    val labelShape: Shape = CouiShapes.Default.extraSmall
}

/**
 * 单个「标签 + 圆形按钮」条目，对应 `COUIFloatingButtonLabel`：
 * 标签卡 12sp、宽上限 220dp、最多两行，圆形按钮 56dp 且按下缩放 1.0→0.9（200ms）、
 * 抬手 340ms 回 1.0，两者共用 `COUIMoveEaseInterpolator`。触摸形变与位移复用 `couiTouchMotion`。
 * 标签卡默认透明，只有宿主给出底色时才带 12dp 投影，与原厂 `setLabelBackgroundColor` 一致。
 */
@Composable
fun FloatingButtonLabel(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    item: CouiFloatingButtonItem,
    dark: Boolean = CouiTheme.isDark,
    enabled: Boolean = item.enabled,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val press = rememberFloatingPressScale(source, enabled)
    FloatingButtonItemContent(
        modifier = modifier.clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick),
        item = item,
        dark = dark,
        enabled = enabled,
        circleScale = press.value,
        labelScale = 1f,
        labelAlpha = 1f,
    )
}

/**
 * 展开式悬浮按钮，对应 `COUIFloatingButton`：主按钮 56dp，条目自下而上排布，
 * 首项与主按钮间距 32dp、其余 16dp。收起态主按钮点击展开；展开态再点主按钮交给
 * [onMainActionSelected]，未接管则收起；展开期间 [autoCollapseMillis] 内无操作自动收起。
 * 展开为弹簧位移加 0.6→1 缩放与 0→1 透明度，逐项错开 50ms；收起反向。
 * 省略 `expanded` 时由组件持有状态，传入时由宿主控制。
 * 原厂的 `OplusViewSeamless` 转场、`IMeltBallView` 熔球与 `CoordinatorLayout.Behavior` 自动隐藏未复刻。
 */
@Composable
fun FloatingButton(
    modifier: Modifier = Modifier,
    item: CouiFloatingButtonItem,
    items: List<CouiFloatingButtonItem>,
    onItemClick: (CouiFloatingButtonItem) -> Unit,
    dark: Boolean = CouiTheme.isDark,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    onMainActionSelected: (() -> Boolean)? = null,
    autoCollapseMillis: Long = CouiFloatingButtonDefaults.autoCollapseMillis,
) {
    require(items.size <= CouiFloatingButtonDefaults.maxItems) {
        "展开式悬浮按钮最多 ${CouiFloatingButtonDefaults.maxItems} 个条目"
    }
    var internalExpanded by remember { mutableStateOf(false) }
    val open = expanded ?: internalExpanded
    fun setExpanded(value: Boolean) {
        if (expanded == null) internalExpanded = value
        onExpandedChange?.invoke(value)
    }
    val rotation by animateFloatAsState(
        if (open) CouiFloatingButtonDefaults.rotateDegrees else 0f,
        tween(if (items.isEmpty()) CouiFloatingButtonDefaults.rotateDurationNoItemMillis
        else CouiFloatingButtonDefaults.rotateDurationMillis, easing = CouiFloatingButtonDefaults.easing),
        label = "CouiFloatingButtonRotation",
    )
    LaunchedEffect(open) {
        if (open && autoCollapseMillis > 0) {
            delay(autoCollapseMillis)
            setExpanded(false)
        }
    }
    Column(modifier, horizontalAlignment = Alignment.End) {
        items.asReversed().forEachIndexed { reversedIndex, action ->
            val index = items.lastIndex - reversedIndex
            FloatingButtonMenuItem(
                item = action,
                dark = dark,
                index = index,
                expanded = open,
                bottomPadding = if (index == 0) CouiFloatingButtonDefaults.firstItemBottomMargin
                else CouiFloatingButtonDefaults.itemBottomMargin,
                onClick = {
                    onItemClick(action)
                    setExpanded(false)
                },
            )
        }
        val source = remember { MutableInteractionSource() }
        val press = rememberFloatingPressScale(source, item.enabled)
        FloatingButtonItemContent(
            modifier = Modifier.clickable(source, indication = null, enabled = item.enabled, role = Role.Button) {
                if (!open) {
                    setExpanded(true)
                } else if (onMainActionSelected?.invoke() != true) {
                    setExpanded(false)
                }
            },
            item = item.copy(label = null),
            dark = dark,
            enabled = item.enabled,
            circleScale = press.value,
            labelScale = 1f,
            labelAlpha = 1f,
            rotation = rotation,
        )
    }
}

@Composable
private fun FloatingButtonMenuItem(item: CouiFloatingButtonItem, dark: Boolean, index: Int, expanded: Boolean,
    bottomPadding: Dp, onClick: () -> Unit) {
    val density = LocalDensity.current
    val distance = with(density) { CouiFloatingButtonDefaults.itemDistance(index).toPx() }
    val translation = remember { Animatable(distance) }
    val scale = remember { Animatable(CouiFloatingButtonDefaults.itemScaleFrom) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(expanded) {
        if (expanded) {
            if (index > 0) delay(index * CouiFloatingButtonDefaults.itemStaggerMillis)
            coroutineScope {
                launch {
                    translation.animateTo(0f, spring(dampingRatio = CouiFloatingButtonDefaults.itemSpringDamping,
                        stiffness = CouiFloatingButtonDefaults.itemSpringStiffness))
                }
                launch {
                    scale.animateTo(1f, tween(CouiFloatingButtonDefaults.expandDurationMillis,
                        easing = CouiFloatingButtonDefaults.easing))
                }
                launch {
                    alpha.animateTo(1f, tween(CouiFloatingButtonDefaults.expandLabelAlphaDurationMillis,
                        easing = CouiFloatingButtonDefaults.easing))
                }
            }
        } else {
            if (index > 0) delay(index * CouiFloatingButtonDefaults.itemStaggerMillis)
            coroutineScope {
                launch {
                    translation.animateTo(distance, tween(CouiFloatingButtonDefaults.closeDurationMillis,
                        easing = CouiFloatingButtonDefaults.easing))
                }
                launch {
                    scale.animateTo(CouiFloatingButtonDefaults.itemScaleFrom,
                        tween(CouiFloatingButtonDefaults.closeDurationMillis,
                            easing = CouiFloatingButtonDefaults.easing))
                }
                launch {
                    alpha.animateTo(0f, tween(CouiFloatingButtonDefaults.closeLabelAlphaDurationMillis,
                        easing = CouiFloatingButtonDefaults.easing))
                }
            }
        }
    }
    val visible = expanded || alpha.value > 0f
    if (!visible) return
    val source = remember { MutableInteractionSource() }
    val press = rememberFloatingPressScale(source, item.enabled && expanded && item.expandable)
    FloatingButtonItemContent(
        modifier = Modifier.padding(bottom = bottomPadding).graphicsLayer { translationY = translation.value }
            .clickable(source, indication = null, enabled = item.enabled && expanded,
                role = Role.Button, onClick = onClick),
        item = item,
        dark = dark,
        enabled = item.enabled,
        circleScale = press.value * scale.value,
        labelScale = scale.value,
        labelAlpha = alpha.value,
    )
}

/**
 * 条目视觉：标签卡与 56dp 圆形按钮。圆形按钮的缩放同时承载按压与展开动画，
 * 标签卡只跟随展开；[rotation] 只用于主按钮图标的 45° 旋转。
 */
@Composable
private fun FloatingButtonItemContent(
    modifier: Modifier,
    item: CouiFloatingButtonItem,
    dark: Boolean,
    enabled: Boolean,
    circleScale: Float,
    labelScale: Float,
    labelAlpha: Float,
    rotation: Float = 0f,
) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val motion = rememberCouiTouchMotion(enabled)
    Row(
        modifier.couiTouchMotion(motion, enabled, cancelOnConsumedMove = false)
            .graphicsLayer {
                translationX = motion.translationX.value
                translationY = motion.translationY.value
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!item.label.isNullOrEmpty()) {
            val labelShape = CouiTheme.shapes.extraSmall
            val labelBackground = item.labelBackgroundColor
            Box(Modifier.graphicsLayer {
                scaleX = labelScale
                scaleY = labelScale
                alpha = labelAlpha
            }.padding(end = CouiFloatingButtonDefaults.sideMargin).then(
                if (labelBackground.alpha > 0f)
                    Modifier.shadow(CouiFloatingButtonDefaults.labelElevation, labelShape, clip = false)
                else Modifier,
            ).background(labelBackground, labelShape)
                .padding(horizontal = CouiFloatingButtonDefaults.labelHorizontalPadding,
                    vertical = CouiFloatingButtonDefaults.labelVerticalPadding)
                .widthIn(max = CouiFloatingButtonDefaults.labelMaxWidth)) {
                Text(text = item.label, kind = CouiTextKind.Body,
                    color = item.labelColor ?: scheme.onSurface,
                    maxLines = CouiFloatingButtonDefaults.labelMaxLines,
                    overflow = TextOverflow.Ellipsis)
            }
        }
        Box(Modifier.graphicsLayer {
            scaleX = circleScale + motion.scaleX.value - 1f
            scaleY = circleScale + motion.scaleY.value - 1f
            rotationZ = rotation
        }.size(CouiFloatingButtonDefaults.buttonSize)
            .shadow(CouiFloatingButtonDefaults.buttonElevation, CouiTheme.shapes.circle, clip = false)
            .background(if (enabled) item.containerColor ?: scheme.accent
            else (item.containerColor ?: scheme.accent).copy(alpha = .38f), CouiTheme.shapes.circle),
            contentAlignment = Alignment.Center) {
            CouiIcon(Modifier.size(CouiFloatingButtonDefaults.iconSize), item.icon,
                item.contentDescription, if (enabled) item.contentColor ?: scheme.onAccent
                else (item.contentColor ?: scheme.onAccent).copy(alpha = .6f))
        }
    }
}

/** 按压 1.0→0.9（200ms）与抬手回弹（340ms），插值器为 `COUIMoveEaseInterpolator`。 */
@Composable
private fun rememberFloatingPressScale(source: MutableInteractionSource, enabled: Boolean): State<Float> {
    val pressed by source.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }
    LaunchedEffect(pressed, enabled) {
        if (pressed && enabled) {
            scale.animateTo(CouiFloatingButtonDefaults.pressScale,
                tween(CouiFloatingButtonDefaults.pressDurationMillis, easing = CouiFloatingButtonDefaults.easing))
        } else {
            scale.animateTo(1f, tween(CouiFloatingButtonDefaults.releaseDurationMillis,
                easing = CouiFloatingButtonDefaults.easing))
        }
    }
    return scale.asState()
}

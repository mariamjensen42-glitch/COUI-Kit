package com.itos.coui.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 一条待显示的轻提示。id 由 [ToastHostState] 分配，用于稳定列表身份与去重。
 *
 * 操作按钮与图标槽在入队时随文案一起快照，之后不再随宿主重组更新；需要不同内容时重新调用 [ToastHostState.show]。
 */
@Immutable
data class CouiToast(
    val id: Long,
    val message: String,
    val duration: CouiToastDuration,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val icon: (@Composable () -> Unit)? = null,
)

/**
 * 轻提示队列。宿主调用 [show] 追加，[ToastHost] 负责计时与退出。
 * 状态只保存文案、操作与图标槽，不持有协程、Context 或页面导航。
 */
@Stable
class ToastHostState internal constructor() {
    private val entries = mutableStateListOf<CouiToast>()
    private var nextId = 0L

    /** 当前排队中的提示，按加入顺序排列。 */
    val toasts: List<CouiToast> get() = entries

    /**
     * 追加一条提示；超过 [CouiToastDefaults.maxVisible] 时移除最早的一条。
     *
     * @param message 提示文案，按 [CouiToastDefaults.maxWidth] 换行，不截断。
     * @param duration 停留时长；退出动画另计。
     * @param actionLabel 操作按钮文案，与 [onAction] 同时提供时才显示；最长两行，超出以省略号结尾。
     * @param onAction 操作按钮回调。回调后提示立即进入退出动画，不等剩余停留时间。
     * @param icon 图标槽，内容按 [CouiToastDefaults.iconSize] 的方框居中裁剪；文案起点相应右移。
     */
    fun show(
        message: String,
        duration: CouiToastDuration = CouiToastDuration.Short,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        icon: (@Composable () -> Unit)? = null,
    ) {
        entries.add(CouiToast(id = nextId++, message = message, duration = duration,
            actionLabel = actionLabel, onAction = onAction, icon = icon))
        while (entries.size > CouiToastDefaults.maxVisible) entries.removeAt(0)
    }

    /** 移除指定提示；通常由 [ToastHost] 在退出动画结束后调用。 */
    fun dismiss(id: Long) {
        entries.removeAll { it.id == id }
    }

    /** 立即清空队列，不等待退出动画。 */
    fun dismissAll() {
        entries.clear()
    }
}

@Composable
fun rememberToastHostState(): ToastHostState = remember { ToastHostState() }

/**
 * 轻提示的绘制宿主。自身不占满父级，覆盖在内容上不会拦截触摸；只有操作按钮区域接收点击。
 * 宿主决定位置：用 Box 包裹页面内容，再把本组件放到需要的位置。
 * 计时与退出动画在本组件内完成，因此它必须保持组合；移除页面时未消费的提示随之丢弃。
 */
@Composable
fun ToastHost(
    state: ToastHostState,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiToastColors = CouiToastDefaults.colors(dark),
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(CouiToastDefaults.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.toasts.forEach { toast ->
            key(toast.id) { ToastItem(toast, state, colors) }
        }
    }
}

@Composable
private fun ToastItem(toast: CouiToast, state: ToastHostState, colors: CouiToastColors) {
    val transition = remember { MutableTransitionState(false) }
    var actionTaken by remember { mutableStateOf(false) }
    LaunchedEffect(toast.id) {
        // 在副作用里推进，重组不会把已关闭的提示重新打开。
        // 停留阶段由「剩余时长」与「操作按钮点击」二选一结束，进入、退出与出队仍在同一副作用内顺序推进。
        transition.targetState = true
        withTimeoutOrNull(toast.duration.millis) {
            snapshotFlow { actionTaken }.first { it }
        }
        transition.targetState = false
        delay(CouiToastDefaults.exitMillis)
        state.dismiss(toast.id)
    }
    val enterMillis = CouiToastDefaults.enterMillis.toInt()
    val exitMillis = CouiToastDefaults.exitMillis.toInt()
    AnimatedVisibility(
        visibleState = transition,
        enter = fadeIn(tween(enterMillis)) + slideInVertically(tween(enterMillis)) { it / 4 },
        exit = fadeOut(tween(exitMillis)) + slideOutVertically(tween(exitMillis)) { it / 4 },
    ) {
        val action = toast.actionLabel?.takeIf { toast.onAction != null }
        CouiToastSurface(
            message = toast.message,
            actionLabel = action,
            icon = toast.icon,
            colors = colors,
            onAction = {
                // 退出动画期间仍可点击，用已处理标记保证回调只触发一次。
                if (!actionTaken) {
                    toast.onAction?.invoke()
                    actionTaken = true
                }
            },
        )
    }
}

/**
 * 单条提示的容器：几何、排布、配色与语义都来自轻提示族，队列与计时由外层宿主决定。
 *
 * 轻提示与 Snackbar 共用这一个绘制节点，避免复制 `coui_snack_bar_*` 的排布与投影。
 * 操作按钮的回调只转发点击，重复点击与退场由调用方处理。
 */
@Composable
internal fun CouiToastSurface(
    message: String,
    actionLabel: String?,
    icon: (@Composable () -> Unit)?,
    colors: CouiToastColors,
    modifier: Modifier = Modifier,
    onAction: () -> Unit = {},
) {
    // 原厂单行留白 10dp，多行改为上 12dp / 下 8dp；换行数由实际排版回传，纵向留白不参与宽度测量，不会形成回环。
    var lineCount by remember { mutableIntStateOf(1) }
    val multiline = lineCount > 1
    Row(
        modifier.widthIn(max = CouiToastDefaults.maxWidth)
            .shadow(
                elevation = CouiToastDefaults.shadowElevation,
                shape = CouiToastDefaults.shape,
                clip = false,
                ambientColor = CouiToastDefaults.shadowColor,
                spotColor = CouiToastDefaults.shadowColor,
            )
            .clip(CouiToastDefaults.shape)
            .background(colors.container)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let { content ->
            Box(
                Modifier.padding(
                    start = CouiToastDefaults.iconStartPadding,
                    top = CouiToastDefaults.iconVerticalPadding,
                    bottom = CouiToastDefaults.iconVerticalPadding,
                ).size(CouiToastDefaults.iconSize).clipToBounds(),
                contentAlignment = Alignment.Center,
            ) { content() }
        }
        BasicText(
            text = message,
            modifier = Modifier.weight(1f, fill = false).padding(
                start = CouiToastDefaults.horizontalPadding,
                // 有操作按钮时由它的起始留白承担间距，避免两端各留一次。
                end = if (actionLabel == null) CouiToastDefaults.horizontalPadding else 0.dp,
                top = if (multiline) CouiToastDefaults.multiLineTopPadding
                else CouiToastDefaults.verticalPadding,
                bottom = if (multiline) CouiToastDefaults.multiLineBottomPadding
                else CouiToastDefaults.verticalPadding,
            ),
            style = TextStyle(
                color = colors.content,
                fontSize = CouiToastDefaults.textSize,
                lineHeight = CouiToastDefaults.textSize * CouiToastDefaults.lineHeightMultiplier,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            onTextLayout = { layout -> if (layout.lineCount != lineCount) lineCount = layout.lineCount },
        )
        if (actionLabel != null) {
            val interaction = remember { MutableInteractionSource() }
            BasicText(
                text = actionLabel,
                modifier = Modifier.padding(start = CouiToastDefaults.actionStartPadding)
                    .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                        onAction()
                    }
                    .padding(
                        end = CouiToastDefaults.actionEndPadding,
                        top = if (multiline) CouiToastDefaults.actionMultiLineTopPadding
                        else CouiToastDefaults.actionVerticalPadding,
                        bottom = if (multiline) CouiToastDefaults.actionMultiLineBottomPadding
                        else CouiToastDefaults.actionVerticalPadding,
                    )
                    .widthIn(max = CouiToastDefaults.actionMaxWidth),
                style = TextStyle(
                    color = colors.action,
                    fontSize = CouiToastDefaults.actionTextSize,
                    lineHeight = CouiToastDefaults.actionTextSize * CouiToastDefaults.actionLineHeightMultiplier,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
                maxLines = CouiToastDefaults.actionMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

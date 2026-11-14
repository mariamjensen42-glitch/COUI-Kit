package com.itos.coui.emptystate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 空状态的文字配色；图标槽与操作槽继续使用各自组件的颜色或当前主题。 */
@Immutable
data class CouiEmptyStateColors(val title: Color, val description: Color)

/**
 * 空状态的固定参数，取自 ColorOS 17 手机管家 17.3.13 内嵌的 COUI 资源
 * `coui_component_empty_state.xml` 与 `coui_component_empty_*` 尺寸。
 * 图标槽 120dp × 100dp、文字列最大宽度 280dp、说明距标题 2dp、操作槽距文字 8dp；
 * 标题最多 2 行、说明最多 3 行，超出按尾部省略。
 */
object CouiEmptyStateDefaults {
    /** 原厂 EmptyStateAnimView 的占位尺寸：`coui_component_empty_anim_view_width_normal`。 */
    val iconWidth = 120.dp

    /** `coui_component_empty_anim_view_height_normal`。 */
    val iconHeight = 100.dp

    /** 标题与说明的可用宽度：`coui_component_empty_group_width`。 */
    val contentWidth = 280.dp

    /** 说明距标题的间距：`coui_component_empty_view_subtitle_margin_top`。 */
    val descriptionSpacing = 2.dp

    /** 标题 + 说明的可滚动上限：`coui_component_empty_text_scroll_max_height`，超出时内部滚动。 */
    val textMaxHeight = 92.dp

    /** 操作槽距文字的间距：原厂 `empty_view_action` 的 8dp 上边距。 */
    val actionSpacing = 8.dp

    /** 原厂 `empty_view_title` 的行数上限。 */
    const val titleMaxLines = 2

    /** 原厂 `empty_view_subtitle` 的行数上限。 */
    const val descriptionMaxLines = 3

    /**
     * 按深浅色取文字配色。标题对应原厂 `couiColorLabelPrimary`，说明对应 `couiColorLabelSecondary`。
     * 显式传入 [CouiEmptyStateColors] 时优先，不受本函数影响。
     */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiEmptyStateColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiEmptyStateColors(scheme.onSurface, scheme.onSurfaceVariant)
    }
}

/**
 * 整页空状态：图标槽、标题、说明与操作槽自上而下居中排列，只负责内容布局。
 * 布局按原厂 `coui_component_empty_state.xml` 复刻：图标槽 120dp × 100dp、文字列 280dp 居中、
 * 标题 16sp medium（[CouiTextKind.ItemTitle]）、说明 12sp（[CouiTextKind.Body]，对应 `couiTextBodyXS`）、
 * 操作槽独立。
 *
 * 图标由宿主以内容槽传入，组件不内置插图、不弹窗。
 * 竖向从顶部起排，与原厂根布局一致：需要整页垂直居中时，把本组件放进自带居中策略的容器。
 * 原厂把标题与说明包在最大 92dp 的滚动视图内（`coui_component_empty_text_scroll_max_height`）：
 * 大字号或长文案超出上限时，文字块内部滚动，图标与操作槽位置不动。
 *
 * 操作有两种入口，二者互斥：
 * - [actionLabel] 与 [onAction] 同时非空时，组件内部渲染原厂 `empty_view_action` 样式的文字按钮
 *   （14sp medium、强调色、无容器）。
 * - [action] 内容槽留给宿主自定义，宽度不受 280dp 文字列限制。同时传 [action] 时以 [action] 槽为准。
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiEmptyStateColors = CouiEmptyStateDefaults.colors(dark),
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        // 原厂 coui_component_empty_state.xml 的根 LinearLayout 只有 gravity=center_horizontal，
        // 竖向从顶部起排；需要垂直居中时由宿主容器提供，组件不自行居中。
        verticalArrangement = Arrangement.Top,
    ) {
        if (icon != null) {
            Box(
                Modifier.size(CouiEmptyStateDefaults.iconWidth, CouiEmptyStateDefaults.iconHeight),
                contentAlignment = Alignment.Center,
            ) { icon() }
        }
        Column(
            modifier = Modifier.widthIn(max = CouiEmptyStateDefaults.contentWidth)
                .heightIn(max = CouiEmptyStateDefaults.textMaxHeight)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                kind = CouiTextKind.ItemTitle,
                color = colors.title,
                textAlign = TextAlign.Center,
                maxLines = CouiEmptyStateDefaults.titleMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (!description.isNullOrEmpty()) {
                Text(
                    text = description,
                    kind = CouiTextKind.Body,
                    color = colors.description,
                    modifier = Modifier.padding(top = CouiEmptyStateDefaults.descriptionSpacing),
                    textAlign = TextAlign.Center,
                    maxLines = CouiEmptyStateDefaults.descriptionMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (action != null) {
            Box(Modifier.padding(top = CouiEmptyStateDefaults.actionSpacing)) { action() }
        } else if (actionLabel != null && onAction != null) {
            EmptyStateActionLabel(
                label = actionLabel,
                onClick = onAction,
                dark = dark,
                modifier = Modifier.padding(top = CouiEmptyStateDefaults.actionSpacing),
            )
        }
    }
}

/**
 * 原厂 `empty_view_action` 的等价实现：14sp medium（[CouiTextKind.ButtonLabel]）、
 * 取 `?couiColorPrimaryText`（[CouiTheme.colorSchemeFor] 的 `accent`）、无容器无内边距。
 * 原厂该 TextView 同样没有内边距，点击热区即文字边界；需要 44dp 触控目标时改用 [EmptyState] 的 `action` 槽。
 */
@Composable
private fun EmptyStateActionLabel(
    label: String,
    onClick: () -> Unit,
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier.clip(CouiCapsuleShape)
            // 文字按钮没有容器，按压不叠蒙层，与 CouiButtonStyle.Text 的处理一致。
            .couiButtonFeedback(dark = dark, interactionSource = source, pressFeedback = false)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Text(
            text = label,
            kind = CouiTextKind.ButtonLabel,
            color = CouiTheme.colorSchemeFor(dark).accent,
            textAlign = TextAlign.Center,
        )
    }
}

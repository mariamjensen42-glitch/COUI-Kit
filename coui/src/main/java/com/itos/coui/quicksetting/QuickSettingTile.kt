package com.itos.coui.quicksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.shape.CouiShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 磁贴的两种尺寸：[Small] 为 62dp 方块加下方标签，[Large] 为 138×62dp 横条、标签在磁贴内。 */
enum class CouiQuickSettingSize { Small, Large }

/**
 * 控制中心磁贴的网格与留白。数值取自 ColorOS 17 控制中心的真机布局树
 * （一加 Ace 5，1264×2780 @560dpi，3.5px/dp）：四列、单元 62dp、间距 14dp。
 */
object CouiQuickSettingDefaults {
    val cellSize = 62.dp
    /** 横向与纵向间距，实测 49px = 14dp。 */
    val gap = 14.dp
    /** 大磁贴宽度 = 2 个单元 + 1 个间距。 */
    val largeWidth = 138.dp
    val shape = CouiShape(20.dp, 3f)
    val iconSize = 24.dp
    /** 小磁贴标签与磁贴的间距，实测 14px = 4dp。 */
    val smallLabelSpacing = 4.dp
    val largePadding = 16.dp
    val largeIconSpacing = 8.dp
}

/**
 * 一个控制中心磁贴：图标、标签与开关状态，对应 ColorOS 17 控制中心的快捷开关。
 *
 * 开启态用强调色填充，关闭态用页面卡片色。与[通知卡](../notificationcard/NotificationCard.kt)同一套做法，
 * 组件**不自带材质**：[containerColor] 传 `Color.Transparent` 后，宿主用 `Modifier.couiFloatingSurface`
 * 包裹即可接入凝光玻璃，`shape` 需两处一致。
 *
 * 小磁贴的标签排在磁贴下方，大磁贴的标签在磁贴内部；整卡承担点击并暴露 Switch 语义。
 */
@Composable
fun QuickSettingTile(
    modifier: Modifier = Modifier,
    label: String,
    icon: @Composable () -> Unit,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    size: CouiQuickSettingSize = CouiQuickSettingSize.Small,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
    containerColor: Color? = null,
    shape: Shape = CouiQuickSettingDefaults.shape,
) {
    val background = containerColor ?: if (checked) colors.accent else colors.card
    val contentColor = when {
        !enabled -> colors.disabled
        checked && containerColor == null -> CouiTheme.colorSchemeFor(dark).onAccent
        else -> colors.title
    }
    val source = remember { MutableInteractionSource() }
    val toggle = Modifier.toggleable(value = checked, interactionSource = source, indication = null,
        enabled = enabled, role = Role.Switch) { onToggle(it) }

    when (size) {
        CouiQuickSettingSize.Small -> Column(modifier.width(CouiQuickSettingDefaults.cellSize), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(CouiQuickSettingDefaults.cellSize)
                .clip(shape)
                .background(background)
                .couiButtonFeedback(dark, source, enabled, list = true)
                .then(toggle),
                contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(Modifier.height(CouiQuickSettingDefaults.smallLabelSpacing))
            Text(text = label, kind = CouiTextKind.Body, color = if (enabled) colors.summary else colors.disabled,
                textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        CouiQuickSettingSize.Large -> Row(modifier.width(CouiQuickSettingDefaults.largeWidth)
            .height(CouiQuickSettingDefaults.cellSize)
            .clip(shape)
            .background(background)
            .couiButtonFeedback(dark, source, enabled, list = true)
            .then(toggle)
            .padding(horizontal = CouiQuickSettingDefaults.largePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.largeIconSpacing)) {
            Box(Modifier.size(CouiQuickSettingDefaults.iconSize), contentAlignment = Alignment.Center) { icon() }
            Text(text = label, kind = CouiTextKind.ItemTitle, color = contentColor,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

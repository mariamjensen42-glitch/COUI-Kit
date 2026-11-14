package com.itos.coui.quicksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
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
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 磁贴的两种尺寸：Small 为 62dp 正圆加下方标签，Large 为 138×62dp 胶囊、标签在磁贴内。 */
enum class CouiQuickSettingSize { Small, Large }

/**
 * 控制中心磁贴的网格与留白。数值取自 ColorOS 17 控制中心的真机布局树
 * （一加 Ace 5，1264×2780 @560dpi，3.5px/dp）：四列、单元 62dp、间距 14dp。
 *
 * 小磁贴是 62dp 正圆，大磁贴是整条胶囊；两者都不是 20dp 圆角的卡片。
 * 大磁贴内部还有 42dp 的图标底盘，标签从底盘右侧 6dp 开始。
 */
object CouiQuickSettingDefaults {
    val cellSize = 62.dp
    /** 横向与纵向间距，实测 49px = 14dp。 */
    val gap = 14.dp
    /** 大磁贴宽度 = 2 个单元 + 1 个间距。 */
    val largeWidth = 138.dp
    val shape: Shape = CircleShape
    /** 小磁贴图标实测 98px = 28dp。 */
    val smallIconSize = 28.dp
    /** 大磁贴与档位条内的图标实测 84px = 24dp。 */
    val iconSize = 24.dp
    /** 大磁贴图标底盘实测 147px = 42dp。 */
    val largeIconContainerSize = 42.dp
    /** 小磁贴标签与磁贴的间距，实测 14px = 4dp。 */
    val smallLabelSpacing = 4.dp
    /** 小磁贴标签占满单元加间距的 76dp，否则长标签会比原厂更早省略。 */
    val smallLabelWidth = cellSize + gap
    /** 大磁贴左侧到图标底盘的距离，实测 35px = 10dp。 */
    val largePadding = 10.dp
    /** 大磁贴标签到右缘的留白，实测 53px ≈ 15dp。 */
    val largeEndPadding = 15.dp
    /** 图标底盘到标签的距离，实测 21px = 6dp。 */
    val largeIconSpacing = 6.dp
}

/**
 * 一个控制中心磁贴：图标、标签与开关状态，对应 ColorOS 17 控制中心的快捷开关。
 *
 * 开启态按原厂真机使用白色磁贴，关闭态在深色下用 20% 白、浅色下用页面卡片色；图标颜色由宿主提供，
 * 白色开启态下应传强调色图标。组件不自带材质：containerColor 传 Color.Transparent 后，
 * 宿主用 Modifier.couiFloatingSurface 包裹即可接入凝光玻璃，shape 需两处一致。
 *
 * 小磁贴的标签排在磁贴下方，大磁贴的标签在磁贴内部；整卡（含下方标签）承担点击并暴露
 * Switch 语义。
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
    checkedContainerColor: Color = Color.White,
    iconContainerColor: Color? = null,
    shape: Shape = CouiQuickSettingDefaults.shape,
) {
    val background = if (checked) checkedContainerColor else
        (containerColor ?: if (dark) Color.White.copy(alpha = 0.2f) else colors.card)
    val contentColor = when {
        !enabled -> colors.disabled
        checked -> colors.accent
        else -> colors.title
    }
    val discColor = iconContainerColor ?: if (checked) {
        Color.Transparent
    } else {
        Color.White.copy(alpha = 0.2f)
    }
    val source = remember { MutableInteractionSource() }
    val toggle = Modifier.toggleable(value = checked, interactionSource = source, indication = null,
        enabled = enabled, role = Role.Switch) { onToggle(it) }

    when (size) {
        CouiQuickSettingSize.Small -> Column(modifier.width(CouiQuickSettingDefaults.cellSize).then(toggle),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(CouiQuickSettingDefaults.cellSize)
                .clip(shape)
                .background(background)
                .couiButtonFeedback(dark, source, enabled, list = true),
                contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(Modifier.height(CouiQuickSettingDefaults.smallLabelSpacing))
            Text(text = label, modifier = Modifier.requiredWidth(CouiQuickSettingDefaults.smallLabelWidth),
                kind = CouiTextKind.Tag,
                color = if (enabled) colors.title else colors.disabled,
                textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        CouiQuickSettingSize.Large -> Row(modifier.width(CouiQuickSettingDefaults.largeWidth)
            .height(CouiQuickSettingDefaults.cellSize)
            .clip(shape)
            .background(background)
            .couiButtonFeedback(dark, source, enabled, list = true)
            .then(toggle)
            .padding(start = CouiQuickSettingDefaults.largePadding,
                end = CouiQuickSettingDefaults.largeEndPadding),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(CouiQuickSettingDefaults.largeIconContainerSize)
                .clip(CircleShape)
                .background(discColor),
                contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(Modifier.width(CouiQuickSettingDefaults.largeIconSpacing))
            Text(text = label, kind = CouiTextKind.ButtonLabel, color = contentColor,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

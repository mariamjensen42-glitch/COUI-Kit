package com.cycling.coui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.theme.CouiColor
import com.cycling.coui.theme.CouiTheme

/**
 * 目录图标的语义色，取自 ColorOS 17 图标资源里的高频品牌色：
 * `#00BD13` 绿、`#0080FF` 蓝、`#FFB200` 黄、`#EB3B2F` 红、`#FF7700` 橙、`#952AF4` 紫。
 *
 * 原厂功能图标没有底色，只有图形本身：`drawable/common_file_folder_icon.xml` 是同色系两档
 * `#007BFF` + `#1F8AFD`，`common_file_calendar_icon.xml` 是 `#2D40E9` 主体配 17% 同色底层。
 * 库内图标是单 path 的单色矢量（`android:tint`），这里只取品牌色直接着色，不叠加任何底。
 */
internal enum class DemoIconTone { Blue, Green, Yellow, Red, Orange, Purple }

/** 取当前深浅色下的品牌色；紫色不在 `CouiColor` 语义色板内，取原厂图标资源的实测值。 */
@Composable
internal fun DemoIconTone.color(): Color {
    val dark = CouiTheme.isDark
    return when (this) {
        DemoIconTone.Blue -> if (dark) CouiColor.Night.BLUE else CouiColor.Day.BLUE
        DemoIconTone.Green -> if (dark) CouiColor.Night.GREEN else CouiColor.Day.GREEN
        DemoIconTone.Yellow -> if (dark) CouiColor.Night.YELLOW else CouiColor.Day.YELLOW
        DemoIconTone.Red -> if (dark) CouiColor.Night.RED else CouiColor.Day.RED
        DemoIconTone.Orange -> if (dark) CouiColor.Night.ORANGE else CouiColor.Day.ORANGE
        DemoIconTone.Purple -> if (dark) Color(0xFF9D47FF) else Color(0xFF952AF4)
    }
}

/** 各演示场景复用已核对调用来源的库图标。 */
internal object DemoIcons {
    val Effects: ImageVector
        @Composable get() = CouiIcons.Outlined.WandSpark
    val EffectsSelected: ImageVector
        @Composable get() = CouiIcons.Filled.WandSpark
    val Settings: ImageVector
        @Composable get() = CouiIcons.Filled.SettingDetail
    val SettingsSelected: ImageVector
        @Composable get() = CouiIcons.Filled.SettingDetail
    val Tune: ImageVector
        @Composable get() = CouiIcons.Filled.Sun
    val Switch: ImageVector
        @Composable get() = CouiIcons.Filled.Bluetooth
    val Checkbox: ImageVector
        @Composable get() = CouiIcons.Filled.Grid
    val Dialogs: ImageVector
        @Composable get() = CouiIcons.Filled.Bell
    val Title: ImageVector
        @Composable get() = CouiIcons.Filled.Foldable
    val Date: ImageVector
        @Composable get() = CouiIcons.Filled.Health
    val SimOne: ImageVector
        @Composable get() = CouiIcons.Filled.Transfer
    val SimTwo: ImageVector
        @Composable get() = CouiIcons.Filled.Transfer

    /** 记录侧滑的确认勾选。 */
    val Check: ImageVector
        @Composable get() = CouiIcons.Outlined.Check
}

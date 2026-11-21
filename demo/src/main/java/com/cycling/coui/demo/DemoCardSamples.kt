package com.cycling.coui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.actioncard.ActionCard
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.choicecard.ChoiceCardGroup
import com.cycling.coui.choicecard.CouiChoiceCard
import com.cycling.coui.choicecard.CouiThumbnailChoice
import com.cycling.coui.choicecard.ThumbnailChoiceGroup
import com.cycling.coui.choicecard.ThumbnailNavigationGroup
import com.cycling.coui.icon.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Group
import com.cycling.coui.notificationcard.NotificationCard
import com.cycling.coui.blocks.quicksetting.CouiQuickSettingDefaults
import com.cycling.coui.blocks.quicksetting.CouiQuickSettingSize
import com.cycling.coui.blocks.quicksetting.LevelSlider
import com.cycling.coui.blocks.quicksetting.QuickSettingTile
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.theme.LocalCouiContentColor

/**
 * 组件展台：用页面底色铺底，让 `colors.card` 的白色组件在 Group 的白色卡片上仍有对比。
 * 陈列的是组件本身，不是页面——不要为了贴原厂页面把内容从 Group 里拆出去。
 */
@Composable
private fun ComponentStage(dark: Boolean, content: @Composable ColumnScope.() -> Unit) {
    val colors = CouiPageDefaults.themeColors(dark)
    Column(Modifier.fillMaxWidth().padding(8.dp)
        .clip(CouiPageDefaults.shape)
        .background(colors.background)
        .padding(8.dp), content = content)
}

/** 操作卡的两种形态：提示卡（无标题）与推荐卡（标题 + 副标题）。 */
@Composable
internal fun ActionCardSample(dark: Boolean) {
    val accent = CouiTheme.colorScheme.accent
    Group(title = stringResource(R.string.action_card_notice)) {
        Box(Modifier.padding(16.dp)) {
            ActionCard(
                summary = stringResource(R.string.action_card_notice_summary),
                icon = { Icon(CouiIcons.Filled.ShieldCheck, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
                actions = {
                    Button(onClick = {}, label = stringResource(R.string.action_card_ignore),
                        style = CouiButtonStyle.Text, dark = dark)
                    Button(onClick = {}, label = stringResource(R.string.action_card_enable),
                        style = CouiButtonStyle.Text, dark = dark)
                },
            )
        }
    }
    Group(title = stringResource(R.string.action_card_recommend)) {
        Box(Modifier.padding(16.dp)) {
            ActionCard(
                title = stringResource(R.string.action_card_recommend_title),
                summary = stringResource(R.string.action_card_recommend_summary),
                icon = { Icon(CouiIcons.Filled.Grid, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
                actions = {
                    Button(onClick = {}, label = stringResource(R.string.action_card_ignore),
                        style = CouiButtonStyle.Text, dark = dark)
                    Button(onClick = {}, label = stringResource(R.string.action_card_go),
                        style = CouiButtonStyle.Secondary, dark = dark)
                },
            )
        }
    }
}

/** 模式选择卡组：单选由宿主维护，选中态只叠加强调描边。 */
@Composable
internal fun ChoiceCardSample(dark: Boolean) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val accent = CouiTheme.colorScheme.accent
    val items = listOf(
        CouiChoiceCard(stringResource(R.string.choice_card_balanced),
            { Icon(CouiIcons.Filled.Battery, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            stringResource(R.string.choice_card_balanced_summary)),
        CouiChoiceCard(stringResource(R.string.choice_card_saver),
            { Icon(CouiIcons.Filled.Fan, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            stringResource(R.string.choice_card_saver_summary)),
        CouiChoiceCard(stringResource(R.string.choice_card_performance),
            { Icon(CouiIcons.Filled.Radar, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            stringResource(R.string.choice_card_performance_summary)),
    )
    Group(title = stringResource(R.string.choice_card_title)) {
        Box(Modifier.padding(16.dp)) {
            ChoiceCardGroup(items = items, selectedIndex = selected, onSelect = { selected = it }, dark = dark)
        }
    }
}

/** 缩略图卡：两列单选对应「显示与亮度」，三列导航对应「通知与控制中心 - 分类设置」。 */
@Composable
internal fun ThumbnailChoiceSample(dark: Boolean) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val colors = CouiPageDefaults.themeColors(dark)
    Group(title = stringResource(R.string.thumbnail_choice_title)) {
        Box(Modifier.padding(16.dp)) {
            ThumbnailChoiceGroup(
                items = listOf(
                    CouiThumbnailChoice(
                        label = stringResource(R.string.thumbnail_choice_light),
                        thumbnail = { PhonePreview(Color(0xFFE9EDF5), Color(0xFF3A6FF8)) },
                    ),
                    CouiThumbnailChoice(
                        label = stringResource(R.string.thumbnail_choice_dark),
                        // 比页面底色亮一档，否则深色预览在深色主题下看不出边界。
                        thumbnail = { PhonePreview(Color(0xFF3A3A44), Color(0xFF8AB4FF)) },
                    ),
                ),
                selectedIndex = selected,
                onSelect = { selected = it },
                colors = colors,
            )
        }
    }
    Group(title = stringResource(R.string.thumbnail_nav_title)) {
        ComponentStage(dark) {
        ThumbnailNavigationGroup(
            items = listOf(
                CouiThumbnailChoice(
                    label = stringResource(R.string.thumbnail_nav_lockscreen),
                    thumbnail = { PhonePreview(Color(0xFF23262E), Color(0xFF6FA8FF)) },
                    value = stringResource(R.string.thumbnail_nav_lockscreen_value),
                ),
                CouiThumbnailChoice(
                    label = stringResource(R.string.thumbnail_nav_banner),
                    thumbnail = { PhonePreview(Color(0xFF2A2320), Color(0xFFFFB27A)) },
                    value = stringResource(R.string.thumbnail_nav_banner_value),
                ),
                CouiThumbnailChoice(
                    label = stringResource(R.string.thumbnail_nav_silent),
                    thumbnail = { PhonePreview(Color(0xFF1F2A24), Color(0xFF7FD8A4)) },
                    value = stringResource(R.string.thumbnail_nav_silent_value),
                ),
            ),
            onSelect = {},
            dark = dark,
            colors = colors,
        )
        }
    }
}

/** 通知卡：图标 + 标题 + 相对时间 + 两行正文，对应通知中心的单条通知。 */
@Composable
internal fun NotificationCardSample(dark: Boolean) {
    val accent = CouiTheme.colorScheme.accent
    Group(title = stringResource(R.string.notification_card_title)) {
        ComponentStage(dark) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NotificationCard(
                title = stringResource(R.string.notification_card_title_usb),
                body = stringResource(R.string.notification_card_body_usb),
                time = stringResource(R.string.notification_card_time_now),
                dark = dark,
                icon = { Icon(CouiIcons.Filled.Bell, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            )
            NotificationCard(
                title = stringResource(R.string.notification_card_title_dev),
                body = stringResource(R.string.notification_card_body_dev),
                time = stringResource(R.string.notification_card_time_3m),
                dark = dark,
                icon = { Icon(CouiIcons.Filled.ShieldCheck, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            )
            NotificationCard(
                title = stringResource(R.string.notification_card_title_file),
                body = stringResource(R.string.notification_card_body_file),
                time = stringResource(R.string.notification_card_time_5m),
                dark = dark,
                icon = { Icon(CouiIcons.Filled.CloudData, null, Modifier.size(CouiPageDefaults.iconSize), tint = accent) },
            )
            }
        }
    }
}

/** 控制中心磁贴与竖排档位条：四列网格，单元 62dp、间距 14dp。 */
@Composable
internal fun QuickSettingSample(dark: Boolean) {
    var wlan by rememberSaveable { mutableStateOf(true) }
    var bluetooth by rememberSaveable { mutableStateOf(false) }
    var flashlight by rememberSaveable { mutableStateOf(false) }
    var dnd by rememberSaveable { mutableStateOf(true) }
    var brightness by rememberSaveable { mutableFloatStateOf(.35f) }
    var volume by rememberSaveable { mutableFloatStateOf(.28f) }
    val accent = CouiTheme.colorScheme.accent
    val colors = CouiPageDefaults.themeColors(dark)
    Group(title = stringResource(R.string.quick_setting_title)) {
        ComponentStage(dark) {
            Column(Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.gap)) {
            Row(horizontalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.gap)) {
                QuickSettingTile(label = stringResource(R.string.quick_setting_wlan), checked = wlan,
                    onToggle = { wlan = it }, size = CouiQuickSettingSize.Large, dark = dark,
                    icon = { Icon(CouiIcons.Filled.CloudLink, null,
                        Modifier.size(CouiQuickSettingDefaults.iconSize),
                        tint = if (wlan) accent else LocalCouiContentColor.current) })
                QuickSettingTile(label = stringResource(R.string.quick_setting_bluetooth), checked = bluetooth,
                    onToggle = { bluetooth = it }, size = CouiQuickSettingSize.Large, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Bluetooth, null,
                        Modifier.size(CouiQuickSettingDefaults.iconSize),
                        tint = if (bluetooth) accent else LocalCouiContentColor.current) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.gap)) {
                // 媒体卡占位：原厂这里是 2×2 的播放卡，此处只演示档位条的并排关系。
                Box(Modifier.width(CouiQuickSettingDefaults.largeWidth)
                    .height(CouiQuickSettingDefaults.largeWidth)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.card))
                LevelSlider(value = brightness, onValueChange = { brightness = it }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.DarkLight, null,
                        Modifier.size(CouiQuickSettingDefaults.iconSize),
                        tint = if (dark) Color.Black else Color.White) })
                LevelSlider(value = volume, onValueChange = { volume = it }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Headset, null,
                        Modifier.size(CouiQuickSettingDefaults.iconSize),
                        tint = if (dark) Color.Black else Color.White) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.gap)) {
                QuickSettingTile(label = stringResource(R.string.quick_setting_flashlight), checked = flashlight,
                    onToggle = { flashlight = it }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Fan, null,
                        Modifier.size(CouiQuickSettingDefaults.smallIconSize),
                        tint = if (flashlight) accent else LocalCouiContentColor.current) })
                QuickSettingTile(label = stringResource(R.string.quick_setting_dnd), checked = dnd,
                    onToggle = { dnd = it }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Bell, null,
                        Modifier.size(CouiQuickSettingDefaults.smallIconSize),
                        tint = if (dnd) accent else LocalCouiContentColor.current) })
                QuickSettingTile(label = stringResource(R.string.quick_setting_location), checked = false,
                    onToggle = { }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Location, null,
                        Modifier.size(CouiQuickSettingDefaults.smallIconSize),
                        tint = LocalCouiContentColor.current) })
                QuickSettingTile(label = stringResource(R.string.quick_setting_hotspot), checked = false,
                    onToggle = { }, dark = dark,
                    icon = { Icon(CouiIcons.Filled.Radar, null,
                        Modifier.size(CouiQuickSettingDefaults.smallIconSize),
                        tint = LocalCouiContentColor.current) })
            }
            }
        }
    }
}

/** 示例用的手机预览占位，替代原厂的亮暗壁纸缩略图。 */
@Composable
private fun PhonePreview(background: Color, accent: Color) {
    Column(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(background).padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.fillMaxWidth(0.55f).height(5.dp).clip(RoundedCornerShape(3.dp)).background(accent))
        Box(Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(4.dp))
            .background(accent.copy(alpha = .25f)))
        Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(4.dp))
            .background(accent.copy(alpha = .15f)))
    }
}

package com.cycling.coui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.backdrop.CouiBackdrop
import com.cycling.coui.backdrop.couiBackdrop
import com.cycling.coui.backdrop.rememberCouiBackdrop
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.layout.Item
import com.cycling.coui.menu.CouiMenuItem
import com.cycling.coui.menu.CouiMenuDefaults
import com.cycling.coui.menu.Menu
import com.cycling.coui.menu.ProvideCouiMenuBackdrop
import com.cycling.coui.menu.couiMenuAnchor
import com.cycling.coui.menu.rememberCouiMenuAnchor
import com.cycling.coui.pageindicator.PageIndicator
import com.cycling.coui.progress.CouiProgressSegment
import com.cycling.coui.progress.SegmentedProgress
import com.cycling.coui.shape.CouiShape
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.text.rememberCouiTextStyle
import com.cycling.coui.theme.CouiPresets
import com.cycling.coui.theme.CouiTheme

/**
 * 清理存储页的页面级复刻，重点是顶栏的圆底更多按钮与它的下拉菜单：
 * 菜单锚定在按钮触点、向下展开，圆角 20dp，只保留模糊材质不加页面染色。
 */
@Composable
internal fun CleanupPageSample(dark: Boolean, onBack: () -> Unit) {
    val scheme = CouiPresets.green(dark)
    CouiTheme(darkTheme = dark, colorScheme = scheme) {
        val title = scheme.onSurface
        val summary = scheme.onSurfaceVariant
        val items = listOf(
            CouiMenuItem(0, stringResource(R.string.cleanup_menu_history)),
            CouiMenuItem(1, stringResource(R.string.cleanup_menu_whitelist)),
        )
        val base = CouiMenuDefaults.style(dark)
        val menuStyle = base.copy(
            colors = base.colors.copy(surface = if (dark) Color(0xB3161616) else Color(0xB3F7F7F7)),
            shape = CouiShape(20.dp),
            itemShape = CouiShape(20.dp),
            radius = 20.dp,
            itemTextStyle = rememberCouiTextStyle(CouiTextKind.ItemTitle),
            themeTint = Color.Transparent,
        )
        // 菜单的毛玻璃需要窗口采样源; 不提供时只有不透明底色。
        ProvideCouiMenuBackdrop { windowModifier ->
            val backdrop = rememberCouiBackdrop()
            Box(windowModifier.fillMaxSize()) {
                // 只含页面渐变底的采样源; 顶栏按钮在源之外, 不会采样到自身。
                Box(Modifier.fillMaxSize().couiBackdrop(backdrop)
                    .drawBehind { drawRect(cleanupBackdrop(dark, size.width, size.height)) })
                Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)) {
                    CleanupToolbar(title, dark, items, menuStyle, backdrop) { }
                    Spacer(Modifier.height(20.dp))
                    CleanupHeadline(title, summary)
                    Spacer(Modifier.height(16.dp))
                    Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onClick = {},
                        label = stringResource(R.string.cleanup_action), style = CouiButtonStyle.Primary, dark = dark)
                    Spacer(Modifier.height(24.dp))
                    CleanupStorageCard(title, summary, scheme.accent)
                    Spacer(Modifier.height(16.dp))
                    CleanupRecommendCard(title, summary, scheme.accent)
                    Spacer(Modifier.height(16.dp))
                    PageIndicator(modifier = Modifier.fillMaxWidth(), count = 4, selectedIndex = 0, dark = dark)
                    Spacer(Modifier.height(16.dp))
                    CleanupAppSection(title, summary)
                }
            }
        }
    }
}

/** 顶栏：返回与更多都是圆底纯色按钮；菜单与更多按钮同属一个作用域，锚点才有触点坐标。 */
@Composable
private fun CleanupToolbar(
    title: Color,
    dark: Boolean,
    items: List<CouiMenuItem<Int>>,
    menuStyle: com.cycling.coui.menu.CouiMenuStyle,
    backdrop: CouiBackdrop,
    onBack: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val anchor = rememberCouiMenuAnchor()
    val more = stringResource(R.string.manager_more)
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        GlassIconButton(dark = dark, contentDescription = stringResource(R.string.settings_back), onClick = onBack,
            backdrop = backdrop) {
            Icon(CouiIcons.Filled.Back, null, Modifier.size(22.dp), tint = title)
        }
        Spacer(Modifier.width(12.dp))
        BasicText(text = stringResource(R.string.cleanup_title),
            style = rememberCouiTextStyle(CouiTextKind.TopBar).copy(fontSize = 20.sp, color = title),
            modifier = Modifier.weight(1f))
        GlassIconButton(modifier = Modifier.couiMenuAnchor(anchor), dark = dark, contentDescription = more,
            backdrop = backdrop, onClick = { anchor.open { expanded = true } }) {
            Icon(CouiIcons.Filled.More, null, Modifier.size(22.dp), tint = title)
        }
    }
    Menu(expanded = expanded, onDismissRequest = { expanded = false }, items = items,
        onItemClick = { expanded = false }, style = menuStyle, anchor = anchor,
        // 定位以点击点为基准, 下移让菜单整体落在按钮下方而不是压住按钮。
        offset = DpOffset(0.dp, 30.dp))
}

@Composable
private fun CleanupHeadline(title: Color, summary: Color) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        BasicText(text = stringResource(R.string.cleanup_garbage),
            style = rememberCouiTextStyle(CouiTextKind.HomeTitle).copy(fontSize = 34.sp, color = title))
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.cleanup_detail), kind = CouiTextKind.ItemSummary, color = summary)
            Spacer(Modifier.width(2.dp))
            Icon(CouiIcons.Filled.Next, null, Modifier.size(14.dp), tint = summary)
        }
    }
}

/** 存储构成条：绿、紫、橙、浅灰依次累加，尾部保留轨道色。 */
@Composable
private fun CleanupStorageCard(title: Color, summary: Color, accent: Color) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        .clip(CouiShape(24.dp)).background(title.copy(alpha = .07f)).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.cleanup_storage), kind = CouiTextKind.ItemTitle,
                color = title, modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.cleanup_used), kind = CouiTextKind.ItemSummary, color = summary)
            Spacer(Modifier.width(4.dp))
            Icon(CouiIcons.Filled.Next, null, Modifier.size(16.dp), tint = summary)
        }
        Spacer(Modifier.height(14.dp))
        SegmentedProgress(segments = listOf(
            CouiProgressSegment(.52f, accent),
            CouiProgressSegment(.05f, Color(0xFF9B5CF6)),
            CouiProgressSegment(.04f, Color(0xFFFF9500)),
            CouiProgressSegment(.13f, Color(0xFFB8C4CC)),
        ), dark = true)
    }
}

/** 推荐优化卡：左侧图标与两行文案，右下两个胶囊操作。 */
@Composable
private fun CleanupRecommendCard(title: Color, summary: Color, accent: Color) {
    Text(text = stringResource(R.string.cleanup_recommend), kind = CouiTextKind.GroupTitle, color = summary,
        modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp))
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        .clip(CouiShape(24.dp)).background(title.copy(alpha = .07f)).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CouiShape(12.dp)).background(Color(0xFF1A6DFF).copy(alpha = .18f)),
                contentAlignment = Alignment.Center) {
                Icon(CouiIcons.Filled.Grid, null, Modifier.size(22.dp), tint = Color(0xFF3B9EFF))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.cleanup_apps), kind = CouiTextKind.ItemTitle, color = title)
                Spacer(Modifier.height(2.dp))
                Text(text = stringResource(R.string.cleanup_apps_summary), kind = CouiTextKind.ItemSummary,
                    color = summary)
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)) {
            PillAction(stringResource(R.string.manager_ignore), summary, title.copy(alpha = .10f))
            PillAction(stringResource(R.string.cleanup_go), accent, accent.copy(alpha = .18f))
        }
    }
}

@Composable
private fun PillAction(label: String, contentColor: Color, container: Color) {
    val source = remember { MutableInteractionSource() }
    Box(Modifier.clip(CouiShape(999.dp, 2f)).background(container)
        .clickable(source, indication = null, role = Role.Button, onClick = {})
        .padding(horizontal = 18.dp, vertical = 8.dp)) {
        Text(text = label, kind = CouiTextKind.ButtonLabel, color = contentColor, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CleanupAppSection(title: Color, summary: Color) {
    Group(title = stringResource(R.string.cleanup_app_section)) {
        Item(icon = { Icon(CouiIcons.Filled.Phone, null, Modifier.size(24.dp), tint = Color(0xFF27B73D)) },
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.cleanup_wechat_action), kind = CouiTextKind.ItemValue,
                        color = summary)
                    Spacer(Modifier.width(4.dp))
                    Icon(CouiIcons.Filled.Next, null, Modifier.size(16.dp), tint = summary)
                }
            }) {
            Text(text = stringResource(R.string.cleanup_wechat), kind = CouiTextKind.ItemTitle, color = title)
        }
        Divider()
        Item(icon = { Icon(CouiIcons.Filled.CloudLink, null, Modifier.size(24.dp), tint = Color(0xFF3B9EFF)) },
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "285 MB", kind = CouiTextKind.ItemValue, color = summary)
                    Spacer(Modifier.width(4.dp))
                    Icon(CouiIcons.Filled.Next, null, Modifier.size(16.dp), tint = summary)
                }
            }) {
            Text(text = stringResource(R.string.cleanup_qq), kind = CouiTextKind.ItemTitle, color = title)
        }
    }
}

/** 清理页的底色是顶部偏绿的径向渐变，比首页更亮一点。 */
private fun cleanupBackdrop(dark: Boolean, width: Float, height: Float): Brush = Brush.radialGradient(
    colorStops = arrayOf(
        0f to if (dark) Color(0xFF1A5C28) else Color(0xFFCDEFD2),
        .5f to if (dark) Color(0xFF0A2210) else Color(0xFFEFF9F0),
        1f to if (dark) Color.Black else Color.White,
    ),
    center = Offset(width * .5f, height * .05f),
    radius = height * .75f,
)

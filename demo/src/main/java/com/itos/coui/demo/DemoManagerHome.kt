package com.itos.coui.demo

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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.button.Button
import com.itos.coui.button.CouiButtonStyle
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.backdrop.rememberCouiBackdrop
import com.itos.coui.demo.ui.Icon
import com.itos.coui.icons.CouiIcons
import com.itos.coui.progress.CircularProgress
import com.itos.coui.shape.CouiShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.text.rememberCouiTextStyle
import com.itos.coui.theme.CouiPresets
import com.itos.coui.theme.CouiTheme

/**
 * 手机管家首页的页面级复刻：绿色渐变底、页内大标题、通知卡、白心分数环与 2×2 入口卡。
 * 只使用库的公开组件与主题，页面配色来自 `CouiPresets.green`，不新增库能力。
 */
@Composable
internal fun ManagerHomeSample(dark: Boolean, onBack: () -> Unit) {
    val scheme = CouiPresets.green(dark)
    CouiTheme(darkTheme = dark, colorScheme = scheme) {
        val title = scheme.onSurface
        val summary = scheme.onSurfaceVariant
        val backdrop = rememberCouiBackdrop()
        Box(Modifier.fillMaxSize()) {
            // 只含页面渐变底的采样源; 顶栏按钮在源之外, 不会采样到自身。
            Box(Modifier.fillMaxSize().couiBackdrop(backdrop)
                .drawBehind { drawRect(managerBackdrop(dark, size.width, size.height)) })
            Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)) {
                ManagerToolbar(title, dark, backdrop)
                Spacer(Modifier.height(8.dp))
                ManagerNoticeCard(title, summary, scheme.accent)
                Spacer(Modifier.height(28.dp))
                ManagerScoreRing(title, summary, scheme.accent)
                Spacer(Modifier.height(16.dp))
                Text(text = stringResource(R.string.manager_score_hint), kind = CouiTextKind.ItemTitle,
                    color = title, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp))
                Spacer(Modifier.height(16.dp))
                Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), onClick = {},
                    label = stringResource(R.string.manager_optimize), style = CouiButtonStyle.Primary, dark = dark)
                Spacer(Modifier.height(28.dp))
                ManagerTiles(title, summary)
            }
        }
    }
}

/** 顶部只保留页内大标题与玻璃圆底更多按钮，不使用折叠顶栏，避免与渐变底叠加两层背景。 */
@Composable
private fun ManagerToolbar(title: Color, dark: Boolean, backdrop: CouiBackdrop) {
    val style = rememberCouiTextStyle(CouiTextKind.HomeTitle).copy(fontSize = 30.sp, color = title)
    val more = stringResource(R.string.manager_more)
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        BasicText(text = stringResource(R.string.manager_name), style = style, modifier = Modifier.weight(1f))
        GlassIconButton(dark = dark, contentDescription = more, onClick = {}, backdrop = backdrop) {
            Icon(CouiIcons.Filled.More, null, Modifier.size(22.dp), tint = title)
        }
    }
}

/** 通知提示卡：图标与说明在上，操作在右下，整卡不做点击。 */
@Composable
private fun ManagerNoticeCard(title: Color, summary: Color, accent: Color) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        .clip(CouiShape(20.dp)).background(title.copy(alpha = .08f)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(CouiIcons.Filled.Bell, null, Modifier.size(22.dp), tint = summary)
            Spacer(Modifier.width(12.dp))
            Text(text = stringResource(R.string.manager_notice), kind = CouiTextKind.ItemSummary,
                color = title, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.End)) {
            Text(text = stringResource(R.string.manager_ignore), kind = CouiTextKind.ButtonLabel, color = summary)
            Text(text = stringResource(R.string.manager_enable), kind = CouiTextKind.ButtonLabel, color = accent)
        }
    }
}

/**
 * 分数环：环外发光、右下留缺口，环心是白色实心圆与深色读数。
 * 白心由 `content` 槽自行绘制，不使用额外的组件参数。
 */
@Composable
private fun ManagerScoreRing(title: Color, summary: Color, accent: Color) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgress(
            progress = .78f,
            size = 200.dp,
            strokeWidth = 18.dp,
            gapDegrees = 58f,
            startAngleDegrees = 79f,
            glow = true,
            color = accent,
            // 原厂分数环的弧是沿竖直方向的饱和度渐变，端点取首页 78 分环实测值。
            arcGradient = listOf(Color(0xFF00D616), Color(0xFF29CF28)),
            trackColor = accent.copy(alpha = .12f),
            content = {
                Box(Modifier.size(124.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicText(text = "78",
                            style = rememberCouiTextStyle(CouiTextKind.Display).copy(color = Color(0xFF101010)))
                        BasicText(text = stringResource(R.string.manager_score_unit),
                            style = rememberCouiTextStyle(CouiTextKind.ItemSummary).copy(color = Color(0xFF8A8A8A)))
                    }
                }
            },
        )
    }
}

/** 2×2 入口卡：图标在上、标题与副标题在下，与实用工具页的横向卡区分。 */
@Composable
private fun ManagerTiles(title: Color, summary: Color) {
    val tiles = listOf(
        Triple(R.string.manager_storage_title, R.string.manager_storage_summary,
            CouiIcons.Filled.Fan to CouiColorTone.Blue),
        Triple(R.string.manager_virus_title, R.string.manager_virus_summary,
            CouiIcons.Filled.ShieldFlash to CouiColorTone.Green),
        Triple(R.string.manager_speed_title, R.string.manager_speed_summary,
            CouiIcons.Filled.Radar to CouiColorTone.Orange),
        Triple(R.string.manager_update_title, R.string.manager_update_summary,
            CouiIcons.Filled.AppUpdate to CouiColorTone.Red),
    )
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (titleRes, summaryRes, icon) ->
                    ManagerTile(Modifier.weight(1f), title, summary, stringResource(titleRes),
                        stringResource(summaryRes), icon.first, icon.second)
                }
            }
        }
    }
}

private enum class CouiColorTone { Blue, Green, Orange, Red }

@Composable
private fun ManagerTile(
    modifier: Modifier,
    titleColor: Color,
    summaryColor: Color,
    title: String,
    summary: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tone: CouiColorTone,
) {
    val source = remember { MutableInteractionSource() }
    Column(modifier.clip(CouiShape(20.dp)).background(titleColor.copy(alpha = .06f))
        .clickable(source, indication = null, role = Role.Button, onClick = {})
        .padding(16.dp)) {
        Icon(icon, null, Modifier.size(24.dp), tint = tone.color())
        Spacer(Modifier.height(22.dp))
        Text(text = title, kind = CouiTextKind.ItemTitle, color = titleColor, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(text = summary, kind = CouiTextKind.ItemSummary, color = summaryColor, maxLines = 1)
    }
}

private fun CouiColorTone.color(): Color = when (this) {
    CouiColorTone.Blue -> Color(0xFF3B9EFF)
    CouiColorTone.Green -> Color(0xFF3DDC5A)
    CouiColorTone.Orange -> Color(0xFFFFA033)
    CouiColorTone.Red -> Color(0xFFFF5A5A)
}

/** 真机页面底色是顶部偏绿、向下收黑的径向渐变，不是纯色表面。 */
private fun managerBackdrop(dark: Boolean, width: Float, height: Float): Brush = Brush.radialGradient(
    colorStops = arrayOf(
        0f to if (dark) Color(0xFF123E1D) else Color(0xFFD6F2DA),
        .55f to if (dark) Color(0xFF06150A) else Color(0xFFF1FAF2),
        1f to if (dark) Color.Black else Color.White,
    ),
    center = Offset(width * .5f, height * .1f),
    radius = height * .8f,
)

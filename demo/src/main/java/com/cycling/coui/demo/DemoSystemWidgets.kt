package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.button.IconButton
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.seekbar.VerticalSeekBar
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.MarqueeText
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.toptips.TopTips
import com.cycling.coui.toptips.TopTipsContent
import kotlin.math.roundToInt

/** 跑马灯文本：超宽滚动，两端渐隐；关闭滚动后按静态单行显示。 */
@Composable
internal fun MarqueeTextSample(dark: Boolean) {
    val long = stringResource(R.string.marquee_long_text)
    val short = stringResource(R.string.marquee_short_text)
    var scrolling by rememberSaveable { mutableStateOf(true) }
    val onSurface = CouiTheme.colorSchemeFor(dark).onSurface
    Group(title = stringResource(R.string.marquee_title)) {
        Box(Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp)) {
            MarqueeText(text = long, kind = CouiTextKind.Body, color = onSurface,
                enabled = scrolling)
        }
        Divider()
        Box(Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp)) {
            MarqueeText(text = short, kind = CouiTextKind.Body, color = onSurface)
        }
        Divider()
        DemoPreference(title = stringResource(R.string.marquee_toggle_scrolling),
            summary = stringResource(R.string.marquee_toggle_scrolling_summary),
            onClick = { scrolling = !scrolling })
    }
}

/** 竖向滑动条：拖动、点击跳转与标签。 */
@Composable
internal fun VerticalSeekBarSample(dark: Boolean) {
    var value by rememberSaveable { mutableFloatStateOf(0.4f) }
    Group(title = stringResource(R.string.vertical_seek_title)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            VerticalSeekBar(
                modifier = Modifier.height(200.dp),
                value = value,
                onValueChange = { value = it },
                dark = dark,
            )
            VerticalSeekBar(
                modifier = Modifier.height(200.dp),
                value = value,
                onValueChange = { value = it },
                label = "${(value * 100).roundToInt()}%",
                dark = dark,
            )
        }
        Divider()
        DemoPreference(title = stringResource(R.string.vertical_seek_reset),
            summary = stringResource(R.string.vertical_seek_value, (value * 100).roundToInt()),
            onClick = { value = 0.4f })
    }
}

/** 顶部提示条：内容装配、显示与消失动画。 */
@Composable
internal fun TopTipsSample(dark: Boolean) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column {
        TopTips(visible = visible, dark = dark) {
            TopTipsContent(
                content = stringResource(R.string.top_tips_content),
                title = stringResource(R.string.top_tips_title),
                icon = {
                    Icon(CouiIcons.Filled.Bell, null, tint = CouiTheme.colorSchemeFor(dark).onSurface,
                        size = CouiTopTipsIconSize)
                },
                action = {
                    Button(onClick = { visible = false }, label = stringResource(R.string.top_tips_action),
                        style = CouiButtonStyle.Text, dark = dark)
                },
                close = {
                    IconButton(onClick = { visible = false }) {
                        Icon(CouiIcons.Filled.Close, stringResource(R.string.top_tips_close),
                            tint = CouiTheme.colorSchemeFor(dark).onSurface, size = CouiTopTipsIconSize)
                    }
                },
                dark = dark,
            )
        }
        Group(title = stringResource(R.string.top_tips_title)) {
            DemoPreference(title = stringResource(R.string.top_tips_show),
                onClick = { visible = true })
            Divider()
            DemoPreference(title = stringResource(R.string.top_tips_hide),
                enabled = visible, onClick = { visible = false })
            Divider()
            Text(text = stringResource(R.string.top_tips_hint), kind = CouiTextKind.Body,
                color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant,
                modifier = Modifier.padding(horizontal = CouiPageDefaults.rowPadding, vertical = 12.dp))
        }
    }
}

/** 提示条图标槽边长，与原厂 `coui_toptips_view_icon_btn_size` 一致。 */
private val CouiTopTipsIconSize = 24.dp

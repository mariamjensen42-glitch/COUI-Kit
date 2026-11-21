package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.banner.Banner
import com.cycling.coui.banner.rememberBannerState
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.surface.Surface
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import kotlin.math.roundToInt

@Composable
internal fun BannerSample(dark: Boolean) {
    val colors = CouiTheme.colorSchemeFor(dark)
    var count by rememberSaveable { mutableIntStateOf(4) }
    var peek by rememberSaveable { mutableIntStateOf(0) }
    // count 变化会重建轮播状态并回到第一页，露边宽度只是布局参数，不影响当前页。
    val state = rememberBannerState(count = count)
    Group {
        DemoSliderPreference(title = stringResource(R.string.banner_count), value = count.toFloat(),
            valueRange = 2f..5f, steps = 2, valueText = count.toString(),
            onValueChange = { value -> count = value.roundToInt() })
        Divider()
        DemoSliderPreference(title = stringResource(R.string.banner_margin), value = peek.toFloat(),
            valueRange = 0f..24f, steps = 3, valueText = "${peek}dp",
            onValueChange = { value -> peek = value.roundToInt() })
    }
    Group(title = stringResource(R.string.banner_example)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            // 每屏一张卡片，两侧露出相邻卡片的边；底部居中显示页码圆点，自动轮播默认开启。
            Banner(count = count, state = state,
                startItemWidth = peek.dp, endItemWidth = peek.dp,
                modifier = Modifier.fillMaxWidth().height(160.dp)) { index ->
                Surface(Modifier.fillMaxSize(), color = colors.card) {
                    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomStart) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "${index + 1}", kind = CouiTextKind.Display, color = colors.accent)
                            Text(text = stringResource(R.string.banner_page, index + 1), kind = CouiTextKind.ItemTitle)
                        }
                    }
                }
            }
        }
    }
}

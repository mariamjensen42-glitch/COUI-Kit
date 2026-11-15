package com.itos.coui.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.seekbar.CouiIconSeekBarType
import com.itos.coui.seekbar.IconSeekBar
import com.itos.coui.seekbar.SectionSeekBar
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 分段滑动条与图标滑动条。 */
@Composable
internal fun SeekBarFamilySample(dark: Boolean) {
    var section by rememberSaveable { mutableIntStateOf(2) }
    var brightness by rememberSaveable { mutableIntStateOf(60) }
    var volume by rememberSaveable { mutableIntStateOf(30) }
    val onSurface = CouiTheme.colorSchemeFor(dark).onSurface

    Column {
        Group(title = stringResource(R.string.seekbar_section_title)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
            ) {
                SectionSeekBar(
                    value = section,
                    onValueChange = { section = it },
                    valueRange = 0..8,
                    dark = dark,
                )
                Text(
                    text = stringResource(R.string.seekbar_section_value, section),
                    kind = CouiTextKind.ItemSummary,
                    color = onSurface,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Divider()
            DemoPreference(
                title = stringResource(R.string.seekbar_section_reset),
                summary = stringResource(R.string.seekbar_section_value, section),
                onClick = { section = 2 },
            )
        }
        Group(title = stringResource(R.string.seekbar_icon_title)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.seekbar_icon_brightness),
                    kind = CouiTextKind.ItemSummary,
                    color = onSurface,
                )
                IconSeekBar(
                    progress = brightness,
                    onProgressChange = { brightness = it },
                    type = CouiIconSeekBarType.Brightness,
                    dark = dark,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.seekbar_icon_value, brightness),
                    kind = CouiTextKind.ItemSummary,
                    color = onSurface,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.seekbar_icon_volume),
                    kind = CouiTextKind.ItemSummary,
                    color = onSurface,
                    modifier = Modifier.padding(top = 16.dp),
                )
                IconSeekBar(
                    progress = volume,
                    onProgressChange = { volume = it },
                    type = CouiIconSeekBarType.Volume,
                    dark = dark,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.seekbar_icon_value, volume),
                    kind = CouiTextKind.ItemSummary,
                    color = onSurface,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

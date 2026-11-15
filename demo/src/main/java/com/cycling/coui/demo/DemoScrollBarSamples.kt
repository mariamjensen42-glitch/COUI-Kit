package com.cycling.coui.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.demo.ui.Text
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.scrollbar.ScrollBar
import com.cycling.coui.theme.CouiTheme

@Composable
internal fun ScrollBarSample(dark: Boolean) {
    val state = rememberScrollState()
    Group(title = stringResource(R.string.scrollbar_examples)) {
        Box(Modifier.fillMaxWidth().height(280.dp)) {
            Column(Modifier.fillMaxSize().verticalScroll(state)
                .padding(start = 16.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)) {
                repeat(40) { index ->
                    Text(stringResource(R.string.scrollbar_item, index + 1),
                        Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    if (index != 39) Divider()
                }
            }
            ScrollBar(Modifier.matchParentSize(), state = state, dark = dark)
        }
        Divider()
        Text(stringResource(R.string.scrollbar_hint), Modifier.padding(16.dp),
            color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant)
    }
}

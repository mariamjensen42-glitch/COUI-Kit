package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.icon.Icon
import com.cycling.coui.demo.ui.Text
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.tag.CouiTagDefaults
import com.cycling.coui.tag.Tag
import com.cycling.coui.theme.CouiTheme

@Composable
internal fun TagSample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val solid = CouiTagDefaults.colors(dark).copy(container = scheme.accent, content = scheme.onAccent)
    Group(title = stringResource(R.string.tag_examples)) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Tag(text = stringResource(R.string.tag_beta), dark = dark, colors = solid)
            Tag(text = stringResource(R.string.tag_new), dark = dark, colors = solid,
                leftIcon = { Icon(CouiIcons.Filled.Rings, null, Modifier.fillMaxSize()) })
            Tag(text = stringResource(R.string.tag_recommend), dark = dark,
                colors = CouiTagDefaults.colors(dark).copy(container = scheme.card, content = scheme.accent))
        }
        Divider()
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Tag(text = stringResource(R.string.tag_corner), dark = dark, colors = solid,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 0.dp))
            Tag(text = stringResource(R.string.tag_outlined), dark = dark,
                colors = CouiTagDefaults.colors(dark).copy(container = Color.Transparent, content = scheme.accent,
                    stroke = scheme.accent), strokeWidth = 1.dp)
        }
        Divider()
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.tag_hint), Modifier.weight(1f), color = scheme.onSurfaceVariant)
        }
    }
}

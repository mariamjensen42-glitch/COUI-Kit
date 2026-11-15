package com.cycling.coui.demo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.navigation.CouiNavigationItem
import com.cycling.coui.navigation.DockBar
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

@Composable
internal fun DockBarSample(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= 33) DockBarContent(dark)
    else Text(text = stringResource(R.string.requires_api_34), kind = CouiTextKind.Body,
        color = CouiTheme.colorScheme.onSurfaceVariant)
}

@RequiresApi(33)
@Composable
private fun DockBarContent(dark: Boolean) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val scheme = CouiTheme.colorScheme
    val items = listOf(
        CouiNavigationItem(stringResource(R.string.dock_bar_home), CouiIcons.Filled.Grid),
        CouiNavigationItem(stringResource(R.string.dock_bar_tools), CouiIcons.Filled.GridSliders),
    )
    ChromePreview(modifier = Modifier.height(180.dp), background = {
        Box(Modifier.matchParentSize().background(
            Brush.verticalGradient(listOf(scheme.container, scheme.surface))))
        Text(text = stringResource(R.string.dock_bar_preview_text), kind = CouiTextKind.Body,
            color = scheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2,
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp))
    }) { backdrop ->
        DockBar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), backdrop = backdrop,
            items = items, selectedIndex = selected, onSelection = { selected = it },
            centerIcon = CouiIcons.Filled.ShieldCheck,
            centerDescription = stringResource(R.string.dock_bar_center), onCenterClick = {},
            dark = dark)
    }
}

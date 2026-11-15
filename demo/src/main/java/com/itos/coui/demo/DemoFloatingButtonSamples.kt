package com.itos.coui.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.itos.coui.floatingactionbutton.CouiFloatingButtonItem
import com.itos.coui.floatingactionbutton.FloatingButton
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.theme.CouiTheme

@Composable
internal fun FloatingButtonSample(dark: Boolean) {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    val scheme = CouiTheme.colorSchemeFor(dark)
    val items = listOf(
        CouiFloatingButtonItem(icon = CouiIcons.Filled.Add, label = stringResource(R.string.fab_menu_create),
            location = 0, labelBackgroundColor = scheme.container, labelColor = scheme.onSurface),
        CouiFloatingButtonItem(icon = CouiIcons.Filled.More, label = stringResource(R.string.fab_menu_share),
            location = 1, labelBackgroundColor = scheme.container, labelColor = scheme.onSurface),
        CouiFloatingButtonItem(icon = CouiIcons.Filled.CloudData, label = stringResource(R.string.fab_menu_upload),
            location = 2, labelBackgroundColor = scheme.container, labelColor = scheme.onSurface),
    )
    Group {
        DemoPreference(title = stringResource(R.string.fab_menu_clicks), value = clicks.toString())
        Divider()
        DemoPreference(title = stringResource(R.string.fab_menu_hint))
    }
    Group(title = stringResource(R.string.fab_menu_title)) {
        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingButton(
                item = CouiFloatingButtonItem(icon = CouiIcons.Filled.Add,
                    contentDescription = stringResource(R.string.fab_menu_expand)),
                items = items,
                onItemClick = { clicks++ },
                dark = dark,
            )
        }
    }
}

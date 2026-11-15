package com.cycling.coui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.navigation.AccessoryNavigationBar
import com.cycling.coui.navigation.BottomFloatingAccessory
import com.cycling.coui.navigation.BottomFloatingNavigationBar
import com.cycling.coui.navigation.CouiAccessoryAction
import com.cycling.coui.navigation.CouiNavigationItem
import com.cycling.coui.navigation.CouiSideNavigationItem
import com.cycling.coui.navigation.NavigationRail
import com.cycling.coui.navigation.SideNavigationBar
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

@Composable
internal fun NavigationFamilySample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val items = listOf(
        CouiNavigationItem(stringResource(R.string.nav_family_home), CouiIcons.Filled.CloudLink),
        CouiNavigationItem(stringResource(R.string.nav_family_files), CouiIcons.Filled.BookOpen),
        CouiNavigationItem(stringResource(R.string.nav_family_lab), CouiIcons.Filled.Lab),
        CouiNavigationItem(stringResource(R.string.nav_family_settings), CouiIcons.Filled.Album),
    )
    val sideItems = items.map { CouiSideNavigationItem(label = it.title, icon = it.icon) }
    var sideIndex by remember { mutableIntStateOf(0) }
    var railIndex by remember { mutableIntStateOf(0) }
    var barIndex by remember { mutableIntStateOf(0) }
    var drawerMode by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var accessoriesExpanded by remember { mutableStateOf(false) }
    val actions = listOf(
        CouiAccessoryAction(CouiIcons.Filled.AI, stringResource(R.string.nav_family_ai), onClick = {}),
        CouiAccessoryAction(CouiIcons.Filled.Album, stringResource(R.string.nav_family_settings), onClick = {}),
        CouiAccessoryAction(CouiIcons.Filled.Lab, stringResource(R.string.nav_family_lab), onClick = {}),
    )

    Group(title = stringResource(R.string.nav_family_side)) {
        DemoSwitchPreference(
            title = stringResource(R.string.nav_family_drawer),
            checked = drawerMode,
            onCheckedChange = { drawerMode = it; expanded = it },
        )
        Divider()
        Box(
            Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(16.dp)),
        ) {
            SideNavigationBar(
                items = sideItems,
                selectedIndex = sideIndex,
                onSelection = { sideIndex = it },
                expanded = if (drawerMode) expanded else true,
                onExpandedChange = { expanded = it },
                docked = !drawerMode,
                dark = dark,
                modifier = Modifier.fillMaxSize(),
                content = {
                    Box(
                        Modifier.fillMaxSize().background(scheme.container),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = stringResource(R.string.nav_family_content), kind = CouiTextKind.Body)
                    }
                },
            )
        }
    }

    Group(title = stringResource(R.string.nav_family_rail)) {
        Box(Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(16.dp))) {
            Row(Modifier.fillMaxSize().background(scheme.container)) {
                NavigationRail(
                    items = items,
                    selectedIndex = railIndex,
                    onSelection = { railIndex = it },
                    dark = dark,
                )
                Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.nav_family_content), kind = CouiTextKind.Body)
                }
            }
        }
    }

    Group(title = stringResource(R.string.nav_family_bar)) {
        BottomFloatingNavigationBar(
            items = items,
            selectedIndex = barIndex,
            onSelection = { barIndex = it },
            dark = dark,
            modifier = Modifier.fillMaxWidth(),
            leading = {
                BottomFloatingAccessory(dark = dark) {
                    Icon(CouiIcons.Filled.PanelEnterLeft, null, Modifier.size(24.dp))
                }
            },
            trailing = {
                BottomFloatingAccessory(dark = dark) {
                    Icon(CouiIcons.Filled.AI, null, Modifier.size(24.dp))
                }
            },
        )
    }

    Group(title = stringResource(R.string.nav_family_accessory)) {
        DemoSwitchPreference(
            title = stringResource(R.string.nav_family_expand),
            checked = accessoriesExpanded,
            onCheckedChange = { accessoriesExpanded = it },
        )
        Divider()
        Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            AccessoryNavigationBar(
                actions = actions,
                expanded = accessoriesExpanded,
                dark = dark,
            )
        }
    }
}

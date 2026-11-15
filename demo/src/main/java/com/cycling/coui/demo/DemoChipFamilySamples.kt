package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.chip.Chip
import com.cycling.coui.chip.ChipGroup
import com.cycling.coui.chip.ChipGroupEditText
import com.cycling.coui.chip.CouiChipDefaults
import com.cycling.coui.chip.CouiChipStyle
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.theme.CouiTheme

@Composable
internal fun ChipsSample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val colors = CouiChipDefaults.colors(dark, scheme.accent, scheme.onAccent)
    var selected by remember { mutableStateOf(setOf(0)) }
    var tags by remember {
        mutableStateOf(listOf("Design", "Compose", "Kit", "Accessibility", "Motion", "Theme"))
    }
    var draft by remember { mutableStateOf("") }
    var collapsed by remember { mutableStateOf(false) }

    Group(title = stringResource(R.string.chip_basic)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Chip(
                text = stringResource(R.string.chip_tag_value, 1),
                leadingIcon = { Icon(CouiIcons.Filled.Rings, null, Modifier.fillMaxSize()) },
                colors = colors,
            )
            Chip(text = stringResource(R.string.chip_tag_value, 2), count = "12", showRedDot = true, colors = colors)
            Chip(
                text = stringResource(R.string.chip_tag_value, 3),
                style = CouiChipStyle.Selection,
                selected = true,
                colors = colors,
            )
        }
    }

    Group(title = stringResource(R.string.chip_selectable)) {
        ChipGroup(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            List(5) { index ->
                Chip(
                    text = stringResource(R.string.chip_tag_value, index + 1),
                    style = CouiChipStyle.Selection,
                    selected = index in selected,
                    onSelectedChange = { checked ->
                        selected = if (checked) selected + index else selected - index
                    },
                    colors = colors,
                )
            }
        }
    }

    Group(title = stringResource(R.string.chip_closable)) {
        ChipGroupEditText(
            text = draft,
            onTextChange = { draft = it },
            onSubmit = { value ->
                tags = tags + value
                draft = ""
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = stringResource(R.string.chip_input_hint),
        ) {
            tags.forEach { tag ->
                Chip(
                    text = tag,
                    onClose = { tags = tags - tag },
                    closeContentDescription = stringResource(R.string.chip_close_description),
                    colors = colors,
                )
            }
        }
    }

    Group(title = stringResource(R.string.chip_collapsable)) {
        DemoSwitchPreference(
            title = stringResource(R.string.chip_collapse_toggle),
            checked = collapsed,
            onCheckedChange = { collapsed = it },
        )
        Divider()
        ChipGroup(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            collapsable = true,
            collapsed = collapsed,
            onCollapsedChange = { collapsed = it },
            collapsedMaxRows = 2,
        ) {
            List(12) { index ->
                Chip(text = stringResource(R.string.chip_tag_value, index + 1), colors = colors)
            }
        }
    }
}

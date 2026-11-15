package com.itos.coui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogActionStyle
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.dialog.CouiGuidePage
import com.itos.coui.dialog.CouiListItem
import com.itos.coui.dialog.CouiListSelection
import com.itos.coui.dialog.GuideDialog
import com.itos.coui.dialog.ListDialog
import com.itos.coui.dialog.SecurityDialog
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

@Composable
internal fun DialogFamilySample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    var opened by remember { mutableStateOf<String?>(null) }
    var checked by remember { mutableStateOf(true) }
    var single by remember { mutableStateOf(1) }
    var multi by remember { mutableStateOf(setOf(0)) }

    Group(title = stringResource(R.string.dialog_guide_group)) {
        DemoPreference(title = stringResource(R.string.dialog_guide_entry), onClick = { opened = "guide" })
    }
    Group(title = stringResource(R.string.dialog_security_group)) {
        DemoPreference(title = stringResource(R.string.dialog_security_entry), onClick = { opened = "security" })
    }
    Group(title = stringResource(R.string.dialog_list_group)) {
        DemoPreference(title = stringResource(R.string.dialog_list_entry), onClick = { opened = "list" })
        Divider()
        DemoPreference(title = stringResource(R.string.dialog_list_single_entry), onClick = { opened = "single" })
        Divider()
        DemoPreference(title = stringResource(R.string.dialog_list_multi_entry), onClick = { opened = "multi" })
    }

    val dismiss = { opened = null }
    val done = CouiDialogAction(
        text = stringResource(R.string.dialog_done),
        onClick = dismiss,
        style = CouiDialogActionStyle.Primary,
        colors = CouiDialogDefaults.primaryActionColors(scheme.accent),
    )
    when (opened) {
        "guide" -> GuideDialog(
            pages = List(3) { index ->
                CouiGuidePage(
                    title = stringResource(R.string.dialog_guide_page_title, index + 1),
                    description = stringResource(R.string.dialog_guide_page_description),
                    image = { GuideImage(index + 1, dark) },
                )
            },
            onFinish = dismiss,
            onDismissRequest = dismiss,
            onSkip = dismiss,
            dark = dark,
        )
        "security" -> {
            val linkLabel = stringResource(R.string.dialog_security_link)
            val template = stringResource(R.string.dialog_security_statement)
            val statement = buildAnnotatedString {
                append(template)
                val start = template.indexOf(linkLabel)
                if (start >= 0) addLink(
                    LinkAnnotation.Clickable(
                        tag = "privacy",
                        styles = TextLinkStyles(SpanStyle(color = scheme.accent,
                            textDecoration = TextDecoration.Underline)),
                    ) { dismiss() },
                    start, start + linkLabel.length,
                )
            }
            SecurityDialog(
                onDismissRequest = dismiss,
                confirmAction = done,
                title = stringResource(R.string.dialog_security_title),
                message = stringResource(R.string.dialog_security_message),
                statement = statement,
                checked = checked,
                onCheckedChange = { checked = it },
                dark = dark,
            )
        }
        "list" -> ListDialog(
            items = List(4) { CouiListItem(text = stringResource(R.string.dialog_list_item, it + 1)) },
            onDismissRequest = dismiss,
            title = stringResource(R.string.dialog_list_entry),
            onItemClick = { dismiss() },
            dark = dark,
        )
        "single" -> ListDialog(
            items = List(4) { CouiListItem(text = stringResource(R.string.dialog_list_item, it + 1)) },
            onDismissRequest = dismiss,
            title = stringResource(R.string.dialog_list_single_entry),
            selection = CouiListSelection.Single,
            checkedIndex = single,
            onCheckedIndexChange = { single = it },
            confirmAction = done,
            dark = dark,
        )
        "multi" -> ListDialog(
            items = List(4) { CouiListItem(text = stringResource(R.string.dialog_list_item, it + 1)) },
            onDismissRequest = dismiss,
            title = stringResource(R.string.dialog_list_multi_entry),
            selection = CouiListSelection.Multi,
            checkedIndices = multi,
            onCheckedIndicesChange = { multi = it },
            maxChecked = 2,
            confirmAction = done,
            dark = dark,
        )
    }
}

@Composable
private fun GuideImage(index: Int, dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(scheme.accent.copy(alpha = if (dark) .32f else .18f), scheme.container))
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.dialog_guide_image, index), kind = CouiTextKind.Title,
            color = scheme.onSurfaceVariant)
    }
}

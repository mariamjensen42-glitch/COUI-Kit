package com.itos.coui.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.itos.coui.demo.ui.Text
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.CouiTextLinkDefaults
import com.itos.coui.text.Text as CouiText
import com.itos.coui.theme.CouiTheme

@Composable
internal fun RichTextSample(dark: Boolean) {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    val prefix = stringResource(R.string.richtext_prefix)
    val link = stringResource(R.string.richtext_link)
    val suffix = stringResource(R.string.richtext_suffix)
    val styles = CouiTextLinkDefaults.textLinkStyles(dark)
    val text = buildAnnotatedString {
        append(prefix)
        withLink(LinkAnnotation.Clickable("privacy", styles) { clicks++ }) { append(link) }
        append(suffix)
    }
    Group(title = stringResource(R.string.richtext_examples)) {
        CouiText(text, kind = CouiTextKind.Body, modifier = Modifier.fillMaxWidth().padding(16.dp))
        Divider()
        CouiText(text, kind = CouiTextKind.ItemSummary, modifier = Modifier.fillMaxWidth().padding(16.dp))
        Divider()
        Text(stringResource(R.string.richtext_clicks, clicks), Modifier.padding(16.dp),
            color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant)
    }
}

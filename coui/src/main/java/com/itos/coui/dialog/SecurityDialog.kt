package com.itos.coui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.R
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.checkbox.Checkbox
import com.itos.coui.effect.couiStateMask
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme

/**
 * 安全弹窗：正文之外的说明文案（可携带链接）与「不再提示」勾选框。
 *
 * 链接由宿主在 [statement] 里用 `LinkAnnotation` 标注，点击由文本自身的链接处理器接管。
 * 勾选状态受控，返回键关闭交给 [onDismissRequest]。
 */
@Composable
fun SecurityDialog(
    onDismissRequest: () -> Unit,
    confirmAction: CouiDialogAction,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    statement: AnnotatedString? = null,
    checked: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    checkboxLabel: String = stringResource(R.string.coui_dialog_security_checkbox),
    dismissAction: CouiDialogAction? = null,
    neutralAction: CouiDialogAction? = null,
    placement: CouiDialogPlacement = CouiDialogPlacement.Bottom,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiDialogColors = CouiDialogDefaults.themeColors(dark),
    shape: Shape = CouiTheme.shapes.dialog,
    cornerRadius: Dp = Dp.Unspecified,
    material: CouiDialogMaterial? = null,
    backdrop: CouiBackdrop? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    visible: Boolean? = null,
) {
    CouiDialogWindow(modifier, onDismissRequest, colors, dark, placement, shape, cornerRadius, properties,
        material, backdrop, visible) {
        val configuration = LocalConfiguration.current
        var messageLines by remember(message) { mutableIntStateOf(1) }
        var stacked by remember { mutableIntStateOf(if (neutralAction != null) 1 else 0) }
        val tall = messageLines > 1 || (stacked == 1 && (dismissAction != null || neutralAction != null))
        Column(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                if (!title.isNullOrEmpty()) Box(Modifier.fillMaxWidth().padding(
                    start = CouiDialogDefaults.contentPadding, end = CouiDialogDefaults.contentPadding,
                    top = CouiDialogDefaults.titleTop, bottom = CouiDialogDefaults.titleBottom)) {
                    DialogText(text = title, kind = DialogTextKind.Title, color = colors.title,
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(max = CouiDialogDefaults.titleMaxHeight(configuration.screenHeightDp))
                            .verticalScroll(rememberScrollState()))
                }
                if (!message.isNullOrEmpty()) DialogText(text = message, kind = DialogTextKind.Message,
                    color = colors.message, modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = CouiDialogDefaults.contentPadding)
                        .padding(top = if (title.isNullOrEmpty() && !tall) CouiDialogDefaults.messageTopWithoutTitle else 0.dp,
                            bottom = if (title.isNullOrEmpty() && !tall) CouiDialogDefaults.messageBottomWithoutTitle
                            else 0.dp),
                    onLineCount = { if (messageLines != it) messageLines = it })
                Column(Modifier.fillMaxWidth().padding(top = CouiSecurityDialogDefaults.panelTopPadding,
                    bottom = CouiSecurityDialogDefaults.panelBottomPadding)) {
                    if (statement != null) BasicText(
                        text = statement,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = CouiSecurityDialogDefaults.statementHorizontalPadding)
                            .padding(top = CouiSecurityDialogDefaults.statementTopPadding,
                                bottom = CouiSecurityDialogDefaults.statementBottomPadding),
                        style = CouiSecurityDialogDefaults.statementTextStyle().copy(color = colors.message),
                    )
                    SecurityCheckbox(checked, onCheckedChange, checkboxLabel, colors, dark)
                }
            }
            DialogActions(confirmAction, dismissAction, neutralAction, colors, dark, material != null) {
                val next = if (it) 1 else 0
                if (stacked != next) stacked = next
            }
        }
    }
}

@Composable
private fun SecurityCheckbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, label: String,
    colors: CouiDialogColors, dark: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val enabled = onCheckedChange != null
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = CouiSecurityDialogDefaults.checkboxStartPadding,
                end = CouiSecurityDialogDefaults.checkboxEndPadding,
                top = CouiSecurityDialogDefaults.checkboxTopPadding)
            .couiStateMask(interactionSource, dark = dark, enabled = enabled, shape = CouiTheme.shapes.extraSmall)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled,
                role = Role.Checkbox) { onCheckedChange?.invoke(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, enabled = enabled)
        Spacer(Modifier.width(CouiSecurityDialogDefaults.checkboxSpacing))
        CouiText(text = label, color = colors.message, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

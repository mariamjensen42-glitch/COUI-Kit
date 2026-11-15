package com.itos.coui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.itos.coui.buttonBar.CouiButtonBarDefaults
import com.itos.coui.buttonBar.CouiButtonBarLayout

/** 弹窗底部操作区。普通、安全与列表弹窗共用同一套横排/竖排规则。 */
@Composable
internal fun DialogActions(
    confirm: CouiDialogAction,
    dismiss: CouiDialogAction?,
    neutral: CouiDialogAction?,
    colors: CouiDialogColors,
    dark: Boolean,
    material: Boolean,
    onStacked: (Boolean) -> Unit,
) {
    // 项目既有特化：横排取「取消、中性、确认」，竖排取「中性、确认、取消」。
    CouiButtonBarLayout(
        items = listOfNotNull(neutral, confirm, dismiss),
        modifier = Modifier.zIndex(1f),
        horizontalItems = listOfNotNull(dismiss, neutral, confirm),
        text = { it.text },
        enabled = { it.enabled },
        textStyle = rememberDialogTextStyle(DialogTextKind.Button),
        contentVerticalPadding = CouiButtonBarDefaults.buttonVerticalPadding,
        onStacked = onStacked,
    ) { action, _, modifier ->
        DialogActionButton(
            action,
            if (action === neutral) colors.copy(action = colors.destructive) else colors,
            dark,
            material,
            modifier,
        )
    }
}

@Composable
private fun DialogActionButton(
    action: CouiDialogAction,
    colors: CouiDialogColors,
    dark: Boolean,
    material: Boolean,
    modifier: Modifier,
) {
    DialogButton(
        modifier = modifier,
        text = action.text,
        onClick = action.onClick,
        dark = dark,
        enabled = action.enabled,
        style = action.style,
        material = material,
        colors = action.colors ?: DialogButtonDefaults.secondaryColors(dark, material).copy(
            content = colors.action,
            disabledContent = colors.disabledAction,
        ),
    )
}

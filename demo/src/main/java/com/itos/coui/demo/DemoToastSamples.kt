package com.itos.coui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.itos.coui.demo.ui.Icon
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.theme.CouiTheme
import com.itos.coui.toast.CouiToastDefaults
import com.itos.coui.toast.CouiToastDuration
import com.itos.coui.toast.ToastHostState

@Composable
internal fun ToastSample(state: ToastHostState) {
    val short = stringResource(R.string.toast_message_short)
    val long = stringResource(R.string.toast_message_long)
    val stack = (1..3).map { stringResource(R.string.toast_message_stack, it) }
    val actionMessage = stringResource(R.string.toast_message_action)
    val actionLabel = stringResource(R.string.toast_action_undo)
    val undone = stringResource(R.string.toast_message_undone)
    val iconMessage = stringResource(R.string.toast_message_icon)
    val contentColor = CouiTheme.colorScheme.onSurface
    Group {
        DemoPreference(title = stringResource(R.string.toast_show_short), onClick = { state.show(short) })
        Divider()
        DemoPreference(title = stringResource(R.string.toast_show_long),
            onClick = { state.show(long, CouiToastDuration.Long) })
        Divider()
        DemoPreference(title = stringResource(R.string.toast_show_stack),
            onClick = { stack.forEach { message -> state.show(message) } })
        Divider()
        DemoPreference(title = stringResource(R.string.toast_show_action), onClick = {
            state.show(actionMessage, actionLabel = actionLabel, onAction = { state.show(undone) })
        })
        Divider()
        DemoPreference(title = stringResource(R.string.toast_show_icon), onClick = {
            state.show(iconMessage, icon = {
                // 填满 30dp 图标槽, 否则小图标会在槽内留出过多空白。
                Icon(DemoIcons.Check, null, tint = contentColor, size = CouiToastDefaults.iconSize)
            })
        })
        Divider()
        DemoPreference(title = stringResource(R.string.toast_dismiss_all), onClick = { state.dismissAll() })
    }
}

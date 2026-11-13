package com.itos.coui.demo

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.button.Button
import com.itos.coui.button.CouiButtonStyle
import com.itos.coui.demo.ui.Icon
import com.itos.coui.emptystate.EmptyState
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.Group

/** 空状态：仅标题、带说明、文字操作按钮，以及自定义操作槽。 */
@Composable
internal fun EmptyStateSample(dark: Boolean) {
    val title = stringResource(R.string.emptystate_title)
    val description = stringResource(R.string.emptystate_description)
    val action = stringResource(R.string.emptystate_action)
    val retry = stringResource(R.string.emptystate_retry)
    Group(title = stringResource(R.string.emptystate_basic)) {
        EmptyState(modifier = Modifier.padding(vertical = 24.dp), title = title, dark = dark)
    }
    Group(title = stringResource(R.string.emptystate_text_only)) {
        EmptyState(modifier = Modifier.padding(vertical = 24.dp), title = title,
            description = description, dark = dark)
    }
    // 原厂 empty_view_action：14sp medium 的强调色文字按钮，由组件内部渲染。
    Group(title = stringResource(R.string.emptystate_with_action)) {
        EmptyState(modifier = Modifier.padding(vertical = 24.dp), title = title,
            description = description, dark = dark,
            icon = { Icon(CouiIcons.Outlined.Search, null, Modifier.size(48.dp)) },
            actionLabel = action, onAction = {})
    }
    // 需要强调或更大触控目标时改用自己的内容槽，此时 actionLabel 不再生效。
    Group(title = stringResource(R.string.emptystate_custom_action)) {
        EmptyState(modifier = Modifier.padding(vertical = 24.dp), title = title,
            description = description, dark = dark,
            action = {
                Button(onClick = {}, label = retry, style = CouiButtonStyle.Primary, dark = dark)
            })
    }
}

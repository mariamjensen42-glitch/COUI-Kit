package com.itos.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.textfield.TextField

@Composable
internal fun TextFieldSample(dark: Boolean) {
    var plain by rememberSaveable { mutableStateOf("") }
    var titled by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    // 输入框自带描边容器，不再套分组卡片，按原厂表单那样独立成列。
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(modifier = Modifier.fillMaxWidth(), value = plain, onValueChange = { plain = it },
            dark = dark)
        TextField(modifier = Modifier.fillMaxWidth(), value = titled, onValueChange = { titled = it },
            title = stringResource(R.string.textfield_label), dark = dark)
        // 空内容时提示错误；输入后错误消失，输入本身不被拦截。
        TextField(modifier = Modifier.fillMaxWidth(), value = error, onValueChange = { error = it },
            title = stringResource(R.string.textfield_error),
            error = if (error.isEmpty()) stringResource(R.string.textfield_error_hint) else null, dark = dark)
        TextField(modifier = Modifier.fillMaxWidth(), value = "", onValueChange = {},
            title = stringResource(R.string.textfield_disabled),
            enabled = false, dark = dark)
    }
}

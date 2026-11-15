package com.itos.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.button.Button
import com.itos.coui.button.CouiButtonStyle
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.textfield.CodeInputView
import com.itos.coui.textfield.MultiInputCard
import com.itos.coui.textfield.TextField

@Composable
internal fun TextFieldsSample(dark: Boolean) {
    var nickname by remember { mutableStateOf("Coui") }
    var showError by remember { mutableStateOf(true) }
    var code by remember { mutableStateOf("1234") }
    var secure by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    Group(title = stringResource(R.string.textfield_error_title)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = nickname,
                onValueChange = {
                    nickname = it
                    showError = false
                },
                label = stringResource(R.string.textfield_error_label),
                dark = dark,
                error = if (showError) stringResource(R.string.textfield_error_message) else null,
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { showError = true },
                    label = stringResource(R.string.textfield_error_action),
                    style = CouiButtonStyle.Text,
                )
            }
        }
    }

    Group(title = stringResource(R.string.textfield_code_title)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            CodeInputView(
                code = code,
                onCodeChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
                secure = secure,
                dark = dark,
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { code = "" },
                    label = stringResource(R.string.textfield_code_clear),
                    style = CouiButtonStyle.Text,
                )
            }
        }
        Divider()
        DemoSwitchPreference(
            title = stringResource(R.string.textfield_code_secure),
            checked = secure,
            onCheckedChange = { secure = it },
        )
    }

    Group(title = stringResource(R.string.textfield_multi_title)) {
        MultiInputCard(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            hint = stringResource(R.string.textfield_multi_hint),
            maxLength = 60,
            dark = dark,
        )
    }
}

package com.cycling.coui.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.layout.Group
import com.cycling.coui.statement.PrivacyStatement
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/**
 * 隐私声明：确认与拒绝都是宿主回调，示例把结果写在页面下方。
 * 容器固定 420dp 高，模拟声明页在真实页面里的可用高度。
 */
@Composable
internal fun PrivacyStatementSample(dark: Boolean) {
    var action by rememberSaveable { mutableStateOf<String?>(null) }
    val agreed = stringResource(R.string.statement_agreed)
    val declined = stringResource(R.string.statement_declined)
    Group(title = stringResource(R.string.statement_title)) {
        Column(Modifier.fillMaxWidth().height(420.dp).padding(16.dp)) {
            PrivacyStatement(
                modifier = Modifier.fillMaxSize(),
                title = stringResource(R.string.statement_heading),
                statement = stringResource(R.string.statement_body),
                confirmText = stringResource(R.string.statement_confirm),
                exitText = stringResource(R.string.statement_exit),
                onConfirm = { action = agreed },
                onExit = { action = declined },
                dark = dark,
            )
        }
    }
    Group {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            val current = action
            Text(
                text = if (current != null) stringResource(R.string.statement_result, current)
                else stringResource(R.string.statement_hint),
                kind = CouiTextKind.ItemSummary,
                color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant,
            )
        }
    }
}

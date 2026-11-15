package com.itos.coui.demo

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
import com.itos.coui.demo.ui.Icon
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme
import com.itos.coui.topappbar.UserFollowBar

/** 用户主页顶栏：头像、主副标题与关注按钮。 */
@Composable
internal fun UserFollowSample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    var following by rememberSaveable { mutableStateOf(false) }
    var fill by rememberSaveable { mutableStateOf(false) }

    Column {
        Group(title = stringResource(R.string.user_follow_group)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 8.dp),
            ) {
                UserFollowBar(
                    title = stringResource(R.string.user_follow_name),
                    subtitle = stringResource(R.string.user_follow_subtitle),
                    following = following,
                    onFollowingChange = { following = it },
                    fill = fill,
                    avatar = {
                        Icon(
                            imageVector = CouiIcons.Filled.UserCircle,
                            contentDescription = null,
                            tint = scheme.onSurface,
                            size = 16.dp,
                        )
                    },
                    dark = dark,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.user_follow_hint),
                    kind = CouiTextKind.ItemSummary,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Divider()
                DemoSwitchPreference(
                    title = stringResource(R.string.user_follow_fill),
                    checked = fill,
                    onCheckedChange = { fill = it },
                )
            }
        }
    }
}

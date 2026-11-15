package com.cycling.coui.demo

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
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import com.cycling.coui.topappbar.UserFollowBar

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
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
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

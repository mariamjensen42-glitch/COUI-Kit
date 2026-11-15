package com.cycling.coui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cycling.coui.theme.CouiTheme

/**
 * 浅色与深色两套外观。
 *
 * 组件的默认色全部由 `CouiTheme` 推导，所以同一份预览代码在两套外观下都应成立；
 * 新增组件预览时优先套这个注解，而不是手写单个 `@Preview`。
 */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class CouiLightDarkPreview

/**
 * 三种窗口宽度断点。
 *
 * 361dp 取自原厂参考设备（一加 Ace 5，1264×2780 @560dpi），属于 [com.cycling.coui.layout.CouiWindowSizeClass.Compact]；
 * 720dp 与 1000dp 覆盖折叠屏展开与平板横屏。用它检查布局在断点两侧是否按预期切换。
 */
@Preview(name = "Compact 361dp", showBackground = true, widthDp = 361, heightDp = 780)
@Preview(name = "Medium 720dp", showBackground = true, widthDp = 720, heightDp = 900)
@Preview(name = "Expanded 1000dp", showBackground = true, widthDp = 1000, heightDp = 900)
annotation class CouiBreakpointPreview

/**
 * 预览用的页面底板：铺满、套主题、给一层分组间距。
 *
 * 只用于预览，不参与发布 API；背景色取自当前外观，避免预览器默认白底把浅色内容吃掉。
 */
@Composable
internal fun CouiPreviewSurface(
    dark: Boolean = CouiTheme.isDark,
    content: @Composable () -> Unit,
) {
    CouiTheme(darkTheme = dark) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CouiTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            content()
        }
    }
}

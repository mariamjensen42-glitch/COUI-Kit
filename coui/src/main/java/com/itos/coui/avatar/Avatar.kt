package com.itos.coui.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.shape.CouiShape
import com.itos.coui.theme.CouiTheme

/** 头像的常用尺寸与轮廓；圆角与圆形由调用方选择，默认是圆形。 */
object CouiAvatarDefaults {
    val size = 40.dp
    val smallSize = 32.dp
    val largeSize = 56.dp
    val shape: Shape = CouiShape(999.dp, 2f)
}

/**
 * 头像容器：固定尺寸、可裁剪的底，内容由调用方提供（图片、首字母、图标都行）。
 *
 * 组件只负责尺寸、轮廓和底色，不做图片加载、占位或首字母取字——这些属于业务与图片库职责。
 * 需要圆角方形时传 `shape = CouiShape(12.dp)`；底色默认取主题的表面色，宿主可按联系人色板覆盖。
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: Dp = CouiAvatarDefaults.size,
    shape: Shape = CouiAvatarDefaults.shape,
    containerColor: Color = CouiTheme.colorScheme.container,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.size(size).clip(shape).background(containerColor), contentAlignment = Alignment.Center) {
        content()
    }
}

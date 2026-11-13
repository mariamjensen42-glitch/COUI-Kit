package com.itos.coui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.shape.CouiShape
import com.kyant.capsule.ContinuousRoundedRectangle
import com.itos.coui.theme.CouiTheme

internal object DemoShapes {
    val contentCard = CouiShape(20.dp)
    val card = ContinuousRoundedRectangle(24.dp)
    val item = ContinuousRoundedRectangle(12.dp)
    val record = ContinuousRoundedRectangle(16.dp)
    val capsule = ContinuousRoundedRectangle(999.dp)
    val checkbox = ContinuousRoundedRectangle(4.dp)
    val checkboxBorder = ContinuousRoundedRectangle(3.2.dp)
    val menu = CouiShape(28.dp)
    val menuItem = ContinuousRoundedRectangle(28.dp)
    val couiCircle = CouiShape(999.dp, cornerWeight = 2f)
    val couiCapsule = CouiCapsuleShape
    val effect = CouiShape(24.dp)
}

/** 顶栏渐变的页面底色；Demo 不做额外染色，Unspecified 表示沿用组件自身的通用预设。 */
internal val LocalDemoTopBarGradient = staticCompositionLocalOf { Color.Unspecified }

@Composable
internal fun DemoTheme(dark: Boolean, content: @Composable () -> Unit) {
    val currentContent by rememberUpdatedState(content)
    // 切换局部外观时移动同一内容树，保留页面状态与 renderer 的生命周期。
    val scopedContent = remember { movableContentOf { currentContent() } }
    CouiTheme(darkTheme = dark) {
        CompositionLocalProvider(LocalDemoTopBarGradient provides Color.Unspecified) { scopedContent() }
    }
}

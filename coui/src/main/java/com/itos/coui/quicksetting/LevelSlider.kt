package com.itos.coui.quicksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.shape.CouiShape
import com.itos.coui.theme.CouiTheme

/**
 * 竖排档位条的尺寸与圆角。数值取自 ColorOS 17 控制中心的真机布局树：
 * `vertical_seekbar` 为 `[656,735][873,1218]`，即 62×138dp，与 4 列磁贴同宽、两行高。
 */
object CouiLevelSliderDefaults {
    val width = 62.dp
    val height = 138.dp
    val shape = CouiShape(20.dp, 3f)
    val iconPadding = 16.dp
}

/**
 * 控制中心的竖排档位条：底部按比例填充，图标固定在底部。
 *
 * 与 [com.itos.coui.slider.Slider] 不是同一个控件——后者是横向细轨道加圆钮，带刻度吸附、触感与 RTL 镜像；
 * 这里是整块 62×138dp 的胶囊，填充量即当前值，竖直方向不参与 RTL 镜像，也不做步进与触感。
 *
 * 按下或拖动到任意位置都会直接给出该位置对应的值，`value` 取 0f..1f，1f 为充满。
 * 同样不自带材质：[containerColor] 传 `Color.Transparent` 后由宿主用 `Modifier.couiFloatingSurface` 包裹。
 */
@Composable
fun LevelSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: @Composable () -> Unit,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
    containerColor: Color = colors.card,
    /**
     * 填充色。默认取强调色，两种主题下都可见；原厂深色控制中心用白色填充，需要时显式传 [Color.White]，
     * 但白色在浅色主题的白底轨道上不可见，浅色下不要用它。
     */
    fillColor: Color = colors.accent,
    shape: Shape = CouiLevelSliderDefaults.shape,
    enabled: Boolean = true,
) {
    val level = value.coerceIn(0f, 1f)
    Box(modifier.width(CouiLevelSliderDefaults.width)
        .height(CouiLevelSliderDefaults.height)
        .clip(shape)
        .background(containerColor)
        .semantics { progressBarRangeInfo = ProgressBarRangeInfo(level, 0f..1f) }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            val update: (Float) -> Unit = { y -> onValueChange((1f - y / size.height).coerceIn(0f, 1f)) }
            awaitEachGesture {
                val down = awaitFirstDown()
                update(down.position.y)
                down.consume()
                drag(down.id) { change ->
                    update(change.position.y)
                    change.consume()
                }
            }
        },
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (level > 0f) {
            Box(Modifier.fillMaxWidth().fillMaxHeight(level).background(fillColor))
        }
        Box(Modifier.padding(bottom = CouiLevelSliderDefaults.iconPadding)
            .size(CouiQuickSettingDefaults.iconSize), contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

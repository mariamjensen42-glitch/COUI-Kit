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
import androidx.compose.foundation.shape.CircleShape
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
import com.itos.coui.theme.CouiTheme

/**
 * 竖排档位条的尺寸与圆角。数值取自 ColorOS 17 控制中心的真机布局树：
 * `vertical_seekbar` 为 `[656,735][873,1218]`，即 62×138dp 胶囊，与 4 列磁贴同宽、两行高；
 * 图标槽实测 126px = 36dp，距底 35px = 10dp。
 */
object CouiLevelSliderDefaults {
    val width = 62.dp
    val height = 138.dp
    val shape: Shape = CircleShape
    val iconPadding = 10.dp
    val iconContainerSize = 36.dp
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
     * 填充色。原厂深色控制中心用白色填充，这里在深色下默认 [Color.White]；浅色下白色会与浅色轨道混在一起，
     * 因此默认退回强调色，需要白色时由宿主显式覆盖。
     */
    fillColor: Color = if (dark) Color.White else colors.accent,
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
            .size(CouiLevelSliderDefaults.iconContainerSize), contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

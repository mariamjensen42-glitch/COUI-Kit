package com.itos.coui.button

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.shape.CouiShape

/** 按钮外观。Primary 为强调色实心，Secondary 为中性容器，Text 为无容器文字操作。 */
enum class CouiButtonStyle { Primary, Secondary, Text }

/** 一个按钮的容器与内容色；禁用态单独给出，与原厂资源一致，不由正常态推导透明度。 */
@Immutable
data class CouiButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
)

/**
 * 按钮固定参数与配色，取自 ColorOS 17 手机管家 17.3.13 内嵌的 COUI 资源：
 * `Widget.COUI.Button` / `Widget.COUI.Button.Large`、`couiTextAppearanceButtonL` 与 `coui_btn_*` 尺寸。
 * 大按钮 44dp 高、最小 132dp 宽、水平 12dp / 垂直 8dp 内留白、16sp sans-serif-medium、行距 1.2；
 * 容器取 `?couiColorContainerTheme`，内容取 `?couiColorLabelOnColor`（白色），禁用内容 0x28 白。
 * 同一组数值由 `CouiDialogDefaults` 的对话框操作按钮使用，本组件不另行调参。
 */
object CouiButtonDefaults {
    val minHeight = 44.dp
    val minWidth = 132.dp
    val horizontalPadding = 12.dp
    val verticalPadding = 8.dp

    /** 图标与文字的间距，与原厂按钮内部留白一致。 */
    val iconSpacing = 4.dp

    /** `coui_loading_btn_circle_radius`：加载态三圆点的半径。 */
    val loadingDotRadius = 1.dp

    /** `coui_loading_btn_circle_spacing`：相邻圆点之间的净距。 */
    val loadingDotSpacing = 2.dp

    /**
     * 三圆点的脉冲周期。原厂没有为按钮加载单独提供动画资源（`anim/` 下没有对应文件），
     * 节奏沿用同一套加载字形的 800ms（见 `CouiSwitchLoadingMotion`）。
     */
    const val loadingPeriodMillis = 800L

    /** COUIButton 自动圆角：等宽高画圆，其他尺寸使用平滑胶囊。 */
    val shape: Shape = CouiButtonShape

    /**
     * 主按钮：强调色填充，内容色取色板的 `onAccent`；省略时沿用原厂的白色。
     * 亮色强调（例如绿色主题）需要深色内容，因此不再固定为白色。
     * 禁用容器为强调色 77/255，内容为 0x28 白。
     */
    fun primaryColors(accent: Color, onAccent: Color = Color.White): CouiButtonColors = CouiButtonColors(
        container = accent,
        content = onAccent,
        disabledContainer = accent.copy(alpha = 77f / 255f),
        disabledContent = Color(0x28FFFFFF),
    )

    /** 次按钮：浅色 0xFFEBEBEB、深色 0xFF353535，内容为 0xE6 的前景色。 */
    fun secondaryColors(dark: Boolean): CouiButtonColors = CouiButtonColors(
        container = Color(if (dark) 0xFF353535 else 0xFFEBEBEB),
        content = Color(if (dark) 0xE6FFFFFF else 0xE6000000),
        disabledContainer = Color(if (dark) 0x1AFFFFFF else 0x14000000),
        disabledContent = Color(if (dark) 0x28FFFFFF else 0x42000000),
    )

    /**
     * 文字按钮：无容器、内容取强调色，禁用内容沿用 0x42 / 0x28。
     * 原厂 COUIButton 只有主、次两个分支，这一档按同一套按钮排版去掉容器推演，供行内低优先级操作使用。
     */
    fun textColors(accent: Color, dark: Boolean): CouiButtonColors = CouiButtonColors(
        container = Color.Transparent,
        content = accent,
        disabledContainer = Color.Transparent,
        disabledContent = Color(if (dark) 0x28FFFFFF else 0x42000000),
    )

    /** 按外观取配色；显式 colors 仍可覆盖。 */
    fun colors(style: CouiButtonStyle, accent: Color, dark: Boolean,
        onAccent: Color = Color.White): CouiButtonColors = when (style) {
        CouiButtonStyle.Primary -> primaryColors(accent, onAccent)
        CouiButtonStyle.Secondary -> secondaryColors(dark)
        CouiButtonStyle.Text -> textColors(accent, dark)
    }
}

private object CouiButtonShape : Shape {
    private val circle = CouiShape(999.dp, 2f)

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        (if (size.width == size.height) circle else CouiCapsuleShape).createOutline(size, layoutDirection, density)
}

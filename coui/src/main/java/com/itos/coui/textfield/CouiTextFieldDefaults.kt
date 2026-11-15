package com.itos.coui.textfield

import android.graphics.Typeface
import android.os.Build
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.text.couiTextStyle
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

/**
 * 输入框的配色角色。卡片底色与同页分组卡片同源，但没有沿用页面色板类型：
 * 输入组件族和其它组件一样持有自己的角色，不依赖 `layout` 的页面类型。
 * 需要强调色与主题不一致时，在 [CouiTextFieldDefaults.colors] 的结果上 `copy`。
 */
@Immutable
data class CouiTextFieldColors(
    /** 卡片底色；下划线等无底色样式由宿主显式传透明。 */
    val container: Color,
    /** 输入的正文。 */
    val text: Color,
    /** 占位提示。 */
    val placeholder: Color,
    /** 标签文字。 */
    val label: Color,
    /** 光标。 */
    val cursor: Color,
    /** 禁用时的正文、占位提示与标签。 */
    val disabled: Color,
    /** 错误态：描边与错误文案。 */
    val error: Color = Color(0xFFDB382C),
)

/**
 * 输入框的固定度量与预设。外部尺寸与页面留白由调用方的 Modifier 提供，组件只给出最小高度。
 * 配色从当前主题的 `card`、`onSurface`、`onSurfaceVariant` 与 `accent` 解析，不创建主题作用域。
 */
object CouiTextFieldDefaults {
    /** 单行输入的最小高度；字号放大或多行内容会继续撑高。 */
    val minHeight = 62.dp

    /** `Widget.COUI.EditText.HintAnim.Rectangle` 的内留白：左 16、右 10、上下 12。 */
    val contentPadding = PaddingValues(start = 16.dp, end = 10.dp, top = 12.dp, bottom = 12.dp)

    /** 图标槽与输入区之间的间距。槽位内容尺寸由调用方决定。 */
    val slotSpacing = 8.dp

    /** `coui_textinput_corner_radius`：10dp 标准圆角，不是分组卡片的平滑大圆角。 */
    val shape: Shape = RoundedCornerShape(10.dp)

    /** 标签常驻时的字号；只在 [textStyle] 未指定字号时回退使用。 */
    val labelSize = 16.sp

    /** 标签浮动到输入上方的字号。 */
    val floatingLabelSize = 10.sp

    /** 标签浮动时上移的距离。输入文字同步下移同一距离，卡片高度不变。 */
    val labelShift = 8.dp

    /** 错误态的描边宽度。 */
    val errorStrokeWidth = 1.dp

    /** 错误文案与卡片之间的距离。 */
    val errorTextSpacing = 4.dp

    /** 进入错误态的时长：抖动 450ms、描边与文案染色 217ms，`DURATION_HINT_ANIMATOR`。 */
    val errorEnterDurationMillis = 217

    /** 退出错误态时文案淡出 283ms。 */
    val errorExitDurationMillis = 283

    /** 抖动总时长，`ShakeInterpolator` 的四段累计。 */
    val errorShakeDurationMillis = 450

    /** 抖动振幅 `coui_edit_text_shake_amplitude`。 */
    val errorShakeAmplitude = 5.33.dp

    /** `COUIEaseInterpolator`：PathInterpolator(0.42, 0, 1, 1) 的近似缓入曲线。 */
    val errorEase = CubicBezierEasing(0.42f, 0f, 1f, 1f)

    /** 错误色：日间 `coui_color_error`、夜间 `coui_color_error_dark`。 */
    @Composable
    fun errorColor(dark: Boolean = CouiTheme.isDark): Color =
        if (dark) Color(0xFFFF6C61) else Color(0xFFDB382C)

    /** 错误文案：12sp，与 `couiTextCaption` 同档。 */
    fun errorTextStyle(): TextStyle = TextStyle(fontSize = 12.sp)

    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiTextFieldColors {
        val theme = CouiTheme.colorSchemeFor(dark)
        // 禁用强度与页面禁用文字一致，避免同一页出现两种淡化程度。
        return CouiTextFieldColors(container = theme.card, text = theme.onSurface,
            placeholder = theme.onSurfaceVariant, label = theme.onSurfaceVariant,
            cursor = theme.accent, disabled = theme.onSurface.copy(alpha = .3f),
            error = errorColor(dark))
    }

    /**
     * 输入与占位提示共用的排版：16sp、系统无衬线、常规字重，行距与列表主文字一致。
     * 需要和输入框对齐的前缀、计数器等文字可使用同一样式。
     */
    @Composable
    fun textStyle(): TextStyle {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        return remember(configuration, density) {
            val weight = 400
            val typeface = Typeface.create(Typeface.create("sans-serif", Typeface.NORMAL), weight, false)
            val pixels = with(density) { 16.sp.toPx().roundToInt().toFloat() }
            couiTextStyle(typeface, weight, pixels,
                if (Build.VERSION.SDK_INT >= 35) 1.158f else 1.05f, includeFontPadding = true, density = density)
        }
    }
}

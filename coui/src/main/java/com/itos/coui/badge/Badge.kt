package com.itos.coui.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiTheme

/**
 * 角标固定参数，取自 ColorOS 17 手机管家内嵌的 `Widget.COUI.COUIHintRedDot`：
 * 红点直径 6dp、高度 16dp，数字宽度按位数取 16 / 20 / 26dp，字号 10dp，
 * 容器 `coui_color_additional_red`（日间 #EB3B2F、夜间 #EB493D），内容白色，圆角为全圆角胶囊。
 */
object CouiBadgeDefaults {
    val dotSize = 6.dp
    val height = 16.dp

    /** 一位、两位与三位以上数字的宽度。 */
    val singleDigitWidth = 16.dp
    val doubleDigitWidth = 20.dp
    val wideWidth = 26.dp

    /** 原厂字号以 dp 声明（`coui_hint_text_size`），不随系统字号缩放。 */
    val textSize = 10.dp

    /** 数字上限；超过后显示为 `上限+`。 */
    const val maxCount = 99

    /** 红点距锚点右上角的偏移：`coui_hint_red_dot_small_reddot_topend_margin` 1dp。 */
    val dotOffset = DpOffset(1.dp, 1.dp)

    /** 数字角标距锚点右上角的偏移：`coui_hint_red_dot_small_number_topend_margin_rectangle` 6dp。 */
    val numberOffset = DpOffset(6.dp, 6.dp)

    /** 容器色与内容色。 */
    fun colors(dark: Boolean): Pair<Color, Color> =
        Color(if (dark) 0xFFEB493D else 0xFFEB3B2F) to Color.White
}

/**
 * 角标。count 为 null 时显示红点，大于 0 时显示数字，小于等于 0 时不占位。
 * 宽度按位数取原厂三档，超过 maxCount 显示为 `maxCount+`。角标只表达状态，不处理点击；
 * 需要点击时由宿主把交互放在角标外层。
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    count: Int? = null,
    maxCount: Int = CouiBadgeDefaults.maxCount,
    dark: Boolean = CouiTheme.isDark,
    color: Color = CouiBadgeDefaults.colors(dark).first,
    contentColor: Color = CouiBadgeDefaults.colors(dark).second,
) {
    val value = count
    if (value == null) {
        Box(modifier.size(CouiBadgeDefaults.dotSize).clip(CircleShape).background(color))
    } else if (value > 0) {
        val label = if (value > maxCount) "$maxCount+" else value.toString()
        val width = when (label.length) {
            1 -> CouiBadgeDefaults.singleDigitWidth
            2 -> CouiBadgeDefaults.doubleDigitWidth
            else -> CouiBadgeDefaults.wideWidth
        }
        val textSize = with(LocalDensity.current) { CouiBadgeDefaults.textSize.toSp() }
        Box(
            modifier.width(width).height(CouiBadgeDefaults.height).clip(CouiCapsuleShape).background(color),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = label,
                style = TextStyle(
                    color = contentColor,
                    fontSize = textSize,
                    lineHeight = textSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            )
        }
    }
}

/**
 * 把角标叠在内容右上角。内容尺寸不因角标改变，角标可以超出内容边界。
 * 红点与数字角标离角落的距离不同，按角标形态传入 [offset]：红点用
 * [CouiBadgeDefaults.dotOffset]，数字用默认的 [CouiBadgeDefaults.numberOffset]。
 * RTL 下角标跟随起始侧翻转。点击和语义仍由 content 自己提供。
 */
@Composable
fun BadgedBox(
    modifier: Modifier = Modifier,
    badge: @Composable () -> Unit,
    offset: DpOffset = CouiBadgeDefaults.numberOffset,
    content: @Composable BoxScope.() -> Unit,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(modifier) {
        content()
        Box(
            Modifier.align(Alignment.TopEnd).offset(
                x = if (rtl) -offset.x else offset.x,
                y = -offset.y,
            ),
        ) { badge() }
    }
}

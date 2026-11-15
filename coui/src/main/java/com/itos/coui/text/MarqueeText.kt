package com.itos.coui.text

import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.theme.CouiTheme
import kotlinx.coroutines.delay
import kotlin.math.ceil

/**
 * 跑马灯文本的固定参数。
 *
 * 取自 ColorOS 17 的 `coui_top_tips_*` 资源，与原厂 `COUIMarqueeTextView` 的语义一一对应：
 * `coui_top_tips_scroll_text_interval` 是两次重复之间的空档宽度，
 * `coui_top_tips_scroll_speed` 是每秒滚动的距离，
 * `coui_top_tips_scroll_text_start_location` 是首帧的横向偏移，
 * `coui_top_tips_fading_edge_size` 是两端渐隐的长度。
 * [startDelayMillis] 对应原厂 `DEFAULT_SCROLL_DELAY_DURATION`，不是资源项。
 */
object CouiMarqueeTextDefaults {
    /** `coui_top_tips_scroll_text_interval`：相邻两次重复之间的空档。 */
    val interval = 80.dp

    /** `coui_top_tips_scroll_speed`：每秒滚动的距离。 */
    val speed = 25.dp

    /** `coui_top_tips_scroll_text_start_location`：首帧的横向偏移。 */
    val startLocation = 10.dp

    /** `coui_top_tips_fading_edge_size`：左右两端渐隐的长度。 */
    val fadingEdgeLength = 26.dp

    /**
     * 出现后到开始滚动的延迟，对应原厂 `DEFAULT_SCROLL_DELAY_DURATION` 与
     * 构造函数末尾的 `postDelayed(mStartScrollRunnable, 1000L)`。
     */
    const val startDelayMillis = 1000L
}

/**
 * 横向滚动的单行文本，对应原厂 `com.coui.appcompat.tips.COUIMarqueeTextView`。
 *
 * 行为与原厂一致：
 * - 只有当文本宽度超过可用宽度时才滚动；不超宽时按静态文本绘制，两端也不渐隐。
 * - 开始滚动前先静止显示 [CouiMarqueeTextDefaults.startDelayMillis]，对应原厂的起始延迟。
 * - 文本超出时按 [CouiMarqueeTextDefaults.interval] 的空档重复拼接，滚过一段后按同一空档回绕，
 *   视觉上是无缝的连续滚动。
 * - 滚动速度按 [CouiMarqueeTextDefaults.speed] 每秒推进，与刷新率无关；
 *   原厂按 `coui_top_tips_scroll_speed / refreshRate` 每帧推进，等价于每秒推进同样的距离。
 * - 左右两端按 [CouiMarqueeTextDefaults.fadingEdgeLength] 渐隐。
 * - 文本含 RTL 字符时滚动方向与 LTR 相反，与原厂按字符方向判定方向的规则相同。
 *
 * 原厂用 `TextView` 的 `StaticLayout` 判定字符方向，这里沿用同一判定来源；
 * 字体与字重取 [CouiTextKind]，字号按系统字体缩放换算，与库内其他文字一致。
 * 无障碍描述固定为原始文本，滚动期间不重复朗读拼接后的内容。
 *
 * @param enabled 是否滚动。关闭后按静态单行文本绘制，不滚动也不渐隐。
 */
@Composable
fun MarqueeText(
    modifier: Modifier = Modifier,
    text: String,
    kind: CouiTextKind = CouiTextKind.Body,
    color: Color = Color.Unspecified,
    enabled: Boolean = true,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val resolvedColor = color.takeOrElse { CouiTheme.contentColor }
    val paint = remember(kind, density, configuration) { marqueeTextPaint(kind, density, configuration) }
    val fadePaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val intervalPx = with(density) { CouiMarqueeTextDefaults.interval.toPx() }
    val speedPx = with(density) { CouiMarqueeTextDefaults.speed.toPx() }
    val startPx = with(density) { CouiMarqueeTextDefaults.startLocation.toPx() }
    val fadePx = with(density) { CouiMarqueeTextDefaults.fadingEdgeLength.toPx() }

    var containerWidth by remember { mutableIntStateOf(0) }
    // 原厂按单行规则处理文本，换行不在跑马灯路径内，这里同样压成一行。
    val singleLine = remember(text) { text.replace('\n', ' ') }
    val spaceWidth = remember(paint) { paint.measureText(" ") }
    val textWidth = remember(singleLine, paint) { paint.measureText(singleLine) }
    // 原厂空档由空格填充，取能覆盖目标宽度的整数个空格。
    val spaceCount = remember(intervalPx, spaceWidth) {
        if (spaceWidth <= 0f) 0 else ceil(intervalPx / spaceWidth).toInt() + 1
    }
    val unit = remember(singleLine, spaceCount) { singleLine + " ".repeat(spaceCount) }
    val unitWidth = remember(unit, paint) { paint.measureText(unit) }
    val rtl = remember(singleLine, paint) { marqueeHasRtl(singleLine, paint) }
    val scrolling = enabled && containerWidth > 0 && textWidth > containerWidth && unitWidth > 0f

    // 相位在 [0, unitWidth) 内推进；横向位移按方向取正负，与原厂 translate 的符号一致。
    var phase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(scrolling, unitWidth, speedPx) {
        if (!scrolling) {
            phase = 0f
            return@LaunchedEffect
        }
        // 原厂在构造时 postDelayed 1 秒再 continueRoll()，首屏先静止显示原文。
        delay(CouiMarqueeTextDefaults.startDelayMillis)
        var previous = 0L
        while (true) {
            withFrameNanos { time ->
                if (previous != 0L) {
                    val seconds = (time - previous) / 1_000_000_000f
                    phase = (phase + speedPx * seconds) % unitWidth
                }
                previous = time
            }
        }
    }

    // 单行布局不依赖可用宽度，高度只与字体有关；重复份数才与容器宽度相关。
    val singleLayout = remember(singleLine, paint) { marqueeLayout(singleLine, paint) }
    val heightPx = singleLayout.height
    val textLayout = remember(unit, containerWidth, scrolling, paint) {
        if (!scrolling) {
            singleLayout
        } else {
            // 多留两段，保证位移在 [-unitWidth, unitWidth] 内变化时可视区始终有内容。
            val repeats = ceil(containerWidth / unitWidth).toInt() + 2
            marqueeLayout(unit.repeat(repeats), paint)
        }
    }
    // 内容按 unitWidth 周期重复，因此位移回绕一个周期时画面完全一致，回绕点不可见。
    // LTR 与原厂一致：首帧右移 startLocation，随后向左滚动。
    // RTL 与原厂一致：滚动方向相反，因此位移向右推进。
    val offset = if (rtl) phase - startPx else startPx - phase

    Box(
        modifier
            .layout { measurable, constraints ->
                val width = if (constraints.hasBoundedWidth) constraints.maxWidth else textWidth.toInt()
                val placeable = measurable.measure(Constraints.fixed(width, heightPx))
                layout(width, heightPx) { placeable.place(0, 0) }
            }
            .onSizeChanged { containerWidth = it.width }
            .semantics { contentDescription = text },
    ) {
        androidx.compose.foundation.Canvas(
            Modifier.fillMaxSize(),
        ) {
            drawIntoCanvas { canvas ->
                val native = canvas.nativeCanvas
                paint.color = resolvedColor.toArgb()
                val checkpoint = native.save()
                if (scrolling) {
                    // 原厂 fading edge 在视图边界处把内容淡出，这里先离屏绘制，再用同一区间的 alpha 遮罩裁切。
                    native.saveLayer(0f, 0f, size.width, size.height, null)
                    native.translate(offset, 0f)
                    textLayout.draw(native)
                    if (rtl) {
                        // RTL 方向下位移可能为正，视图左侧会出现空档；内容按周期重复，
                        // 再画一份前一个周期的副本即可补满，与原厂回收首段的做法等价。
                        native.save()
                        native.translate(offset - unitWidth, 0f)
                        textLayout.draw(native)
                        native.restore()
                    }
                    val ratio = if (fadePx > 0f && size.width > fadePx * 2f) (fadePx / size.width).coerceAtMost(0.5f) else 0f
                    if (ratio > 0f) {
                        fadePaint.shader = android.graphics.LinearGradient(
                            0f, 0f, size.width, 0f,
                            intArrayOf(0x00000000, 0xFF000000.toInt(), 0xFF000000.toInt(), 0x00000000),
                            floatArrayOf(0f, ratio, 1f - ratio, 1f),
                            Shader.TileMode.CLAMP,
                        )
                        fadePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                        native.drawRect(0f, 0f, size.width, size.height, fadePaint)
                        fadePaint.xfermode = null
                        fadePaint.shader = null
                    }
                } else {
                    textLayout.draw(native)
                }
                native.restoreToCount(checkpoint)
            }
        }
    }
}

/**
 * 与原厂一致的字体：Kind 声明字族、字重与字号。
 *
 * 字重与库内 `rememberCouiSystemTextStyle` 一样先应用系统字重补偿，字号按 sp 换算。
 */
private fun marqueeTextPaint(kind: CouiTextKind, density: Density, configuration: Configuration): TextPaint {
    val adjustment = configuration.fontWeightAdjustment
    val weight = if (adjustment == Configuration.FONT_WEIGHT_ADJUSTMENT_UNDEFINED) kind.weight
        else (kind.weight + adjustment).coerceIn(1, 1000)
    val typeface = Typeface.create(Typeface.create(kind.family, Typeface.NORMAL), weight, false)
    // 与库内 couiTextStyle 一致：ItemValue 按 dp，其余按 sp 换算。
    val pixels = if (kind == CouiTextKind.ItemValue) kind.size * density.density
        else with(density) { kind.size.sp.toPx() }
    return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        textSize = pixels
    }
}

/**
 * 单行布局。原厂按 TextView 的单行规则处理文本，这里把换行替换为空格后按单行排版，
 * 宽度给足避免换行；绘制时的横向偏移由调用方平移。
 */
private fun marqueeLayout(text: String, paint: TextPaint): StaticLayout =
    StaticLayout.Builder.obtain(text, 0, text.length, paint, MarqueeLayoutWidth).build()

/** 单行排版的宽度上限；文本不会换行，宽度只影响截断，这里给足以保留全部字符。 */
private const val MarqueeLayoutWidth = 1 shl 20

/** 原厂用 StaticLayout 的字符方向判定 RTL，命中任意一个 RTL 字符即反向滚动。 */
private fun marqueeHasRtl(text: String, paint: TextPaint): Boolean {
    if (text.isEmpty()) return false
    val layout = marqueeLayout(text, paint)
    for (index in 0 until text.length) {
        if (layout.isRtlCharAt(index)) return true
    }
    return false
}

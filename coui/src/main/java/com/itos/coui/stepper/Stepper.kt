package com.itos.coui.stepper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.itos.coui.R
import com.itos.coui.effect.CouiMaskType
import com.itos.coui.effect.couiFocusOutline
import com.itos.coui.effect.couiStateMask
import com.itos.coui.input.CouiHapticFeedback
import com.itos.coui.input.isCouiTouchCancelled
import com.itos.coui.input.performCouiHapticFeedback
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.text.rememberCouiTextStyle
import com.itos.coui.theme.CouiTheme

/** 数值步进器的固定几何、范围与配色；加减图标与文案由组件参数覆盖。 */
object CouiStepperDefaults {
    /** 原厂 `ObservableStep` 允许的最小值与最大值，超出范围在构造时拒绝。 */
    const val minimum = -999
    const val maximum = 9999

    /** 原厂 `stepper_button_size`。 */
    val buttonSize = 32.dp

    /** 原厂 indicator 的最小宽度与左右留白。 */
    val indicatorMinWidth = 44.dp
    val spacing = 12.dp

    /** 原厂长按后连续加减的间隔。 */
    const val repeatIntervalMillis = 100L

    /** 加减按钮的背景是圆形，不随主题轮廓 token 变化。 */
    val shape: Shape = CircleShape

    /**
     * 原厂 `couiColorContainer8` 的日夜实值；主题里没有同名中性容器 token，
     * 与焦点描边一起固定在此，内容色与禁用色仍跟随 `CouiColorScheme`。
     */
    fun containerColor(dark: Boolean): Color = Color(if (dark) 0x1AFFFFFF else 0x14000000)

    /** 原厂 `couiColorFocusOutline`：`couiBlueTintColorFocusOutlineNormal` 的日夜值。 */
    fun focusOutlineColor(dark: Boolean): Color = Color(if (dark) 0x661875F5 else 0x66006AFF)

    /** 底色取 `couiColorContainer8`，内容色取 `couiColorPrimaryNeutral`，禁用色取 `couiColorDivider`。 */
    @Composable
    fun colors(dark: Boolean = CouiTheme.isDark): CouiStepperColors {
        val scheme = CouiTheme.colorSchemeFor(dark)
        return CouiStepperColors(
            container = containerColor(dark),
            content = scheme.onSurface,
            disabled = scheme.outlineVariant,
            focusOutline = focusOutlineColor(dark),
        )
    }
}

/** 加减按钮底色、数值与图标色、禁用色和焦点描边色。 */
@Immutable
data class CouiStepperColors(
    val container: Color,
    val content: Color,
    val disabled: Color,
    val focusOutline: Color,
)

/**
 * 数值步进器：减号、当前值与加号，对应原厂 `COUIStepperView`。
 *
 * `value` 由宿主持有，组件只提交变化后的值，且提交值已夹紧到 [valueRange]；到达上下界时
 * 对应按钮禁用。单击提交一次，长按后立即提交并按 100ms 连续提交，每次提交都触发原厂的
 * `GRANULAR_SHORT_VIBRATE_SYNC` 触感。标签文案、数值格式与业务校验由宿主负责。
 *
 * 进度展示不是本组件的职责，横向步骤条见 [StepBar]。
 */
@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange = CouiStepperDefaults.minimum..CouiStepperDefaults.maximum,
    step: Int = 1,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiStepperColors = CouiStepperDefaults.colors(dark),
    plusIcon: Painter = painterResource(R.drawable.coui_stepper_plus),
    minusIcon: Painter = painterResource(R.drawable.coui_stepper_minus),
    decreaseDescription: String = stringResource(R.string.coui_stepper_decrease),
    increaseDescription: String = stringResource(R.string.coui_stepper_increase),
) {
    require(valueRange.first >= CouiStepperDefaults.minimum && valueRange.last <= CouiStepperDefaults.maximum) {
        "数值范围必须落在 ${CouiStepperDefaults.minimum}..${CouiStepperDefaults.maximum}"
    }
    require(valueRange.first <= valueRange.last) { "数值范围的下界不能大于上界" }
    val current = value.coerceIn(valueRange)
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        StepperButton(
            enabled = enabled && current > valueRange.first,
            description = decreaseDescription,
            dark = dark,
            colors = colors,
            icon = minusIcon,
            onStep = {
                val next = (current - step).coerceIn(valueRange)
                if (next != current) onValueChange(next)
            },
        )
        Box(Modifier.padding(horizontal = CouiStepperDefaults.spacing)
            .width(indicatorWidth(valueRange.last))) {
            Text(text = current.toString(), kind = CouiTextKind.TopBar,
                color = if (enabled) colors.content else colors.disabled, maxLines = 1,
                modifier = Modifier.align(Alignment.Center))
        }
        StepperButton(
            enabled = enabled && current < valueRange.last,
            description = increaseDescription,
            dark = dark,
            colors = colors,
            icon = plusIcon,
            onStep = {
                val next = (current + step).coerceIn(valueRange)
                if (next != current) onValueChange(next)
            },
        )
    }
}

/**
 * 原厂 `onMeasure` 把指示器宽度固定为「0–9 中最宽数字重复最大值位数」的文本宽度，
 * 再与 44dp 最小宽度取大；宽度不随当前值变化，加减时数字不会左右跳动。
 *
 * 原厂用 `split("")` 取位数，实现会多切出一段空串，因此比字符数多一位；这里按原厂保留。
 */
@Composable
private fun indicatorWidth(maximum: Int) = with(LocalDensity.current) {
    val measurer = rememberTextMeasurer()
    val style = rememberCouiTextStyle(CouiTextKind.TopBar)
    val pixels = remember(measurer, style, maximum) {
        val widest = ('0'..'9').maxByOrNull {
            measurer.measure(it.toString(), style, softWrap = false).size.width
        } ?: '0'
        val count = maximum.toString().length + 1
        measurer.measure(widest.toString().repeat(count), style, softWrap = false).size.width
    }
    maxOf(pixels.toDp(), CouiStepperDefaults.indicatorMinWidth)
}

/**
 * 单个加减按钮：圆形 `couiColorContainer8` 底，原厂该处的 `COUIMaskEffectDrawable` 是类型 0，
 * 只有 hover 与 press 两层，焦点由 `COUIStrokeDrawable` 单独描边；按压形变复用 `CouiPressMotion`。
 *
 * 触摸语义对应原厂 `LongPressProxy`：抬手在控件内提交一次，超出点击区域或父容器接管则放弃；
 * 长按后立即提交并每 100ms 重复，抬手或取消时结束。
 */
@Composable
private fun StepperButton(
    enabled: Boolean,
    description: String,
    dark: Boolean,
    colors: CouiStepperColors,
    icon: Painter,
    onStep: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val press = rememberCouiPressMotion()
    val view = LocalView.current
    val currentOnStep by rememberUpdatedState(onStep)
    val step = {
        view.performCouiHapticFeedback(CouiHapticFeedback.GranularShortVibrateSync)
        currentOnStep()
    }
    Box(
        Modifier.size(CouiStepperDefaults.buttonSize)
            .graphicsLayer { scaleX = press.scale.value; scaleY = press.scale.value }
            .couiPressMotion(press, enabled, cancelOnConsumedMove = true)
            // 焦点描边画在按钮外侧, 必须在裁剪层之外。
            .couiFocusOutline(source, CouiStepperDefaults.shape, colors.focusOutline, enabled)
            .background(colors.container, CouiStepperDefaults.shape)
            .couiStateMask(source, dark = dark, enabled = enabled,
                type = CouiMaskType.HoverPress, shape = CouiStepperDefaults.shape)
            .pointerInput(enabled, source) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val feedback = PressInteraction.Press(down.position)
                    source.tryEmit(feedback)
                    var released = false
                    try {
                        val tapped = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                            awaitTouchEnd(down, cancelOutOfBounds = true)
                        }
                        if (tapped != null) {
                            released = tapped
                            if (tapped) step()
                        } else {
                            // 长按时段：立即提交一次，随后每 100ms 一次，抬手或取消收尾。
                            step()
                            while (true) {
                                val ended = withTimeoutOrNull(CouiStepperDefaults.repeatIntervalMillis) {
                                    awaitTouchEnd(down, cancelOutOfBounds = false)
                                }
                                if (ended != null) {
                                    released = ended
                                    break
                                }
                                step()
                            }
                        }
                    } finally {
                        source.tryEmit(if (released) PressInteraction.Release(feedback)
                        else PressInteraction.Cancel(feedback))
                    }
                }
            }
            .semantics {
                role = Role.Button
                contentDescription = description
                onClick(label = description) {
                    if (enabled) { step(); true } else false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(painter = icon, contentDescription = null,
            colorFilter = ColorFilter.tint(if (enabled) colors.content else colors.disabled),
            modifier = Modifier.size(CouiStepperDefaults.buttonSize))
    }
}

/**
 * 等到本次触摸结束。`cancelOutOfBounds` 为 true 时离开控件也算结束（对应原厂 `GestureDetector`
 * 的点击区域）；长按重复已开始后只用抬手、取消或父容器接管收尾，离开控件不结束。
 * 返回 true 表示正常抬手，false 表示取消或父容器接管。
 */
private suspend fun AwaitPointerEventScope.awaitTouchEnd(
    down: PointerInputChange,
    cancelOutOfBounds: Boolean,
): Boolean {
    val bounds: Rect? = if (cancelOutOfBounds) size.toSize().toRect() else null
    while (true) {
        val event = awaitPointerEvent()
        if (event.isCouiTouchCancelled()) return false
        val change = event.changes.firstOrNull { it.id == down.id } ?: return false
        if (!change.pressed) return !change.isConsumed
        if (change.isConsumed) return false
        if (bounds != null && !bounds.contains(change.position)) return false
    }
}

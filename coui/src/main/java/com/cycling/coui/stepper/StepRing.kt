package com.cycling.coui.stepper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.cycling.coui.theme.CouiTheme

/**
 * 环形步进徽标，对应 ColorOS 17 手机管家「应用内清理」页的
 * `com.oplus.phonemanager.clear.inappclear.InAppClearStepIndicatorView`。
 *
 * 一个 24dp 的细环里放当前步数：轨道用容器色，进度弧从 12 点方向顺时针覆盖
 * `step / total` 圈；走到最后一步时整圈改成实心进度色（原厂 `drawOval` 分支）。
 * 与横向的 [StepBar] 不是同一族——这里只表达「第几步 / 共几步」，没有标签与连线。
 *
 * [step] 夹在 `1..total`，`total` 至少按 1 处理；数字随 [size] 等比缩放
 * （原厂 `onSizeChanged` 用 `designTextSize * 高度 / designViewSize` 重算字号）。
 * 原厂字号来自资源 dp 并直接 `setTextSize`，所以**不跟随系统字体缩放**，这里保持一致。
 * 组件没有步进动画，改值即重绘。
 */
@Composable
fun StepRing(
    modifier: Modifier = Modifier,
    step: Int,
    total: Int,
    dark: Boolean = CouiTheme.isDark,
    size: Dp = CouiStepRingDefaults.size,
    strokeWidth: Dp = CouiStepRingDefaults.strokeWidth,
    colors: CouiStepRingColors = CouiStepRingDefaults.colors(dark),
) {
    val count = total.coerceAtLeast(1)
    val current = step.coerceIn(1, count)
    val scale = size.value / CouiStepRingDefaults.designSize.value
    val fontSize = with(LocalDensity.current) { (CouiStepRingDefaults.designTextSize * scale).toSp() }
    val textStyle = remember(colors.text, fontSize) {
        TextStyle(
            color = colors.text,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
        )
    }
    val measurer = rememberTextMeasurer()
    val label = current.toString()
    val layout = remember(label, textStyle) {
        measurer.measure(label, textStyle, maxLines = 1, softWrap = false)
    }
    Canvas(modifier.size(size).semantics { contentDescription = label }) {
        val stroke = strokeWidth.toPx()
        if (stroke <= 0f) return@Canvas
        val diameter = this.size.minDimension - stroke
        if (diameter <= 0f) return@Canvas
        val topLeft = Offset((this.size.width - diameter) / 2f, (this.size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)
        // 轨道是整圈 BUTT 描边；进度从 12 点方向顺时针，端部圆头（原厂 ROUND）。
        drawArc(colors.track, 0f, FullCircleDegrees, false, topLeft, arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt))
        if (current >= count) {
            drawOval(colors.progress, topLeft, arcSize, style = Stroke(width = stroke))
        } else {
            drawArc(colors.progress, StartAngleDegrees, FullCircleDegrees * current / count, false,
                topLeft, arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round))
        }
        drawText(
            layout,
            topLeft = Offset(
                (this.size.width - layout.size.width) / 2f,
                (this.size.height - layout.size.height) / 2f,
            ),
        )
    }
}

private const val FullCircleDegrees = 360f
private const val StartAngleDegrees = -90f

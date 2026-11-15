package com.cycling.coui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import com.cycling.coui.theme.CouiTheme

/**
 * 线性进度条。progress 为 0..1 时按比例填充，越界值先夹紧。
 * 轨道与进度共用同一胶囊形状，进度端部随容器轮廓收圆，不用矩形叠加再补角。
 * 省略 [color] 时取主题强调色，省略 [trackColor] 时取分隔线色，与环形进度保持同一取色规则。
 */
@Composable
fun LinearProgress(
    modifier: Modifier = Modifier,
    progress: Float,
    dark: Boolean = CouiTheme.isDark,
    color: Color = CouiTheme.colorSchemeFor(dark).accent,
    trackColor: Color = CouiTheme.colorSchemeFor(dark).outlineVariant,
    height: Dp = CouiProgressDefaults.linearHeight,
    shape: Shape = CouiTheme.shapes.capsule,
) {
    val value = progress.coerceIn(0f, 1f)
    CouiProgressTrack(modifier.fillMaxWidth().height(height), shape) {
        drawRect(trackColor)
        val width = size.width * value
        if (width > 0f) drawRect(color, size = Size(width, size.height))
    }
}

/** 一段占用：占整条轨道的比例与颜色。比例按 0..1 语义解释，不写像素宽度。 */
@Immutable
data class CouiProgressSegment(val fraction: Float, val color: Color)

/**
 * 分段线性进度条，用于存储占用这类由多项组成的构成条。
 * 各段按顺序从左向右累加，段间不留间隙；累计超过整条轨道后不再绘制剩余段。
 * 未占用的尾部保留 [trackColor]，不自动补齐最后一段。
 */
@Composable
fun SegmentedProgress(
    modifier: Modifier = Modifier,
    segments: List<CouiProgressSegment>,
    dark: Boolean = CouiTheme.isDark,
    trackColor: Color = CouiTheme.colorSchemeFor(dark).outlineVariant,
    height: Dp = CouiProgressDefaults.linearHeight,
    shape: Shape = CouiTheme.shapes.capsule,
) {
    CouiProgressTrack(modifier.fillMaxWidth().height(height), shape) {
        drawRect(trackColor)
        var left = 0f
        for (segment in segments) {
            val remaining = size.width - left
            if (remaining <= 0f) break
            val width = (size.width * segment.fraction.coerceIn(0f, 1f)).coerceAtMost(remaining)
            if (width <= 0f) continue
            drawRect(segment.color, topLeft = Offset(left, 0f), size = Size(width, size.height))
            left += width
        }
    }
}

/** 轨道与进度共用一次轮廓裁剪；形状轮廓同时决定可见范围和端部圆角。 */
@Composable
private fun CouiProgressTrack(
    modifier: Modifier,
    shape: Shape,
    content: DrawScope.() -> Unit,
) {
    Canvas(modifier) {
        val scope = this
        val path = Path().apply { addOutline(shape.createOutline(scope.size, scope.layoutDirection, scope)) }
        clipPath(path) { content() }
    }
}

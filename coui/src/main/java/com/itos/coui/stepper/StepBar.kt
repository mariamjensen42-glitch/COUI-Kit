package com.itos.coui.stepper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageColors
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 步骤条的圆点尺寸、连接线粗细与标签间距。 */
object CouiStepBarDefaults {
    val dotSize = 10.dp
    val lineWidth = 2.dp
    val labelSpacing = 8.dp
}

/**
 * 横向步骤条：圆点与连接线表示进度，已完成和当前步骤用强调色，后续步骤用分隔线色。
 *
 * [currentIndex] 由宿主持有，组件不推进进度、不处理点击。步骤数为 1 时只画圆点不画连线；
 * 标签在圆点下方居中，超长以省略号截断，不做横向滚动。原厂步骤条没有同名控件，
 * 数值加减的步进器见 [Stepper]。
 */
@Composable
fun StepBar(
    modifier: Modifier = Modifier,
    steps: List<String>,
    currentIndex: Int,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiPageColors = CouiPageDefaults.themeColors(dark),
) {
    require(steps.isNotEmpty()) { "步骤条至少需要一步" }
    val active = currentIndex.coerceIn(0, steps.lastIndex)
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(CouiStepBarDefaults.dotSize)) {
            Canvas(Modifier.fillMaxWidth().height(CouiStepBarDefaults.dotSize)) {
                if (steps.size <= 1) return@Canvas
                val segment = size.width / steps.size
                val y = size.height / 2f
                val start = segment / 2f
                val end = size.width - segment / 2f
                val stroke = CouiStepBarDefaults.lineWidth.toPx()
                drawLine(colors.divider, Offset(start, y), Offset(end, y), strokeWidth = stroke)
                if (active > 0) {
                    val reached = start + segment * active
                    drawLine(colors.accent, Offset(start, y), Offset(reached, y), strokeWidth = stroke)
                }
            }
            Row(Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, _ ->
                    val reached = index <= active
                    // 每段等分并居中, 圆心与上面 Canvas 的 segment/2 + segment*i 对齐。
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(CouiStepBarDefaults.dotSize).clip(CircleShape)
                            .background(if (reached) colors.accent else colors.divider))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            steps.forEachIndexed { index, label ->
                Text(text = label, kind = CouiTextKind.Body,
                    color = if (index <= active) colors.title else colors.summary,
                    textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(top = CouiStepBarDefaults.labelSpacing))
            }
        }
    }
}

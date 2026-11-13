package com.itos.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.progress.CircularProgress
import com.itos.coui.progress.CouiProgressDefaults
import com.itos.coui.progress.CouiProgressSegment
import com.itos.coui.progress.LinearProgress
import com.itos.coui.progress.SegmentedProgress
import com.itos.coui.progress.Skeleton
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiPresets
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

@Composable
internal fun ProgressSample(dark: Boolean) {
    var progress by rememberSaveable { mutableFloatStateOf(.35f) }
    var animated by rememberSaveable { mutableStateOf(true) }
    Group {
        DemoSliderPreference(title = stringResource(R.string.progress_value), value = progress,
            onValueChange = { progress = it }, valueText = "${(progress * 100).roundToInt()}%")
        Divider()
        DemoSwitchPreference(title = stringResource(R.string.progress_animated), checked = animated,
            onCheckedChange = { animated = it })
    }
    Group(title = stringResource(R.string.progress_circular)) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            CircularProgress(progress = progress)
            CircularProgress(modifier = Modifier.size(CouiProgressDefaults.loadingSize))
            CircularProgress(progress = 1f, modifier = Modifier.size(CouiProgressDefaults.circularMediumSize),
                strokeWidth = CouiProgressDefaults.circularMediumStrokeWidth)
        }
    }
    Group(title = stringResource(R.string.progress_linear)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LinearProgress(progress = progress)
            LinearProgress(progress = progress, height = CouiProgressDefaults.linearLargeHeight)
            // 存储占用构成条：各段按占比累加，尾部未占用部分保留轨道色。
            SegmentedProgress(segments = listOf(
                CouiProgressSegment(.42f, CouiTheme.colorScheme.accent),
                CouiProgressSegment(.1f, Color(0xFF9B5CF6)),
                CouiProgressSegment(.07f, Color(0xFFFF9500)),
                CouiProgressSegment(.14f, Color(0xFFB0B0B0)),
            ))
        }
    }
    Group(title = stringResource(R.string.progress_ring)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center) {
            // 局部绿色预设：只替换强调色，中性表面沿用当前深浅模式。
            CouiTheme(darkTheme = dark, colorScheme = CouiPresets.green(dark)) {
                val scheme = CouiTheme.colorScheme
                CircularProgress(
                    progress = progress,
                    size = 120.dp,
                    strokeWidth = 8.dp,
                    // 缺口中心落在 45°（右下），与手机管家首页分数环一致。
                    gapDegrees = 70f,
                    startAngleDegrees = 80f,
                    glow = true,
                    color = scheme.accent,
                    trackColor = scheme.accent.copy(alpha = .18f),
                    content = {
                        Text(text = "${(progress * 100).roundToInt()}", kind = CouiTextKind.Display,
                            color = scheme.onSurface)
                    },
                )
            }
        }
    }
    Group(title = stringResource(R.string.progress_skeleton)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Skeleton(Modifier.fillMaxWidth().height(16.dp), animated = animated)
            Skeleton(Modifier.fillMaxWidth(.6f).height(16.dp), animated = animated)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Skeleton(Modifier.size(48.dp), shape = CircleShape, animated = animated)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Skeleton(Modifier.fillMaxWidth().height(14.dp), animated = animated)
                    Skeleton(Modifier.fillMaxWidth(.4f).height(14.dp), animated = animated)
                }
            }
        }
    }
}

package com.cycling.coui.demo

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.progress.CircularProgress
import com.cycling.coui.progress.CouiInstallLoadProgressStyle
import com.cycling.coui.progress.CouiLoadProgressState
import com.cycling.coui.progress.CouiProgressDefaults
import com.cycling.coui.progress.CouiProgressSegment
import com.cycling.coui.progress.InstallLoadProgress
import com.cycling.coui.progress.LinearProgress
import com.cycling.coui.progress.SegmentedProgress
import com.cycling.coui.progress.Skeleton
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiPresets
import com.cycling.coui.theme.CouiTheme
import kotlin.math.roundToInt

/**
 * 分数环弧线的渐变端点，取手机管家首页 78 分环的沿弧采样结果：顶端 `#00D616`、底端 `#29CF28`。
 * 原厂只有饱和度沿竖直方向下降，色相与明度基本不变；端点随页面而异，故不放进库的默认值。
 */
private val ProgressScoreGradient = listOf(Color(0xFF00D616), Color(0xFF29CF28))

@Composable
internal fun ProgressSample(dark: Boolean) {
    var progress by rememberSaveable { mutableFloatStateOf(.35f) }
    var animated by rememberSaveable { mutableStateOf(true) }
    var installState by rememberSaveable { mutableStateOf(CouiLoadProgressState.Idle) }
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
                    // 沿弧的竖直渐变：顶端满饱和、底端偏淡，取手机管家首页 78 分环的实测端点。
                    arcGradient = ProgressScoreGradient,
                    trackColor = scheme.accent.copy(alpha = .18f),
                    content = {
                        Text(text = "${(progress * 100).roundToInt()}", kind = CouiTextKind.Display,
                            color = scheme.onSurface)
                    },
                )
            }
        }
    }
    Group(title = stringResource(R.string.progress_ring_score)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center) {
            CouiTheme(darkTheme = dark, colorScheme = CouiPresets.green(dark)) {
                val scheme = CouiTheme.colorScheme
                // 手机管家分数环：白色内圆填满环内孔，外环 189dp、描边 24dp、内孔 141dp。
                // 开 glow 时环按 circularGlowRingScale(0.82) 内收且 size 含光晕，
                // 故 size = 189 / 0.82 + 24 ≈ 225dp 才能得到 189dp 的环外径。
                // 白圆画在环之后：环内侧的发光会透到内孔里，压一层白圆才干净。
                Box(Modifier.size(225.dp), contentAlignment = Alignment.Center) {
                    CircularProgress(
                        progress = progress,
                        size = 225.dp,
                        strokeWidth = 24.dp,
                        gapDegrees = 70f,
                        startAngleDegrees = 80f,
                        glow = true,
                        color = scheme.accent,
                        arcGradient = ProgressScoreGradient,
                        trackColor = scheme.accent.copy(alpha = .18f),
                    )
                    Box(Modifier.size(141.dp).clip(CircleShape).background(Color.White))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${(progress * 100).roundToInt()}",
                            kind = CouiTextKind.Display, color = Color(0xFF1A1A1A))
                        Text(text = stringResource(R.string.progress_ring_unit),
                            kind = CouiTextKind.Body, color = Color(0xFF6B6B6B))
                    }
                }
            }
        }
    }
    Group(title = stringResource(R.string.progress_install)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                InstallLoadProgress(
                    progress = installProgress(installState),
                    state = installState,
                    text = stringResource(R.string.progress_install_text),
                    dark = dark,
                    onClick = { installState = nextInstallState(installState) },
                )
                InstallLoadProgress(
                    progress = installProgress(installState),
                    state = installState,
                    text = stringResource(R.string.progress_install_text),
                    style = CouiInstallLoadProgressStyle.Large,
                    dark = dark,
                    onClick = { installState = nextInstallState(installState) },
                )
            }
            Text(text = stringResource(R.string.progress_install_hint), kind = CouiTextKind.ItemSummary,
                color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant)
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

private fun installProgress(state: CouiLoadProgressState): Int = when (state) {
    CouiLoadProgressState.Idle -> 0
    CouiLoadProgressState.InProgress -> 40
    CouiLoadProgressState.Waiting -> 70
    CouiLoadProgressState.Failed -> 100
}

private fun nextInstallState(state: CouiLoadProgressState): CouiLoadProgressState = when (state) {
    CouiLoadProgressState.Idle -> CouiLoadProgressState.InProgress
    CouiLoadProgressState.InProgress -> CouiLoadProgressState.Waiting
    CouiLoadProgressState.Waiting -> CouiLoadProgressState.Failed
    CouiLoadProgressState.Failed -> CouiLoadProgressState.Idle
}

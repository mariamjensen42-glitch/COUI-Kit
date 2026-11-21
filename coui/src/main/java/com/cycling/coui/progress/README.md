# Progress

## Overview
COUI 进度与占位组件包：CircularProgress（确定/不确定环形）、LinearProgress/SegmentedProgress（线性与分段）、ScanRing（带旋转扫描色带的分数环）、InstallLoadProgress（安装下载进度按钮）、Skeleton（脉冲占位），以及 CouiProgressDefaults/CouiScanRingDefaults/CouiInstallLoadProgressDefaults 参数表。进度值、状态与文本由宿主传入；共享相位由 rememberCouiPhase 按帧推进。

## Interface
- `public @Composable fun CircularProgress(...)` - 环形进度，progress 为 null 时进入不确定态 `[CircularProgress.kt:CircularProgress]`
- `public @Composable fun LinearProgress(...)`、`public data class CouiProgressSegment`、`public @Composable fun SegmentedProgress(...)` - 线性、分段进度条 `[LinearProgress.kt:LinearProgress]` `[LinearProgress.kt:CouiProgressSegment]` `[LinearProgress.kt:SegmentedProgress]`
- `public @Composable fun InstallLoadProgress(...)`、`public enum class CouiInstallLoadProgressStyle`、`public enum class CouiLoadProgressState` - 安装/下载进度按钮与状态 `[InstallLoadProgress.kt:InstallLoadProgress]` `[CouiInstallLoadProgressDefaults.kt:CouiInstallLoadProgressStyle]` `[CouiInstallLoadProgressDefaults.kt:CouiLoadProgressState]`
- `public @Composable fun Skeleton(...)` - 内容占位块 `[Skeleton.kt:Skeleton]`
- `public @Immutable data class CouiInstallLoadProgressMetrics` / `CouiInstallLoadProgressColors`、`public object CouiInstallLoadProgressDefaults` - 安装进度尺寸/颜色与默认参数、colors(dark) `[CouiInstallLoadProgressDefaults.kt:CouiInstallLoadProgressMetrics]` `[CouiInstallLoadProgressDefaults.kt:CouiInstallLoadProgressColors]` `[CouiInstallLoadProgressDefaults.kt:CouiInstallLoadProgressDefaults]` `[CouiInstallLoadProgressDefaults.kt:colors]`
- `public object CouiProgressDefaults` - 环形/线性/骨架尺寸、周期与光晕常量 `[CouiProgressDefaults.kt:CouiProgressDefaults]`
- `public @Composable fun ScanRing(...)`、`public @Immutable class CouiScanRingPalette`、`public object CouiScanRingDefaults` - 分数环、角度渐变色板与默认参数（172dp 环、22dp 描边、70 分红线、2000ms 分数动画、5000ms 扫描一圈） `[ScanRing.kt:ScanRing]` `[CouiScanRingDefaults.kt:CouiScanRingPalette]` `[CouiScanRingDefaults.kt:CouiScanRingDefaults]`
- `internal @Composable fun rememberCouiPhase(active, periodMillis): State<Float>` - 不确定旋转与占位脉冲共用的 0..1 锯齿相位 `[CouiProgressMotion.kt:rememberCouiPhase]`

## Invariants
- CircularProgress 的 progress 非 null 时按 sweep*progress.coerceIn(0f,1f) 绘制；gapDegrees 夹紧到 0..350 且缺口以 startAngleDegrees 为中心 `[CircularProgress.kt:CircularProgress]`
- CircularProgress strokeWidth 夹紧到 0..minDimension/2；stroke 或 diameter <=0 时跳过绘制，绘制顺序为光晕、轨道、弧线 `[CircularProgress.kt:CircularProgress]`
- 不确定态 arcSweep=min(270f, 360-gap)，起点叠加 phase*360，周期 800ms `[CircularProgress.kt:CircularProgress]`
- glow 时环半径乘 0.82，size 表示含光晕整体尺寸；光晕 3 层，逐层加宽并衰减 `[CircularProgress.kt:CircularProgress]`
- InstallLoadProgress require max>0；progress 夹紧 0..max 后除以 max；仅 InProgress/Waiting 绘制填充，Failed 铺满 primary `[InstallLoadProgress.kt:InstallLoadProgress]`
- InstallLoadProgress 语义：contentDescription=text；仅 onClick!=null 设 Role.Button；showsProgress 时写 progressBarRangeInfo `[InstallLoadProgress.kt:InstallLoadProgress]`
- Small 样式在非 zh 语言下宽度加 wideLocaleExtraWidth；进度用弹簧，按下 200ms/抬手 340ms `[InstallLoadProgress.kt:InstallLoadProgress]`
- LinearProgress 的 progress 夹紧 0..1；SegmentedProgress 按顺序累加、段间无间隙、超出轨道停止绘制 `[LinearProgress.kt:LinearProgress]` `[LinearProgress.kt:SegmentedProgress]`
- Skeleton 系数=minFactor+(1-minFactor)*wave，animated=false 时 wave=1f `[Skeleton.kt:Skeleton]`
- rememberCouiPhase 仅 active 时用 withFrameNanos 推进，离开组合随 LaunchedEffect 取消 `[CouiProgressMotion.kt:rememberCouiPhase]`
- ScanRing 弧长 = score/100×360 从 90° 顺时针；扫描态用 `[0,1,2,2,3,2,1,0]` 镜像的八阶色板旋转，静止态用五阶色板平移 90°；停止时按剩余角度补完并夹在 500–5000ms `[ScanRing.kt:ScanRing]` `[CouiScanRingDefaults.kt:CouiScanRingDefaults]`
- ScanRing 转红立即生效，回绿按 600ms + 300ms 延迟插值颜色与停位；`animateScore=false` 时两者都直接落位 `[ScanRing.kt:ScanRing]`
- ScanRing 要求两套色板停位数量一致、首尾颜色相同、停位从 0 到 1，否则构造即失败 `[CouiScanRingDefaults.kt:CouiScanRingPalette]`

## Side Effects
- rememberCouiPhase 用 LaunchedEffect + withFrameNanos 写 mutableFloatStateOf，相位由绘制阶段读取，不产生逐帧重组 `[CouiProgressMotion.kt:rememberCouiPhase]`
- CircularProgress/LinearProgress/SegmentedProgress/Skeleton 在 Canvas/drawBehind 内绘制；线性与分段轨道经 `CouiProgressTrack` 的 `clipPath` 裁剪，Skeleton 用 `Modifier.clip` 裁剪 `[CircularProgress.kt:CircularProgress]` `[LinearProgress.kt:CouiProgressTrack]` `[Skeleton.kt:Skeleton]`
- InstallLoadProgress 使用 MutableInteractionSource/collectIsPressedAsState、animateFloatAsState、clickable(Role.Button) 与无障碍语义，读取 LocalConfiguration 语言并用 rememberTextMeasurer/drawText `[InstallLoadProgress.kt:InstallLoadProgress]`
- ScanRing 用三个 Animatable（分数、配色混合、扫描相位）与 snapshotFlow 触发 onScoreChange；Canvas 内用 Brush.sweepGradient + drawOval/drawPath 绘制，绘制阶段读取动画值 `[ScanRing.kt:ScanRing]`

## Verification
- Run: `./gradlew :coui:lintDebug`

# ⏳进度与占位

`CircularProgress` 表达一段可量化的进度或未知时长的等待，`LinearProgress` 用横条表达同一比例，`SegmentedProgress` 表达由多项构成的占用，`Skeleton` 在数据到达前占住布局。

## 环形进度

传入 0..1 的进度值按比例绘制，越界值先夹紧：

~~~kotlin
CircularProgress(progress = uploaded.toFloat() / total)
~~~

传 `null` 显示不确定态：一段固定弧长绕中心匀速旋转，用于无法给出比例的等待：

~~~kotlin
CircularProgress()
~~~

一圈为 800ms、线性匀速，取原厂 `COUISwitch` 加载分支的旋转资源（360° / 800ms / `LinearInterpolator`，见 `CouiSwitchLoadingMotion`）。弧长取 270°、缺口 90°，与同一套加载字形 `drawable/coui_icon_loading.xml` 的路径一致：从右侧顺时针画到下方，留出右下角缺口。原厂环形进度只有确定态，这一分支是按加载字形推演的。

两种状态都绘制轨道，轨道色默认取 `couiColorDivider`（日间 `#1F000000`、夜间 `#33FFFFFF`）。弧色按状态取：确定态是 `couiColorHintNeutral`（日间 `#42000000`、夜间 `#4DFFFFFF`），不确定态是主题强调色——原厂把两者分成 `COUICircularProgressBar` 与 `COUICircleProgressBar` 两个控件，前者用中性前景、后者用 `?couiColorPrimary`。只需要转圈、不需要轨道时传透明轨道色：

~~~kotlin
CircularProgress(
    modifier = Modifier.size(CouiProgressDefaults.loadingSize),
    color = contentColor,
    trackColor = Color.Transparent,
    strokeWidth = 2.dp,
)
~~~

默认尺寸取 `Widget.COUI.COUICircularProgressBar` 的 large 档：40dp、5dp 描边；medium 档为 30dp、3dp（`circularMediumSize` / `circularMediumStrokeWidth`），小号加载环为 18dp（`loadingSize`，对应 `coui_loading_view_default_length`）。`modifier` 中的尺寸约束优先，描边宽度会夹紧到半径以内。

### 大号读数环

需要把分数或容量放在环心时，用 `size` 放大环、用 `content` 提供中心内容，并用 `gapDegrees` 在起点前留出缺口：

~~~kotlin
CircularProgress(
    progress = score / 100f,
    size = 120.dp,
    strokeWidth = 8.dp,
    gapDegrees = 70f,
    startAngleDegrees = -55f,
    glow = true,
    color = scheme.accent,
    arcGradient = listOf(Color(0xFF00D616), Color(0xFF29CF28)),
    trackColor = scheme.accent.copy(alpha = .18f),
    content = { Text(text = score.toString(), kind = CouiTextKind.Display) },
)
~~~

`startAngleDegrees` 是进度起点，默认 `-90f`（正上方）；缺口位于起点之前，不参与比例换算，`gapDegrees` 夹紧到 `CouiProgressDefaults.maxCircularGapDegrees` 以内，保证轨道至少保留一段可见弧长。`glow` 在弧外叠加同色光晕，此时环半径按 `circularGlowRingScale` 内收，`size` 表示含光晕的整体尺寸，不是环外径。`content` 在环心居中绘制，不参与环的测量，也不会被轨道或光晕裁剪。

### 渐变弧

`arcGradient` 非空时弧线改用竖直渐变，第一项是环顶、最后一项是环底，`color` 退为光晕与取色基线。原厂分数环的弧正是竖直渐变：顶端满饱和、底端偏淡，色相与明度基本不变，只有饱和度下降——手机管家首页 78 分环沿弧采样时 R 通道 0→41、B 通道 16→40，G 通道稳定在 208–214。端点色随页面而异（首页 `#00D616` → `#29CF28`，清理存储 `#2ACB1B` → `#58C358`），所以端点不进入默认值，由调用方按页面给出。

渐变按环的上下外缘铺满描边带，换 `size` 或 `strokeWidth` 只改变长度、不改变两端颜色；端部仍是圆头，与原厂弧端收口一致。整圈（无缺口且进度为 1）直接按椭圆描边绘制，避免 `Path.arcTo` 在 360° 上退化成空路径。

### 分数环

手机管家首页那种「白色圆盘 + 外环」的分数环不单独提供组件，用 `CircularProgress` 叠一层实心圆即可——内圆尺寸取环的内径，两者正好贴合：

~~~kotlin
// 开 glow 时 size 含光晕且环按 circularGlowRingScale 内收，
// 要得到 189dp 的环外径，size 需取 189 / 0.82 + 24 ≈ 225dp。
Box(Modifier.size(225.dp), contentAlignment = Alignment.Center) {
    Box(Modifier.size(141.dp).clip(CircleShape).background(Color.White))
    CircularProgress(
        progress = score / 100f,
        size = 225.dp,
        strokeWidth = 24.dp,
        gapDegrees = 70f,
        startAngleDegrees = 80f,
        glow = true,
        color = scheme.accent,
        arcGradient = listOf(Color(0xFF00D616), Color(0xFF29CF28)),
        trackColor = scheme.accent.copy(alpha = .18f),
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = score.toString(), kind = CouiTextKind.Display, color = Color(0xFF1A1A1A))
                Text(text = "分", kind = CouiTextKind.Body, color = Color(0xFF6B6B6B))
            }
        },
    )
}
~~~

数值取自真机测量（872×1920 手机管家截图，2.2px/dp）：环外径 189dp、描边 24dp、内孔 141dp，三者满足 `189 - 2×24 = 141`，说明圆盘正好填满环内孔。缺口落在右下，对应 `startAngleDegrees = 80f`、`gapDegrees = 70f`。

**内圆尺寸必须按环内孔算，不能按环外径算**：开 `glow` 时环会按 `circularGlowRingScale`（0.82）内收，且 `size` 表示含光晕的整体尺寸，直接传 189dp 得到的环只有约 159dp 外径、111dp 内孔，白色圆盘会盖住环的内侧。圆盘与数字的配色是页面级的（原厂是白底深字），由调用方显式给出，不跟随深色主题——手机管家整页是深色背景，圆盘仍是白色。

## 线性进度

横条进度与环共用取色规则：省略 `color` 取主题强调色，省略 `trackColor` 取分隔线色。

~~~kotlin
LinearProgress(progress = used.toFloat() / total)
~~~

轨道与进度使用同一个胶囊形状，进度端部随容器轮廓收圆。默认高度 8dp，页面上的大号电量条用 `CouiProgressDefaults.linearLargeHeight`（12dp），也可以用 `height` 给出其他值。形状由 `shape` 覆盖，轮廓同时决定可见范围和端部圆角。

### 分段占用

存储占用这类由多项组成的构成条用 `SegmentedProgress`，每段给出占整条轨道的比例和颜色：

~~~kotlin
SegmentedProgress(
    segments = listOf(
        CouiProgressSegment(.42f, scheme.accent),
        CouiProgressSegment(.10f, Color(0xFF9B5CF6)),
        CouiProgressSegment(.07f, Color(0xFFFF9500)),
        CouiProgressSegment(.14f, Color(0xFFB0B0B0)),
    ),
)
~~~

各段按顺序从左向右累加，段间不留间隙；累计超过整条轨道后不再绘制剩余段，未占用的尾部保留轨道色，不自动补齐最后一段。比例是 0..1 的语义值，不写像素宽度，因此换宽度后构成关系保持不变。

## 占位块

`Skeleton` 在数据到达前占住布局，形状与高度由调用方给出：

~~~kotlin
Skeleton(Modifier.fillMaxWidth().height(16.dp))
Skeleton(Modifier.size(48.dp), shape = CircleShape)
~~~

默认是 8dp 圆角、16dp 最小高度。底色取 `color` 自身的透明度，动画只在其上按 `CouiProgressDefaults.skeletonMinFactor` 到 1 波动，因此宿主调整底色时对比度仍按比例保持。`animated = false` 时保持静止，适合列表整体滚动或截图场景。

## 动画与资源

两处动画都由 `withFrameNanos` 按帧推进相位，不创建 `rememberInfiniteTransition`，也不引入额外动画依赖。相位只在绘制阶段读取，不产生逐帧重组；组件离开组合后协程随 `LaunchedEffect` 取消，不残留计时。

进度与占位都是纯绘制组件，不读取业务状态，也不要求 `CouiBackdrop` 或 API 34。环形尺寸、描边与配色沿用原厂 COUI 进度资源，不确定态的周期与弧长沿用同一套加载字形；占位脉冲按同一组透明度区间推演。线性进度的 8dp 高度取手机管家电量条与存储占用条，大号 12dp 按同一比例放大；分段构成条与缺口环属于按页面形态扩展的分支，不对应原厂的独立控件。

## Demo 示例

环形、大号读数环、线性、分段与占位示例见 [DemoProgressSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoProgressSamples.kt)，库入口见 [CircularProgress](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/progress/CircularProgress.kt)、[LinearProgress](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/progress/LinearProgress.kt) 与 [Skeleton](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/progress/Skeleton.kt)。

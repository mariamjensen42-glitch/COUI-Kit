# ↕️竖向滑动条

`VerticalSeekBar` 是竖向的进度条，对应原厂 `COUIVerticalSeekBar`。底部为 0，顶部为 1，常用于音量、亮度一类竖排控制。

## 最小调用

进度由页面持有，拖动时更新：

~~~kotlin
var value by rememberSaveable { mutableFloatStateOf(0.4f) }

VerticalSeekBar(
    modifier = Modifier.height(200.dp),
    value = value,
    onValueChange = { value = it },
)
~~~

组件位于 `com.cycling.coui.seekbar`。

## 外观

- 轨道是细圆角竖条，进度自底部向上填充到滑块位置，滑块是覆盖在轨道上的圆点。
- 拖动开始后轨道与进度同时变宽、圆角同步放大，抬手后复原，对应原厂 `mBackgroundEnlargeScale` 与 `mProgressEnlargeScale`。
- 尺寸取自 `coui_vertical_seekbar_*` 与 `coui_seekbar_*` 资源，固定值在 `CouiVerticalSeekBarDefaults`。
- 按压变宽与回弹各 183ms，对应原厂 `TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION`。
- 配色由 `CouiVerticalSeekBarDefaults.colors(dark)` 给出：轨道取原厂 `couiColorContainer12`（日间 `#1F000000`、夜间 `#33FFFFFF`），进度与滑块取当前主题强调色（原厂 `couiColorContainerTheme` / `couiColorPrimary`），禁用态为强调色 30% alpha。宿主换主题时无需逐个传参，需要固定色时传入 `colors`。

## 文字标签

`label` 在轨道上方显示一行文字，对应原厂 `couiVerticalSeekBarText`：

~~~kotlin
VerticalSeekBar(
    modifier = Modifier.height(200.dp),
    value = value,
    onValueChange = { value = it },
    label = "${(value * 100).roundToInt()}%",
)
~~~

## 交互

- 拖动时进度跟随手指，位移按原厂 `calculateDamping()` 的 0.4 系数平滑收敛，手指抖动不会直接反映到进度上。
- 点击轨道按点击位置跳转，跳转时长随位移增大，最短 150ms。
- 抬手时速度超过 100px/s 才继续惯性滑动，衰减系数取原厂 `mFlingLinearDamping` 的 15。
- 进度每跨越 5% 档位触发一次触感反馈，对应原厂 `performFeedback()` 的分档节奏。

无障碍暴露进度范围与当前百分比；`enabled = false` 时进度与滑块切到禁用色，且不响应手势。

## 与原厂的差异

- 原厂抬手惯性由 `com.oplus.physicsengine` 的 `FlingBehavior` 驱动（频率 2.8、阻尼比 1.0、线性阻尼 15）。该实现不在公开 API 范围内，这里用同一阻尼系数的单轴衰减复现可见行为，到达边界即停止，不做原厂的边界回弹。
- 原厂支持形变（`couiVerticalSeekBarDeformation`）、线性马达分档震动与厂商平滑圆角路径，这些依赖厂商私有接口，这里只保留按压变宽与普通触感反馈。
- 原厂按下态还会把上下留白按 `mVerticalPaddingScale` 收敛，并给文字加阴影层；这里留白固定、文字无阴影。
- 原厂 `mShowText` 默认关闭，这里 `label` 非空即绘制；原厂 `couiVerticalSeekBarMaxHeight` 默认 0（不限高），这里固定 480dp 上限。

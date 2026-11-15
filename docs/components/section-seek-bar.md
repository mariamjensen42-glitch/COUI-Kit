# 🎚️分段与图标滑动条

`SectionSeekBar` 与 `IconSeekBar` 对应原厂 `COUISectionSeekBar` 和 `COUIIntentSeekBar` 的图标滑条：
前者按整数档滑动并在轨道上画段标记，后者是带图标面板的亮度 / 音量滑条。

## 分段滑动条

进度按 `valueRange` 的整数档取值，每一档在轨道上有一个标记：

~~~kotlin
var section by rememberSaveable { mutableIntStateOf(2) }

SectionSeekBar(
    value = section,
    onValueChange = { section = it },
    valueRange = 0..8,
)
~~~

- 轨道高 20dp、圆角 2dp，滑块半径 6dp，段标记半径 3dp，控件最小高度 36dp，
  取自 `coui_seekbar_background_height`、`coui_seekbar_background_radius`、
  `coui_seekbar_thumb_out_radius`、`coui_section_seekbar_tick_mark_radius` 与 `couiSeekBarMinHeight`。
- 滑块左侧的标记用未激活色（画在进度填充上），右侧用激活色（画在空轨道上），
  与原厂 `SectionProgressRenderer.drawMarks` 的分工一致；被滑块覆盖的标记跳过。
- 配色由 `CouiSectionSeekBarDefaults.colors(dark)` 给出：轨道 `?couiColorContainer12`，
  进度 `?couiColorContainerTheme`，滑块 `coui_color_white`，
  标记分别取 `coui_seekbar_active_mark_selector` 与 `coui_seekbar_inactive_mark_selector`。

交互按原厂 `SectionAnimationHandler` 移植：

| 行为 | 参数 |
| --- | --- |
| 拖动跟手 | 手指位移的 0.4 倍（`DRAG_MOVE_RATIO`），进度取最近档位 |
| 抬手吸附 | 150ms，`PathInterpolator(0, 0, 0.25, 1)`（`RELEASE_SNAP_DURATION`） |
| 点击跳转 | 200ms，`COUIMoveEaseInterpolator`（`PROGRESS_TRANSITION_DURATION`） |
| 档位触感 | 每次跨档一次分档震动 |

无障碍暴露整数进度范围与档位数，`enabled = false` 时切到禁用色且不响应手势。

## 图标滑动条

胶囊面板左侧是图标、右侧是细轨道，`type` 决定图标行为：

~~~kotlin
IconSeekBar(
    progress = brightness,
    onProgressChange = { brightness = it },
    type = CouiIconSeekBarType.Brightness,
)
~~~

- 面板高 32dp、`CouiIconSeekBarDefaults.iconSize` 24dp，图标中心距面板左缘 24dp，
  进度区自 44dp 起、到右侧 12dp 止，按原厂 408×96px 基准折算。
- `type = Brightness` 时太阳图标随进度旋转，每档 2°（对应原厂 `progress * 2`）。
- `type = Volume` 时图标按档位调整不透明度。
- 点击轨道按原厂时长过渡：`max(150, |Δ| / max * 483)` ms，曲线 `PathInterpolator(0.3, 0, 0.1, 1)`。
- 拖动阻尼取 `clamp(1 - |x - center| / center, 0.4, 1)`，越靠近中心越跟手。

配色由 `CouiIconSeekBarDefaults.colors(dark)` 给出：面板 `coui_icon_seekbar_background`，
未激活轨道 `coui_icon_seekbar_background_color_normal`，进度 `coui_icon_seekbar_def_progress_color`，
图标固定白色。

## 与原厂的差异

- 原厂 `effect/` 目录的 ConicLight、Glitter、Loading、Deformation 四条渲染链没有移植。
  它们只服务于 `COUIIntentSeekBar`：锥形光依赖私有 AGSL 绘制器 `IConicLightDrawer`，
  形变与惯性依赖 `com.oplus.physicsengine` 的弹簧与甩动引擎，均不在公开 API 范围内。
- 原厂 `SectionAnimationHandler` 在拖动过程中会按方向变化做 100ms 的中间吸附，
  这里拖动连续跟手，只在抬手时吸附。
- 原厂分段滑条的形变（`couiSeekBarDeformation`）、进度闪光（`couiSeekBarShowGlitterEffect`）
  与厂商平滑圆角路径依赖 `com.oplus.graphics`，未移植。
- 原厂图标滑条固定 408×96px 居中面板，这里面板铺满可用宽度、高度 32dp。
- 原厂音量档在 `ic_volume_seekbar_close/middle/open` 三张位图间切换，
  项目没有对应位图，统一用单个音量图标并按档位调整不透明度。
- 原厂 305/306 与线性马达触感码是厂商常量，这里映射为系统分档触感反馈。

## Demo 与源码

- Demo：`demo/src/main/java/com/itos/coui/demo/DemoSeekBarFamilySamples.kt`（目录项「Seek bar variants」）。
- 源码：[SectionSeekBar.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/seekbar/SectionSeekBar.kt)、
  [IconSeekBar.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/seekbar/IconSeekBar.kt)。

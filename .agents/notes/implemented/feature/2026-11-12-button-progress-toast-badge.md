# Agent Note: 按钮、进度、轻提示与角标的原厂基准与特化

Status: implemented

## Problem

组件目录缺少四类基础能力：提交操作的按钮、进度与占位、短暂结果反馈、未读角标与气泡说明。宿主只能自行用 `clickable`、`Canvas` 或第三方组件拼装，按压形变、加载态和退出计时各写一份。

首版实现以“无原厂基准、按现有风格设计”为前提，数值多为自定。随后取得 ColorOS 17 系统应用包，按资源重新对齐了按钮、环形进度与气泡。

## Decision

**按钮**对齐 `Widget.COUI.Button` / `Widget.COUI.Button.Large`：

- 高度 44dp、最小宽度 132dp、水平内留白 12dp、垂直 8dp。
- 标签 16sp `sans-serif-medium`、行距 1.2、最多两行。
- 形状按原厂自动圆角规则：宽高相等画圆，其他尺寸使用平滑胶囊。
- 配色：主按钮容器为强调色、内容白色、禁用容器强调色 77/255、禁用内容 0x28 白；次按钮容器 0xFFEBEBEB / 0xFF353535。
- 交互链与 `CouiDialogButton` 一致：`couiTouchMotion` + `couiPressMotion` + `rememberCouiSpotlight`（主 `OpaqueMedium1`、次 `OpaqueMedium2`）+ `couiButtonFeedback`。
- 文字外观为项目特化，原厂只有主、次分支。加载态原厂是三圆点（`coui_loading_btn_circle_radius` 1dp、`coui_loading_btn_circle_spacing` 2dp），这里用同色圆环承载，保留标签宽度。

**环形进度**对齐 `Widget.COUI.COUICircularProgressBar` 与 `Widget.COUI.COUICircleProgressBar`：

- large 档 40dp / 5dp 描边，medium 档 30dp / 3dp，小号加载环 18dp（`coui_loading_view_default_length`）。
- 弧色 `couiColorHintNeutral`（日间 `#42000000`、夜间 `#4DFFFFFF`），轨道 `couiColorDivider`（`#1F000000` / `#33FFFFFF`）。
- 不确定态原厂没有对应分支；周期取原厂开关加载的 360° / 800ms 线性旋转，固定弧长属于项目特化。原厂把确定态与加载态分成两个控件，加载环弧色为 `?couiColorPrimary`、底色 `?couiColorSecondary`，因此省略 `color` 时按状态取色：确定态中性前景、不确定态主题强调色。

**气泡**对齐 `COUIToolTips` 默认分支：容器 `?couiColorTips`（`#0080FF` / `#1A8CFF`），内容白色，四边留白 13dp，圆角 12dp（`couiRoundCornerM`）。定位、越界翻转与夹紧规则由本库实现。

**角标**对齐 `Widget.COUI.COUIHintRedDot`：红点直径 6dp、数字高度 16dp、宽度按位数取 16 / 20 / 26dp、字号 10dp（原厂以 dp 声明）、容器 `coui_color_additional_red`（`#EB3B2F` / `#EB493D`）、内容白色、全圆角胶囊。`BadgedBox` 的叠放位置由本库实现。

**轻提示**对齐 `coui_snack_bar_*`：COUI 没有独立 Toast，短暂底部反馈由 SnackBar 承担。容器日间 `#FFFFFF`、夜间 `#242424`，圆角 `coui_snack_bar_background_radius` 12.67dp，内留白水平 16dp / 垂直 10dp，文字 `coui_snack_bar_text_size` 14dp（原厂以 dp 声明）。原厂背景是带内阴影的九宫格图片、没有 elevation 资源，这里用平台投影承载分离感，数值为项目取值；计时、堆叠与无障碍实时区域由本库实现。

## Evidence

材料：`com.coloros.phonemanager_17.3.13-17003013_minAPI30_maxAPI37(arm64-v8a)(nodpi)_apkmirror.com.apk`，包名 `com.coloros.phonemanager`，versionName 17.3.13 / versionCode 17003013，minSdk 30、targetSdk 37，139,791,170 字节，SHA-256 `f1209b952de3e1c37a9fcfcf42d8bd40f2edf5fb1f7f90ba91220ac8e6319475`。

用 `apktool 3.0.3` 以 `-s`（仅资源）解包后，`res/` 内 `coui` 命中 21,833 处、COUI 样式 534 个。实际引用到的定义：

| 用途 | 资源 |
| --- | --- |
| 按钮样式 | `Widget.COUI.Button`、`Widget.COUI.Button.Large`、`couiTextAppearanceButtonL` |
| 按钮尺寸 | `coui_btn_text_size` 16sp、`coui_btn_large_height_min` 44dp、`coui_btn_large_width_min` 132dp、`coui_btn_padding_horizontal` 12dp、`coui_btn_padding_vertical` 8dp、`coui_spacing_multiplier_button_l` 1.2、`coui_btn_drawable_radius_large` 21.5dp |
| 按钮配色 | `?couiColorContainerTheme`、`?couiColorLabelOnColor` = `#FFFFFF`、`?couiBtnDefaultTextColorDisable` = `#28FFFFFF`、`?couiColorDisable` |
| 按钮加载 | `coui_loading_btn_circle_radius` 1dp、`coui_loading_btn_circle_spacing` 2dp、`drawable/coui_icon_loading.xml`（24dp、1.4dp 描边弧） |
| 进度样式 | `Widget.COUI.COUICircularProgressBar`、`Widget.COUI.COUICircleProgressBar` |
| 进度尺寸 | `coui_circular_progress_large_length` 40dp / `_stroke_width` 5dp、`_medium_length` 30dp / 3dp、`coui_loading_view_default_length` 18dp |
| 进度配色 | `coui_color_hint_neutral` `#42000000` / `#4DFFFFFF`、`coui_color_divider` `#1F000000` / `#33FFFFFF` |
| 气泡样式 | `COUIToolTips`、`drawable/coui_tool_tips_background.xml` |
| 气泡尺寸 | `coui_tooltips_default_background_padding_*` 13dp、`coui_round_corner_m` 12dp |
| 气泡配色 | `coui_tool_tips_background_color` → `?couiColorTips` → `coui_color_tips` `#0080FF` / `#1A8CFF` |
| 角标样式 | `Widget.COUI.COUIHintRedDot`、`.Small` |
| 角标尺寸 | `coui_dot_diameter` 6dp、`coui_height` 16dp、`coui_small/medium/large_width` 16/20/26dp、`coui_hint_text_size` 10dp |
| 角标配色 | `coui_color_additional_red` `#EB3B2F` / `#EB493D`、`coui_hint_red_dot_text_color` `#FFFFFF` |
| 轻提示样式 | `coui_snack_bar_background_radius` 12.67dp、`coui_snack_bar_child_margin_horizontal_*` 16dp / `_vertical` 10dp、`coui_snack_bar_text_size` 14dp |
| 轻提示配色 | `coui_snack_bar_background` `#FFFFFF` / `#242424`、`coui_snack_bar_background_shadow_color` `#ee000000` / `#99000000` |

材料缺口：COUI 没有 `couiToast*` 资源，轻提示按同族 SnackBar 取值；操作按钮、图标、多行分支已按 `coui_snack_bar_item.xml` 补齐，九宫格阴影按实测的外扩 14–20dp、峰值 5% 推演为 26dp 跨度 + 5% 黑色投影（原厂没有 elevation 资源）。

按压分支已用 jadx 1.5.6 单类反编译 `com.coui.appcompat.button.COUIButton`（1325 行）核实：

- `brightness`（`mMaxBrightness`）只被 attr 和 `setMaxBrightness` 写入，**类内从不读取**，是死属性，不需要实现按压缩放亮度。
- 按压视觉走 `e7.a` 蒙层：`getAnimatorColor()` 把蒙层颜色 `compositeColors(mask.B(), drawableColor)` 合成到容器色上，`animType == 1` 时生效。这与 `couiButtonFeedback` 的 hover/focus/press 蒙层同构，当前实现保留。
- `scaleEnable` 传给触摸形变动画器（`cOUITouchMotionAnimator.F(isEnabled() && mScaleEnable)`），默认开启，对应 `couiTouchMotion`，当前实现保留。

因此按钮的按压链路不需要按 `brightness` 改写；此前记录的“待核实”项到此关闭。

本次仍未运行 Gradle 构建与设备操作，只做了资源静态核对。

## Alternatives considered

- 沿用首版自定数值（40dp 高度、20dp 留白、24dp 进度环、深灰气泡）：与取得的原厂资源不一致，同一套 UI 里会出现两种按钮尺寸、两种气泡配色。
- 用圆环承载按钮加载：代码更少，但原厂 `Widget.COUI.Button` 明确用 `coui_loading_btn_circle_radius` 1dp / `_spacing` 2dp 的三圆点并隐藏文字；按原厂改为三点脉冲。
- 让按钮复用 `couiFloatingSurface`：材质更贴近顶栏与 FAB，但要求 `CouiBackdrop` 与 API 34，普通表单按钮无法接受。
- 进度改用 `rememberInfiniteTransition`：代码更短，但会引入与现有自绘动画并行的第二套机制。
- Tooltip 内建长按：`detectTapGestures` 会消费子节点点击，与 `Item` 的整行交互冲突。
- Toast 使用 `Popup` 或 `WindowManager`：可跨页面存活，但把窗口生命周期带进组件，超出页面内反馈的职责。

## Consequences

按钮、环形进度与气泡现在与 ColorOS 17 应用内嵌的 COUI 资源一致；同一页面不会出现两种操作按钮高度或两种气泡底色。

剩余代价：轻提示按 SnackBar 取值、阴影按九宫格实测参数推演为平台投影；气泡需要宿主自行装配触发手势；按钮触摸区只有 44dp，紧凑布局需宿主自行留白；`Widget.COUI.Button.Large` 的 21.5dp 圆角与原库胶囊几何相差 0.5dp，当前保留胶囊。

原厂没有对应分支的细节（按钮三点的脉冲曲线、不确定态弧长、占位脉冲、轻提示投影）按同族资源推演：三点与不确定态弧长沿用 `coui_icon_loading` 的 270° 字形与 800ms 周期，投影沿用九宫格实测的外扩与峰值不透明度。

使用说明见[按钮](../../../../docs/components/button.md)、[进度与占位](../../../../docs/components/progress.md)、[轻提示](../../../../docs/components/toast.md)、[角标与气泡](../../../../docs/components/badge-tooltip.md)。

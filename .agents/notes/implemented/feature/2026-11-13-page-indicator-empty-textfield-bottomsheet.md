# Agent Note: 页码指示器、空状态、输入框与底部面板的移植边界

Status: implemented

## Problem

对 ColorOS 17 手机管家 17.3.13 的解包资源做组件盘点后，发现项目缺少四类有明确原厂定义的组件：页码圆点、空状态、带浮动提示的输入框、底部面板。其余 COUI 控件族（Chip、CheckBox、Switch、SeekBar、AlertDialog、PopupWindow、Toolbar、SearchBar、NavigationView、CalendarPicker）已有对应实现。

## Decision

四个组件都按原厂资源取值，公开入口保持受控、不接管业务：

- **PageIndicator**：纯 Canvas 绘制，不安装手势、不提交翻页。直径按与当前页的距离取 6 / 4.5 / 2.5dp，间距 3dp；选中色取 `coui_color_label_primary`、未选中取 `coui_color_hint_neutral`，都按透明度从 `onSurface` 派生，宿主换色板时对比度保持。
- **EmptyState**：图标槽 120×100dp、文字列 280dp 居中、说明距标题 2dp、操作槽 8dp、标题 2 行、说明 3 行。图标与操作都是内容槽，组件不内置插图、不处理点击。
- **TextField**：对应 `Widget.COUI.EditText.HintAnim.Rectangle`。10dp 圆角描边容器、最小高度 62dp、内留白 16/10/20.4/12dp（`paddingTop` 按原厂 `getModePaddingTop()` = `rectModePaddingTop + 文字行高/2`），内容垂直居中；`placeholder` 是框内浮动标签，空且未聚焦时与输入文字同位，聚焦或有内容后上浮 13.65dp 并缩到 10sp。描边常态 0.33dp、聚焦与错误 1dp。矩形模式在原厂由 `COUIEditText` 自绘 `GradientDrawable` 风格描边（`setStroke` + `setCornerRadii`，无填充），`coui_edittext_default_background*` 那组 9patch 是下划线分支用的。
- **BottomSheet**：`Dialog` 承载，`gravity=BOTTOM`、关闭系统压暗与窗口动画、`SOFT_INPUT_ADJUST_NOTHING`；遮罩与滑入自绘，退场动画结束后才让窗口离开组合，中途反向从当前进度继续。面板只作为命中目标挡住遮罩，不消费事件，子节点交互不受影响。

## Evidence

材料：`com.coloros.phonemanager_17.3.13-17003013_minAPI30_maxAPI37(arm64-v8a)(nodpi)_apkmirror.com.apk`，versionName 17.3.13 / versionCode 17003013，targetSdk 37，SHA-256 `f1209b952de3e1c37a9fcfcf42d8bd40f2edf5fb1f7f90ba91220ac8e6319475`，`platformBuildVersionName=17`。

| 组件 | 主要资源 |
| --- | --- |
| PageIndicator | `Widget.COUI.COUIPageIndicator(.Dark)`、`coui_page_indicator_dot_size` 6dp / `_medium` 4.5dp / `_small` 2.5dp、`_spacing` 3dp、`_stroke_width` 1dp |
| EmptyState | `layout/coui_component_empty_state.xml`、`coui_component_empty_anim_view_width_normal` 120dp / `_height_normal` 100dp、`coui_component_empty_group_width` 280dp、`_view_subtitle_margin_top` 2dp |
| TextField | `Widget.COUI.EditText.HintAnim.Rectangle`（paddingLeft 16dp / paddingRight 10dp / rectModePaddingTop 12dp / paddingBottom 12dp）、`coui_textinput_corner_radius` 10dp、`coui_edit_text_height_min` 62dp、`coui_textinput_hint_text_size` 10sp、`coui_textinput_stroke_width` 0.33dp / `_focus_stroke_width` 1dp、`coui_edittext_default_background` 描边选择器 |
| BottomSheet | `coui_round_corner_xxxxl` 32dp、`coui_bottom_sheet_dialog_elevation` 17dp、`coui_bottom_sheet_margin_top_default` 40dp、`_margin_bottom_default` 64dp / `_with_navigationbar` 8dp、`coui_color_mask`、`coui_panel_drag_view` 36×4dp |

### dex 复核（jadx 1.5.6，单类反编译）

对 `com.coui.appcompat.indicator.COUIPageIndicator`、`com.coui.appcompat.edittext.COUIEditText`、`com.coui.appcompat.panel.COUIBottomSheetBehavior` 与 `com.coui.appcompat.animation.{b,c,e}` 做单类反编译后确认：

- **PageIndicator**：`onMeasure` 用 `(dotSize + 2 × dotSpacing) × min(6, count)`，即每个点两侧各留一个 spacing、最多排版 6 个点。点的半径不是离散三档：按 `|index - currentPosition|` 落在四段距离带（0–3、3–5、5–7、7–9），用 `COUIEaseInterpolator`（PathInterpolator 0.33, 0, 0.67, 1）在 `[dotSize/2, medium/2, small/2, 0]` 相邻档之间连续插值，超出最后一段按 0。点击走整视图 `onTouchEvent` → `callOnClick()`，`dotClickable` 默认 true 且需要 `OnDotClickListener` 才生效。
- **COUIEditText**：提示上浮用 `COUIMoveEaseInterpolator`（PathInterpolator 0.3, 0, 0.1, 1）+ **200ms**；焦点线用 `COUIInEaseInterpolator`（PathInterpolator 0, 0, 0.1, 1）+ **250ms**。
- **COUIBottomSheetBehavior**：`mIsHandlePanel` 只影响 `expandedOffset`（手柄面板贴合 fitToContents）；跟手是 `COUIPanelPercentFrameLayout` 的 ratio 定位，不是独立手势分支。

据此修正了实现：PageIndicator 的点间距改为 `2 × spacing`、增加 6 点上限；TextField 的提示与焦点线拆成两组时长/曲线（原先统一 250ms + 0,0,0.1,1）。PageIndicator 的连续插值未还原，仍用离散三档近似。

### 第二轮：全部对齐原厂

第一轮遗留的近似项按 dex 结论重做：

- **PageIndicator**：`dexdump -d` 反汇编 `COUIPageIndicator$c` 后确认 jadx 的 `{0, f, f, f-…}` 是误读，实际起点数组为 `{0, -1, -2, -3}`、带宽 `{3, 5, 7, 9}`、档位 `{dotSize/2, medium/2, small/2, 0}`，`mMaskOffset = 当前位置 - 首段带宽`（当前点固定在 `d = 3`），`count < 6` 时首段带宽放宽到 5。整数距离上退化为：`d ∈ [0,3]` 满半径、`4` 4.5dp、`5` 2.5dp、`6` 0，负向对称。因此 5 个点及以下所有点满半径、只靠颜色区分当前页，6 个点起窗口随当前页滑动。已按此重写 `radiusAt` / `layoutDots`，并新增 `onDotClick`（100ms tapTimeout、外接方块优先、否则取最近点、不消费事件）。
- **EmptyState**：新增 `CouiTextKind.Body`（12sp regular，对应 `couiTextBodyXS`）与 `ButtonLabel`（14sp medium，对应 `couiTextButtonM`），说明改用 12sp；操作槽新增 `actionLabel` + `onAction`，内部渲染 14sp 文字按钮（无容器、无按压蒙层）。
- **轻提示**：补齐 SnackBar 的操作按钮（14sp、最大 110dp、右 4dp）、图标槽（30dp、左 16dp）、多行留白（上 12 / 下 8dp），文案改为 14dp medium、起始对齐；`maxWidth` 改为 `coui_snack_bar_max_width` 328dp。阴影按九宫格实测参数推演：外扩 14–20dp、峰值不透明度约 5%、`coui_snack_bar_shadow_size` 26dp，因此改用 26dp 跨度配 5% 黑色投影。
- **BottomSheet**：补拖拽跟手与下拉关闭。结算按 `dragCallback.onViewReleased`：`vy < 0` 回展开、`vy > 500 && |vy| > |vx|` 收起、拖过面板一半收起、其余回弹；与内容滚动按 `onNestedPreScroll` 的先后顺序共存。面板按 `coui_bottom_sheet_dialog_follow_hand_margin_left` / `_right` / `_bottom` 四周离屏 8dp。
- **Button**：反编译 `COUIButton` 后确认 `brightness`（`mMaxBrightness`）只写不读，是死属性；按压走 `e7.a` 蒙层合成，与现有 `couiButtonFeedback` 同构，代码不需要改。

## Alternatives considered

- PageIndicator 支持点击跳页（原厂 `dotClickable=true`）：会让组件承担翻页语义，与宿主分页容器重复；保持只读，点击交给宿主。
- EmptyState 内置插图：原厂是 `EmptyStateAnimView` 动效占位，复用需要额外资源与动效实现；改为内容槽。
- EmptyState 保留原厂 92dp 滚动容器：组件内滚动会与宿主页面滚动冲突，改用行数上限收敛高度。
- TextField 直接复用 `InputDialog` 的输入实现：弹窗输入没有下划线与浮动提示，且绑定了弹窗布局；只复用其文字度量方式。
- BottomSheet 用 `Popup` 而非 `Dialog`：`Popup` 没有独立窗口，遮罩、返回键与 IME 行为都要自己兜，且层级受宿主窗口限制。

## Consequences

四个组件与已有按钮、进度、角标、气泡共用同一套原厂数值，页面内不再出现两套输入框或两种指示器尺寸。

已知差异与待核实项（按原厂资源逐项核对后的结论）：

- PageIndicator 的静止态半径插值、点击与 RTL 镜像已还原；未实现的是滚动过程中的药丸/融合路径（`Path.Op` 合并）、`ArgbEvaluator` 颜色过渡与 `SpringAnimation` 跟手（组件只接受整数页码，没有滚动偏移输入），以及无障碍播报、`SavedState` 与动态增删点。
- EmptyState 说明 12sp（`couiTextBodyXS`）、操作 14sp medium（`couiTextButtonM`）已按原厂落地，标题复用 `ItemTitle`（16sp medium，数值与 `?couiTextHeadlineXS` 一致，仅角色名不同）。原厂标题与说明宽度是固定 280dp，当前用 `widthIn(max)`，短文本时列宽更窄但居中显示一致。图标槽固定 120×100dp，原厂布局本身是 `wrap_content`，该尺寸来自 `EmptyStateAnimView` 的固有值。
- TextField 聚焦色用库强调色，原厂 `?couiColorPrimary` 在 Theme.COUI 指向绿色 `#00BD13` / `#24B232`；`?couiColorSecondNeutral` 未在资源中解析到具体色值，用 `onSurfaceVariant` 近似；错误文字 12sp 与提示动画曲线为推断值。原厂容器无填充、靠 9patch 描边，这里用 `Modifier.border` 按 0.33dp / 1dp 复刻；原厂的删除按钮、字数计数与密码可见性开关没有实现，`endPadding` 仍按原厂 10dp 保留。
- BottomSheet 的拖拽跟手、按速度与位移结算、下拉关闭已实现，面板四周按 `coui_bottom_sheet_dialog_follow_hand_margin_*` 留 8dp；未移植 half/collapsed/peek 三档、`ratio`/`hasAnchor` 换算、pull-up 监听与 IME 100dp 半屏阈值。圆角取 32dp 已复核：`coui_default_panel_bg_without_shadow` 用 `?couiRoundCornerXXXXL`、`bg_panel_os17` 用 `bg_panel_corner_radius`，两者都是 32dp；`coui_bottom_sheet_bg_top_corner_radius` 的 28dp 属于另一套带阴影/跟手的面板背景。
- 四个组件都未运行 Gradle 构建与设备验证，只做了资源静态核对与跨文件签名检查。

使用说明见[页码指示器](../../../../docs/components/page-indicator.md)、[空状态](../../../../docs/components/empty-state.md)、[输入框](../../../../docs/components/text-field.md)、[底部面板](../../../../docs/components/bottom-sheet.md)。

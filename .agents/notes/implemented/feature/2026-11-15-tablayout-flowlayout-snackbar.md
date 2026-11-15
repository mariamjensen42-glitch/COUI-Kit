# Agent Note: 页签条、换行搜索历史与 Snackbar 的装配边界

Status: implemented

## Problem

[覆盖对照](../../../../docs/status/coverage.md)的「下一轮」三项此前没有实现：页签条缺少跟随文字宽度的滑动指示器，`FlowLayout` 没有换行与折叠，Snackbar 只有 `ToastHost` 的队列语义（最多三条堆叠、固定两档时长、位置全交给宿主）。

难点不在三个控件本身，而在已有资产：Snackbar 的容器几何、排布和配色已经完整落在 `Toast.kt` 里；`ChipTab` 看起来能当搜索历史的标签；`CouiSpring` 看起来能拿去做页签指示器。若直接复制或改坏既有消费者，后续每加一个同类控件都会多一份几何或状态机。

## Decision

三个组件都是纯布局、文字与状态装配，不复制模糊、材质、轮廓光、阴影或融合后端：

- [TabLayout](../../../../coui/src/main/java/com/cycling/coui/tablayout/TabLayout.kt) 在组合阶段用 `TextMeasurer` 得到文字宽度，据此计算等分/滚动宽度、指示器几何与滚动终点；指示器用外层 `Canvas` 按内容坐标减 `scrollState.value` 绘制。**不在 layout 阶段写 State**，也不为拿测量值引入 `SubcomposeLayout`。切换时长取原厂 `getIndicatorAnimTime()`。
- [FlowLayout](../../../../coui/src/main/java/com/cycling/coui/flowlayout/FlowLayout.kt) 用同一组测量值按 `Line.canAdd()` 分行，折叠时超出的行仍在布局中、靠 alpha 与 `clipToBounds` 裁剪，容器高度随展开态立即切换。[SearchHistory](../../../../coui/src/main/java/com/cycling/coui/searchhistory/SearchHistory.kt) 只装配标题栏、清空淡出和 `FlowLayout`，**不复制换行逻辑**。
- [SnackbarHost](../../../../coui/src/main/java/com/cycling/coui/snackbar/Snackbar.kt) 保持原厂 `COUISnackBar.make()` 的单实例互斥：`show()` 在已有提示时返回 `false`，排队交给宿主在 `onDismissed` 提交。计时按原厂 `onTouchEvent()`，DOWN/MOVE 暂停、UP/CANCEL 按完整时长重启；`durationMillis = 0` 不自动消失。进出场复用 `CouiSpring`，几何复用从 `Toast.kt` 抽出的 `internal CouiToastSurface`。

`CouiToastSurface` 是**同一绘制节点的两处消费**，不是转发参数的包装层：`ToastHost` 仍各自持有队列、计时与退出动画，公开行为不变；`SnackbarHost` 持有自己的弹簧、计时与互斥状态。两者不得合并成一个带模式开关的 State，否则「单实例互斥」与「最多三条堆叠」会互相牵制。

## 原厂依据与复用核查

依据集中在 `references/apk/jadx/settings/classes{,7}/sources/com/coui/appcompat/` 的 `tablayout/`、`searchhistory/`、`snackbar/` 与 `references/apk/settings/decoded/res/values/{styles,dimens}.xml`，逐条静态核查见[覆盖对照的双向核查](../../../../docs/status/coverage.md)。

检查过的候选与结论：

| 候选 | 结论 |
| --- | --- |
| `CouiToastSurface`（由 `Toast.kt` 抽出） | 复用。几何、排布、配色、九宫格投影参数完全相同，复制会形成第二份 `coui_snack_bar_*` 实现。 |
| `CouiSpring` | 复用。原厂 `COUISpringForce` 的 response/bounce 与本库 `response → (2π/response)²` 同源，不需要新弹簧。 |
| `ToastHostState` 加 `mode` 开关 | 不复用。队列语义是公开契约，改动会影响现有消费者。 |
| `ChipTab` / `ChipTabRow` | 不复用。它们带背景源、renderer 与 API 34 门槛，搜索历史的 `Widget.COUI.Chip.Suggestion` 是静态标签，不是同一分支。 |
| `CouiScrollPhysics` / `SegmentedControl` 的滑块 | 不复用。前者是越界弹性滚动，后者是胶囊单选，都不是 `COUISlidingTabStrip` 的指示器动画。 |
| `Scaffold` 增加 Snackbar 槽 | 不做。[可选轻量页面容器](../architecture/2026-11-03-optional-coui-scaffold.md)明确不增加这类槽位；Snackbar 由宿主用 `Box` 装配。 |

`FlowLayout`、`TabLayout` 各自新增布局与绘制代码，是因为现有组件里没有对应的换行裁剪与指示器几何，属于「算法分支无法复用」而非参数不足；两者的 Defaults 都归自己的组件族。

## Alternatives considered

- 给 `ToastHostState` 增加 `singleInstance` / `durationMillis` / `pausable` 开关：一份 State 同时表达两种互斥语义，队列与计时路径互相污染；已有 Demo 与文档都会被迫理解模式差异。改为各持一份状态、共用绘制节点。
- 把 Snackbar 几何复制到 `snackbar` 包：省一次 `Toast.kt` 改动，但 `coui_snack_bar_*` 的排布、多行留白与投影参数出现两份实现，后续修一处就会漏另一处。
- 搜索历史用 `ChipTabRow` 套一层：需要传入 `CouiBackdrop`、依赖 API 34 浮动材质，还会把分类单选语义带进历史词；原厂这里是普通 Suggestion chip。
- 页签指示器用 `onGloballyPositioned` 收集每个页签坐标：坐标换算依赖窗口/父级坐标系，RTL 与滚动时容易错位；改用组合阶段测量 + 同一套宽度公式，指示器与页签共享一份几何。
- 用 `SubcomposeLayout` 先测页签再 subcompose 指示器：能拿到真实 Placeable 宽度，但为一条 2dp 矩形引入额外组合层级，且在动画帧里反复 subcompose；文字测量已足以复现原厂的 `getTextView().getWidth()` 口径。

## Consequences

- 收益：三项缺口落地，Snackbar 与轻提示共用一份几何；三个组件都不依赖背景源、OEM API 或 API 34，公开轻提示行为不变。
- 代价：`Toast.kt` 多了一个 `internal` 共用入口；Snackbar 的位置由宿主 `Box` 决定（Compose 没有等价的父容器查找）；页签只接受整数下标，没有 ViewPager 拖动中的指示器插值；搜索历史标签没有原厂 chip 的按压反馈与字号适配。差异逐项写在各自使用页与覆盖对照。
- 验证范围：只做了源码、资源与调用链的静态双向核查，以及资源、导航、链接与编码的一致性自查；Kotlin 类型与 import 解析以宿主编译为准。按[验证与评审](../../../../docs/development/validation.md)，未执行 Android 构建、Lint、Preview、安装或真机操作；指示器文字色过渡、标签外观、弹簧观感、触摸暂停计时、RTL、深浅色与无障碍播报均待真机验收。

## Existing-note audit

- [按钮、进度、轻提示与角标](2026-11-12-button-progress-toast-badge.md)：部分重叠。该篇持有 `coui_snack_bar_*` 几何与九宫格阴影的取值依据；本篇只新增「共用绘制节点 + 单实例状态」的装配边界，不改其结论。
- [页码、空状态、输入框与底部面板](2026-11-13-page-indicator-empty-textfield-bottomsheet.md)：部分重叠。该篇记录轻提示补齐 SnackBar 操作按钮/图标/多行分支；本篇沿用那份几何，未改参数。
- [线性进度与快捷动作](2026-11-13-linear-progress-and-quick-actions.md)：无关。
- [可选轻量页面容器](../architecture/2026-11-03-optional-coui-scaffold.md)：边界澄清。该篇的「不增加 Snackbar 槽」约束 Scaffold 的槽位，不禁止独立的 Snackbar 组件；本篇遵守该约束，由宿主装配。
- [ChipTab 组件装配](2026-10-24-chip-tab.md)：部分重叠。该篇持有分类 tab 的材质与输入链；本篇检查后不复用，未改其行为。

# Agent Note: 标签、富文本链接与滚动条的移植边界

Status: implemented

## Problem

[覆盖对照](../../../../docs/status/coverage.md)把 `tagview`、`clickablespan`、`scrollbar` 列为待补的低成本项，但三者在 View 体系里的承载都不直接对应 Compose：

- `COUITagView` 的背景是 `MaterialShapeDrawable` + 手动 `clipPath`，四角半径、填充与描边各自可设，默认直角且容器色读一个 `Theme.COUI` 里并未赋值的属性。
- `COUIClickableSpan` 是 `ClickableSpan` 子类，颜色来自状态选择器；本库的 `Text` 只接受 `String`，没有任何富文本入口。
- `COUIScrollBar` 是一个挂在宿主 `View` 上的绘制助手：自己维护 OFF/ON/FADING 状态、按 `computeVerticalScrollRange/Offset/Extent` 算几何、在 `onInterceptTouchEvent` 里抢拇指拖动，还会对宿主补发 `ACTION_CANCEL`。

照搬类结构会引入 Material 依赖、第二套文本排版，或一个只有单个消费者的状态接口；只做外观近似又达不到 1:1。

## Decision

三个组件都是纯布局、文本与绘制装配，不新增效果后端：

- [Tag](../../../../coui/src/main/java/com/cycling/coui/tag/Tag.kt) 用 `Modifier.clip(shape).background(...)` 承载原厂「按轮廓裁剪子节点 + 填充」，四角独立的普通圆角直接由宿主的 `RoundedCornerShape` 给出。文本 10dp `sans-serif-medium` 单行省略、左图标 5×10dp 与 3/2dp 间距都按 `coui_tag_view_layout.xml` 固定。**不复用 `CouiShape`**：它是等半径 Lp 超椭圆，既不能四角分别设半径，也不是原厂 MaterialShapeDrawable 的普通圆角。
- [Text](../../../../coui/src/main/java/com/cycling/coui/text/Text.kt) 增加 `AnnotatedString` 重载，与既有 `String` 重载共用同一个内部排版函数，保留 `CouiTextKind`、颜色优先级、对齐与最大行数语义；链接样式由 [CouiTextLinkDefaults](../../../../coui/src/main/java/com/cycling/coui/text/CouiTextLinkDefaults.kt) 以 `TextLinkStyles` 预设交给宿主装配。**不内置**链接样式拦截：`AnnotatedString` 里的 span 是宿主业务文本的一部分，库不替调用方改写。
- [ScrollBar](../../../../coui/src/main/java/com/cycling/coui/scrollbar/ScrollBar.kt) 只读 Compose 的 `ScrollState`，用 Canvas 绘制拇指，用 `awaitEachGesture` 在**拇指矩形命中**后才消费事件，其余触摸原样留给滚动容器；拖动只写入目标位置，滚动事务在 `LaunchedEffect` 里统一提交。

## 原厂依据与复用核查

依据集中在 `references/apk/coui-src/com/coui/appcompat/` 的 `tagview/`、`clickablespan/`、`scrollbar/` 与 `references/apk/settings/decoded/res/` 的 `coui_tag_view_layout.xml`、`values/{dimens,colors,color}/*`、`values/styles.xml`：

| 项 | 原厂 | 本次移植 |
| --- | --- | --- |
| Tag 容器色 | `?attr/couiColorBackgroundWithTag`，`Theme.COUI` 未赋值；文本默认 `coui_color_white` | 默认沿用原厂 attr 组合（`couiColorContainer8` 薄填充 + 白字），文档与 Demo 均显式传入实心配色 |
| Tag 文本 | `couiTextTag`：10sp `sans-serif-medium`，`coui_default_tag_textsize=10dp` | 10dp 转 sp，固定不随系统字号缩放（与 Badge 的 dp 字号口径一致） |
| Tag 轮廓 | `MaterialShapeDrawable`，四角独立默认 0，`couiTagStrokeWidth` 默认 0 | `clip(Shape)` + 可选 `border`，默认直角 |
| 链接色 | `color/coui_clickable_text_color`：`?couiColorLink`（= `?couiColorLabelTheme`，日 `#0080FF` / 夜 `#1A8CFF`）、按下与选中 `#4D2660F5`、禁用 `#26000000` / `#26FFFFFF` | 常规取 `accent`，按下/聚焦/悬停 `#4D2660F5`，禁用色单独暴露 |
| 滚动条几何 | 14dp 宽、48dp 最小高、8/16dp 上下留白、5.5/4dp inset，色 `#29000000` / `#40FFFFFF` | 同值进 `CouiScrollBarDefaults` |
| 滚动条显隐 | `awakenScrollBars(2000)`，`state==0` 时下限 750ms，淡出用 `ViewConfiguration.getScrollBarFadeDuration()`（250ms），首次挂载 `getScrollDefaultDelay()*4`（1200ms） | 2000ms 停留 + 250ms 淡出，首次 1200ms，拖动期间不淡出 |
| 滚动条拖动 | 目标 top 夹到 `[0, height-thumbH]` 后换算回 `scrollBy`，`state==0` 时不接管 DOWN | 同一换算；可见性（alpha）为 0 时不命中 |
| RTL | `TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())` | 改为 `LocalLayoutDirection`，按真实布局方向 |

检查过的候选与结论：

| 候选 | 结论 |
| --- | --- |
| `CouiShape`（Tag 轮廓） | 不复用。等半径超椭圆，无法表达原厂四角独立与普通圆角。 |
| `CouiTextKind.Tag` + `rememberCouiTextStyle` | 不复用。该路径按 sp 排版并做系统字重补偿，原厂 Tag 字号以 dp 声明、不随字号缩放。 |
| `ChipTab` 系列（当标签用） | 不复用。需要背景源与材质，语义是分类单选 tab。 |
| `CouiMenuScrollBar` | 不复用。它是菜单内部的私有滚动条，绑定菜单位置与材质，不是公开可复用后端。 |
| 新建 `ClickableText` 组件 | 不做。会复制 `Text` 的 kind/对齐/maxLines 分支，后续两套排版会漂移。 |
| 为 `LazyListState` 造 `CouiScrollBarState` 接口 | 本轮不做。当前没有第二个消费者，属为将来造抽象；需要时再按实际调用点扩展。 |
| `detectDragGestures` 承载拖动 | 不复用。它在触摸斜率后无条件消费，会让落在滚动条区域的列表拖动失效。 |

## Alternatives considered

- **Tag 复用 `CouiShape` 并整体替换圆角**：省一个 Shape 参数，但原厂四角独立且默认直角，换成主题 token 会让所有标签突然变成 12dp 超椭圆。
- **Tag 直接调 Material 的 `MaterialShapeDrawable`**：能 1:1 搬背景，但耦合 Material 运行时（`coui` 的模块边界只允许 Android/Compose/AndroidX），且本项目已用 Compose 绘制替代同类需求。
- **给 `Text` 增加一个 `linkStyles` 参数并在内部改写 span**：能省宿主编排，但库会静默覆盖调用方写的样式，且 `AnnotatedString` 复用同一份文本时行为不一致。改为预设 + 显式传入。
- **ScrollBar 在输入协程内直接 `scrollBy`**：`AwaitPointerEventScope` 限制挂起调用，且会与滚动事务互相竞争；改为写目标位置、由 `LaunchedEffect` 提交。
- **ScrollBar 铺满全屏并接管所有拖动**：实现简单，但会抢走列表滚动。改为精确命中拇指矩形。
- **滚动条整段按整像素取整**：更贴原厂整数 rect，但 Compose 中会导致慢速滚动时抖动；保留亚像素，记 <1px 差异。

## Consequences

- 收益：三项缺口落地，`Text` 有了富文本出口，滚动条不依赖背景源、OEM API 或 Material；三者都不改既有公开行为（`Text` 的 `String` 重载语义不变）。
- 代价：Tag 默认色是原厂 attr 组合（中性薄填充 + 白字），日常实心标签需宿主显式配色；富文本禁用态需要宿主改普通 span；滚动条只支持 `ScrollState`，拖动时不取消容器已在进行的 fling，位置为亚像素。
- 验证范围：只做了源码、资源、导航、链接与编码的静态核对；Kotlin 类型与 import 解析以宿主编译为准。按[验证与评审](../../../../docs/development/validation.md)，未执行 Android 构建、Lint、Preview、安装或真机操作。标签观感、链接按下色、滚动条淡出曲线、拖动跟随、RTL 与无障碍播报均待真机验收。

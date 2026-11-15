# Agent Note: 页面级卡片、柱状图与中央凸起底栏

Status: implemented

## Problem

真机截图（ColorOS 17 手机管家、实用工具、清理存储、电池）里有四类界面元素，现有组件无法表达：

- 页内通知卡与推荐卡：图标 + 说明/标题 + 右下操作组，`Group` + `Item` 拼不出卡片级入口；
- 模式选择卡：等宽卡片、图标 + 主副文字、选中描边，`SegmentedControl` 是胶囊滑块，不带图标与副标题；
- 电量柱状图：圆角柱、逐柱配色、右侧刻度，库内没有任何图表；
- 底部 dock：胶囊两个 tab + 中央凸起圆钮，`NavigationBar` 的附件排在同一行两端，没有中央凸起形态。

## Decision

新增四个组件，全部只做布局与装配，不新增 Shader、模糊、轮廓或投影实现：

- [ActionCard](../../../../coui/src/main/java/com/cycling/coui/actioncard/ActionCard.kt)：`title` 可选，省略时退化为提示卡形态。两种形态共用同一套布局，不另建 `NoticeCard`——两者差异只有标题一行，独立组件会成为只转发参数的层。
- [ChoiceCardGroup](../../../../coui/src/main/java/com/cycling/coui/choicecard/ChoiceCardGroup.kt)：`selectable` + `Role.RadioButton` + `selectableGroup()`，选中态只叠加强调描边与文字色，不改高度。与 `SegmentedControl` 分工：后者是胶囊滑块，用于 2~4 个短标签的即时切换。
- [BarChart](../../../../coui/src/main/java/com/cycling/coui/chart/BarChart.kt)：Canvas 绘制圆角柱，柱宽按可用宽度均分，`axisLabels` 同时决定右侧刻度与水平网格线位置。不做滚动、缩放、动画或交互，也不引入图表库。
- [DockBar](../../../../coui/src/main/java/com/cycling/coui/navigation/DockBar.kt)：复用 `CouiFloatingRenderer.pair()` + `Modifier.couiFloatingPair`，胶囊是 index 1 主体、中央圆钮是 index 2 主体，融球、材质、阴影、跟手光全部由既有双主体链承担。

DockBar 的几何与装配约束：

- 胶囊 56dp 高、圆钮 56dp、凸起量 20dp；圆钮 `align(TopCenter)`、胶囊 `align(BottomCenter)`，在 76dp 高的容器里重叠 36dp，重叠量决定融球的过渡宽度。
- 两个主体的内容分别录进各自的 `GraphicsLayer`（`drawWithContent` 里只 `record` 不绘制），由 patch 统一输出，与 `NavigationBar` 的 `FloatingNode` 同一套做法。
- 没有中央图标时 `centerSurface()` 返回 1×1 的 `visible = false` 占位：`floatingSurfaces` 要求所有主体尺寸大于零，否则整条栏会被跳过。
- 选中项只在切换到不同项时回调，与 `NavigationBar` 的提交语义一致；底栏不维护选中状态。

## Alternatives considered

- 把 `NavigationBar` 加一个「中央凸起」开关：三主体布局、附件展开/收起动画和中央凸起是两套几何，合并会让两个分支互相牵制。
- 为 `DockBar` 单独写一套材质与阴影常量：会与底栏、顶栏按钮的玻璃参数分叉，违反基础效果复用规则。
- 用 `standalone` 单主体画两个分离的胶囊与圆钮：两圆之间会出现硬边，读不出原厂的连续过渡。
- 引入第三方图表库：只为一张柱状图增加运行时依赖，且无法复用 `CouiPageColors`。
- 让 `BarChart` 内置刻度文字排版：Canvas 里画文字需要额外 text measurer，交给右侧 `Column` 更简单，也让刻度样式跟随主题文字。

## Consequences

- `CouiQuickAction` 新增 `labelMaxLines`（默认 2），原厂「常用信息与密码」这类两行入口不再被截断；单行入口在同高度内居中，卡片高度不因单双行切换而跳动。
- 实用工具页的入口后来实测为**全圆胶囊**（60dp 行高、8dp 间距、图标后留白 8dp、14sp 标签），不是本文当时假设的 24dp 卡片。`QuickActionGrid` 因此新增 `CouiQuickActionStyle.Capsule`，`Card` 保持默认以免改动既有调用；两者只差轮廓、行高、间距与标签字号，不值得拆成两个组件。
- `ActionCard` 的两种形态后来发现说明字号不同：提示卡（无标题）实测单行墨迹 13.7dp、行距 20.3dp，对应 **15sp**；推荐卡（有标题）的副标题实测 13sp。原实现两处都用 `ItemSummary`(13sp)，提示卡偏小。为此新增 `CouiTextKind.BodyLarge`(15sp)，`ActionCard` 按有无标题切换。测量基准是 872×1920 的手机管家截图，与 1264×2780 同一设备等比缩放（2.2px/dp），比例用同页 16sp 主按钮与 14sp 文字按钮标定。
- `TopAppBarButton` 新增可选 `backdrop`，自定义顶栏（手机管家、清理存储）可以不用 `TopAppBarSurface` 也拿到真实玻璃；省略时行为不变。
- DockBar 需要 API 34 与页面背景源，低版本没有替代实现，Demo 显示 `requires_api_34`。
- 未验证：四个组件的真机视觉与深浅色、DockBar 在 RTL 下的凸起位置、`BarChart` 在大量柱与极窄宽度下的圆角表现、`ChoiceCardGroup` 超过四项时的压缩效果。本地未执行 Gradle 构建，仅做静态检查。

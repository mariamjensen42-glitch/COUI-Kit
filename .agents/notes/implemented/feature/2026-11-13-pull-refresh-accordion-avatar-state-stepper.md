# Agent Note: 下拉刷新、折叠面板、头像、三态容器与步骤条

Status: implemented

## Problem

组件库盘点后仍缺五个高频结构性组件：

- 列表没有下拉刷新，`nestedScroll` + 回弹 + 指示器要每个页面重写；
- 设置页的「高级/网络」需要卡片内按需展开，`Group` 是静态卡片、`Item` 只有 trailing；
- 头像在每个页面手搓圆角容器；
- 加载/空/失败/就绪四态每次都要写一个 `when`；
- 流程类页面没有进度步骤条。

## Decision

五个组件都只做布局与状态分发，不新增 Shader、动画后端或网络行为：

- [PullToRefresh](../../../../coui/src/main/java/com/itos/coui/pulltorefresh/PullToRefresh.kt)：`nestedScroll` 的 `onPostScroll` 里以「`consumed.y == 0` 且 `available.y > 0`」判定已到顶，位移用 `mutableFloatStateOf` 同步累加，回弹与刷新态收尾统一放在 `LaunchedEffect(refreshing)`，避免松手动画与状态动画互相覆盖。内容用 `graphicsLayer` 下移而不是改布局尺寸，列表滚动位置和 Scaffold 内边距都不受影响。
- [Accordion](../../../../coui/src/main/java/com/itos/coui/accordion/Accordion.kt)：受控展开 + `AnimatedVisibility`；箭头自绘两段折线并整体旋转，不依赖图标库，也不受宿主图标配色影响。
- [Avatar](../../../../coui/src/main/java/com/itos/coui/avatar/Avatar.kt)：只负责尺寸、裁剪与底色，图片加载、占位、首字母取字、在线角标都留给宿主与 `BadgedBox`。
- [StateLayout](../../../../coui/src/main/java/com/itos/coui/statelayout/StateLayout.kt)：`CouiLoadState` 四态 + 宿主提供的空/失败槽；不内置文案，避免把中英文案塞进库。
- [Stepper](../../../../coui/src/main/java/com/itos/coui/stepper/Stepper.kt)：Canvas 画连接线、weight 均分圆点，圆心与线段的 `segment/2 + segment*i` 对齐；`currentIndex` 越界夹紧。

## Alternatives considered

- 下拉刷新复用 `SwipeRefresh` 类第三方实现：会引入依赖，且无法与 `couiVerticalScroll` 和折叠顶栏的 nestedScroll 共用一条链。
- 指示器内置固定样式：不同页面对指示器的要求不同（环形、文字、Logo），改成 `indicator` 槽后宿主可完全替换。
- Accordion 使用图标库的 chevron：库的图标子集不保证有箭头，自绘折线更稳，也避免为单个组件扩大图标面。
- StateLayout 内置「加载中/暂无内容/重试」文案：需要维护中英资源且场景差异大，改为宿主提供槽。
- Stepper 做成可点击导航：那是 `SegmentedControl` / `ChipTab` 的职责，步骤条只表达进度。

## Consequences

- 新增五个包与对应 Defaults，没有改动已有组件的公开契约。
- `PullToRefresh` 依赖内容是可滚动容器；静态内容不会产生 overscroll，下拉不会触发，文档已写明。
- 未验证：真机上下拉回弹手感与 `LazyColumn` 的配合、Accordion 展开时的高度动画在长内容下的表现、Stepper 在 5 个以上步骤时的标签压缩、`StateLayout` 切换时列表状态的保留。本地未执行 Gradle 构建，仅做静态检查。

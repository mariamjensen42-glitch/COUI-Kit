# Agent Note: 通知卡只做布局，材质交给浮动表面

Status: implemented

## Problem

通知中心的单条通知需要「图标 + 标题 + 相对时间 + 正文」的卡片，`ActionCard` 的说明只有一段、没有相对时间，也缺少标题与时间同一行的排布。

原厂这张卡是玻璃材质。如果组件内建材质，就得在 `notificationcard` 里重新拼 `CouiFloatingMaterial`、`CouiFloatingShadow` 和轮廓光，等于把 `FloatingToolbar`、`ChipTab` 已经在用的那套链路再写一遍，违反基础效果复用。

## Decision

[NotificationCard](../../../../coui/src/main/java/com/itos/coui/notificationcard/NotificationCard.kt) 只负责布局与排版，不自带材质：

- `containerColor` 默认取 `colors.card`，组件直接 `background` 填充。
- 需要玻璃时宿主传 `Color.Transparent`，用 `Modifier.couiFloatingSurface` 包裹并传入**同一个** `shape`。材质、模糊、轮廓光、阴影全部由既有浮动表面链路承担。

`shape` 必须是同一个实例：`clip` 用一次、`couiFloatingSurface` 的描边和裁剪再用一次，不一致会错位。文档已写明。

没有把材质做成 `backdrop: CouiBackdrop? = null` 那样的可选参数（`TopAppBarButton` 的写法），因为那需要组件自己持有 `CouiFloatingRenderer` 和 `GraphicsLayer`，而这两个必须与绘制节点一一对应、不能跨节点复用；让宿主在包裹处创建，生命周期更清楚。

## 原厂依据与适配边界

几何取自本机真机布局树（一加 Ace 5，ColorOS 17.0.0.105，1264×2780 @560dpi）：`expandableNotificationRow` 为 `[56,712][1208,1008]`，即宽 329dp（左右各 16dp 页边距）、单条最小高 60dp；内容左起点 58dp = 16 内边距 + 24 图标 + 18 间距；时间右对齐，距卡片右缘 14dp。

标题 16sp、时间 12sp 由行高反推（标题 66px≈18.9dp、时间 49px≈14dp）。正文取 13sp 与操作卡副标题一致，**未用原厂资源确认**；原厂正文行高 70px≈20dp，可能是 14sp 或两行排版导致，需要拆包复核。

原厂通知还有分组头（带条数角标）、折叠箭头、「点击查看更多」和长按快捷操作，本组件未覆盖：这些属于 SystemUI 的交互外壳，不是应用内的通用通知卡。真机截图叠加与浅色模式尚未验收。

## Alternatives considered

- 组件内建 `CouiFloatingMaterial` / `CouiFloatingShadow`：会重复 `FloatingToolbar`、`ChipTab` 已使用的材质、模糊与轮廓光链路，违反基础效果复用。
- 提供 `backdrop: CouiBackdrop? = null` 可选参数：需要组件自己持有 `CouiFloatingRenderer` 与 `GraphicsLayer`，两者必须与绘制节点一一对应，跨节点复用会破坏资源生命周期。
- 把通知卡并入 `ActionCard`：通知语义需要相对时间与标题/正文分层，`ActionCard` 的说明只有一段，合并会让两个场景互相牵制。

## Consequences

- 收益：通知卡保持纯布局，玻璃、模糊、轮廓光与阴影由既有浮动表面链路承担，不复制材质算法。
- 代价：宿主必须自己创建 backdrop、renderer 与 layer，并保证 `shape` 与包裹处一致；默认 `colors.card` 只是无材质占位。
- 已知上限：正文 13sp 未用原厂资源确认；分组头、折叠箭头、长按快捷操作未覆盖；真机截图叠加与浅色模式待验收。

## Existing-note audit

- [页面级卡片、柱状图与中央凸起底栏](2026-11-13-card-chart-dock-components.md)：`ActionCard` 与本组件相邻但不替代，前者是「图标 + 可选标题 + 右下操作组」，后者是通知语义的标题/时间/正文；两者共享 `colors` 与 `ItemTitle`/`ItemSummary` 排版，不共享材质实现。

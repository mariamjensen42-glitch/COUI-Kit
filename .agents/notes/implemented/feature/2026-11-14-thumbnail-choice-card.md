# Agent Note: 缩略图选择卡拆成两个入口

Status: implemented

## Problem

ColorOS 17 设置页有两处「缩略图 + 文字」的卡片，`ChoiceCardGroup` 都装不下：它是图标 + 主副文字的横向等分卡，没有缩略图槽位。

- 显示与亮度：两列、**无卡片底色**、两卡无缝并排贴在同一分组表面上，底部是单选钮。
- 通知与控制中心 - 分类设置：三列、**每卡自带底色与圆角**、间距 8dp，底部是「当前值 + 箭头」，点击跳转而非选中。

两处版式、底色、交互语义都不同，强行合成一个组件只能靠参数切换版式。

## Decision

[ThumbnailChoiceCard](../../../../coui/src/main/java/com/itos/coui/choicecard/ThumbnailChoiceCard.kt) 提供两个公开入口，共享 `CouiThumbnailChoice(label, thumbnail, value)` 与同一套槽位逻辑：

- `ThumbnailChoiceGroup`：固定两列，不自绘底色，`selectable` + `Role.RadioButton`，受控 `selectedIndex`。
- `ThumbnailNavigationGroup`：固定三列，`clip(CouiShape(20.dp, 3f))` + `colors.card`，`clickable` + `Role.Button`。

列数不做参数：两处原厂页面各自固定列数，加 `columns` 只会产生「三列单选」「两列导航」这类没有原厂依据的组合。同理不提供统一的 `dark`，单选型没有底色可 tint，按压反馈由 `RadioButton` 的选中动画承担。

箭头 6×11dp 由本模块本地绘制，核心不依赖 `coui-icons`（与 Accordion 的箭头同一做法），不做 tint。

字号按字体度量反推而非按行高比例猜：`CouiTextMetrics` 用 `Paint.fontMetrics` 算行高，sans-serif 下 16sp≈18.8dp、14sp≈16.4dp、12sp≈14.1dp，与布局树实测的 18.9 / 16.3 / 14.0dp 逐一吻合，因此两列主文字取 `ItemTitle`(16sp)、三列主文字取 `ItemValue`(14sp)、值取 `Body`(12sp)。

## 原厂依据与适配边界

依据是本机真机布局树（一加 Ace 5，ColorOS 17.0.0.105，1264×2780 @560dpi），不是 APK 资源：本次任务期间设备断开，未能取得 `Widget.COUI.*` 与对应 layout。所有像素到 dp 换算为整数（3.5px/dp），留白链 24/10、8/4、16 均自洽，可认为量到了真实值。

两处未确认，已在[使用文档](../../../../docs/components/thumbnail-choice.md#已知偏差)标明：

- 两列形态的图片、文字、单选钮三者同轴但整体偏离卡片几何中心 7.6dp。判断是预览位图自带右侧留白，实现按几何居中；若拆包确认是布局约束需再改。
- 三列圆角取 20dp。平滑圆角在顶边与左边的内缩读数本就不等（21.7 / 16.3dp），取中间值。

以上两项、以及「三列值行是否另有最小高度」需要在拿到原厂资源后复核。RTL 尚未验收。

在模拟器上按真机 dp 宽度（`wm density 479`，1080px → 361dp）实测：两列预览 210×425px = 70.1×142.0dp，与规格一致；内容总高 217.4dp（规格 219dp，差在文字行高取整）。三列卡初次实测只有 93.5dp，原因是示例把卡片套进了 `Group`，多出 16dp 内边距；原厂三列卡直接贴页面（布局树 `[56,1309][421,1926]`，无外层卡片）。去掉外层后实测 104.2dp / 间距 8.0dp / 页边距 16.0dp，与真机完全一致。这是**页面**的装配方式：三列卡直接贴页面。放进 Group 陈列时需要额外的展台处理，见 demo 的 `ComponentStage`。

## Alternatives considered

- 用一个组件加 `columns` 参数：会产生「三列单选」「两列导航」这类原厂不存在的组合，版式、底色和选中语义仍要分支处理。
- 复用 `ChoiceCardGroup`：它是图标 + 主副文字的横向等分卡，没有缩略图槽位，也无法表达三列的「当前值 + 箭头」。
- 提供统一的 `dark` 或底色 tint：两列单选型本来就不自绘底色，按压反馈由 `RadioButton` 承担；统一 tint 会多出一个没有消费点的参数。

## Consequences

- 收益：两个入口各自固定列数与交互语义，共享同一套缩略图/文字槽位，避免参数化的伪通用组件。
- 代价：两列形态的 7.6dp 同轴偏移与三列 20dp 圆角仍按几何推断；两列/三列都直接贴页面，放进 `Group` 需要额外展台。
- 已知上限：原厂资源未取得；RTL 与「三列值行最小高度」待验收。

## Existing-note audit

- `ChoiceCardGroup` 没有独立 Note，只有[模式选择卡使用文档](../../../../docs/components/choice-card.md)：相邻不替代，继续持有「图标 + 主副文字 + 强调色描边」的形态；本篇只补缩略图形态，不改其选中语义。
- [胶囊分段指示器](2026-11-07-coui-segmented-control.md)：无关，不复用其指示器与材质后端。

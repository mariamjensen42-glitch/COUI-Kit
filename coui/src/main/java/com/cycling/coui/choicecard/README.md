# ChoiceCard

## Overview
模式选择卡组件包。`ChoiceCardGroup` 提供横向等分、带图标与副文字的单选模式卡（如均衡/省电/高性能）；`ThumbnailChoiceGroup` 与 `ThumbnailNavigationGroup` 提供缩略图预览型单选卡与导航卡。选择状态均由宿主持有。

## Interface
- `public object CouiChoiceCardDefaults`：轮廓基线、留白、图标尺寸、间距、最小高度与描边宽度 [ChoiceCardGroup.kt:CouiChoiceCardDefaults]
- `public @Stable class CouiChoiceCard(label, icon, summary)` [ChoiceCardGroup.kt:CouiChoiceCard]
- `public @Composable fun ChoiceCardGroup(modifier, items, selectedIndex, onSelect, dark, colors)` [ChoiceCardGroup.kt:ChoiceCardGroup]
- `public object CouiThumbnailChoiceDefaults`：两列单选型与三列导航型的尺寸、间距与箭头参数 [ThumbnailChoiceCard.kt:CouiThumbnailChoiceDefaults]
- `public @Stable class CouiThumbnailChoice(label, thumbnail, value)` [ThumbnailChoiceCard.kt:CouiThumbnailChoice]
- `public @Composable fun ThumbnailChoiceGroup(modifier, items, selectedIndex, onSelect, colors)` [ThumbnailChoiceCard.kt:ThumbnailChoiceGroup]
- `public @Composable fun ThumbnailNavigationGroup(modifier, items, onSelect, dark, colors)` [ThumbnailChoiceCard.kt:ThumbnailNavigationGroup]

## Invariants
- `ChoiceCardGroup` 以 `require(items.isNotEmpty())` 约束非空；`selectedIndex`/`onSelect` 成对使用，容器为 `selectableGroup`，每卡暴露 `Role.RadioButton` [ChoiceCardGroup.kt:ChoiceCardGroup]
- 选中态只叠加强调色描边与强调色文字，卡片高度不变（`defaultMinSize(minHeight = 92.dp)`）[ChoiceCardGroup.kt:ChoiceCardCell]
- `ChoiceCardGroup` 与胶囊分段 `SegmentedControl` 不互相替代：后者不带图标与副标题 [ChoiceCardGroup.kt:ChoiceCardGroup]
- 两列 `ThumbnailChoiceGroup` 无缝并排且不自绘底色，需贴在外层 `Group` 或页面表面上 [ThumbnailChoiceCard.kt:ThumbnailChoiceGroup]
- 三列 `ThumbnailNavigationGroup` 每卡自带 `navigationShape` 与卡片底色，整卡点击并暴露 `Role.Button` [ThumbnailChoiceCard.kt:ThumbnailNavigationGroup]
- 缩略图与图标均由调用方提供，库只给固定槽位、不做 tint [ThumbnailChoiceCard.kt:CouiThumbnailChoice]

## Side Effects
- 指针/无障碍：`selectable`/`clickable` 与 `Role.RadioButton`/`Role.Button` 语义 [ChoiceCardGroup.kt:ChoiceCardCell]、[ThumbnailChoiceCard.kt:ThumbnailNavigationCell]
- 按压反馈：`couiButtonFeedback` 交互蒙层 [ChoiceCardGroup.kt:ChoiceCardCell]
- 绘制：导航卡箭头由 `Canvas` 本地绘制 [ThumbnailChoiceCard.kt:Chevron]
- 读取 `CouiTheme.isDark`/`colorScheme` 获取默认外观 [ChoiceCardGroup.kt:ChoiceCardGroup]

## Verification
- Run: `./gradlew :coui:lintDebug`

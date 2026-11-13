# 快捷动作网格

`QuickActionGrid` 把一组带彩色图标的入口排成等宽卡片，用于页面顶部的功能分区。

## 基本用法

每个入口给出标题、图标内容和点击回调：

~~~kotlin
val actions = listOf(
    CouiQuickAction(
        label = "照片清理",
        icon = { Icon(CouiIcons.Filled.Photo, null, tint = CouiColor.Day.BLUE) },
        onClick = { openPhotos() },
    ),
    CouiQuickAction(
        label = "视频清理",
        icon = { Icon(CouiIcons.Filled.Fan, null, tint = CouiColor.Day.GREEN) },
        onClick = { openVideos() },
    ),
)

QuickActionGrid(actions = actions)
~~~

网格默认两列，按 `CouiPageDefaults.quickActionColumns` 等分整行宽度，末行不足时用空位补齐，卡片宽度不随标题长度变化。`modifier` 中的水平留白由调用方给出，与 `Group` 一致使用 `CouiPageDefaults.horizontalMargin`。

## 状态与事件

`onClick` 由宿主持有，组件不保存选中状态。整张卡片承担点击，图标与文字不单独响应；`enabled = false` 时只降低内容不透明度并保留占位，不改变网格尺寸。

标题在单行内显示，超出宽度以省略号截断，不换行撑高卡片。

## 外观与预设

卡片沿用 `CouiPageDefaults.shape`（24dp 超椭圆）和 `CouiPageColors.card`，与 [Group](group.md) 的卡片保持同一轮廓和底色，因此可以直接放在同一页面上而不出现两种卡片质感。列数、间距与单卡高度由 `CouiPageDefaults` 的 `quickActionColumns`、`quickActionSpacing`、`quickActionMinHeight` 维护。

图标由调用方提供，库不做 tint：原厂这类入口是彩色图标资源，不是单色矢量，组件不假设颜色来源。需要接入宿主配色时传入 `colors`，它只影响卡片底色与文字颜色。

## 平台与装配

网格自带卡片底色，不再放进 `Group` 的卡片内，否则会形成双层背景。分组标题由调用方按 `CouiTextKind.GroupTitle` 单独排版。

组件不依赖 `CouiBackdrop`、API 34 或任何效果入口，只使用按压反馈与点击语义。

## 完整示例与 API

两列示例见 [DemoLayoutSamples](../../demo/src/main/java/com/itos/coui/demo/DemoLayoutSamples.kt)，库入口见 [QuickActionGrid](../../coui/src/main/java/com/itos/coui/layout/QuickActionGrid.kt)。

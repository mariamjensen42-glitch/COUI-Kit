# 快捷动作网格

`QuickActionGrid` 把一组带彩色图标的入口排成等宽卡片，用于页面顶部的功能分区。默认是 24dp 圆角卡片；`style = CouiQuickActionStyle.Capsule` 切换为实用工具页的全圆胶囊条目。

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

实用工具页的胶囊形态只需多传一个 `style`：

~~~kotlin
QuickActionGrid(
    modifier = Modifier.padding(horizontal = CouiPageDefaults.horizontalMargin),
    actions = actions,
    style = CouiQuickActionStyle.Capsule,
)
~~~

## 状态与事件

`onClick` 由宿主持有，组件不保存选中状态。整张卡片承担点击，图标与文字不单独响应；`enabled = false` 时只降低内容不透明度并保留占位，不改变网格尺寸。

标题默认最多两行，超出宽度以省略号截断，行数由 `CouiQuickAction.labelMaxLines` 控制；原厂「常用信息与密码」这类入口占两行，单行入口在相同高度内居中。

## 外观与预设

两种外观由 `style` 选择，默认 `CouiQuickActionStyle.Card`：

| 项 | `Card` | `Capsule` |
| --- | --- | --- |
| 轮廓 | `CouiTheme.shapes.card`（24dp 超椭圆） | `CouiTheme.shapes.capsule`（全圆） |
| 最小高度 | 56dp | 60dp |
| 行 / 列间距 | 12dp | 8dp |
| 图标后留白 | 12dp | 8dp |
| 标签 | 16sp `ItemTitle` | 14sp `ItemValue` |

`Capsule` 对应 ColorOS 17 实用工具页：两列全圆条目，行高 60dp、间距 8dp、左右内留白 16dp、图标 24dp，数值取自真机截图测量（1264×2780 @560dpi）。`Card` 沿用 [Group](group.md) 的卡片轮廓和底色，两者可以直接放在同一页面上而不出现两种卡片质感。

列数、间距与单卡高度由 `CouiPageDefaults` 的 `quickActionColumns`、`quickActionSpacing`、`quickActionMinHeight` 以及对应的 `quickActionCapsule*` 维护。

图标由调用方提供，库不做 tint：原厂这类入口是彩色图标资源，不是单色矢量，组件不假设颜色来源。需要接入宿主配色时传入 `colors`，它只影响卡片底色与文字颜色。

## 平台与装配

网格自带卡片底色，不再放进 `Group` 的卡片内，否则会形成双层背景。分组标题由调用方按 `CouiTextKind.GroupTitle` 单独排版。

组件不依赖 `CouiBackdrop`、API 34 或任何效果入口，只使用按压反馈与点击语义。

## 完整示例与 API

两列示例见 [DemoLayoutSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoLayoutSamples.kt)，库入口见 [QuickActionGrid](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/layout/QuickActionGrid.kt)。

# 🖼️缩略图选择卡

缩略图卡把一张预览图和文字并排放进同一张卡，对应 ColorOS 17 设置页的两处形态：

- `ThumbnailChoiceGroup`：**两列单选**，对应「设置 - 显示与亮度」的亮色 / 暗色模式预览。
- `ThumbnailNavigationGroup`：**三列导航**，对应「设置 - 通知与控制中心 - 分类设置」。

两者共享 `CouiThumbnailChoice(label, thumbnail, value)`，但版式、底色与交互语义不同，因此拆成两个公开入口而不是靠参数切换：单选型不带底色、两卡无缝并排、底部是 `RadioButton`；导航型自带圆角与卡片底色、间距 8dp、底部是当前值加箭头。

需要「图标 + 主副文字」的模式选择卡时用[模式选择卡](choice-card.md)，需要胶囊分段时用[分段选择器](segmented-control.md)。

## 最小调用

两列单选，放在 `Group` 里直接贴分组表面：

~~~kotlin
var selected by rememberSaveable { mutableIntStateOf(0) }

Group(title = "外观") {
    ThumbnailChoiceGroup(
        modifier = Modifier.padding(16.dp),
        items = listOf(
            CouiThumbnailChoice("亮色模式", thumbnail = { LightPreview() }),
            CouiThumbnailChoice("暗色模式", thumbnail = { DarkPreview() }),
        ),
        selectedIndex = selected,
        onSelect = { selected = it },
    )
}
~~~

三列导航，每卡自带底色：

~~~kotlin
ThumbnailNavigationGroup(
    // 页面里直接贴页面（左右 16dp）；放进 Group 陈列时要让出内边距，否则卡宽会被压窄。
    modifier = Modifier.padding(horizontal = CouiPageDefaults.horizontalMargin),
    items = listOf(
        CouiThumbnailChoice("锁屏通知", thumbnail = { LockPreview() }, value = "列表"),
        CouiThumbnailChoice("横幅通知", thumbnail = { BannerPreview() }, value = "已开启"),
        CouiThumbnailChoice("设为静音", thumbnail = { SilentPreview() }, value = "已关闭"),
    ),
    onSelect = { index -> openCategory(index) },
)
~~~

## 参数

`ThumbnailChoiceGroup`

- `items`：至少一项。
- `selectedIndex` / `onSelect`：受控单选，整卡承担点击并暴露 `RadioButton` 语义。
- `colors`：标题文字色；卡片不自绘底色，底色由外层 `Group` 或页面提供。

`ThumbnailNavigationGroup`

- `items`：至少一项，`value` 可省略，省略时只显示箭头。
- `onSelect`：点击回调，整卡承担点击并暴露 `Button` 语义。
- `dark` / `colors`：卡片底色、标题、当前值与箭头色。

缩略图由调用方提供，库只给固定尺寸的槽位并居中，不假设它是图片、绘制结果还是动画。

## 尺寸

数值取自真机布局树（一加 Ace 5，1264×2780 @560dpi，3.5px/dp），像素换算到 dp 均为整数：

| 项 | 两列单选 | 三列导航 |
| --- | --- | --- |
| 卡片 | 133 × 219dp，间距 0 | 104 × 176dp，间距 8dp |
| 缩略图槽位 | 70 × 142dp，贴卡片顶边 | 48 × 102dp，四周留白 16dp |
| 图 → 文字 | 24dp | 8dp |
| 主文字 | 16sp `ItemTitle` | 14sp `ItemValue` |
| 文字 → 底部 | 10dp + 24dp 单选钮 | 4dp + 14dp 值行（12sp `Body`） |
| 轮廓 | 无底色，随外层 | `CouiShape(20.dp, 3f)` |

全部集中在 `CouiThumbnailChoiceDefaults`。

## 已知偏差

- 两列形态的内容轴相对卡片中心左移 7.6dp（实测图片、文字与单选钮三者同轴，但都偏离卡片几何中心）。判断是原厂预览位图自带右侧留白所致，本实现按几何居中处理；若后续拆包确认是布局约束，再改。
- 三列圆角取 20dp：顶边水平内缩实测 21.7dp、左边垂直内缩 16.3dp，平滑圆角两个方向的读数本就不等，取中间值。待原厂资源确认。

源码见 [ThumbnailChoiceCard](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/choicecard/ThumbnailChoiceCard.kt)。

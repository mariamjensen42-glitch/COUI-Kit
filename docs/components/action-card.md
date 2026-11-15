# 🗂️操作卡

`ActionCard` 是页面内的一张卡片：图标、可选标题与说明，右下角是宿主提供的操作组。省略 `title` 时是提示卡形态（单段说明 + 操作）；提供 `title` 时是推荐卡形态（标题 + 副标题 + 操作）。

两种形态的说明字号不同：提示卡的说明是 15sp 的 `BodyLarge`，推荐卡的说明是 13sp 的 `ItemSummary`。真机测量依据为手机管家首页提示条（两行说明，单行墨迹 13.7dp、行距 20.3dp）与清理存储页推荐卡（标题 16sp、副标题 13sp）。

整卡不承担点击，交互只由 `actions` 里的子控件提供。卡片底色与文字色来自 `CouiPageColors`，与 `Group` 使用同一套页面配色。

## 最小调用

~~~kotlin
ActionCard(
    summary = "开启通知，及时获知设备状态，减少安全风险",
    icon = { Icon(CouiIcons.Filled.ShieldCheck, null, Modifier.size(24.dp)) },
    actions = {
        Button(onClick = onIgnore, label = "忽略", style = CouiButtonStyle.Text)
        Button(onClick = onEnable, label = "开启", style = CouiButtonStyle.Text)
    },
)
~~~

推荐卡形态补上标题：

~~~kotlin
ActionCard(
    title = "应用管理",
    summary = "应用安装、升级与卸载",
    icon = { Icon(CouiIcons.Filled.Grid, null, Modifier.size(24.dp)) },
    actions = {
        Button(onClick = onIgnore, label = "忽略", style = CouiButtonStyle.Text)
        Button(onClick = onOpen, label = "前往", style = CouiButtonStyle.Secondary)
    },
)
~~~

## 参数

- `summary`：必填。提示卡形态用 `CouiTextKind.BodyLarge`（15sp），推荐卡形态用 `CouiTextKind.ItemSummary`（13sp）——没有标题时说明本身承担主文案，需要更大的字号；这一差异来自真机测量，不是可配置项。
- `title`：可选，使用 `CouiTextKind.ItemTitle`；为空时只有说明。
- `icon`：可选，尺寸与间距由 `CouiActionCardDefaults` 决定，库不做 tint。
- `colors`：卡片、标题与说明的配色；默认取当前主题的页面配色。
- `actions`：`RowScope` 内容，间距与上边距由 `CouiActionCardDefaults` 决定。原厂提示条的两个操作是**文字按钮**（`CouiButtonStyle.Text`），不要换成实心按钮。

提示卡与推荐卡的差别只有标题，不要为两种形态各写一套卡片；需要多行说明时直接换行，卡片会随内容增高。

源码见 [ActionCard](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/actioncard/ActionCard.kt)。

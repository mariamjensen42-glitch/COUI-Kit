# 🃏模式选择卡

`ChoiceCardGroup` 是横向等分的卡片组，每张卡包含图标、主文字与可选副文字，选中项叠加强调色描边。对应 ColorOS 电池页的「均衡 / 省电 / 高性能」。

单选由宿主持有：`selectedIndex` 与 `onSelect` 成对使用，整卡承担点击并暴露 `RadioButton` 语义。它与[分段选择器](segmented-control.md)不互相替代——后者是胶囊滑块，不带图标与副标题。

## 最小调用

~~~kotlin
var selected by rememberSaveable { mutableIntStateOf(0) }
val items = listOf(
    CouiChoiceCard("均衡模式", { Icon(CouiIcons.Filled.Battery, null, Modifier.size(24.dp)) }, "16 小时 59 分钟"),
    CouiChoiceCard("省电模式", { Icon(CouiIcons.Filled.Fan, null, Modifier.size(24.dp)) }, "18 小时 20 分钟"),
    CouiChoiceCard("高性能模式", { Icon(CouiIcons.Filled.Radar, null, Modifier.size(24.dp)) }, "16 小时 8 分钟"),
)

ChoiceCardGroup(
    items = items,
    selectedIndex = selected,
    onSelect = { selected = it },
)
~~~

## 参数

- `items`：至少一项，每项由 `CouiChoiceCard(label, icon, summary)` 描述。
- `selectedIndex` / `onSelect`：受控单选；选中项只改描边与文字色，不改变卡片高度。
- `dark`：跟随主题，也可显式指定。
- `colors`：卡片底色、标题、副标题与强调色；默认取当前主题的页面配色。

卡片最小高度、间距、图标尺寸与描边宽度在 `CouiChoiceCardDefaults`。项目数较多时卡片会等分压缩，超过四项建议改用[快捷动作网格](quick-action-grid.md)或列表。

源码见 [ChoiceCardGroup](../../coui/src/main/java/com/itos/coui/choicecard/ChoiceCardGroup.kt)。

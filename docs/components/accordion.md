# 📂折叠面板

`Accordion` 是卡片内的可展开行：标题常驻，点击切换展开态，内容用展开/收起动画挂载。对应设置页的「高级设置」「网络」这类按需展开的分组。

展开状态由宿主持有（受控），组件只负责切换回调与动画。它与 `Group` 的区别是 `Group` 是静态卡片，`Accordion` 是同一张卡片里的一段可折叠内容。

## 最小调用

~~~kotlin
var expanded by rememberSaveable { mutableStateOf(false) }

Group(title = "高级设置") {
    Accordion(
        title = "高级",
        summary = "动画缩放、日志与其他选项",
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Item(title = { Text("动画缩放", kind = CouiTextKind.ItemTitle) })
        Divider()
        Item(title = { Text("调试日志", kind = CouiTextKind.ItemTitle) })
    }
}
~~~

## 参数

- `title` / `summary`：标题与可选副标题，使用 `ItemTitle` 与 `ItemSummary`。
- `expanded` / `onExpandedChange`：受控展开态；点击标题行切换。
- `enabled`：禁用时降低标题不透明度并移除点击。
- `dark` / `colors`：按压反馈与文字、箭头的配色。

箭头由组件自绘（两段折线，展开时旋转 180°），不依赖图标库，也不受宿主图标配色影响。内容区域的上下留白由 `CouiAccordionDefaults.contentBottomPadding` 控制，行间距仍由放入的子项决定。

源码见 [Accordion](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/accordion/Accordion.kt)。

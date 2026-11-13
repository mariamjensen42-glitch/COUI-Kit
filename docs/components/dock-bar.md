# 🚢悬浮底栏

`DockBar` 是中央凸起的悬浮底栏：胶囊里排布页面选择项，一个圆形操作凸出在胶囊上沿。两个主体共用同一条玻璃材质，交界处按融球合成，因此凸起圆钮与胶囊之间是连续过渡。

与[底栏](navigation-bar.md)的区别是导航项居中、附件以圆形凸出，而不是排在同一行两端。需要左右附件（主题切换、新建）时用 `NavigationBar`；需要「首页 / 实用工具 + 中央检测」这类形态时用 `DockBar`。

`DockBar` 需要 API 34 和页面背景源。放入 COUI `Scaffold` 的 `bottomBar` 时可省略 `backdrop`；第三方容器需显式传入只覆盖页面背景的采样源。

## 最小调用

~~~kotlin
@RequiresApi(34)
@Composable
fun BottomDock(backdrop: CouiBackdrop, selected: Int, onSelect: (Int) -> Unit) {
    DockBar(
        backdrop = backdrop,
        items = listOf(
            CouiNavigationItem("首页", CouiIcons.Filled.Grid),
            CouiNavigationItem("实用工具", CouiIcons.Filled.GridSliders),
        ),
        selectedIndex = selected,
        onSelection = onSelect,
        centerIcon = CouiIcons.Filled.ShieldCheck,
        centerDescription = "安全检测",
        onCenterClick = { /* 业务跳转 */ },
    )
}
~~~

## 参数

- `items`：至少一项，`selectedIndex` 必须在范围内；`onSelection` 只在切换到不同项时触发。
- `centerIcon`：为空时不绘制凸起，此时栏高不变、只显示胶囊。
- `centerDescription` / `onCenterClick`：凸起圆钮的描述与点击；`onCenterClick` 为空时不安装点击语义。
- `renderer`：默认创建双主体绘制器；宿主可先创建并处理失败，再显式传入。
- `themeTint`、`lightColor`、`centerContainerColor`：材质染色、边缘光颜色与凸起圆钮底色。

胶囊高度、凸起量、圆钮尺寸与单项宽度在 `CouiDockBarDefaults`。凸起圆钮与胶囊的按压、拖动和跟手光由各自节点持有，两条弹簧互不干扰。

源码见 [DockBar](../../coui/src/main/java/com/itos/coui/navigation/DockBar.kt)。

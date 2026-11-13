# 📭空状态

`EmptyState` 在无数据、无结果或未开始时说明当前状态，由图标槽、标题、说明和操作槽自上而下居中排列。组件位于 `com.itos.coui.emptystate`。

## 基本用法

标题必填，说明与两个内容槽可选。只给标题时就是最小的空状态：

~~~kotlin
EmptyState(title = "暂无记录")
~~~

补充说明时传入 `description`，两段文字都按可用宽度居中，标题最多 2 行、说明最多 3 行，超出按尾部省略：

~~~kotlin
EmptyState(
    title = "暂无记录",
    description = "新增内容会显示在这里",
)
~~~

`description` 传 `null` 或空字符串时不占位，文字列直接收拢到标题高度。

## 图标槽与操作槽

`icon` 与 `action` 都是 `@Composable () -> Unit`，由宿主决定内容，组件只提供位置和尺寸。操作槽也可以直接用文字入口 `actionLabel` + `onAction`，由组件按原厂样式渲染：

~~~kotlin
EmptyState(
    title = "暂无记录",
    description = "新增内容会显示在这里",
    icon = { Icon(CouiIcons.Outlined.Search, null, Modifier.size(48.dp)) },
    actionLabel = "刷新",
    onAction = { refresh() },
)
~~~

- 图标槽固定 120dp × 100dp，内容居中绘制，对应原厂 `EmptyStateAnimView` 的占位；传入 24dp 图标时四周留白不变，需要铺满时在槽内使用 `Modifier.fillMaxSize()`。
- 操作槽在文字下方 8dp 处，内容宽度不受 280dp 文字列限制。原厂 `empty_view_action` 是 14sp medium 的文字操作（`?couiTextButtonM` + `?couiColorPrimaryText`，`CouiTextKind.ButtonLabel`），`actionLabel` + `onAction` 就按这一规格渲染：无容器、无内边距，点击热区即文字边界。
- `actionLabel` 与 `onAction` 需同时非空才渲染；与 `action` 槽互斥，两者同时传入时以 `action` 槽为准。需要强调（主按钮）或更大的触控目标时改用 `action` 槽，例如 `action = { Button(onClick = refresh, label = "刷新") }`。
- 两个槽都不提供禁用、加载等状态语义，交互与状态归槽内组件和宿主。

## 尺寸与配色

数值取自 ColorOS 17 手机管家 17.3.13 内嵌的 `coui_component_empty_state.xml`：

| 参数 | 取值 | 原厂资源 |
| --- | --- | --- |
| 图标槽 | 120dp × 100dp | `coui_component_empty_anim_view_width_normal` / `_height_normal` |
| 文字列宽 | 280dp | `coui_component_empty_group_width` |
| 说明距标题 | 2dp | `coui_component_empty_view_subtitle_margin_top` |
| 操作距文字 | 8dp | `empty_view_action` 的 `layout_marginTop` |
| 标题 | 16sp medium、最多 2 行 | `?couiTextHeadlineXS` + `empty_view_title` 的 `maxLines` |
| 说明 | 12sp、最多 3 行 | `?couiTextBodyXS` + `empty_view_subtitle` 的 `maxLines` |
| 操作 | 14sp medium | `?couiTextButtonM` + `?couiColorPrimaryText` |

标题使用 `CouiTextKind.ItemTitle`（16sp medium），说明使用 `CouiTextKind.Body`（12sp，对应原厂 `couiTextBodyXS`），操作文字使用 `CouiTextKind.ButtonLabel`（14sp medium，对应 `couiTextButtonM`）。

文字色默认由 `CouiEmptyStateDefaults.colors(dark)` 提供：标题取 `couiColorLabelPrimary`（`CouiTheme.colorScheme.onSurface`），说明取 `couiColorLabelSecondary`（`onSurfaceVariant`）。需要局部覆盖时直接传 `CouiEmptyStateColors`，不会影响槽内组件：

~~~kotlin
EmptyState(
    title = title,
    description = description,
    colors = CouiEmptyStateColors(title = contentColor, description = hintColor),
)
~~~

## 高度与滚动

组件只负责内容布局，自身按内容高度收拢，竖向从顶部起排——与原厂根布局的 `gravity="center_horizontal"` 一致，不自行垂直居中。需要整页居中时，把组件放进自带居中策略的容器：

~~~kotlin
Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    EmptyState(
        title = title,
        description = description,
    )
}
~~~

原厂把标题与说明包在最大 92dp 的滚动视图（`coui_component_empty_text_scroll_max_height`）内，本组件改用行数上限和省略号收敛高度，超出内容由宿主页面滚动。`modifier` 在根节点应用一次，默认宽度铺满可用空间，内部尺寸随后追加。

## Demo 示例

标题、说明与操作槽的组合见 [DemoEmptyStateSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoEmptyStateSamples.kt)，库入口见 [EmptyState](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/emptystate/EmptyState.kt)。

# 🧩卡片

`Card` 是通用卡片容器：圆角轮廓、卡片底色与内留白，内部布局完全由调用方决定。

它不做列表、不做标题与说明的排版，也不自带 elevation 与描边。带分组标题与说明的成组卡片用 [Group](group.md)，需要图标 + 标题 + 说明 + 操作的提示卡用[操作卡](action-card.md)。

原厂布局树里没有卡片控件类，成组的界面内容一律是带圆角的 `FrameLayout`——`references/screenshots` 的 35 份布局树中共 525 个，圆角取自 `coui_round_corner_*` 的 24dp 一档。因此组件只固定轮廓、底色与内留白。

## 最小调用

~~~kotlin
Card {
    Text(text = title, kind = CouiTextKind.ItemTitle)
    Text(text = summary, kind = CouiTextKind.ItemSummary, color = CouiTheme.colorScheme.onSurfaceVariant)
}
~~~

内容按 `ColumnScope` 纵向排列，组件只加 16dp 内留白，行间距由内容自己给出。

## 可点卡片

传入 `onClick` 时整卡可点，并复用列表行的按压蒙层：

~~~kotlin
Card(onClick = { openDetail() }) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, kind = CouiTextKind.ItemTitle, modifier = Modifier.weight(1f))
        Icon(CouiIcons.Filled.Next, contentDescription = null)
    }
}
~~~

- 蒙层用 `couiButtonFeedback(list = true)`，与 `Group` 内的行一致：按下立即起、短点按到达 0.7 再退场、焦点丢失时释放。
- 蒙层本身不裁剪圆角，由容器先 `clip` 再叠蒙层，所以悬停不会溢出到卡片外。
- `enabled = false` 时保留底色并忽略点击；不传 `onClick` 时卡片是纯静态容器，交互交给内部子控件。

## 轮廓与内留白

~~~kotlin
Card(shape = CouiTheme.shapes.medium, containerColor = CouiTheme.colorScheme.container, contentPadding = 12.dp) {
    Text(text = label, kind = CouiTextKind.ItemSummary)
}
~~~

- `shape`：默认 `CouiTheme.shapes.card`（24dp、权重 3 的平滑超椭圆）；更紧凑的嵌套容器传 `medium`（12dp）。
- `containerColor`：默认取当前外观的卡片色 `CouiTheme.colorScheme.card`；需要玻璃材质时传 `Color.Transparent`，外层用 `Modifier.couiFloatingSurface` 包裹，材质逻辑不在本组件内复制。
- `contentPadding`：默认 `CouiCardDefaults.padding`（16dp），与分组标题行留白对齐。

## 与 Group 的分工

| | `Card` | `Group` |
| --- | --- | --- |
| 分组标题与说明 | 无 | 有，`title` / `footer` |
| 页面左右留白 | 无，由调用方给 | 固定 16dp `horizontalMargin` |
| 内部结构 | 完全自由 | 按行（`Item`）组织，可插 `Divider` |
| 适用场景 | 单张卡片、自定义内容块 | 设置项、入口列表 |

## Demo 示例

静态卡、可点卡与禁用示例见 [DemoContainerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoContainerSamples.kt)，库入口见 [Card](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/card/Card.kt)。

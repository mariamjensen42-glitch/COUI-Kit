# 表面

`Surface` 是通用表面：铺底色、按形状裁剪、可选描边，并把内容色下发给子树。用于弹层、浮层与自定义容器；需要标题、说明和尾部槽位时用[卡片](card.md)，需要分组语义时用 [Group](group.md)。

## 最小调用

```kotlin
Surface(Modifier.fillMaxWidth()) {
    Text(Modifier.padding(16.dp), text = "下载完成", kind = CouiTextKind.Body)
}
```

子项里的 `Text`、`Icon` 会读到 `Surface` 下发的 `LocalCouiContentColor`，不需要重复传色。

## 参数

| 参数 | 说明 |
| --- | --- |
| `modifier` | 外部布局；表面自身不提供尺寸与内边距 |
| `shape` | 裁剪与描边形状，默认 `CouiTheme.shapes.card`，随宿主主题替换 |
| `color` | 底色，默认 `CouiTheme.colorScheme.card` |
| `contentColor` | 下发的 `LocalCouiContentColor`，默认 `onSurface` |
| `border` | 可选的 `BorderStroke`；描边沿形状内侧绘制，与裁剪一致 |

## 边界

- 不处理点击、选中、悬停与按压。整块可交互时由外层加 `Modifier.clickable`、`toggleable` 或 `Modifier.couiStateMask`。
- 不绘制投影，也不读取背景源。投影用 `Modifier.couiShadow`，凝光玻璃等材质由外层 `Modifier.couiFloatingSurface` 接入。
- 内容色只作用于这棵子树；局部覆盖顺序见[外观与预设](../guide/appearance.md)。

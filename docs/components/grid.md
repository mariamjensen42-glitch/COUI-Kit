# 网格

`Grid` 是等宽网格：按 `columns` 均分可用宽度，行高取该行最高子项，只做排布。适合静态的入口卡、缩略图与设置分区。

## 最小调用

```kotlin
Grid(Modifier.fillMaxWidth(), columns = 3) {
    repeat(3) {
        Surface(Modifier.height(56.dp)) {
            Text(Modifier.padding(12.dp), text = "入口", kind = CouiTextKind.ItemTitle)
        }
    }
}
```

## 参数

| 参数 | 说明 |
| --- | --- |
| `modifier` | 外部布局与留白；网格自身不加内边距 |
| `columns` | 列数，必须为正数 |
| `horizontalSpacing` | 列间距，默认 `CouiGridDefaults.horizontalSpacing` |
| `verticalSpacing` | 行间距，默认 `CouiGridDefaults.verticalSpacing` |

## 边界

- 不裁剪、不铺底、不建立滚动，也不虚拟化子项；长列表用 `LazyVerticalGrid`。
- 列宽固定为均分值，子项不能跨列。需要 2×1 这类跨列重排时用[控制中心磁贴](quick-setting.md)的 `DeformationTileGrid`；需要按内容宽度换行时用[换行布局](flow-layout.md)。
- 父级必须给出确定宽度。在可滚动容器里约束无界时退化为单列排布，不会除零或抛异常。

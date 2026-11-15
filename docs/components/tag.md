# 🏷️标签

`Tag` 是静态标签：可选左右图标与单行文本，容器按轮廓裁剪内容。它只表达状态，不处理点击。

带图标、标题和说明的提示卡用[操作卡](action-card.md)，未读数量用[角标](badge-tooltip.md)。

## 最小调用

~~~kotlin
Tag(text = "Beta", colors = CouiTagDefaults.colors().copy(container = CouiTheme.colorScheme.accent, content = CouiTheme.colorScheme.onAccent))
~~~

文本 10dp `sans-serif-medium` 单行省略，容器左右各 3dp 内留白。原厂字号以 dp 声明（`coui_default_tag_textsize`），不随系统字号缩放。

不传 `colors` 时取原厂 attr 默认值：容器 `?attr/couiColorBackgroundWithTag`（`Theme.COUI` 里没有赋值，这里用 `coui_color_container8` 的中性薄填充）、内容 `coui_color_white`。这组默认适合深色实心底，日常用标签请像上面这样显式给出配色。

## 图标

左图标与右图标都是内容槽，尺寸与位置由 `CouiTagDefaults` 给出：

~~~kotlin
Tag(text = "新品", leftIcon = { Icon(CouiIcons.Filled.Rings, contentDescription = null, modifier = Modifier.fillMaxSize()) })
~~~

左图标固定 5×10dp，上下各留 3dp、与文本间隔 2dp；右图标按内容尺寸排列，与文本之间没有额外间距——这两处都来自原厂 `coui_tag_view_layout.xml`。

## 轮廓与描边

轮廓默认直角（原厂 `couiTagCornerRadius` 默认 0）。四角可以分别给半径：

~~~kotlin
Tag(text = "圆角", shape = RoundedCornerShape(topStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 0.dp))
~~~

需要描边时传入宽度与颜色，宽度为 0 时不绘制：

~~~kotlin
Tag(text = "描边", strokeWidth = 1.dp, colors = CouiTagDefaults.colors().copy(container = Color.Transparent, stroke = accent))
~~~

## 配色

容器与内容色由 `CouiTagDefaults.colors(dark)` 提供，可整体替换或逐项覆盖：

~~~kotlin
Tag(text = "推荐", colors = CouiTagDefaults.colors(dark).copy(container = CouiTheme.colorScheme.card, content = CouiTheme.colorScheme.accent))
~~~

原厂容器色读 `?attr/couiColorBackgroundWithTag`，该属性在 `Theme.COUI` 中没有赋值，文本默认色是 `coui_color_white`；两层默认值原本就假设使用方会逐个指定，文档里的示例因此都显式传入了配色。

## 装配

标签不处理点击、焦点与语义角色。需要点击时把交互放在外层，避免标签内部再挂一次手势：

~~~kotlin
Row(Modifier.clickable(onClick = { open() })) { Tag(text = label) }
~~~

窄容器中需要省略号时，把宽度约束交给外层（例如 `Modifier.widthIn(max = …)`），标签自身按可用宽度单行截断。

## Demo 与源码

示例见 [DemoTagSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoTagSamples.kt)，公开入口见 [Tag](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/tag/Tag.kt)。

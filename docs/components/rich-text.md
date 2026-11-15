# 🔗富文本

`Text` 的 `AnnotatedString` 重载承载行内样式与可点击链接：链接、强调等范围样式由调用方写进文本，组件只负责排版。

普通单段文字用另一个只收 `String` 的重载；两者的 `CouiTextKind`、颜色、对齐与最大行数语义相同。

## 最小调用

~~~kotlin
val styles = CouiTextLinkDefaults.textLinkStyles()
val text = buildAnnotatedString {
    append("继续即表示你同意")
    withLink(LinkAnnotation.Clickable("privacy", styles) { openPolicy() }) { append("隐私政策") }
    append("与服务条款。")
}
Text(text = text, kind = CouiTextKind.Body)
~~~

`buildAnnotatedString` 与 `withLink` 来自 Compose 文本 API，链接的点击回调也由宿主提供；组件不读取业务状态。

## 链接配色

`CouiTextLinkDefaults.textLinkStyles()` 返回可直接传给 `LinkAnnotation.Clickable` 的 `TextLinkStyles`：

| 状态 | 来源 |
| --- | --- |
| 常规 | `?couiColorLink`（`?couiColorLabelTheme`，日间 `#0080FF`、夜间 `#1A8CFF`） |
| 按下、聚焦、悬停 | `#4D2660F5` |
| 禁用 | 日间 `#26000000`、夜间 `#26FFFFFF` |

三种可表达状态都取自原厂 `color/coui_clickable_text_color` 选择器，且都不加下划线。

Compose 的 `TextLinkStyles` 没有禁用态：链接不可用时应改用 `CouiTextLinkDefaults.colors(dark).disabled` 的普通 span，不挂 `LinkAnnotation`。

~~~kotlin
val colors = CouiTextLinkDefaults.colors()
val text = buildAnnotatedString {
    withStyle(SpanStyle(color = if (enabled) colors.normal else colors.disabled)) { append(label) }
}
~~~

## 行内样式

范围样式（粗体、颜色、字号）与其他 `AnnotatedString` 用法一致。span 自带的颜色优先于 `Text` 的 `color` 参数：

~~~kotlin
val text = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) { append(title) }
    append(" ")
    withStyle(SpanStyle(color = CouiTheme.colorScheme.accent)) { append(value) }
}
Text(text = text, kind = CouiTextKind.ItemSummary)
~~~

## 装配

链接的可点击区域来自文本排版，命中范围与字形一致；整行还承担其他交互时，不要让外层再叠加一次点击处理。TalkBack 会朗读整段文本，链接的可访问性由 `LinkAnnotation` 提供，不需要额外的语义包装。

## Demo 与源码

示例见 [DemoRichTextSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoRichTextSamples.kt)，公开入口见 [Text](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/text/Text.kt) 与 [CouiTextLinkDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/text/CouiTextLinkDefaults.kt)。

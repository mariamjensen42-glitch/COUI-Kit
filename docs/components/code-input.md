# 验证码输入

`CodeInputView` 是一排等宽圆角格子，整段验证码由一个不可见输入框承接键盘与粘贴，位于 `com.cycling.coui.textfield`。它只渲染格子与当前输入位置，验证码由宿主持有。

单行或多行文本输入用[输入框](text-field.md)，多行文本卡片用[多行输入卡](multi-input.md)。

## 最小调用

~~~kotlin
var code by remember { mutableStateOf("") }
var error by remember { mutableStateOf<String?>(null) }

CodeInputView(
    code = code,
    onCodeChange = { code = it },
    modifier = Modifier.fillMaxWidth(),
    length = 6,
    onComplete = { value -> error = verify(value) },
)
~~~

`code` 是完整验证码，超过 `length` 的部分被截断；输入到满 `length` 位时回调 `onComplete`。`onCodeChange` 每次都收到去掉超长部分后的完整新值，宿主不需要自己截断。

## 尺寸与间距

格子按屏宽缩放：`缩放系数 = min(屏宽dp, 360) / 360`，格子基准 42×46dp，即窄屏按比例缩小、宽屏不再放大。间距在可用宽度里重算后夹在 2dp 与 8dp 之间：

```
raw     = (可用宽 - 格子数 * 格子宽 - 2 * 16dp) / (格子数 * 2 - 2)
spacing = raw.coerceIn(2dp, 8dp)
```

结果由 `Row` 的 `spacedBy` 居中排布，两侧不留额外内边距；需要与页面其他行对齐时由调用方在外面加 `padding`。

## 输入与动画

| 动作 | 表现 |
| --- | --- |
| 输入一位 | 该格数字从 0.6 缩放到 1、透明度 0→1，100ms |
| 删除一位 | 数字在原位淡出 100ms，起始延迟 33ms |
| 当前格变化 | 描边 100ms 淡入，起始延迟 33ms |

当前输入位置取“已输字符数”所在格；输满后停在最后一格。描边颜色取 `CouiCodeInputColors.cellStroke`（默认强调色），宽度 1.6dp 并整体内缩半个线宽，不做外扩。

`secure = true` 时已填格只画 5dp 圆点、不显示数字，也不播放数字缩放动画；描边动画与普通模式相同。

## 配色与排版

~~~kotlin
CodeInputView(
    code = code,
    onCodeChange = { code = it },
    colors = CouiCodeInputDefaults.colors(dark = true),
    metrics = CouiCodeInputDefaults.metrics.copy(cornerRadius = 12.dp),
)
~~~

`CouiCodeInputDefaults.colors(dark)` 给四个角色：`cellBackground`（卡片色）、`cellStroke`（强调色）、`number`（正文色）、`dot`（日间 85% 黑、夜间 85% 白）。`CouiCodeInputDefaults.metrics` 是完整度量，可 `copy` 单项覆盖；`CouiCodeInputDefaults.textStyle()` 是 30sp 常规字重。

`enabled = false` 时整体按 0.3 透明度绘制，输入与聚焦都停止，已输入内容仍然显示。

## 与原厂的差异

- 原厂用 `EditText` + `TextWatcher`，先清空再按字符重建列表，退格键删除列表末位；这里用一个透明 `BasicTextField` 承接输入，退格删除光标前一位且直接改写整段值。可见结果在追加输入时一致，光标居中插入时行为不同。
- 原厂首格自动请求焦点并在点击时唤起键盘；这里不主动抢焦点，也不自弹键盘，键盘由系统在点击透明输入框后打开。
- 原厂把每个格子实现为 View 并挂无障碍虚拟节点；这里格子是 Canvas 绘制，不提供逐格语义，读屏按整段验证码朗读。
- 原厂 `couiCodeInputCount` 与 `couiEnableSecurityInput` 是 XML 属性，这里对应 `length` 与 `secure` 参数。

## Demo 与源码

示例见 [DemoTextFieldFamilySamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoTextFieldFamilySamples.kt)，公开入口见 [CodeInputView](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/textfield/CodeInputView.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/textfield/CouiCodeInputDefaults.kt)。

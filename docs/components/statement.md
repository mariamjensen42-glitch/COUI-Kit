# 📜隐私声明

`PrivacyStatement` 是全屏隐私声明页，对应 COUI 的 `COUIFullPageStatement`：居中标题、可滚动的正文、174dp 宽的主按钮与底部文字拒绝入口。系统应用里「继续前请阅读」这类页面都由它承担。

结构自 `coui_full_page_statement.xml` 逐项移植：

- 标题 16sp / 行距 1.05 / `sans-serif-medium`，上下留白 12dp，左右 24dp，超过 90dp 后自身滚动。
- 正文 13sp / 行距 1.1、次要色，内边距 12dp 顶 / 14dp 底 / 24dp 左右，最多 248dp 后滚动。
- 主按钮 174dp 宽、上留白 20dp、下留白 16dp；下方文字按钮 16sp `sans-serif-medium`、最小高度 32dp、底部留白 24dp。
- `smallestScreenWidthDp < 480` 且横屏时，原厂切到 `button_layout_land`：一行两个等宽按钮，高 44dp、左右外边距 37dp、中间各 4dp。

## 最小调用

~~~kotlin
PrivacyStatement(
    title = "继续前请阅读",
    statement = buildAnnotatedString {
        append("本应用会收集基础诊断信息……")
        withLink(LinkAnnotation.Url("https://example.com/privacy")) { append("隐私政策") }
    },
    confirmText = "同意并继续",
    exitText = "暂不同意",
    onConfirm = { /* 宿主提交 */ },
    onExit = { /* 宿主返回 */ },
    modifier = Modifier.fillMaxSize(),
)
~~~

正文接受 `AnnotatedString`，链接样式写在 `LinkAnnotation` 里；纯文本用 `String` 重载即可。

## 参数

- `title` / `statement`：标题与正文；正文是滚动区域，高度由剩余空间决定，最多 248dp。
- `confirmText` / `exitText`：两个按钮的文案，由宿主给出，组件不内置「同意」。
- `onConfirm` / `onExit`：业务提交与退回，都由宿主实现；组件不持有勾选或同意状态。
- `dark`：取色模式，标题用 `onSurface`、正文用 `onSurfaceVariant`、文字按钮用 `accent`。
- `modifier`：组件占满父容器，尺寸与安全区由宿主的 Scaffold 决定。

固定尺寸都在 `CouiStatementDefaults`，包括横屏双按钮栏的 44dp / 37dp / 4dp 与 480dp 的判定阈值。组件不负责滚动条样式、返回键与提交流程。

## 与原厂的差异

- 正文没有 `requiresFadingEdge="vertical"` + 32dp 的上下渐隐；Compose 侧没有等价的滚动渐隐开关，需要时由宿主外壳加遮罩。
- 小屏竖屏的 `coui_full_page_statement_tiny` 布局未移植（16dp 留白、76dp 功能区的紧凑形态）。
- 标题与正文不做 `COUIChangeTextUtil.adaptFontSize` 的自动缩字；正文过长时靠滚动而不是缩字号。
- 主按钮复用库内 `Button` 的 `Primary` 样式，圆角与高度跟随按钮组件，不单独复刻 `?couiButtonColorfulDefaultStyle`。
- 原厂 `setCustomView()` 的附加功能区（勾选框、协议入口）未内置，需要时由宿主在正文下方自行装配。

## Demo

完整示例见 [DemoStatementSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoStatementSamples.kt)：420dp 高的容器里放整页声明，两个回调把结果写到页面下方。

源码见 [PrivacyStatement](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/statement/PrivacyStatement.kt)、[CouiStatementDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/statement/CouiStatementDefaults.kt)。

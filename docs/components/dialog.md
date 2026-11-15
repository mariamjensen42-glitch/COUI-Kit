# 💬弹窗

`Dialog` 用于显示确认、提示等弹窗，可以放在屏幕底部或居中显示。需要单行输入时，可以使用 `InputDialog`。任务执行期间可以使用 `LoadingDialog` 显示加载动画和提示。这些组件位于 `com.cycling.coui.dialog`。

[确认弹窗](#显示一个简单确认弹窗) · [输入弹窗](#输入型弹窗) · [加载弹窗](#显示加载弹窗) · [背景材质](#使用背景材质)

## 显示一个简单确认弹窗

### 最小调用

在页面的 Composable 中加入下面的代码。示例进入组合时显示弹窗；`title`、`confirmLabel`、`cancelLabel` 和 `onConfirm` 由页面提供：

~~~kotlin
var visible by remember { mutableStateOf(true) }

Dialog(
    visible = visible,
    title = title,
    onDismissRequest = { visible = false },
    confirmAction = CouiDialogAction(confirmLabel, onClick = {
        onConfirm()
        visible = false
    }),
    dismissAction = CouiDialogAction(cancelLabel, onClick = { visible = false }),
)
~~~

确认时示例先调用 `onConfirm`，再将 `visible` 设为 `false`；取消时直接设为 `false`。返回键和点击外部会调用 `onDismissRequest`。组件不会自动更新页面的显隐状态。要从按钮打开弹窗，可以将 `visible` 的初值设为 `false`，再在按钮回调中设为 `true`。标题和正文可以传 `null`，对应区域不会显示。两个短按钮横排；单个按钮、三个按钮或文案放不下时自动竖排。

### 控制显隐

传入 `visible` 时，将调用持续保留在组合中。`true` 开始入场，`false` 开始退场；库在退场结束后释放窗口。需要背景材质时，移动和背景采样使用同一帧的位移、缩放，动画中途可以反向。`Dialog` 和两个 `InputDialog` 重载均支持此参数。不要再用 `if (visible)` 包裹这类调用，否则退场内容会立即移除。

也可以省略 `visible`，由外层条件创建窗口。此时使用系统窗口动画，不提供移动过程中的实时采样。下面的 `visible`、`onDismiss`、`confirmLabel` 和 `onConfirm` 由页面提供；需要关闭时，由相应回调更新页面状态：

~~~kotlin
if (visible) {
    Dialog(
        onDismissRequest = onDismiss,
        confirmAction = CouiDialogAction(confirmLabel, onConfirm),
    )
}
~~~

## 添加第三个操作

通过 `neutralAction` 添加第三个操作。例如，在确认和取消之外增加重置按钮：

~~~kotlin
Dialog(
    onDismissRequest = onDismiss,
    dark = dark,
    title = title,
    message = message,
    confirmAction = CouiDialogAction(
        text = confirmLabel,
        onClick = onConfirm,
        enabled = canConfirm,
        style = CouiDialogActionStyle.Primary,
        colors = CouiDialogDefaults.primaryActionColors(accent),
    ),
    dismissAction = CouiDialogAction(cancelLabel, onDismiss),
    neutralAction = CouiDialogAction("重置选择", onReset),
)
~~~

三个按钮竖排时，从上到下依次是 `neutralAction`、`confirmAction` 和 `dismissAction`。红字是 `neutralAction` 的默认文字色，取自弹窗的 `colors.destructive`。三个按钮都可以各自传入 `CouiDialogAction.colors`，分别指定底色、文字色和禁用态配色。选择 `Primary` 样式时需显式传入按钮配色。`enabled = false` 只禁用对应按钮。

如果三个按钮都使用普通配色，让它们沿用默认的 `Secondary` 样式，并把 `destructive` 设为普通操作的文字色 `action`。下面的文案和操作回调由宿主提供。

~~~kotlin
val colors = CouiDialogDefaults.themeColors(dark)

Dialog(
    onDismissRequest = onDismiss,
    dark = dark,
    title = title,
    colors = colors.copy(destructive = colors.action),
    confirmAction = CouiDialogAction(confirmLabel, onConfirm),
    dismissAction = CouiDialogAction(cancelLabel, onDismiss),
    neutralAction = CouiDialogAction(alternativeLabel, onAlternative),
)
~~~

## 使用自定义正文

需要在正文中放列表或选择器时，可以使用 `content` 槽位：

~~~kotlin
Dialog(
    onDismissRequest = onDismiss,
    dark = dark,
    title = title,
    confirmAction = CouiDialogAction(confirmLabel, onConfirm),
    dismissAction = CouiDialogAction(cancelLabel, onDismiss),
) {
    Text("这里放页面自己的内容")
}
~~~

`content` 位于正文滚动区。操作区独立排列；正文较长时，按钮仍保留在窗口内。示例中的 `Text` 使用 `com.cycling.coui.text.Text`。

## 输入型弹窗

需要单行输入时，可以使用 `InputDialog`。下面用 `TextFieldState` 保存草稿，仅在输入非空时启用保存按钮：

~~~kotlin
@Composable
fun RenameDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    dark: Boolean,
    accent: Color,
    title: String,
    message: String,
    saveLabel: String,
    cancelLabel: String,
    clearLabel: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    if (!visible) return
    val editor = rememberTextFieldState()
    InputDialog(
        modifier = modifier,
        state = editor,
        onDismissRequest = onDismiss,
        dark = dark,
        accent = accent,
        title = title,
        message = message,
        clearContentDescription = clearLabel,
        confirmAction = CouiDialogAction(
            text = saveLabel,
            onClick = { onSave(editor.text.toString()) },
            enabled = editor.text.isNotBlank(),
        ),
        dismissAction = CouiDialogAction(cancelLabel, onDismiss),
    )
}
~~~

编辑不会自动提交或关闭弹窗。示例在 `onSave` 中传出输入值，由页面保存并关闭弹窗；关闭后重新打开会创建新的输入状态。需要保留草稿时，可以将 `TextFieldState` 提到弹窗外层。

页面负责设置长度、内容校验和确认条件。`InputDialog` 支持 `TextFieldState` 和 `TextFieldValue`。需要按替换范围过滤粘贴或输入法组合输入时，使用 `state` 版本的 `inputTransformation`；程序性的 `state.edit` 和清除不会经过该转换。

默认会在窗口获焦后请求输入焦点和键盘。需要手动点击输入区时，设置 `autoFocus = false`。

传入 `visible` 时，卡片随键盘展开、收起而移动，背景模糊采样同步跟随卡片位置。关闭弹窗会立即释放输入焦点并收起键盘，卡片从当前位置退场。省略 `visible` 时，使用系统窗口的 `ADJUST_RESIZE` 避让键盘。两种方式均不需要额外添加 `imePadding`；自定义 `properties` 时，保留 `decorFitsSystemWindows = true` 和 `usePlatformDefaultWidth = false`。

### 宿主页的键盘避让

宿主页如果使用 `imePadding()`，只在自己的窗口拥有焦点时启用。输入弹窗打开后，宿主页仍可能收到键盘尺寸；继续应用间距会提前空出整块区域，在键盘升起前露出窗口底色。

在宿主 Activity 的 Composable 中创建以下 Modifier，替换页面原来的无条件 `imePadding()`，并传给现有的 Scaffold 或页面容器。焦点判断放在宿主页，不放在 `InputDialog` 内部：

~~~kotlin
val imePadding = if (LocalWindowInfo.current.isWindowFocused) {
    Modifier.imePadding()
} else {
    Modifier
}
val pageModifier = Modifier.fillMaxSize().then(imePadding)
~~~

## 显示加载弹窗

`LoadingDialog` 在屏幕中央显示循环转动的弧线和一行提示。把调用留在组合中，由宿主在任务开始和结束时更新 `visible`。

~~~kotlin
LoadingDialog(
    visible = isWorking,
    message = loadingMessage,
)
~~~

`isWorking` 由宿主的任务状态提供，`loadingMessage` 是宿主的提示文案。提示过长时以省略号收尾。默认屏蔽外点关闭。传入 `onDismissRequest` 后，返回键可请求宿主关闭。需要让点击外部也请求关闭时，同时设置 `dismissOnClickOutside = true`。宿主处理任务和关闭请求，通过 `visible` 控制窗口显隐。

## 多页引导

`GuideDialog` 用横向分页展示多张引导页，底部主按钮在末页换成「知道了」。每页由标题、可选描述和可组合的图片区组成，图片按 `imageHeight` 裁切。

~~~kotlin
GuideDialog(
    pages = listOf(
        CouiGuidePage(title = firstTitle, description = firstDescription, image = { Image(first, null) }),
        CouiGuidePage(title = secondTitle, image = { Image(second, null) }),
    ),
    onFinish = onClose,
    onDismissRequest = onClose,
    onSkip = onClose,
)
~~~

翻页进度同时驱动底部按钮：主按钮从半宽长到整行，次级按钮同步淡出后隐藏；进度取自原厂的 0.15 响应弹簧。`onPageChange` 在翻页停稳后回调。单页时只显示一个整行按钮，文案取 `finishLabel`；不传 `onSkip` 时不显示跳过按钮。

图片区高度默认 328dp，紧凑样式改用 `CouiGuideDialogDefaults.compactImageHeight`。页码指示器最多显示 6 个点，规则同[页码指示器](page-indicator.md)。

`shape` 默认取 `CouiGuideDialogDefaults.shape`（32dp，对应原厂 `coui_dialog_guide_clip_radius_root`），不跟随 `CouiTheme.shapes.dialog`；普通、安全与列表弹窗才使用主题的 45dp 弹窗轮廓。

## 安全弹窗

`SecurityDialog` 在正文下加一段可带链接的说明文案和一个「不再提示」勾选框，默认从底部弹出。

~~~kotlin
SecurityDialog(
    onDismissRequest = onClose,
    confirmAction = CouiDialogAction(agreeLabel, onAgree, style = CouiDialogActionStyle.Primary,
        colors = CouiDialogDefaults.primaryActionColors(accent)),
    title = title,
    message = message,
    statement = buildAnnotatedString {
        append(template)
        addLink(LinkAnnotation.Clickable("privacy", TextLinkStyles(SpanStyle(color = accent))) { onPrivacy() },
            start, end)
    },
    checked = checked,
    onCheckedChange = { checked = it },
)
~~~

`statement` 自己携带链接标注，点击由文本的链接处理器接管；传 `null` 时整段说明省略。`checked` 受控，传 `onCheckedChange = null` 时勾选框只显示不可操作。返回键关闭交给 `onDismissRequest`。

## 列表弹窗

`ListDialog` 提供纯文本操作列表、单选列表和多选列表三种形态，默认从底部弹出。

~~~kotlin
ListDialog(
    items = items,
    onDismissRequest = onClose,
    title = title,
    onItemClick = { index -> onSelect(index) },
)
~~~

`selection = CouiListSelection.Single` 时传入 `checkedIndex` 与 `onCheckedIndexChange`；`CouiListSelection.Multi` 时传入 `checkedIndices` 与 `onCheckedIndicesChange`。多选可用 `maxChecked` 限制数量，达到上限后继续勾选只回调 `onMaxChecked`，不改变选择。

每项可带 `summary` 次要说明，`enabled = false` 的项不响应点击。列表超长时在卡片内滚动，需要固定操作时传入 `confirmAction`。

## 与原厂的差异

- 引导页的翻页手势沿用 Compose 分页的默认行为，原厂自定义的甩动速度阈值、最大/最小甩动时长与衰减系数没有复刻；程序化翻页用 650ms 缓动近似原厂的弹簧插值。
- 引导弹窗的卡片取弹窗最大高度，图片高度只有 328dp 与紧凑 240dp 两档，没有原厂的模糊背景注册回调；材质走公共 `material` + `backdrop` 入口。
- 安全弹窗的勾选框间距使用公共 Checkbox 的度量，原厂 `COUICheckBox` 内部文字与方框间距没有逐像素移植。
- 列表弹窗的分隔线按 1 物理像素绘制（原厂为 0.33dp），项与勾选区的按压反馈使用公共状态蒙层，不是原厂 `coui_alert_dialog_item_background` 波纹；项内图标位没有开放。
- 原厂 `COUIRotatingDialogBuilder`（旋转加载弹窗）、推荐按钮和 Tiny 样式没有移植。

## 使用背景材质

要让背景模糊随弹窗一起移动，在 API 33 及以上提供窗口背景源，并把显隐状态传给 `visible`。下面的 `page` 是原有宿主页，可以包含 COUI `Scaffold`。把背景修饰符挂到页面容器上，弹窗留在该容器之外。

~~~kotlin
@Composable
fun BlurredDialogPage(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    page: @Composable () -> Unit,
) {
    val supported = CouiDialogDefaults.isMaterialSupported()
    ProvideCouiMenuBackdrop { windowSource ->
        Box(modifier.fillMaxSize().then(if (supported) windowSource else Modifier)) {
            page()
        }
        Dialog(
            visible = visible,
            title = title,
            onDismissRequest = onDismiss,
            confirmAction = CouiDialogAction(confirmLabel, onDismiss),
            material = if (supported) CouiDialogDefaults.material(CouiTheme.isDark) else null,
        )
    }
}
~~~

宿主打开弹窗时将 `visible` 设为 `true`，在 `onDismiss` 中改为 `false`。保留弹窗调用与窗口背景源，让库在退场结束后释放窗口。`InputDialog` 和 `LoadingDialog` 使用相同的材质接入方式。普通弹窗与输入弹窗也可以省略 `visible`，由外层条件创建窗口，此时系统移动整个窗口，COUI 会采样弹窗到位时的位置。

`material` 非空时须通过 `backdrop` 或 `ProvideCouiMenuBackdrop` 提供有效窗口源。COUI `Scaffold` 自动提供的是栏位使用的正文源。窗口源的作用域见[宿主页面接入](../guide/secondary-pages.md#正文源与窗口源)。Android 12 上传入 `material = null`，使用不透明表面。

启用材质时，`colors.dimAmount` 同时控制窗口遮罩与面板采样的压暗强度。面板先压暗采样，再做模糊和混色。

## 位置和外观

默认从底部显示。需要居中时，设置 `placement = CouiDialogPlacement.Center`：

~~~kotlin
Dialog(
    onDismissRequest = onDismiss,
    confirmAction = CouiDialogAction(confirmLabel, onConfirm),
    dark = dark,
    placement = CouiDialogPlacement.Center,
)
~~~

`dark` 和 `colors` 默认读取局部 `CouiTheme`；没有主题时使用原生预设，模式来自 Local 或系统。字号、字重、行距和 fontScale 由组件控制。自定义正文使用宿主样式。通用规则见[外观与预设](../guide/appearance.md)。

默认 `colors` 使用 `CouiDialogDefaults.themeColors(dark)`。显式自定义时可从该入口复制调整；主操作配色可以使用 `CouiDialogDefaults.primaryActionColors(accent)`。

修改圆角时，传入 `shape = CouiShape(radius, cornerWeight)`。裁剪与材质轮廓从 Shape 读取半径和权重。`cornerRadius` 默认 `Dp.Unspecified`，表示从 Shape 解析；显式传入时必须与 CouiShape 的半径一致。

使用其他自定义 `Shape` 并开启材质轮廓时，须显式提供有限非负的 `cornerRadius`；轮廓权重使用 Dialog 预设。无材质轮廓时只使用 Shape 裁剪。

## 窗口和限制

默认 `DialogProperties` 关闭平台默认宽度，使用 COUI 自己的外框策略。自定义 `properties` 时如果还要保留 COUI 宽度，显式设置 `usePlatformDefaultWidth = false`。

尺寸极小的弹窗和推荐/AI 支付按钮暂不支持。

## Demo 中的样式与示例

Demo 的“组件 → 弹窗”分组包含底部、居中、长内容、多个操作、输入和加载弹窗示例。加载示例会在模拟任务结束后自动关闭。

同分组的“Dialog variants”页面演示多页引导、安全弹窗的说明文案与勾选框，以及文本、单选、多选三种列表弹窗。

支持的设备可以通过“弹窗背景材质”开关比较模糊材质和不透明表面。

Demo 源码见 [DemoDialogSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoDialogSamples.kt)、[输入型示例](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoDialogInputSample.kt) 与[引导、安全、列表示例](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoDialogFamilySamples.kt)。公开参数见 [Dialog / CouiDialogAction](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/Dialog.kt)、[CouiDialogActionStyle / CouiDialogActionColors](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/CouiDialogButton.kt)、[InputDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/InputDialog.kt)、[LoadingDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/LoadingDialog.kt)、[GuideDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/GuideDialog.kt)、[SecurityDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/SecurityDialog.kt)、[ListDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/ListDialog.kt) 与 [CouiDialogDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/dialog/CouiDialogDefaults.kt)。

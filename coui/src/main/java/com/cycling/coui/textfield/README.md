# TextField

## Overview
COUI 文本输入组件包。对外提供 `TextField` 三种受控重载（String / TextFieldValue / TextFieldState）、多行输入卡片 `MultiInputCard` 与验证码输入 `CodeInputView`；输入本体统一由 Foundation `BasicTextField` 承担，外观共享 `CouiTextFieldDefaults`、`CouiMultiInputDefaults`、`CouiCodeInputDefaults` 的度量、配色与文字样式，错误抖动/描边/文案由 `CouiTextFieldRoot` 统一装配。

## Interface
- `TextField` — public 三个 @Composable 重载：`value: String`、`value: TextFieldValue`、`state: TextFieldState` `[TextField.kt:TextField]`
- `CodeInputView` — public @Composable 受控验证码输入，整行透明 BasicTextField 承接键盘与粘贴，满位回调 onComplete `[CodeInputView.kt:CodeInputView]`
- `MultiInputCard` — public @Composable 受控多行输入卡片，含可选提示、最多 5 行正文与 n/max 字数 `[MultiInputCard.kt:MultiInputCard]`
- `CouiTextFieldColors` / `CouiTextFieldDefaults` — public 颜色槽与默认对象；含 `colors`、`textStyle`、`errorColor`、`errorTextStyle` 工厂 `[CouiTextFieldDefaults.kt:CouiTextFieldColors]` `[CouiTextFieldDefaults.kt:CouiTextFieldDefaults]`
- `CouiMultiInputColors` / `CouiMultiInputDefaults` — public 多行卡片颜色槽与默认对象；`colors`/`textStyle`/`counterTextStyle` 工厂 `[CouiMultiInputDefaults.kt:CouiMultiInputColors]` `[CouiMultiInputDefaults.kt:CouiMultiInputDefaults]`
- `CouiCodeInputMetrics` / `CouiCodeInputColors` / `CouiCodeInputDefaults` — public 格子度量、颜色槽与默认对象；含 `colors`/`textStyle` 工厂 `[CouiCodeInputDefaults.kt:CouiCodeInputMetrics]` `[CouiCodeInputDefaults.kt:CouiCodeInputColors]` `[CouiCodeInputDefaults.kt:CouiCodeInputDefaults]`
- `CouiCodeInputDefaults.cellScale` / `cellSpacing` — internal 尺寸缩放比与格子间距计算 `[CouiCodeInputDefaults.kt:cellScale]` `[CouiCodeInputDefaults.kt:cellSpacing]`

## Invariants
- 三个 TextField 重载都调用 `requireTextFieldLines`：多行要求 minLines>=1 且 maxLines>=minLines；singleLine 时传入 BasicTextField 的 minLines/maxLines 强制为 1 `[TextField.kt:requireTextFieldLines]` `[TextField.kt:TextField]`
- String/TextFieldValue 重载完全由 value 驱动，onValueChange 经 rememberUpdatedState 转发，组件不持有文本副本 `[TextField.kt:TextField]`
- state 重载 lineLimits：单行 `SingleLine`、多行 `MultiLine(minLines,maxLines)`；scrollState 默认 `rememberScrollState()` `[TextField.kt:TextField]`
- 错误抖动固定 450ms keyframes（83ms -1、216ms 0.5、333ms -0.5，终点 0），只作用于卡片 offset `[TextField.kt:CouiTextFieldRoot]`
- 描边仅在 error!=null 时用 errorColor（217ms 过渡），否则 Transparent；错误文案保留最后一条非空内容直到 283ms 淡出结束 `[TextField.kt:couiTextFieldSurface]` `[TextField.kt:CouiTextFieldError]`
- 标签状态优先级：label 空→Hidden；useLabelAsPlaceholder 空值→Inline、有值→Dismissed；有值→Anchored；空值有 placeholder→Anchored；否则 Inline `[TextField.kt:CouiTextFieldDecoration]`
- 浮动量仅在 Anchored 时以 spring(1f, 1754.5962f) 趋近 1；标签上移 labelShift*float，占位与输入同步下移同值，卡片高度不变 `[TextField.kt:CouiTextFieldDecoration]`
- 聚焦态不画描边、不改底色与标签颜色，编辑态唯一线索是光标；图标槽与留白不拦截输入焦点 `[TextField.kt:CouiTextFieldDecoration]`
- `CodeInputView` 入口 require(length>0)，渲染值固定 `code.take(length)`；activeIndex 满位时钳为 length-1 `[CodeInputView.kt:CodeInputView]`
- onValueChange 先 `raw.take(length)` 再回调，仅 `next.length == length` 时调用 onComplete，不按内容变化去重 `[CodeInputView.kt:CodeInputView]`
- 透明输入框 matchParentSize 覆盖整行、graphicsLayer alpha=0、KeyboardType.Number；组件自身不 requestFocus、不主动弹键盘 `[CodeInputView.kt:CodeInputView]`
- 格子删除先 33ms 延迟把 appear 归零、结束后才清空 shown；新增先写 shown、snapTo(0) 再 100ms 升至 1，缩放自 0.6 起 `[CodeInputView.kt:CodeInputCell]`
- cellScale = min(screenWidthDp, 360)/360；cellSpacing 公式结果夹在 minCellSpacing..maxCellSpacing 后取整 `[CouiCodeInputDefaults.kt:cellScale]` `[CouiCodeInputDefaults.kt:cellSpacing]`
- `MultiInputCard` 入口 require(maxLength==null || maxLength>0)；展示值与 onValueChange 均 `take(maxLength)` `[MultiInputCard.kt:MultiInputCard]`
- limitReached 以截断后长度 >= maxLength 判定；满位 emphasized 置位并延迟 1000ms 回落，字数颜色以 250ms 过渡 `[MultiInputCard.kt:MultiInputCard]`
- hint 仅在 text 为空且非 null 时显示、最多 3 行；BasicTextField 固定 maxLines=5；卡片 clickable 仅在 enabled 时 requestFocus 且与输入共用 interactionSource `[MultiInputCard.kt:MultiInputCard]`

## Side Effects
- 平台 IME 输入：所有控件经 BasicTextField 处理键盘、光标与选择；CodeInputView 使用 KeyboardType.Number `[TextField.kt:TextField]` `[CodeInputView.kt:CodeInputView]` `[MultiInputCard.kt:MultiInputCard]`
- CodeInputView 透明 BasicTextField 以 matchParentSize 与 graphicsLayer(alpha=0f) 覆盖整行，仍接收焦点与粘贴事件 `[CodeInputView.kt:CodeInputView]`
- MultiInputCard 点击卡片调用 FocusRequester.requestFocus 拉起焦点与键盘 `[MultiInputCard.kt:MultiInputCard]`
- 读取 LocalConfiguration.screenWidthDp、LocalDensity 与配置计算缩放/行高；textStyle 调用 android.graphics.Typeface.create 并按 API 35 分支 `[CodeInputView.kt:CodeInputView]` `[CouiTextFieldDefaults.kt:textStyle]`
- rememberTextMeasurer 测量字符，Canvas drawRoundRect/drawCircle/drawText 自绘验证码格子 `[CodeInputView.kt:CodeInputCell]`
- Animatable、animateColorAsState、AnimatedVisibility、LaunchedEffect+delay 驱动抖动、描边、文案与字数动画 `[TextField.kt:CouiTextFieldRoot]` `[MultiInputCard.kt:MultiInputCard]`
- 主题读取：CouiTheme.isDark 与 CouiTheme.colorSchemeFor(dark) 生成默认颜色 `[CouiTextFieldDefaults.kt:colors]` `[CouiCodeInputDefaults.kt:colors]`

## Verification
- Run: `./gradlew :coui:lintDebug`

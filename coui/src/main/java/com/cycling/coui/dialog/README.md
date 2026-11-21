# Dialog

## Overview
COUI 弹窗组件包。对外提供 `Dialog`（警告/操作弹窗）、`GuideDialog`（多页引导）、`InputDialog`、`ListDialog`、`LoadingDialog`、`SecurityDialog`；统一由内部 `CouiDialogWindow` 创建并控制平台 Dialog 窗口，`Dialog` 再装配 `CouiDialogActions`、`DialogButton`、`DialogText`，各预设复用 `CouiDialogDefaults` 的尺寸、配色与材质；文字角色由内部 `DialogTextKind` / `rememberDialogTextStyle` 提供。

## Interface
- `Dialog` — public Composable，普通警告弹窗入口，装配窗口、滚动内容区与操作区 `[Dialog.kt:Dialog]`
- `CouiDialogAction` — public 受控操作数据类（text/onClick/enabled/style/colors）`[Dialog.kt:CouiDialogAction]`
- `CouiDialogPlacement` — public 窗口位置枚举 Bottom/Center `[CouiDialogDefaults.kt:CouiDialogPlacement]`
- `CouiDialogColors` / `CouiDialogMaterial` / `CouiDialogDefaults` — public 配色、材质与默认参数对象 `[CouiDialogDefaults.kt:CouiDialogColors]` `[CouiDialogDefaults.kt:CouiDialogMaterial]` `[CouiDialogDefaults.kt:CouiDialogDefaults]`
- `CouiDialogActionStyle` / `CouiDialogActionColors` — public 按钮样式枚举与四色数据类 `[CouiDialogButton.kt:CouiDialogActionStyle]` `[CouiDialogButton.kt:CouiDialogActionColors]`
- `CouiGuidePage` / `GuideDialog` / `CouiGuideDialogDefaults` — public 引导页数据类、多页引导 Composable 与参数 `[GuideDialog.kt:CouiGuidePage]` `[GuideDialog.kt:GuideDialog]` `[CouiGuideDialogDefaults.kt:CouiGuideDialogDefaults]`
- `InputDialog` — public 两个重载：TextFieldValue 与 TextFieldState `[InputDialog.kt:InputDialog]`
- `CouiListItem` / `CouiListSelection` / `ListDialog` / `CouiListDialogDefaults` — public 列表项、选择模式、列表弹窗与度量 `[ListDialog.kt:CouiListItem]` `[CouiListDialogDefaults.kt:CouiListSelection]` `[ListDialog.kt:ListDialog]` `[CouiListDialogDefaults.kt:CouiListDialogDefaults]`
- `LoadingDialog` — public 居中循环加载弹窗 `[LoadingDialog.kt:LoadingDialog]`
- `SecurityDialog` / `CouiSecurityDialogDefaults` — public 安全弹窗与说明可链接文案度量 `[SecurityDialog.kt:SecurityDialog]` `[CouiSecurityDialogDefaults.kt:CouiSecurityDialogDefaults]`
- `CouiDialogWindow` — internal 窗口 owner：窗口配置、进退场动画、遮罩、材质采样与卡片布局 `[CouiDialogWindow.kt:CouiDialogWindow]`
- `LocalCouiDialogSoftInputMode` — internal `staticCompositionLocalOf<Int?>`，默认 null `[CouiDialogWindow.kt:LocalCouiDialogSoftInputMode]`
- `DialogActions` / `DialogButton` / `DialogButtonDefaults` — internal 操作区与按钮装配 `[CouiDialogActions.kt:DialogActions]` `[CouiDialogButton.kt:DialogButton]` `[CouiDialogButton.kt:DialogButtonDefaults]`
- `DialogText` / `DialogTextKind` / `rememberDialogTextStyle` — internal 文字排版角色与样式解析 `[CouiDialogText.kt:DialogText]` `[CouiDialogText.kt:DialogTextKind]` `[CouiDialogText.kt:rememberDialogTextStyle]`

## Invariants
- `CouiDialogAction` 要求 text 非空白；Primary 样式必须显式提供 colors，否则 init 抛异常 `[Dialog.kt:CouiDialogAction]`
- visible 非 null 时窗口保留到退场动画结束，调用方必须让 Dialog 留在组合中；visible 为 null 时依赖外层条件与系统窗口动画 `[Dialog.kt:Dialog]`
- material 非空时要求 API 33 且 backdrop 非空；必须从 DialogWindowProvider 取得 Window，否则 check 失败 `[CouiDialogWindow.kt:CouiDialogWindow]`
- 入场先等 windowSize.height>0 再 snap 初始态，withFrameNanos 后才置 motionStarted；退场结束才清 retained/motionStarted `[CouiDialogWindow.kt:CouiDialogWindow]`
- Bottom 平移进退场、Center 缩放淡出（退出 150ms）；visible=false 时清除 FLAG_DIM_BEHIND 触发系统淡出 `[CouiDialogWindow.kt:CouiDialogWindow]`
- 触摸探索开启时强制 dismissOnClickOutside=false；点击外部仅在落点不位于卡片矩形内时 dismiss `[CouiDialogWindow.kt:CouiDialogWindow]`
- 导航栏高度与手势导航判定须在进入 Dialog 子组合前读取 `[CouiDialogWindow.kt:CouiDialogWindow]`
- DialogCard 外层只记录锚点、动画只作用内层；采样显式应用同一变换逆矩阵；效果缺失时回退纯色 drawRect `[CouiDialogWindow.kt:DialogCard]`
- `CouiDialogColors` 要求 dimAmount 位于 0f..1f；`CouiDialogMaterial` 要求 blurRadius 为 0 或至少 10（Kawase 等级）`[CouiDialogDefaults.kt:CouiDialogColors]` `[CouiDialogDefaults.kt:CouiDialogMaterial]`
- titleMaxHeight 断点：screenHeightDp>=560 取 160dp，>=300 取 120dp，否则 80dp `[CouiDialogDefaults.kt:CouiDialogDefaults]`
- DialogActions 横排顺序为取消、中性、确认，竖排为中性、确认、取消；neutral 用 destructive 色 `[CouiDialogActions.kt:DialogActions]`
- DialogText：Message 多行左对齐、单行居中；Button 最多 2 行 Ellipsis；Title 附加 heading 语义 `[CouiDialogText.kt:DialogText]`
- 输入型弹窗要求 decorFitsSystemWindows=true；autoFocus 须先等窗口获焦再 requestFocus/show 键盘 `[InputDialog.kt:InputDialogFrame]` `[InputDialog.kt:DialogInput]`
- TextFieldValue 重载清除写空 TextFieldValue；TextFieldState 重载程序性 state.edit 不经过 inputTransformation `[InputDialog.kt:InputDialog]`
- ListDialog 四项 require：items 非空、maxChecked 为正、checkedIndex 与 checkedIndices 合法；达到上限仅回调 onMaxChecked `[ListDialog.kt:ListDialog]`
- LoadingDialog 要求 message 非空白；显隐由 visible 控制，任务完成与取消由宿主处理 `[LoadingDialog.kt:LoadingDialog]`
- SecurityDialog 勾选受控（checked 默认 true）；onCheckedChange 为 null 时整行与 Checkbox 禁用 `[SecurityDialog.kt:SecurityCheckbox]`
- GuideDialog 要求 pages 非空；单页禁用 pager 滚动、不显示指示器与跳过按钮 `[GuideDialog.kt:GuideDialog]`

## Side Effects
- 通过 ComposeDialog/CouiDialogWindow 创建平台窗口，并直接写 Window：gravity、透明背景、FLAG_BLUR_BEHIND、dimAmount、FLAG_DIM_BEHIND、窗口动画与 softInputMode `[CouiDialogWindow.kt:CouiDialogWindow]`
- CompositionLocal 声明：`LocalCouiDialogSoftInputMode` 由输入型窗口 owner 下发；windowBackdrop 以 RetainWhileComposed 保活 `[CouiDialogWindow.kt:LocalCouiDialogSoftInputMode]` `[CouiDialogWindow.kt:CouiDialogWindow]`
- 注册/注销 AccessibilityManager.TouchExplorationStateChangeListener `[CouiDialogWindow.kt:CouiDialogWindow]`
- AnimationUtils.loadInterpolator、resources.getInteger(config_mediumAnimTime) 及 display/WindowInsets 平台读取 `[CouiDialogWindow.kt:CouiDialogWindow]`
- rememberGraphicsLayer record/drawLayer、Kawase 模糊与材质混色、Paint/nativeCanvas 轮廓光 `[CouiDialogWindow.kt:DialogCard]`
- 输入法交互：LocalSoftwareKeyboardController.show/hide、LocalFocusManager.clearFocus、windowInfo.isWindowFocused `[InputDialog.kt:DialogInput]`
- stringResource 读取 coui_dialog_* 文案；rememberInfiniteTransition 驱动加载动画 `[GuideDialog.kt:GuideDialog]` `[LoadingDialog.kt:LoadingIndicator]`
- 指针/绘制副作用：couiStateMask 与 clickable、couiTouchMotion/couiPressMotion、rememberCouiSpotlight、Canvas Offscreen+BlendMode.Clear `[ListDialog.kt:ListRow]` `[CouiDialogButton.kt:DialogButton]` `[InputDialog.kt:DialogInputClear]`
- semantics 暴露 Indeterminate progressBarRangeInfo 与 Polite liveRegion `[LoadingDialog.kt:LoadingDialog]`

## Verification
- Run: `./gradlew :coui:lintDebug`

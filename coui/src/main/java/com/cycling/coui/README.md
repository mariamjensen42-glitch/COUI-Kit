# COUI Components Blueprint

## System Overview

本目录是 `coui` 模块的组件与效果源码根。宿主提供页面、导航、受控状态与业务提交；本目录内的包只持有通用布局、局部交互、动画与绘制，并通过 `CouiTheme`、`Theme` token 和 `effect`/`backdrop` 绘制后端装配外观。

~~~text
宿主 App / demo
  └─ coui 组件包（本目录）
       ├─ theme / shape / layout：主题 token、轮廓与页面骨架
       ├─ motion / input / scroll：手势、触觉与滚动物理
       ├─ effect / backdrop：绘制后端与背景采样
       └─ 具体组件包：button、dialog、navigation、textfield 等
~~~

模块只依赖 Android、Compose 与 AndroidX；`coui-icons` 是选项，核心不反向依赖图标包。依赖清单见 `coui/build.gradle.kts` 的 `dependencies`。

## Subdirectories & System Boundaries

- [Accordion](./accordion/README.md) - 受控折叠面板：标题行常驻，内容按 `expanded` 垂直展开/收起。
- [ActionCard](./actioncard/README.md) - 页面提示卡/推荐卡：图标、标题、说明与调用方操作组。
- [Avatar](./avatar/README.md) - 固定尺寸头像容器：只负责裁剪轮廓与底色，不加载图片。
- [Backdrop](./backdrop/README.md) - 背景采样源、离屏录制与模糊/自定义效果链。
- [Badge](./badge/README.md) - 红点/数字角标与 `BadgedBox` 右上角叠加容器。
- [BottomSheet](./bottomsheet/README.md) - `Dialog` 承载的贴边底部面板：自绘遮罩、跟手拖动与嵌套滚动。
- [Button](./button/README.md) - 标签/内容槽 `Button`、`ButtonGroup`、`IconButton` 与按钮 Defaults。
- [ButtonBar](./buttonBar/README.md) - 1–3 项操作按钮条：按文案宽度决定横排或竖排。
- [Card](./card/README.md) - 通用圆角卡片容器，可选整卡点击。
- [Chart](./chart/README.md) - 静态圆角柱状图，可选轴标签与水平网格。
- [Checkbox](./checkbox/README.md) - 复刻 `coui_btn_check` 几何与动效的纯绘制复选框。
- [Chip](./chip/README.md) - `Chip`/`ChipGroup`/`ChipTab` 标签、折叠组与分类栏。
- [ChoiceCard](./choicecard/README.md) - 模式选择卡与缩略图选择/导航卡。
- [Diagnostic](./diagnostic/README.md) - 按需启停的绘制取证、采样层登记与 JSON 快照。
- [Dialog](./dialog/README.md) - `Dialog`/`GuideDialog`/`InputDialog`/`ListDialog`/`LoadingDialog`/`SecurityDialog` 与窗口 owner。
- [Effect](./effect/README.md) - 阴影、漂浮材质、融球、描边、聚光、模糊与状态反馈渲染后端。
- [EmptyState](./emptystate/README.md) - 整页空状态的图标、标题、说明与操作槽布局。
- [FloatingActionButton](./floatingactionbutton/README.md) - API 33 单按钮 `Fab` 与可展开 `FloatingButton`。
- [FloatingToolbar](./floatingtoolbar/README.md) - 胶囊悬浮操作栏：共享玻璃表面、跟手高光与形变。
- [FlowLayout](./flowlayout/README.md) - 可折叠标签流式布局，超出折叠行的标签淡出裁剪。
- [Input](./input/README.md) - 触觉反馈码、动画中按钮点击、输入重定位边界与合成 CANCEL 识别。
- [Layout](./layout/README.md) - 窗口宽度档位、页面 token、`Group`/`Divider`/`Footer`/`Item`、`QuickActionGrid` 与 `Scaffold`。
- [Menu](./menu/README.md) - 两级浮层菜单、锚点、离屏 Fast Kawase 材质与选择手势。
- [Motion](./motion/README.md) - 弹簧通道、触摸形变、按压缩放、渐进拖动与融球跟随基础算法。
- [Navigation](./navigation/README.md) - `NavigationBar`/`DockBar`/`SideNavigationBar`/`NavigationRail`/浮动与配件导航外观。
- [NotificationCard](./notificationcard/README.md) - 通知中心单条通知的图标、标题、时间与正文排版。
- [PageIndicator](./pageindicator/README.md) - 连续半径插值与 6 点滑动窗口页码指示器。
- [Picker](./picker/README.md) - 滚轮、日期/农历/时间/时限/月历选择与公农历换算。
- [Preview](./preview/README.md) - 深浅色与三档断点预览注解和预览底板，不进发布 API。
- [Progress](./progress/README.md) - 环形/线性/分段/安装进度与骨架占位。
- [PullToRefresh](./pulltorefresh/README.md) - 嵌套滚动下拉刷新容器与 OEM 风格刷新头。
- [QuickSetting](./quicksetting/README.md) - 控制中心开关磁贴、形变网格与竖排档位条。
- [RadioButton](./radiobutton/README.md) - 复刻 `coui_btn_radio` 几何与动效的纯绘制单选按钮。
- [Scroll](./scroll/README.md) - 纵向越界阻尼回弹、SpringOverScroller 物理与 `couiVerticalScroll`。
- [ScrollBar](./scrollbar/README.md) - 延迟淡出的可拖动竖向滚动条。
- [Search](./search/README.md) - 常驻搜索栏与展开取消胶囊的共享转场。
- [SearchHistory](./searchhistory/README.md) - 搜索历史标题、删除按钮与可折叠标签组。
- [SeekBar](./seekbar/README.md) - 图标、分段与竖向三个自绘滑动条。
- [SegmentedControl](./segmentbutton/README.md) - 胶囊分段选择器与指示器弹簧状态机。
- [Shape](./shape/README.md) - 全库圆角 token、`CouiShape` 平滑轮廓与胶囊等值线。
- [SidePane](./sidepane/README.md) - 从起始/结束边滑出的侧栏容器，页面内容常驻。
- [Slider](./slider/README.md) - 受控滑杆：逻辑值、弹簧状态机与 Canvas 渲染器分离。
- [Snackbar](./snackbar/README.md) - 底部单实例提示条：按压暂停计时与弹簧进退场。
- [State](./state/README.md) - 底色、状态蒙层、焦点描边与按压缩放的组合表面。
- [StateLayout](./statelayout/README.md) - 按 `CouiLoadState` 分发加载/就绪/空/失败的容器。
- [Stepper](./stepper/README.md) - 数值步进器与横向步骤条。
- [SwipeReveal](./swipereveal/README.md) - 侧滑露出的尺寸、弹簧与融球预设，不含业务提交。
- [Switch](./switch/README.md) - 开关、拖动吸附与加载态动画。
- [TabLayout](./tablayout/README.md) - 受控单行页签条：`Fixed`/`Scrollable` 与滑动选中胶囊。
- [Tag](./tag/README.md) - 可选左右图标的状态标签，不处理点击。
- [Text](./text/README.md) - `CouiTextKind` 排版角色、`Text`、`MarqueeText` 与链接配色。
- [TextField](./textfield/README.md) - `TextField` 三种受控重载、`MultiInputCard` 与 `CodeInputView`。
- [Theme](./theme/README.md) - 进程级样式注册、`CouiTheme` 作用域、色彩/排版/轮廓 token。
- [Toast](./toast/README.md) - 队列式轻提示 `ToastHost` 与可跨包复用的单条提示表面。
- [Tooltip](./tooltip/README.md) - 锚点上方/下方自适应的非焦点气泡 `Popup`。
- [TopAppBar](./topappbar/README.md) - 固定/折叠顶栏、搜索转场与 `UserFollowBar`。
- [TopTips](./toptips/README.md) - 顶部提示条容器与图标/标题/跑马灯正文/操作/关闭排布。

## Global Invariants

- 外观只经 `CouiTheme` 与各 `*Defaults` 进入组件；组件读取主题 CompositionLocal，不缓存宿主色板或 `Context` `[theme/CouiTheme.kt:CouiTheme]`。
- 受控状态归宿主：展开、可见、选中、勾选、刷新等目标由调用方传入，组件只回调请求；本地仅持有交互与动画 `[switch/Switch.kt:Switch]` `[pulltorefresh/PullToRefresh.kt:PullToRefresh]`。
- 背景采样源唯一：一个 `CouiBackdrop` 只挂到一个正文节点，消费者必须成对 `retain`/`release`，子树不得采样自身 `[backdrop/CouiBackdrop.kt:CouiBackdrop]`。
- 绘制后端复用：组件通过 `effect` 包的 Modifier 或 renderer 装配阴影、材质、描边与聚光，不复制 Shader/模糊算法 `[effect/CouiFloatingSurfaces.kt:CouiFloatingRenderer]`。
- 手势与触觉复用 `motion`/`input` 基础件；触觉按 `enabled && isAttachedToWindow` 门控，失败返回 `false` `[input/CouiHaptics.kt:performCouiHapticFeedback]` `[motion/CouiTouchInput.kt:couiTouchMotion]`。
- GPU 效果入口按 SDK 分层：API 33+ 使用 RuntimeShader/RenderEffect，低版本回退普通绘制或平台模糊 `[effect/CouiEdge.kt:CouiEdge]` `[search/SearchBar.kt:SearchBar]`。
- 用户文案经模块字符串资源提供；核心不依赖 `coui-icons`，组件图标为内联路径或调用方传入 `[pulltorefresh/PullToRefresh.kt:PullRefreshLabel]` `[search/CouiSearchIcon.kt:CouiSearchIcon]` `[seekbar/IconSeekBar.kt:CouiIconSeekBarPaths]`。

## Global Side Effects

- 进程级单例：`Coui.defaultStyle` 由 `initialize` 一次性注册，`CouiRenderDiagnostics` 持有全局登记表 `[theme/Coui.kt:Coui]` `[diagnostic/CouiRenderDiagnostics.kt:CouiRenderDiagnostics]`。
- CompositionLocal 供给：主题、内容色、背景源、工具栏 backdrop/overlay 由上层 Provider 下发，缺省消费者按各包契约抛错或回退 `[theme/CouiTheme.kt:CouiTheme]` `[backdrop/ProvideCouiBackdrop.kt:ProvideCouiBackdrop]` `[topappbar/TopAppBarSurface.kt:TopAppBarSurface]`。
- 平台窗口与弹层：对话框、菜单、气泡与底部面板直接配置 `Window`/`WindowManager`、`Popup` 与窗口动画标志 `[dialog/CouiDialogWindow.kt:CouiDialogWindow]` `[menu/Menu.kt:CouiMenuPopup]` `[tooltip/Tooltip.kt:Tooltip]`。
- 平台绘制资源：RuntimeShader、RenderEffect、Mesh/RenderNode、`GraphicsLayer`/Offscreen、Android `Paint`/`Canvas` 与 `LruCache` `[effect/CouiShadowRenderer.kt:CouiShadowRenderer]` `[shape/CouiCapsuleShape.kt:quarterContours]`。
- 帧时钟与动画：`withFrameNanos`、`Choreographer`、`ValueAnimator`/`AnimatorSet` 与 `androidx.dynamicanimation` 的 `SpringAnimation` `[progress/CouiProgressMotion.kt:rememberCouiPhase]` `[motion/CouiSpring.kt:CouiSpring]`。
- 平台输入与服务：`View.performHapticFeedback`、`VelocityTracker`、`AccessibilityManager`、`NestedScrollConnection` 与 `WindowInsets` `[input/CouiHaptics.kt:performCouiHapticFeedback]` `[dialog/CouiDialogWindow.kt:CouiDialogWindow]`。

## System Verification

- Run: `./gradlew :coui:lintDebug`

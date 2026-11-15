# 更新记录（CHANGELOG）

COUI-Kit 处于早期预览阶段。本文记录公开 API、行为、接入和兼容要求的变化。日期以提交合并时间为准；版本归档以真实 Git Tag 和发行产物为准。

## [未发布]

以下为相对 `0.0.1-alpha11` 的变更。本版起项目只保留 ColorOS 17 一套外观，不再提供任何外部设计体系的适配层。

### 新增与改进

- 新增按钮组 `ButtonGroup`、`CouiButtonGroupItem` 与 `CouiButtonGroupDefaults`：按 `SimpleButtonGroupCtrl` + `COUIButtonLayout` 测量文案与列宽决定横排或竖排（竖排反转顺序），152dp / 174dp 宽度、16dp 与 32dp 容器留白、小屏（<600dp）均分父宽。[按钮](docs/components/button.md#按钮组)
- `Button(label, …)` 新增 `description`：对应原厂 `DescButtonTextSpan` 的两行文案，主文案 16sp、描述 10sp、间距 0dp、各自单行省略，字号沿用 1.15 倍缩放封顶；Compose 排版不能与同一段文字内联拼接。
- 新增按钮条 `ButtonBar`、`CouiButtonBarItem` 与 `CouiButtonBarDefaults`：按 `COUIButtonBarLayout` 的横竖排判定与 44/56dp 高度下限排布 1–3 个操作，内容超高时独立滚动；`Dialog` 的操作区改为复用同一布局，`CouiDialogDefaults` 的按钮尺寸改为引用同一来源。[按钮条](docs/components/button-bar.md)
- 新增展开式悬浮按钮 `FloatingButton`、`FloatingButtonLabel`、`CouiFloatingButtonItem` 与 `CouiFloatingButtonDefaults`：主按钮 56dp、条目自下而上展开（首项 32dp、其余 16dp）、弹簧 500/0.8 与 50ms 错开、主按钮 45° 旋转、5s 自动收起，按压 200/340ms。`OplusViewSeamless` 转场与熔球融合未复刻。[FAB](docs/components/fab.md#展开式悬浮按钮)
- 新增按压参数 `CouiPressFeedbackParams` 与 `CouiPressFeedbackDefaults`，`rememberCouiPressMotion(params)` 可切换原厂两套弹簧（olivelink 与 `pressfeedback`）；新增 `Modifier.couiPressRipple` 承载 `COUIPressRippleDrawable` 的水波纹分支，颜色与 12dp/4dp 外扩按原厂资源，平台波纹节奏为近似实现。[运动与跟随回弹](docs/effects/motion.md#按压参数)
- 新增数值步进器 `Stepper`、`CouiStepperColors` 与 `CouiStepperDefaults`：对应 `COUIStepperView`，受控数值、`-999..9999` 边界夹紧、上下界禁用按钮、单击一次与长按 100ms 连续加减、每次提交触发 `GRANULAR_SHORT_VIBRATE_SYNC`；32dp 圆钮与 18sp 数值的几何、容器色与内容色按原厂 style 与资源取值，加减图标移植 `ic_plus_sign` / `ic_minus_sign`。[数值步进器](docs/components/stepper.md)
- `CouiHapticFeedback` 新增 `GranularShortVibrateSync`（308）。
- 横向步骤条由 `Stepper` 改名为 `StepBar`，Defaults 同步为 `CouiStepBarDefaults`；`Stepper` 这一名称让给与原厂 `COUIStepperView` 同义的数值步进器。
- 新增标签 `Tag` 与 `CouiTagDefaults`：左右图标槽、四角独立轮廓、可选描边与 10dp `sans-serif-medium` 单行文本，几何与字号取自 `COUITagView` / `COUITagBackgroundView` 与 `coui_tag_view_layout.xml`；原厂 `couiColorBackgroundWithTag` 在 `Theme.COUI` 中没有赋值，默认沿用原厂 attr 组合（`couiColorContainer8` 中性薄填充 + `coui_color_white`），实际外观由宿主显式传入。[标签](docs/components/tag.md)
- 新增富文本入口：`Text` 增加 `AnnotatedString` 重载，新增 `CouiTextLinkDefaults` 按 `color/coui_clickable_text_color` 提供链接的常规、按下与禁用三色；Compose 的链接样式没有禁用态，链接不可用时由宿主改用普通 span。[富文本](docs/components/rich-text.md)
- 新增滚动条 `ScrollBar` 与 `CouiScrollBarDefaults`：拇指高度按视口占比、位置按可滚范围占比，滚动后停留 2 秒再在 250 毫秒内淡出，拇指可直接拖动并贴起始侧，几何与配色取自 `COUIScrollBar` 与 `coui_scrollbar_*`；只读取 `ScrollState`，且不取消滚动容器已进行的 fling。[滚动条](docs/components/scroll-bar.md)
- 新增页签条 `TabLayout` 与 `CouiTabItem`、`CouiTabLayoutMode`：文字页签配底部滑动指示器，宽度跟随文字且不小于 32dp，切换时长按原厂 `min(300, 距离 × 50 + 150)`，固定模式等分、滚动模式把选中项滚向中间；自定义视图、图标、红点与 Mediator 联动未移植。[页签条](docs/components/tab-layout.md)
- 新增换行布局 `FlowLayout` 与搜索历史 `SearchHistory`：按测量宽度逐行装填、可折叠到指定行数，展开/折叠的 250/100/400/300ms 淡入淡出与清空时 300ms 整块淡出取自 `COUIFlowLayout`、`COUISearchHistoryView`；搜索历史装配 `FlowLayout`，不复制换行逻辑，chip 按压反馈与字号适配未移植。[换行布局](docs/components/flow-layout.md) · [搜索历史](docs/components/search-history.md)
- 新增 `SnackbarHost` 与 `CouiSnackbarState`：单实例互斥、任意停留时长（`0` 不自动消失）、触摸暂停后按完整时长重新计时、response 0.3/0.25 的弹簧缩放与淡入淡出；容器几何与配色复用轻提示的 `coui_snack_bar_*` 绘制节点，公开轻提示行为不变。父容器位置由宿主的 `Box` 决定，通知样式与拖动消失未移植。[Snackbar](docs/components/snackbar.md)
- 新增跑马灯文本 `MarqueeText`、竖向滑动条 `VerticalSeekBar`、顶部提示条 `TopTips` 与 `TopTipsContent`：分别对应 `COUIMarqueeTextView`、`COUIVerticalSeekBar`、`COUICustomTopTips`。滚动与渐隐、拖动阻尼与惯性、250ms 纵向缩放按原厂资源与默认 style 的 selector 实现，进度色跟随主题强调色；未复刻厂商物理引擎、形变与平滑圆角等私有能力，逐项差异见[覆盖状态](docs/status/coverage.md)。[跑马灯文本](docs/components/marquee-text.md) · [竖向滑动条](docs/components/vertical-seek-bar.md) · [顶部提示条](docs/components/top-tips.md)
- 双主体融球改用 COUI 17 的逐对预计算模型：`CouiMetaballRenderer` 复用 `floatingPair()` 写入的 `pre[24]`，shader 只保留仍在求值的常量，边缘退晕、融合强度、颜色范围与弧线范围与原厂同轴；`blendRangePx` 语义不变，圆角先乘 CONIC 系数再夹到半尺寸。公开签名与文档语义不变。[横向双主体融合](docs/effects/metaball.md)
- `TopAppBarSurface` 新增 `blurFactor`，`drawCouiGradientBackdrop` 相应支持按进度缩放卷积半径：原厂是把 start/end 两个半径同乘 `1 - fraction`，这里作为位置 ratio 之外的独立乘子；`CollapsingTopAppBar` 与 `LargeTopAppBar` 按 `1 - collapsedFraction` 接线，完全收起时不再模糊。[顶栏与按钮](docs/components/top-app-bar.md#背景参数)
- 折叠顶栏背景 alpha 改为原厂双数据源：可见 appbar offset 优先，无折叠数据源时回退越过折叠范围后累计的内容滚动距离，30dp 阈值归入 `CouiCollapsingAppBarDefaults`；状态保存同步该余量并兼容旧存档。[折叠顶栏](docs/components/collapsing-app-bar.md#外观与采样)
- 新增[原厂对照与覆盖状态](docs/status/coverage.md)：按「已对齐 / 有偏差 / 未实现」记录可核验的结论、证据入口与待验收项，供后续按表推进。
- 材质混色改用 COUI 17 的两层模型：新增 `CouiMaterialLayer`（常量色 + `android.graphics.BlendMode`）、`CouiMaterialPreset`、`CouiMaterialType` 与 `CouiMaterialDefaults`。参数表取自 Settings 17.0.0 的 `COUIMaterialBlurEffect` 静态块，13 种类型各含模糊半径、两层颜色与两层混合模式，`TopBar1` 与原厂一致不区分深浅色；支持 `SRC_OVER` / `OVERLAY` / `COLOR_DODGE` / `LUMINOSITY`，其余模式在构造时拒绝。`rememberCouiMaterialEffect` 新增预设入口，`isNoEffect` 时返回 `null`；新增 `CouiMaterialDefaults.interpolate` 对应原厂按进度插值半径与颜色、混合模式取第三份参数的语义。顶栏系统按钮、分段选择器与 ChipTab 的材质改为从参数表取值，取值一致，观感不变。[模糊与材质混色](docs/effects/blur-material.md#使用-coui-17-参数表)
- 新增轮廓 token `CouiShapes` 与 `CouiTheme.shapes`：`extraSmall` / `small` / `medium` / `large` / `extraLarge` / `card` / `menu` / `panel` / `dialog` / `capsule` / `circle` 十一档，取值来自原厂 `coui_round_corner_*` 刻度。组件参数默认读取主题 token，宿主在 `CouiTheme(shapes = …)` 里整体替换即可，不需要逐组件传 Shape；省略时沿用外层 token，嵌套主题只改配色不重置轮廓。各组件 `Defaults` 保留同值基线供显式覆盖，输入框、轻提示、控制中心磁贴与档位条使用所属组件的私有资源，不参与 token。[外观与预设](docs/guide/appearance.md#轮廓-token)
- 新增自适应断点 `CouiAdaptiveLayout`、`CouiWindowSizeClass` 与 `LocalCouiWindowSizeClass`：按容器宽度分 `Compact`（小于 600dp）/ `Medium`（600–839dp）/ `Expanded`（840dp 及以上），量的是约束而非窗口，分屏与侧栏等局部容器会报自己的宽度；无界时回退窗口宽。容器只有宽度一维，不撑满父级。[自适应布局](docs/components/adaptive-layout.md)
- 新增预览注解 `CouiLightDarkPreview` 与 `CouiBreakpointPreview`：一次声明浅色/深色与 361dp / 720dp / 1000dp 三档宽度，新增组件预览直接套注解，不再逐个手写 `@Preview`。注解随库编译进产物，宿主应用时不需要额外依赖；在 IDE 中看到渲染结果仍需宿主自己的 `ui-tooling`。
- 新增图标按钮 `IconButton` 与 `CouiIconButtonDefaults`：圆形命中区三档（40 / 32 / 48dp），触摸形变、跟手光与 hover/focus/press 蒙层复用 `Button` 的反馈链，跟手光取 `OpaqueMedium2`；命中区由主题最小交互尺寸兜底到 48dp，视觉缩小不影响可点范围。原厂无独立图标按钮类，尺寸取自布局树的 `ImageButton` 命中区。[图标按钮](docs/components/icon-button.md)
- 新增卡片容器 `Card` 与 `CouiCardDefaults`：24dp 卡片轮廓、卡片底色与 16dp 内留白，`onClick` 非空时整卡可点并复用列表行蒙层，不传时纯静态容器；不引入 elevation 与描边，内部布局由调用方决定。[卡片](docs/components/card.md)
- 新增侧栏 `SidePane`、`CouiSidePaneSide` 与 `CouiSidePaneDefaults`：内容槽常驻、侧栏从起始边或结束边滑出，遮罩自绘并随进度淡入，面板只圆外缘两角；收起动画播完才移出组合。展开 250ms、收起 200ms，宽度 280dp 且在窄窗口下最多占 85%。[侧栏](docs/components/side-pane.md)
- 新增按钮 `Button`：几何、排版、配色与反馈链取自 ColorOS 17 的 `Widget.COUI.Button.Large`（44dp 高、最小 132dp 宽、四周 12dp 内留白、14sp `sans-serif-medium`），提供主操作与次操作，加载态为原厂 `coui_loading_btn_circle_*` 的三圆点，并支持禁用。[按钮](docs/components/button.md)
- 新增环形进度 `CircularProgress` 与占位 `Skeleton`：尺寸、描边与配色取自 `Widget.COUI.COUICircularProgressBar`（40dp / 5dp、30dp / 3dp），不确定态的 800ms 旋转与 270° 弧长沿用原厂加载字形 `coui_icon_loading`。[进度与占位](docs/components/progress.md)
- 新增轻提示 `ToastHost` 与 `ToastHostState`：几何与配色取 `coui_snack_bar_*`（12.67dp 圆角、16/10dp 留白、14sp 文字、26dp 跨度 5% 投影），支持操作按钮、图标槽、多行留白、自动消失与最多三条堆叠。[轻提示](docs/components/toast.md)
- 新增角标 `Badge`、`BadgedBox` 与受控气泡 `Tooltip`：角标取自 `Widget.COUI.COUIHintRedDot`（红点 6dp、数字宽 16/20/26dp、10dp 字号、`coui_color_additional_red`），气泡取自 `COUIToolTips`（强调色容器、13dp 留白、12dp 圆角）。[角标与气泡](docs/components/badge-tooltip.md)
- 普通阴影 `CouiShadowStyle` 新增可选 `blurRadius`：`null` 时维持原派生模糊（与原厂 `blur_r=0` 交给平台原生投影的观感不同），传入后显式控制模糊半径以更接近原生；`CouiShadowDefaults.menu()` 保持 `null` 以与原厂 LV4 菜单 1:1。效果页示例初值收到 6dp 投影 / 12dp 模糊（滑块仍可拉回 `menu()` 原厂值），并新增模糊滑块。[普通阴影](docs/effects/shadow.md)
- 新增共享状态蒙层 `couiStateMask` 与焦点描边 `couiFocusOutline`，配套 `CouiMaskType` / `CouiStrokeType` / `CouiMaskColors` / `CouiMaskDefaults`：悬停、焦点（含选中）与按压三条独立弹簧通道按该顺序叠加，`HoverPress` 关掉焦点通道；描边沿用原厂 3dp 半径按像素取整后乘二的笔宽与 `Outer` 差集裁剪，可见半笔宽。`couiButtonFeedback` 与开关的焦点描边改为复用这两个入口，调用签名不变；原厂 `Processor` 族、Drawable 图层广播与自定义路径涟漪未移植。[光效](docs/effects/lighting.md#状态蒙层与焦点描边)

- 新增页码指示器 `PageIndicator`：半径按原厂 `COUIPageIndicator` 的四段距离带连续插值（`COUIEaseInterpolator` 0.33 / 0 / 0.67 / 1），最多显示 6 个点、窗口随当前页滑动，可选 `onDotClick` 点按回调。[页码指示器](docs/components/page-indicator.md)
- 新增空状态 `EmptyState`：布局按 `coui_component_empty_state.xml` 复刻（图标槽 120×100dp、文字列 280dp、标题 2 行 / 说明 3 行），说明 12sp、操作按钮 14sp medium，新增 `actionLabel` + `onAction` 入口。[空状态](docs/components/empty-state.md)
- 新增输入框 `TextField`：对应 `Widget.COUI.EditText.HintAnim.Rectangle` 分支，10dp 圆角填充容器、最小高度 62dp、内留白 16/10/12/12dp，浮动标签 200ms 上浮。原厂 0.66dp 描边（聚焦 1dp）尚未实现，见[输入框](docs/components/text-field.md)。
- 新增底部面板 `BottomSheet`：`Dialog` 承载、遮罩自绘、退场动画结束后才销毁窗口；圆角 32dp、elevation 17dp、四周 8dp 留白、上下限与遮罩取自 `coui_bottom_sheet_*`；支持拖拽跟手、按速度与位移结算、下拉关闭，并与内容滚动共存。[底部面板](docs/components/bottom-sheet.md)
- 新增线性进度 `LinearProgress` 与分段占用 `SegmentedProgress`：8dp / 12dp 胶囊轨道，轨道与进度共用同一轮廓裁剪，分段版按占比累加、尾部保留轨道色。[进度与占位](docs/components/progress.md)
- `CircularProgress` 新增 `size`、`gapDegrees`、`startAngleDegrees`、`glow` 与 `content`：支持大号读数环、缺口弧、外发光光晕与环心内容，原有 40dp / 30dp 默认行为不变。[进度与占位](docs/components/progress.md)
- `CircularProgress` 新增 `arcGradient`：弧线改用竖直渐变，端点对齐环的上下外缘、换尺寸不变色。原厂分数环的弧即顶部满饱和、底部偏淡的饱和度渐变（手机管家首页 R 通道 0→41、B 通道 16→40，G 通道不变），端点随页面而异故不进默认值。[渐变弧](docs/components/progress.md#渐变弧)
- 新增快捷动作网格 `QuickActionGrid` 与 `CouiQuickAction`：两列等分图标卡片，沿用 `Group` 的 24dp 轮廓与卡片底色，末行自动补齐。[快捷动作网格](docs/components/quick-action-grid.md)
- `QuickActionGrid` 新增 `style` 参数与 `CouiQuickActionStyle.Capsule`：实用工具页的全圆胶囊条目，60dp 行高、8dp 间距、图标后留白 8dp、14sp 标签，数值取自真机截图测量；默认仍为 24dp 圆角卡片，调用方行为不变。[快捷动作网格](docs/components/quick-action-grid.md#外观与预设)
- `CouiPresets` 新增 `GreenLight` / `GreenDark` 与 `green(dark)`：只替换强调色及强调内容色，中性表面沿用原生预设。[外观与预设](docs/guide/appearance.md)
- `CouiTextKind` 新增 `Display`：48sp 展示型读数，用于电量百分比、健康分数等页面主数字。
- `CouiButtonDefaults.primaryColors` 与 `colors` 新增 `onAccent` 参数，主按钮内容色默认取 `CouiColorScheme.onAccent`；原生蓝色主题仍为白色，亮色强调（如绿色预设）自动转为深色内容。[按钮](docs/components/button.md)
- 轻提示修正两处与原厂九宫格的偏差：容器改用等半径标准圆角、带图标时上下留白由 16dp 收到 10dp（容器高度 62dp → 50dp）。[轻提示](docs/components/toast.md)
- `TopAppBarButton` 新增可选 `backdrop`：自定义顶栏（不套 `TopAppBarSurface`）可直接传入只含页面背景的采样源，按钮不再依赖顶栏作用域；省略时沿用所在顶栏的源，行为不变。[顶栏](docs/components/top-app-bar.md)
- 新增操作卡 `ActionCard`：图标 + 可选标题 + 说明 + 右下操作组，`title` 省略时为提示卡形态，用于手机管家的通知卡与推荐优化卡。[操作卡](docs/components/action-card.md)
- 新增通知卡 `NotificationCard`：图标 + 标题 + 相对时间 + 正文，对应通知中心的单条通知。组件只做布局，`containerColor` 默认取页面卡片色，宿主传 `Color.Transparent` 并用 `Modifier.couiFloatingSurface` 包裹即可接入凝光玻璃，材质逻辑不在此复制。[通知卡](docs/components/notification-card.md)
- 新增控制中心磁贴 `QuickSettingTile` 与竖排档位条 `LevelSlider`：小磁贴 62dp 正圆 + 下方标签、大磁贴 138×62dp 胶囊、档位条 62×138dp 胶囊，四列单元 62dp、间距 14dp，数值取自控制中心真机布局树。两者同样不自带材质，`containerColor` 传 `Color.Transparent` 后由宿主用 `Modifier.couiFloatingSurface` 包裹。[控制中心磁贴](docs/components/quick-setting.md)
- 修正 `TextField` 的度量：最小高度 52→62dp、圆角 24→10dp、浮动标签 12→10sp、槽间距 12→8dp，内留白改为 16/10/12/12dp；依据 `coui_edit_text_height_min`、`coui_textinput_corner_radius`、`coui_textinput_hint_text_size`、`coui_textinput_rect_padding_middle`。[输入框](docs/components/text-field.md)
- 修正 `Button` 的文字排版与内留白：原厂 `Widget.COUI.Button.Large` 继承的 `coui_btn_text_size` 与 `couiTextAppearanceButton` 都是 14sp medium，四周内留白 12dp；原实现用了 16sp 与垂直 8dp。[按钮](docs/components/button.md)
- 修正控制中心磁贴与档位条与原厂截图不符的几何和状态：两者原先误用 20dp 卡片圆角，实际小磁贴是 62dp 正圆、大磁贴与档位条是整条胶囊；开启态改为原厂白色磁贴，大磁贴补 42dp 图标底盘与 10 / 6 / 15dp 内部留白，小磁贴图标 28dp、标签 10sp medium，大磁贴标签 14sp medium，档位条深色下填充白色。新增 `checkedContainerColor`、`iconContainerColor` 与 `CouiQuickSettingDefaults.smallIconSize`；`largePadding` 语义改为大磁贴左侧内留白。[控制中心磁贴](docs/components/quick-setting.md)
- 修正 `ActionCard` 提示卡形态的说明字号：从 13sp 改为 15sp（`CouiTextKind.BodyLarge`），推荐卡形态保持 13sp。真机实测提示条单行墨迹 13.7dp、行距 20.3dp，对应 15sp 而非 13sp。[操作卡](docs/components/action-card.md)
- 修正 `NotificationCard` 的图标与留白：图标 24→32dp、图标到文字 18→12dp、内留白 16dp 改为水平 14dp / 垂直 12dp、删除标题与正文之间的 2dp 间距。依据 SystemUI 通知行布局树的 `android:id/icon` 112×112px、内容列起点 58dp 与文本列 bounds；`CouiNotificationCardDefaults.padding` 拆为 `horizontalPadding` / `verticalPadding`，`titleBodySpacing` 移除。[通知卡](docs/components/notification-card.md)
- 修正轻提示的文字单位与容器：文案与操作按钮 14dp→14sp（`coui_snack_bar_item.xml` 的 `couiTextAppearanceButton`，随系统字号缩放），操作按钮与文案间距 12→16dp（原厂由文案的 16dp 末尾留白承担），并删除容器内缘那圈无资源依据的内阴影渐变——九宫格内部是纯色填充，阴影只在外缘。[轻提示](docs/components/toast.md)
- 修正角标的叠加偏移：红点距角落 1dp（`coui_hint_red_dot_small_reddot_topend_margin`）、数字角标 6dp（`coui_hint_red_dot_small_number_topend_margin_rectangle`），原先两者共用 6dp。`CouiBadgeDefaults.offsetX/offsetY` 改为 `dotOffset` / `numberOffset`，`BadgedBox` 新增 `offset` 参数。[角标与气泡](docs/components/badge-tooltip.md)
- 补齐空状态与底部面板的两处原厂约束：`EmptyState` 的标题 + 说明限制在 92dp（`coui_component_empty_text_scroll_max_height`）内滚动，新增 `CouiEmptyStateDefaults.textMaxHeight`；`BottomSheet` 的面板高度增加 `coui_panel_max_height`（2000dp）上限，指示条顶距 8→12dp（`coui_panel_drag_view_shadow_margin_top` 4dp + `coui_panel_drag_view_padding_vertical` 8dp）。[空状态](docs/components/empty-state.md) · [底部面板](docs/components/bottom-sheet.md)
- `CouiTextKind` 新增 `BodyLarge`：15sp 常规字重正文，用于没有标题、说明本身承担主文案的场景。
- `CouiTextKind` 新增 `Tag`：10sp medium，对应原厂 `couiTextAppearanceTag`，用于控制中心小磁贴标签等系统标签场景。
- 新增模式选择卡 `ChoiceCardGroup` 与 `CouiChoiceCard`：横向等分、图标 + 主副文字、选中态强调描边与 `RadioButton` 语义，对应电池页的模式选择。[模式选择卡](docs/components/choice-card.md)
- 新增缩略图卡 `ThumbnailChoiceGroup` 与 `ThumbnailNavigationGroup`，共享 `CouiThumbnailChoice`：两列单选型不自绘底色、两卡无缝并排、底部为单选钮，对应显示与亮度；三列导航型自带 `CouiShape(20.dp, 3f)` 轮廓、8dp 间距与「当前值 + 箭头」，对应通知与控制中心的分类设置。尺寸取自真机布局树。[缩略图选择卡](docs/components/thumbnail-choice.md)
- 新增柱状图 `BarChart` 与 `CouiBar`：圆角柱、逐柱配色、右侧刻度与水平网格线，用于电量使用情况。[柱状图](docs/components/bar-chart.md)
- 新增悬浮底栏 `DockBar`：胶囊导航与中央凸起圆钮共用双主体玻璃材质并按融球合成，对应手机管家与实用工具页的 dock。[悬浮底栏](docs/components/dock-bar.md)
- `CouiQuickAction` 新增 `labelMaxLines`（默认 2）：入口标题可换行，原厂「常用信息与密码」这类两行入口不再被截断。[快捷动作网格](docs/components/quick-action-grid.md)
- 新增下拉刷新 `PullToRefresh`：列表到顶后继续下拉累积位移，松手超过阈值触发刷新，指示器收到下拉进度并可替换。[下拉刷新](docs/components/pull-to-refresh.md)
- 新增折叠面板 `Accordion`：受控展开态、展开/收起动画与自绘旋转箭头，用于设置页的按需展开分组。[折叠面板](docs/components/accordion.md)
- 新增头像容器 `Avatar` 与 `CouiAvatarDefaults`：固定尺寸、可裁剪、底色可覆盖，内容由宿主提供。[头像](docs/components/avatar.md)
- 新增三态容器 `StateLayout` 与 `CouiLoadState`：加载、空、失败与就绪的统一分发，文案与重试入口由宿主提供；空态与失败态按原厂 `COUIEmptyStateView` 摆位——顶部留白为 (容器高 − 槽高) × 0.45，按高 200dp / 宽 320dp / 高 480dp 判三档尺寸类型、`Small` 跳过插图，容器高未被固定时收拢到内容高度，`emptyPageSize` 可强制档位。[三态容器](docs/components/state-layout.md)
- 新增步骤条 `Stepper`：横向圆点、连接线与标签，当前步骤由宿主持有。[步骤条](docs/components/stepper.md)
- Demo 新增「手机管家」与「清理存储」两个页面级复刻，演示绿色渐变底、页内大标题、通知卡、白心分数环、2×2 入口卡、多色存储构成条，以及锚定在圆底更多按钮上的毛玻璃菜单。

按钮、环形进度、角标、气泡、页码、空状态、输入框、底部面板与轻提示均对齐 ColorOS 17 应用内嵌的 COUI 资源；原厂没有对应分支的细节（按钮加载的三点动画、不确定态弧长、占位脉冲、轻提示投影）按同族资源的数值与节奏推演。

- Demo 的二级页导航从「每页一个 Activity」换成 Navigation 3：新增 `DemoRoute` 导航键与 `rememberDemoBackStack()`（`listSaver` 保存页面栈，不引入序列化插件），`MainActivity` 用 `NavDisplay` 分发首页与详情页并处理系统返回；移除 `DemoSecondaryActivity` 及其 manifest 声明。库 API 不变。[Demo 指南](docs/guide/demo.md)

### 移除

- 移除可选 `coui-material3` 模块。`io.github.itoseo:coui-material3` 不再随新版本发布，已发布的历史版本仍可从 Maven Central 下载。
- 核心不再提供任何外部设计体系的默认值解析；`Coui.initialize` 仍可接收宿主自定义的 `CouiStyle`。
- Demo 移除 Material 3 与 Material Icons 依赖，改用 `com.itos.coui.text.Text`、`coui-icons` 与 `CouiTheme` 色板；「Material 主题配色」开关及其偏好项一并删除。
- 文档移除 Material 3 接入章节、颜色与字体角色映射表，以及 CI 中的第三坐标检查。

### 升级说明

- 原 `Stepper`（横向步骤圆点、连线与标签）改名为 `StepBar`，`CouiStepperDefaults` 改为 `CouiStepBarDefaults`；行为与参数不变，调用方替换名称即可。`Stepper` 现在是数值步进器，继续用旧名会拿到另一个组件。
- 原先依赖 `coui-material3` 的调用方，删除该依赖与 `Coui.initialize(defaultStyle = CouiMaterial3Style())`，改用 `CouiTheme` 提供配色，或显式传参覆盖单个组件，见[外观与预设](docs/guide/appearance.md)。
- `CouiMaterialBlendMode` 已移除。调用 `rememberCouiMaterialEffect(mix, blend, mode)` 的地方改为传两层：原 `mix` 映射为 `CouiMaterialLayer(mix, BlendMode.LUMINOSITY)`，原 `blend` 沿用原来的 `OVERLAY` / `COLOR_DODGE`。`CouiDialogMaterial.blendMode` 的类型同步改为 `android.graphics.BlendMode`，取值不变。
- 预览版不保证二进制兼容，升级后重新编译使用库的模块。

## [0.0.1-alpha11] - 2026-11-12

### 新增与改进

- 新增 `LoadingDialog`，居中显示加载动画和单行提示。宿主通过 `visible` 控制显隐。默认屏蔽外点关闭，可通过 `dismissOnClickOutside` 启用。[加载弹窗](docs/components/dialog.md#显示加载弹窗)
- 修正弹窗材质的合成顺序。先按窗口遮罩强度压暗采样底图，再做模糊和混色。[背景材质](docs/components/dialog.md#使用背景材质)
- 修复顶栏空白处的触摸穿透。顶栏布局之外的渐隐区域仍可与正文交互。[顶栏](docs/components/top-app-bar.md)
- 新增支持 RTL 自动镜像的 `CouiIcons.Filled.Next` 和 `CouiIcons.Outlined.Next`。[图标](docs/components/icons.md)

### 升级说明

- `coui`、`coui-material3` 和 `coui-icons` 使用同一版本。alpha10 的公开调用可沿用，升级后重新编译宿主模块。

## [0.0.1-alpha10] - 2026-11-10

以下为相对 `0.0.1-alpha9` 的变更。

### 新增与改进

- 新增胶囊分段选择器 `SegmentedControl`（Android 14 / API 34+）。支持点按、横向拖动、弹簧指示器、即时选择或抬手提交，以及普通填色和透明模糊两种外观。[分段选择器](docs/components/segmented-control.md)
- 统一普通顶栏与大标题顶栏的默认标题排版。普通 `TopAppBar` 为 `18sp`。`LargeTopAppBar` 展开为 `40sp`，有起始侧导航按钮时收起为 `18sp`，没有时为 `24sp`。默认折叠过程保持基础字重 `700`。普通顶栏和大标题顶栏的默认样式跟随系统字体大小与字重补偿。
- 常驻 `SearchBar` 的输入胶囊支持在展开、收起动画期间跟手移动和变形，松手后回弹。
- `Fab` 和 `NavigationBar` 的 `renderer` 改为可选。省略时组件各自创建绘制器。宿主也可自行处理初始化失败，并在创建成功后显式传入。

### 升级说明

- `LargeTopAppBar` 移除了 `expandedTitleSize` 和 `collapsedTitleSize`。把原有字号分别传给 `expandedTitleStyle = TextStyle(fontSize = …)` 和 `collapsedTitleStyle = TextStyle(fontSize = …)`。两套样式也可各自指定字体族、字重和颜色。[大标题顶栏](docs/components/collapsing-app-bar.md#普通大标题)
- 更新 `CouiTextKind` 枚举引用。将 `PreferenceTitle`、`PreferenceSummary`、`PreferenceValue` 分别改为 `ItemTitle`、`ItemSummary`、`ItemValue`。将 `Toolbar` 改为 `TopBar`，其字号从固定 `18dp` 改为 `18sp`。将 `Description` 改为 `GroupFooter`。
- 移除了 `Category`、`SearchCancel`、`PageTitle`、`SectionTitle`、`Body`、`Stepper` 和 `Progress`。直接使用这些预设的调用方可改用 Compose `TextStyle`。常驻搜索的取消文字样式由 `SearchBar` 内部提供。
- 预览版有源码不兼容变更。更新上述调用后，重新编译使用库的模块。

## [0.0.1-alpha9] - 2026-11-09

`coui`、`coui-material3` 与 `coui-icons` 同版本发行。源码见 [v0.0.1-alpha9](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha9)，Demo 下载见 [Release](https://github.com/ItosEO/COUI-Kit/releases/tag/v0.0.1-alpha9)。

- 新增 7 个账号页位图，通过 `CouiIcons.Bitmap` 获取 `Painter`。`DevicePair` 提供日间和夜间原图。
- 新增 `CouiIcons.Outlined.Scan` 和设置齿轮的 Filled、Outlined 样式。原多色设置图标改名为 `CouiIcons.Filled.SettingDetail`，升级时请更新原有引用。
- 新增 `CouiIcons.Filled.Headset`，在原耳机轮廓上填实两个耳罩。
- 网页图标库新增 Bitmap 筛选，图标按名称从 A 到 Z 排序。首页和图标库默认以 80% 比例显示。
- 将 AndroidX DynamicAnimation 升至 `1.1.0`，解除严格锁版。修复关闭系统动画时 Slider 松手后仍停在按下高度的问题。[PR #22](https://github.com/ItosEO/COUI-Kit/pull/22)

## [0.0.1-alpha8] - 2026-11-09

`coui`、`coui-material3` 与 `coui-icons` 同版本发行。源码见 [v0.0.1-alpha8](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha8)，发行附件见 [Release](https://github.com/ItosEO/COUI-Kit/releases/tag/v0.0.1-alpha8)。以下为相对 alpha7 的主要变更。

### 新增与调整

- 新增可选 `coui-icons` 模块，核心搜索组件不再依赖公开图标包。
- 图标使用 VectorDrawable XML 保存，传统 View 与 Compose 读取同一份资源；支持日夜配色、多色和渐变图标。入口按 `CouiIcons.Filled`、`CouiIcons.Outlined` 提供，补充设置、隐私与安全、相册和云服务图标。
- 新增 `CouiColor.Day`、`CouiColor.Night` 配色预设，包括 `GRAY` 和 `DARK_GRAY`。
- 新增项目首页和图标工具网页，支持跨页主题持久化、DarkLight 日夜切换、名称搜索、Filled/Outlined 筛选、配色预览及 SVG/ImageVector 源码复制。
- FAB 支持圆角方形与自定义圆角，统一内容、材质、光效和投影的几何；`foregroundComposition` 用于相邻 FAB 的前景合成，修复拖动时背景回填遮住其他按钮的问题。
- FAB 提供 `contentColor`，默认日夜均为不透明白色。`onClick` 为必传参数，允许传入空回调 `{}`。
- 底栏提供 `selectedColor` 和 `unselectedColor`，文字与图标共用状态色；选中态不透明度为 `230 / 255`，未选中态为 `184 / 255`。`selectedIcon` 省略时复用 `icon`，拖动预选仍由底栏处理。
- `LargeTopAppBar` 支持通过 `expandedTitleSize`、`collapsedTitleSize` 分别配置展开与收起字号。
- 修复顶栏与搜索栏的主题配色，常驻搜索材质读取当前主题的 `materialTint`。
- Demo 的效果导航使用 `WandSpark`，选中与未选中分别使用 Filled 和 Outlined，并更新启动图标。
- 发布流程覆盖三个库模块；Maven Central 在上传后异步校验和发布，保留上传前的 Lint 与发行产物检查。

### 升级说明

- 使用公开图标时，添加同版本的 `implementation("io.github.itoseo:coui-icons:0.0.1-alpha8")`，并将旧图标入口调整为 `CouiIcons.Filled.*` 或 `CouiIcons.Outlined.*`。图标需在 Composable 中获取，传统 View 使用对应的 drawable XML。
- `Search`、`Headset`、`SliderCustom` 和 `LayoutSpliced` 仅提供 Outlined。多色图标使用 `Image` 或 `Icon(..., tint = Color.Unspecified)` 保留原色；`AppUpdate(color)` 只调整背景，保留白色前景。
- 所有 `Fab` 调用均需提供 `onClick`。默认内容色不再取 `onAccent` 或外层颜色；使用 `contentColor` 显式覆盖，或传入 `Color.Unspecified` 继承外层内容色。
- 预览版不保证二进制兼容，升级后重新编译使用库的模块。

## [0.0.1-alpha7] - 2026-11-06

`coui` 与 `coui-material3` 同版本发行，源码见 [v0.0.1-alpha7](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha7)。发行附件及更新日志见 [Release](https://github.com/ItosEO/COUI-Kit/releases/tag/v0.0.1-alpha7)。以下为相对 alpha6 的主要变更。

- 修复 临时禁用纵向滚动会中断子内容横向拖动的问题，恢复图表连续选点和 tooltip 跟随；保留边界回弹，以及选点期间禁止上下滚动的行为。
- 调整 Demo 启动图标，更新前景矢量与白色背景。

## [0.0.1-alpha6] - 2026-11-05

`coui` 与 `coui-material3` 同版本发行，源码见 [v0.0.1-alpha6](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha6)。发行附件及更新日志见 [Release](https://github.com/ItosEO/COUI-Kit/releases/tag/v0.0.1-alpha6)。以下为相对 alpha5 的主要变更。

- 新增 顶栏默认背景遮罩，浅色使用白色、深色使用黑色，并向下渐隐。
- 优化 顶栏背景渐隐曲线，平滑中间节点与底部过渡，减轻渐变接痕。
- 优化 渐进模糊的颜色计算精度，减轻横向色带。
- 调整 顶栏模糊强度，同步普通顶栏、大标题顶栏与旧平台预设。
- 调整 Demo 顶栏示例使用默认背景遮罩，同步背景颜色与透明遮罩的接入说明。
- 调整 Demo 本地与 CI 统一使用 `itos` 签名。
- 调整 Demo 版本名为 `0.0.1-alpha6`，与发行 Tag 对齐；版本码继续使用 Git commit count。

顶栏背景参数省略或为 `Color.Unspecified` 时使用默认遮罩。显式颜色覆盖默认值；`Color.Transparent` 关闭遮罩并保留模糊。升级时检查已有背景颜色覆盖，详见[顶栏背景参数](docs/components/top-app-bar.md#背景参数)。

## [0.0.1-alpha5] - 2026-11-05

`coui` 与 `coui-material3` 同版本发行，源码见 [v0.0.1-alpha5](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha5)。发行记录、验证结果及 Maven 坐标见 [Release](https://github.com/ItosEO/COUI-Kit/releases/tag/v0.0.1-alpha5)。以下为相对 alpha4 的主要变更。

### 组件命名迁移

公开 UI 组件统一简化名称。升级时同步修改 import、调用和函数引用；不保留旧名称转发。名称及下述参数变化属于源码及二进制不兼容变更，已发布版本的名称不变。

- `CouiText`、`CouiSwitch`、`CouiSlider`、`CouiCheckbox` → `Text`、`Switch`、`Slider`、`Checkbox`。
- `CouiMenu`、`CouiFab`、`CouiBottomBar`、`CouiScaffold` → `Menu`、`Fab`、`NavigationBar`、`Scaffold`。
- `CouiSearchBar`、`CouiCollapsingTopAppBar`、`CouiTopAppBar` → `SearchBar`、`CollapsingTopAppBar`、`TopAppBarSurface`；顶栏的 Button、ButtonGroup、ActionVisibility、Search、SearchButton、SearchAction 入口同样去掉前缀。
- `CouiChipTab`、`CouiChipTabButton`、`CouiChipTabRow` → `ChipTab`、`ChipTabButton`、`ChipTabRow`。
- `CouiAlertDialog`、`CouiInputDialog`、`CouiWheelPicker`、`CouiCalendarPicker` → `Dialog`、`InputDialog`、`WheelPicker`、`CalendarPicker`；开发快照中的 `AlertDialog` 也需改为 `Dialog`。

开发快照中已使用 `BottomBar` 的调用方，将 import 和调用改为 `com.itos.coui.navigation.NavigationBar`，参数保持不变。

`CouiTextKind`、Defaults、Style、状态等类型，以及 `CouiTheme`、`rememberCoui…` 工厂和 `coui…` 效果入口保持原名。与 M3 等同名组件混用时，通过 import 别名区分来源。

### 新增

- 新增受控 `WheelPicker`、`CalendarPicker` 和 `RadioButton`，分别提供滚轮、月历和单选交互。
- `TopAppBar` 提供完整的标题、导航与操作槽位；`LargeTopAppBar` 支持大标题折叠。仅需背景效果、继续使用宿主顶栏布局时，调用 `TopAppBarSurface`。
- `TopAppBarSearch` 支持可选右侧融合动作，内容由 `action` 提供，点击由 `onActionClick` 提交。
- `Dialog` 和 `InputDialog` 增加 `visible`，支持受控进退场与移动过程中的实时背景采样。传入时须保持调用在组合中，不用 `if (visible)` 提前移除；输入弹窗同步处理焦点、键盘关闭与位移避让。
- `Text` 增加可选 `maxLines`、`overflow`；Body 与 Description 默认按逻辑起始边对齐，显式 `textAlign` 优先。
- `SearchBar` 增加可选 `enabled`，统一控制点击、编辑、清除、取消、IME 提交和交互反馈，禁用时释放自身输入但保留宿主状态。取消文字宽度按文本、样式、密度、布局方向和测量器缓存。
- `Item` 移除无条件语义合并；整行点击沿用 clickable 的语义，纯布局保留子控件语义。`Group.headerActions` 仅随非空标题渲染。
- 移除无效的 `CouiPageDefaults.groupPadding` 与仅供 Demo 使用的 `homeRowMinHeight`；目录行高由 Demo 持有。Demo 顶栏使用既有 Defaults 并覆盖所需外观，同名图标直接使用 CouiIcons。
- `CouiPageDefaults.iconSpacing` 统一 Item 图标与文字的间距；Demo 的带图标分隔线由行内留白、图标尺寸和该间距计算缩进。
- `Group`、`Item`、`Divider` 和 `Footer` 提供可独立组合的设置式布局；`Item` 支持内容槽和可选整行点击。
- `Item` 的内容槽参数 `headline` 更名为 `title`，`supporting` 更名为 `summary`，`leading` 更名为 `icon`，图标槽尺寸 `leadingSize` 更名为 `iconSize` 并移至 `icon` 后；开发分支消费者需同步名称及受影响的位置参数，参数类型与默认值保持不变。
- 可选 `Scaffold` 提供栏位测量、正文 Padding 和系统安全区处理，核心无需 Material 3。采样、滚动、导航与主题由宿主分别组合。
- `CollapsingTopAppBar`、`SearchBar` 和相应状态提供折叠标题及常驻搜索；与既有顶栏搜索分别使用。
- `couiVerticalScroll` 提供 COUI 纵向惯性与边界回弹，支持短内容和空内容。拖动事件由可选 `interactionSource` 提供，位置与范围继续使用 `ScrollState`。
- `Text` 与 `rememberCouiTextStyle` 提供固定 COUI 排版角色；`CouiIcons` 提供系统功能矢量子集，来源与权利说明随源码和 API 文档归档分发。
- Demo 使用目录和独立 Activity 组织详情，提供目录搜索、共享外观偏好及完整页面装配示例。

### 既有接口与行为

- `LocalCouiContentColor` 的缺省值为 `Color.Unspecified`。COUI 文字和图标在消费处解析当前主题前景色；直接读取该 Local 的宿主需处理未指定值。显式文字颜色及图标的不染色语义保留。
- `CouiColorScheme` 增加独立 `background`，默认沿用 `surface`；原生页面与组件表面分别配色。M3 适配的菜单项和头部标签分别读取 `labelLarge` 与 `bodySmall`。
- `TopAppBarSurface` 支持背景透明度和全高渐隐曲线，保留显式渐变颜色的 alpha；`dark` 默认跟随 `CouiTheme`。
- 浮动表面增加双主体入口，复用既有材质、融合和光效。列表按压复用现有反馈入口。
- ChipTab 与纵向滚动共享物理内核，各实例的动画等级独立决定摩擦参数。

开发分支中临时布局名称及搜索配色参数的迁移说明见 [PR #16](https://github.com/ItosEO/COUI-Kit/pull/16)。Dialog 的显隐与宿主页键盘避让接线见 [弹窗文档](docs/components/dialog.md)。

## [0.0.1-alpha3] - 2026-11-01

`coui` 与 `coui-material3` 同版本发行。源码为 [v0.0.1-alpha3](https://github.com/ItosEO/COUI-Kit/tree/v0.0.1-alpha3)，提交 `e40c1c413b68fa43924ac6e48620ad3e9d703a08`。发行文件见 [coui](https://repo.maven.apache.org/maven2/io/github/itoseo/coui/0.0.1-alpha3/) 与 [coui-material3](https://repo.maven.apache.org/maven2/io/github/itoseo/coui-material3/0.0.1-alpha3/)。两个模块均通过发行结构、GPG 签名和校验和检查。

升级需重新编译消费者，不承诺二进制 ABI。设备上的字体、嵌套主题和最小交互尺寸仍需验收，构建通过不表示视觉兼容已全部确认。

- 核心移除 Material 3 与 Material Icons 依赖，使用独立文本、图标和菜单定位；原生默认最小布局占位保留。
- 新增可选 CouiTheme、原生浅深色预设、普通字体和内容上下文；原组件通过默认参数简化装配，保留显式配置与底层 Effect。
- 新增可选 `coui-material3` 模块，通过 `Application.onCreate` 中的 `Coui.initialize(defaultStyle = CouiMaterial3Style())` 接入。组件在消费处读取 M3 颜色、字体和内容上下文；顶栏按钮恢复 M3 最小交互尺寸语义。
- `LocalCouiDarkTheme` 接收宿主模式；`CouiTheme` 支持局部原生或自定义外观。原生默认 tint 透明；M3 菜单与底栏默认使用宿主 surface 作已有色相层。
- Chip 与 Dialog 只接入宿主字体族，保留原生排版。Dialog 的布局测量和绘制共同响应异步字体结果。
- 菜单 Style 末尾增加可选 dark；自动默认不再通过表面亮度猜模式，旧完整 Style 调用保留。FAB 区分省略与显式材质，保留显式内容色与参数顺序。
- Demo 启动选择 M3，以局部 scope 切换原生外观并保留内容状态。PR 与快照发布同步检查、发行两个同版本 artifact，见[外观与预设](docs/guide/appearance.md)。

## 已合入 main 的近期改动（待建立发行版本映射）

以下是源码仓库的变更记录，不代表这些特性在每一个 Maven Central 预览版本中都存在。

### 2026-10-30

- [#9](https://github.com/ItosEO/COUI-Kit/pull/9)：按使用场景整理组件文档、快速开始和 README，补全接入示例。

### 2026-10-29

- [#8](https://github.com/ItosEO/COUI-Kit/pull/8)：统一 Touch/Press/Slider/Switch 等持续动画通道，调整中断与重定向。Touch/Press 运动轴收为只读 `State<Float>`，涉及二进制不兼容，原消费者需要重新编译。

### 2026-10-28

- [#7](https://github.com/ItosEO/COUI-Kit/pull/7)：新增 COUI Switch，包含受控点击、拖动吸附、加载和禁用反馈。
- [#6](https://github.com/ItosEO/COUI-Kit/pull/6)：新增普通警告弹窗与输入型弹窗预设，支持底部/居中窗口、受控操作及输入过滤。

### 2026-10-24 至 2026-10-25

- 新增 ChipTab；优化回弹中断和输入交接。
- 完善 Demo 按需签名打包、CI、MPL 2.0/NOTICE，以及 GitHub Packages 开发快照构建与发布入口。

## 已发布版本与源码对应关系

- Maven Central 的版本以 [实际包页面](https://central.sonatype.com/artifact/io.github.itoseo/coui)及远端元数据为准；[README](README.md)中的依赖示例使用双模块同版本发行。
- alpha3 的源码 Tag 与发行文件见上文；历史版本没有对应发行 Tag，不据此断言 `0.0.1-alpha1` 或 `0.0.1-alpha2` 分别对应哪个提交。
- 根目录 `VERSION_NAME=0.1.0-SNAPSHOT` 是开发/快照的基础版本，不代表 Maven Central 已公开 `0.1.0`。
- 今后每次不可变版本发布，需记录版本号、发布日期、源代码 Tag/Commit SHA、AAR 校验/发行地址、公开 API 兼容变化和迁移说明。再将上述记录归入对应版本段落。

发布渠道及执行方式见[发布指南](docs/development/publishing.md)。

# 原厂对照与覆盖状态

基准是 `references/apk/` 下反编译的 ColorOS 17 包：Settings 17.0.0、SystemUI 17.99.02、PhoneManager。本表只记「能从当前代码或原厂入口核验」的结论，不作为功能清单或改动日志；参数、枚举、字段和调用链以源码为准，公开契约变化仍以[使用文档](../components/README.md)与 [CHANGELOG](../../CHANGELOG.md) 为准。

「证据」列指向可复核的入口，不复制实现。标注「待验收」的项必须在真机按同一背景/手势对照后才能改写，不能靠静态推断直接改观感。

## 已对齐

| 能力 | 原厂实现 | 本项目入口 | 对齐粒度 | 证据 |
| --- | --- | --- | --- | --- |
| 材质混色 | `COUIMaterialBlurEffect` 静态块的两层 `BlendModeColorFilter` | `CouiMaterialDefaults` / `CouiMaterialPreset` | 13 类型 × 深浅，半径 + 两层色 + 两层 mode；`TopBar1` 不读 `dark` | [逆向分析 §3.1](../../references/apk/ANALYSIS.md)、[模糊与材质混色](../effects/blur-material.md) |
| 浮动表面融球 | `BlurDrawableShaderMetaBallStringKt` + `MetaBallParamsKt.buildPreCmpArray` | `CouiFloatingShader` + `floatingPair()` | 17 常量、三球三对 `pre[24]`、同一份 Kotlin 预算 | `coui/.../effect/CouiFloatingShader.kt`、`CouiFloatingGeometry.kt` |
| 双主体融合 | 同上 | `CouiMetaballRenderer` | 复用 `floatingPair()`，shader 读 `pre[24]`；`ShaderBrush` 绘制路径不复用 | [横向双主体融合](../effects/metaball.md) |
| 顶栏位置可变 Gaussian | `OplusGradientGaussianBlurEffect` | `drawCouiGradientBackdrop` | 先横后纵、整数半径、位置 `ratio` 决定核大小 | `coui/.../effect/CouiGradientBlur.kt` |
| 顶栏模糊随折叠缩放 | `AppBarBlurHelper.updateGradientBlurFraction`：start/end 半径同乘 `1-fraction` | `TopAppBarSurface.blurFactor` | 两个乘子顺序执行，强度只缩半径不改渐变分布 | [顶栏与按钮](../components/top-app-bar.md#背景参数) |
| 顶栏背景 alpha | `AppBarBlurOverlayBehavior`：30dp 阈值，可见 offset 优先、内容滚动回退 | `CouiCollapsingAppBarState.scrollAlphaFraction` | 双数据源与阈值；阈值常量归 Defaults | [折叠顶栏](../components/collapsing-app-bar.md#外观与采样) |
| 超椭圆 / G2 圆角 | `sdRoundRect` 的 lp 范数分支 | `CouiShape.cornerWeight`、`CouiCapsuleShape` | 等半径 Lp 轮廓 + 胶囊分支 | [外观与预设](../guide/appearance.md#轮廓-token) |

## 有偏差

| 能力 | 原厂实现 | 本项目入口 | 差异 | 影响 | 证据 |
| --- | --- | --- | --- | --- | --- |
| 低平台顶栏模糊 | 同一梯度模糊链 | `TopAppBarSurface` 的 API 31–32 分支 | 三层固定半径 + 渐变遮罩，`blurFactor` 不生效 | 旧平台折叠时不会变清晰 | [模糊与材质混色](../effects/blur-material.md#与参数表的有意差异) |
| 底栏浅色胶囊 | `TYPE_FRAMEWORK_BOTTOM_BAR` | `DockBar` / `NavigationBar` / `FloatingToolbar` | 两层色比参数表低一档，深色一致 | **待真机验收**，未同背景对照前不改观感 | 同上 |
| 大标题页背景 alpha | 独立 overlay 视图按 30dp 淡入 | `LargeTopAppBar` | 主背景恒定不透明，只有模糊随折叠淡出 | 与原厂 overlay 层不同，但避免静止态整体透明 | [折叠顶栏](../components/collapsing-app-bar.md) |
| 紧凑顶栏滚动源 | `totalScrollRange == 0` 时改读内容滚动 | `TopAppBar` | 无 `scrollBehavior`，拿不到滚动位置 | 固定栏不随滚动淡入 | `coui/.../topappbar/TopAppBar.kt` |
| 纯色底 alpha | `normalBackgroundAlphaFraction`（深色另档） | `gradientColor` 的 alpha | 项目用遮罩色 alpha 顶替，未复刻独立分支 | 深色渐变观感差异待对照 | `coui/.../topappbar/TopAppBarSurface.kt` |
| 菜单描边混色 | `COUIMaterialEffect` | `CouiEdge` 的固定两段混色 | 模式固定，未核原厂菜单材质 mode | **待核**，不能断言为已确认偏差 | `coui/.../effect/CouiEdge.kt`、[轻提示](../components/toast.md) |
| QS 磁贴 | `DeformationFrameLayout` + 展开面板 | `QuickSettingTile` / `LevelSlider` | 只有开关态，无长按展开形变与网格容器 | 控制中心页不能 1:1 复用 | [控制中心磁贴](../components/quick-setting.md) |
| 选择器 | 日期 / 时间 / 农历 / 倒计时 Picker | `WheelPicker` / `CalendarPicker` | 只有滚轮与月历，弹窗由宿主组合 | 设置页日期时间入口需宿主装配 | [选择器](../components/pickers.md) |
| 输入框 | `COUICodeInputView`、多输入、错误气泡 | `TextField` | 只有通用输入卡片 | 验证码等页面需另写 | [输入框](../components/text-field.md) |
| 弹窗 | 引导弹窗、多页、安全弹窗、列表底栏弹窗 | `Dialog` / `InputDialog` / `LoadingDialog` | 三个预设 | 引导页需宿主装配 | [弹窗](../components/dialog.md) |
| Chip | `COUIChip` / `COUIChipGroup` / `COUIChipDrawable` | `ChipTab` / `ChipTabRow` | 只有分类 tab，无可关闭 chip 与标签输入组 | 标签编辑场景缺件 | [ChipTab](../components/chip-tab.md) |
| GPU 抗锯齿等 | `fwidth` 覆盖率、逐次随机噪声、三维投影 | 代码里的 `shortcut:` 注释 | 13 处显式降级，按公开 API 可表达的范围实现 | 已在注释逐条声明，不算隐藏缺陷 | `coui/.../effect/`、`backdrop/CouiBackdrop.kt` |

## 未实现 / 待验收

「计划」列是本轮之后的候选顺序，不代表已排期。用户指定的三个小组件优先；三项实现已完成，并按静态双向核查（见下节）收敛了可静态确定的偏差，结论仍为「可见行为近似，非 1:1」。使用文档、Demo 与两处导航（`docs/components/README.md`、`zensical.toml`）均已登记；三项均未做真机验收，故仍留在本表。

| 能力 | 原厂实现 | 现状 | 计划 | 证据 |
| --- | --- | --- | --- | --- |
| 跑马灯文本 | `COUIMarqueeTextView`（自绘滚动、左右渐隐、按测量宽度决定是否跑） | 已实现 `coui/src/main/java/com/itos/coui/text/MarqueeText.kt`；静态双向核查见下节，已补 1000ms 起始延迟，非 1:1 | 差异处理、使用文档与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/tips/COUIMarqueeTextView.java`、`res/values/dimens.xml:2070-2073` 的 `coui_top_tips_*` |
| 竖向滑动条 | `COUIVerticalSeekBar`（竖向进度、按压形变、物理速度、文字） | 已实现 `coui/src/main/java/com/itos/coui/seekbar/VerticalSeekBar.kt` + `CouiVerticalSeekBarDefaults.kt`；KDoc 声明省略厂商 `FlingBehavior`、形变与线性马达；静态双向核查见下节，滑块尺寸、按压过渡、分档震动与配色已按核查收敛，非 1:1 | 差异处理、使用文档与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/seekbar/COUIVerticalSeekBar.java`、`res/values/attrs.xml:1577-1607`、`res/values/styles.xml:5500-5525`、`res/values/dimens.xml:2141-2145` |
| 顶部提示条 | `COUICustomTopTips`（卡片容器、显示/消失 250ms 动画、内容布局由子类给出） | 已实现 `coui/src/main/java/com/itos/coui/toptips/TopTips.kt` + `CouiTopTipsDefaults.kt` + `TopTipsContent.kt`（对应 `coui_default_toptips.xml` 的默认排布）；静态双向核查见下节，轮廓差异仍在，非 1:1 | 差异处理与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/tips/COUICustomTopTips.java`、`res/layout/coui_default_toptips.xml`、`res/layout/coui_ai_payment_toptips.xml` |
| 页签条 | `COUITabLayout` + `COUISlidingTabStrip` | 无；ChipTab/SegmentedControl 都不带滑块指示器 | 下一轮 | `.../tablayout/` |
| 搜索历史 | `COUISearchHistoryView` + `COUIFlowLayout` | 无 Flow 换行布局 | 下一轮 | `.../searchhistory/` |
| Snackbar | `COUISnackBar` / `COUINotificationSnackBar` | `ToastHost` 复用几何，但队列/时长/位置语义不同 | 下一轮 | [轻提示](../components/toast.md#与原厂-snackbar-的对应) |
| PIN / 图案锁 | `COUIPasswordDial`、`COUILockPatternView`、`COUINumericKeyboard` | 无 | 系统页批次 | `.../passworddial/`、`.../lockview/` |
| 隐私声明面板 | `COUIStatementPanel` / `COUIFullPageStatement` | 无 | 系统页批次 | `.../statement/` |
| 编辑态底栏 / 附件栏 | `COUIBottomFloatingEditToolbar`、`COUIBottomFloatingAccessoryNavigationBar` | 只有 `NavigationBar` 左右两个 icon | 系统页批次 | `.../bottomfloatingedittoolbar/`、`.../bottomfloatingaccessorynavigationbar/` |
| 弹簧链列表 | `COUIGridSpringChainScrollView` | `scroll` 只到弹性滚动与 fling | 视需求 | `.../springchain/` |
| 轮播 Banner | `COUIBanner` + `COUIPageIndicatorKit` | 只有 `PageIndicator` | 视需求 | `.../banner/` |
| 可展开列表 | `COUIExpandableListView` / `ExpandableRecyclerView` | `Accordion` 是单卡内一段 | 视需求 | `.../expandable/` |
| 聚光引导 | `COUISpotLightEffect`（挖洞 + 引导页） | 只有 `CouiSpotlight` shader | 视需求 | `.../spotlight/` |
| Preference 家族 | 80 个 `COUI*Preference` 顶层类 | `Item` + `Switch` / `Checkbox` 可组合替代，不逐类 1:1 | 不单独立项 | [Item](../components/item.md)、[Group](../components/group.md) |

## 双向核查：三个用户指定优先组件

按[复刻指南](../development/replication-guide.md)的「原厂 → 移植、移植 → 原厂」要求，对 `coui/src/main/java/` 下三个实现逐条比对 `references/apk/jadx/settings/classes7/` 的反编译源码与 `references/apk/settings/decoded/res/` 的资源。以下是静态核查结论；参数一致、终点收敛不等于 1:1 验收，涉及观感的条目仍须真机同背景对照。

### 跑马灯文本 `MarqueeText`

- 一致：`CouiMarqueeTextDefaults` 的 `interval=80.dp` / `speed=25.dp` / `startLocation=10.dp` / `fadingEdgeLength=26.dp` 与 `res/values/dimens.xml:2070-2073` 的 `coui_top_tips_*` 四项一致。原厂 `initSpeed()` 用 `25dp / display.getRefreshRate()` 每帧推进，移植用 `withFrameNanos` 按 `25dp * seconds` 推进，每秒位移等价；空档空格数都是 `ceil(intervalPx / spaceWidth) + 1`（原厂 `iCeil+1`，无 off-by-one）；RTL 反向、无障碍固定原始文本、超宽才滚动、两端渐隐、离屏段回收（模 `unitWidth` 回绕 vs 删首段追加尾段）语义等价。
- 差异（已收敛）：移植原缺 `DEFAULT_SCROLL_DELAY_DURATION=1000` 起始延迟（`COUIMarqueeTextView.java` 构造函数末尾 `postDelayed(mStartScrollRunnable, 1000L)`），本轮已在滚动 `LaunchedEffect` 开头补 `delay(1000)`；原厂滚动判定用 `setContent()` 后的当前 `getText()`（重复串），移植用原始单行 `textWidth`；原厂是 `ValueAnimator.ofInt(Integer.MAX_VALUE)` + `COUILinearInterpolator` 帧驱动，移植是 `withFrameNanos` 时间驱动；原厂渐隐是 TextView 的 horizontal fading edge，移植是 `PorterDuffXfermode(DST_IN)` 线性渐变遮罩；原厂有 `setMarqueeEnable(false)` / `stopRoll()` / `onDetachedFromWindow()` 命令式收尾并恢复多行语义，移植只有 `enabled` 参数与 `LaunchedEffect` 取消。
- 结论：可见滚动行为近似，非 1:1。

### 顶部提示条 `TopTips`

- 一致：250ms 只做 `scaleY`（`COUICustomTopTips.java` 的 `showWithAnim()` / `dismissWithAnim()` 各一个 `ObjectAnimator.ofFloat(this,"scaleY",…)` + `setDuration(250L)`；移植 `graphicsLayer { scaleY = scale.value }` 不改 alpha）；底色 `Color(0x0A000000)` / `Color(0x14FFFFFF)` 与 `res/values/colors.xml:1068-1070` 及 `res/values-night/colors.xml:350` 的 `coui_color_fill_thin` 完全一致；20dp 最小高、12dp 上下与 16dp 左右留白与 `res/values/dimens.xml:2078-2105` 的 `coui_toptips_*` 一致。
- 差异（轮廓）：原厂 `init()` 在 `RoundCornerUtil.isVersionSupport()` 时取 `couiRoundCornerMRadius=9dp` / `couiRoundCornerMWeight=0.99`（`res/values/dimens.xml:1634,1637`；`res/values/styles.xml:12377-12379`），否则取 `couiRoundCornerM=12dp` 普通圆角；移植默认 `shape = CouiTheme.shapes.medium` = `CouiShape(12.dp)`（`coui/src/main/java/com/itos/coui/shape/CouiShapes.kt:28`），权重取 `CouiCornerWeight = 2.5f`（`CouiShape.kt:17`）。`0.99` 低于 `CouiShape.kt:24` 的 `require(cornerWeight >= 2f)` 下限，现有 `CouiShape` 无法表达原厂轮廓。
- 其他差异：移植 `tween(250)` 默认 `FastOutSlowInEase`，原厂 `ObjectAnimator` 未显式设置插值器（默认 `AccelerateDecelerateInterpolator`）；原厂命令式 `showWithAnim/dismissWithAnim` 带 `AnimatorListener`，移植是 `visible` 受控状态、无回调；移植无 `setContentView(View)` 重复调用保护（原厂抛 `RuntimeException("Repeat calls are not allowed!!")`）；`TopTipsContent.kt` 以单 `Row` 装配图标/标题/`MarqueeText` 正文/操作/关闭，对应 `coui_default_toptips.xml` 的顺序，但缺原厂 `COUIDefaultTopTips` 的双按钮横竖排约束、`AI_PAYMENT` 样式与 300ms 宽度变化动画（`WIDTH_CHANGE_ANIMATION_DURATION=300`），属有意的范围收缩；原厂 `COUIDarkModeUtil.setForceDarkAllow(this,false)` 无对应（移植容器色已按 `dark` 取值）。
- 结论：容器动画与配色对齐，轮廓不一致且有损地简化了默认内容布局，非 1:1。

### 竖向滑动条 `VerticalSeekBar`

- 一致：核心常量全部吻合——`trackEnlargeScale=6f` / `progressEnlargeScale=4f`（`BACKGROUND_RADIUS_SCALE` / `PROGRESS_RADIUS_SCALE`）、`damping=0.4f`（`MAX_MOVE_DAMPING`）、`clickMinMillis=150L` / `clickDurationFactor=483f`（`DURATION_150` / `DURATION_483`）、`flingThreshold=100f`（`PHYSICAL_VELOCITY_LIMIT`）、8000 最大速度、`flingDamping=15f`（`mFlingLinearDamping`）；KDoc 声明的 2.8 / 1.0 弹簧参数与 `COUIVerticalSeekBar.java:2443-2445` 的 `mFlingFrequency=2.8f` / `mFlingDampingRatio=1.0f` / `mFlingLinearDamping=15.0f` 精确一致。36dp 最小宽、480dp 最大高、10dp 纵向 padding、2dp 轨道/进度半径、4dp 轨道/进度宽、12sp 文字、进度自底部、绘制顺序「轨道 → 进度 → 滑块 → 文字」与 `res/values/styles.xml:5500-5515`、`res/values/dimens.xml:2141-2145` 一致。
- 滑块尺寸（已收敛）：原厂 `ensureSize()`（`COUIVerticalSeekBar.java:726,731`）算 `mThumbOutRadius = mProgressRadius * mProgressRadiusEnlargeScale` = 8dp、`mThumbOutWidth = mProgressWidth * mProgressEnlargeScale` = 16dp，`drawThumb()`（:673-680）画 16dp 宽高、8dp 圆角的圆角矩形；圆角等于半宽，与直径 16dp 的圆等价，本轮 `thumbSize` 由 12dp 改为 16dp。`res/values/styles.xml:5514` 的 6dp 属性被 `ensureSize()` 覆盖（全文件仅此一处给 `mThumbOutRadius` 赋值），不影响结果。
- 其他差异：原厂按下/抬手有 183ms 放大/回弹动画（`TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION`）+ `COUIMoveEaseInterpolator`，`setReleaseAnimatorValues()` 挂 5 个 `PropertyValuesHolder`（`progressRadius` / `backgroundRadius` / `progressWidth` / `backgroundWidth` / `animatePadding`），并用 `coui_vertical_seekbar_progress_pressed_padding_vertical=4dp` 收敛按下态 padding（`res/values/dimens.xml:2142`）；本轮移植补了 183ms `animateFloatAsState` 过渡（插值器为 `tween` 的 `FastOutSlowInEase`，非原厂 `COUIMoveEaseInterpolator`），仍无 padding 收敛。原厂 `mShowText` 默认 false（`COUIVerticalSeekBar.java:2471`）、文字带阴影（`TEXT_SHADOW_DY=8` / `RADIUS=25`），移植 `label` 非空即画且无阴影。原厂平滑圆角走 `OplusCanvas` / `OplusPathAdapter`，移植用 `addRoundRect` 普通圆角。原厂形变、线性马达分档震动 `TYPE_STEPABLE_EDGE` / `TYPE_STEPABLE_REGULATE`、`FlingBehavior` 边界回弹，移植 KDoc 已声明省略；原厂 `performAdaptiveFeedback()` 在档位边界触发，本轮移植改为每跨越 5% 档位触发一次 `performSeekHaptic`。原厂 `mMaxHeight` 默认 0（`:2491`，style 无 `MaxHeight` 项），移植固定 480dp 上限。
- 配色核验：`res/values/styles.xml:5502,5506,5513` 与 `.Dark`（:5517-5519）/ `.Light`（:5522-5524）引用的是 `res/color/coui_seekbar_{background,progress}_selector*.xml`，启用态解析为 `couiColorContainer12`（`#1f000000`，夜 `#33ffffff`）与 `couiColorContainerTheme` / `couiColorPrimary`；移植初版以 `coui_seekbar_background_color_normal`（`#0D000000` / `#1AFFFFFF`）与 `coui_seekbar_progress_color_normal`（`#4D4D4D` / `#999999`）为依据，这四个静态色在 `res/` 中只被 `colors.xml` / `values-night/colors.xml` / `public.xml` 声明，未在任何 style 或 selector 引用，**配色依据错误**。本轮已按 selector 实值修正：轨道 `#1F000000` / `#33FFFFFF`（`coui_color_container12`），进度与滑块跟随 `CouiTheme.colorSchemeFor(dark).accent`（对应主题属性），禁用态取强调色 30% alpha（`coui_color_disable_blue` 口径）。主题色随宿主主题变化，仍待真机对照。
- 结论：几何与物理参数高度对齐；滑块尺寸、按压过渡、分档震动与配色依据本轮已按源码收敛，剩余差异为厂商物理引擎回弹、形变、平滑圆角路径、padding 收敛、文字阴影、动画插值器与主题色真机观感，非 1:1。

### 核查后的剩余工作

1. 使用文档、Demo 与导航已补齐：三个使用页、`DemoSystemWidgets.kt`、中英文字符串、`DemoCatalog.kt` / `DemoSecondaryPage.kt` 路由；`docs/components/README.md` 与 `zensical.toml` 已登记三页。`top-tips.md` 已写明 9dp/0.99 与 12dp/2.5 的轮廓差异，`vertical-seek-bar.md` 的配色依据已按 selector 实值改写。
2. 三个使用页的「与原厂的差异」段已按静态核查写实：渐隐改为「机制不同、需真机同背景对照」，顶部提示条改为「轮廓、插值器不一致，且有多项原厂能力不在实现范围内」，不再做等价断言。
3. 本轮已按源码收敛可静态确定的偏差：1000ms 起始延迟、滑块 16dp、183ms 按压过渡、5% 分档触感、selector 配色与禁用态。仍待真机同背景/手势对照的只有观感项：轮廓权重 0.99（现有 `CouiShape` 无法表达）、渐隐曲线、按压插值器、padding 收敛、文字阴影、主题强调色、厂商物理引擎回弹。
4. 按 AGENTS.md「默认不因代码修改而更新规范或新增 Note」，本轮差异属验收结论，记入本表与交付说明，不新增 Note、不修改规范。

## 逆向材料缺口

- `oplus-framework.jar` 未拉取：`pull.sh:105-106` 列有 `oplus-framework.jar` / `oplus-services.jar` / `oplus-service-jobscheduler.jar` / `boot-oplus-framework.vdex`，本轮 `adb devices` 无在线设备、`references/apk/` 下无任何 `.jar`，`OplusRenderEffect`、`OplusPathAdapter`、`ViewRootManager` 的 ABI 与内部算法只能从调用侧推断。**唯一仍需真机授权才能闭合的缺口。**
- 已反编译 dex 目录精确为 6 个：Settings `classes`(2171) / `classes7`(3115) = 2/14；SystemUI `classes3`(4015) / `classes4`(5067) / `classes9`(3602) = **3/9**（旧写 4/9 有误）；PhoneManager `classes`(5388) = 1/6。另持久化了 `phonemanager/classes2-single/COUICustomSnackBar.java`。
- `COUICustomSnackBar` 已反编译（旧记「只有类描述符」不成立），见[逆向分析 §9](../../references/apk/ANALYSIS.md)；类在 `phonemanager/decoded/classes2.dex`，JADX 未完全恢复，只作结构证据。
- QS `IDeformationAnimationController` / `CellCalculatorManager` 的约束数学已展开（旧记「未展开」不成立），见[逆向分析 §6](../../references/apk/ANALYSIS.md)。
- `EffectiveAnimationView` 的 Lottie 资源未逐个归类，规模口径见[逆向分析 §7](../../references/apk/ANALYSIS.md)。

详见[逆向分析报告](../../references/apk/ANALYSIS.md#9-未覆盖)。

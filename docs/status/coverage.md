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
| QS 磁贴 | `DeformationFrameLayout` + `DragBarView` + `StrokeDrawable` + 展开面板 | `QuickSettingTile` / `LevelSlider` / `DeformationTileGrid` | 磁贴的开关态、档位条与编辑态形变已覆盖：网格按跨度重排、20dp 圆角、2dp/4dp 编辑态描边、把手拖动 0.35 格切换 1×1 与 2×1、response 0.35 弹簧过渡。差异：`DeformationFrameLayout` 依赖 SystemUI 私有 `CellCalculatorManager`/`SpanCache`/`springtransition`，改用 Compose 弹簧与单元格重算；跨行跨度与拖动换位未移植；QS 面板的展开与分页联动不在组件范围 | 可见行为近似，非 1:1；编辑态磁贴网格可用 | [控制中心磁贴](../components/quick-setting.md) |
| 选择器 | `COUIDatePicker` / `COUITimePicker` / `COUILunarDatePicker` / `COUITimeLimitPicker` | `WheelPicker` / `CalendarPicker` / `DatePicker` / `TimePicker` / `TimeLimitPicker` / `LunarDatePicker` / `CouiLunarCalendar` | 年月日、时分（含 AM/PM 与日期列）、带上限的时分、农历年月日都按原厂列序、取值与文案移植；农历互转覆盖 1900–2100。差异：原厂内嵌 `CalendarView` 的切换拆为 `CalendarPicker` 组合；`COUINumberPicker` 的振动与 View 专有开关未复刻；农历节气与节日未移植；时间列的日期文案按 `EEE`/`MMM d` 组合而非 `DateUtils` flag | 可见行为近似，非 1:1；设置页日期时间入口可直接使用 | [选择器](../components/pickers.md) |
| 输入框 | `COUICodeInputView`、`COUICardMultiInputView`、错误气泡 | `TextField`（含 `error`）、`CodeInputView`、`MultiInputCard` | 错误态的抖动、描边染色与文案淡入淡出按原厂时长移植；验证码格子的缩放、间距公式与逐位动画同原厂；多输入的 152dp 卡片、3/5 行与字数上限同原厂。差异：验证码不自抢焦点与自弹键盘、退格按光标删除、无逐格无障碍节点；多输入的计数留白与父级手势拦截沿用 Compose 语义 | 可见行为近似，非 1:1；表单与验证码页面可用 | [输入框](../components/text-field.md) / [验证码](../components/code-input.md) / [多行卡片](../components/multi-input.md) |
| 弹窗 | `COUIGuideDialogBuilder` / `COUISecurityAlertDialogBuilder` / `COUIAlertDialogBuilder`（列表） | `Dialog` / `InputDialog` / `LoadingDialog` / `GuideDialog` / `SecurityDialog` / `ListDialog` | 引导弹窗的分页、指示器、按钮宽度弹簧与文字交叉淡入按原厂参数移植；安全弹窗的说明文案、链接标注与勾选框度量同原厂；列表弹窗的项高、内边距、分隔线缩进与多选上限同原厂。差异：翻页手势不复制原厂甩动阈值与衰减、程序化翻页用 650ms 缓动、勾选框间距用公共 Checkbox、分隔线画 1px、旋转加载弹窗与推荐按钮未移植 | 可见行为近似，非 1:1；引导、隐私确认与底部选择列表可用 | [弹窗](../components/dialog.md) |
| Chip | `COUIChip` / `COUIChipGroup` / `COUIChipGroupEditText` / `COUIChipDrawable` | `Chip` / `ChipGroup` / `ChipGroupEditText` / `CouiChipDefaults` | 四套样式的尺寸、宽度公式、文字居中、关闭区与红点位置、染色与高度弹簧按原厂参数移植；单选/必选交给宿主，`smoothtyle` 平滑轮廓、`hovered`/`focused` 染色与 drawable mask 按下态未复刻 | 可见行为近似，非 1:1；标签编辑场景可用 | [Chip](../components/chip.md) |
| 导航栏变体 | `COUINavigationRailView` / `COUISideNavigationBar` / `COUIBottomFloatingNavigationBar` / `COUIBottomFloatingAccessoryNavigationBar` | `NavigationRail` / `SideNavigationBar` / `BottomFloatingNavigationBar` / `AccessoryNavigationBar` | 导航轨的菜单宽度、居中、项间距公式与分隔线同原厂；侧边导航的停靠判定、面板宽度（抽屉 300dp、停靠 `min(280dp, 宽*0.382)`）与项度量同原厂；悬浮底栏的 56/48/70/12/8dp 尺寸与横向滚动同原厂；附件的低端档弹簧（0.2/0.5+0.3 回弹、最小缩放 0.5）同原厂。差异：`DrawerLayout` 的边缘拖动、返回键与预测性返回、编辑态遮罩未复刻；导航轨红点、长按禁用与大图标档位未移植；底栏融球材质、溢出菜单与编辑态附件栏未移植；附件回弹按 `dampingRatio=0.7` 近似 | 可见行为近似，非 1:1；平板分栏与系统页导航可用 | [导航栏变体](../components/navigation-layout.md) |
| GPU 抗锯齿等 | `fwidth` 覆盖率、逐次随机噪声、三维投影 | 代码里的 `shortcut:` 注释 | 13 处显式降级，按公开 API 可表达的范围实现 | 已在注释逐条声明，不算隐藏缺陷 | `coui/.../effect/`、`backdrop/CouiBackdrop.kt` |
| 按钮组排布 | `SimpleButtonGroupCtrl` + `COUIButtonLayout`（`getButtonLineCount` 比较文案与列宽） | `ButtonGroup` | 尺寸、间距、小屏均分、竖排反转与原厂一致；高度下限用 `coui_alert_dialog_button_min_height_multiline` 56dp，未复刻 `getButtonMaxHeight` 的逐行公式 | 双行按钮高度可能相差 1–2dp | `coui/.../button/ButtonGroup.kt`、`references/apk/coui-src/com/coui/appcompat/button/SimpleButtonGroupCtrl.java` |
| 两行按钮文案 | `DescButtonTextSpan`（`canvas.drawText` 组合两组 `FontMetricsInt` 基线） | `Button(label, description)` | 字号、间距、单行省略、居中与 1.15 倍字体缩放封顶一致；排版交给 Compose，不能与同一段 `Spanned` 内联拼接 | 只可作按钮整体内容 | `coui/.../button/Button.kt`、`.../button/DescButtonTextSpan.java` |
| 按钮条 | `COUIButtonBarLayout` | `ButtonBar`，`Dialog` 复用同一布局 | 横竖排判定、宽度、留白、44/56dp 下限与超高滚动一致；按下按钮置顶绘制、推荐按钮、AI Payment 文本色、`UIUtil.setViewClipExpandSize` 裁剪扩展未复刻；`Dialog` 保留自身横竖排顺序 | 操作顺序与推荐的强调样式与原厂不同 | `coui/.../buttonBar/ButtonBar.kt`、`.../buttonBar/COUIButtonBarLayout.java` |
| 展开式悬浮按钮 | `COUIFloatingButton` + `COUIFloatingButtonLabel` + `COUIFloatingButtonItem` | `FloatingButton` / `FloatingButtonLabel` / `CouiFloatingButtonItem` | 尺寸、首项 32dp/其余 16dp 间距、标签 12sp/220dp/2 行、按下 200ms 到 0.9 + 抬手 340ms、展开弹簧 500/0.8 + 0.6→1 缩放与淡入、50ms 错开、收起 150ms/200ms、主按钮 45° 旋转、5s 自动收起均对齐 | `OplusViewSeamless` 转场、`IMeltBallView` 熔球、`CoordinatorLayout.Behavior`、caustic 参数表与抬手动画的最小可见变化（0.993/0.965/0.99）未复刻；只绘制普通投影 | `coui/.../floatingactionbutton/FloatingButton.kt`、`.../floatingactionbutton/{COUIFloatingButton,COUIFloatingButtonLabel,COUIFloatingButtonItem}.java` |
| 按压参数体系 | `IPressParams` / `PressParams`（五字段）+ `PressParamsSpringData` | `CouiPressFeedbackParams` + `CouiPressFeedbackDefaults` | 五个字段、两套原厂取值与 `(2π/response)²` の stiffness 映射一致；olivelink candy 分支的 0.3 response 与 0.0001 抬手最小变化并入同一组参数 | 宽高比极大的按压比原厂稍慢 | `coui/.../motion/CouiPressMotion.kt`、`.../pressfeedback/PressParams.java`、`.../touchanimation/param/olivelink/PressParams.java` |
| 水波纹分支 | `COUIPressRippleDrawable`（平台 `RippleDrawable` + `COUIPressMaskDrawable`） | `Modifier.couiPressRipple` | 颜色取 `coui_color_ripple_press_background`，遮罩形状与 `shape` 对齐，`padded` 对应 12dp/4dp 外扩 | 原厂直接用平台 RippleDrawable，移植按 20/75/150/200–400ms 近似，非 1:1 | `coui/.../effect/CouiPressRipple.kt`、`.../rippleutil/COUIPressRippleDrawable.java` |

## 未实现 / 待验收

「计划」列是本轮之后的候选顺序，不代表已排期。用户指定的三个小组件与后续「下一轮」三项均已实现，并按静态双向核查（见下两节）收敛了可静态确定的偏差，结论仍为「可见行为近似，非 1:1」。本轮补齐的标签、链接文本、滚动条与数值步进器四项同样只做了静态对照，差异写在各自使用页；十项均未做真机验收，故仍留在本表。十项的公开入口、使用文档、Demo 与两处导航（`docs/components/README.md`、`zensical.toml`）均已登记。

最近一轮补齐的十一族——标签、验证码与多行输入、日期与时间选择器、引导 / 安全 / 列表弹窗、导航栏变体、分段与图标滑动条、安装进度按钮、控制中心编辑态形变、状态效果表面、渐进式手势动画、用户关注顶栏——同样只做了静态对照：能静态确定的偏差写在各自使用页的「与原厂的差异」并汇总到本表或上方的「有偏差」表，全部未做真机验收。

| 能力 | 原厂实现 | 现状 | 计划 | 证据 |
| --- | --- | --- | --- | --- |
| 跑马灯文本 | `COUIMarqueeTextView`（自绘滚动、左右渐隐、按测量宽度决定是否跑） | 已实现 `coui/src/main/java/com/cycling/coui/text/MarqueeText.kt`；静态双向核查见下节，已补 1000ms 起始延迟，非 1:1 | 差异处理、使用文档与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/tips/COUIMarqueeTextView.java`、`res/values/dimens.xml:2070-2073` 的 `coui_top_tips_*` |
| 竖向滑动条 | `COUIVerticalSeekBar`（竖向进度、按压形变、物理速度、文字） | 已实现 `coui/src/main/java/com/cycling/coui/seekbar/VerticalSeekBar.kt` + `CouiVerticalSeekBarDefaults.kt`；KDoc 声明省略厂商 `FlingBehavior`、形变与线性马达；静态双向核查见下节，滑块尺寸、按压过渡、分档震动与配色已按核查收敛，非 1:1 | 差异处理、使用文档与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/seekbar/COUIVerticalSeekBar.java`、`res/values/attrs.xml:1577-1607`、`res/values/styles.xml:5500-5525`、`res/values/dimens.xml:2141-2145` |
| 分段 / 图标滑动条 | `COUISectionSeekBar`（段标记、0.4 跟手、换算档位与吸附）、`COUIIntentSeekBar` 的图标滑条（胶囊面板、图标旋转、0.4~1 阻尼） | 已实现 `coui/src/main/java/com/cycling/coui/seekbar/SectionSeekBar.kt` + `CouiSectionSeekBarDefaults.kt`、`.../IconSeekBar.kt` + `CouiIconSeekBarDefaults.kt`；段标记分工、跟手系数、吸附与点击过渡时长、图标面板几何按原厂参数移植；`effect/` 的 ConicLight / Glitter / Loading / Deformation 渲染链未移植（依赖 `COUIIntentSeekBar` 的私有 AGSL `IConicLightDrawer` 与 `com.oplus.physicsengine`），非 1:1 | 效果链与真机验收 | `references/apk/coui-src/com/coui/appcompat/seekbar/{COUISectionSeekBar,COUIIconSeekBar}.java`、`.../seekbar/effect/`；[分段与图标滑动条](../components/section-seek-bar.md) |
| 渐进式手势动画 | `animatekit/progressive`（`HorizontalProgressiveConfig`、`ProgressiveCalculator`、`ProgressiveGestureSession`、`AnimationPhase`、`RubberbandConfig`） | `ProgressiveDragState` + `rememberProgressiveDragState` + `Modifier.couiProgressiveDrag` + `CouiProgressiveDefaults`（`coui/src/main/java/com/cycling/coui/motion/`） | 相位（拖动/回落/橡皮筋拖动/回弹）、按行距递增的响应与步长封顶（`span * step + base > max` 时取 `(max - base) / span`）、速度对回落响应与回弹的修正、橡皮筋公式 `(1 - 1/(curveRatio*over/maxDistance + 1))*maxDistance` 均按原厂参数移植。差异：原厂是 SystemUI 内部 View + 协程框架并依赖私有 `OplusAnimator`/`springtransition`，这里换用 `CouiSpring`；同一行距共用一条弹簧；触发排与行分组由宿主传入而非内部命中测试；`blendDuration` 未做 250ms 参数混合；`addGroup`/`GroupView` 多组联动收敛为 `setProgressWindow` + `progress` | 可见行为近似，非 1:1；整排跟随式拖动可用 | `references/apk/coui-src/com/coui/animatekit/progressive/`；[渐进式拖动](../components/progressive-drag.md) |
| 状态效果表面 | `COUIStateEffectDrawable` + `COUIMaskEffectDrawable` + `COUIStrokeDrawable` + `StateEffectAnimator` | `StateEffectSurface`（`coui/src/main/java/com/cycling/coui/state/`） | 图层顺序、三层蒙层、焦点并入选中、禁用归零、按压 `enableScaleEffect` 缩放均按原厂语义组合；蒙层与描边复用 `Modifier.couiStateMask` / `Modifier.couiFocusOutline`。差异：弹簧只有响应 0.3 一档、Processor 体系（padding/margin/尺寸/字号按状态替换）未移植、`setStateLocked`/`setNativeStateEnabled` 无对应 API、圆角轮廓改由 `shape` 指定、波纹未并入容器 | 可见行为近似，非 1:1；需要整块状态反馈的卡片与列表行可用 | `references/apk/coui-src/com/coui/appcompat/state/`；[状态效果表面](../components/state-effect.md) |
| 安装进度按钮 | `COUIInstallLoadProgress`（`COUILoadProgress` 状态机）、`COUILottieLoadingView` | `InstallLoadProgress`（`coui/src/main/java/com/cycling/coui/progress/InstallLoadProgress.kt` + `CouiInstallLoadProgressDefaults.kt`） | 小 52×28dp / 大 220×44dp、圆角 13.5 / 21.5dp、底色 + 进度填充 + 双色文字、按下亮度 0.8 与内缩 5% 与文字 0.92 倍、200ms 进入 / 340ms 回弹、进度弹簧 response 1.0 均按原厂移植；非中文环境小号加宽 30dp。差异：`LOAD_STYLE_CIRCLE` 的 33dp 位图样式、`INSTALL_HAVE_GIFT` 礼包背景、`couiButtonDrawable` 自绘图标与末尾省略号截断未移植；`COUILottieLoadingView` 依赖厂商 `com.oplus.anim` 未移植 | 圆形样式与真机验收 | `references/apk/coui-src/com/coui/appcompat/progressbar/{COUIInstallLoadProgress,COUILoadProgress,COUILottieLoadingView}.java`、`res/values/styles.xml:14483-14503`、`res/values/dimens.xml:1280-1293`；[进度与占位](../components/progress.md) |
| 用户关注顶栏 | `COUIUserFollowView`（`MotionLayout` + 运行时 `MotionScene`）、`COUIUserInfoToolbar` | `UserFollowBar`（`coui/src/main/java/com/cycling/coui/topappbar/UserFollowBar.kt` + `CouiUserFollowDefaults.kt`） | 头像 24dp、按钮 52×28dp、高度 52dp、间距 8dp 按原厂约束移植；关注态在 300ms 内插值按钮底色、内容色、字号与位置，副标题可省略。差异：原厂 `MotionLayout` 场景换成三条 `animateFloatAsState`、`COUICustomToolbar` 封装与 `setAnimate(false)` 跳变无对应、头像改用圆形容器加 1dp 边框且 `setImage(Bitmap)`/`release()` 未移植、副标题隐藏改为高度插值而非 `Barrier` 重连约束 | 可见行为近似，非 1:1；用户主页顶栏可用 | `references/apk/coui-src/com/coui/appcompat/toolbar/userfollow/`、`res/values/styles.xml:14346`、`res/values/dimens.xml:778-783`；[用户关注顶栏](../components/user-follow-bar.md) |
| 顶部提示条 | `COUICustomTopTips`（卡片容器、显示/消失 250ms 动画、内容布局由子类给出） | 已实现 `coui/src/main/java/com/cycling/coui/toptips/TopTips.kt` + `CouiTopTipsDefaults.kt` + `TopTipsContent.kt`（对应 `coui_default_toptips.xml` 的默认排布）；静态双向核查见下节，轮廓差异仍在，非 1:1 | 差异处理与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/tips/COUICustomTopTips.java`、`res/layout/coui_default_toptips.xml`、`res/layout/coui_ai_payment_toptips.xml` |
| 页签条 | `COUITabLayout` + `COUISlidingTabStrip` | 已实现 `coui/src/main/java/com/cycling/coui/tablayout/TabLayout.kt` + `CouiTabLayoutDefaults.kt`；高度、字号、项间距与固定模式间距分配按 Settings 实际样式与 `onMeasure` 收敛，文字色过渡为连续插值，非 1:1 | 差异处理与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/tablayout/COUITabLayout.java`、`COUISlidingTabStrip.java`、`res/values/styles.xml:4487-4530,5338-5355,7984-7986`、`res/values/dimens.xml:1955-1966,4916-4930`；[页签条](../components/tab-layout.md) |
| 搜索历史 | `COUISearchHistoryView` + `COUIFlowLayout` | 已实现 `.../flowlayout/FlowLayout.kt` + `.../searchhistory/SearchHistory.kt`；换行、折叠行数、展开/折叠动画与清空淡出按源码收敛，chip 按压反馈与字号适配未移植，非 1:1 | 差异处理与真机验收 | `references/apk/jadx/settings/classes/sources/com/coui/appcompat/searchhistory/COUIFlowLayout.java`、`COUISearchHistoryView.java`、`res/values/dimens.xml:937-946`；[换行布局](../components/flow-layout.md) · [搜索历史](../components/search-history.md) |
| Snackbar | `COUISnackBar` / `COUINotificationSnackBar` | 已实现 `.../snackbar/Snackbar.kt` + `CouiSnackbarDefaults.kt`，与 `ToastHost` 共用同一绘制节点；单实例互斥、任意时长、触摸暂停后续满计时、弹簧进出场按源码收敛，通知样式与拖动消失未移植，非 1:1 | 差异处理与真机验收 | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/snackbar/COUISnackBar.java`、`res/values/dimens.xml:1871-1906`；[Snackbar](../components/snackbar.md) |
| PIN / 图案锁 | `COUIPasswordDial`、`COUILockPatternView`、`COUINumericKeyboard` | 无 | 系统页批次 | `.../passworddial/`、`.../lockview/` |
| 隐私声明面板 | `COUIStatementPanel` / `COUIFullPageStatement` | 无 | 系统页批次 | `.../statement/` |
| 编辑态底栏 | `COUIBottomFloatingEditToolbar` | 只有 `NavigationBar` 左右两个 icon；附件栏已由 `AccessoryNavigationBar` 覆盖，见 [导航栏变体](../components/navigation-layout.md) | 系统页批次 | `.../bottomfloatingedittoolbar/`、`.../bottomfloatingaccessorynavigationbar/` |
| 弹簧链列表 | `COUIGridSpringChainScrollView` | `scroll` 只到弹性滚动与 fling | 视需求 | `.../springchain/` |
| 轮播 Banner | `COUIBanner` + `COUIPageIndicatorKit` | 只有 `PageIndicator` | 视需求 | `.../banner/` |
| 可展开列表 | `COUIExpandableListView` / `ExpandableRecyclerView` | `Accordion` 是单卡内一段 | 视需求 | `.../expandable/` |
| 聚光引导 | `COUISpotLightEffect`（挖洞 + 引导页） | 只有 `CouiSpotlight` shader | 视需求 | `.../spotlight/` |
| Preference 家族 | 80 个 `COUI*Preference` 顶层类 | `Item` + `Switch` / `Checkbox` 可组合替代，不逐类 1:1 | 不单独立项 | [Item](../components/item.md)、[Group](../components/group.md) |
| 标签 | `COUITagBackgroundView` / `COUITagView`（四角轮廓、可选描边、左图标 5×10dp、10dp `sans-serif-medium`） | 已实现 `coui/src/main/java/com/cycling/coui/tag/Tag.kt`；几何、字号与轮廓按源码收敛，容器色默认沿用原厂 attr 组合（`couiColorContainer8` 薄填充 + 白字），实际配色由宿主传入 | 真机验收 | `.../tagview/COUITagView.java`、`COUITagBackgroundView.java`、`res/layout/coui_tag_view_layout.xml`、`res/values/dimens.xml:1019` |
| 链接文本 | `COUIClickableSpan`（`?couiColorLink` + `#4D2660F5` 按下/选中 + 禁用态） | 已实现 `Text` 的 `AnnotatedString` 重载与 `CouiTextLinkDefaults`；Compose 的 `TextLinkStyles` 没有禁用态，需宿主改普通 span，非 1:1 | 真机验收 | `.../clickablespan/COUIClickableSpan.java`、`res/color/coui_clickable_text_color.xml`、`res/values/colors.xml:844-846` |
| 滚动条 | `COUIScrollBar`（14dp 宽、48dp 最小高、8/16dp 留白、5.5/4dp inset、2000ms 停留后 250ms 淡出、拇指拖动） | 已实现 `coui/src/main/java/com/cycling/coui/scrollbar/ScrollBar.kt`；几何、显隐与拖动换算按源码收敛，只接受 `ScrollState` 且不取消滚动容器已进行的 fling，非 1:1 | 真机验收、`LazyListState` 适配 | `.../scrollbar/COUIScrollBar.java`、`res/values/dimens.xml:1670-1675`、`res/values/colors.xml:1502` |
| 数值步进器 | `COUIStepperView` + `IStepper` + `ObservableStep` + `LongPressProxy` | 已实现 `coui/src/main/java/com/cycling/coui/stepper/Stepper.kt`（含同文件的 `CouiStepperDefaults`、`CouiStepperColors`）；范围夹紧、上下界禁用、长按 100ms 重复与每次提交触感按源码收敛，动画弹簧与点击 slop 未逐值核对，非 1:1 | 真机验收（长按节奏、按压形变与焦点描边） | `references/apk/jadx/settings/classes7/sources/com/coui/appcompat/stepper/`、`res/layout/coui_stepper_view.xml`、`res/values/styles.xml:5356-5364`、`res/values/dimens.xml:4433`；[数值步进器](../components/stepper.md) |

## 双向核查：三个用户指定优先组件

按[复刻指南](../development/replication-guide.md)的「原厂 → 移植、移植 → 原厂」要求，对 `coui/src/main/java/` 下三个实现逐条比对 `references/apk/jadx/settings/classes7/` 的反编译源码与 `references/apk/settings/decoded/res/` 的资源。以下是静态核查结论；参数一致、终点收敛不等于 1:1 验收，涉及观感的条目仍须真机同背景对照。

### 跑马灯文本 `MarqueeText`

- 一致：`CouiMarqueeTextDefaults` 的 `interval=80.dp` / `speed=25.dp` / `startLocation=10.dp` / `fadingEdgeLength=26.dp` 与 `res/values/dimens.xml:2070-2073` 的 `coui_top_tips_*` 四项一致。原厂 `initSpeed()` 用 `25dp / display.getRefreshRate()` 每帧推进，移植用 `withFrameNanos` 按 `25dp * seconds` 推进，每秒位移等价；空档空格数都是 `ceil(intervalPx / spaceWidth) + 1`（原厂 `iCeil+1`，无 off-by-one）；RTL 反向、无障碍固定原始文本、超宽才滚动、两端渐隐、离屏段回收（模 `unitWidth` 回绕 vs 删首段追加尾段）语义等价。
- 差异（已收敛）：移植原缺 `DEFAULT_SCROLL_DELAY_DURATION=1000` 起始延迟（`COUIMarqueeTextView.java` 构造函数末尾 `postDelayed(mStartScrollRunnable, 1000L)`），本轮已在滚动 `LaunchedEffect` 开头补 `delay(1000)`；原厂滚动判定用 `setContent()` 后的当前 `getText()`（重复串），移植用原始单行 `textWidth`；原厂是 `ValueAnimator.ofInt(Integer.MAX_VALUE)` + `COUILinearInterpolator` 帧驱动，移植是 `withFrameNanos` 时间驱动；原厂渐隐是 TextView 的 horizontal fading edge，移植是 `PorterDuffXfermode(DST_IN)` 线性渐变遮罩；原厂有 `setMarqueeEnable(false)` / `stopRoll()` / `onDetachedFromWindow()` 命令式收尾并恢复多行语义，移植只有 `enabled` 参数与 `LaunchedEffect` 取消。
- 结论：可见滚动行为近似，非 1:1。

### 顶部提示条 `TopTips`

- 一致：250ms 只做 `scaleY`（`COUICustomTopTips.java` 的 `showWithAnim()` / `dismissWithAnim()` 各一个 `ObjectAnimator.ofFloat(this,"scaleY",…)` + `setDuration(250L)`；移植 `graphicsLayer { scaleY = scale.value }` 不改 alpha）；底色 `Color(0x0A000000)` / `Color(0x14FFFFFF)` 与 `res/values/colors.xml:1068-1070` 及 `res/values-night/colors.xml:350` 的 `coui_color_fill_thin` 完全一致；20dp 最小高、12dp 上下与 16dp 左右留白与 `res/values/dimens.xml:2078-2105` 的 `coui_toptips_*` 一致。
- 差异（轮廓）：原厂 `init()` 在 `RoundCornerUtil.isVersionSupport()` 时取 `couiRoundCornerMRadius=9dp` / `couiRoundCornerMWeight=0.99`（`res/values/dimens.xml:1634,1637`；`res/values/styles.xml:12377-12379`），否则取 `couiRoundCornerM=12dp` 普通圆角；移植默认 `shape = CouiTheme.shapes.medium` = `CouiShape(12.dp)`（`coui/src/main/java/com/cycling/coui/shape/CouiShapes.kt:28`），权重取 `CouiCornerWeight = 2.5f`（`CouiShape.kt:17`）。`0.99` 低于 `CouiShape.kt:24` 的 `require(cornerWeight >= 2f)` 下限，现有 `CouiShape` 无法表达原厂轮廓。
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

## 双向核查：「下一轮」三项

同样按[复刻指南](../development/replication-guide.md)做原厂 → 移植、移植 → 原厂的静态核查，入口为 `references/apk/jadx/settings/classes{,7}/sources/com/coui/appcompat/` 下的反编译源码与 `references/apk/settings/decoded/res/`。参数一致不等于 1:1，观感项仍须真机同背景对照。

### 页签条 `TabLayout`

- 一致：`SettingsSmallTabLayoutStyle`（父 `COUISmallTabLayoutStyle`）的 `layout_height=40dp`、`couiTabTextAppearance`→`COUISmallTabViewStyle`/`COUIMiddleTabViewStyle` 的 `tablayout_large_text_size=16`、`tablayout_selected_indicator_height=2dp`、`DEFAULT_MIN_INDICATOR=32dp`、`coui_tablayout_indicator_padding=0dp`、`ANIMATION_DURATION=300`、`getIndicatorAnimTime()` 的 `min(300, |from-to|×50+150)`；`COUISlidingTabStrip.onMeasure()` 的间距分配——项宽取 `tabMinDivider`（small 20dp）、滚动模式容器内边距 `tabMinMargin - tabMinDivider/2`（16−10=6dp）、固定模式 `i5 = (n−1)×divider + margin×2 + 文字总宽`，未超 `coui_tab_layout_content_min_width=360dp` 时 `iMax = max((360−文字总宽)/(n+1), divider)` 且每项两侧各得 `iMax/2`，超视口时退化为固定间距居中、再宽则按 `margin − divider/2` 排布；`setIndicatorPosition()` 以文字宽加两侧 padding、按 32dp 下限居中；`dispatchDraw()` 把指示器画在容器底部并按可见滚动范围裁剪；`calculateScrollXForTab()` 在 `positionOffset=0` 时等价于「选中项中心 − 视口中心」；`MEDIUM_FONT` / `REGULAR_FONT` 对应选中与未选中字重。
- 差异：原厂 `animateIndicatorToPosition()` 用 `ArgbEvaluator` 只对切换的两个页签插值文字色，移植用连续的选中进度对所有页签插值；原厂底部分隔线是 1px，移植按 1dp；原厂按 `COUIResponsiveUtils` 在 small/medium/large 屏切换 16/24/40dp 外边距与 20/24dp 间距，移植固定取手机值；原厂支持自定义视图、图标、红点角标、`COUITabLayoutMediator` 与 ViewPager 自动联动，以及 `setScrollPosition()` 带入的拖动中指示器宽度插值（`mLastSelectionOffset` 方向分支），移植只接受整数下标。
- 结论：高度、字号、项宽与静止几何对齐，响应式分支、连续拖动与扩展分支非 1:1。

### 换行布局与搜索历史 `FlowLayout` / `SearchHistory`

- 一致：`Line.canAdd()` 的「已用 + 间距 + 本项宽度 ≤ 可用宽度」、首项按可用宽度夹紧；`fillChildInLines()` 超过 `maxRow` 后其余项不再排版；`getFoldedStateHeight()` / `getExpandedStateHeight()` 按行数取容器高度；`ensureAndExpandButton()` 在放不下时让第 `maxRowsFolded` 行最后一个标签退位、展开时把折叠按钮放最后一行；`EXPAND_BUTTON_APPEAR_DURATION=250`、`HIDDEN_VIEWS_APPEAR_DELAY=100`、`HIDDEN_VIEWS_APPEAR_DURATION=400`、`HIDDEN_VIEWS_DISAPPEAR_DURATION=300` 与 `COUIMoveEaseInterpolator` 的 `(0.3, 0, 0.1, 1)`；`SEARCH_VIEW_DISAPPEAR_DURATION=300` 的整块 alpha 淡出；`coui_component_search_history_*` 的 8/8/8/16/16/16/32dp 留白、18dp 删除图标、7dp 标题上下留白与 `maxRowUnfolded=8`；`Widget.COUI.Chip.Suggestion` 的 28dp 高、52–300dp 宽、12dp 水平内留白。
- 差异：原厂用 `View.measure` 与 `COUIChip`，移植在组合阶段用 `TextMeasurer` 估算宽度、由 `widthIn` 与内留白决定实际宽度；原厂标签有圆角/按压反馈/关闭图标与 `COUIChangeTextUtil.adaptFontSize`，移植没有；展开/折叠与清空标记由库自绘，原厂用 drawable 与 `COUIPressFeedbackImageView`；原厂 `drawChild()` 逐子视图裁剪，移植用 `clipToBounds()` 裁剪整列；原厂 `onDetachedFromWindow()` 取消动画，移植由 `animateFloatAsState` 随组合退出收尾。
- 结论：换行、折叠、展开与清空的主要状态转换对齐，标签自身外观与按压反馈非 1:1。

### Snackbar `SnackbarHost`

- 一致：`coui_snack_bar_margin_bottom=32dp`、`max_width=328dp` 与 `coui_snack_bar_item.xml` 的容器/文案/操作/图标排布（沿用 `CouiToastSurface`，不复制第二份）；`setSnackBarProgress()` 的 0.8↔1 缩放与 0↔1 alpha；`DEFAULT_SNACKBAR_SHOW_SPRING_RESPONSE=0.3`、`DISMISS=0.25`、`BOUNCE=0`；`setDuration(0)` 不自动消失；`onTouchEvent()` 的 DOWN/MOVE 取消计时、UP/CANCEL 重新按完整时长 `postDelayed`；`setOnAction()` 点击后 `dismiss()`；`make()` 发现父容器已有可见 Snackbar 时不再 `addView`；`OnStatusChangeListener` 的 shown/dismissed 时机。
- 差异：原厂进度放大到 `DEFAULT_SPRING_FACTOR=10000` 后积分，移植归一到 0..1（库内 `CouiSpring` 的 `response → ((2π/response)²)` 与原厂 `COUISpringForce` 同源）；原厂自行沿 View 树找父容器，Compose 由宿主用 `Box` 决定位置；`COUINotificationSnackBar` 的标题/副标题/关闭按钮与 X/Y 拖动回弹、`COUICustomSnackBar` 的 `setTouchSlidable` 与 180ms alpha 消失没有移植；原厂 `setEnabled()`、`setContentText("")` 等重启/取消计时的旁路没有对应公开入口。
- 结论：单实例、计时与弹簧进出场对齐；位置装配、通知样式与拖动消失非 1:1。

### 核查后的剩余工作

1. 四个使用页、组件目录与 `zensical.toml` 已登记；四个组件的源码入口、公开签名与差异写入各自使用页的「与原厂的差异」，不把静态核查写成真机验收。
2. 三个组件都没有新增绘制后端：`SnackbarHost` 复用轻提示的绘制节点与 `CouiSpring`，`FlowLayout` / `TabLayout` 只用公开布局与 Canvas；检查过 `ChipTab`（依赖背景源与 API 34）、`ToastHostState`（队列语义不同）与 `CouiScrollPhysics`（不是指示器动画分支），均不适合复用，理由见对应交付记录。
3. 待真机同背景/手势对照：指示器文字色过渡与拖动中宽度插值、标签圆角与按压反馈、Snackbar 弹簧观感与触摸暂停计时、RTL、深浅色与无障碍播报。本地不执行构建与设备操作。

## 双向核查：数值步进器

按「原厂 → 移植、移植 → 原厂」核对 `references/apk/jadx/settings/classes7/` 与 `references/apk/settings/decoded/res/`。结论仍为静态核对；参数一致不等于 1:1 验收。

- 一致：`ObservableStep` 的范围校验与夹紧（`maximum ≤ 9999`、`minimum ≥ -999`、`max ≥ min`，`setStep` 夹到区间）与「只在值变化时回调」的观察者语义；按钮可用性 `step < max` / `step > min` 且控件可用；32dp 按钮（`stepper_button_size`）、indicator 44dp 最小宽与 12dp 左右留白、18sp `sans-serif-medium`；内容色 `couiColorPrimaryNeutral`（日 `#E6000000` / 夜 `#E6FFFFFF`，与 `CouiColorScheme.onSurface` 同值）、禁用色 `couiColorDivider`（`#1F000000` / `#33FFFFFF`，与 `outlineVariant` 同值）、按钮底色 `couiColorContainer8`（`#14000000` / `#1AFFFFFF`）、焦点描边 `couiColorFocusOutline`（`#66006AFF` / `#661875F5`）；`LongPressProxy` 的单击一次、长按立即提交后每 100ms 重复、抬手/取消结束重复、重复期间离开控件不结束；每次提交前触发 `GRANULAR_SHORT_VIBRATE_SYNC=308`。
- 差异（保留）：指示器宽度按原厂 `split("")` 的取位数语义多算一位（原厂行为，非笔误修正）；按压形变复用 `CouiPressMotion`，原厂是 `COUIPressFeedbackHelper` 的 `FILL_BUTTON_PRESS_FEEDBACK` 弹簧，响应/阻尼未逐值核对；长按超时取 `viewConfiguration.longPressTimeoutMillis`（原厂 `GestureDetector` 默认超时），点击区域以控件边界近似 `GestureDetector` 的 tap region；焦点描边画在 32dp 之外，可见性取决于父容器是否裁剪。
- 命名：原厂 `COUIStepperView` 与项目原有 `Stepper`（横向步骤条）同名不同物，本轮把步骤条改为 `StepBar`，`Stepper` 归数值步进器。

## 逆向材料缺口

- `oplus-framework.jar` 未拉取：`pull.sh:105-106` 列有 `oplus-framework.jar` / `oplus-services.jar` / `oplus-service-jobscheduler.jar` / `boot-oplus-framework.vdex`，本轮 `adb devices` 无在线设备、`references/apk/` 下无任何 `.jar`，`OplusRenderEffect`、`OplusPathAdapter`、`ViewRootManager` 的 ABI 与内部算法只能从调用侧推断。**唯一仍需真机授权才能闭合的缺口。**
- 已反编译 dex 目录精确为 6 个：Settings `classes`(2171) / `classes7`(3115) = 2/14；SystemUI `classes3`(4015) / `classes4`(5067) / `classes9`(3602) = **3/9**（旧写 4/9 有误）；PhoneManager `classes`(5388) = 1/6。另持久化了 `phonemanager/classes2-single/COUICustomSnackBar.java`。
- `COUICustomSnackBar` 已反编译（旧记「只有类描述符」不成立），见[逆向分析 §9](../../references/apk/ANALYSIS.md)；类在 `phonemanager/decoded/classes2.dex`，JADX 未完全恢复，只作结构证据。
- QS `IDeformationAnimationController` / `CellCalculatorManager` 的约束数学已展开（旧记「未展开」不成立），见[逆向分析 §6](../../references/apk/ANALYSIS.md)。
- `EffectiveAnimationView` 的 Lottie 资源未逐个归类，规模口径见[逆向分析 §7](../../references/apk/ANALYSIS.md)。

详见[逆向分析报告](../../references/apk/ANALYSIS.md#9-未覆盖)。

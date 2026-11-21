# ColorOS 17 组件库规范与组件地图

本文定义在现有 `coui` 模块内补齐 ColorOS 17 能力时的目标版本、证据边界、职责边界，以及 PhoneManager 首页到当前公开组件的映射。本轮只交付规范与地图，不新增公开 API、不新增模块、不写转发包装层。

## 1. 基准与证据边界

### 1.1 目标版本

以 ColorOS V17.0.0 / Android 17（API 37）三包内嵌的 COUI 17.x 为唯一目标：

| 包 | versionName | 用途 |
| --- | --- | --- |
| Settings | 17.0.0 | 组件参数与调用链主参照 |
| SystemUI | 17.99.02 | 材质、融球与 QS 等系统效果参照 |
| PhoneManager | 17.6.8 | 本轮 UI 页面与控件参照 |

来源、哈希与提取方式见[逆向素材](../../references/apk/README.md)；代码侧结论见[逆向分析](../../references/apk/ANALYSIS.md)。三包交叉比对结论是：`Theme.COUI` 的 187 个属性零差异，847 个共有颜色仅 1 项不同，743 个共有尺寸仅 4 项不同且都在 SystemUI，夜间样式不覆盖 COUI 样式。因此只维护一套 COUI 17 token，不按应用分叉；`uiengine` 13.8.12 属于旧一代，不用于本目标的默认值。

本轮页面证据以仓库内 `references/apk/phonemanager/decoded/` 为准；本地 `.androdirea/decoded/PhoneManager-108135231` 是同源反编译副本，不作为文档引用路径。

### 1.2 证据分级

组件地图中的结论分三类，写法遵循[复刻指南](replication-guide.md)：

- **原厂依据**：布局、资源、反编译源码能直接确认的类、参数、状态或调用链。
- **公开平台适配**：用 Android/Compose/AndroidX 公开 API 承载同一行为，需写明与 OEM 实现的差别。
- **项目主动特化**：用户指定或仓库已批准的视觉/交互取舍，不传播成其他组件的默认值。

PhoneManager 的 6 个 dex 已全部反编译（`references/apk/jadx/phonemanager/`），自有控件源码在 `classes4.dex`。`ScanCircleView` 已按源码移植为[分数环](../components/scan-ring.md)：弧长、五阶角度渐变、扫描旋转与停止分支、70 分红线均有原厂依据，`main_circle_*.webp` 位图部分明确不在范围内。`ScanCircleWaveView`、`ScaleCardViewWithBackground`、`OptionalOpaqueAppBarLayout` 与 `ImmersiveBackgroundToolbarBehavior` 已读到源码分支但未移植，地图中标为**待源码核验**的条目只表示「有源码、未对照实现」。缺口口径见[覆盖对照](../status/coverage.md#逆向材料缺口)。

## 2. 库内规范

1. `coui` 只持有通用 COUI 组件、效果、主题、几何、输入、动效和固定预设，不引入业务状态、宿主主题缓存、厂商类、厂商 SO 或外部设计体系类型。页面、导航、查询、错误展示和业务回调归宿主，见[架构](architecture.md)。
2. 新增能力按[开发流程](workflow.md)先固定目标页面与实际消费者，再追参数、状态、退出路径和最终绘制；算法进入系统扩展时继续追下游。不能按 setter 名称、资源名或截图猜实现。
3. 新增组件前先查[效果目录](../effects/README.md)及现有消费者。模糊、材质、轮廓、光效、阴影、跟手和融合优先复用已有基础效果；组件只装配自己的布局、语义和局部状态，不复制绘制后端，也不增加只转发参数的包装层。
4. token 原厂导出在 `references/apk/*/tokens/`，只作为研究证据。运行期外观经 `CouiTheme` 或显式参数进入组件；完整参数、默认值和固定常量由源码、KDoc 与所属 Defaults 持有，本文不复制数值表。
5. 公开入口遵守[API 设计](api-design.md)与[代码规范](coding-style.md)：Modifier 为第一个参数且默认 `Modifier`，值/选择/展开目标受控，列表 key 稳定，CANCEL 与节点移除有收尾，不新增 `!!`。用户文案进入模块字符串资源，默认英文并同步简体中文。
6. 外观差异按「原厂依据 / 公开平台适配 / 项目主动特化」记录；影响后续判断的取舍写 Note，已确认偏差写[覆盖对照](../status/coverage.md)，发布变化写 CHANGELOG。本文只维护稳定的规范入口与地图，不记改动历史。

## 3. 组件地图

状态图例：**已有（近似）**＝有公开入口且已有静态对照，非 1:1；**可组合**＝用现有公开件按原页语义装配，需宿主给尺寸/状态；**未实现**＝库内无对应实现；**宿主负责**＝页面/数据/导航职责，不入库；**待源码核验**＝只有布局层证据。

| PhoneManager 页面 / 布局 | 原厂视图或组件 | `coui` 对应入口 | 状态 | 证据与边界 |
| --- | --- | --- | --- | --- |
| 首页根容器 `main_activity_root_view_pager.xml` | `ViewPager2` + `COUIBottomFloatingNavigationBar`，`main_navigation_bottom_container.xml` 另有 `COUINavigationView` | [DockBar](../components/dock-bar.md)、[NavigationBar](../components/navigation-bar.md)、[BottomFloatingNavigationBar](../components/navigation-layout.md) | 已有（近似） | 布局 `:2-8`、底栏布局 `:2-6`；融球材质、溢出菜单与编辑态附件栏未移植，见[覆盖对照](../status/coverage.md#有偏差) |
| 首页主体 `layout_main.xml` | `COUIPercentWidthFrameLayout` / `COUIPercentWidthRecyclerView`、`COUIButton`、`COUIHintRedDot`；自定义 `ScanCircleView` / `ScanCircleWaveView` / `MarqueeText` | [Card](../components/card.md)、[Group](../components/group.md)、[QuickActionGrid](../components/quick-action-grid.md)、[Button](../components/button.md)、[Badge](../components/badge-tooltip.md)、[MarqueeText](../components/marquee-text.md)、[ScanRing](../components/scan-ring.md) | 已有（近似）+ 可组合 | 布局 `:4-43`；扫描环已按源码移植，扩散波纹依赖 webp 位图未移植 |
| 工具项 `item_tool_kit.xml` / `item_tool.xml` | `ScaleCardViewWithBackground` 60dp 高、20dp 圆角；`COUIHintRedDot`；14sp `?couiColorLabelPrimary` | [Item](../components/item.md)、[Card](../components/card.md)、[Badge](../components/badge-tooltip.md)、[StateEffectSurface](../components/state-effect.md) | 可组合 | 布局 `:2-9`；缩放与按压反馈未核验，60dp/20dp 由宿主显式设置，不写成库默认 |
| 工具页 `toolkit_page_layout.xml` | `ViewPagerRecyclerView` + `COUIToolbar` + 沉浸顶栏 `include` | [TopAppBar](../components/top-app-bar.md)、[Scaffold](../components/scaffold.md)、[CouiGradientBackdrop](../effects/blur-material.md) | 可组合 + 宿主负责 | 布局 `:2-6`；列表与滚动由宿主组合，顶栏已有，沉浸行为未整体移植 |
| 安全首页 `activity_security_home.xml` | 业务渐变背景 + `COUIPercentWidthFrameLayout` + `COUIToolbar` | [TopAppBar](../components/top-app-bar.md)、[Card](../components/card.md)、[Group](../components/group.md) | 可组合 + 宿主负责 | 布局 `:2-7`；黄色渐变属业务资源，不进核心 |
| 顶栏 `main_appbar_with_immersive_divider_layout.xml` | `OptionalOpaqueAppBarLayout`、`COUIToolbar`、`ImmersiveBackgroundToolbarBehavior` | [TopAppBarSurface](../components/top-app-bar.md)、[CouiGradientBackdrop](../effects/blur-material.md) | 已有（近似）+ 待源码核验 | 布局 `:2-6`；自定义 behavior 未移植，低平台顶栏模糊差异见[覆盖对照](../status/coverage.md#有偏差) |
| 列表页 `activity_allow_list.xml` 等 | `COUINestedScrollView`、`COUIPercentWidthRecyclerView`、`COUIEmptyStateView`、`BottomButtonLayout`、`COUIButton` | [Group](../components/group.md)、[Item](../components/item.md)、[EmptyState](../components/empty-state.md)、[ButtonBar](../components/button-bar.md)、[Button](../components/button.md) | 已有（近似） | 原页布局在 `references/apk/phonemanager/decoded/res/layout/`；选择、加载与提交由宿主持有 |
| 底部/居中弹窗、引导与安全弹窗 | `COUIAlertDialogBuilder`、`COUIGuideDialogBuilder`、`COUISecurityAlertDialogBuilder` 及 `coui_*dialog*` 布局 | [Dialog](../components/dialog.md) 及同页 `GuideDialog` / `SecurityDialog` / `ListDialog` | 已有（近似） | 分页手势、旋转加载弹窗等差异见[覆盖对照](../status/coverage.md#有偏差) |
| 反馈、空态、加载 | `COUISnackBar`、`COUITopTips`、`COUIEmptyStateView`、Lottie 加载资源 | [Snackbar](../components/snackbar.md)、[TopTips](../components/top-tips.md)、[EmptyState](../components/empty-state.md)、[Progress](../components/progress.md) | 已有（近似） | 通知样式、默认内容布局与 Lottie 私有链未移植，见各自使用页 |
| 下拉刷新、折叠设置、状态切换 | `COUISpringChain` / `COUIFoldable*` 类族与 Preference 布局 | [PullToRefresh](../components/pull-to-refresh.md)、[Accordion](../components/accordion.md)、[StateLayout](../components/state-layout.md)、[Item](../components/item.md) | 可组合 | Preference 80 个顶层类不逐类 1:1，按 `Item` + 控件组合替代 |

地图只说明「哪个原厂页面对应哪个公开入口」。某个类或资源在 APK 内出现，不等于目标页面实际使用它；新增组件前仍按[复刻指南](replication-guide.md#2-从系统页面到实际消费者)确认真实消费者、主题覆盖和运行分支。

## 4. 后续落地顺序

本轮不实现。下一轮按以下顺序推进，任一步都需要先更新本表的状态与证据：

1. 已核验 `ScanCircleView` 并移植为 `ScanRing`；`ScanCircleWaveView`、`ScaleCardViewWithBackground`、`OptionalOpaqueAppBarLayout` 与 `ImmersiveBackgroundToolbarBehavior` 已读到源码分支但未移植。下一步判断扩散波纹与 60dp 工具卡是「宿主组合现有件」还是新增通用组件，位图依赖需先给出矢量化方案。
2. 依据核验结果判断扫描圆环与 60dp 工具卡是「新增通用组件」还是「宿主组合现有件」。只有确认无法复用已有效果/状态组件时才新增实现，并在 Note 说明检查过的候选。
3. 对顶栏沉浸行为和底栏材质做原厂 → 移植、移植 → 原厂双向核查；涉及观感的改动先完成真机同背景对照，再改[覆盖对照](../status/coverage.md)与对应使用页。
4. 若新增或修改公开入口，按[文档维护](documentation.md)同步组件页、目录、Demo 与导航；仅调整内部参数则留在源码/Defaults，不动使用文档与 CHANGELOG。

## 5. 维护约定

- 本文是开发规范与地图，唯一维护位置在 `docs/development/`；原则性约束回[架构](architecture.md)、[代码规范](coding-style.md)和[文档维护](documentation.md)，具体参数回源码，不在此复制。
- 三包 token 导出、布局与反编译源码只作为证据；不要在 `coui` 运行时读取 `references/apk/`，也不要把本机路径写入依赖。
- 状态更新必须有可复核入口。静态对照只能把能力标为「近似」或「待验收」，不能写成 1:1。

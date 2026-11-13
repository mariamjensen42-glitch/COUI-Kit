# 组件目录

需要在布局中直接使用控件时，从这里选择。可以只使用一个组件，也可以和其他设计体系混搭；不要求替换宿主 Scaffold、主题或导航。首次接入见[快速开始](../guide/getting-started.md)，页面装配见[宿主页面接入](../guide/secondary-pages.md)。

## 页面与导航

| 组件 | 用途 |
| --- | --- |
| [页面容器](scaffold.md) | 可选的 `Scaffold`，统一顶栏、底栏、正文 Padding 与安全区 |
| [顶栏与按钮](top-app-bar.md) | `TopAppBar` 标题与操作、`TopAppBarSurface` 自定义背景容器 |
| [顶栏搜索](search.md) | 搜索按钮与输入框之间的转场 |
| [常驻搜索](search-bar.md) | 搜索胶囊与独立取消按钮 |
| [折叠顶栏](collapsing-app-bar.md) | `LargeTopAppBar` 大标题折叠、`CollapsingTopAppBar` 常驻搜索联动 |
| [底栏](navigation-bar.md) | 页面选择、左右附件和融合表面 |
| [悬浮底栏](dock-bar.md) | 胶囊导航与中央凸起圆钮的融球底栏 |
| [FAB](fab.md) | 独立浮动操作 |

## 列表与内容

| 组件 | 用途 |
| --- | --- |
| [Group](group.md) | 分组卡片、标题操作与说明 |
| [快捷动作网格](quick-action-grid.md) | 两列图标入口卡片与等宽排布 |
| [操作卡](action-card.md) | 图标、标题与说明组成的提示卡与推荐卡 |
| [通知卡](notification-card.md) | 单条通知的图标、标题、相对时间与正文 |
| [控制中心磁贴](quick-setting.md) | 快捷开关磁贴与竖排档位条 |
| [模式选择卡](choice-card.md) | 等宽模式卡与强调色选中描边 |
| [缩略图选择卡](thumbnail-choice.md) | 缩略图单选卡与三列缩略图导航卡 |
| [柱状图](bar-chart.md) | 圆角柱形、逐柱配色与右侧刻度 |
| [折叠面板](accordion.md) | 卡片内按需展开的设置分组 |
| [头像](avatar.md) | 固定尺寸的可裁剪头像容器 |
| [步骤条](stepper.md) | 横向步骤圆点、连线与标签 |
| [三态容器](state-layout.md) | 加载、空、失败与就绪的统一切换 |
| [下拉刷新](pull-to-refresh.md) | 列表到顶下拉触发刷新与进度指示 |
| [Item](item.md) | 行内容与点击、选择装配 |
| [Divider](divider.md) | 组内分隔线 |
| [Footer](footer.md) | 分组辅助说明 |
| [系统功能图标](icons.md) | 有实际调用来源的矢量子集 |

## 输入与选择

| 组件 | 用途 |
| --- | --- |
| [滑动选择条](slider.md) | 连续、分段与隐藏点位的步进选择 |
| [分段选择器](segmented-control.md) | 胶囊内的单选滑块、不等宽标签与拖动回弹 |
| [单/复选框](checkbox.md) | 单选、复选状态与切换动画 |
| [开关](switch.md) | 二值切换、拖动吸附与加载状态 |
| [ChipTab](chip-tab.md) | 单选分类、横向滚动与独立图标动作 |
| [选择器](pickers.md) | 受控整数滚轮与月历；时间、日期和弹窗由宿主组合 |

## 弹窗与菜单

| 组件 | 用途 |
| --- | --- |
| [菜单](menu.md) | 选择、勾选、分组与子菜单 |
| [弹窗](dialog.md) | 底部/居中窗口、受控操作、输入与加载预设 |

## 反馈与操作

| 组件 | 用途 |
| --- | --- |
| [按钮](button.md) | 原厂 COUIButton 的主、次操作与文字外观，含加载与禁用 |
| [悬浮操作栏](floating-toolbar.md) | 单条玻璃胶囊承载多个同级操作 |
| [进度与占位](progress.md) | 环形与线性进度、分段占用、不确定态旋转与骨架占位 |
| [轻提示](toast.md) | 短暂结果反馈、自动消失与堆叠 |
| [角标与气泡](badge-tooltip.md) | 未读角标、红点与受控气泡说明 |
| [页码指示器](page-indicator.md) | 跟随当前页的圆点指示器 |
| [空状态](empty-state.md) | 图标、标题、说明与操作的居中布局 |
| [输入框](text-field.md) | 圆角描边容器、可选浮动标签与错误态的受控输入 |
| [底部面板](bottom-sheet.md) | 从底部升起的受控面板，含遮罩与内部滚动 |

浮动材质需要的公共准备见[背景采样](../effects/backdrop.md)；单独使用算法见[效果目录](../effects/README.md)。

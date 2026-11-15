# 🧭导航栏变体

四个组件覆盖宽窗口与平板的导航形态：`SideNavigationBar` 是可停靠的侧边导航容器，`NavigationRail` 是竖向导航轨，`BottomFloatingNavigationBar` 是带文案的悬浮底栏，`AccessoryNavigationBar` 是可展开的圆形附件组。

需要手机上的玻璃底栏与左右附件时用[底栏](navigation-bar.md)，需要中央凸起圆钮时用[悬浮底栏](dock-bar.md)，需要在内容上滑出侧栏时用[侧栏](side-pane.md)。选中项与展开状态都由宿主持有，组件只负责装配与回调。

## 侧边导航

`SideNavigationBar` 自己排布面板与正文：可用宽度不小于 600dp 时面板停靠、正文占剩余空间；更窄时面板变成带遮罩的抽屉，点击遮罩回调 `onExpandedChange(false)`。

~~~kotlin
SideNavigationBar(
    items = listOf(
        CouiSideNavigationItem("首页", CouiIcons.Filled.Home),
        CouiSideNavigationItem("文件", CouiIcons.Filled.Folder, badge = 3),
    ),
    selectedIndex = selected,
    onSelection = onSelect,
    expanded = expanded,
    onExpandedChange = { expanded = it },
    content = { PageContent() },
)
~~~

- `docked`：为 null 时按自身可用宽度判定，也可显式固定停靠或抽屉。
- `expanded`：只影响抽屉形态；停靠形态始终显示面板。
- `header` / `footer`：排在导航项上方与下方的插槽，随面板一起滚动之外固定。
- 项高 48dp、圆角 8dp、项间距 4dp、图标 24dp；抽屉宽 300dp，停靠宽 `min(280dp, 可用宽度 * 0.382)`。

抽屉形态不接管返回键与侧滑手势，关闭请求只经 `onExpandedChange`。

## 导航轨

`NavigationRail` 用于平板与横屏窗口：菜单块按原厂规则整体居中，图标在上、标签在下，标签最多两行。

~~~kotlin
NavigationRail(
    items = items,
    selectedIndex = selected,
    onSelection = onSelect,
)
~~~

项与项之间的间距由 `max((容器高度 * 0.4 - 全部项高度) / (项数 + 1), 8dp)` 反算：项少时间距被撑开，项多时收窄到 8dp。菜单宽 64dp、两侧各留 4dp，右侧画一条 1 物理像素的分隔线。

`header` / `footer` 固定在菜单块上下，不参与居中。

## 悬浮底栏

`BottomFloatingNavigationBar` 在胶囊容器里排布图标加文案的导航项，两端可用 `leading`、`trailing` 放圆形附件，附件外框用 `BottomFloatingAccessory`。

~~~kotlin
BottomFloatingNavigationBar(
    items = items,
    selectedIndex = selected,
    onSelection = onSelect,
    leading = {
        BottomFloatingAccessory { Icon(CouiIcons.Filled.Menu, null, Modifier.size(24.dp)) }
    },
)
~~~

栏高 56dp、项高 48dp、项最大宽 70dp、项内边距 12dp、项间距 8dp。项数超出可用宽度时整条内容横向滚动，不折叠成溢出菜单。

## 可展开附件

`AccessoryNavigationBar` 把圆形附件按原厂低端档弹簧逐个推出：透明度用 0.2 响应，缩放与位移用 0.5 响应配 0.3 回弹，最小缩放 0.5。

~~~kotlin
AccessoryNavigationBar(
    actions = listOf(
        CouiAccessoryAction(CouiIcons.Filled.AI, "助手") { onAssistant() },
        CouiAccessoryAction(CouiIcons.Filled.Settings, "设置", onClick = { onSettings() }, enabled = false),
    ),
    expanded = expanded,
)
~~~

收起时只保留 `collapsedVisibleCount` 个按钮（默认 1 个），展开按钮本身也由宿主提供，组件不内建开关。

## 与原厂的差异

- `COUISideNavigationBar` 基于 `DrawerLayout`，自带边缘拖动、侧滑与返回键拦截；本实现只做停靠与淡入淡出的遮罩抽屉，拖动、返回键与预测性返回交给宿主，编辑态遮罩渐变（`setIsInEditState`）没有复刻。
- 侧边导航的面板宽度、停靠判定和项间距按原厂参数移植；原厂面板内的项样式由宿主自定义布局提供，本实现给出固定的项高、圆角与图标文字排布。
- 导航轨的项间距公式、菜单宽度、图标与文字尺寸同原厂；原厂的红点（`COUIHintRedDot`）、长按禁用与大图标档位没有移植，导航项类型复用 `CouiNavigationItem`。
- 悬浮底栏按原厂尺寸排布，但原厂的融球材质、菜单模糊、`melt ball` 展开视图、溢出菜单与编辑态附件栏没有移植；材质走纯色容器加阴影，与[底栏](navigation-bar.md)的玻璃表面是两套实现。
- 附件栏的弹簧参数取自原厂 `LOW_END_*` 常量，回弹 0.3 按 `dampingRatio = 1 - 0.3` 近似，不区分高低端设备的动效档位。

## Demo 中的样式与示例

Demo 的“组件 → Navigation variants”页面包含侧边导航的停靠与抽屉切换、固定高度的导航轨、带左右附件的悬浮底栏，以及可展开的附件栏。

Demo 源码见 [DemoNavigationFamilySamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoNavigationFamilySamples.kt)。公开参数见 [SideNavigationBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/navigation/SideNavigationBar.kt)、[NavigationRail](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/navigation/NavigationRail.kt)、[BottomFloatingNavigationBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/navigation/BottomFloatingNavigationBar.kt) 与 [AccessoryNavigationBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/navigation/AccessoryNavigationBar.kt)。

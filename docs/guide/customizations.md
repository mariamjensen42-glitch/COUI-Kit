# 与原厂的定制差异

COUI-Kit 以 ColorOS 17 组件为参考，并保留以下视觉与交互微调。本文说明当前定制效果及其作用范围；具体参数由组件实现维护。这里的“原厂”指各组件选定的参考版本，不代表所有系统版本和应用。

组件调用见[组件目录](../components/README.md)，主题配置见[外观与预设](appearance.md)，平台限制见[兼容要求](compatibility.md)。

## 顶栏与搜索

### 搜索展开按钮的融球效果

`TopAppBarSearch` 提供外侧 `action` 时，按钮通过融球效果从搜索胶囊中分离；收起时沿同一路径重新融合。按钮支持文字、图标或自定义内容。

这项定制作用于搜索胶囊与外侧按钮的组合，不是输入框内部 `trailingIcon` 的显示动画。调用方式见[顶栏搜索](../components/search.md)，绘制入口见 [TopAppBarSearchPair](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/topappbar/TopAppBarSearchPair.kt)。

### 二级页大标题与弧线收起

Demo 二级页统一使用 `LargeTopAppBar`。参考的原厂设置二级页使用紧凑顶栏；这里的大标题装配与收起轨迹属于项目定制。

`LargeTopAppBar` 的展开标题略向右收进。展开和收起字号都跟随系统字体大小，调用方可分别覆盖两端字号。单位与示例见[普通大标题](../components/collapsing-app-bar.md#普通大标题)。有返回按钮时，展开标题位于按钮下方，折叠时沿连续弧线向右上方移动到按钮旁，横移略领先于上移。展开沿原路径返回，RTL 布局镜像移动方向。返回按钮和操作区保持在标题上层，允许转场中轻微遮挡标题。标题宽度保留操作区。

展开字号参考 ColorOS 设置首页的 `SettingsCollapsingTitleExpanded`。导航槽占用空间时，默认收起字号与普通顶栏一致。默认折叠过程保留同一字体和字重，避免到达端点时笔画突变。入口见[大标题顶栏](../components/collapsing-app-bar.md)与 [TopAppBar](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/topappbar/TopAppBar.kt)。

### 顶栏模糊强度与渐变

顶栏模糊强度采用项目调校值，配合浅色白色、深色黑色的背景遮罩。遮罩以原厂透明度节点为基准，平滑浅色中间节点的变化速度，并让深浅色遮罩在底端平缓淡出，减轻分段过渡的接痕。通用顶栏背景预设使用固定逻辑高度的渐隐区和 smoothstep 曲线，渐隐区锚定底部，随顶栏高度变化移动。

`TopAppBar` 与默认 `TopAppBarSurface` 使用通用预设；`LargeTopAppBar`、`CollapsingTopAppBar` 保留各自的模糊半径和渐变配置。不能将通用预设的数值直接套到所有顶栏。显式提供 `gradientSpeed` 时，使用对应的幂函数曲线。

参数入口见 [CouiTopAppBarDefaults](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/topappbar/CouiTopAppBarDefaults.kt)与 [TopAppBarSurface](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/topappbar/TopAppBarSurface.kt)，渐变取舍见[顶栏渐进模糊](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/bug-fix/2026-10-18-coui-topbar-gradient-blur.md)。

### 按钮材质与搜索避让

顶栏按钮沿用原厂光效几何，略增强底色权重以改善可读性。普通按钮的内光晕和普通投影适当减弱；搜索表面不使用这两项普通按钮的减弱系数。

搜索收起节奏为常驻操作区作了调整：搜索表面加快收回，为常驻按钮让出位置，按钮按自己的恢复节奏返回。常驻操作由同一搜索状态协调移动。

材质入口见 [CouiTopAppBarButtonDefaults](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/topappbar/CouiTopAppBarButtonDefaults.kt)，转场取舍见[搜索转场与焦点边界](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/bug-fix/2026-10-20-topappbar-search-transition-boundaries.md)。

## 菜单

### 二级菜单收起接合

小屏二级菜单收起的尾段，背景轮廓附近的窄带逐渐淡出。中央底色保持不透明，并逐渐接合主菜单同一位置的材质，减轻面板移除时边框和整块底色的突变。

文字和图标不随这段背景过渡淡出。完全展开时仍绘制完整子菜单材质。这项处理用于二级菜单返回主菜单，不改变整组菜单关闭的退场方式。

实现见 [CouiMenuSubMenu](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/menu/CouiMenuSubMenu.kt)与 [CouiMenuMaterial](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/menu/CouiMenuMaterial.kt)，合成约束见[菜单输入与合成](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/bug-fix/2026-10-15-coui-submenu-input-dispatch.md)。

### 轮廓光组合

菜单使用通知风格的背景感知描边与菜单内光晕组合，并单独调整描边强度。浅色轮廓中过亮的颜色按比例收回，保留颜色关系，减少直接截断为白色造成的生硬边缘。

这是一套菜单专用的组合光效，不等同于原厂菜单的单一预设。入口见 [CouiMenuEdge](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/menu/CouiMenuEdge.kt)与 [CouiEdge](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiEdge.kt)。

### 文字排版

菜单项和头部标签按 Metric 的菜单排版调整，分别采用 `labelLarge` 与 `bodySmall` 的角色基准。原生主题的菜单排版使用同一基准。

这项定制只针对菜单，不将 Dialog 等组件的文字同步替换。调用见[菜单](../components/menu.md)，角色依据见[菜单排版依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/architecture/2026-10-31-optional-material3-style.md)。

## 其他组件

| 组件 | 定制行为 | 作用范围与依据 |
| --- | --- | --- |
| 菜单、底栏与搜索栏 | 可叠加宿主页面色相，保留材质亮度。 | 仅在宿主提供 tint 或启用相应主题策略时生效；原生默认不加额外染色。见[外观与预设](appearance.md#菜单底栏悬浮操作栏搜索栏与-fab-的染色)。 |
| `ChipTab` | 投影复用库内顶栏胶囊按钮预设，并随选中进度变化，减轻浅色背景上的底部亮带。 | 这是投影预设的选择，不表示分类间距、滚动或边缘渐隐属于主动视觉定制。见 [CouiChipTabDefaults](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/chip/CouiChipTabDefaults.kt)与[装配依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-10-24-chip-tab.md)。 |
| [分段选择器](../components/segmented-control.md) | 亮色透明模式下，胶囊外轮廓沿用本库菜单的颜色处理。过亮的描边按比例压低，内晕按透明度与模糊底色混合，减轻边缘泛白。 | 混色属于本库定制，轮廓形状沿用原厂分段选择器的胶囊预设。见[分段选择器依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-07-coui-segmented-control.md)。 |
| `Slider` | 默认轨道采用 G2 平滑胶囊轮廓，背景、进度与光效使用同一 Shape；拇指保持圆形。 | 宿主可覆盖 Shape；平滑轨道不称为原厂默认。见 [Slider](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/slider/Slider.kt)与[滑条依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-10-21-coui-slider.md)。 |
| `Switch` | 轨道和滑块采用平滑胶囊轮廓，滑块拉伸时保持平滑几何。 | 参考实现的滑块使用普通 `drawRoundRect`。见[开关依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-10-27-coui-switch.md)。 |
| `Switch` | 触摸取消时不提交候选值，恢复宿主提供的 `checked`。 | 参考实现的 `CompoundButton` 在 CANCEL 后可能吸附并改变值；本库采用受控状态约定。见[开关依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-10-27-coui-switch.md)。 |
| `CalendarPicker` | 新选中日期的数字与选中圆同步放大，文字不额外淡入淡出。 | Calendar 17.3.70 的参考绘制仅改变圆半径和透明度，数字字号固定。见[月历依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-01-coui-pickers.md)。 |
| `NavigationBar` | 透明附件使用减弱后的投影强度。 | 只针对底栏透明附件，不扩展到独立 FAB。见 [NavigationBar](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/navigation/NavigationBar.kt)与[投影依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/bug-fix/2026-10-18-coui-native-shadow.md)。 |
| [按钮](../components/button.md) | 主、次外观的几何、排版、配色与反馈链沿用原厂 `Widget.COUI.Button.Large`；文字外观由本库新增，加载态按原厂 `coui_loading_btn_circle_*` 的三圆点实现。 | 原厂 COUIButton 只有主、次两个分支，也没有为三圆点提供独立动画资源，脉冲节奏沿用同一套加载字形的 800ms。见[按钮依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-12-button-progress-toast-badge.md)。 |
| [进度与占位](../components/progress.md) | 环形尺寸、描边与配色沿用 `Widget.COUI.COUICircularProgressBar`；不确定态的 800ms 周期与 270° 弧长按同一套加载字形 `coui_icon_loading` 推演。 | 原厂环形进度只有确定态，不确定态分支是按加载字形推演的。见[进度依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-12-button-progress-toast-badge.md)。 |
| [轻提示](../components/toast.md) | 几何与配色沿用 `coui_snack_bar_*`（12.67dp 标准圆角、16/10dp 留白、14dp 文字），并绘制九宫格烘焙的内阴影；带图标时上下留白从原厂 16dp 收到 10dp，避免单行容器撑到 62dp 后出现大片空白；投影按九宫格实测的外扩与峰值推演为 26dp 跨度 + 5% 黑色。 | COUI 没有独立 Toast，短暂反馈由 SnackBar 承担；原厂阴影烘焙在九宫格图片内，没有 elevation 资源。见[轻提示依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-12-button-progress-toast-badge.md)。 |
| [角标与气泡](../components/badge-tooltip.md) | 角标与气泡的尺寸、留白、圆角沿用 `COUIHintRedDot` 与 `COUIToolTips`；`BadgedBox` 的叠放位置与气泡定位、夹紧规则由本库定义。 | 原厂没有对应的叠放容器与受控气泡协议。见[角标依据](https://github.com/ItosEO/COUI-Kit/blob/main/.agents/notes/implemented/feature/2026-11-12-button-progress-toast-badge.md)。 |

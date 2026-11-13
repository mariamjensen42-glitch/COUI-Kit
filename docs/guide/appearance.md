# 外观与预设

组件可省略有默认值的外观参数，也可显式传入颜色、Style、Shape、材质和阴影。不配置主题时使用原生 COUI 预设；需要接入宿主配色时，用 `CouiTheme` 建立局部外观。

## 不配置主题

在 Compose 中直接使用原生默认值，`checked` 和回调由宿主持有：

~~~kotlin
Switch(checked = checked, onCheckedChange = onCheckedChange)
~~~

底层 Effect 不要求主题。原生主题使用中性表面、文字与 Theme.COUI 的蓝色强调色，不读取动态色。

## 局部原生或自定义外观

`CouiTheme` 建立局部原生或自定义外观，退出后恢复外层策略，并同时提供自身模式。

~~~kotlin
val colors = (if (dark) CouiPresets.Dark else CouiPresets.Light)
    .copy(accent = Color(0xFF0080FF))
CouiTheme(darkTheme = dark, colorScheme = colors) {
    content()
}
~~~

显式组件配置始终优先。存在局部 `CouiTheme` 时，模式匹配则使用它；不匹配则直接使用该模式的 native 预设与透明 tint，不再尝试外层策略。没有局部 `CouiTheme` 时，模式匹配则使用已选的默认样式，不匹配则回退到对应 native 预设与透明 tint。不会套用另一模式的色板。完整显式 Style、颜色、Shape、材质和阴影由调用方负责匹配。

`CouiPresets` 提供 `Light` / `Dark` 两组蓝色强调色预设，以及 `GreenLight` / `GreenDark` 两组绿色强调色预设。绿色预设只替换强调色与强调内容色，中性表面、卡片和页面底色沿用对应深浅模式的原生值，`CouiPresets.green(dark)` 按模式直接取值：

~~~kotlin
CouiTheme(darkTheme = dark, colorScheme = CouiPresets.green(dark)) {
    content()
}
~~~

绿色是页面主色，不表示原厂存在名为 Green 的主题；需要其他强调色时仍按上面 `copy(accent = …)` 的方式自定义。

`CouiTypography` 管普通内容、标签、菜单项及可选字体族。Shape 仍由各组件 Defaults 提供。

`CouiColorScheme.background` 是页面底色，`surface` 是组件表面，`container` 是通用容器底色，`card` 是列表分组卡片色。原生深色卡片使用 `couiColorCardBackground` 的半透明白色，叠加页面背景；菜单等顶层容器使用独立颜色。自定义色板省略 `background` 时沿用 `surface`，省略 `card` 时沿用 `container`。`CouiPageDefaults.themeColors()` 分别读取 `background` 和 `card`，宿主将页面底色传给自己的页面容器。`CouiPageDefaults.colors(accent, dark)` 从对应的原生浅色或深色预设生成 `CouiPageColors`，再应用显式强调色；它不会读取或创建主题作用域。

`com.itos.coui.text.Text` 提供固定 COUI 排版角色，`kind` 必填。文字颜色依次取显式 `color`、已提供的 `LocalCouiContentColor`、当前 `CouiTheme.colorScheme.onSurface`。省略颜色即可在独立布局中跟随主题，详见[Item](../components/item.md#配色与文字)。菜单项则读取 `CouiTheme.typography.menuItem` 的字号、字重与字体族。

`LocalCouiContentColor` 的缺省值为 `Color.Unspecified`，表示未提供内容色。直接读取该 Local 的宿主应在未指定时取当前主题颜色；COUI 的文字和图标已经在消费处解析。显式颜色优先，包括透明色；图标显式传入 `tint = Color.Unspecified` 时保留不染色语义。

## 菜单、底栏、悬浮操作栏、搜索栏与 FAB 的染色

原生默认 `materialTint` 透明。宿主提供 `materialTint` 时，用它为菜单、底栏、悬浮操作栏和 API 34 及以上的常驻搜索栏染色，保留材质亮度。关闭染色后，背景采样仍会包含页面颜色。

关闭染色时，菜单使用 `CouiMenuDefaults.style().copy(themeTint = Color.Transparent)`，底栏显式传 `themeTint = Color.Transparent`。自定义局部 `CouiTheme` 也可提供自己的 `materialTint`；透明表示关闭。显式材质始终优先。

默认 FAB 材质取强调色，阴影取最终 `material.firstColor`，不叠加页面 tint。FAB 在日间和夜间的默认内容色均为不透明白色，可通过 `contentColor` 覆盖；传入 `Color.Unspecified` 时继承外部内容色。子图标的显式 tint 始终优先。配色预设见 [FAB](../components/fab.md)。底栏右附件图标固定为白色。

## 内容插槽与最小交互尺寸

顶栏按钮、搜索表面、Chip 和 FAB 的内容插槽提供 `LocalCouiContentColor` 与 `LocalCouiTextStyle`，插槽中的内容可直接继承组件样式。组件部分 TextStyle 与已有基线合并；`CouiTheme` 的完整基线替换外层样式。子内容的显式颜色与样式、Icon tint 和内层 Local 优先。

仅转发的顶栏 title、buttonGroup、Chip leadingAction 和 Dialog 自定义正文不会被强设新样式。NavigationBar 自有文字继承宿主 ambient 字体属性，同时保留自己的字号、行高与字重。`com.itos.coui.theme` 包中的 `CouiText`、`CouiIcon` 和 `ProvideCouiTextStyle` 是内部实现；普通文字组件使用公开的 `com.itos.coui.text.Text`。

顶栏按钮使用组件自己的最小布局尺寸，搜索按钮使用固定容器。宿主内容自带的最小交互尺寸语义不会被组件改写。

独立复选框的外层点击容器应隐藏涟漪，并保留点击和无障碍语义，装配方式见[只放一个复选框](../components/checkbox.md#只放一个复选框)。

## Shape 与效果开关

[CouiShape](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/shape/CouiShape.kt)和[CouiCapsuleShape](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/shape/CouiCapsuleShape.kt)可被宿主 Shape 替换。材质距离场、内容 Path 和投影 Outline 有各自的几何要求，不能因圆角同名就互换。

顶栏使用 `CouiToolbarStyle`，菜单使用 `CouiMenuStyle`。菜单默认工厂使用当前主题的 `dark`；自定义 Style 应显式传入模式，省略时按 surface 亮度判断。

轮廓与内晕通过 `LocalCouiContourGlowEnabled` 控制。显式构造 Style 时，应与对应阴影工厂使用同一值：

~~~kotlin
CompositionLocalProvider(LocalCouiContourGlowEnabled provides contourEnabled) {
    content()
}
~~~

普通阴影、模糊、材质和跟手光仍由各自入口控制。固定预设直接引用所属 Defaults，不在宿主复制算法参数。

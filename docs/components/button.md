# 🔵按钮

`Button` 用于提交一个操作。几何、排版、配色和反馈链按 ColorOS 17 手机管家内嵌的 `Widget.COUI.Button.Large` 实现，提供主操作、次操作和文字三种外观。

## 最小调用

点击后由页面处理业务：

~~~kotlin
Button(
    onClick = onConfirm,
    label = confirmLabel,
)
~~~

省略外观参数时使用次操作外观（中性容器），深浅色跟随 `CouiTheme`；未设置主题时使用 COUI 预设。通用规则见[外观与预设](../guide/appearance.md)。

## 三种外观

~~~kotlin
Button(onClick = onPrimary, label = primaryLabel, style = CouiButtonStyle.Primary)
Button(onClick = onSecondary, label = secondaryLabel, style = CouiButtonStyle.Secondary)
Button(onClick = onText, label = textLabel, style = CouiButtonStyle.Text)
~~~

`Primary` 与 `Secondary` 取自原厂 COUIButton：主操作使用强调色填充，内容色取 `CouiColorScheme.onAccent`（原生蓝色主题下为白色），禁用容器为强调色的 77/255、内容为 0x28 白；次操作容器在浅色为 `0xFFEBEBEB`、深色为 `0xFF353535`，内容为 0xE6 的前景色，禁用内容为 0x42 / 0x28。

主操作内容色跟随色板而不是固定白色，是为了让亮色强调（例如 `CouiPresets.GreenDark` 的绿色）保持足够对比度。原厂资源只覆盖蓝色强调色，因此白色仍是默认结果；显式传入 `colors` 时以传入值为准。

`Text` 没有容器，内容取强调色。原厂 COUIButton 只有主、次两个分支，文字外观按同一套按钮排版去掉容器推演，用于行内低优先级操作。

配色仍可显式覆盖：

~~~kotlin
Button(onClick = onClick, label = label,
    colors = CouiButtonDefaults.primaryColors(CouiTheme.colorScheme.accent))
~~~

## 两行文案

需要一条主文案加一行说明时传入 `description`（对应原厂 `DescButtonTextSpan`）：

~~~kotlin
Button(
    onClick = onSync,
    label = syncLabel,
    description = batteryHint,
    style = CouiButtonStyle.Primary,
)
~~~

主文案 16sp、描述 10sp（`coui_btn_desc_text_size` / `coui_btn_desc_sub_text_size`），两者都是 `sans-serif-medium`，行距 0dp（`coui_btn_desc_top_margin`），各自单行居中、超出以省略号结束，按钮宽度取两者较大者。字号沿用 `COUIChangeTextUtil.getSuitableFontSize(…, 2)` 的 1.15 倍封顶，系统字体放大不会把两行文案顶出按钮。

与原厂的差异：原厂在 `ReplacementSpan.draw` 里用 `canvas.drawText` 组合两组 `FontMetricsInt` 基线，因此可以和同一段文字内联拼接；这里由 Compose 文本排版承担，只能作为按钮内容整体使用。

## 按钮组

一到两个同级操作使用 `ButtonGroup`，对应 `SimpleButtonGroupCtrl` 加 `COUIButtonLayout`：

~~~kotlin
ButtonGroup(
    items = listOf(
        CouiButtonGroupItem(label = cancelLabel, onClick = onCancel),
        CouiButtonGroupItem(label = confirmLabel, onClick = onConfirm, style = CouiButtonStyle.Primary),
    ),
)
~~~

横排按钮宽 `CouiButtonGroupDefaults.mediumButtonWidth`（152dp，`coui_medium_btn_width`），内侧各留 4dp，视觉间隔 8dp；父容器左右留白 16dp（`coui_horizontal_btn_margin`）。任一按钮文案放不下时整体转竖排：按钮宽 174dp（`coui_larger_btn_width`）、父容器留白 32dp（`coui_horizontal_single_btn_margin`）、相邻按钮间距 16dp（`coui_vertical_btn_margin_top`），第一项排在下方，与原厂 `addView` 顺序一致。窗口宽小于 600dp（`WindowWidthSizeClass.Compact`）时横排按钮均分父宽。

双行文案按钮恒按单行判定，不参与换排比较。竖排时的按钮高度下限取 56dp。

## 图标与自定义内容

需要图标或自定义排版时使用内容槽版本，槽内内容继承按钮的内容色：

~~~kotlin
Button(onClick = onCreate, style = CouiButtonStyle.Primary) {
    Icon(CouiIcons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
    Text(text = createLabel, kind = CouiTextKind.ItemTitle)
}
~~~

图标和文字之间使用 `CouiButtonDefaults.iconSpacing`（4dp，与原厂按钮内部留白一致）。子内容的显式 `color` 和 `tint` 仍然优先。

## 加载与禁用

请求进行中设置 `loading = true`，完成后由页面决定是否禁用：

~~~kotlin
Button(
    onClick = onSubmit,
    label = submitLabel,
    style = CouiButtonStyle.Primary,
    loading = submitting,
    loadingDescription = submittingLabel,
    enabled = !submitting,
)
~~~

加载期间不接受点击，标签淡出、原位换成三个半径 1dp、间距 2dp 的圆点（`coui_loading_btn_circle_radius` / `coui_loading_btn_circle_spacing`），按钮宽度不跳动。原厂没有为这三个点单独提供动画资源，脉冲节奏沿用同一套加载字形的 800ms 周期，三点透明度错开三分之一周期依次亮起。`loadingDescription` 只在加载期间写入无障碍状态说明。

## 尺寸与形状

高度取 `CouiButtonDefaults.minHeight`（44dp），最小宽度 132dp，四周内留白 12dp，标签 14sp `sans-serif-medium`、行距 1.2、最多两行，超出以省略号结束。形状按原厂规则自动选择：宽高相等时画圆，其他尺寸使用平滑胶囊。`modifier` 是第一个参数，调用方的尺寸约束优先。

组件不额外扩到 48dp 触摸区，与原厂一致；紧凑排列需要更大触摸范围时由宿主自行留白。

## 交互链

按压与反馈复用原厂同一条链，不新增实现：

- 触摸形变与位移：`couiTouchMotion`
- 按压缩放：`couiPressMotion`
- 跟手光：`rememberCouiSpotlight`，主操作用 `OpaqueMedium1`，次操作用 `OpaqueMedium2`。该效果要求 API 33，低版本返回空实现，形变与蒙层仍然生效。
- hover / focus / press 蒙层：`couiButtonFeedback`；文字外观没有容器，不叠按压蒙层

手势被父容器接管、取消或组件禁用时，形变、跟手光与蒙层各自收尾，不提交点击。

按钮不使用背景采样与材质模糊，因此不要求 `CouiBackdrop`，也不要求 API 34。

## Demo 示例

三种外观、加载、禁用与两行文案示例见 [DemoButtonSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoButtonSamples.kt)，库入口和配色见 [Button / CouiButtonDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/button/Button.kt)。按钮组见同文件的 `ButtonSample` 与 `ButtonGroup`。

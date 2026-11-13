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

高度取 `CouiButtonDefaults.minHeight`（44dp），最小宽度 132dp，内留白为水平 12dp、垂直 8dp，标签 16sp `sans-serif-medium`、行距 1.2、最多两行，超出以省略号结束。形状按原厂规则自动选择：宽高相等时画圆，其他尺寸使用平滑胶囊。`modifier` 是第一个参数，调用方的尺寸约束优先。

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

三种外观、加载与禁用示例见 [DemoButtonSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoButtonSamples.kt)，库入口和配色见 [Button / CouiButtonDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/button/Button.kt)。

# 🪜步骤条

`StepBar` 用圆点与连接线表示横向进度，已完成和当前步骤用强调色，后续步骤用分隔线色；标签排在每个圆点下方。

`currentIndex` 由宿主持有，组件不推进进度、不处理点击。它只表达进度，不是导航控件——需要可点击的步骤切换时，用 `SegmentedControl` 或 `ChipTab`。需要数值加减时用 [`Stepper`](stepper.md)，原厂 `COUIStepperView` 指的是那个数值控件，不是步骤条。

## 最小调用

~~~kotlin
StepBar(
    steps = listOf("扫描", "清理", "完成"),
    currentIndex = current,
)
~~~

## 参数

- `steps`：至少一步；只有一步时只画圆点，不画连线。
- `currentIndex`：越界会自动夹紧到有效范围。
- `dark` / `colors`：圆点、连线与标签的配色。

圆点尺寸、线宽与标签间距在 `CouiStepBarDefaults`。标签超长以省略号截断，不做横向滚动；步骤多于 4 个时建议缩短文案或改用纵向流程。

源码见 [StepBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/stepper/StepBar.kt)。

## 与原厂的差异

原厂同名 `COUIStepperView` 指数值步进器；横向步骤条在本项目中命名为 `StepBar`，原厂无对应控件，按项目设计绘制。

## Demo

完整示例见 [DemoExtraSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoExtraSamples.kt)。

## 环形步进徽标 `StepRing`

`StepRing` 是同一批次的另一种形态：24dp 细环里放当前步数，进度弧从 12 点方向顺时针覆盖 `step / total` 圈，走到最后一步时整圈改成实心进度色。它只表达「第几步 / 共几步」，没有标签与连线，适合塞进列表行、卡片角标或紧凑流程。

~~~kotlin
Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    StepRing(step = 2, total = 3)          // 24dp
    StepRing(step = 3, total = 3, size = 32.dp)  // 数字随尺寸等比放大
}
~~~

`step` 夹在 `1..total`，`total` 至少按 1 处理。轨道色取 `couiColorContainer8`（日 `#14000000` / 夜 `#1AFFFFFF`），进度弧与数字跟随主题强调色；数字在 24dp 上是 18dp，按 `size / 24dp` 等比缩放。

原厂 `setTextSize` 直接用资源 dp 换算出的 px，**不跟随系统字体缩放**，移植保持一致。组件没有步进动画，改值即重绘。

与原厂的差异：原厂另有 `StrokeCap.BUTT` 的整圈轨道与 `drawOval` 的满圈分支，已按源码实现；`setStep()` 里的 `setContentDescription` 改为 `Modifier.semantics`，无障碍读出的仍是纯步数。原厂 `importantForAccessibility` 与最小 24dp 命中区不变。

源码见 [StepRing](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/stepper/StepRing.kt)、[CouiStepRingDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/stepper/CouiStepRingDefaults.kt)。

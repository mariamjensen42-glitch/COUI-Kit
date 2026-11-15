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

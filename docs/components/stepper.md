# ➕数值步进器

`Stepper` 是减号、当前值与加号组成的数值控件，对应原厂 `COUIStepperView`。它不是横向进度条，进度展示用 [`StepBar`](step-bar.md)。

数值由宿主持有：组件只提交变化后的值，且提交值已夹紧到 `valueRange`。到达下界或上界时对应按钮禁用；单击提交一次，长按后立即提交并按 100ms 连续提交，每次提交触发原厂的 `GRANULAR_SHORT_VIBRATE_SYNC` 触感。

## 最小调用

~~~kotlin
var quantity by rememberSaveable { mutableIntStateOf(1) }

Stepper(
    value = quantity,
    onValueChange = { quantity = it },
    valueRange = 0..99,
)
~~~

## 参数

- `value` / `onValueChange`：当前值与新的期望值；库不保存数值，也不重排或格式化文本。
- `valueRange`：默认 `-999..9999`，与原厂 `ObservableStep` 的边界一致；超出该范围或下界大于上界时抛 `IllegalArgumentException`。
- `step`：每次加减的量，默认 1。
- `enabled`：关闭后两个按钮都不可用，数值降为禁用色。
- `dark` / `colors`：底色、数值与图标色、禁用色和焦点描边色。
- `plusIcon` / `minusIcon`：默认是原厂 `ic_plus_sign` / `ic_minus_sign` 的矢量，由内容色染色；需要别的图形时替换。
- `decreaseDescription` / `increaseDescription`：两个按钮的无障碍说明，默认取库内英文/简体中文资源。

几何与固定值在 `CouiStepperDefaults`：32dp 圆形按钮、44dp 数值最小宽度与 12dp 左右留白、100ms 重复间隔。数值文本采用 18sp `sans-serif-medium` 一档，宽度按原厂算法固定为「最大值位数 + 1」个最宽数字，加减时数字不会左右跳动。

## 与原厂的差异

- 原厂 `enableScaleEffect(view, 2)` 的按压形变由 `CouiPressMotion` 承担，弹簧参数沿用项目现有按钮链；hover/press 蒙层用 `couiStateMask` 的 `CouiMaskType.HoverPress`（原厂 `COUIMaskEffectDrawable` 类型 0），焦点由 `couiFocusOutline` 单独描边（原厂 `COUIStrokeDrawable`），两层与原厂分层一致。
- 焦点描边画在 32dp 按钮外侧，可见性取决于父容器是否裁剪；原厂使用同一 drawable 结构。
- 长按由 `awaitEachGesture` 自行实现：抬手在控件内、且未被父容器消费才算单击；长按后离开控件不结束重复，抬手或取消才结束。

源码见 [Stepper](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/stepper/Stepper.kt)。

## Demo

完整示例见 [DemoExtraSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoExtraSamples.kt)。

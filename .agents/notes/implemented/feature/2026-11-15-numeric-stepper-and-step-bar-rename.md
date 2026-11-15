# Agent Note: 数值步进器与步骤条改名

Status: implemented

## Problem

原厂 `COUIStepperView` 是数值步进器（`IStepper` + `ObservableStep` + `LongPressProxy`），项目里没有对应能力；而项目原有的 `Stepper` 是横向步骤条。两者同名不同物：看过原厂控件的人会以为 `Stepper` 是数值控件，反过来想找数值加减的人也不会想到它被占在了进度展示上。继续沿用旧名，这条错位会一直留在公开 API 里。

## Decision

[Stepper](../../../../coui/src/main/java/com/cycling/coui/stepper/Stepper.kt) 承载数值步进器，`CouiStepperColors` / `CouiStepperDefaults` 持有配色与固定值；横向步骤条改名 [StepBar](../../../../coui/src/main/java/com/cycling/coui/stepper/StepBar.kt)，Defaults 同步为 `CouiStepBarDefaults`，行为与参数不变。

- 受控语义：`value` + `onValueChange`，提交值已夹紧到 `valueRange`；`valueRange` 默认 `-999..9999`，越界或下界大于上界时抛 `IllegalArgumentException`。
- 到达下界时减号禁用、到达上界时加号禁用；`enabled = false` 时两个按钮都不可用且数值降到禁用色。
- 触摸语义对应 `LongPressProxy`：抬手在控件内且未被父容器消费才算单击；长按后立即提交一次、随后每 100ms 提交一次，抬手、CANCEL 或父容器接管时结束，重复期间离开控件不结束。
- 每次提交前触发 `GRANULAR_SHORT_VIBRATE_SYNC`（308），即 `CouiHapticFeedback` 新增的 `GranularShortVibrateSync`。
- 几何与配色取原厂：32dp 圆钮（`stepper_button_size`）、44dp 数值最小宽与 12dp 左右留白、18sp `sans-serif-medium`、按钮底色 `couiColorContainer8`、内容色 `couiColorPrimaryNeutral`、禁用色 `couiColorDivider`、焦点描边 `couiColorFocusOutline`。内容色与禁用色分别等于 `CouiColorScheme.onSurface` 与 `outlineVariant`，所以这两档走主题，容器色与描边色留在 Defaults。
- 指示器宽度按原厂 `onMeasure` 的 `split("")` 语义实现：比最大值的字符数多算一位。这是原厂行为，不当作笔误修正。

## 复用核查

按开发流程的「基础效果复用」，先对照了现有后端再决定装配方式：

| 需求 | 结论 |
| --- | --- |
| hover/press 蒙层 | 复用 `couiStateMask` + `CouiMaskType.HoverPress`。原厂该处是 `COUIMaskEffectDrawable` 类型 0，只有 hover 与 press；类型 1 才会叠焦点蒙层。 |
| 焦点描边 | 复用 `couiFocusOutline`（`CouiStrokeType.Outer`），与原厂 `COUIStrokeDrawable` 同一套「双倍笔宽再裁掉内侧」的画法。 |
| 按压形变 | 复用 `CouiPressMotion`，不新写弹簧。 |
| 触感 | 复用 `performCouiHapticFeedback`，只补一个原厂反馈码。 |
| 加减图标 | 核心 `res` 新增两份矢量，图形与 `viewport` 直接取原厂 `ic_plus_sign` / `ic_minus_sign`，颜色由 `Icon` 染色；不引 `coui-icons` 依赖，核心模块仍只依赖 Android/Compose/AndroidX。 |

## Alternatives considered

- **数值步进器叫 `NumberStepper`，`Stepper` 保持步骤条**：无破坏性变更，最省事。放弃原因是它把「与原厂同名不同物」永久固化，文档得起两套别名才能说清谁是谁。
- **数值步进器叫 `Counter`**：同样避开改名，但 `Stepper` 这个原厂词继续错位，`Counter` 也与「步进 / 单位」语义弱相关。
- **长按用 `combinedClickable`**：少写一段手势，但拿不到「抬手结束重复」的信号，也无法表达「长按后离开控件不结束、抬手或取消才结束」。改为 `awaitEachGesture` + `AwaitPointerEventScope.withTimeoutOrNull`。
- **复用 `couiButtonFeedback` 并加一个关闭焦点的开关**：少一次调用，但该函数的原厂依据就是「三层全开」的通用按钮；数值步进器要的是类型 0 的蒙层，参数化会让两个消费者的语义互相污染。批注改为直接用 `couiStateMask`。
- **指示器宽度按字符数取整（修掉多算一位）**：更符合直觉，但与原厂宽度不同，数字列会窄一档。保留原厂行为并在 KDoc、使用页与覆盖表写明。
- **加减图标只开槽位、不内置默认**：核心不用搬图形。放弃是因为原厂控件自带默认图标，裸调 `Stepper()` 应当能画出来。

## Consequences

- 公开 API 破坏性变更一处：`Stepper` → `StepBar`、`CouiStepperDefaults` → `CouiStepBarDefaults`。CHANGELOG 升级说明、`docs/components/` 导航与 Demo 路由（`stepper` → `stepbar`）均已同步。
- 新增公开类型 `CouiStepperColors` 与 `CouiHapticFeedback.GranularShortVibrateSync`。
- 与本轮已合入的 `Tag` 无重叠：标签是静态展示件，数值步进器是受控输入件，两者各自有使用页与 Note。
- 未验证：本地未执行 Gradle 构建与设备操作；长按节奏、按压形变、焦点描边在父容器裁剪下的可见性只有静态核对结论。已知差异记在[覆盖状态](../../../../docs/status/coverage.md)的「双向核查：数值步进器」：按压弹簧与 `GestureDetector` 的 tap slop 未逐值核对。

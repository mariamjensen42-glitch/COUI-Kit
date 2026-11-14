# COUI 组件库补全计划

面向 ColorOS 17 的 Compose 组件库。本文是**工作计划**，不是设计文档；设计约束以 `docs/development/*` 与对应组件使用页为准。

- 现状：`coui` 147 个 Kotlin 文件、约 40 个公开组件 + 8 组效果，覆盖页面骨架、输入、弹窗、反馈。
- 参考基准：真机一加 Ace 5 / ColorOS 17.0.0.105 / Android 17，1264×2780 @560dpi。22 页 39 张原图 + 6 张补充截图，见 `references/screenshots/`。
- 目标：把 ColorOS 17 系统应用里**真实出现但库中缺失或形态不符**的控件补齐，并把已有组件对齐到截图。

## 一、缺口盘点

### A. 明确缺失（截图已确认，非推测）

| 编号 | 组件 | 原厂依据 | 形态要点 | 复用现有 effect | 状态 |
|---|---|---|---|---|---|
| A1 | `ThumbnailChoiceGroup` / `ThumbnailNavigationGroup` | 设置-显示与亮度（2 列）；设置-通知与控制中心-分类设置（3 列） | 缩略图 + 下方文字；选中态带 RadioButton；导航型带右侧值 + chevron | 无（纯布局 + `CouiShape`） | **已完成** |
| A2 | ~~`ScoreRing`~~ | 手机管家首页 78 分环；清理存储 62.0MB 环 | **不需要新组件**：`CircularProgress` 已有 glow / 圆头 / gap / content，叠一层白色实心圆即可，实测 142 + 2×24 ≈ 189 正好贴合 | 无 | **不需要**（demo 已给组合） |
| A3 | `NotificationCard` | 通知中心 | 图标 + 标题 + 相对时间 + 正文 2 行；**材质不内建**，`containerColor` 默认卡片色，宿主传 Transparent 并用 `couiFloatingSurface` 包裹 | 复用既有浮动表面 | **已完成** |
| A4 | `QuickSettingTile` | 控制中心全展开 | 大磁贴 138×62dp + 小磁贴 62dp，四列、间距 14dp；材质由宿主包裹 | 复用浮动表面 | **已完成** |
| A5 | `LevelSlider` | 控制中心亮度 / 音量条 | 62×138dp 胶囊档位条，底部填充 + 图标；**不扩展 `Slider`**（手势层硬编码 X 轴，且形态不同） | 独立实现，不复制材质 | **已完成** |

### B. 已有组件形态不符（改现有，不新建）

| 编号 | 组件 | 差异 | 依据 |
|---|---|---|---|
| B1 | `QuickActionGrid` | 已加 `CouiQuickActionStyle.Capsule`：全圆、60dp 行高、8dp 间距、14sp 标签 | `safecenter/tools_*.jpg` | **已完成** |
| B2 | `ActionCard` | 结构与操作位置本来就对（文字按钮在右下）；真实差异是**提示卡说明字号 15sp**，原实现用了 13sp | `safecenter/home_dark.jpg` | **已完成** |
| B3 | `SegmentedProgress` | **不需要**：已支持逐段配色（`CouiProgressSegment.color`）；「尾段斜纹」在真机截图上不存在，实测尾段是纯色深灰轨道 | `safecenter/cleanup_menu_dark.jpg` | **不需要** |
| B4 | `BarChart` | **不需要**：`axisLabels` 已提供右侧刻度并同时定位网格线；图例在原厂位于卡片标题行右侧（y 2431 vs 图表 2584），属于宿主排版，不在图表内部 | `battery/modes_dark.jpg` | **不需要** |
| B5 | `CircularProgress` | 无渐变弧、无圆头、无实心内圆结构（已读源码确认） | `safecenter/*.jpg` |

### C. 效果层缺口

| 编号 | 能力 | 用途 | 备注 |
|---|---|---|---|
| C1 | 渐变弧 + 圆头描边 | `ScoreRing`、`CircularProgress` 增强 | 新增最小绘制入口，先评估能否并入 `progress` 包而非新建 effect |
| C2 | 斜纹填充 | `SegmentedProgress` 尾段 | 用 `PathEffect` 或 Shader，不进 Shader 池 |
| C3 | 材质明暗解耦 | 浅色模式下仍出现深色玻璃（天气首页） | 现状 `CouiMaterial` 由 `darkTheme` 决定，需改为「跟随内容明暗」，这是**架构级改动**，影响所有消费方 |

> C3 优先级最高但风险最大：改的是 `CouiMaterial` 的语义，所有组件都会受影响。建议先做 A/B 类，C3 单独一个 PR。

## 二、阶段划分

### P0 参考库（已完成）

- 真机抓图链路 `tools/capture.sh`（成对抓深浅色 + uiautomator 布局树）。
- 22 页 39 张原图，见 `references/screenshots/README.md` 进度表。

### P1 证据固定（每个组件动手前必做）

按 `docs/development/replication-guide.md` 执行，每个组件产出：

1. 原厂资源名与类名（`Widget.COUI.*`、`coui_component_*.xml`、`coui_*` drawable）。
2. 尺寸、字号、行高、圆角、留白的**数值表**（从资源 + 布局树双取，互相校验）。
3. 状态清单：常态 / 按下 / 选中 / 禁用 / 加载 / 空。
4. 截图叠加比对图。

> **资源已就位（2026-11-14）**：从 PKG110 / ColorOS 17.0.0.105 拉取系统应用 APK 到 `references/apk/`（gitignore，不入库），用 `apktool d -s --no-assets` 解包。
> - 17.x（设备上系统应用内嵌的 COUI，核对基准）：`settings`、`systemui`、`notificationcenter`、`safecenter`、`battery`、`launcher`、`weather`、`calendar`、`clock`、`filemanager`、`calculator`、`oplus-framework`、`oneplus-rro`、`systemui-overlay`；A4/A5 已据此修正标签 10/14sp、关闭态 20% 白色等。
> - 13.8.12（旧一代独立库，只用于追旧分支）：`uiengine`（VariUIEngine）。
> - 后六个应用本次只补了 `res/layout*` 与 `res/values*`（含 `values-night`、`values-zh-rCN`），够查控件几何与样式；整包解码产物暂存 `E:\temp\coloros-unpack\<应用>`，需要 drawable 时再取。
> - 两代差异见[复刻指南](../development/replication-guide.md#2-从系统页面到实际消费者)。

### P2 纯布局组件（无新 effect，风险最低）

| 顺序 | 组件 | 工作量 | 依赖 |
|---|---|---|---|
| 1 | `ThumbnailChoiceCard` | 中 | 无 |
| 2 | `QuickActionGrid` 胶囊变体 | 小 | 无 |
| 3 | `ActionCard` 文字按钮变体 | 小 | 无 |
| 4 | `BarChart` 刻度 + 图例 | 小 | 无 |
| 5 | 分组小标题 | — | **不需要**：`Group.title` + `headerActions` 已覆盖「模式说明 ⓘ」这类带图标操作的分组标题 |

交付：组件源码 + `docs/components/*.md` + demo 样例 + CHANGELOG 未发布条目。

### P3 材质组件（复用 `CouiMaterial`）

| 顺序 | 组件 | 关键风险 |
|---|---|---|
| 6 | `NotificationCard` | 玻璃卡在三行布局下的材质合成顺序 |
| 7 | `QSTile` | 磁贴开启态的强调色填充 + 玻璃叠加，需要对照控制中心逐像素调 |

### P4 新绘制能力

| 顺序 | 能力 | 说明 |
|---|---|---|
| 8 | 渐变弧 + 圆头 | 先扩 `CircularProgress`，评估能否不新建 effect |
| 9 | `ScoreRing` | 依赖 8；实心内圆 + 外发光 |
| 10 | `SegmentedProgress` 逐段配色 + 斜纹 | 独立，无依赖 |

### P5 现有组件对齐

- `VerticalSlider`（扩 `Slider`）。
- 按截图逐页比对，记录偏差清单，按「影响 1:1 程度」排序修。

### P6 材质明暗解耦（C3）

单独 PR。需要先写设计说明：`CouiMaterial` 的 `dark` 参数语义从「主题明暗」改为「内容明暗」，给出迁移方式。

### P7 页面级验收

把 `DemoManagerHome`、`DemoCleanupPage`、`DemoSettingsPage` 按截图做 1:1 叠加比对，作为组件的集成验证。

## 三、每个组件的验收标准

1. 主要元素 `bounds` 与真机布局树误差 ≤ 2dp；字号、行高完全一致。
2. 深浅两套截图叠加，边缘无可见错位。
3. 状态全集齐全，动效时长与缓动有原厂依据（资源或录屏抽帧），不靠猜。
4. `docs/components/<组件>.md` 同步；公开入口按 `docs/development/api-design.md` 收窄。
5. demo 有样例；CHANGELOG「未发布」有对应条目。

## 四、明确不做

- 不引入 Material 3 或任何外部设计体系依赖。
- 不做业务逻辑（记录侧滑、删除确认、提交），只做融合算法与视觉。
- 不新增单元测试或模拟器任务（按 `AGENTS.md` 交付约束）。
- 不擅自执行 Git 操作、构建、签名、发布。

## 五、下一步

1. 已取得拆包资源（`references/apk/`），按组件需要补拉其余系统应用。
2. 从 P2 第 1 项 `ThumbnailChoiceCard` 开始，它是两处复用、纯新增、零 effect 依赖。

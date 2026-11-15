# ColorOS 17 组件设计规格证据

本目录存放**从原厂资源提炼出的、可核对的组件级设计规格**。它是写组件库时唯一需要「获取」的东西，不是源码，也不是组件文档。

- 输入 `references/apk/` 已在 `.gitignore:43` 排除，APK、smali、jadx 反编译文本都不入库。
- 本目录入库，只含结构化证据与生成脚本产物，不含任何厂商二进制或反编译源码。
- 按 `docs/development/documentation.md:16,30`，**完整参数与默认值只在源码 / KDoc 维护**。本目录的数值表是原厂证据快照，不是供人抄进组件文档的第二份默认值；当前实现与原厂的差异登记在对应 Note。

## 与全局 token 的分工

| 产物 | 位置 | 回答的问题 |
| --- | --- | --- |
| 全局 token 词表 | `references/apk/<pkg>/tokens/coui-tokens.json` | 有哪些颜色、尺寸、样式、主题属性 |
| 组件规格证据 | `references/specs/<pkg>-<component>.json` / `.md` | **某个组件在某个状态下用哪个 token**，以及它的浅深字面值、来源行号 |

写组件库需要的是后者。词表有 1309 色 / 1570 尺寸，但没有「谁消费它」的绑定；规格证据沿样式继承链求引用闭包，只输出该组件真正消费的资源。

## 最小必要采集清单

| 优先级 | 取什么 | 来源 | 在证据报告中的章节 |
| --- | --- | --- | --- |
| P0 | 几何：宽高、内留白、图标、间距、圆角、最小触摸区 | `res/values*/dimens.xml`、`Widget.COUI.*` 样式 | 1 / 4 |
| P0 | 颜色 × 状态：normal / pressed / selected / checked / focused / disabled 的浅深字面值 | `res/color/*.xml` selector、`res/drawable/*` state-list、`Theme.COUI` 属性 | 2 / 3 |
| P0 | 文字：字号、字重、行距、单行/多行、省略 | `styles.xml` 的 `textAppearance` / `textSize` / `lineSpacingMultiplier` | 1 / 4 |
| P0 | 轮廓：圆角档位、`couiRoundType`、描边与阴影开关 | 样式 item、尺寸 | 1 |
| P0 | 动效数值：duration、控制点、spring 参数、模糊半径、按压系数 | `res/anim*` / `interpolator`；res 里没有的去代码侧 | 5 / 8 |
| P0 | 状态机：进入事件、前置条件、中断 / 反向 / CANCEL 收尾、输入启用时机 | 代码侧，只能人工读成文字规则 | 8 |
| P0 | 真机多状态对照截图（浅 / 深 / 各交互态 / 关键屏幕限定符） | 设备 | 不在本报告内，单独存 `references/screenshots/` |
| P1 | 图标 vector / webp、Lottie、`res-dump.txt` 交叉核对 | `decoded/res/` | — |

**明确不采集**：Java→Kotlin 转换、smali、Activity / Adapter / Binder / Dagger / 系统 API 代码、`pull.sh` 里其余 35+ 个包、全部 1996 个布局、全部 dex。这些对「写组件库」没有信息增量。

## 复跑

在项目根目录执行（Python 3，只读解析 `decoded/res`，不改动 `references/apk/`）：

```bash
python tools/extract_component_spec.py references/apk/settings/decoded \
    --component Button --match 'button|btn' --label settings --out references/specs
python tools/extract_component_spec.py references/apk/systemui/decoded \
    --component Button --match 'button|btn' --label systemui --out references/specs
python tools/extract_component_spec.py references/apk/phonemanager/decoded \
    --component Button --match 'button|btn' --label phonemanager --out references/specs

python tools/extract_component_spec.py --compare references/specs/*-button.json
```

换组件时改 `--component` 与 `--style-prefix`（默认 `Widget.COUI.<component>`），`--match` 控制同族资源的发现范围，`--exclude` 去掉 Material / 单选 / 复选框等噪声，`--device-sdk` 控制 `values-vN` 叠加（当前设备 API 37）。

每份 `.md` 的章节：来源与构建指纹 → 1 样式继承链 → 2 状态色 → 3 颜色 → 4 几何 → 5 动效 → 6 未闭合引用 → 7 同族未引用资源（仅线索）→ 8 静态资源不覆盖、必须另取的部分。每个数值带 `资源名 → 文件:行号`，`.json` 里还保留完整解析链 `chain`，可回溯 `?attr` 与 `@color` 的每一步。

## 实测结论（2026-11-15，PKG110_17.0.0.105(CN01)）

- 三包 `theme_attrs_coui` 均为 **187**，与 `DESIGN-TOKENS.md` 的主题属性统计一致；`unresolved` 均为 **0**，说明 `?attr` → `@color` → 字面值与 ColorStateList 默认色链路已闭合。
- `--compare` 对三包 Button 规格的 **494 个共有键比对，0 处差异**（见 `compare-button.md`）。各包独有键只是额外内嵌的样式变体，不是规格差异。
- 结论：**维护一套组件规格即可**，不需要按应用分叉；只在遇到 README 里记录的那 5 处 SystemUI 微调时交叉核对。
- 深色只换颜色：`values-night/styles.xml` 不重定义 COUI 样式（`settings/README.md:55`、`2026-11-14-coui-shapes-token.md:43`）。一套几何 + 两套色板，不复制样式层。

## 静态资源不覆盖的部分

报告第 8 节列出脚本无法抽取、但对复刻必要的东西：按压 / 回弹的弹簧数值与曲线、材质模糊半径映射与 AGSL 常量、状态机分支、运行门控、对照截图。这些只在代码侧（`references/apk/jadx/` 的 `*Params` / `*Config` / 插值器 / 物理引擎）或真机上。

定点取证按 `docs/development/replication-guide.md` 的追踪顺序：目标页面 → 实际组件与 controller → 主题与调用方覆盖 → 参数生产者 → 绘制入口。**只取数值与规则，不搬类结构**；不要把反编译文本当可编译源码，也不要把没取到的值补成猜测值。

## 落地与升级规则

1. **以 res 为准，代码只补 res 表达不了的动态参数**。系统自己就是用这些资源渲染的，这是最权威的设计契约。
2. 每个数值必须能回答「来自哪个包、哪个文件哪一行、哪个构建 SHA」；ROM 升级后重新 pull、复跑、比对，不沿用旧行号。
3. `CouiTheme` / `CouiShapes` / `CouiSpring` 与组件 `Defaults` 是**承载层**，不是来源层。不要用当前实现反推原厂。
4. 实现与原厂有意不同时，把取舍写进对应 Note 并在 `docs/guide/customizations.md` 登记；证据报告本身保持原厂原值，不改。

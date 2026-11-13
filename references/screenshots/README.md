# 真机参考截图清单（ColorOS 17）

## 为什么还需要截图

单控件的尺寸、配色、圆角、字号已经能从 COUI 资源里读到（`Widget.COUI.*`、`coui_*`、`coui_component_*.xml`），**不需要靠截图反推**。截图不可替代的价值只有三块：

1. **页面级组合**：分组间距、卡片圆角与内留白的节奏、标题层级、卡片之间的呼吸感。资源里只有单个控件，没有「一页怎么拼」。
2. **材质在真实背景上的渲染结果**：模糊半径、混色比例、边缘高光、投影强度、玻璃层叠顺序。这些是 GPU 渲染出来的，XML 里查不到。
3. **动效关键帧与状态全集**：资源只给终态，中间帧、时长、缓动要靠录屏。

所以下面按「材质浮层 > 页面模板 > 状态帧」排序，越靠前信息量越大。

## 已抓取进度

抓图命令：`bash tools/capture.sh <应用> <页面> both`（`both` 会强制切浅色、深色各抓一张，结束后恢复原夜间模式）。
每张图配一份同名 `.layout.xml` 布局树，含每个元素的 `bounds`、`class`、`text`。

设备基准：一加 Ace 5（PKG110）· ColorOS 17.0.0.105 · Android 17 (API 37) · 1264×2780 @ 560dpi（≈361dp 宽）。

| 页面 | 文件 | 状态 |
|---|---|---|
| 设置首页 | `settings/home_{light,dark}.png` | 完成 |
| 显示与亮度 | `settings/display_{light,dark}.png` | 完成 |
| 通知与控制中心 | `settings/notification_settings_{light,dark}.png` | 完成 |
| 应用管理 | `settings/apps_{light,dark}.png` | 完成 |
| 关于本机 | `settings/device_info_{light,dark}.png` | 完成 |
| 电池 | `battery/home_{light,dark}.png` | 完成 |
| WLAN | `settings/wifi_dark.png` | 仅深色，且 WLAN 关闭、列表为空 |
| 控制中心（全展开） | `systemui/control_center_{light,dark}.png` | 完成 |
| 通知中心 | `systemui/notification_shade_{light,dark}.png` | 完成 |
| 最近任务 | `systemui/recents_dark.png` | 仅深色 |
| 音量 OSD | `systemui/volume_osd_{light,dark}.png` | 完成（右缘细条形态） |
| 桌面（大文件夹） | `launcher/home_{light,dark}.png` | 完成 |
| 图标长按菜单 | `launcher/icon_menu_dark.png` | 仅深色 |
| 文件夹长按菜单 | `launcher/folder_menu_light.png` | 仅浅色 |
| 天气首页 | `weather/home_{light,dark}.png` | 完成 |
| 日历月视图 | `calendar/home_{light,dark}.png` | 完成 |
| 文件管理 | `filemanager/home_{light,dark}.png` | 完成 |
| 闹钟列表 | `clock/alarm_dark.png` | 仅深色 |
| 新建/编辑闹钟 | `clock/alarm_edit_{light,dark}.png` | 完成 |
| 世界时钟 | `clock/world_{light,dark}.png` | 完成 |
| 计时器 | `clock/timer_{light,dark}.png` | 完成 |
| 计算器 | `calculator/home_dark.png` | 仅深色 |
| 手机管家首页 | `safecenter/home_dark.jpg` | 仅深色 |
| 实用工具（清理/隐私） | `safecenter/tools_clean_dark.jpg` | 仅深色 |
| 实用工具（安全/辅助） | `safecenter/tools_safety_dark.jpg` | 仅深色 |
| 清理存储（菜单展开） | `safecenter/cleanup_menu_dark.jpg` | 仅深色 |
| 清理存储（分数环） | `safecenter/cleanup_dark.jpg` | 仅深色 |
| 电池模式卡与柱状图 | `battery/modes_dark.jpg` | 仅深色 |

待抓：WLAN 列表（需先开 Wi-Fi）、应用管理条目全形态（复选/红色项）、通知设置二级页、悬浮通知、
桌面文件夹展开态（大文件夹需点空白区，多次尝试未成功）、锁屏（会触发锁屏密码，未抓）、
相册、音乐播放页（含个人内容，待确认）。

## 拍摄规范

- 用系统截图，或 `adb pull /sdcard/Pictures/Screenshots`。**不要用微信 / QQ 传**，二次压缩会毁掉渐变和模糊层次。
- 同一台机、同一分辨率拍完一轮；记下屏幕分辨率与 DPI（设置 → 关于手机）。
- 每页**浅色 + 深色各一张**。
- 状态栏统一：时间 9:41、电量 100%、清空通知、隐藏运营商名，避免干扰对齐。
- 动效用**录屏 60fps**，不要连拍截图。
- 命名 `应用_页面_状态_主题.png`，例：`settings_display_list_dark.png`。
- 存放 `references/screenshots/<应用名>/`。

## P0 材质与浮层

> 优先级最高。模糊、混色、高光只能从渲染结果反推，缺了这块凝光视效就是猜。

| # | 目标 | 操作路径 | 要拍的状态 | 对应组件 |
|---|---|---|---|---|
| 1 | 控制中心 | 状态栏下拉一次；再下拉全展开 | 浅/深；磁贴开与关；亮度条拖动态；媒体卡；Wi-Fi / 蓝牙展开的二级面板 | Menu、Dialog、Backdrop、GradientBlur、Slider |
| 2 | 通知中心 | 下拉 → 通知列表 | 有 1 条 / 多条；同应用折叠分组；媒体通知；长按单条展开快捷操作 | ActionCard、Item、Menu |
| 3 | 悬浮通知 | 应用发通知时停留 | 压在主界面上的完整一帧（看背景模糊强度） | Toast、Backdrop |
| 4 | 最近任务 | 底部上滑停住 | 卡片层叠、卡片间距、底部「清除全部」 | — |
| 5 | 桌面图标长按菜单 | 桌面长按任一 App | 毛玻璃菜单 + 图标周围聚光 | Menu、CouiMenuSpotlight、CouiMenuShadow |
| 6 | 桌面文件夹 | 点开桌面文件夹 | 打开瞬间（背景放大模糊）与打开后 | Backdrop、Dialog |
| 7 | 音量面板 | 按音量键 | 侧边滑杆 OSD；媒体音量 / 铃声音量两种 | Slider、FloatingToolbar |
| 8 | 锁屏 | 锁屏界面 | 时间排版、通知堆叠、底部左右两个圆钮 | — |

## P1 页面级模板

> 补齐目前组件库缺的形态，同时作为 demo 的 1:1 复刻目标。

| # | 目标 | 操作路径 | 要拍的状态 | 对应组件 / 缺口 |
|---|---|---|---|---|
| 9 | 设置首页 + 3 个二级页 | 设置 → 首页 / WLAN 列表 / 显示与亮度 / 应用管理 | **列表条目全形态**：只有标题、标题+副标题、右侧值文字+chevron、右侧开关、右侧复选、左侧大图标入口、红色危险项、分组 Footer 说明 | Item、Group、Footer、Divider、Switch、Checkbox |
| 10 | 天气首页 | 天气 App | 顶部大数字、逐小时、多日卡片、空气质量条、日出日落卡 | Display 字号、BarChart |
| 11 | 相册 | 相册 → 网格 / 单张大图 / 选择模式 | 网格间距、大图底部工具条、选择模式顶部计数+底栏操作 | — |
| 12 | 音乐播放页 | 音乐 → 播放页 / 收起后的 mini player | 封面高斯模糊背景、底部 mini player 胶囊 | Backdrop、GradientBlur、DockBar |
| 13 | 时钟 | 时钟 → 时钟 / 世界时钟 / 计时器 | 大数字排版、世界时钟列表、计时器滚轮 + 圆环 | WheelPicker、CircularProgress、Display |
| 14 | 计算器 | 计算器 | 网格按钮、按下态、运算中结果区 | — |
| 15 | 日历 | 日历 → 月视图 / 日程列表 / 新建日程 | 今天、选中、有日程圆点三种格子态；新建日程的滚轮 | CalendarPicker（缺月视图整页） |
| 16 | 文件管理 / 软件商店 | 对应 App 列表页 | 混合列表：图标 + 双行 + 右侧按钮 + 行内进度条 | Item、LinearProgress |

## P2 状态与动效关键帧

> 每个控件至少三帧，用录屏抽帧。

| # | 目标 | 要拍的状态 |
|---|---|---|
| 17 | 开关 | 关 / 开 / 加载中（若 ColorOS 17 有加载态）/ 拖动中 |
| 18 | 按钮 | 常态 / 按下 / 加载 / 禁用（主操作 + 次操作各一套） |
| 19 | 下拉刷新 | 下拉中 / 到阈值未松手 / 刷新中 / 回弹 |
| 20 | 顶栏搜索 | 点按前 / 展开中 / 展开后 / 输入中 / 取消回退（5 帧） |
| 21 | 大标题折叠 | 展开 / 中间 / 收起（3 个滚动位置） |
| 22 | 滑块 | 常态 / 拖动中 / 松手回弹 |
| 23 | 分段选择器 | 指示器移动中间帧 |
| 24 | 底部面板 | 升起中间帧 + 遮罩变化 |

## 更省事的做法

真机插 USB 后不用手动截图，直接抓原图并同时拿布局树：

```bash
adb exec-out screencap -p > references/screenshots/settings/home_light.png
```

布局树（坐标、尺寸、字号、控件类名）用 Android CLI：

```bash
android layout --full --pretty -o references/screenshots/settings/home.json
android screen capture -a --output references/screenshots/settings/home_annotated.png
```

`-a` 会给每个 UI 元素打编号框，配合 `android screen resolve` 能直接定位坐标。需要我批量抓的时候，把上面 P0/P1 的页面在真机上一一打开，我来跑脚本。

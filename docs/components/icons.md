# 图标

`CouiIcons` 在 Composable 中提供矢量 `ImageVector` 与位图 `Painter`，并根据 `CouiTheme` 加载日夜资源。

使用图标时，在调用模块中添加依赖，并与核心库使用相同版本：

```kotlin
implementation("com.github.mariamjensen42-glitch.COUI-Kit:coui-icons:$couiVersion")
```

以下示例中的 `contentDescription` 由调用方根据界面语义提供。

## 基本显示

在 Composable 中使用 `Image` 可直接显示图标内置颜色；需要跟随内容色时传入 `tint`，传 `Color.Unspecified` 保留图标默认配色：

```kotlin
Image(
    imageVector = CouiIcons.Filled.Wifi,
    contentDescription = contentDescription,
)

Icon(
    imageVector = CouiIcons.Filled.Wifi,
    contentDescription = contentDescription,
    tint = Color.Unspecified,
)
```

图标按 `CouiIcons.Filled` 和 `CouiIcons.Outlined` 提供。`Back`、`Next`、`Close`、`Add`、`DarkLight`、`ListFilter`、`More` 和 `Scan` 在两种样式中使用相同图形；`Search`、`SliderCustom` 和 `LayoutSpliced` 仅提供 `Outlined`。它们默认日间为黑色、夜间为白色，其中 `Back`、`Next`、`ChevronRight`、`Download`、`Copy` 和 `Send` 支持 RTL 自动镜像。

`Outlined` 另提供两类只有线框形式的图标：

- 设置入口线框图，与同名 `Filled` 图标成对，保留原厂功能色：`Wifi`、`Bluetooth`、`Bell`（蓝）、`Sun`、`Plane`、`Location`（黄）、`Volume`（绿）、`ShieldUser`（蓝）。
- 通用操作图标，中性黑白：`Check`、`Delete`、`Edit`、`Info`、`Warning`、`ChevronRight`、`History`、`Folder`、`Sync`、`Link`、`Download`、`Visibility`、`Gesture`、`Error`、`ArrowDown`、`ExpandMore`、`ExpandLess`。

`Filled` 覆盖常用功能图形，按用途分为几组：分享与编辑（`Share`、`Copy`、`Send`、`Upload`、`Print`、`OpenInNew`）、播放控制（`Play`、`Pause`、`Previous`、`NextTrack`、`VolumeOff`）、系统与网络（`Hotspot`、`Vpn`、`Nfc`、`Cast`、`BatteryCharging`、`SystemUpdate`、`Storage`、`DataUsage`、`Language`、`NotificationsOff`、`MicOff`、`Fingerprint`、`PrivacyShield`、`DoNotDisturb`、`Flashlight`、`Vibrate`）、内容与文件（`Document`、`Video`、`Chat`、`Person`、`Apps`、`Calendar`、`Clock`、`Keyboard`、`QrCode`、`Help`）。这一组统一为中性单色，需要功能色时由调用方传 `tint`。

`Filled` 另提供一组自带功能色的清理与扫描图标，按原厂配色分为蓝（`ClearFile`、`ClearPhoto`、`ClearDoc`、`RecycleBin`、`CloudGallery`、`ClearUnusedApp`、`SdCard`、`NoNetwork`、`FindPhone`、`StartupApp`、`Usb`、`AntiFraud`、`SmartNetwork`、`PrivacyProtect`、`NotificationManage`、`NetworkAbnormal`、`DeviceAdmin`、`FingerScan`、`AppManagement`、`HighPerformance`、`CloudService`、`RiskInterception`、`AppUsage`、`SystemOpt`、`LoginDevice`、`CloudSpaceUpdate`、`ScanLog`、`CloudSpaceLow`、`AppManager`）、绿（`ClearApk`、`ClearApp`、`ClearWidget`、`AddShortcut`、`ClearInApp`、`DedupFile`、`CompressApp`、`AppDownload`、`IdleApp`、`PowerSaving`、`NetAcceleration`、`SystemBoost`、`FaceUnlock`、`OneKeyOpt`、`SystemResource`、`LowStorage`、`AppClean`、`DevMode`、`Ota`、`CallIntercept`、`FileEncryption`、`RecycleMessage`、`SmsIntercept`、`WidgetClean`、`BlockMms`、`CallHighlight`、`PaymentSafe`）、橙（`LargeFile`、`AppPermission`、`RiskBehavior`、`AntiHarassment`、`VirusRisk`、`OverAuthorized`、`Virus`、`AntiTheft`、`ScreenInsurance`）、黄（`DuplicateFile`、`AppLock`、`UnencryptedNetwork`、`AccessibilityRisk`、`Backlight`、`RecentLock`、`RecycleNote`）、红（`ClearAudio`、`ClearVideo`、`VirusUpdate`）五组。它们默认显示五组功能色，需要统一着色时传 `tint`。

`Filled` 另有一组原厂固定色图形，不随主题变化也不写入功能色色板，取色与原资源一致：`RecycleFileManager`、`RecycleGallery`（蓝 `#0066FF`）、`DndKey`、`BrightnessAuto`（琥珀 `#F2A900`）、`GestureAllApps`、`GestureSwipeBack`、`GestureSwipeUp`（深蓝 `#001E2D`）、`BluetoothWarning`（`#FCBD00`）、`FloatingWindow`（`#455A64`）、`GesturePlay`（`#1A73E8`）、`PalmUnlock`、`SmartLock`（`#00A1FF`）、`AppCleanWarning`、`VerifyError`（`#E32E27`）、`TagClear`、`TagVirus`（`#264B91`）、`CorpBadgeOff`（`#607D8B`）、`EmergencyStar`（`#EA4335`）、`Finder`（`#00677D`）、`TileEditAdd`（`#006AFF`）、`NetworkQuality`（`#1A8CFF`）、`SpaceRelease`（`#007AFF`）、`AppPlaceholder`（`#5EBC5E`）。这些色值不在功能色色板内，传入 `tint` 会整体覆盖。

`Filled` 还提供中性单色的设置与系统界面操作图形：`Guarantee`、`Hearing`、`Swap`、`RestoreDefault`、`MoveTop`、`SplitScreen`，与前面两组一样可通过 `tint` 定制颜色。

矢量图形保存在 VectorDrawable 资源中，传统 View 可直接引用，例如 `@drawable/coui_wifi_filled`。传统 View 根据 Android 配置选择日夜资源。

`Search`、`Back`、`Next`、`Close`、`Add`、`DarkLight`、`CloudData`、`Album` 和 `WandSpark` 的可见图形均为不透明单色，默认日间黑色、夜间白色。底栏状态透明度由 `NavigationBar` 应用；独立 FAB 默认以不透明白色染色，可通过 `contentColor` 覆盖。

## 显示位图

`CouiIcons.Bitmap` 返回 `Painter`。在 `Image` 中通过 `Modifier.size` 指定显示尺寸，图标保留 PNG 的颜色和透明度。

```kotlin
Image(
    painter = CouiIcons.Bitmap.DevicePair,
    contentDescription = contentDescription,
    modifier = Modifier.size(24.dp),
)
```

`DevicePair` 根据 COUI 主题使用日间或夜间原图。在 `Item` 的 `icon` 插槽内显示时，可将 `contentDescription` 设为 `null`，由列表标签说明含义。使用 `Image` 显示该 `Painter`，并设置 `colorFilter = null` 保留原色。传统 View 可通过 `@drawable/coui_device_pair` 引用该资源。

## 指定颜色与特殊图标

`CouiColor.Day` 和 `CouiColor.Night` 均提供 `GRAY`（`#474747`）和 `DARK_GRAY`（`#191919`）。

- **单色图标指定 `tint`**：可结合 `CouiTheme.isDark` 从 `CouiColor.Day` / `CouiColor.Night` 选择颜色并传给 `Icon`：

```kotlin
val iconTint = if (CouiTheme.isDark) CouiColor.Night.BLUE else CouiColor.Day.BLUE
Icon(
    imageVector = CouiIcons.Filled.Wifi,
    contentDescription = contentDescription,
    tint = iconTint,
)
```

- **多色图标**：`AI`、`Google`、`SettingDetail`、`Phone` 内置多色或渐变路径，应使用 `Image` 或 `Icon(..., tint = Color.Unspecified)`，避免整体 `tint` 覆盖原有配色。
- **应用更新图标（`AppUpdate`）**：自定义圆形背景色时调用 `CouiIcons.Filled.AppUpdate(color = ...)`，并在 `Icon` 中传 `tint = Color.Unspecified` 以保留白色前景：

```kotlin
val badgeColor = if (CouiTheme.isDark) CouiColor.Night.GREEN else CouiColor.Day.GREEN
Icon(
    imageVector = CouiIcons.Filled.AppUpdate(color = badgeColor),
    contentDescription = contentDescription,
    tint = Color.Unspecified,
)
```

[CouiIcons.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui-icons/src/main/java/com/cycling/coui/icons/CouiIcons.kt) · [CouiColor.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/theme/CouiColor.kt)

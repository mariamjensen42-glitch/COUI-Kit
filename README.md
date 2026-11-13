<div align="center">

# COUI-Kit

面向 Android Jetpack Compose 的 COUI 组件与视觉效果库

[![Maven Central](https://img.shields.io/maven-central/v/io.github.itoseo/coui?label=Maven%20Central&color=blue)](https://central.sonatype.com/artifact/io.github.itoseo/coui)
[![Android API 31+](https://img.shields.io/badge/minSdk-31-3DDC84?logo=android)](docs/guide/compatibility.md)
[![MPL 2.0](https://img.shields.io/badge/License-MPL--2.0-blue)](LICENSE)
[![Telegram 讨论群组](https://img.shields.io/badge/Telegram-%E8%AE%A8%E8%AE%BA%E7%BE%A4%E7%BB%84-26A5E4?logo=telegram&logoColor=white)](https://t.me/metric_official/2949)

</div>

COUI-Kit 参考 ColorOS 17 的「凝光视效」，提供背景模糊、材质混色、轮廓光和跟手动效。

## 特点

- 组件和效果可以分别使用，也可以组合使用。
- 纯 COUI 页面可组合 `CouiTheme` 与可选的 [Scaffold](docs/components/scaffold.md)，统一栏位测量和安全区，不引入外部设计体系依赖。
- 可以只取用单个 COUI 组件，与宿主现有设计体系或自有组件混搭；不要求替换宿主的 Scaffold、主题和导航，**灵活自由**
- 支持接入 App 的配色、文字样式和形状，并提供常用外观预设。
- 内置原生主题与组件默认值，配色、字体和 Shape 可由宿主逐项覆盖，见[外观与预设](docs/guide/appearance.md)。
- 基于 Android、Compose 和 AndroidX 的公开 API，不依赖厂商 SDK 或私有 SO，**全系统可用**

## Demo

运行 `demo` 模块，可以从目录进入组件与效果详情，并切换深浅色和外观预设。运行方法、操作说明和 CI 构建下载方式见 [Demo 指南](docs/guide/demo.md)。

## 开始使用

### 引入依赖

在已启用 Compose 的模块中添加核心依赖：

```kotlin
dependencies {
    implementation("io.github.itoseo:coui:0.0.1-alpha11")
}
```

仓库配置和工具链要求见[快速开始](docs/guide/getting-started.md)。部分效果需要更高的 Android 版本，见[兼容要求](docs/guide/compatibility.md#效果门槛)。

使用 `CouiIcons` 时，添加可选的图标依赖，详见[图标用法](docs/components/icons.md)。

### 外观与主题

组件默认使用 COUI 原生配色与排版。需要接入宿主配色时，在页面作用域组合 `CouiTheme`；也可以只用显式参数覆盖单个组件：

```kotlin
CouiTheme(darkTheme = darkTheme) {
    content()
}
```

`CouiTheme` 只提供配色、排版和内容作用域，不接管导航、业务状态或页面容器。需要自定义色板时传入 `colorScheme`，局部覆盖规则见[外观与预设](docs/guide/appearance.md)。

默认深浅模式跟随系统。App 有独立主题开关时，在现有主题 Provider 中提供实际的 `darkTheme`：

```kotlin
CompositionLocalProvider(LocalCouiDarkTheme provides darkTheme) {
    content()
}
```

`darkTheme` 和 `content` 来自宿主主题。库不会从自定义色板猜测模式；显式组件参数继续优先。

协作调试时，也可通过 [GitHub Packages](docs/guide/github-packages.md) 接入已发布的开发快照。需要本地修改库时，按[源码模块接入](docs/guide/getting-started.md#源码模块)配置工程。

## 文档

在线阅读：[itoseo.github.io/COUI-Kit](https://itoseo.github.io/COUI-Kit/)

- [快速开始](docs/guide/getting-started.md)
- [组件目录](docs/components/README.md)
- [图标用法](docs/components/icons.md)
- [效果目录](docs/effects/README.md)
- [兼容要求](docs/guide/compatibility.md)
- [外观与预设](docs/guide/appearance.md)
- [与原厂的定制差异](docs/guide/customizations.md)
- [GitHub Packages 开发快照](docs/guide/github-packages.md)
- [更新记录与版本映射](CHANGELOG.md)

## 反馈与贡献

遇到问题，可以先查看[常见问题](docs/guide/getting-started.md#常见问题)。反馈时请附上库版本、Android 版本、复现步骤和相关日志；视觉问题请同时提供截图或录屏。

欢迎提交修复、组件、效果或文档改进，参与开发请阅读[贡献指南](CONTRIBUTING.md)。

## 许可与名称使用

本项目自有源码采用 [MPL 2.0](LICENSE)，第三方内容保留各自许可与版权声明，具体范围见 [NOTICE](NOTICE)。

ColorOS 及相关名称归 OPPO 所有；本项目与 OPPO 没有隶属、授权或背书关系。

修改版和分叉版的发行名称、Logo 及来源说明遵循[名称与标识使用政策](TRADEMARKS.md)。

---

[![Star History Chart](https://api.star-history.com/svg?repos=ItosEO/COUI-Kit&type=Date)](https://star-history.com/#ItosEO/COUI-Kit&Date)

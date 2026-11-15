# COUI-Kit

面向 Android Jetpack Compose 的 COUI 组件与视觉效果库，参考 ColorOS 17 的「凝光视效」，提供背景模糊、材质混色、轮廓光和跟手动效。

组件和效果可以分别使用，也可以组合使用；可以只取用单个组件与宿主现有设计体系混搭，不要求替换宿主的 Scaffold、主题和导航。

## 特点

- 组件与效果独立可用，也可组合使用。
- 纯 COUI 页面可组合 `CouiTheme` 与可选的 [Scaffold](components/scaffold.md)，统一栏位测量和安全区。
- 支持接入 App 的配色、文字样式和形状，并提供常用外观预设。
- 内置原生主题与组件默认值，配色、字体和 Shape 可由宿主逐项覆盖，见[外观与预设](guide/appearance.md)。
- 按容器宽度提供 `Compact` / `Medium` / `Expanded` 三档断点，见[自适应布局](components/adaptive-layout.md)。
- 基于 Android、Compose 和 AndroidX 的公开 API，不依赖厂商 SDK 或私有 SO。

## 引入依赖

```kotlin
dependencies {
    implementation("io.github.itoseo:coui:0.0.1-alpha13")
}
```

仓库配置和工具链要求见[快速开始](guide/getting-started.md)，运行门槛见[兼容要求](guide/compatibility.md)。

## 从这里开始

| 入口 | 内容 |
| --- | --- |
| [快速开始](guide/getting-started.md) | 添加依赖、配置页面与常见问题 |
| [组件目录](components/README.md) | 页面、列表、输入、弹窗与反馈组件 |
| [效果目录](effects/README.md) | 背景采样、模糊、阴影、光效与动效 |
| [外观与预设](guide/appearance.md) | 配色、字体、Shape 与局部覆盖 |
| [与原厂的定制差异](guide/customizations.md) | 与 ColorOS 原厂实现的差异说明 |
| [开发文档](development/README.md) | 实现流程、代码规范与发布 |

## 反馈与贡献

遇到问题时先查看[常见问题](guide/getting-started.md#常见问题)。反馈请附上库版本、Android 版本、复现步骤和相关日志；视觉问题请同时提供截图或录屏。

参与开发请阅读[开发文档](development/README.md)与[开发流程](development/workflow.md)。

## 许可与名称使用

本项目自有源码采用 MPL 2.0，第三方内容保留各自许可与版权声明。ColorOS 及相关名称归 OPPO 所有；本项目与 OPPO 没有隶属、授权或背书关系。

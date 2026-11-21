# Diagnostic

## Overview
临时绘制取证包。`CouiRenderDiagnostics` 由 App 在 canary 场景启用，登记 `CouiBackdrop` 采样层与绘制节点探针，支持定点重绘、隐藏底栏、JSON 快照与截图；所有操作约定在主线程执行。

## Interface
- `public object CouiRenderDiagnostics`：`enabled`、`enable(reporter)`、`event(message)`、`snapshot()`、`sourceIds(target)`、`suspend captureSource(id)`、`redraw(target)`、`recreateSource(id)`、`setBottomHidden(hidden)`、`reset()` [CouiRenderDiagnostics.kt:CouiRenderDiagnostics]
- `public fun Modifier.renderDiagnosticProbe(name: String?): Modifier` [CouiRenderDiagnostics.kt:renderDiagnosticProbe]
- `internal class RenderProbeNode(name) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode` [CouiRenderDiagnostics.kt:RenderProbeNode]
- `internal fun diagnosticCoordinates(coordinates): Any`：坐标转 JSON [CouiRenderDiagnostics.kt:diagnosticCoordinates]
- `internal fun attachSource/detachSource/attachProbe/detachProbe(...)`：采样层与探针登记 [CouiRenderDiagnostics.kt:attachSource]

## Invariants
- 仅在 `reporter != null`（`enabled`）时工作：禁用时 `event` 为空操作，`attachSource` 直接返回 [CouiRenderDiagnostics.kt:enabled]
- 事件队列上限 64，满后移除最早条目 [CouiRenderDiagnostics.kt:event]
- `redraw`/`recreateSource`/`setBottomHidden` 在无匹配目标时 `require` 失败并抛异常 [CouiRenderDiagnostics.kt:redraw]
- `renderDiagnosticProbe` 在 `name == null` 或诊断未启用时原样返回修饰符，不挂节点 [CouiRenderDiagnostics.kt:renderDiagnosticProbe]
- 探针 `hidden` 只跳过 `drawContent()`，不改变布局、尺寸或点击 [CouiRenderDiagnostics.kt:RenderProbeNode]
- 探针字段每次绘制递增 `drawCount` 并记录 `lastDrawMs` [CouiRenderDiagnostics.kt:RenderProbeNode]

## Side Effects
- 全局单例可变状态：`reporter`、`nextId`、`sources`/`probes`/`events` [CouiRenderDiagnostics.kt:CouiRenderDiagnostics]
- 平台 API：`android.os.SystemClock.uptimeMillis()` 打时间戳 [CouiRenderDiagnostics.kt:event]
- 绘制拦截：`DrawModifierNode.draw()` 包裹 `drawContent()` 并触发 `invalidateDraw` [CouiRenderDiagnostics.kt:RenderProbeNode]
- JSON 序列化：`org.json.JSONObject`/`JSONArray` 生成快照 [CouiRenderDiagnostics.kt:snapshot]
- 接入 `CouiBackdrop` 的 `diagnosticState`/`diagnosticCapture`/`diagnosticRecreate` [CouiRenderDiagnostics.kt:attachSource]

## Verification
- Run: `./gradlew :coui:lintDebug`

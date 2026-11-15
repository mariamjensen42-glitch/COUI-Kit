package com.itos.coui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

/** Demo 的导航键；页面内容与行包装仍由 Demo 持有，不进入库 API。 */
internal sealed interface DemoRoute : NavKey {
    data object Home : DemoRoute
    data class Detail(val page: String) : DemoRoute
}

private const val HomeKey = ""

/**
 * 页面栈跨配置变更保存。路由本身不引入序列化插件：只把 page 名写进 Bundle，
 * 首页用空串表示，恢复时还原成对应的 [DemoRoute]。
 */
private val DemoBackStackSaver = listSaver<SnapshotStateList<DemoRoute>, String>(
    save = { stack -> stack.map { route -> if (route is DemoRoute.Detail) route.page else HomeKey } },
    restore = { keys ->
        mutableStateListOf<DemoRoute>().apply {
            keys.forEach { key -> add(if (key == HomeKey) DemoRoute.Home else DemoRoute.Detail(key)) }
        }
    },
)

@Composable
internal fun rememberDemoBackStack(): SnapshotStateList<DemoRoute> =
    rememberSaveable(saver = DemoBackStackSaver) { mutableStateListOf<DemoRoute>(DemoRoute.Home) }

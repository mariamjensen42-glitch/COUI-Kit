package com.cycling.coui.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.cycling.coui.menu.ProvideCouiMenuBackdrop

class MainActivity : DemoHostActivity() {
    @Composable
    internal override fun Page(dark: Boolean, preferences: DemoPreferences) {
        val backStack = rememberDemoBackStack()
        // 首页不再出栈，栈空时不拦截系统返回，交给 Activity 结束。
        val back: () -> Unit = { if (backStack.size > 1) backStack.removeLastOrNull() }
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            onBack = back,
            entryProvider = { key ->
                when (key) {
                    DemoRoute.Home -> NavEntry(key) {
                        DemoApp(dark, preferences, onToggleTheme = { preferences.dark(!dark) },
                            openPage = { backStack.add(DemoRoute.Detail(it)) })
                    }
                    is DemoRoute.Detail -> NavEntry(key) {
                        DemoSecondaryPage(page = key.page, dark = dark, preferences = preferences, back = back)
                    }
                    else -> error("未知路由: $key")
                }
            },
        )
    }
}

/** 普通不透明 Activity，不覆盖 ROM 的 Window 动画，不截获 Activity 根级返回。 */
abstract class DemoHostActivity : ComponentActivity() {
    @Composable
    internal abstract fun Page(dark: Boolean, preferences: DemoPreferences)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            val preferences = androidx.compose.runtime.remember { DemoPreferences.get(this) }
            val dark = preferences.darkOverride ?: systemDark
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            DemoTheme(dark = dark) {
                ProvideCouiMenuBackdrop(isolateContent = false) { windowSource ->
                    Box(Modifier.fillMaxSize().then(windowSource)) {
                        Page(dark, preferences)
                    }
                }
            }
        }
    }
}

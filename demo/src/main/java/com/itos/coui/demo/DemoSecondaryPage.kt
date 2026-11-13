package com.itos.coui.demo

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.floatingactionbutton.CouiFabColor
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.toast.ToastHost
import com.itos.coui.toast.rememberToastHostState

@Composable
internal fun DemoSecondaryPage(page: String, dark: Boolean, preferences: DemoPreferences,
    back: () -> Unit) {
    // 页面级复刻自带渐变底与页内大标题，不套用统一的折叠顶栏外壳。
    if (page == "manager") {
        ManagerHomeSample(dark = dark, onBack = back)
        return
    }
    if (page == "cleanup") {
        CleanupPageSample(dark = dark, onBack = back)
        return
    }
    val toggleTheme = { preferences.dark(!dark) }
    var fabPreset by rememberSaveable { mutableStateOf(CouiFabColor.Notes) }
    var roundedFab by rememberSaveable { mutableStateOf(false) }
    val toastState = rememberToastHostState()
    val catalogPage = if (DemoWheelPickerEntries.any { it.page == page }) "wheel" else page
    val entry = (DemoComponentGroups + DemoEffectGroups).flatMap { it.entries }.firstOrNull { it.page == catalogPage }
    val title = entry?.title ?: R.string.settings_about
    val floatingAction: (@Composable BoxScope.() -> Unit)? = if (page == "fab") {
        {
            FabSampleButton(Modifier.align(Alignment.BottomEnd).padding(24.dp),
                dark = dark, preset = fabPreset, rounded = roundedFab)
        }
    } else null
    CompositionLocalProvider(LocalCouiContourGlowEnabled provides preferences.contour) {
        // 轻提示覆盖在页面之上，自身不占位也不拦截输入。
        Box(Modifier.fillMaxSize()) {
            DemoSettingsPage(title = stringResource(title), backDescription = stringResource(R.string.settings_back),
                onBack = back, dark = dark, colors = CouiPageDefaults.themeColors(dark), floatingAction = floatingAction) {
                when (page) {
                    "sliders", "switch", "checkbox", "dialogs", "menu" -> ComponentDetail(page, dark, toggleTheme)
                    "button" -> ButtonSample(dark)
                    "progress" -> ProgressSample(dark)
                    "toast" -> ToastSample(toastState)
                    "badge" -> BadgeSample(dark)
                    "pageindicator" -> PageIndicatorSample(dark)
                    "emptystate" -> EmptyStateSample(dark)
                    "textfield" -> TextFieldSample(dark)
                    "bottomsheet" -> BottomSheetSample(dark)
                    "chip" -> ChipTabSample(dark)
                    "segment" -> SegmentedControlSample(dark)
                    "effect_backdrop" -> BlurTutorial(dark)
                    "effect_shadow" -> Group(title = stringResource(R.string.shadow_title)) {
                        if (Build.VERSION.SDK_INT >= 34) ShadowSamples(dark)
                        else EffectText(stringResource(R.string.requires_api_34))
                    }
                    "effect_fusion" -> RecordSwipeSamples(dark)
                    "effect_material" -> Group(title = stringResource(R.string.material_title)) {
                        if (Build.VERSION.SDK_INT >= 33) MaterialSample(dark)
                        else EffectText(stringResource(R.string.requires_api_33))
                    }
                    "wheel", "timepicker", "countdownpicker", "datepicker", "calendar" -> PickerSamples(page, dark)
                    "layout" -> LayoutSample()
                    "effect_gradient" -> Group(title = stringResource(R.string.effect_gradient_title)) {
                        if (Build.VERSION.SDK_INT >= 33) GradientBlurSample()
                        else EffectText(stringResource(R.string.requires_api_33))
                    }
                    "effect_lighting" -> Group(title = stringResource(R.string.effect_lighting_title)) {
                        if (Build.VERSION.SDK_INT >= 33) LightingSample(dark)
                        else EffectText(stringResource(R.string.requires_api_33))
                    }
                    "effect_motion" -> Group(title = stringResource(R.string.effect_motion_title)) {
                        MotionSample()
                    }
                    "effect_floating" -> Group(title = stringResource(R.string.effect_floating_title)) {
                        if (Build.VERSION.SDK_INT >= 34) FloatingSurfaceSample(dark)
                        else EffectText(stringResource(R.string.requires_api_34))
                    }
                    "appbar", "search", "navigation" -> ChromeDetail(page, dark)
                    "floatingtoolbar" -> FloatingToolbarSample(dark)
                    "cards" -> ActionCardSample(dark)
                    "choice" -> ChoiceCardSample(dark)
                    "thumbnail" -> ThumbnailChoiceSample(dark)
                    "notification" -> NotificationCardSample(dark)
                    "quicksetting" -> QuickSettingSample(dark)
                    "chart" -> BarChartSample(dark)
                    "dockbar" -> DockBarSample(dark)
                    "refresh" -> PullToRefreshSample(dark)
                    "accordion" -> AccordionSample(dark)
                    "avatar" -> AvatarSample(dark)
                    "statelayout" -> StateLayoutSample(dark)
                    "stepper" -> StepperSample(dark)
                    "fab" -> FabSample(preset = fabPreset, onPresetChange = { fabPreset = it },
                        rounded = roundedFab, onRoundedChange = { roundedFab = it })
                    "about" -> {
                        DemoAboutHero()
                        Group {
                            DemoPreference(title = stringResource(R.string.app_name), value = "1.0")
                            Divider()
                            DemoPreference(title = stringResource(R.string.settings_baseline), value = "ColorOS 17")
                        }
                    }
                }
            }
            if (page == "toast") {
                ToastHost(toastState, Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp))
            }
        }
    }
}

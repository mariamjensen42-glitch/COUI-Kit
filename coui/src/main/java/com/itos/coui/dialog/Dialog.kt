package com.itos.coui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.theme.CouiTheme

/** 受控操作。确认后的关闭、状态变更及业务提交由 onClick 的宿主处理。 */
data class CouiDialogAction(val text: String, val onClick: () -> Unit, val enabled: Boolean = true,
    val style: CouiDialogActionStyle = CouiDialogActionStyle.Secondary, val colors: CouiDialogActionColors? = null) {
    init {
        require(text.isNotBlank()) { "操作文案不能为空" }
        require(style != CouiDialogActionStyle.Primary || colors != null) { "主操作必须由宿主提供主题配色" }
    }
}

/**
 * 普通警告弹窗。两短按钮横排，单个、三个或放不下的按钮竖排。
 * content 可装配列表、输入和选择器；不提供推荐/AI Payment 专用按钮及原厂高内容标题合并协议。
 * CouiShape 自动提供轮廓半径；其他 Shape 使用材质时须显式提供 cornerRadius。
 * visible 非空时由库保留退场窗口，调用须留在组合中；null 使用外层条件和系统窗口动画。
 */
@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmAction: CouiDialogAction,
    dark: Boolean = CouiTheme.isDark,
    title: String? = null,
    message: String? = null,
    dismissAction: CouiDialogAction? = null,
    neutralAction: CouiDialogAction? = null,
    placement: CouiDialogPlacement = CouiDialogPlacement.Bottom,
    colors: CouiDialogColors = CouiDialogDefaults.themeColors(dark),
    shape: Shape = CouiTheme.shapes.dialog,
    cornerRadius: Dp = Dp.Unspecified,
    material: CouiDialogMaterial? = null,
    backdrop: CouiBackdrop? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    visible: Boolean? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    CouiDialogWindow(modifier, onDismissRequest, colors, dark, placement, shape, cornerRadius, properties, material, backdrop, visible) {
        val configuration = LocalConfiguration.current
        var messageLines by remember(message) { mutableIntStateOf(1) }
        var stacked by remember { mutableIntStateOf(if (neutralAction != null) 1 else 0) }
        val tall = messageLines > 1 || (stacked == 1 && (dismissAction != null || neutralAction != null))
        Column(Modifier.fillMaxWidth()) {
            // 对应正文/标题的滚动区与独立 buttonPanel；操作不随长文滚出窗口。
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                if (!title.isNullOrEmpty()) Box(Modifier.fillMaxWidth().padding(
                    start = CouiDialogDefaults.contentPadding, end = CouiDialogDefaults.contentPadding,
                    top = CouiDialogDefaults.titleTop, bottom = CouiDialogDefaults.titleBottom)) {
                    DialogText(text = title, kind = DialogTextKind.Title, color = colors.title, modifier = Modifier.fillMaxWidth()
                        .heightIn(max = CouiDialogDefaults.titleMaxHeight(configuration.screenHeightDp))
                        .verticalScroll(rememberScrollState()))
                }
                if (!message.isNullOrEmpty()) DialogText(text = message, kind = DialogTextKind.Message, color = colors.message, modifier =
                    Modifier.fillMaxWidth().padding(horizontal = CouiDialogDefaults.contentPadding)
                        .padding(top = if (title.isNullOrEmpty() && !tall) CouiDialogDefaults.messageTopWithoutTitle else 0.dp,
                            bottom = if (title.isNullOrEmpty() && !tall) CouiDialogDefaults.messageBottomWithoutTitle
                                else CouiDialogDefaults.messageBottom),
                    onLineCount = { if (messageLines != it) messageLines = it })
                content()
            }
            DialogActions(confirmAction, dismissAction, neutralAction, colors, dark, material != null) {
                val next = if (it) 1 else 0
                if (stacked != next) stacked = next
            }
        }
    }
}

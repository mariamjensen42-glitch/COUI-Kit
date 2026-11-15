package com.cycling.coui.dialog

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 安全弹窗的说明文案与勾选框度量。
 *
 * 取自原厂 `coui_security_alert_dialog_statement_or_checkbox.xml`：容器上下各留 8dp 与 16dp，
 * 说明文案 12sp、行距 1.2、左右 24dp，勾选框距左 20dp、距右 24dp。
 */
object CouiSecurityDialogDefaults {
    val statementTopPadding = 10.dp
    val statementBottomPadding = 6.dp
    val statementHorizontalPadding = 24.dp
    val panelTopPadding = 8.dp
    val panelBottomPadding = 16.dp
    val checkboxTopPadding = 4.dp
    val checkboxStartPadding = 20.dp
    val checkboxEndPadding = 24.dp
    val checkboxSpacing = 8.dp

    fun statementTextStyle(): TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 12.sp * 1.2f)

    fun checkboxTextStyle(): TextStyle = TextStyle(fontSize = 12.sp)
}

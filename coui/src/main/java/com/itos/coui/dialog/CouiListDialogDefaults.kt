package com.itos.coui.dialog

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 列表弹窗的交互形态。 */
enum class CouiListSelection { None, Single, Multi }

/** 列表弹窗的单项度量。取自原厂 `coui_list_dialog_item.xml` 与 `list_dialog_item.xml`。 */
object CouiListDialogDefaults {
    val itemMinHeight = 50.dp
    val itemHorizontalPadding = 20.dp
    val itemVerticalPadding = 16.dp
    val dividerThickness: Dp = Dp.Hairline
    val dividerHorizontalPadding = 20.dp
    val panelTopPadding = 16.dp
    val listBottomPadding = 30.dp

    fun itemTextStyle(): TextStyle = TextStyle(fontSize = 16.sp)

    fun summaryTextStyle(): TextStyle = TextStyle(fontSize = 12.sp)
}

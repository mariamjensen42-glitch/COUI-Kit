package com.cycling.coui.shape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 库内共享的轮廓 token。
 *
 * 取值来自 ColorOS 17 的 `coui_round_corner_*` 圆角刻度与各组件实际使用的形状：
 * 刻度从 `coui_round_corner_xs` 到 `coui_round_corner_xxxxl` 以 4dp 递增到 32dp，
 * 卡片族在 24dp 上改用权重 3 的平滑超椭圆，菜单 28dp、弹窗 45dp 是刻度外的组件取值。
 * 弹窗的 45dp 是 ColorOS 17 底部弹窗截图轮廓的拟合值（权重 3 的超椭圆，残差 2.7px）。
 * 胶囊与正圆不参与刻度，分别对应 Oplus `cornerType=4` 的 sdCapsule 与等宽高圆。
 *
 * 组件参数默认读取 [com.cycling.coui.theme.CouiTheme.shapes] 的同名 token，
 * 宿主整体替换时不需要逐个传入。各组件 Defaults 保留同值基线，供显式覆盖与文档引用。
 * 输入框 10dp、轻提示 12.67dp 等来自单个组件的资源（`coui_textinput_corner_radius`、
 * `coui_snack_bar_*`），不属于共享刻度，仍由各自 Defaults 持有。
 */
@Immutable
data class CouiShapes(
    /** `coui_round_corner_xs`：4dp，复选框容器。 */
    val extraSmall: Shape = CouiShape(4.dp),
    /** `coui_round_corner_s`：8dp，占位块。 */
    val small: Shape = CouiShape(8.dp),
    /** `coui_round_corner_m`：12dp，气泡。 */
    val medium: Shape = CouiShape(12.dp),
    /** `coui_round_corner_l`：16dp，圆角方形 FAB。 */
    val large: Shape = CouiShape(16.dp),
    /** `coui_round_corner_xl`：20dp，选择卡。 */
    val extraLarge: Shape = CouiShape(20.dp),
    /** 分组卡片族：24dp 平滑超椭圆，权重 3。 */
    val card: Shape = CouiShape(24.dp, 3f),
    /** 菜单与子菜单：28dp。 */
    val menu: Shape = CouiShape(28.dp),
    /** 底部面板：只圆上两角，32dp。 */
    val panel: Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    /** 弹窗：45dp 平滑超椭圆，权重 3。 */
    val dialog: Shape = CouiShape(45.dp, 3f),
    /** 整条胶囊。 */
    val capsule: Shape = CouiCapsuleShape,
    /** 正圆。 */
    val circle: Shape = CouiShape(999.dp, 2f),
) {
    companion object {
        /** 原生默认刻度；也是各组件 Defaults 与无主题回退的取值来源。 */
        val Default = CouiShapes()
    }
}

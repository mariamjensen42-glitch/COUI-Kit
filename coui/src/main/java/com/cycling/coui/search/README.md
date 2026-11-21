# Search

## Overview
COUI 搜索组件包：SearchBar 是设置式常驻搜索栏，展开时分裂出取消胶囊；CouiSearchBarState 以四个 CouiSpring 驱动宽度、取消、页面与结果进度。查询、展开目标与结果均由宿主持有；API 33+ 经 couiFloatingPair 绘制材质，低版本回退普通背景与聚光。

## Interface
- `public @Composable fun SearchBar(...)` - 常驻搜索栏与分裂取消胶囊 `[SearchBar.kt:SearchBar]`
- `public @Stable class CouiSearchBarState`（internal 构造）、`public @Composable fun rememberCouiSearchBarState(expanded)` - 展开目标状态与创建入口 `[CouiSearchBarState.kt:CouiSearchBarState]` `[CouiSearchBarState.kt:rememberCouiSearchBarState]`
- `public val CouiSearchBarState.expanded` / `active` / `contentAlpha` / `resultsAlpha` - 只读派生进度 `[CouiSearchBarState.kt:expanded]` `[CouiSearchBarState.kt:active]` `[CouiSearchBarState.kt:contentAlpha]` `[CouiSearchBarState.kt:resultsAlpha]`
- `public @Immutable data class CouiSearchBarColors`、`public object CouiSearchBarDefaults` + `colors(dark)` - 配色与尺寸/模糊常量 `[CouiSearchBarDefaults.kt:CouiSearchBarColors]` `[CouiSearchBarDefaults.kt:CouiSearchBarDefaults]` `[CouiSearchBarDefaults.kt:colors]`
- `internal object CouiSearchIcon` + `imageVector` - 组件自带 20dp 搜索图标 `[CouiSearchIcon.kt:CouiSearchIcon]` `[CouiSearchIcon.kt:imageVector]`
- `internal const val cancelTranslationPx` / `blurRadius` / `cancelContentBlurRadius` - 取消胶囊位移与离焦模糊原始 px `[CouiSearchBarDefaults.kt:cancelTranslationPx]` `[CouiSearchBarDefaults.kt:blurRadius]` `[CouiSearchBarDefaults.kt:cancelContentBlurRadius]`

## Invariants
- CouiSearchBarState 构造函数为 internal；expanded 对外只读，只能经 updateTarget 修改 `[CouiSearchBarState.kt:expanded]`
- 四个 CouiSpring 初值按 expanded 取 1000f 或 0f；updateTarget 在目标相同时直接返回，否则 running=true 并启动全部弹簧 `[CouiSearchBarState.kt:updateTarget]`
- width/cancel 用 animateTo(..., .5f, .25f)，page 用 .35f，results 进入 .35f/退出 .1f；updateRunning 汇总四路 isRunning `[CouiSearchBarState.kt:updateTarget]`
- rememberCouiSearchBarState 首次组合构造 state，之后 SideEffect 同步目标，DisposableEffect 在离开组合时 dispose `[CouiSearchBarState.kt:rememberCouiSearchBarState]`
- SearchBar 的 backdrop 取 LocalCouiToolbarBackdrop ?: LocalCouiBackdrop，均缺失时 checkNotNull 抛错；宿主持有 query/expanded/results `[SearchBar.kt:SearchBar]`
- enabled=false 时停止输入、点击与反馈并释放焦点；onQueryChange/onSearch/cancelSearch 均要求 enabled && expanded `[SearchBar.kt:SearchBar]`
- BasicTextField enabled=enabled && expanded、singleLine、ImeAction.Search；Search 动作先隐藏键盘再 onSearch `[SearchBar.kt:SearchBar]`
- 收起状态输入胶囊挂 contentDescription=placeholder 的展开语义，输入框 clearAndSetSemantics；清空按钮仅在 expanded && query 非空时出现 `[SearchBar.kt:SearchBar]`
- body 宽度=maxWidth-(cancelWidth+gap)*widthProgress，至少 1px；取消按钮仅在 active && cancelProgress>0 时放置且 zIndex=1 `[SearchBar.kt:SearchBar]`
- API>=33 走 couiFloatingPair(drawOrder=[1,0], foregroundComposition=true)，低于 33 回退 background+clip+spotlight `[SearchBar.kt:SearchBar]`
- state.active 期间 normalShadow 冻结在进入前值；RTL 下位移取反；取消文字样式按 fontScale 与 SDK 分支 `[SearchBar.kt:SearchBar]`

## Side Effects
- 读取 LocalCouiToolbarBackdrop、LocalCouiBackdrop、LocalCouiContourGlowEnabled、LocalDensity、LocalLayoutDirection、LocalConfiguration、LocalFocusManager、LocalSoftwareKeyboardController `[SearchBar.kt:SearchBar]`
- FocusRequester 调用 keyboard.show()/hide() 与 clearFocus；挂 clickable/couiTouchMotion/couiPressMotion/couiToolbarFeedback 并共用 MutableInteractionSource `[SearchBar.kt:SearchBar]`
- graphicsLayer、rememberCouiSpotlight、couiFloatingPair/CouiFloatingRenderer、rememberGraphicsLayer 绘制；RenderEffect.createBlurEffect 模糊取消文字，Typeface.create 分支 `[SearchBar.kt:SearchBar]`
- CouiSearchBarState 的 CouiSpring 动画与 mutableStateOf；CouiSearchIcon 以 lazy 缓存黑/白 ImageVector 单例 `[CouiSearchBarState.kt:updateTarget]` `[CouiSearchIcon.kt:imageVector]`
- 语义：contentDescription/Role.Button，收起时 clearAndSetSemantics `[SearchBar.kt:SearchBar]`

## Verification
- Run: `./gradlew :coui:lintDebug`

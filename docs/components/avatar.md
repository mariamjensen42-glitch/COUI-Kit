# 👤头像

`Avatar` 是固定尺寸、可裁剪的头像容器：图片、图标、首字母都放进去，组件只负责尺寸、轮廓和底色。

它不做图片加载、占位图、首字母取字或在线状态角标——这些属于业务与图片库职责，需要角标时用 `BadgedBox` 包一层。

## 最小调用

~~~kotlin
Avatar(containerColor = scheme.accent) {
    Text("城", kind = CouiTextKind.ItemTitle, color = scheme.onAccent)
}
~~~

图片内容自己负责填充：

~~~kotlin
Avatar(size = CouiAvatarDefaults.largeSize) {
    Image(painter, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
}
~~~

## 参数

- `size`：默认 40dp，常用尺寸见 `CouiAvatarDefaults`（32dp / 40dp / 56dp）。
- `shape`：默认圆形；需要圆角方形时传 `CouiShape(12.dp)`。
- `containerColor`：底色，默认取主题 `container`；按联系人色板着色时直接覆盖。
- `content`：`BoxScope` 内容，居中摆放。

源码见 [Avatar](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/avatar/Avatar.kt)。

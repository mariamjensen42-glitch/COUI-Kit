# 发布渠道与开发快照

公开版本由 JitPack 响应发行 Tag 自动构建，开发快照由独立的 GitHub Packages 工作流手动发布。推送 `v` 开头的 Tag 后，JitPack 按仓库根的 [jitpack.yml](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/jitpack.yml) 构建并发布三个库模块。

## 渠道与版本来源

| 渠道 | 用途 | 来源与注意事项 |
| --- | --- | --- |
| JitPack | 公开发行的预览版与正式版 | 响应 `v` 开头的 Tag 与分支构建；坐标 `com.github.mariamjensen42-glitch.COUI-Kit:coui:<tag>`（图标与业务模块同理，替换 artifactId），接入版本见 [README](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/README.md) |
| GitHub Packages | 手动发布当前提交的开发快照 | [publish-preview.yml](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/.github/workflows/publish-preview.yml) 生成带短 SHA 的 `-SNAPSHOT`，接入需认证 |
| 本地文件仓库 | CI/开发检查 AAR、POM、Module Metadata、源码包及 Dokka | 各模块的 `build/repository`；不上传远端，不视为真实发行 |

使用者优先选择 JitPack 上已验证的 Tag；需要针对具体 Commit 协作调试时才使用 GitHub Packages 快照。不同渠道不可在同一模块重复引入。

## GitHub Packages 手动快照发布

需要让宿主验证当前 COUI 提交时，在 Actions 手动运行“发布库预览版”。成功后，从运行摘要复制 Maven 坐标交给接入者；用户侧配置见[GitHub Packages 接入](../guide/github-packages.md)。

### 手动发布步骤

1. 打开 Actions → 发布库预览版 → Run workflow，选择已经评审的受信任分支。日常使用 main；手动选择分支等于授权执行该分支的构建脚本。
2. 等待本地 Maven 产物检查、上传和远端元数据读取全部成功。
3. 从运行摘要复制完整依赖坐标，并记录对应源码提交与 Actions run 地址。

普通分支 push 和 `[ci]` push 均不触发库发布。Demo 打包仍按 [CI 指南](ci.md)执行。该工作流只允许在拥有 Packages 的仓库运行，不使用 PR 事件，不把发布凭据提供给 fork PR。

## 版本与产物

`coui`、`coui-icons` 与 `coui-blocks` 同版本、同步发布，正式版、预览版和开发快照均遵守此规则。即使某个模块没有代码变化，也随其他模块发布对应版本。图标模块与业务模块依赖同版本核心；只有三个坐标均可下载，才视为该版本发布完成。

库 group、核心 artifactId 和开发基础版本由根目录 [gradle.properties](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/gradle.properties) 持有。图标模块与业务模块分别在[图标模块](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui-icons/build.gradle.kts)和[业务模块](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui-blocks/build.gradle.kts)显式指定 artifactId。GitHub Packages 在开发版本中加入提交短 SHA；JitPack 由 Tag 去掉首字母 `v` 得到版本，并在 [jitpack.yml](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/jitpack.yml) 通过 `-PGROUP`、`-PVERSION_NAME` 同时覆盖三个模块的 group 与版本，不修改开发基础版本。库版本不读取 Demo versionCode 或显示版本名。

[核心配置](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/build.gradle.kts)、[图标模块配置](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui-icons/build.gradle.kts)与[业务模块配置](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui-blocks/build.gradle.kts)均只发布 release 变体，包含 AAR、源码包、Dokka HTML 文档包、POM 和 Gradle Module Metadata。源码与文档归档附带根许可、来源与名称声明；核心与图标模块归档包含 ICON_SOURCES.md，核心还包含既有 Skia/AndroidX 声明。不发布 Demo、不提前混淆库，也不上传 Demo 密钥。

发布前先生成三个模块的本地 Maven 文件仓库，调用与 PR 相同的[真实产物检查](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/.github/scripts/verify-maven-artifacts.cjs)。脚本检查独立坐标、非空产物、源码与 HTML、许可、Compose BOM、核心动画依赖的版本声明，以及各模块依赖边界。全部通过后 Packages 才上传并读取远端版本元数据。Gradle 的版本约束以 `.module` 为准。每次发行仍须确认对应运行结果，不能以历史成功代替当前版本验收。

Packages 按分支串行执行，不取消正在上传的任务；JitPack 在 Tag 推送后自行排队构建。重新运行相同提交的 Packages 任务仍是 SNAPSHOT 发布；SHA 只能标识源码，不把 SNAPSHOT 当作不可变发行版本。

## 发行与变更记录

先将发布工作流与待发行改动合入 main，确认该提交的 PR 检查通过，再创建尚未发布的版本标签。Tag 必须指向 main 历史中的提交；不要在未合入的开发分支发布。JitPack 只认 Tag 名，构建记录与日志在 [JitPack 构建页](https://jitpack.io/com/github/mariamjensen42-glitch/COUI-Kit)查看。

发行只使用 Alpha 和正式 Release；Release 内容手写。先创建 GitHub 预发布草稿，Tag 推送会独立触发 JitPack 构建。GitHub Release 可在发行内容就绪后公开，不等待 JitPack 构建完成。正式版不带预览后缀，并取消 GitHub 的 Pre-release 标记。

Release 正文按 `demo` 和实际有改动的库模块分组。每条以“新增”“优化”“重构”“移除”“调整”或其他符合变更的动词开头。`CHANGELOG.md` 的版本条目只记录库模块的变更及升级说明。

例如准备发布 `0.0.1-alpha5`：

```sh
git switch main
git pull --ff-only
git tag -a v0.0.1-alpha5 -m "发布 0.0.1-alpha5"
git push origin v0.0.1-alpha5
```

支持 `v主版本.次版本.补丁版本` 和 `-alphaN` 后缀，例如 `v0.1.0`、`v0.1.0-alpha1`。版本数字不带多余前导零，不使用 Beta、RC、SNAPSHOT、构建元数据或 Commit 标题作为 Tag 名。

推送后等待 JitPack 状态变为 ok，再核对三个坐标的 POM、AAR 与 Module Metadata 是否可下载；构建失败可直接在 JitPack 页面重新构建同一 Tag，不需要移动标签。

建立新版本时，至少完成：

1. 为确认过的发行源码建立 Git Tag，记录 Commit SHA，并让版本与构建产物一一对应。
2. 使用独立消费者确认选定版本能在声明的 SDK/AGP/Compose 组合下构建和运行，并记录实际矩阵。
3. 在 [CHANGELOG](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/CHANGELOG.md) 记录用户可见能力、API 兼容性、迁移说明与发布日期；同步 README、快速开始和兼容表中的版本号。
4. 正式版、Alpha 与 SNAPSHOT 分开标识；不把快照重发视为不可变版本。

## 凭据与本地检查入口

GitHub Packages 工作流使用自身的 `GITHUB_TOKEN`，仅上传与远端读取步骤接收凭据；job 授予 contents: read、packages: write。该渠道不使用 Demo 签名或 GPG Secrets，也不需要 Central 凭据。JitPack 由平台按 Tag 构建，仓库不配置发布密钥。

获得当前任务的本地构建授权后，可在根目录仅生成文件仓库，不需要任何上传凭据：

```sh
./gradlew :coui:publishAllPublicationsToLocalRepository :coui-blocks:publishAllPublicationsToLocalRepository :coui-icons:publishAllPublicationsToLocalRepository --no-configuration-cache
node .github/scripts/verify-maven-artifacts.cjs
```

本地 Maven 目标用于检查发行结构，不会切换 Demo 或外部宿主的依赖。PR Android 检查也会生成这个目标，验证库打包和文档生成；不执行远端上传或单测。

三个模块分别提供 `publishAllPublicationsToGitHubPackagesRepository` 上传任务。Gradle 按仓库名读取 GitHubPackagesUsername 和 GitHubPackagesPassword；手动本地上传须另有当前任务授权。工作流通过同名 ORG_GRADLE_PROJECT 环境属性提供它们，仓库地址指向 `mariamjensen42-glitch/COUI-Kit`。

## 失败处理

本地构建或产物检查失败时不会上传。上传失败可能已留下部分文件，先检查失败步骤和远端状态，再决定重跑；不以工作流启动或局部上传成功作为已发布。Packages 上传后读取失败时保留失败状态，核对远端版本，避免误报成功。

JitPack 构建失败时查看该 Tag 的构建日志，修复后发布新 Tag；已经可下载的版本不覆盖，也不移动旧 Tag。私有包与 JitPack 缓存的保留、清理按各平台实际策略处理。接入坐标见[快速开始](../guide/getting-started.md)。

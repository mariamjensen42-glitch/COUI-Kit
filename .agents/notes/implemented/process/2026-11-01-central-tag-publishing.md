# Agent Note: Maven Central 按版本 Tag 自动发行

Status: implemented

## Problem

两模块已有可验证的 Maven 产物，公开发行最初由 Central 承担：推送发行 Tag 后签名、上传并等待 Portal 发布。该渠道要求维护者持有 Portal User Token 与 ASCII-armored GPG 私钥并完成公钥分发，凭据缺失时整条发行链路不可用；实际使用中这些凭据从未配置，Tag 推送只换来一个缺凭据的失败记录，发布动作仍由维护者在本地串联。

## Decision

2026-11-15 起下线 Central 渠道，公开发行改由 JitPack 按 Tag 构建承担：

- 删除 `publish-central.yml` 与 `central-release-version.cjs`，PR 检查不再执行 Tag 版本解析器自检。Tag 名只由发布指南约定，不再有脚本闸门。
- JitPack 读根目录 `jitpack.yml`，用 `-PGROUP=com.github.mariamjensen42-glitch.COUI-Kit`、`-PVERSION_NAME=<tag 去掉首字母 v>` 同时覆盖两模块，无需仓库内的任何发布凭据或签名密钥。
- Vanniktech 插件保留：GitHub Packages 快照上传仍依赖它产出 POM、源码包与 Dokka 归档，删除插件会让快照渠道一起失效。
- GitHub Packages 的仓库地址、POM 的 url/scm 与工作流 gate 统一指向 `mariamjensen42-glitch/COUI-Kit`；校验步骤从 `GITHUB_REPOSITORY` 小写派生主机路径，避免再次硬编码所有者。

代价是公开版本不可覆盖、不可撤销，且没有中央仓库的签名与索引；收益是发行链路上没有维护者私有凭据，Tag 推送后消费者即可解析坐标。使用说明归[发布指南](../../../../docs/development/publishing.md)。

## Evidence

- [JitPack 自定义命令](https://jitpack.io/docs/BUILDING/#custom-commands)规定 `jitpack.yml` 里的 Gradle 调用即发布入口，坐标固定为 `com.github.<owner>.<repo>`。
- [GitHub Packages 认证](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)要求仓库地址与包所有者一致，`GITHUB_TOKEN` 需 packages: write。
- 历史实现（Central 工作流、Portal User Token 与 GPG 属性注入、`publishAndReleaseToMavenCentral` 共享部署、版本解析器自检）在 `v0.0.1-alpha13` 及以前的提交里可查，`publish-central.yml` 因 gate 只允许上游仓库而在本仓库一直 skipped。

## Existing-note audit

- [GitHub Packages 预览发布](2026-10-25-github-packages-preview.md)：部分重叠，本改动用它继续持有快照渠道；已同步更新该篇对公开渠道的描述。
- [Demo 签名与 CI](2026-10-25-demo-signing-ci.md)：无关，Demo 打包与签名不受影响。
- 开发指南与快速开始属于接入说明，已在同一改动里改为 JitPack 坐标，不另立提案。

## Alternatives considered

- **保留 Central 工作流，等凭据就绪再用。** 工作流存在即暗示可用，且 PR 检查要继续跑 Tag 版本自检；凭据缺失时失败记录会掩盖真实发行状态。
- **同时保留 JitPack 与 Central 双渠道。** 同一版本号会在两处出现不同校验状态，接入文档要维护两套坐标；公开版本只需一个可解析来源。
- **去掉 Vanniktech 插件改手写 maven-publish。** 需要自行实现 POM、源码包与 Dokka 归档，与 Packages 现有链路重复，且不改变对外坐标。

## Consequences

Tag 推送后必须在 JitPack 页面确认构建为 ok 并核对两个坐标可下载，不再有 Portal 的发布状态可查；构建失败就发新 Tag，不移动旧标签。GitHub Packages 仍按提交 SHA 发布快照，`io.github.itoseo` 这一 group 由 `gradle.properties` 持有，与 JitPack 的 `com.github.mariamjensen42-glitch.COUI-Kit` 并存但不混用。仓库内不再有需要轮换的发布密钥，`MAVEN_CENTRAL_*` 与 `GPG_*` Secrets 可删除。

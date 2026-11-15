# 跨包组件规格一致性比对

由 `tools/extract_component_spec.py --compare` 生成；只比对各包**共有**的规格键。

- 输入：`phonemanager-button.json`, `settings-button.json`, `systemui-button.json`
- 共有键：494
- 差异键：0

- `phonemanager` 独有键：81（该包额外内嵌的样式/资源，不是差异）
- `settings` 独有键：64（该包额外内嵌的样式/资源，不是差异）
- `systemui` 独有键：282（该包额外内嵌的样式/资源，不是差异）

## 差异明细

无。三包共有的静态规格完全一致，维护一套组件规格即可。

> 独有键只说明某包内嵌了额外变体，不能据此推断另一包缺少该能力；
> 是否为目标页面实际使用，仍需按 `docs/development/replication-guide.md` 找到消费者。

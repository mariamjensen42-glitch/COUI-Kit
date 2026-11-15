#!/usr/bin/env python3
"""从 apktool 反编译结果中抽取「组件级」原厂设计规格证据。

与 references/apk/<pkg>/extract_tokens.py 的分工：
  - extract_tokens.py 产出全局 token 词表（colors / dimens / styles / theme attrs）。
  - 本脚本产出某个组件的规格链：样式继承 -> 状态色 -> 几何 -> 动效，
    把 `@dimen/` `@color/` `?attr/` 递归解析成字面值，并逐项记录来源文件与行号。

只做只读解析，不修改 references/apk/ 下任何材料。输出为可复跑的证据快照，
不是组件文档；实现常量仍以源码与 KDoc 为唯一维护位置。

用法：
    python tools/extract_component_spec.py <decoded_dir> --component Button \
        --match 'button|btn' --label settings --out references/specs

跨包一致性比对（不重新抽取）：
    python tools/extract_component_spec.py --compare references/specs/*-button.json

可选：
    --match       资源名/样式名匹配正则（默认取 --component 的小写形式）
    --style-prefix 样式族前缀，默认 Widget.COUI.<component>
    --exclude     排除的资源名正则（默认去掉 Material / 单选 / 复选框等噪声）
    --label       产物文件名前缀，通常为 APK 包名标签
    --out         输出目录（默认 references/specs）
    --device-sdk  叠加 values-vN 时的目标 API（默认 37，ColorOS 17 / Android 17）
"""

from __future__ import annotations

import argparse
import json
import re
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict, deque
from pathlib import Path

ANDROID = "{http://schemas.android.com/apk/res/android}"
VALUE_TAGS = ("color", "dimen", "bool", "integer", "string")
UNRESOLVED = "?"
NAME_LINE_RE = re.compile(r'<(?:color|dimen|bool|integer|string|style|item)\b[^>]*\bname="([^"]+)"')


def rel(path: Path, base: Path) -> str:
    try:
        return path.relative_to(base).as_posix()
    except ValueError:
        return path.as_posix()


def line_index(path: Path) -> dict[str, int]:
    """apktool 输出每节点一行，用逐行正则建立 name -> 行号（ElementTree 不保留行号）。"""
    out: dict[str, int] = {}
    try:
        for i, line in enumerate(path.read_text(encoding="utf-8", errors="replace").splitlines(), 1):
            m = NAME_LINE_RE.search(line)
            if m and m.group(1) not in out:
                out[m.group(1)] = i
    except OSError:
        pass
    return out


def style_item_lines(path: Path) -> dict[tuple[str, str], int]:
    """apktool 的 <item> 分散在多个 style 里，name 会重复。按所属 style 精确定位行号。"""
    out: dict[tuple[str, str], int] = {}
    try:
        lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    except OSError:
        return out
    current = None
    for i, line in enumerate(lines, 1):
        m = re.search(r'<style\b[^>]*\bname="([^"]+)"', line)
        if m:
            current = m.group(1)
            continue
        m = re.search(r'<item\b[^>]*\bname="([^"]+)"', line)
        if m and current:
            out.setdefault((current, m.group(1)), i)
    return out


def selector_default_color(path: Path):
    """ColorStateList 没有 <color name=...> 定义，取无状态 item 的 android:color 作为默认色。"""
    if not path.is_file():
        return None
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return None
    if root.tag != "selector":
        return None
    for item in reversed(list(root)):
        if item.tag != "item":
            continue
        flags = [k for k in item.attrib if k.startswith(ANDROID) and k != ANDROID + "color"]
        if not flags:
            return item.get(ANDROID + "color")
    return None


class ResourceTable:
    """按配置限定符叠加后的资源表：{type: {name: {"value", "source"}}}。"""

    def __init__(self, res: Path, device_sdk: int):
        self.res = res
        self.device_sdk = device_sdk
        self.light: dict[str, dict[str, dict]] = defaultdict(dict)
        self.night: dict[str, dict[str, dict]] = defaultdict(dict)
        self.styles: dict[str, dict] = {}
        self.variant_rows: list[tuple[str, str, str, str]] = []
        self._load()

    def _dirs(self):
        base, versions, nights, others = [], [], [], []
        for d in sorted((p for p in self.res.glob("values*") if p.is_dir()), key=lambda p: p.name):
            name = d.name
            if name == "values":
                base.append(d)
            elif "night" in name:
                nights.append(d)
            elif (m := re.search(r"-v(\d+)$", name)):
                if int(m.group(1)) <= self.device_sdk:
                    versions.append((int(m.group(1)), d))
            else:
                others.append(d)
        versions.sort(key=lambda t: t[0])
        return base, [d for _, d in versions], nights, others

    def _load(self):
        base, versions, nights, others = self._dirs()
        for d in base + versions:
            self._read_dir(d, self.light, styles=True)
        for d in nights:
            # 夜间只覆盖颜色/尺寸值；COUI 17 的 values-night/styles.xml 不重定义组件样式，
            # 因此不让夜间 style 污染样式继承链与主题属性表。
            self._read_dir(d, self.night, styles=False)
        # 夜间表以浅色为底，再叠加夜间覆盖；这样 ?attr 与 @color 在夜间也能解析。
        for kind in VALUE_TAGS:
            merged = dict(self.light[kind])
            merged.update(self.night[kind])
            self.night[kind] = merged
        for d in others:
            self._read_variants(d)

    def _read_variants(self, d: Path):
        """sw/w/h 等尺寸限定符变体按行保留，不叠加进主值（取决于具体设备）。"""
        for f in sorted(d.glob("*.xml")):
            try:
                root = ET.parse(f).getroot()
            except ET.ParseError:
                continue
            idx = line_index(f)
            for el in root:
                name = el.get("name")
                if el.tag in VALUE_TAGS and name:
                    self.variant_rows.append(
                        (el.tag, name, (el.text or "").strip(),
                         f"{rel(f, self.res.parent)}:{idx.get(name, 0)}"))

    def _read_dir(self, d: Path, target, styles: bool):
        for f in sorted(d.glob("*.xml")):
            try:
                root = ET.parse(f).getroot()
            except ET.ParseError:
                continue
            idx = line_index(f)
            item_lines = style_item_lines(f) if styles else {}
            for el in root:
                name = el.get("name")
                if not name:
                    continue
                src = f"{rel(f, self.res.parent)}:{idx.get(name, 0)}"
                if el.tag in VALUE_TAGS:
                    target[el.tag][name] = {"value": (el.text or "").strip(), "source": src}
                elif el.tag == "style" and styles:
                    items = {}
                    for it in el:
                        if it.tag == "item" and it.get("name"):
                            k = it.get("name")
                            items[k] = {
                                "value": (it.text or "").strip(),
                                "source": f"{rel(f, self.res.parent)}:"
                                          f"{item_lines.get((name, k), idx.get(name, 0))}",
                            }
                    entry = {"parent": el.get("parent"), "items": items, "source": src}
                    if name in self.styles:
                        self.styles[name]["items"].update(items)
                    else:
                        self.styles[name] = entry

    def get(self, table, kind: str, name: str):
        return table.get(kind, {}).get(name)


def style_chain(styles: dict, name: str, seen=None) -> list[str]:
    seen = seen or set()
    if name in seen or name not in styles:
        return []
    seen.add(name)
    parent = styles[name].get("parent") or ""
    parent = parent.split("/")[-1].lstrip("@")
    chain = style_chain(styles, parent, seen) if parent else []
    return chain + [name]


def merged_style(styles: dict, name: str) -> dict[str, dict]:
    out: dict[str, dict] = {}
    for step in style_chain(styles, name):
        out.update(styles[step]["items"])
    return out


def theme_attributes(styles: dict) -> dict[str, dict]:
    """Theme.COUI 沿 parent 链合并出的主题属性，用于解析 ?attr 引用。

    只取 Theme.COUI 本身的继承链，不并入 Theme.COUI.* 子类变体：
    后者是换主色的分支，合并会污染基础值（实测会得到 405 项而非 187 项）。
    """
    if "Theme.COUI" not in styles:
        return {}
    out: dict[str, dict] = {}
    for step in style_chain(styles, "Theme.COUI"):
        out.update(styles[step]["items"])
    return out


def resolve(value: str, table: ResourceTable, which: str, theme: dict[str, dict], seen=None) -> dict:
    """递归展开 @type/name、?attr/name、?attrName，返回 {literal, chain, resolved}。"""
    seen = set(seen or ())
    raw = (value or "").strip()
    chain: list[str] = []
    cur = raw
    for _ in range(12):
        chain.append(cur)
        if not cur or cur in ("@null", "@empty"):
            return {"literal": cur, "chain": chain, "resolved": True}
        if cur.startswith("?"):
            name = cur.lstrip("?").split("/")[-1]
            entry = theme.get(name)
            if not entry or entry["value"] in seen:
                return {"literal": cur, "chain": chain, "resolved": False}
            seen.add(entry["value"])
            cur = entry["value"]
            continue
        m = re.fullmatch(r"@(?:android:)?(color|dimen|bool|integer|string)/(.+)", cur)
        if m:
            kind, name = m.group(1), m.group(2)
            entry = table.get(table.light if which == "light" else table.night, kind, name)
            if not entry and kind == "color":
                # ColorStateList 定义在 res/color/*.xml，不在 values/colors.xml。
                default = selector_default_color(table.res / "color" / f"{name}.xml")
                if default:
                    cur = default
                    continue
                return {"literal": f"<selector:{name}>", "chain": chain,
                        "resolved": True, "selector": name}
            if not entry or entry["value"] in seen:
                return {"literal": cur, "chain": chain, "resolved": False}
            seen.add(entry["value"])
            cur = entry["value"]
            continue
        return {"literal": cur, "chain": chain, "resolved": True}
    return {"literal": cur, "chain": chain, "resolved": False}


def parse_selector(path: Path, table: ResourceTable, which: str, theme: dict[str, dict]):
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return []
    if root.tag != "selector":
        return []
    states = []
    for item in root:
        if item.tag != "item":
            continue
        flags = {k.replace(ANDROID, "android:"): v for k, v in item.attrib.items()
                 if k.startswith(ANDROID) and k != ANDROID + "color"}
        label = "default"
        if flags.get("android:state_enabled") == "false":
            label = "disabled"
        elif flags.get("android:state_pressed") == "true":
            label = "pressed"
        elif flags.get("android:state_selected") == "true":
            label = "selected"
        elif flags.get("android:state_checked") == "true":
            label = "checked"
        elif flags.get("android:state_focused") == "true":
            label = "focused"
        color = item.get(ANDROID + "color")
        states.append({
            "state": label,
            "flags": flags,
            "raw": color,
            "resolved": resolve(color, table, which, theme),
        })
    return states


def parse_motion(res: Path, pattern: re.Pattern, table: ResourceTable, theme: dict[str, dict]):
    """收集与组件相关的 anim / interpolator 参数，不解析 native 与 Java 侧物理参数。"""
    anims, interpolators = [], []
    for sub in ("anim", "animator"):
        d = res / sub
        if not d.is_dir():
            continue
        for f in sorted(d.glob("*.xml")):
            if not pattern.search(f.stem):
                continue
            try:
                root = ET.parse(f).getroot()
            except ET.ParseError:
                continue
            params = {}
            for el in root.iter():
                for k, v in el.attrib.items():
                    key = k.replace(ANDROID, "android:")
                    if key in ("android:duration", "android:startOffset", "android:interpolator",
                               "android:fromAlpha", "android:toAlpha", "android:fromXScale",
                               "android:toXScale", "android:fromYScale", "android:toYScale",
                               "android:valueFrom", "android:valueTo", "android:propertyName",
                               "android:repeatCount", "android:repeatMode"):
                        params.setdefault(key, []).append(v)
            if params:
                anims.append({"name": f.name, "source": rel(f, res.parent), "params": params,
                              "root_tag": root.tag})
    d = res / "interpolator"
    if d.is_dir():
        for f in sorted(d.glob("*.xml")):
            if not pattern.search(f.stem):
                continue
            try:
                root = ET.parse(f).getroot()
            except ET.ParseError:
                continue
            controls = {}
            for k, v in root.attrib.items():
                key = k.replace(ANDROID, "android:")
                if key.startswith("android:control") or key in ("android:factor", "android:cycles"):
                    controls[key] = {"raw": v, "resolved": resolve(v, table, "light", theme)}
            if controls:
                interpolators.append({"name": f.name, "source": rel(f, res.parent),
                                      "tag": root.tag, "controls": controls})
    return anims, interpolators


REF_RE = re.compile(
    r"@(?:android:)?(color|dimen|bool|integer|string)/([A-Za-z0-9_.]+)"
    r"|\?(?:android:attr/|attr/)?([A-Za-z0-9_.]+)"
)


def refs_in(value: str):
    """拆出资源引用与主题属性引用。"""
    res_refs, attrs = set(), set()
    for m in REF_RE.finditer(value or ""):
        if m.group(1):
            res_refs.add((m.group(1), m.group(2)))
        else:
            attrs.add(m.group(3))
    return res_refs, attrs


def reference_closure(res: Path, table: ResourceTable, theme: dict[str, dict], initial: list[str]):
    """从样式 item 出发，沿 @type/ 与 ?attr/ 递归求出该组件真正消费的资源集合。

    只沿引用链走，避免把「名字里含 btn 但属于 Material / 单选 / 复选框」的资源整包倒进来。
    """
    pending: deque[tuple[str, str]] = deque()
    attr_seen: set[str] = set()

    def push(value: str):
        res_refs, attrs = refs_in(value)
        for r in res_refs:
            pending.append(r)
        for a in attrs:
            if a in attr_seen:
                continue
            attr_seen.add(a)
            entry = theme.get(a)
            if entry:
                push(entry["value"])

    for v in initial:
        push(v)

    seen: set[tuple[str, str]] = set()
    selector_names: set[str] = set()
    while pending:
        kind, name = pending.popleft()
        if (kind, name) in seen:
            continue
        seen.add((kind, name))
        if kind == "color":
            f = res / "color" / f"{name}.xml"
            if f.is_file():
                selector_names.add(name)
                for which in ("light", "night"):
                    for row in parse_selector(f, table, which, theme):
                        push(row["raw"] or "")
        for which in ("light", "night"):
            entry = table.get(table.light if which == "light" else table.night, kind, name)
            if entry:
                push(entry["value"])
    return seen, selector_names, sorted(attr_seen)


def read_source_meta(decoded: Path) -> dict:
    """从同包 README.md 抓构建指纹；抓不到就留空，不补猜测值。"""
    meta = {"apk": "", "version_name": "", "version_code": "", "build": "", "sha256": "",
            "device": "", "note": ""}
    readme = decoded.parent / "README.md"
    if not readme.exists():
        meta["note"] = "同包 README.md 不存在，来源元数据缺失"
        return meta
    text = readme.read_text(encoding="utf-8", errors="replace")
    def grab(label):
        m = re.search(rf"\|\s*{re.escape(label)}\s*\|([^|]*)\|", text)
        return m.group(1).strip().replace("`", "").strip() if m else ""
    meta["apk"] = grab("包名")
    meta["version_name"] = re.sub(r"^versionName\s*", "", grab("版本").split("/")[0].strip())
    meta["build"] = grab("构建")
    meta["device"] = grab("设备")
    m = re.search(r"\|\s*SHA-256\s*\|\s*`?([0-9a-fA-F]{64})`?\s*\|", text)
    if m:
        meta["sha256"] = m.group(1)
    return meta


def build_spec(decoded: Path, component: str, pattern: re.Pattern, device_sdk: int,
               style_prefix: str, exclude: re.Pattern) -> dict:
    res = decoded / "res"
    if not res.is_dir():
        raise SystemExit(f"未找到 res 目录：{res}")
    table = ResourceTable(res, device_sdk)
    theme = theme_attributes(table.styles)

    # 样式族以精确前缀为准，避免把 Material / 单选 / 复选框的同名资源整包捞进来。
    style_names = sorted(n for n in table.styles
                         if n == style_prefix or n.startswith(style_prefix + "."))
    if not style_names:
        style_names = sorted(n for n in table.styles
                             if pattern.search(n) and not exclude.search(n))
    styles = {}
    for name in style_names:
        items = merged_style(table.styles, name)
        rows = {}
        for k, v in items.items():
            rows[k] = {
                "raw": v["value"],
                "source": v["source"],
                "light": resolve(v["value"], table, "light", theme),
                "night": resolve(v["value"], table, "night", theme),
            }
        styles[name] = {"parent": table.styles[name].get("parent"),
                        "chain": style_chain(table.styles, name),
                        "source": table.styles[name]["source"], "items": rows}

    # 只沿样式链求引用闭包：这才是「该组件真正消费」的资源集合。
    initial = [v["value"] for n in style_names for v in merged_style(table.styles, n).values()]
    refs, selector_names, attr_names = reference_closure(res, table, theme, initial)

    variants = defaultdict(list)
    for kind, name, value, src in table.variant_rows:
        variants[name].append({"type": kind, "value": value, "source": src})

    state_colors = {}
    for name in sorted(selector_names):
        f = res / "color" / f"{name}.xml"
        light, night = parse_selector(f, table, "light", theme), parse_selector(f, table, "night", theme)
        if light or night:
            state_colors[name] = {"source": rel(f, res.parent), "light": light, "night": night}

    colors, dimens = {}, {}
    for kind, name in sorted(refs):
        light = table.light.get(kind, {}).get(name)
        if not light:
            continue
        night = table.night.get(kind, {}).get(name)
        entry = {
            "light": {**light, "resolved": resolve(light["value"], table, "light", theme)},
            "night": ({**night, "resolved": resolve(night["value"], table, "night", theme)}
                      if night else None),
        }
        if kind == "color":
            colors[name] = entry
        else:
            dimens[name] = {"type": kind, **entry, "variants": variants.get(name, [])}

    anims, interpolators = parse_motion(res, pattern, table, theme)
    anims = [a for a in anims if not exclude.search(a["name"])]
    interpolators = [i for i in interpolators if not exclude.search(i["name"])]

    related_names = {
        "styles": [n for n in sorted(table.styles) if pattern.search(n) and n not in styles
                   and not exclude.search(n)],
        "colors": [n for n in sorted(table.light.get("color", {})) if pattern.search(n)
                   and n not in colors and not exclude.search(n)],
        "dimens": [n for n in sorted(table.light.get("dimen", {})) if pattern.search(n)
                   and n not in dimens and not exclude.search(n)],
    }

    unresolved = []
    for name, s in styles.items():
        for k, row in s["items"].items():
            if not row["light"]["resolved"] or not row["night"]["resolved"]:
                unresolved.append({"where": f"style:{name}/{k}", "raw": row["raw"]})
    for name, sel in state_colors.items():
        for which in ("light", "night"):
            for row in sel[which]:
                if row["raw"] and not row["resolved"]["resolved"]:
                    unresolved.append({"where": f"color:{name}/{which}/{row['state']}", "raw": row["raw"]})

    return {
        "schema": 1,
        "source": {**read_source_meta(decoded), "decoded": rel(decoded, decoded.parent.parent),
                   "device_sdk": device_sdk, "config": "values + values-vN(<=sdk) + values-night"},
        "component": component,
        "match": pattern.pattern,
        "style_prefix": style_prefix,
        "exclude": exclude.pattern,
        "counts": {"styles": len(styles), "theme_attrs": len(theme),
                   "theme_attrs_coui": sum(1 for k in theme if k.startswith("coui")),
                   "theme_attrs_used": len(attr_names),
                   "state_selectors": len(state_colors), "colors": len(colors), "dimens": len(dimens),
                   "anims": len(anims), "interpolators": len(interpolators),
                   "unresolved": len(unresolved),
                   "related": {k: len(v) for k, v in related_names.items()}},
        "styles": styles,
        "state_colors": state_colors,
        "colors": colors,
        "dimens": dimens,
        "motion": {"anims": anims, "interpolators": interpolators},
        "related_names": related_names,
        "unresolved": unresolved,
    }


def md_escape(v) -> str:
    return str(v).replace("|", "\\|").replace("\n", " ")


def literal(row) -> str:
    if not row or not isinstance(row, dict):
        return "—"
    # 传入的可能是 resolve() 结果，也可能是 {..., "resolved": resolve()} 包装。
    r = row if "literal" in row else row.get("resolved")
    if not isinstance(r, dict):
        return "—"
    return (r.get("literal") or "—") + ("" if r.get("resolved", True) else " ⚠")


def render_markdown(spec: dict) -> str:
    s = spec["source"]
    out = [
        f"# {spec['component']} 原厂规格证据（{s.get('apk') or '未知包'}）",
        "",
        "> 由 `tools/extract_component_spec.py` 只读解析 `decoded/res` 自动生成，请勿手改。",
        "> 本文件是**原厂证据快照**，不是组件文档；实现常量以源码与 KDoc 为唯一维护位置。",
        "",
        "## 来源",
        "",
        "| 项 | 值 |",
        "| --- | --- |",
        f"| 包名 | {md_escape(s.get('apk') or '缺失')} |",
        f"| 版本 | {md_escape(s.get('version_name') or '缺失')} |",
        f"| 构建 | {md_escape(s.get('build') or '缺失')} |",
        f"| 设备 | {md_escape(s.get('device') or '缺失')} |",
        f"| SHA-256 | `{s.get('sha256') or '缺失'}` |",
        f"| 解析配置 | {md_escape(s.get('config'))}（目标 API {s.get('device_sdk')}） |",
        f"| 匹配规则 | `{md_escape(spec['match'])}` |",
        f"| 样式前缀 | `{md_escape(spec['style_prefix'])}` |",
        f"| 排除规则 | `{md_escape(spec['exclude'])}` |",
        "",
        f"计数：{json.dumps(spec['counts'], ensure_ascii=False)}",
        "",
        "> 主题属性口径：本表按设备实际生效的配置叠加（`values` + `values-vN` + `values-night` 的颜色覆盖），",
        "> 统计全部 item（含 `android:*`）；`DESIGN-TOKENS.md` 的 187 只数 `values` 下 `coui` 开头的属性，",
        "> 两者不是同一口径，不要直接比大小。",
        "",
        "## 1. 样式继承链与合并后的属性",
        "",
        "每个属性给出原值、浅色与深色解析值；⚠ 表示引用链未闭合，需人工补证据。",
        "",
    ]
    for name, sty in spec["styles"].items():
        out += [f"### `{name}`", "",
                f"- parent: `{sty['parent'] or '—'}`；链：`{' → '.join(sty['chain'])}`",
                f"- 来源：`{sty['source']}`", "",
                "| 属性 | 原值 | 浅色 | 深色 | 来源 |", "| --- | --- | --- | --- | --- |"]
        for k, row in sorted(sty["items"].items()):
            out.append(f"| `{md_escape(k)}` | `{md_escape(row['raw'])}` | `{md_escape(literal(row['light']))}` "
                       f"| `{md_escape(literal(row['night']))}` | {md_escape(row['source'])} |")
        out.append("")

    out += ["## 2. 状态色（ColorStateList）", "",
            "状态按 selector 的 item 顺序归并为 disabled / pressed / selected / checked / focused / default。", ""]
    for name, sel in spec["state_colors"].items():
        out += [f"### `{name}`", "", f"来源：`{sel['source']}`", "",
                "| 状态 | 原值 | 浅色 | 深色 |", "| --- | --- | --- | --- |"]
        night = {r["state"]: r for r in sel["night"]}
        for row in sel["light"]:
            n = night.get(row["state"])
            out.append(f"| {row['state']} | `{md_escape(row['raw'])}` | `{md_escape(literal(row['resolved']))}` "
                       f"| `{md_escape(literal(n['resolved']) if n else None)}` |")
        out.append("")

    out += ["## 3. 颜色资源", "", "| 名称 | 浅色 | 深色 | 来源 |", "| --- | --- | --- | --- |"]
    for name, row in spec["colors"].items():
        out.append(f"| `{md_escape(name)}` | `{md_escape(literal(row['light']))}` "
                   f"| `{md_escape(literal(row['night']))}` | {md_escape(row['light']['source'])} |")

    out += ["", "## 4. 几何与其它数值", "",
            "| 名称 | 类型 | 浅色 | 深色 | 限定符变体 | 来源 |", "| --- | --- | --- | --- | --- | --- |"]
    for name, row in spec["dimens"].items():
        variants = ", ".join(f"{v['source'].split('/')[-2]}:{v['value']}"
                             for v in row["variants"]) or "—"
        out.append(f"| `{md_escape(name)}` | {row['type']} | `{md_escape(literal(row['light']))}` "
                   f"| `{md_escape(literal(row['night']))}` | {md_escape(variants)} | "
                   f"{md_escape(row['light']['source'])} |")

    out += ["", "## 5. 动效资源", ""]
    if spec["motion"]["anims"]:
        out += ["### anim / animator", "", "| 文件 | 根标签 | 参数 | 来源 |", "| --- | --- | --- | --- |"]
        for a in spec["motion"]["anims"]:
            params = "; ".join(f"{k}={','.join(v)}" for k, v in a["params"].items())
            out.append(f"| `{a['name']}` | {a['root_tag']} | {md_escape(params)} | {a['source']} |")
    else:
        out.append("未匹配到 anim / animator 资源。**这不代表原厂无动画**：按压物理参数、模糊半径映射、"
                   "材质效果与状态机由代码侧承载，需定点从反编译源码取数值。")
    if spec["motion"]["interpolators"]:
        out += ["", "### interpolator", "", "| 文件 | 标签 | 控制点 | 来源 |", "| --- | --- | --- | --- |"]
        for i in spec["motion"]["interpolators"]:
            cs = "; ".join(f"{k}={v['resolved']['literal']}" for k, v in i["controls"].items())
            out.append(f"| `{i['name']}` | {i['tag']} | {md_escape(cs)} | {i['source']} |")

    out += ["", "## 6. 未闭合引用", ""]
    if spec["unresolved"]:
        out += ["| 位置 | 原值 |", "| --- | --- |"]
        for u in spec["unresolved"]:
            out.append(f"| {md_escape(u['where'])} | `{md_escape(u['raw'])}` |")
    else:
        out.append("无。")

    related = spec.get("related_names", {})
    out += ["", "## 7. 同族但未在本组件样式链中引用的资源", "",
            "仅列名称作为定点取证线索，**不是本组件已确认使用的规格**；使用前需先找到消费者。", ""]
    for kind, names in related.items():
        out += [f"### {kind}（{len(names)}）", ""]
        if names:
            shown = "`, `".join(names[:80])
            more = f" … 其余 {len(names) - 80} 项见 JSON" if len(names) > 80 else ""
            out.append(f"`{shown}`{more}")
        else:
            out.append("无。")
        out.append("")

    out += [
        "",
        "## 8. 静态资源不覆盖、必须另取的部分",
        "",
        "以下是写组件库时仍需补齐的证据，均**不在 `res/` 里**，脚本无法抽取：",
        "",
        "1. 按压 / 回弹的弹簧数值（response、bounce、初速度、结束阈值）与曲线控制点。",
        "2. 材质模糊半径映射、光效 / 阴影的运行时参数与 AGSL 常量。",
        "3. 状态机：进入事件、前置条件、中断 / 反向 / CANCEL 的收尾、输入启用时机。",
        "4. 深浅色、动画等级、光效开关等运行门控对上面各项的分支影响。",
        "5. 真机多状态对照截图（浅 / 深 / 各交互态 / 关键屏幕限定符）。",
        "",
        "按 `docs/development/replication-guide.md` 的追踪顺序定点取证，并把结论写进对应 Note；",
        "不要用当前实现反推原厂，也不要把未取到的值补成猜测值。",
        "",
    ]
    return "\n".join(out)


def flatten_for_compare(doc: dict) -> dict:
    """把一份规格摊平成 {路径: (浅色, 深色)}，用于跨包一致性比对。"""
    def lit(row):
        return (row or {}).get("literal") if isinstance(row, dict) else None

    out = {}
    for style_name, style in doc["styles"].items():
        for key, row in style["items"].items():
            out[f"style:{style_name}/{key}"] = (lit(row["light"]), lit(row["night"]))
    for name, row in doc["dimens"].items():
        out[f"dimen:{name}"] = (lit(row["light"]), lit(row["night"]))
    for name, sel in doc["state_colors"].items():
        night = {r["state"]: r for r in sel["night"]}
        for state in sel["light"]:
            nr = night.get(state["state"])
            out[f"csl:{name}/{state['state']}"] = (lit(state["resolved"]),
                                                   lit(nr["resolved"]) if nr else None)
    return out


def compare_reports(paths: list[Path], out_dir: Path, component: str) -> Path:
    docs = [(p, json.loads(p.read_text(encoding="utf-8"))) for p in paths]
    labels = [p.stem.split("-")[0] for p, _ in docs]
    flat = {label: flatten_for_compare(doc) for (_, doc), label in zip(docs, labels)}
    common = set.intersection(*(set(v) for v in flat.values()))
    diffs = [(k, {l: flat[l][k] for l in labels}) for k in sorted(common)
             if len({str(flat[l][k]) for l in labels}) > 1]
    lines = [
        "# 跨包组件规格一致性比对", "",
        "由 `tools/extract_component_spec.py --compare` 生成；只比对各包**共有**的规格键。", "",
        f"- 输入：{', '.join(f'`{p.name}`' for p in paths)}",
        f"- 共有键：{len(common)}",
        f"- 差异键：{len(diffs)}", "",
    ]
    for label in labels:
        lines.append(f"- `{label}` 独有键：{len(set(flat[label]) - common)}（该包额外内嵌的样式/资源，不是差异）")
    lines += ["", "## 差异明细", ""]
    if diffs:
        lines += ["| 键 | " + " | ".join(labels) + " |", "| --- | " + " | ".join(["---"] * len(labels)) + " |"]
        for key, vals in diffs:
            lines.append(f"| `{key}` | " + " | ".join(f"`{vals[l]}`" for l in labels) + " |")
    else:
        lines.append("无。三包共有的静态规格完全一致，维护一套组件规格即可。")
    lines += ["", "> 独有键只说明某包内嵌了额外变体，不能据此推断另一包缺少该能力；",
              "> 是否为目标页面实际使用，仍需按 `docs/development/replication-guide.md` 找到消费者。", ""]
    out_dir.mkdir(parents=True, exist_ok=True)
    target = out_dir / f"compare-{component.lower()}.md"
    target.write_text("\n".join(lines), encoding="utf-8")
    print(f"OK {target} (common={len(common)}, differing={len(diffs)})")
    return target


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("decoded", type=Path, nargs="?", help="apktool 解包目录，例如 references/apk/settings/decoded")
    ap.add_argument("--component", default="Button", help="组件名，用于产物标题，例如 Button")
    ap.add_argument("--match", default=None, help="资源/样式名匹配正则，默认用 --component 小写")
    ap.add_argument("--label", default="spec", help="产物文件名前缀")
    ap.add_argument("--style-prefix", default=None,
                    help="样式族前缀，默认 Widget.COUI.<component>")
    ap.add_argument("--exclude", default=r"^(mtrl_|btn_(radio|checkbox)_|abc_|settingslib_|privacy_)",
                    help="排除的资源名正则（Material / 单选 / 复选框等噪声）")
    ap.add_argument("--out", type=Path, default=Path("references/specs"))
    ap.add_argument("--device-sdk", type=int, default=37)
    ap.add_argument("--compare", nargs="*", default=None, metavar="JSON",
                    help="只比对已生成的 JSON，不重新抽取；至少给两个文件")
    args = ap.parse_args()

    if args.compare is not None:
        if len(args.compare) < 2:
            ap.error("--compare 至少需要两个 JSON")
        compare_reports([Path(p) for p in args.compare], args.out, args.component)
        return
    if args.decoded is None:
        ap.error("需要给出 decoded 目录，或使用 --compare")

    pattern = re.compile(args.match or re.escape(args.component.lower()), re.IGNORECASE)
    style_prefix = args.style_prefix or f"Widget.COUI.{args.component}"
    exclude = re.compile(args.exclude, re.IGNORECASE)
    spec = build_spec(args.decoded, args.component, pattern, args.device_sdk, style_prefix, exclude)
    args.out.mkdir(parents=True, exist_ok=True)
    stem = f"{args.label}-{args.component.lower()}"
    json_path = args.out / f"{stem}.json"
    md_path = args.out / f"{stem}.md"
    json_path.write_text(json.dumps(spec, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    md_path.write_text(render_markdown(spec), encoding="utf-8")
    print(f"OK {json_path} ({json_path.stat().st_size} bytes)")
    print(f"OK {md_path} ({md_path.stat().st_size} bytes)")
    print("counts: " + json.dumps(spec["counts"], ensure_ascii=False))
    if not spec["source"]["sha256"]:
        print("WARN 来源 SHA-256 未取到，请在 README 中补齐构建指纹", file=sys.stderr)


if __name__ == "__main__":
    main()

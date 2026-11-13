#!/usr/bin/env python3
"""极简 PNG 读取与像素测量工具（无第三方依赖）。

用于从真机截图里量圆角、描边、配色等 uiautomator 拿不到的数值。

用法：
    python tools/pngprobe.py <png> pixel <x> <y>
    python tools/pngprobe.py <png> row <y> [--from <x0>] [--to <x1>]
    python tools/pngprobe.py <png> radius <x0> <y0> <x1> <y1>
    python tools/pngprobe.py <png> avg <x0> <y0> <x1> <y1>

radius：给定矩形外框，沿顶边找表面起点、沿左边找表面起点，输出两方向的圆角像素。
"""

import struct
import sys
import zlib


def load(path):
    data = open(path, "rb").read()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", "不是 PNG"
    pos, idat, w = 8, b"", None
    h = bit = ctype = None
    while pos < len(data):
        (ln,) = struct.unpack(">I", data[pos : pos + 4])
        typ = data[pos + 4 : pos + 8]
        body = data[pos + 8 : pos + 8 + ln]
        if typ == b"IHDR":
            w, h, bit, ctype = struct.unpack(">IIBB", body[:10])
        elif typ == b"IDAT":
            idat += body
        elif typ == b"IEND":
            break
        pos += 12 + ln
    assert bit == 8 and ctype in (2, 6), f"只支持 8bit RGB/RGBA，实际 {bit}/{ctype}"
    ch = 4 if ctype == 6 else 3
    raw = zlib.decompress(idat)
    stride = w * ch
    out = bytearray(w * h * ch)
    prev = bytearray(stride)
    p = 0
    for y in range(h):
        f = raw[p]
        p += 1
        line = bytearray(raw[p : p + stride])
        p += stride
        if f == 1:
            for i in range(ch, stride):
                line[i] = (line[i] + line[i - ch]) & 0xFF
        elif f == 2:
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 0xFF
        elif f == 3:
            for i in range(stride):
                left = line[i - ch] if i >= ch else 0
                line[i] = (line[i] + ((left + prev[i]) >> 1)) & 0xFF
        elif f == 4:
            for i in range(stride):
                a = line[i - ch] if i >= ch else 0
                b = prev[i]
                c = prev[i - ch] if i >= ch else 0
                pa, pb, pc = abs(b - c), abs(a - c), abs(a + b - 2 * c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[i] = (line[i] + pr) & 0xFF
        out[y * stride : (y + 1) * stride] = line
        prev = line
    return w, h, ch, out


def px(img, x, y):
    w, h, ch, buf = img
    i = (y * w + x) * ch
    return tuple(buf[i : i + 3])


def dist(a, b):
    return max(abs(a[0] - b[0]), abs(a[1] - b[1]), abs(a[2] - b[2]))


def cmd_pixel(img, a):
    print(px(img, int(a[0]), int(a[1])))


def cmd_avg(img, a):
    x0, y0, x1, y1 = (int(v) for v in a[:4])
    n = 0
    acc = [0, 0, 0]
    for y in range(y0, y1):
        for x in range(x0, x1):
            c = px(img, x, y)
            acc[0] += c[0]
            acc[1] += c[1]
            acc[2] += c[2]
            n += 1
    print(tuple(round(v / max(n, 1)) for v in acc), "n=", n)


def cmd_row(img, a):
    y = int(a[0])
    w, h, ch, buf = img
    x0 = int(a[a.index("--from") + 1]) if "--from" in a else 0
    x1 = int(a[a.index("--to") + 1]) if "--to" in a else w
    prev = px(img, x0, y)
    print(f"y={y} x={x0} {prev}")
    for x in range(x0 + 1, x1):
        c = px(img, x, y)
        if dist(c, prev) > 12:
            print(f"x={x} {prev} -> {c}")
        prev = c


def cmd_radius(img, a):
    x0, y0, x1, y1 = (int(v) for v in a[:4])
    # 取卡片内一点作为表面色参考
    ref = px(img, (x0 + x1) // 2, y0 + 2)
    # 顶边：从 y0 往下找第一个与背景明显不同的行，再在该行找水平起点
    bg = px(img, max(x0 - 8, 0), y0 - 8)
    print("背景", bg, "表面参考", ref)
    top = None
    for y in range(y0 - 4, y0 + 60):
        if dist(px(img, (x0 + x1) // 2, y), ref) < 6:
            top = y
            break
    if top is None:
        print("未找到顶边")
        return
    left = None
    for x in range(x0 - 4, x1):
        if dist(px(img, x, top), ref) < 6:
            left = x
            break
    print(f"顶边 y={top}，该行表面起点 x={left}")
    if left is not None:
        print(f"水平内缩 {left - x0}px")
    # 左边：在 x0+1 列找垂直起点
    for y in range(y0 - 4, y1):
        if dist(px(img, x0 + 2, y), ref) < 6:
            print(f"左侧垂直起点 y={y}，内缩 {y - top}px")
            break


def main():
    path, cmd, rest = sys.argv[1], sys.argv[2], sys.argv[3:]
    img = load(path)
    print("size", img[0], img[1], "ch", img[2])
    {"pixel": cmd_pixel, "row": cmd_row, "radius": cmd_radius, "avg": cmd_avg}[cmd](img, rest)


if __name__ == "__main__":
    main()

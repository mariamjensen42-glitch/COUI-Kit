#!/usr/bin/env bash
# COUI 真机参考抓图
#
# 用法：
#   tools/capture.sh <应用> <页面>            抓当前深浅色一张
#   tools/capture.sh <应用> <页面> both       强制切浅色、深色各抓一张，结束后恢复原模式
#
# 例：
#   tools/capture.sh settings home both
#   tools/capture.sh systemui control_center both
#
# 产出（references/screenshots/<应用>/）：
#   <页面>_<light|dark>.png        原图，不做任何缩放
#   <页面>_<light|dark>.layout.xml uiautomator 布局树（bounds / 字号 / 类名）
#
# 环境变量：ADB（adb 路径）、SERIAL（设备序列号）

set -euo pipefail

# Git Bash 会把 /sdcard/... 当成 Windows 路径改写，必须关掉
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL='*'

ADB="${ADB:-E:/SoftWare/SDK/platform-tools/adb.exe}"
SERIAL="${SERIAL:-2ac81f7b}"

APP="${1:?用法: tools/capture.sh <应用> <页面> [both]}"
PAGE="${2:?用法: tools/capture.sh <应用> <页面> [both]}"
MODE="${3:-}"

adb() { "$ADB" -s "$SERIAL" "$@"; }

current_mode() { adb shell cmd uimode night 2>/dev/null | tr -d '\r' | sed 's/Night mode: //'; }

capture() {
  local theme="$1" out="references/screenshots/$APP/${PAGE}_$1"
  mkdir -p "references/screenshots/$APP"
  adb exec-out screencap -p > "${out}.png"
  adb shell "uiautomator dump /sdcard/_coui_dump.xml" >/dev/null 2>&1 || true
  adb pull /sdcard/_coui_dump.xml "${out}.layout.xml" >/dev/null 2>&1 || true
  adb shell "rm -f /sdcard/_coui_dump.xml" >/dev/null 2>&1 || true
  printf '%s.png (%s, %s bytes)\n' "$out" "$theme" "$(wc -c < "${out}.png" | tr -d ' ')"
}

if [ "$MODE" = "both" ]; then
  ORIGINAL="$(current_mode)"
  for t in light dark; do
    case "$t" in light) v=no ;; dark) v=yes ;; esac
    adb shell cmd uimode night "$v" >/dev/null 2>&1 || true
    sleep 1
    capture "$t"
  done
  # 尽量还原：custom_schedule / auto 都尝试，失败则回到 no
  case "$ORIGINAL" in
    yes|no) adb shell cmd uimode night "$ORIGINAL" >/dev/null 2>&1 || true ;;
    *) adb shell cmd uimode night auto >/dev/null 2>&1 || adb shell cmd uimode night no >/dev/null 2>&1 || true ;;
  esac
  printf '已恢复夜间模式：%s（原为 %s）\n' "$(current_mode)" "$ORIGINAL"
else
  case "$(current_mode)" in
    yes) capture dark ;;
    no)  capture light ;;
    *)   capture "$(current_mode)" ;;
  esac
fi

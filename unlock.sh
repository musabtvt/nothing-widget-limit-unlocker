#!/usr/bin/env bash
set -e

LIMIT="${1:-7}"
echo "Configuring widget limit to: ${LIMIT}"

if ! command -v adb >/dev/null 2>&1; then
    echo "Error: adb is not found in PATH."
    exit 1
fi

adb push "$(dirname "$0")/bin/unlock_widget_limit.dex" /data/local/tmp/unlock_widget_limit.dex
adb shell "CLASSPATH=/data/local/tmp/unlock_widget_limit.dex app_process /data/local/tmp EncryptAndInsert ${LIMIT}"
echo "Done! The widget limit has been updated."

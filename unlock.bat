@echo off
set LIMIT=%1
if "%LIMIT%"=="" set LIMIT=7

echo Configuring widget limit to %LIMIT%...
adb push "%~dp0bin\unlock_widget_limit.dex" /data/local/tmp/unlock_widget_limit.dex
adb shell "CLASSPATH=/data/local/tmp/unlock_widget_limit.dex app_process /data/local/tmp EncryptAndInsert %LIMIT%"
echo Done! Widget limit updated.
pause

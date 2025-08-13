@echo off
REM Proxy Gradle wrapper to run the Android project wrapper inside android_frontend
setlocal
set ROOT=%~dp0
set APP_DIR=%ROOT%android_frontend
call "%APP_DIR%\gradlew.bat" %*
endlocal

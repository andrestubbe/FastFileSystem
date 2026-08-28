@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo [FastFileSystem] Building Core Library...
call mvn clean compile -q
if errorlevel 1 (
    echo [ERROR] Maven compilation failed!
    pause
    exit /b 1
)

echo [FastFileSystem] Running Interactive Hero Demo...
echo.
call mvn exec:java -Dexec.mainClass=fastfilesystem.Demo -q
echo.
pause
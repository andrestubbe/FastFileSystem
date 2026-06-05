@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo [FastFileSystem] Running Demo (via JitPack)...
cd examples\Demo
call mvn compile exec:java -Dexec.mainClass=fastfilesystem.Demo
cd ..\..
pause

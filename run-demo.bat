@echo off
echo ðŸš€ Running Hero Demo...
cd examples\Demo
call mvn -q compile exec:java -Dexec.mainClass=fastfilesystem.Demo
cd ..\..
pause

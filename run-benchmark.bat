@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo [FastFileSystem] Building Core Library...
call mvn compile -q
if errorlevel 1 (
    echo [ERROR] Core compilation failed!
    pause
    exit /b 1
)

echo [FastFileSystem] Building OpenJDK JMH Benchmark Suite...
cd examples\Benchmark
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo [ERROR] JMH package build failed!
    cd ..\..
    pause
    exit /b 1
)

echo.
echo [FastFileSystem] Running JMH Microbenchmarks (Throughput Mode)...
echo.
java -jar target\benchmarks.jar -f 1 -wi 2 -i 3 -tu ms
cd ..\..
echo.
pause
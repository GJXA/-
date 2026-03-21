@echo off
echo =========================================
echo 校园平台微服务快速集成测试
echo =========================================
echo.

REM 检查Java版本
echo 检查Java版本...
java -version 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到Java或Java版本不符合要求
    echo 请确保已安装Java 17或更高版本
    pause
    exit /b 1
)

REM 停止可能占用端口的进程
echo 停止可能占用端口的进程...
for /l %%i in (8080,1,8085) do (
    for /f "tokens=5" %%p in ('netstat -ano ^| findstr :%%i ^| findstr LISTENING') do (
        if not "%%p"=="" (
            echo 停止进程PID: %%p (端口%%i)
            taskkill /F /PID %%p >nul 2>&1
        )
    )
)
timeout /t 2 /nobreak >nul

REM 编译公共模块
echo.
echo 编译公共模块...
cd campus-common
call ../mvnw clean compile -DskipTests > ..\logs\common-build.log 2>&1
if %errorlevel% neq 0 (
    echo 错误: 公共模块编译失败
    echo 请查看 logs\common-build.log 文件
    pause
    exit /b 1
)
cd ..

REM 创建日志目录
if not exist logs mkdir logs

echo.
echo =========================================
echo 启动所有微服务 (等待60秒后停止)
echo =========================================
echo.

REM 启动用户服务 (端口8081)
start "用户服务" cmd /c "cd campus-service-user && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8081 -DskipTests > ..\logs\user-service.log 2>&1"
echo 启动用户服务 (端口8081)...
timeout /t 10 /nobreak >nul

REM 启动商品服务 (端口8082)
start "商品服务" cmd /c "cd campus-service-product && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8082 -DskipTests > ..\logs\product-service.log 2>&1"
echo 启动商品服务 (端口8082)...
timeout /t 10 /nobreak >nul

REM 启动订单服务 (端口8083)
start "订单服务" cmd /c "cd campus-service-order && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8083 -DskipTests > ..\logs\order-service.log 2>&1"
echo 启动订单服务 (端口8083)...
timeout /t 10 /nobreak >nul

REM 启动兼职服务 (端口8084)
start "兼职服务" cmd /c "cd campus-service-job && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8084 -DskipTests > ..\logs\job-service.log 2>&1"
echo 启动兼职服务 (端口8084)...
timeout /t 10 /nobreak >nul

REM 启动通知服务 (端口8085)
start "通知服务" cmd /c "cd campus-service-notification && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8085 -DskipTests > ..\logs\notification-service.log 2>&1"
echo 启动通知服务 (端口8085)...
timeout /t 10 /nobreak >nul

REM 启动网关服务 (端口8080)
start "网关服务" cmd /c "cd campus-gateway && ../mvnw spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8080 -DskipTests > ..\logs\gateway.log 2>&1"
echo 启动网关服务 (端口8080)...
timeout /t 15 /nobreak >nul

echo.
echo =========================================
echo 所有服务启动完成，等待60秒...
echo =========================================
echo.
timeout /t 60 /nobreak >nul

echo.
echo 检查服务日志中的错误...
echo.

REM 检查各服务日志中的错误
set error_count=0

for %%s in (user-service product-service order-service job-service notification-service gateway) do (
    if exist logs\%%s.log (
        findstr /i "error fail exception" logs\%%s.log >nul
        if not errorlevel 1 (
            echo [%%s] 发现错误或异常
            set /a error_count+=1
        ) else (
            echo [%%s] 未发现明显错误
        )
    ) else (
        echo [%%s] 日志文件不存在
    )
)

echo.
echo 共发现 %error_count% 个服务有错误
echo.

echo 停止所有服务...
taskkill /FI "WINDOWTITLE eq 用户服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 商品服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 订单服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 兼职服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 通知服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 网关服务" /F >nul 2>&1

echo 所有服务已停止
timeout /t 2 /nobreak >nul

if %error_count% gtr 0 (
    echo 测试失败: 发现 %error_count% 个服务有错误
    exit /b 1
) else (
    echo 测试通过: 所有服务启动成功，未发现明显错误
    exit /b 0
)
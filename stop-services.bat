@echo off
echo =========================================
echo 停止校园平台微服务
echo =========================================
echo.

echo 停止所有服务进程...
taskkill /FI "WINDOWTITLE eq 用户服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 商品服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 订单服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 兼职服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 网关服务" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 通知服务" /F >nul 2>&1

echo 停止占用端口的Java进程...
for /l %%i in (8080,1,8085) do (
    for /f "tokens=5" %%p in ('netstat -ano ^| findstr :%%i ^| findstr LISTENING') do (
        if not "%%p"=="" (
            echo 停止进程PID: %%p (端口%%i)
            taskkill /F /PID %%p >nul 2>&1
        )
    )
)

echo 清理完成！
timeout /t 2 /nobreak >nul
@echo off
echo 启动商品服务10秒测试...
echo.

cd campus-service-product

REM 先停止可能存在的服务
taskkill /FI "WINDOWTITLE eq ProductService" /F >nul 2>&1

REM 启动服务并记录PID
echo 启动商品服务...
start "ProductService" /B cmd /c "..\mvnw.cmd spring-boot:run -DskipTests > ..\logs\product-run.log 2>&1"

REM 等待服务启动
echo 等待10秒...
timeout /t 10 /nobreak >nul

REM 检查日志
echo.
echo ============ 日志片段 ============
if exist ..\logs\product-run.log (
    type ..\logs\product-run.log | tail -n 20
) else (
    echo 日志文件不存在
)

REM 停止服务
echo.
echo 停止服务...
taskkill /FI "WINDOWTITLE eq ProductService" /F >nul 2>&1
taskkill /FI "IMAGENAME eq java.exe" /FI "WINDOWTITLE eq ProductService*" /F >nul 2>&1

echo 测试完成
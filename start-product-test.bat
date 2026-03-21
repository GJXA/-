@echo off
echo 启动商品服务测试...
echo.

cd campus-service-product

REM 启动服务并记录日志
echo 启动商品服务 (端口8082)...
start "Product Service Test" cmd /c "..\mvnw.cmd spring-boot:run -DskipTests > ..\logs\product-service-test.log 2>&1"

REM 等待服务启动
echo 等待服务启动（30秒）...
timeout /t 30 /nobreak >nul

REM 检查日志文件
echo.
echo ============ 最后10行日志 ============
if exist ..\logs\product-service-test.log (
    tail -n 10 ..\logs\product-service-test.log
) else (
    echo 日志文件不存在
)

echo.
echo 停止服务...
taskkill /FI "WINDOWTITLE eq Product Service Test" /F >nul 2>&1
timeout /t 2 /nobreak >nul

echo 测试完成
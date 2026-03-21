@echo off
echo =========================================
echo 第一阶段测试：网关 + 用户服务
echo 测试用户认证API
echo =========================================
echo.

REM 设置环境变量
set LOG_DIR=logs\phase1
if not exist %LOG_DIR% mkdir %LOG_DIR%

REM 停止可能占用端口的进程
echo 停止可能占用端口的进程...
for /l %%i in (8080,1,8081) do (
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
call ../mvnw.cmd clean compile -DskipTests > ..\%LOG_DIR%\common-build.log 2>&1
if %errorlevel% neq 0 (
    echo 错误: 公共模块编译失败
    echo 请查看 %LOG_DIR%\common-build.log 文件
    pause
    exit /b 1
)
cd ..

echo 公共模块编译成功
timeout /t 2 /nobreak >nul

echo.
echo =========================================
echo 启动用户服务 (端口8081)
echo =========================================
echo.

REM 启动用户服务
start "UserService" cmd /c "cd campus-service-user && ../mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8081 -DskipTests > ..\%LOG_DIR%\user-service.log 2>&1"
echo 用户服务启动中...
timeout /t 15 /nobreak >nul

REM 检查用户服务是否启动成功
echo 检查用户服务状态...
curl -s -o nul -w "%%{http_code}" http://localhost:8081/actuator/health
if errorlevel 1 (
    echo 错误: 用户服务启动失败
    echo 检查 %LOG_DIR%\user-service.log 文件
    timeout /t 2 /nobreak >nul
    goto cleanup
)

echo 用户服务启动成功
timeout /t 2 /nobreak >nul

echo.
echo =========================================
echo 启动网关服务 (端口8080)
echo =========================================
echo.

REM 启动网关服务
start "GatewayService" cmd /c "cd campus-gateway && ../mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Dserver.port=8080 -DskipTests > ..\%LOG_DIR%\gateway.log 2>&1"
echo 网关服务启动中...
timeout /t 20 /nobreak >nul

REM 检查网关服务是否启动成功
echo 检查网关服务状态...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health
if errorlevel 1 (
    echo 错误: 网关服务启动失败
    echo 检查 %LOG_DIR%\gateway.log 文件
    timeout /t 2 /nobreak >nul
    goto cleanup
)

echo 网关服务启动成功
timeout /t 5 /nobreak >nul

echo.
echo =========================================
echo 测试用户认证API
echo =========================================
echo.

REM 测试用户注册API
echo 1. 测试用户注册...
curl -s -X POST http://localhost:8080/api/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"Test123456\",\"email\":\"test@example.com\",\"phone\":\"13800138000\",\"realName\":\"测试用户\"}" ^
  > %LOG_DIR%\register-response.json 2>nul

echo 注册响应:
type %LOG_DIR%\register-response.json 2>nul
echo.

REM 测试用户登录API
echo 2. 测试用户登录...
curl -s -X POST http://localhost:8080/api/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"Test123456\"}" ^
  > %LOG_DIR%\login-response.json 2>nul

echo 登录响应:
type %LOG_DIR%\login-response.json 2>nul
echo.

REM 检查登录是否成功
findstr /i "token" %LOG_DIR%\login-response.json >nul
if errorlevel 1 (
    echo 警告: 登录响应中没有找到token
) else (
    echo 登录成功: 获取到JWT token
)

REM 提取token用于后续测试
for /f "tokens=1,2 delims=:," %%a in ('findstr /i "token" %LOG_DIR%\login-response.json') do (
    set token=%%b
)

REM 测试需要认证的用户信息API
echo 3. 测试获取当前用户信息 (需要认证)...
if defined token (
    curl -s -X GET http://localhost:8080/api/users/me ^
      -H "Authorization: Bearer %token%" ^
      > %LOG_DIR%\user-info-response.json 2>nul

    echo 用户信息响应:
    type %LOG_DIR%\user-info-response.json 2>nul
    echo.
) else (
    echo 跳过认证测试: 未获取到token
)

echo.
echo =========================================
echo 测试完成
echo =========================================
echo.

:cleanup
echo 停止所有服务...
taskkill /FI "WINDOWTITLE eq UserService" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq GatewayService" /F >nul 2>&1

REM 清理端口占用
for /l %%i in (8080,1,8081) do (
    for /f "tokens=5" %%p in ('netstat -ano ^| findstr :%%i ^| findstr LISTENING') do (
        if not "%%p"=="" (
            echo 停止进程PID: %%p (端口%%i)
            taskkill /F /PID %%p >nul 2>&1
        )
    )
)

echo.
echo 测试结果保存在 %LOG_DIR% 目录
echo 按任意键退出...
pause >nul
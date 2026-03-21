#!/bin/bash

echo "=== 用户服务API测试 ==="
echo

# 设置日志目录
LOG_DIR="logs/user-test-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$LOG_DIR"

# 清理端口
echo "清理端口8086..."
timeout 2 bash -c "cat < /dev/null > /dev/tcp/localhost/8086" 2>&1 && echo "端口8086已占用，尝试清理..." && fuser -k 8086/tcp 2>/dev/null || true
sleep 2

# 编译公共模块
echo "编译公共模块..."
cd campus-common
../mvnw clean compile -DskipTests > "../$LOG_DIR/common-build.log" 2>&1
if [ $? -ne 0 ]; then
    echo "错误: 公共模块编译失败"
    echo "查看日志: $LOG_DIR/common-build.log"
    exit 1
fi
cd ..

echo "公共模块编译成功"
sleep 2

# 启动用户服务（使用端口8086避免冲突）
echo "启动用户服务 (端口8086)..."
cd campus-service-user
../mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8086" -DskipTests > "../$LOG_DIR/user-service.log" 2>&1 &
USER_PID=$!
cd ..

echo "用户服务PID: $USER_PID"
echo "等待20秒让服务启动..."
sleep 20

# 检查服务是否启动
echo "检查用户服务健康状态..."
if curl -s http://localhost:8086/actuator/health > /dev/null; then
    echo "用户服务启动成功"
else
    echo "错误: 用户服务启动失败"
    echo "查看日志: $LOG_DIR/user-service.log"
    kill $USER_PID 2>/dev/null || true
    exit 1
fi

echo
echo "=== 测试用户认证API ==="
echo

# 测试用户注册
echo "1. 测试用户注册..."
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8086/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test123456","email":"test@example.com","phone":"13800138000","realName":"测试用户"}')
echo "注册响应: $REGISTER_RESPONSE"
echo "$REGISTER_RESPONSE" > "$LOG_DIR/register-response.json"

# 检查注册是否成功
if echo "$REGISTER_RESPONSE" | grep -q '"code":200'; then
    echo "注册成功"
else
    echo "警告: 注册可能失败"
fi

sleep 2

# 测试用户登录
echo
echo "2. 测试用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8086/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test123456"}')
echo "登录响应: $LOGIN_RESPONSE"
echo "$LOGIN_RESPONSE" > "$LOG_DIR/login-response.json"

# 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -n "$TOKEN" ]; then
    echo "获取到Token: $TOKEN"

    # 测试获取当前用户信息
    echo
    echo "3. 测试获取当前用户信息..."
    USER_INFO_RESPONSE=$(curl -s -X GET http://localhost:8086/api/users/me \
      -H "Authorization: Bearer $TOKEN")
    echo "用户信息响应: $USER_INFO_RESPONSE"
    echo "$USER_INFO_RESPONSE" > "$LOG_DIR/user-info-response.json"
else
    echo "警告: 未获取到Token"
fi

# 测试其他API
echo
echo "4. 测试获取用户列表..."
USER_LIST_RESPONSE=$(curl -s -X GET http://localhost:8086/api/users/list?page=1&size=10)
echo "用户列表响应: $USER_LIST_RESPONSE"
echo "$USER_LIST_RESPONSE" > "$LOG_DIR/user-list-response.json"

echo
echo "=== 测试完成 ==="
echo

# 停止服务
echo "停止用户服务..."
kill $USER_PID 2>/dev/null || true
sleep 3

# 清理端口
fuser -k 8086/tcp 2>/dev/null || true

echo
echo "测试日志保存在: $LOG_DIR"
echo "=== 测试结束 ==="
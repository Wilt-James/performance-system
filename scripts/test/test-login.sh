#!/bin/bash

echo "=========================================="
echo "      测试登录功能"
echo "=========================================="

# 等待应用启动
echo "🔍 检查应用是否启动..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/auth/login > /dev/null 2>&1; then
        echo "✅ 应用已启动"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ 应用启动超时"
        exit 1
    fi
    echo "   等待应用启动... ($i/30)"
    sleep 2
done

echo ""
echo "🧪 测试登录接口..."

# 测试登录请求
echo "📋 测试用户: admin / 123456"
response=$(curl -s -w "\n%{http_code}" -X POST \
  http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }')

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | head -n -1)

echo ""
echo "📊 响应结果:"
echo "   HTTP状态码: $http_code"
echo "   响应内容: $response_body"

if [ "$http_code" = "200" ]; then
    echo ""
    echo "✅ 登录测试成功！"
    
    # 尝试解析token
    if echo "$response_body" | grep -q "token"; then
        echo "✅ Token生成成功"
        token=$(echo "$response_body" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
        echo "   Token: ${token:0:50}..."
    else
        echo "⚠️  响应中未找到token"
    fi
else
    echo ""
    echo "❌ 登录测试失败"
    echo "   可能的原因:"
    echo "   1. 路径映射问题"
    echo "   2. Security配置问题"
    echo "   3. 数据库连接问题"
    echo "   4. 用户数据未正确初始化"
fi

echo ""
echo "🔍 其他测试用户:"
echo "   dept_manager / 123456 (科室主任)"
echo "   perf_manager / 123456 (绩效管理员)"
echo "   doctor / 123456 (医生)"

echo ""
echo "🌐 相关地址:"
echo "   登录接口: http://localhost:8080/api/auth/login"
echo "   H2控制台: http://localhost:8080/h2-console"
echo "   API文档: http://localhost:8080/doc.html"

echo ""
echo "=========================================="
#!/bin/bash

echo "=========================================="
echo "      快速MySQL连接测试"
echo "=========================================="

echo "📋 数据库配置:"
echo "   URL: jdbc:mysql://localhost:3306/hospital_performance"
echo "   用户名: root"
echo "   密码: root"

echo ""
echo "🧪 测试MySQL连接..."

# 测试连接
mysql -h localhost -P 3306 -u root -proot -e "SELECT 1 as connection_test;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ MySQL连接成功"
    
    # 检查数据库
    mysql -h localhost -P 3306 -u root -proot -e "USE hospital_performance; SELECT 1;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ 数据库 hospital_performance 存在"
    else
        echo "⚠️  数据库 hospital_performance 不存在"
        echo "   创建数据库: mysql -u root -proot -e \"CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
    fi
else
    echo "❌ MySQL连接失败"
    echo "   请检查:"
    echo "   1. MySQL服务是否启动"
    echo "   2. 用户名密码是否正确 (root/root)"
    echo "   3. 端口3306是否可访问"
fi

echo ""
echo "🚀 启动应用:"
echo "   开发环境: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   默认配置: mvn spring-boot:run"

echo ""
echo "=========================================="
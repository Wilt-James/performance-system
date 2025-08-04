#!/bin/bash

echo "=========================================="
echo "      数据库连接检查"
echo "=========================================="

echo "🔍 检查MySQL服务状态..."

# Windows检查
if command -v sc &> /dev/null; then
    echo "检查Windows MySQL服务..."
    sc query mysql 2>/dev/null || sc query mysql80 2>/dev/null || echo "MySQL服务未找到"
fi

# Linux/Mac检查
if command -v systemctl &> /dev/null; then
    echo "检查Linux MySQL服务..."
    systemctl status mysql 2>/dev/null || systemctl status mysqld 2>/dev/null || echo "MySQL服务未找到"
fi

echo ""
echo "🔍 尝试连接数据库..."

# 检查MySQL连接
if command -v mysql &> /dev/null; then
    echo "测试数据库连接..."
    mysql -h localhost -u root -p123456 -e "SHOW DATABASES;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ 数据库连接成功"
        
        echo ""
        echo "🔍 检查目标数据库..."
        mysql -h localhost -u root -p123456 -e "USE hospital_performance; SHOW TABLES;" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "✅ 数据库 'hospital_performance' 存在"
        else
            echo "❌ 数据库 'hospital_performance' 不存在"
            echo ""
            echo "创建数据库命令:"
            echo "mysql -h localhost -u root -p123456 -e \"CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
        fi
    else
        echo "❌ 数据库连接失败"
        echo ""
        echo "请检查:"
        echo "1. MySQL服务是否启动"
        echo "2. 用户名密码是否正确 (当前: root/123456)"
        echo "3. 端口是否正确 (当前: 3306)"
    fi
else
    echo "❌ 未找到mysql命令"
    echo "请确保MySQL客户端已安装"
fi

echo ""
echo "=========================================="
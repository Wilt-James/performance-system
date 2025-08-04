#!/bin/bash

echo "=========================================="
echo "      修复Spring Boot启动问题"
echo "=========================================="

echo "🔍 问题分析:"
echo "   DataSource配置失败 - 无法确定合适的驱动类"
echo "   可能原因: 数据库服务未启动、数据库不存在、连接参数错误"

echo ""
echo "💡 解决方案:"

echo ""
echo "方案1: 使用开发环境配置 (推荐)"
echo "   开发环境配置禁用了Redis，使用简化的数据库配置"

read -p "是否使用开发环境配置启动? (Y/n): " use_dev
if [[ $use_dev != [nN] ]]; then
    echo "   使用dev配置启动..."
    echo ""
    echo "🚀 启动应用 (开发环境)..."
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    exit 0
fi

echo ""
echo "方案2: 使用最小化配置"
echo "   最小化配置使用MySQL，但禁用了部分功能"

read -p "是否使用最小化配置启动? (Y/n): " use_minimal
if [[ $use_minimal != [nN] ]]; then
    echo "   使用minimal配置启动..."
    echo ""
    echo "🚀 启动应用 (最小化配置)..."
    mvn spring-boot:run -Dspring-boot.run.profiles=minimal
    exit 0
fi

echo ""
echo "方案3: 检查并修复数据库连接"
echo ""

echo "🔍 检查MySQL服务状态..."
if command -v mysql &> /dev/null; then
    echo "   MySQL客户端已安装"
    
    echo ""
    echo "📋 数据库连接信息:"
    echo "   主机: localhost:3306"
    echo "   数据库: hospital_performance"
    echo "   用户名: root"
    echo "   密码: 123456"
    
    echo ""
    read -p "是否尝试连接数据库测试? (y/N): " test_db
    if [[ $test_db == [yY] ]]; then
        echo "   测试数据库连接..."
        mysql -h localhost -P 3306 -u root -p123456 -e "SELECT 1;" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "   ✅ 数据库连接成功"
            
            echo ""
            echo "   检查数据库是否存在..."
            mysql -h localhost -P 3306 -u root -p123456 -e "USE hospital_performance; SELECT 1;" 2>/dev/null
            if [ $? -eq 0 ]; then
                echo "   ✅ 数据库 hospital_performance 存在"
                echo ""
                echo "🚀 尝试启动应用..."
                mvn spring-boot:run
            else
                echo "   ❌ 数据库 hospital_performance 不存在"
                echo ""
                echo "💡 创建数据库:"
                echo "   mysql -u root -p -e \"CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
            fi
        else
            echo "   ❌ 数据库连接失败"
            echo ""
            echo "💡 可能的问题:"
            echo "   1. MySQL服务未启动"
            echo "   2. 用户名或密码错误"
            echo "   3. 端口3306被占用"
        fi
    fi
else
    echo "   ❌ MySQL客户端未安装"
    echo ""
    echo "💡 建议:"
    echo "   1. 安装MySQL服务"
    echo "   2. 或使用开发环境配置: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
    echo "   3. 或使用最小化配置: mvn spring-boot:run -Dspring-boot.run.profiles=minimal"
fi

echo ""
echo "📝 其他启动选项:"
echo "   开发环境: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   最小化:   mvn spring-boot:run -Dspring-boot.run.profiles=minimal"
echo "   默认:     mvn spring-boot:run"

echo ""
echo "=========================================="
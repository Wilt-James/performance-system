#!/bin/bash

echo "=========================================="
echo "      MySQL数据库连接测试"
echo "=========================================="

# 配置信息
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="hospital_performance"
DB_USER="root"
DB_PASS="root"

echo "📋 数据库连接信息:"
echo "   主机: $DB_HOST:$DB_PORT"
echo "   数据库: $DB_NAME"
echo "   用户名: $DB_USER"
echo "   密码: $DB_PASS"

echo ""
echo "🔍 检查MySQL服务状态..."

# 检查MySQL是否安装
if command -v mysql &> /dev/null; then
    echo "✅ MySQL客户端已安装"
else
    echo "❌ MySQL客户端未安装"
    echo "   请先安装MySQL客户端"
    exit 1
fi

echo ""
echo "🧪 测试数据库连接..."

# 测试基本连接
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "SELECT 1 as test;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ 数据库连接成功"
else
    echo "❌ 数据库连接失败"
    echo ""
    echo "💡 可能的原因:"
    echo "   1. MySQL服务未启动"
    echo "   2. 用户名或密码错误"
    echo "   3. 端口3306被占用或防火墙阻止"
    echo "   4. MySQL配置不允许远程连接"
    echo ""
    echo "🔧 解决方案:"
    echo "   1. 启动MySQL服务: sudo systemctl start mysql"
    echo "   2. 检查用户权限: mysql -u root -p"
    echo "   3. 检查端口: netstat -an | grep 3306"
    exit 1
fi

echo ""
echo "🗄️ 检查数据库是否存在..."

mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "USE $DB_NAME; SELECT 1;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ 数据库 $DB_NAME 存在"
    
    # 检查表是否存在
    echo ""
    echo "📊 检查数据库表..."
    
    TABLE_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME';" -s -N 2>/dev/null)
    
    if [ "$TABLE_COUNT" -gt 0 ]; then
        echo "✅ 数据库包含 $TABLE_COUNT 个表"
        
        # 检查用户表
        USER_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT COUNT(*) FROM sys_user;" -s -N 2>/dev/null)
        if [ $? -eq 0 ]; then
            echo "✅ 用户表存在，包含 $USER_COUNT 条记录"
        else
            echo "⚠️  用户表不存在，需要初始化数据"
        fi
    else
        echo "⚠️  数据库为空，需要初始化表结构"
    fi
else
    echo "❌ 数据库 $DB_NAME 不存在"
    echo ""
    echo "🔧 创建数据库:"
    echo "   mysql -u root -p -e \"CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
    
    read -p "是否现在创建数据库? (y/N): " create_db
    if [[ $create_db == [yY] ]]; then
        mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        if [ $? -eq 0 ]; then
            echo "✅ 数据库创建成功"
        else
            echo "❌ 数据库创建失败"
        fi
    fi
fi

echo ""
echo "🚀 启动应用测试..."

read -p "是否使用MySQL配置启动应用? (y/N): " start_app
if [[ $start_app == [yY] ]]; then
    echo "   使用开发环境配置启动..."
    echo ""
    echo "🔄 启动Spring Boot应用..."
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
fi

echo ""
echo "📝 其他启动选项:"
echo "   默认配置:     mvn spring-boot:run"
echo "   开发环境:     mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   H2内存数据库: mvn spring-boot:run -Dspring-boot.run.profiles=h2"

echo ""
echo "🌐 相关地址:"
echo "   应用地址: http://localhost:8080"
echo "   登录接口: http://localhost:8080/api/auth/login"
echo "   Druid监控: http://localhost:8080/druid"

echo ""
echo "=========================================="
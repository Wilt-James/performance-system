#!/bin/bash

echo "=========================================="
echo "      数据源配置验证"
echo "=========================================="

# 配置文件路径
CONFIG_FILES=(
    "src/main/resources/application.yml"
    "src/main/resources/application-dev.yml"
    "src/main/resources/application-minimal.yml"
)

echo "📋 检查配置文件存在性..."
for file in "${CONFIG_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file 存在"
    else
        echo "❌ $file 不存在"
    fi
done

echo ""
echo "🔍 检查数据源配置参数..."

# 检查MySQL连接参数
echo ""
echo "📊 MySQL连接参数检查:"

# 检查主配置文件
echo "   application.yml:"
if grep -q "jdbc:mysql://localhost:3306/hospital_performance" src/main/resources/application.yml; then
    echo "   ✅ URL配置正确"
else
    echo "   ❌ URL配置错误"
fi

if grep -q "username: root" src/main/resources/application.yml; then
    echo "   ✅ 用户名配置正确"
else
    echo "   ❌ 用户名配置错误"
fi

if grep -q "password: root" src/main/resources/application.yml; then
    echo "   ✅ 密码配置正确"
else
    echo "   ❌ 密码配置错误"
fi

# 检查开发环境配置
echo ""
echo "   application-dev.yml:"
if grep -q "jdbc:mysql://localhost:3306/hospital_performance" src/main/resources/application-dev.yml; then
    echo "   ✅ URL配置正确"
else
    echo "   ❌ URL配置错误"
fi

if grep -q "username: root" src/main/resources/application-dev.yml && grep -q "password: root" src/main/resources/application-dev.yml; then
    echo "   ✅ 用户名密码配置正确"
else
    echo "   ❌ 用户名密码配置错误"
fi

# 检查最小化配置
echo ""
echo "   application-minimal.yml:"
if grep -q "username: root" src/main/resources/application-minimal.yml && grep -q "password: root" src/main/resources/application-minimal.yml; then
    echo "   ✅ 用户名密码配置正确"
else
    echo "   ❌ 用户名密码配置错误"
fi

echo ""
echo "🔧 Druid连接池配置检查:"

# 检查Druid配置
if grep -q "type: com.alibaba.druid.pool.DruidDataSource" src/main/resources/application.yml; then
    echo "   ✅ Druid数据源类型配置正确"
else
    echo "   ❌ Druid数据源类型配置错误"
fi

if grep -q "driver-class-name: com.mysql.cj.jdbc.Driver" src/main/resources/application.yml; then
    echo "   ✅ MySQL驱动配置正确"
else
    echo "   ❌ MySQL驱动配置错误"
fi

if grep -q "validation-query: SELECT 1" src/main/resources/application.yml; then
    echo "   ✅ 验证查询语句正确"
else
    echo "   ❌ 验证查询语句错误"
fi

echo ""
echo "📁 MyBatis配置检查:"

# 检查MyBatis配置
if grep -q "map-underscore-to-camel-case: true" src/main/resources/application.yml; then
    echo "   ✅ 驼峰命名映射配置正确"
else
    echo "   ❌ 驼峰命名映射配置错误"
fi

if grep -q "id-type: ASSIGN_ID" src/main/resources/application.yml; then
    echo "   ✅ 主键类型配置正确"
else
    echo "   ❌ 主键类型配置错误"
fi

# 检查是否有XML映射文件配置
if grep -q "# mapper-locations:" src/main/resources/application.yml; then
    echo "   ✅ XML映射文件配置已注释（使用注解方式）"
else
    echo "   ⚠️  建议注释XML映射文件配置"
fi

echo ""
echo "🧪 配置文件语法检查:"

# 检查YAML语法
for file in "${CONFIG_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "   检查 $file..."
        if python3 -c "import yaml; yaml.safe_load(open('$file'))" 2>/dev/null; then
            echo "   ✅ YAML语法正确"
        else
            echo "   ❌ YAML语法错误"
        fi
    fi
done

echo ""
echo "🗄️ SQL脚本检查:"

if [ -f "src/main/resources/sql/init.sql" ]; then
    echo "   ✅ init.sql 存在"
    
    # 检查SQL脚本内容
    if grep -q "CREATE TABLE.*sys_user" src/main/resources/sql/init.sql; then
        echo "   ✅ 用户表创建语句存在"
    else
        echo "   ❌ 用户表创建语句不存在"
    fi
    
    if grep -q "INSERT INTO.*sys_user" src/main/resources/sql/init.sql; then
        echo "   ✅ 用户数据插入语句存在"
    else
        echo "   ❌ 用户数据插入语句不存在"
    fi
else
    echo "   ❌ init.sql 不存在"
fi

echo ""
echo "📦 依赖检查:"

if [ -f "pom.xml" ]; then
    if grep -q "mysql-connector-j" pom.xml; then
        echo "   ✅ MySQL驱动依赖存在"
    else
        echo "   ❌ MySQL驱动依赖不存在"
    fi
    
    if grep -q "druid-spring-boot-starter" pom.xml; then
        echo "   ✅ Druid依赖存在"
    else
        echo "   ❌ Druid依赖不存在"
    fi
    
    if grep -q "mybatis-plus-boot-starter" pom.xml; then
        echo "   ✅ MyBatis-Plus依赖存在"
    else
        echo "   ❌ MyBatis-Plus依赖不存在"
    fi
else
    echo "   ❌ pom.xml 不存在"
fi

echo ""
echo "🎯 配置建议:"
echo "   1. 确保MySQL服务正在运行"
echo "   2. 确保数据库 hospital_performance 已创建"
echo "   3. 使用开发环境配置: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   4. 检查Druid监控: http://localhost:8080/druid"

echo ""
echo "=========================================="
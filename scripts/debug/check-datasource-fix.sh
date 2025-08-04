#!/bin/bash

echo "=========================================="
echo "      数据源配置修复验证"
echo "=========================================="

echo "🔍 检查修复后的配置结构..."

# 检查dev配置文件
echo ""
echo "📋 application-dev.yml 配置检查:"
if grep -A 5 "datasource:" src/main/resources/application-dev.yml | grep -q "driver-class-name: com.mysql.cj.jdbc.Driver"; then
    echo "   ✅ 数据源基本配置正确"
else
    echo "   ❌ 数据源基本配置错误"
fi

if grep -A 10 "datasource:" src/main/resources/application-dev.yml | grep -q "url: jdbc:mysql://localhost:3306/hospital_performance"; then
    echo "   ✅ 数据库URL配置正确"
else
    echo "   ❌ 数据库URL配置错误"
fi

if grep -A 10 "datasource:" src/main/resources/application-dev.yml | grep -q "username: root" && grep -A 10 "datasource:" src/main/resources/application-dev.yml | grep -q "password: root"; then
    echo "   ✅ 用户名密码配置正确"
else
    echo "   ❌ 用户名密码配置错误"
fi

# 检查主配置文件
echo ""
echo "📋 application.yml 配置检查:"
if grep -A 5 "datasource:" src/main/resources/application.yml | grep -q "driver-class-name: com.mysql.cj.jdbc.Driver"; then
    echo "   ✅ 数据源基本配置正确"
else
    echo "   ❌ 数据源基本配置错误"
fi

# 检查最小化配置文件
echo ""
echo "📋 application-minimal.yml 配置检查:"
if grep -A 5 "datasource:" src/main/resources/application-minimal.yml | grep -q "driver-class-name: com.mysql.cj.jdbc.Driver"; then
    echo "   ✅ 数据源基本配置正确"
else
    echo "   ❌ 数据源基本配置错误"
fi

echo ""
echo "🔧 配置结构验证:"
echo "   修复前: spring.datasource.druid.driver-class-name (错误)"
echo "   修复后: spring.datasource.driver-class-name (正确)"

echo ""
echo "📝 正确的配置结构:"
echo "spring:"
echo "  datasource:"
echo "    type: com.alibaba.druid.pool.DruidDataSource"
echo "    driver-class-name: com.mysql.cj.jdbc.Driver"
echo "    url: jdbc:mysql://localhost:3306/hospital_performance"
echo "    username: root"
echo "    password: root"
echo "    druid:"
echo "      initial-size: 5"
echo "      min-idle: 5"
echo "      max-active: 10"

echo ""
echo "🚀 现在可以尝试启动应用:"
echo "   mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo ""
echo "=========================================="
#!/bin/bash

echo "=========================================="
echo "      修复Spring Boot启动问题"
echo "=========================================="

echo "🔧 步骤1: 清理项目..."
mvn clean

echo ""
echo "📦 步骤2: 更新依赖..."
mvn dependency:resolve

echo ""
echo "🔨 步骤3: 重新编译..."
mvn compile

echo ""
echo "🚀 步骤4: 使用开发配置启动..."
echo "   使用简化的配置文件启动系统"
echo "   如果MySQL未启动，请先启动MySQL服务"
echo ""

# 使用开发配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo ""
echo "💡 如果仍有问题，请检查:"
echo "   1. MySQL服务是否启动"
echo "   2. 数据库 'hospital_performance' 是否存在"
echo "   3. Java版本是否为17或更高"
echo "   4. 查看详细错误: mvn spring-boot:run -X"
#!/bin/bash

echo "=========================================="
echo "      启动后端服务"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请先安装JDK 8或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请先安装Maven 3.6或更高版本"
    exit 1
fi

echo "✅ 环境检查通过"
echo ""

echo "🔧 请确保:"
echo "   1. MySQL数据库已启动"
echo "   2. 已创建数据库 'hospital_performance'"
echo "   3. 已执行初始化脚本 src/main/resources/sql/init.sql"
echo ""

echo "🚀 正在启动后端服务..."
echo "   后端服务将在 http://localhost:8080 启动"
echo "   API文档地址: http://localhost:8080/doc.html"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run
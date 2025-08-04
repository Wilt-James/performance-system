#!/bin/bash

echo "=========================================="
echo "      项目构建脚本"
echo "=========================================="

echo "🔧 正在清理项目..."
mvn clean

echo ""
echo "📦 正在下载依赖..."
mvn dependency:resolve

echo ""
echo "🔨 正在编译项目..."
mvn compile

echo ""
echo "✅ 构建完成！"
echo ""
echo "💡 如果构建成功，可以使用以下命令启动："
echo "   mvn spring-boot:run"
echo ""
echo "📋 如果遇到问题，请检查："
echo "   1. Java版本是否为17或更高"
echo "   2. Maven版本是否为3.6或更高"
echo "   3. 网络连接是否正常（下载依赖需要）"
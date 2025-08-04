#!/bin/bash

echo "=========================================="
echo "      Spring Boot启动问题修复 V2"
echo "=========================================="

echo "🧹 步骤1: 彻底清理..."
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/com/baomidou/mybatis-plus*

echo ""
echo "📦 步骤2: 重新下载依赖..."
mvn dependency:resolve -U

echo ""
echo "🔨 步骤3: 重新编译..."
mvn compile

echo ""
echo "🚀 步骤4: 使用最小配置启动..."
echo "   使用最简化的配置文件启动系统"
echo "   已禁用Redis和其他可能冲突的组件"
echo ""

# 使用最小配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=minimal

echo ""
echo "💡 如果仍有问题，请尝试:"
echo "   1. 检查Java版本: java -version"
echo "   2. 检查Maven版本: mvn -version"
echo "   3. 清理IDE缓存并重新导入项目"
echo "   4. 使用调试模式: mvn spring-boot:run -Dspring-boot.run.profiles=minimal -X"
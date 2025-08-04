#!/bin/bash

echo "=========================================="
echo "      切换到Spring Boot 2.7.x"
echo "=========================================="

echo "⚠️  警告: 这将临时切换到Spring Boot 2.7.x版本"
echo "   这是为了解决兼容性问题的临时方案"
echo ""

read -p "是否继续? (y/N): " confirm
if [[ $confirm != [yY] ]]; then
    echo "操作已取消"
    exit 0
fi

echo ""
echo "📦 步骤1: 备份当前pom.xml..."
cp pom.xml pom-springboot3-backup.xml
echo "   已备份为: pom-springboot3-backup.xml"

echo ""
echo "🔄 步骤2: 切换到Spring Boot 2.7.x..."
cp pom-springboot2.xml pom.xml
echo "   已切换到Spring Boot 2.7.18"

echo ""
echo "🧹 步骤3: 清理项目..."
mvn clean

echo ""
echo "📦 步骤4: 下载依赖..."
mvn dependency:resolve

echo ""
echo "🔨 步骤5: 编译项目..."
if mvn compile; then
    echo "✅ 编译成功!"
    echo ""
    echo "🚀 启动系统..."
    mvn spring-boot:run
else
    echo "❌ 编译失败"
    echo ""
    echo "🔄 恢复原始配置..."
    cp pom-springboot3-backup.xml pom.xml
    echo "   已恢复到Spring Boot 3.x版本"
fi

echo ""
echo "💡 如需恢复到Spring Boot 3.x:"
echo "   cp pom-springboot3-backup.xml pom.xml"
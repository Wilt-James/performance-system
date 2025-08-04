#!/bin/bash

echo "=========================================="
echo "      测试MyBatis/MyBatis-Plus升级"
echo "=========================================="

echo "🔧 升级内容:"
echo "   ✅ MyBatis官方Starter: 3.0.4"
echo "   ✅ MyBatis-Plus: 3.5.9"
echo "   ✅ MyBatis-Spring: 3.0.3 (兼容版本)"
echo "   ✅ 排除冲突的旧版本依赖"

echo ""
echo "🧹 步骤1: 彻底清理..."
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/com/baomidou/mybatis-plus*
rm -rf ~/.m2/repository/org/mybatis/

echo ""
echo "📦 步骤2: 下载新版本依赖..."
mvn dependency:resolve -U

echo ""
echo "🔍 步骤3: 检查依赖树..."
echo "MyBatis相关依赖:"
mvn dependency:tree | grep -E "(mybatis|baomidou)"

echo ""
echo "🔨 步骤4: 编译项目..."
if mvn compile; then
    echo "✅ 编译成功!"
    echo ""
    echo "🚀 步骤5: 启动测试..."
    echo "   使用升级后的依赖启动系统"
    echo ""
    
    # 尝试启动 - 使用timeout避免无限等待
    timeout 60s mvn spring-boot:run || echo ""
    
    echo ""
    echo "🎉 如果启动成功，说明依赖升级解决了兼容性问题!"
    
else
    echo "❌ 编译失败!"
    echo ""
    echo "🔍 检查编译错误..."
    mvn compile 2>&1 | tail -20
fi

echo ""
echo "💡 如果仍有问题:"
echo "   1. 检查依赖冲突: mvn dependency:tree"
echo "   2. 查看详细错误: mvn spring-boot:run -X"
echo "   3. 使用Spring Boot 2.7.x: ./switch-to-springboot2.sh"

echo ""
echo "=========================================="
#!/bin/bash

echo "=========================================="
echo "      修复MyBatis-Plus编译问题"
echo "=========================================="

echo "🔍 问题分析:"
echo "   MyBatis-Plus 3.5.9中PaginationInnerInterceptor的包路径可能发生变化"
echo "   需要使用正确的导入路径或简化配置"

echo ""
echo "🔧 解决方案:"

echo ""
echo "方案1: 使用简化配置 (推荐)"
echo "   暂时禁用分页插件，只保留核心功能"

read -p "是否使用简化配置? (y/N): " use_simple
if [[ $use_simple == [yY] ]]; then
    echo "   切换到简化配置..."
    mv src/main/java/com/hospital/performance/config/MyBatisPlusConfig.java src/main/java/com/hospital/performance/config/MyBatisPlusConfig.java.backup
    mv src/main/java/com/hospital/performance/config/MyBatisPlusConfigSimple.java src/main/java/com/hospital/performance/config/MyBatisPlusConfig.java
    echo "   ✅ 已切换到简化配置"
fi

echo ""
echo "🧹 清理并编译..."
mvn clean

echo ""
echo "🔨 尝试编译..."
if mvn compile; then
    echo "✅ 编译成功!"
    echo ""
    echo "🚀 尝试启动..."
    timeout 30s mvn spring-boot:run || echo ""
    echo ""
    echo "🎉 如果启动成功，说明简化配置解决了编译问题!"
else
    echo "❌ 编译仍然失败"
    echo ""
    echo "🔍 检查具体错误:"
    mvn compile 2>&1 | grep -A5 -B5 "ERROR"
    echo ""
    echo "💡 建议:"
    echo "   1. 检查MyBatis-Plus版本兼容性"
    echo "   2. 使用Spring Boot 2.7.x: ./switch-to-springboot2.sh"
    echo "   3. 查看详细错误: mvn compile -X"
fi

echo ""
echo "💡 注意:"
echo "   简化配置暂时禁用了分页插件"
echo "   如需分页功能，可在启动成功后再添加正确的分页配置"

echo ""
echo "=========================================="
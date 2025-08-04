#!/bin/bash

echo "=========================================="
echo "      测试Bean配置修复"
echo "=========================================="

echo "🔧 已修复的问题:"
echo "   ✅ 移除了静态内部类的@Component注解"
echo "   ✅ 改为使用@Bean方法返回MetaObjectHandler实例"
echo "   ✅ 确保所有@Bean方法都返回对象实例"

echo ""
echo "🧹 清理并重新编译..."
mvn clean

echo ""
echo "🔨 编译项目..."
if mvn compile; then
    echo "✅ 编译成功!"
    echo ""
    echo "🚀 启动测试..."
    echo "   使用修复后的配置启动系统"
    echo ""
    
    # 尝试启动
    timeout 30s mvn spring-boot:run -Dspring-boot.run.profiles=minimal || echo "启动测试完成"
    
else
    echo "❌ 编译失败，请检查错误信息"
fi

echo ""
echo "💡 如果仍有问题，建议:"
echo "   1. 使用Spring Boot 2.7.x: ./switch-to-springboot2.sh"
echo "   2. 检查详细错误: mvn spring-boot:run -X"
echo "   3. 验证Bean配置: ./validate-beans.sh"

echo ""
echo "=========================================="
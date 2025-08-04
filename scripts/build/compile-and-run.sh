#!/bin/bash

echo "=========================================="
echo "      编译并启动系统"
echo "=========================================="

echo "🧹 清理项目..."
mvn clean

echo ""
echo "📦 下载依赖..."
mvn dependency:resolve

echo ""
echo "🔨 编译项目..."
if mvn compile; then
    echo "✅ 编译成功！"
    echo ""
    echo "🚀 启动后端服务..."
    echo "   后端服务将在 http://localhost:8080 启动"
    echo "   API文档地址: http://localhost:8080/doc.html"
    echo ""
    echo "💡 提示:"
    echo "   1. 确保MySQL数据库已启动"
    echo "   2. 确保已创建数据库 'hospital_performance'"
    echo "   3. 确保已执行初始化脚本"
    echo ""
    echo "按 Ctrl+C 停止服务"
    echo ""
    
    # 启动Spring Boot应用
    mvn spring-boot:run
else
    echo "❌ 编译失败！请检查错误信息"
    echo ""
    echo "常见问题:"
    echo "   1. 检查Java版本是否为17或更高"
    echo "   2. 检查Maven版本是否为3.6或更高"
    echo "   3. 检查网络连接是否正常"
    echo "   4. 查看详细错误信息: mvn compile -X"
fi
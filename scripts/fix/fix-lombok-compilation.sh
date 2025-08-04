#!/bin/bash

echo "=========================================="
echo "      修复Lombok编译问题"
echo "=========================================="

echo "🔍 问题分析:"
echo "   Lombok注解处理器可能没有正确工作"
echo "   需要确保Maven编译器插件正确配置"

echo ""
echo "🧹 清理Maven缓存..."
mvn clean

echo ""
echo "🔄 重新下载依赖..."
mvn dependency:resolve

echo ""
echo "🔨 尝试编译..."
if mvn compile -X | tee compile.log; then
    echo "✅ 编译成功!"
    
    echo ""
    echo "🚀 尝试启动..."
    timeout 30s mvn spring-boot:run || echo ""
    echo ""
    echo "🎉 如果启动成功，说明Lombok问题已解决!"
else
    echo "❌ 编译仍然失败"
    echo ""
    echo "🔍 检查具体错误:"
    grep -A5 -B5 "ERROR" compile.log | head -20
    echo ""
    echo "💡 可能的解决方案:"
    echo "   1. 检查IDE是否安装Lombok插件"
    echo "   2. 重新导入Maven项目"
    echo "   3. 使用命令行编译: mvn clean compile"
    echo "   4. 检查Java版本: java -version"
    echo ""
    echo "🔧 尝试强制重新生成类:"
    echo "   mvn clean compile -U -Dmaven.compiler.forceJavacCompilerUse=true"
fi

echo ""
echo "📝 编译日志已保存到 compile.log"
echo "=========================================="
#!/bin/bash

echo "=========================================="
echo "      验证修复结果"
echo "=========================================="

echo "🔍 检查是否还有Result<Void>问题..."
if grep -r "public Result<Void>" src/main/java/; then
    echo "❌ 仍有Result<Void>问题需要修复"
else
    echo "✅ Result<Void>问题已全部修复"
fi

echo ""
echo "🔍 检查Apache POI依赖..."
if grep -q "poi" pom.xml; then
    echo "✅ Apache POI依赖已添加"
else
    echo "❌ Apache POI依赖缺失"
fi

echo ""
echo "🔨 尝试编译..."
if mvn compile -q; then
    echo "✅ 编译成功！"
    echo ""
    echo "🎉 所有问题已修复，可以启动系统了！"
    echo ""
    echo "启动命令:"
    echo "   ./compile-and-run.sh"
    echo "   或者"
    echo "   mvn spring-boot:run"
else
    echo "❌ 编译仍有问题，请查看错误信息"
    echo ""
    echo "获取详细错误信息:"
    echo "   mvn compile"
fi

echo ""
echo "=========================================="
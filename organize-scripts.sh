#!/bin/bash

# 脚本整理工具 - 将项目根目录下的测试、调试、修复脚本整理到特定文件夹
# Author: Augment Agent
# Date: $(date +%Y-%m-%d)

echo "=========================================="
echo "  项目脚本整理工具"
echo "=========================================="
echo ""

# 检查是否在项目根目录
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误：请在项目根目录下运行此脚本"
    exit 1
fi

echo "📁 正在创建scripts目录结构..."

# 创建目录结构
mkdir -p scripts/{test,fix,debug,build,database,docs}

echo "✅ 目录结构创建完成"
echo ""

# 统计移动的文件数量
moved_count=0

echo "📋 开始移动脚本文件..."
echo ""

# 移动测试脚本
echo "🧪 移动测试脚本..."
for file in test-*.bat test-*.sh quick-test.bat test-bean-*.sh test-mybatis-*.sh; do
    if [ -f "$file" ]; then
        mv "$file" scripts/test/
        echo "  ✓ $file -> scripts/test/"
        ((moved_count++))
    fi
done

# 移动修复脚本
echo ""
echo "🔧 移动修复脚本..."
for file in fix-*.bat fix-*.sh update-admin-password.sql switch-*.sh; do
    if [ -f "$file" ]; then
        mv "$file" scripts/fix/
        echo "  ✓ $file -> scripts/fix/"
        ((moved_count++))
    fi
done

# 移动调试脚本
echo ""
echo "🔍 移动调试脚本..."
for file in validate-*.bat validate-*.sh check-*.sh verify-*.sh show-*.sh; do
    if [ -f "$file" ]; then
        mv "$file" scripts/debug/
        echo "  ✓ $file -> scripts/debug/"
        ((moved_count++))
    fi
done

# 移动构建脚本
echo ""
echo "🏗️  移动构建脚本..."
for file in build.sh compile-*.sh start*.sh start*.bat; do
    if [ -f "$file" ]; then
        mv "$file" scripts/build/
        echo "  ✓ $file -> scripts/build/"
        ((moved_count++))
    fi
done

# 移动数据库脚本
echo ""
echo "🗄️  移动数据库脚本..."
for file in *mysql*.sh *mysql*.bat test-password.java; do
    if [ -f "$file" ]; then
        mv "$file" scripts/database/
        echo "  ✓ $file -> scripts/database/"
        ((moved_count++))
    fi
done

# 移动文档文件
echo ""
echo "📚 移动相关文档..."
for file in *_FIX_*.md *_SUMMARY.md *_GUIDE.md *_REPORT.md TROUBLESHOOTING.md; do
    if [ -f "$file" ]; then
        mv "$file" scripts/docs/
        echo "  ✓ $file -> scripts/docs/"
        ((moved_count++))
    fi
done

echo ""
echo "🔐 设置执行权限..."
# 设置shell脚本执行权限
find scripts -name "*.sh" -type f -exec chmod +x {} \; 2>/dev/null

echo ""
echo "=========================================="
echo "  脚本整理完成！"
echo "=========================================="
echo ""
echo "📊 统计信息："
echo "  ✅ 总共移动了 $moved_count 个文件"
echo ""
echo "📁 目录结构："
echo "scripts/"
echo "  ├── test/     (测试脚本 - $(ls scripts/test/ 2>/dev/null | wc -l) 个文件)"
echo "  ├── fix/      (修复脚本 - $(ls scripts/fix/ 2>/dev/null | wc -l) 个文件)"
echo "  ├── debug/    (调试脚本 - $(ls scripts/debug/ 2>/dev/null | wc -l) 个文件)"
echo "  ├── build/    (构建脚本 - $(ls scripts/build/ 2>/dev/null | wc -l) 个文件)"
echo "  ├── database/ (数据库脚本 - $(ls scripts/database/ 2>/dev/null | wc -l) 个文件)"
echo "  └── docs/     (相关文档 - $(ls scripts/docs/ 2>/dev/null | wc -l) 个文件)"
echo ""

# 创建README文件
echo "📝 创建scripts目录说明文档..."
cat > scripts/README.md << 'EOF'
# 项目脚本目录

本目录包含了项目的各种脚本文件，按功能分类整理。

## 目录结构

### 📁 test/ - 测试脚本
包含各种测试脚本，用于验证功能和配置。

### 📁 fix/ - 修复脚本  
包含各种修复脚本，用于解决已知问题。

### 📁 debug/ - 调试脚本
包含调试和验证脚本，用于排查问题。

### 📁 build/ - 构建脚本
包含构建、编译和启动脚本。

### 📁 database/ - 数据库脚本
包含数据库相关的脚本和配置。

### 📁 docs/ - 相关文档
包含各种修复总结、指南和报告文档。

## 使用说明

1. 进入对应的子目录
2. 根据需要执行相应的脚本
3. 确保脚本有执行权限：`chmod +x script_name.sh`

## 注意事项

- 执行脚本前请确保在正确的目录下
- 某些脚本可能需要特定的环境或权限
- 建议先阅读脚本内容了解其功能
EOF

echo "✅ scripts/README.md 创建完成"
echo ""

# 显示详细内容（如果文件不多的话）
total_files=$(find scripts -type f | wc -l)
if [ $total_files -le 50 ]; then
    echo "📋 详细文件列表："
    echo ""
    for dir in test fix debug build database docs; do
        if [ -d "scripts/$dir" ] && [ "$(ls -A scripts/$dir 2>/dev/null)" ]; then
            echo "📂 scripts/$dir/:"
            ls -la scripts/$dir/ | grep -v "^total" | sed 's/^/    /'
            echo ""
        fi
    done
fi

echo "🎉 脚本整理完成！使用以下命令查看："
echo "   tree scripts          # 查看目录树"
echo "   ls -la scripts/*/     # 查看所有子目录内容"
echo "   cat scripts/README.md # 查看说明文档"
echo ""
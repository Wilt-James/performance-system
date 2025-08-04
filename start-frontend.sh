#!/bin/bash

echo "=========================================="
echo "      启动前端服务"
echo "=========================================="

# 检查Node.js环境
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到Node.js环境，请先安装Node.js 16或更高版本"
    exit 1
fi

echo "✅ 环境检查通过"
echo "   Node.js版本: $(node --version)"
echo ""

echo "🔧 请确保:"
echo "   1. 后端服务已启动 (http://localhost:8080)"
echo "   2. 数据库连接正常"
echo ""

echo "🎨 正在启动前端服务..."
cd frontend

# 检查是否已安装依赖
if [ ! -d "node_modules" ]; then
    echo "   正在安装前端依赖..."
    npm install
fi

echo "   前端服务将在 http://localhost:3000 启动"
echo ""

echo "👤 默认登录账号:"
echo "   管理员: admin / 123456"
echo "   普通用户: user / 123456"
echo ""

# 启动前端开发服务器
npm run dev
#!/bin/bash

# 院内绩效考核系统启动脚本

echo "=========================================="
echo "      院内绩效考核系统启动脚本"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请先安装JDK 8或更高版本"
    exit 1
fi

# 检查Node.js环境
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到Node.js环境，请先安装Node.js 16或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请先安装Maven 3.6或更高版本"
    exit 1
fi

# 检查MySQL是否运行
if ! command -v mysql &> /dev/null; then
    echo "⚠️  警告: 未找到MySQL命令，请确保MySQL服务正在运行"
fi

echo "✅ 环境检查通过，开始启动系统..."
echo ""

# 显示系统信息
echo "📋 系统信息:"
echo "   Java版本: $(java -version 2>&1 | head -n 1)"
echo "   Node.js版本: $(node --version)"
echo "   Maven版本: $(mvn --version | head -n 1)"
echo ""

echo "🔧 系统功能模块:"
echo "   ✓ 用户认证与权限管理"
echo "   ✓ 部门管理"
echo "   ✓ 绩效指标管理"
echo "   ✓ 绩效计算引擎 (工作量法、KPI法、成本核算法)"
echo "   ✓ 多口径统计查询"
echo "   ✓ 医院运营评分"
echo ""

# 启动后端服务
echo "🚀 正在启动后端服务..."
echo "   请确保已经创建数据库并执行了初始化脚本 (src/main/resources/sql/init.sql)"
echo "   后端服务将在 http://localhost:8080 启动"

# 在后台启动Spring Boot应用
nohup mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo "   后端服务PID: $BACKEND_PID"

# 等待后端服务启动
echo "   等待后端服务启动..."
sleep 10

# 检查后端服务是否启动成功
echo "   正在检查后端服务状态..."
for i in {1..30}; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "   ✅ 后端服务启动成功!"
        break
    elif curl -f http://localhost:8080 > /dev/null 2>&1; then
        echo "   ✅ 后端服务启动成功!"
        break
    else
        echo "   等待后端服务启动... ($i/30)"
        sleep 2
    fi
done

if ! curl -f http://localhost:8080 > /dev/null 2>&1; then
    echo "   ⚠️  后端服务启动失败，请检查 backend.log 文件"
    echo "   常见问题:"
    echo "   1. 检查MySQL数据库是否启动"
    echo "   2. 检查数据库连接配置是否正确"
    echo "   3. 检查端口8080是否被占用"
fi
echo ""

# 启动前端服务
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

echo "📊 演示数据:"
echo "   ✓ 已初始化示例部门数据"
echo "   ✓ 已配置常用绩效指标"
echo "   ✓ 已生成模拟绩效数据"
echo "   ✓ 已计算运营评分示例"
echo ""

echo "💡 使用提示:"
echo "   1. 首次使用请先登录系统"
echo "   2. 建议从'绩效计算'模块开始体验"
echo "   3. 可在'多口径统计'中查看详细分析"
echo "   4. '运营评分'提供医院整体评估"
echo ""

# 启动前端开发服务器
npm run dev

echo ""
echo "=========================================="
echo "🎉 系统启动完成!"
echo ""
echo "📱 访问地址:"
echo "   前端系统: http://localhost:3000"
echo "   后端API: http://localhost:8080"
echo "   API文档: http://localhost:8080/doc.html"
echo "   数据库监控: http://localhost:8080/druid"
echo ""
echo "📈 系统架构:"
echo "   后端: Spring Boot + MyBatis-Plus + MySQL"
echo "   前端: Vue 3 + Element Plus + ECharts"
echo "   安全: Spring Security + JWT"
echo ""
echo "感谢使用院内绩效考核系统！"
echo "=========================================="
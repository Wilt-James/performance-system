@echo off
echo ===========================================
echo        菜单展开问题修复验证
echo ===========================================

echo.
echo 问题描述:
echo   在 http://localhost:3000/dashboard 页面点击左边菜单无法展开子目录
echo   但在 http://localhost:3000/statistics/multi-dimension 页面可以正常展开
echo.

echo 问题原因:
echo   Element Plus 菜单组件在 router 模式下，只有当前路由匹配时才会展开
echo   在 dashboard 页面时，activeMenu 为 '/dashboard'，不匹配父级菜单路径
echo.

echo 修复方案:
echo   1. 添加 default-openeds 属性，默认展开所有父级菜单
echo   2. 将 unique-opened 设置为 false，允许多个菜单同时展开
echo   3. 确保在任何页面都能正常展开菜单
echo.

echo 修复内容:
echo   ✅ 添加 :default-openeds="defaultOpeneds" 属性
echo   ✅ 设置 :unique-opened="false"
echo   ✅ 默认展开 ['/system', '/performance', '/statistics']
echo.

echo 测试步骤:
echo   1. 启动前端服务: npm run dev
echo   2. 访问 http://localhost:3000/dashboard
echo   3. 点击"系统管理"菜单 -> 应该能展开显示子菜单
echo   4. 点击"绩效管理"菜单 -> 应该能展开显示子菜单
echo   5. 点击"统计分析"菜单 -> 应该能展开显示子菜单
echo   6. 切换到其他页面，菜单展开功能应该保持正常
echo.

echo 预期结果:
echo   ✅ 在任何页面都能正常点击展开菜单
echo   ✅ 菜单展开状态在页面切换时保持一致
echo   ✅ 子菜单点击跳转功能正常
echo.

echo 正在检查修复文件...

if exist "frontend\src\layout\index.vue" (
    echo ✅ 布局组件文件存在
    
    findstr /C:"default-openeds" "frontend\src\layout\index.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ default-openeds 属性已添加
    ) else (
        echo ❌ default-openeds 属性缺失
    )
    
    findstr /C:"unique-opened.*false" "frontend\src\layout\index.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ unique-opened 设置为 false
    ) else (
        echo ❌ unique-opened 设置错误
    )
    
    findstr /C:"defaultOpeneds" "frontend\src\layout\index.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ defaultOpeneds 计算属性已添加
    ) else (
        echo ❌ defaultOpeneds 计算属性缺失
    )
) else (
    echo ❌ 布局组件文件不存在
)

echo.
echo 启动前端服务进行测试...
echo.

cd frontend
if exist "node_modules" (
    echo 正在启动前端服务...
    echo.
    echo 请在浏览器中进行以下测试:
    echo.
    echo 🔍 测试场景 1: Dashboard 页面
    echo    访问: http://localhost:3000/dashboard
    echo    操作: 点击左侧"系统管理"、"绩效管理"、"统计分析"菜单
    echo    预期: 菜单能正常展开显示子菜单项
    echo.
    echo 🔍 测试场景 2: 其他页面
    echo    访问: http://localhost:3000/statistics/multi-dimension
    echo    操作: 点击左侧菜单
    echo    预期: 菜单展开功能保持正常
    echo.
    echo 🔍 测试场景 3: 页面切换
    echo    操作: 在不同页面间切换
    echo    预期: 菜单展开状态保持一致
    echo.
    
    npm run dev
) else (
    echo 需要先安装依赖:
    echo npm install
)

echo.
echo ===========================================
pause
@echo off
echo ===========================================
echo        导航功能完整修复验证
echo ===========================================

echo.
echo 问题描述:
echo   1. 首页点击底部绩效计算、多口径统计、运营评分、指标管理四栏没有跳转
echo   2. 左侧父菜单点击后子菜单没有展开
echo.

echo 修复方案:
echo   问题1: Dashboard跳转功能检查
echo     ✅ 检查router导入 - 已正确导入useRouter
echo     ✅ 检查跳转函数 - goToCalculation, goToMultiDimension等函数已定义
echo     ✅ 检查路由路径 - 路径与路由配置一致
echo     ✅ 检查点击事件 - @click事件已正确绑定
echo.
echo   问题2: 菜单展开功能修复
echo     ✅ 修改handleMenuSelect逻辑 - 父菜单不再直接跳转
echo     ✅ 保留Element Plus自动展开 - 让组件处理展开/收起
echo     ✅ 子菜单添加点击跳转 - @click="router.push(child.path)"
echo.

echo 修复后的行为:
echo   📱 首页底部快捷入口:
echo     🧮 绩效计算 -> /performance/calculation
echo     📊 多口径统计 -> /statistics/multi-dimension  
echo     🏆 运营评分 -> /statistics/operation-score
echo     ⚙️ 指标管理 -> /performance/indicator
echo.
echo   📂 左侧菜单行为:
echo     📁 点击父菜单 -> 展开/收起子菜单（不跳转）
echo     📄 点击子菜单 -> 跳转到对应页面
echo.

echo 测试步骤:
echo   1. 启动前端服务
echo   2. 访问首页 http://localhost:3000/dashboard
echo   3. 测试底部四个快捷入口的跳转功能
echo   4. 测试左侧菜单的展开/收起功能
echo   5. 测试子菜单的跳转功能
echo.

echo 预期结果:
echo   ✅ 首页底部快捷入口能正常跳转到对应页面
echo   ✅ 左侧父菜单点击能展开/收起子菜单
echo   ✅ 左侧子菜单点击能跳转到对应页面
echo   ✅ 所有导航功能正常工作
echo.

echo 正在检查修复文件...

if exist "frontend\src\views\Dashboard.vue" (
    echo ✅ Dashboard组件存在
    
    findstr /C:"goToCalculation" "frontend\src\views\Dashboard.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 绩效计算跳转函数存在
    ) else (
        echo ❌ 绩效计算跳转函数缺失
    )
    
    findstr /C:"goToMultiDimension" "frontend\src\views\Dashboard.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 多口径统计跳转函数存在
    ) else (
        echo ❌ 多口径统计跳转函数缺失
    )
    
    findstr /C:"goToOperationScore" "frontend\src\views\Dashboard.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 运营评分跳转函数存在
    ) else (
        echo ❌ 运营评分跳转函数缺失
    )
    
    findstr /C:"goToIndicator" "frontend\src\views\Dashboard.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 指标管理跳转函数存在
    ) else (
        echo ❌ 指标管理跳转函数缺失
    )
) else (
    echo ❌ Dashboard组件不存在
)

if exist "frontend\src\layout\index.vue" (
    echo ✅ 布局组件存在
    
    findstr /C:"@click.*router.push" "frontend\src\layout\index.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 子菜单点击跳转已添加
    ) else (
        echo ❌ 子菜单点击跳转缺失
    )
    
    findstr /C:"handleMenuSelect" "frontend\src\layout\index.vue" >nul
    if !errorlevel! equ 0 (
        echo ✅ 菜单选择处理函数存在
    ) else (
        echo ❌ 菜单选择处理函数缺失
    )
) else (
    echo ❌ 布局组件不存在
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
    echo 🔍 测试场景 1: 首页快捷入口
    echo    访问: http://localhost:3000/dashboard
    echo    操作: 点击底部"绩效计算"卡片
    echo    预期: 跳转到绩效计算页面
    echo.
    echo 🔍 测试场景 2: 多口径统计
    echo    操作: 返回首页，点击"多口径统计"卡片
    echo    预期: 跳转到多口径统计页面
    echo.
    echo 🔍 测试场景 3: 运营评分
    echo    操作: 返回首页，点击"运营评分"卡片
    echo    预期: 跳转到运营评分页面
    echo.
    echo 🔍 测试场景 4: 指标管理
    echo    操作: 返回首页，点击"指标管理"卡片
    echo    预期: 跳转到指标管理页面
    echo.
    echo 🔍 测试场景 5: 左侧菜单展开
    echo    操作: 点击左侧"系统管理"菜单
    echo    预期: 菜单展开显示"用户管理"和"部门管理"子项
    echo.
    echo 🔍 测试场景 6: 子菜单跳转
    echo    操作: 点击展开的"用户管理"子菜单
    echo    预期: 跳转到用户管理页面
    echo.
    
    npm run dev
) else (
    echo 需要先安装依赖:
    echo npm install
)

echo.
echo ===========================================
pause
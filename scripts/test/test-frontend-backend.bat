@echo off
echo ===========================================
echo        前后端连接测试脚本
echo ===========================================

echo.
echo 1. 检查后端服务状态...

netstat -an | findstr ":8080" >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ 后端服务(8080端口)正在运行
    
    echo.
    echo 2. 测试后端API接口...
    
    echo    测试登录接口...
    curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}" -w "状态码: %%{http_code}" 2>nul
    if %errorlevel% neq 0 echo    ✗ 登录接口测试失败
    
    echo.
    echo    测试绩效计算接口...
    curl -s -X GET http://localhost:8080/api/performance/calculation/history -w "状态码: %%{http_code}" 2>nul
    if %errorlevel% neq 0 echo    ✗ 绩效计算接口测试失败
    
    echo.
    echo    测试多维度统计接口...
    curl -s -X GET http://localhost:8080/api/statistics/multi-dimension/data -w "状态码: %%{http_code}" 2>nul
    if %errorlevel% neq 0 echo    ✗ 多维度统计接口测试失败
    
    echo.
    echo    测试运营评分接口...
    curl -s -X GET http://localhost:8080/api/statistics/operation-score/data -w "状态码: %%{http_code}" 2>nul
    if %errorlevel% neq 0 echo    ✗ 运营评分接口测试失败
    
    echo.
    echo    测试绩效指标接口...
    curl -s -X GET http://localhost:8080/api/performance/indicator/list -w "状态码: %%{http_code}" 2>nul
    if %errorlevel% neq 0 echo    ✗ 绩效指标接口测试失败
    
) else (
    echo    ✗ 后端服务(8080端口)未运行
    echo.
    echo 请先启动后端服务:
    echo    mvn spring-boot:run -Dspring-boot.run.profiles=dev
)

echo.
echo 3. 检查前端服务状态...

netstat -an | findstr ":3000" >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ 前端服务(3000端口)正在运行
    
    echo.
    echo 4. 测试前端页面访问...
    
    echo    测试前端首页...
    curl -s -o nul -w "状态码: %%{http_code}" http://localhost:3000/ 2>nul
    if %errorlevel% neq 0 echo    ✗ 前端首页访问失败
    
) else (
    echo    ✗ 前端服务(3000端口)未运行
    echo.
    echo 请先启动前端服务:
    echo    cd frontend ^&^& npm run dev
)

echo.
echo 5. 前端代理配置检查...
echo    前端代理配置: /api -^> http://localhost:8080
echo    这意味着前端的 /api/xxx 请求会被代理到 http://localhost:8080/api/xxx

echo.
echo 6. 问题排查建议...
echo.
echo 如果前端页面没有反应，请检查:
echo    1. 浏览器开发者工具 -^> Network 标签页，查看API请求状态
echo    2. 浏览器开发者工具 -^> Console 标签页，查看JavaScript错误
echo    3. 确保前后端服务都在运行
echo    4. 检查API请求路径是否正确

echo.
echo 7. 启动服务命令...
echo.
echo 启动后端服务:
echo    mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo.
echo 启动前端服务:
echo    cd frontend
echo    npm install  # 首次运行需要安装依赖
echo    npm run dev

echo.
echo 8. 访问地址...
echo    前端地址: http://localhost:3000
echo    后端API: http://localhost:8080/api

echo.
echo ===========================================
pause
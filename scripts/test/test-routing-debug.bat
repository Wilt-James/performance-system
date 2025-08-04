@echo off
echo ===========================================
echo        前端路由问题调试指南
echo ===========================================

echo.
echo 问题描述:
echo   在首页 http://localhost:3000/dashboard 时点击其他页面栏目无法切换
echo.

echo 可能的原因和排查步骤:
echo.

echo 1. JavaScript错误导致页面阻塞
echo    - 打开浏览器开发者工具 (F12)
echo    - 查看Console标签页是否有错误信息
echo    - 查看Network标签页是否有失败的请求
echo.

echo 2. API请求阻塞
echo    - 检查是否有长时间pending的API请求
echo    - 检查是否有CORS错误
echo    - 检查后端服务是否正常响应
echo.

echo 3. Vue Router问题
echo    - 检查路由配置是否正确
echo    - 检查是否有路由守卫阻塞
echo    - 检查是否有组件渲染错误
echo.

echo 4. 组件生命周期问题
echo    - 检查onMounted是否有无限循环
echo    - 检查watch是否有问题
echo    - 检查computed是否有循环依赖
echo.

echo 调试步骤:
echo.

echo 步骤1: 检查浏览器控制台
echo   1. 打开 http://localhost:3000/dashboard
echo   2. 按F12打开开发者工具
echo   3. 查看Console标签页的错误信息
echo   4. 查看Network标签页的请求状态
echo.

echo 步骤2: 测试简单路由
echo   1. 在地址栏直接输入: http://localhost:3000/performance/indicator
echo   2. 看是否能正常访问
echo   3. 再尝试点击菜单切换
echo.

echo 步骤3: 检查API请求
echo   1. 在Network标签页中查看所有请求
echo   2. 找出状态为pending或failed的请求
echo   3. 检查请求的响应时间
echo.

echo 步骤4: 临时禁用API调用
echo   1. 注释掉Dashboard.vue中的API调用
echo   2. 重新测试路由切换
echo   3. 逐步恢复API调用找出问题
echo.

echo 常见解决方案:
echo.

echo 1. 如果是API超时:
echo    - 检查后端服务是否正常
echo    - 增加axios超时时间
echo    - 添加请求取消机制
echo.

echo 2. 如果是JavaScript错误:
echo    - 修复控制台显示的错误
echo    - 检查组件的props和data
echo    - 检查模板语法错误
echo.

echo 3. 如果是路由守卫问题:
echo    - 检查router/index.js中的beforeEach
echo    - 确保next()被正确调用
echo    - 检查认证逻辑
echo.

echo 4. 如果是组件问题:
echo    - 检查组件的template语法
echo    - 检查v-if/v-for的条件
echo    - 检查组件的import和export
echo.

echo 临时解决方案:
echo   如果问题持续存在，可以尝试:
echo   1. 重启前端开发服务器
echo   2. 清除浏览器缓存
echo   3. 使用无痕模式测试
echo   4. 检查是否有浏览器扩展干扰
echo.

echo ===========================================
echo 请按照以上步骤逐一排查，并记录发现的错误信息
echo ===========================================
pause
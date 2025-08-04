@echo off
echo ===========================================
echo        路由和接口修复验证测试
echo ===========================================

set BASE_URL=http://localhost:8080

echo.
echo 1. 测试修复后的绩效指标接口...
echo.

echo 测试绩效指标列表接口 (新增的/list接口):
curl -s -X GET "%BASE_URL%/api/performance/indicator/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 测试绩效指标列表接口 (带参数):
curl -s -X GET "%BASE_URL%/api/performance/indicator/list?keyword=门诊" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 测试绩效指标分页接口:
curl -s -X GET "%BASE_URL%/api/performance/indicator/page?current=1&size=10" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 测试绩效指标详情接口 (通过ID):
curl -s -X GET "%BASE_URL%/api/performance/indicator/1" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo.
echo 2. 测试其他修复的接口...
echo.

echo 测试多维度统计数据接口:
curl -s -X GET "%BASE_URL%/api/statistics/multi-dimension/data?period=2025-03&statisticsType=2" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 测试运营评分数据接口:
curl -s -X GET "%BASE_URL%/api/statistics/operation-score/data" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo.
echo ===========================================
echo 修复内容总结:
echo.
echo 1. ✅ 修复绩效指标接口路径冲突:
echo    - 添加了 /api/performance/indicator/list 接口
echo    - 避免与 /api/performance/indicator/{id} 冲突
echo.
echo 2. ✅ 修复运营评分页面数据处理:
echo    - 修复了updateCharts函数中的数据引用问题
echo    - 添加了空值保护
echo.
echo 3. ✅ 前端路由问题排查:
echo    - 检查了路由配置
echo    - 修复了可能导致页面卡死的数据问题
echo.
echo 如果以上接口返回200状态码，说明后端修复成功！
echo 前端路由问题需要在浏览器中测试验证。
echo.
echo 测试步骤:
echo 1. 重启后端服务
echo 2. 访问 http://localhost:3000
echo 3. 进入运营评分页面
echo 4. 尝试切换到其他页面
echo ===========================================
pause
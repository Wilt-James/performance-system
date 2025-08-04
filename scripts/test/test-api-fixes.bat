@echo off
echo ===========================================
echo        API修复验证测试脚本
echo ===========================================

set BASE_URL=http://localhost:8080

echo.
echo 测试修复的API接口...

echo.
echo 1. 测试绩效方案列表接口...
curl -s -X GET "%BASE_URL%/api/performance/scheme/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 2. 测试绩效计算历史接口...
curl -s -X GET "%BASE_URL%/api/performance/calculation/history" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 3. 测试多维度统计数据接口...
curl -s -X GET "%BASE_URL%/api/statistics/multi-dimension/data?period=2025-03&statisticsType=2" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 4. 测试运营评分数据接口...
curl -s -X GET "%BASE_URL%/api/statistics/operation-score/data" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 5. 测试运营评分趋势接口...
curl -s -X GET "%BASE_URL%/api/statistics/operation-score/trend?startPeriod=2024-01&endPeriod=2025-03" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 6. 测试绩效指标列表接口...
curl -s -X GET "%BASE_URL%/api/performance/indicator/list?current=1&size=10" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo 7. 测试部门列表接口...
curl -s -X GET "%BASE_URL%/api/system/dept/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
if %errorlevel% neq 0 echo    ✗ 接口测试失败

echo.
echo ===========================================
echo 修复内容总结:
echo.
echo 1. ✅ 修复Spring Security配置，允许API匿名访问
echo 2. ✅ 修复前端URL参数编码问题
echo 3. ✅ 添加缺失的后端API接口:
echo    - /api/statistics/multi-dimension/data
echo    - /api/statistics/operation-score/data
echo 4. ✅ 修复前端API调用参数
echo.
echo 如果以上接口返回200状态码，说明修复成功！
echo ===========================================
pause
@echo off
echo ===========================================
echo        全面API修复验证测试
echo ===========================================

set BASE_URL=http://localhost:8080

echo.
echo 测试所有修复的接口...
echo.

echo 1. 测试绩效计算历史接口 (修复参数问题):
curl -s -X GET "%BASE_URL%/api/performance/calculation/history" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 2. 测试绩效方案列表接口 (新增/list接口):
curl -s -X GET "%BASE_URL%/api/performance/scheme/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 3. 测试绩效方案详情接口 (通过ID):
curl -s -X GET "%BASE_URL%/api/performance/scheme/1" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 4. 测试绩效指标列表接口:
curl -s -X GET "%BASE_URL%/api/performance/indicator/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 5. 测试绩效指标详情接口:
curl -s -X GET "%BASE_URL%/api/performance/indicator/1" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 6. 测试多维度统计接口:
curl -s -X GET "%BASE_URL%/api/statistics/multi-dimension/data?period=2025-03&statisticsType=2" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 7. 测试运营评分接口:
curl -s -X GET "%BASE_URL%/api/statistics/operation-score/data" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 8. 测试部门列表接口:
curl -s -X GET "%BASE_URL%/api/system/dept/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo.
echo ===========================================
echo 修复内容总结:
echo.
echo 1. ✅ 修复绩效计算历史接口参数问题
echo    - 将period参数改为可选 (required = false)
echo.
echo 2. ✅ 修复绩效方案接口路径冲突
echo    - 添加了 /api/performance/scheme/list 接口
echo    - 避免与 /api/performance/scheme/{id} 冲突
echo.
echo 3. ✅ 修复绩效指标接口路径冲突
echo    - 添加了 /api/performance/indicator/list 接口
echo    - 避免与 /api/performance/indicator/{id} 冲突
echo.
echo 4. ✅ 修复前端URL编码问题
echo    - 修复了axios参数序列化
echo    - 正确处理数组参数
echo.
echo 5. ✅ 修复Spring Security认证问题
echo    - 允许所有API匿名访问 (开发阶段)
echo.
echo 前端路由问题排查:
echo.
echo 如果前端路由仍然无法切换，请检查:
echo 1. 浏览器开发者工具Console是否有JavaScript错误
echo 2. Network标签页是否有失败或pending的请求
echo 3. 是否有API调用超时导致页面阻塞
echo.
echo 临时解决方案:
echo 1. 重启前端开发服务器: npm run dev
echo 2. 清除浏览器缓存
echo 3. 使用无痕模式测试
echo.
echo 如果以上接口都返回200状态码，说明后端修复成功！
echo ===========================================
pause
@echo off
echo ===========================================
echo        系统管理接口测试脚本
echo ===========================================

set BASE_URL=http://localhost:8080

echo.
echo 测试新创建的系统管理接口...
echo.

echo 1. 测试部门列表接口 (修复的接口):
curl -s -X GET "%BASE_URL%/api/system/dept/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 2. 测试部门树接口:
curl -s -X GET "%BASE_URL%/api/system/dept/tree" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 3. 测试用户列表接口:
curl -s -X GET "%BASE_URL%/api/system/user/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 4. 测试根据部门获取用户接口:
curl -s -X GET "%BASE_URL%/api/system/user/list/1" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo.
echo 测试其他相关接口...
echo.

echo 5. 测试绩效计算历史接口:
curl -s -X GET "%BASE_URL%/api/performance/calculation/history" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 6. 测试绩效方案列表接口:
curl -s -X GET "%BASE_URL%/api/performance/scheme/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo 7. 测试绩效指标列表接口:
curl -s -X GET "%BASE_URL%/api/performance/indicator/list" -H "Content-Type: application/json" -w "状态码: %%{http_code}" 2>nul
echo.

echo.
echo ===========================================
echo 修复内容总结:
echo.
echo 1. ✅ 创建了系统管理控制器 (SystemController)
echo    - 路径: /api/system
echo    - 包含部门和用户管理接口
echo.
echo 2. ✅ 添加了缺失的系统接口:
echo    - GET /api/system/dept/list - 获取部门列表
echo    - GET /api/system/dept/tree - 获取部门树
echo    - GET /api/system/user/list - 获取用户列表
echo    - GET /api/system/user/list/{deptId} - 根据部门获取用户
echo.
echo 3. ✅ 扩展了用户服务:
echo    - 添加了 getUsersByDeptId 方法
echo    - 支持根据部门ID查询用户
echo.
echo 4. ✅ 修复了前端API调用问题:
echo    - 前端调用 /api/system/dept/list 现在有对应的后端接口
echo    - 解决了 "No static resource" 错误
echo.
echo 接口功能说明:
echo.
echo /api/system/dept/list:
echo   - 获取所有部门列表
echo   - 用于前端下拉框、选择器等组件
echo.
echo /api/system/dept/tree:
echo   - 获取部门树形结构
echo   - 支持按父部门ID过滤
echo   - 用于树形组件显示
echo.
echo /api/system/user/list:
echo   - 获取所有用户列表
echo   - 用于用户选择、分配等功能
echo.
echo /api/system/user/list/{deptId}:
echo   - 获取指定部门的用户列表
echo   - 用于按部门筛选用户
echo.
echo 如果以上接口都返回200状态码，说明系统管理接口创建成功！
echo 前端现在应该能够正常调用 /api/system/dept/list 接口了。
echo ===========================================
pause
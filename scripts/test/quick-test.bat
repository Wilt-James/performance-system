@echo off
echo ==========================================
echo       快速测试登录功能
echo ==========================================

echo.
echo 测试登录接口...
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"123456\"}" ^
  -w "%%{http_code}"

echo.
echo.
echo 如果看到HTTP状态码200和包含token的JSON响应，说明修复成功！
echo.
echo 常见问题排查:
echo 1. 如果连接被拒绝 - 应用可能未启动
echo 2. 如果404错误 - 路径映射问题
echo 3. 如果500错误 - 数据库或业务逻辑问题
echo 4. 如果405错误 - HTTP方法不支持
echo.
pause
@echo off
echo ==========================================
echo       修复admin用户密码问题
echo ==========================================

echo.
echo 问题分析:
echo   数据库中admin用户的BCrypt密码不匹配明文密码123456
echo   需要更新为正确的BCrypt加密值

echo.
echo 当前密码信息:
echo   用户名: admin
echo   明文密码: 123456
echo   错误的BCrypt: $2a$10$7JB720yubVSOfvVWbGRCu.VGaLIxZHjmQxzQbHjPT9db9dF00miD.
echo   正确的BCrypt: $2a$10$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi

echo.
echo 修复方案:
echo 1. 检查MySQL连接...

mysql -h localhost -P 3306 -u root -proot -e "SELECT 1;" >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ MySQL连接正常
    
    echo.
    echo 2. 更新admin用户密码...
    
    mysql -h localhost -P 3306 -u root -proot hospital_performance -e "UPDATE sys_user SET password = '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi' WHERE username = 'admin';"
    
    if %errorlevel% equ 0 (
        echo    ✓ admin用户密码更新成功
        
        echo.
        echo 3. 验证密码更新...
        
        mysql -h localhost -P 3306 -u root -proot hospital_performance -e "SELECT username, password FROM sys_user WHERE username='admin';"
        
        echo.
        echo 修复完成！现在可以使用以下凭据登录:
        echo   用户名: admin
        echo   密码: 123456
    ) else (
        echo    ✗ 密码更新失败
    )
) else (
    echo    ✗ MySQL连接失败
    echo.
    echo 请确保:
    echo   1. MySQL服务正在运行
    echo   2. 用户名密码正确 (root/root)
    echo   3. 端口3306可访问
)

echo.
echo 手动修复方法:
echo   如果自动修复失败，可以手动执行以下SQL:
echo   mysql -u root -proot hospital_performance
echo   UPDATE sys_user SET password = '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi' WHERE username = 'admin';

echo.
echo 修复后测试:
echo   1. 启动应用: mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo   2. 测试登录: 使用Postman或curl测试登录接口

echo.
echo ==========================================
pause
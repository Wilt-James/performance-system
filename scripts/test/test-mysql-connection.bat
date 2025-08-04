@echo off
echo ==========================================
echo       MySQL数据库连接测试
echo ==========================================

set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=hospital_performance
set DB_USER=root
set DB_PASS=root

echo.
echo 📋 数据库连接信息:
echo    主机: %DB_HOST%:%DB_PORT%
echo    数据库: %DB_NAME%
echo    用户名: %DB_USER%
echo    密码: %DB_PASS%

echo.
echo 🔍 检查MySQL服务状态...

where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MySQL客户端未安装
    echo    请先安装MySQL客户端
    pause
    exit /b 1
) else (
    echo ✅ MySQL客户端已安装
)

echo.
echo 🧪 测试数据库连接...

mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -e "SELECT 1 as test;" 2>nul
if %errorlevel% neq 0 (
    echo ❌ 数据库连接失败
    echo.
    echo 💡 可能的原因:
    echo    1. MySQL服务未启动
    echo    2. 用户名或密码错误
    echo    3. 端口3306被占用或防火墙阻止
    echo    4. MySQL配置不允许远程连接
    echo.
    echo 🔧 解决方案:
    echo    1. 启动MySQL服务: net start mysql
    echo    2. 检查用户权限: mysql -u root -p
    echo    3. 检查端口: netstat -an ^| findstr 3306
    pause
    exit /b 1
) else (
    echo ✅ 数据库连接成功
)

echo.
echo 🗄️ 检查数据库是否存在...

mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -e "USE %DB_NAME%; SELECT 1;" 2>nul
if %errorlevel% neq 0 (
    echo ❌ 数据库 %DB_NAME% 不存在
    echo.
    echo 🔧 创建数据库:
    echo    mysql -u root -p -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo.
    set /p create_db=是否现在创建数据库? (y/N): 
    if /i "%create_db%"=="y" (
        mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        if %errorlevel% equ 0 (
            echo ✅ 数据库创建成功
        ) else (
            echo ❌ 数据库创建失败
        )
    )
) else (
    echo ✅ 数据库 %DB_NAME% 存在
    
    echo.
    echo 📊 检查数据库表...
    
    for /f %%i in ('mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='%DB_NAME%';" -s -N 2^>nul') do set TABLE_COUNT=%%i
    
    if defined TABLE_COUNT (
        if %TABLE_COUNT% gtr 0 (
            echo ✅ 数据库包含 %TABLE_COUNT% 个表
            
            for /f %%i in ('mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SELECT COUNT(*) FROM sys_user;" -s -N 2^>nul') do set USER_COUNT=%%i
            if defined USER_COUNT (
                echo ✅ 用户表存在，包含 %USER_COUNT% 条记录
            ) else (
                echo ⚠️  用户表不存在，需要初始化数据
            )
        ) else (
            echo ⚠️  数据库为空，需要初始化表结构
        )
    )
)

echo.
echo 🚀 启动应用测试...

set /p start_app=是否使用MySQL配置启动应用? (y/N): 
if /i "%start_app%"=="y" (
    echo    使用开发环境配置启动...
    echo.
    echo 🔄 启动Spring Boot应用...
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
)

echo.
echo 📝 其他启动选项:
echo    默认配置:     mvn spring-boot:run
echo    开发环境:     mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo    H2内存数据库: mvn spring-boot:run -Dspring-boot.run.profiles=h2

echo.
echo 🌐 相关地址:
echo    应用地址: http://localhost:8080
echo    登录接口: http://localhost:8080/api/auth/login
echo    Druid监控: http://localhost:8080/druid

echo.
echo ==========================================
pause
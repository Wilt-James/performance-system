@echo off
echo ==========================================
echo       数据源配置验证
echo ==========================================

echo.
echo 📋 检查配置文件存在性...

if exist "src\main\resources\application.yml" (
    echo ✅ application.yml 存在
) else (
    echo ❌ application.yml 不存在
)

if exist "src\main\resources\application-dev.yml" (
    echo ✅ application-dev.yml 存在
) else (
    echo ❌ application-dev.yml 不存在
)

if exist "src\main\resources\application-minimal.yml" (
    echo ✅ application-minimal.yml 存在
) else (
    echo ❌ application-minimal.yml 不存在
)

echo.
echo 🔍 检查数据源配置参数...

echo.
echo 📊 MySQL连接参数检查:

echo    application.yml:
findstr /C:"jdbc:mysql://localhost:3306/hospital_performance" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ URL配置正确
) else (
    echo    ❌ URL配置错误
)

findstr /C:"username: root" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 用户名配置正确
) else (
    echo    ❌ 用户名配置错误
)

findstr /C:"password: root" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 密码配置正确
) else (
    echo    ❌ 密码配置错误
)

echo.
echo    application-dev.yml:
findstr /C:"jdbc:mysql://localhost:3306/hospital_performance" src\main\resources\application-dev.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ URL配置正确
) else (
    echo    ❌ URL配置错误
)

findstr /C:"username: root" src\main\resources\application-dev.yml >nul 2>&1 && findstr /C:"password: root" src\main\resources\application-dev.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 用户名密码配置正确
) else (
    echo    ❌ 用户名密码配置错误
)

echo.
echo    application-minimal.yml:
findstr /C:"username: root" src\main\resources\application-minimal.yml >nul 2>&1 && findstr /C:"password: root" src\main\resources\application-minimal.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 用户名密码配置正确
) else (
    echo    ❌ 用户名密码配置错误
)

echo.
echo 🔧 Druid连接池配置检查:

findstr /C:"type: com.alibaba.druid.pool.DruidDataSource" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ Druid数据源类型配置正确
) else (
    echo    ❌ Druid数据源类型配置错误
)

findstr /C:"driver-class-name: com.mysql.cj.jdbc.Driver" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ MySQL驱动配置正确
) else (
    echo    ❌ MySQL驱动配置错误
)

findstr /C:"validation-query: SELECT 1" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 验证查询语句正确
) else (
    echo    ❌ 验证查询语句错误
)

echo.
echo 📁 MyBatis配置检查:

findstr /C:"map-underscore-to-camel-case: true" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 驼峰命名映射配置正确
) else (
    echo    ❌ 驼峰命名映射配置错误
)

findstr /C:"id-type: ASSIGN_ID" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ 主键类型配置正确
) else (
    echo    ❌ 主键类型配置错误
)

findstr /C:"# mapper-locations:" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ XML映射文件配置已注释（使用注解方式）
) else (
    echo    ⚠️  建议注释XML映射文件配置
)

echo.
echo 🗄️ SQL脚本检查:

if exist "src\main\resources\sql\init.sql" (
    echo    ✅ init.sql 存在
    
    findstr /C:"CREATE TABLE.*sys_user" src\main\resources\sql\init.sql >nul 2>&1
    if %errorlevel% equ 0 (
        echo    ✅ 用户表创建语句存在
    ) else (
        echo    ❌ 用户表创建语句不存在
    )
    
    findstr /C:"INSERT INTO.*sys_user" src\main\resources\sql\init.sql >nul 2>&1
    if %errorlevel% equ 0 (
        echo    ✅ 用户数据插入语句存在
    ) else (
        echo    ❌ 用户数据插入语句不存在
    )
) else (
    echo    ❌ init.sql 不存在
)

echo.
echo 📦 依赖检查:

if exist "pom.xml" (
    findstr /C:"mysql-connector-j" pom.xml >nul 2>&1
    if %errorlevel% equ 0 (
        echo    ✅ MySQL驱动依赖存在
    ) else (
        echo    ❌ MySQL驱动依赖不存在
    )
    
    findstr /C:"druid-spring-boot-starter" pom.xml >nul 2>&1
    if %errorlevel% equ 0 (
        echo    ✅ Druid依赖存在
    ) else (
        echo    ❌ Druid依赖不存在
    )
    
    findstr /C:"mybatis-plus-boot-starter" pom.xml >nul 2>&1
    if %errorlevel% equ 0 (
        echo    ✅ MyBatis-Plus依赖存在
    ) else (
        echo    ❌ MyBatis-Plus依赖不存在
    )
) else (
    echo    ❌ pom.xml 不存在
)

echo.
echo 🎯 配置建议:
echo    1. 确保MySQL服务正在运行
echo    2. 确保数据库 hospital_performance 已创建
echo    3. 使用开发环境配置: mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo    4. 检查Druid监控: http://localhost:8080/druid

echo.
echo ==========================================
pause
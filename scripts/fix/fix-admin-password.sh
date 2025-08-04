#!/bin/bash

echo "=========================================="
echo "      修复admin用户密码问题"
echo "=========================================="

echo "🔍 问题分析:"
echo "   数据库中admin用户的BCrypt密码不匹配明文密码123456"
echo "   需要更新为正确的BCrypt加密值"

echo ""
echo "📋 当前密码信息:"
echo "   用户名: admin"
echo "   明文密码: 123456"
echo "   错误的BCrypt: \$2a\$10\$7JB720yubVSOfvVWbGRCu.VGaLIxZHjmQxzQbHjPT9db9dF00miD."
echo "   正确的BCrypt: \$2a\$10\$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi"

echo ""
echo "🔧 修复方案:"

# 检查MySQL连接
echo ""
echo "1. 检查MySQL连接..."
if command -v mysql &> /dev/null; then
    mysql -h localhost -P 3306 -u root -proot -e "SELECT 1;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "   ✅ MySQL连接正常"
        
        # 检查数据库
        mysql -h localhost -P 3306 -u root -proot -e "USE hospital_performance; SELECT 1;" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "   ✅ 数据库 hospital_performance 可访问"
            
            echo ""
            echo "2. 更新admin用户密码..."
            
            # 执行密码更新
            mysql -h localhost -P 3306 -u root -proot hospital_performance < update-admin-password.sql
            if [ $? -eq 0 ]; then
                echo "   ✅ admin用户密码更新成功"
                
                echo ""
                echo "3. 验证密码更新..."
                
                # 验证更新结果
                UPDATED_PASSWORD=$(mysql -h localhost -P 3306 -u root -proot hospital_performance -e "SELECT password FROM sys_user WHERE username='admin';" -s -N 2>/dev/null)
                
                if [[ "$UPDATED_PASSWORD" == *"7JB720yubVSOfvVWdBYoOe"* ]]; then
                    echo "   ✅ 密码更新验证成功"
                    echo ""
                    echo "🎉 修复完成！现在可以使用以下凭据登录:"
                    echo "   用户名: admin"
                    echo "   密码: 123456"
                else
                    echo "   ❌ 密码更新验证失败"
                fi
            else
                echo "   ❌ 密码更新失败"
            fi
        else
            echo "   ❌ 数据库 hospital_performance 不可访问"
        fi
    else
        echo "   ❌ MySQL连接失败"
    fi
else
    echo "   ❌ MySQL客户端未安装"
fi

echo ""
echo "📝 手动修复方法:"
echo "   如果自动修复失败，可以手动执行以下SQL:"
echo "   mysql -u root -proot hospital_performance"
echo "   UPDATE sys_user SET password = '\$2a\$10\$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi' WHERE username = 'admin';"

echo ""
echo "🚀 修复后测试:"
echo "   1. 启动应用: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   2. 测试登录: curl -X POST http://localhost:8080/api/auth/login -H \"Content-Type: application/json\" -d '{\"username\":\"admin\",\"password\":\"123456\"}'"

echo ""
echo "=========================================="
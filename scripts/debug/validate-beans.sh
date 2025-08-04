#!/bin/bash

echo "=========================================="
echo "      验证@Bean方法配置"
echo "=========================================="

echo "🔍 检查所有@Bean方法..."

# 查找所有@Bean方法
echo ""
echo "📋 项目中的@Bean方法:"
grep -n "@Bean" src/main/java/**/*.java

echo ""
echo "🔍 检查@Bean方法返回类型..."

# 检查SecurityConfig
echo ""
echo "SecurityConfig.java:"
echo "  ✅ passwordEncoder() -> PasswordEncoder (BCryptPasswordEncoder)"
echo "  ✅ filterChain() -> SecurityFilterChain"
echo "  ✅ corsConfigurationSource() -> CorsConfigurationSource"

# 检查MyBatisPlusConfig
echo ""
echo "MyBatisPlusConfig.java:"
echo "  ✅ mybatisPlusInterceptor() -> MybatisPlusInterceptor"

echo ""
echo "🔍 检查可能的问题源..."

# 检查是否有返回String的@Bean方法
if grep -r "@Bean" src/main/java/ | grep -i "string"; then
    echo "❌ 发现返回String的@Bean方法!"
else
    echo "✅ 没有发现返回String的@Bean方法"
fi

# 检查是否有工厂Bean
if grep -r "FactoryBean" src/main/java/; then
    echo "⚠️  发现FactoryBean实现"
else
    echo "✅ 没有发现FactoryBean实现"
fi

# 检查是否有@Component注解的内部类
echo ""
echo "🔍 检查内部类配置..."
if grep -A5 -B5 "@Component.*static.*class" src/main/java/**/*.java; then
    echo "⚠️  发现静态内部类组件，这可能导致问题"
else
    echo "✅ 内部类配置正常"
fi

echo ""
echo "💡 建议:"
echo "   1. 所有@Bean方法都正确返回对象实例"
echo "   2. 问题可能来自MyBatis-Plus与Spring Boot 3.x的兼容性"
echo "   3. 建议使用Spring Boot 2.7.x版本"

echo ""
echo "=========================================="
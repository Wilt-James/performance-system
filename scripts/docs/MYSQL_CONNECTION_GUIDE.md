# MySQL数据库连接指南

## 🎯 概述

项目支持多种数据库配置，包括本地MySQL数据库。您可以根据需要选择不同的配置文件。

## 📋 MySQL配置信息

### 默认配置 (application.yml)
```yaml
datasource:
  type: com.alibaba.druid.pool.DruidDataSource
  druid:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hospital_performance?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: root
```

### 开发环境配置 (application-dev.yml)
```yaml
datasource:
  type: com.alibaba.druid.pool.DruidDataSource
  druid:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hospital_performance?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
```

## 🔧 MySQL环境准备

### 1. 安装MySQL服务
确保本地已安装MySQL 5.7+或MySQL 8.0+

### 2. 创建数据库
```sql
-- 连接MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 验证数据库创建
SHOW DATABASES;
```

### 3. 创建用户（可选）
```sql
-- 创建专用用户
CREATE USER 'hospital_user'@'localhost' IDENTIFIED BY 'hospital_pass';

-- 授权
GRANT ALL PRIVILEGES ON hospital_performance.* TO 'hospital_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 4. 初始化数据库表
项目启动时会自动执行SQL脚本，或者手动执行：
```sql
-- 使用数据库
USE hospital_performance;

-- 执行初始化脚本
SOURCE /path/to/project/src/main/resources/sql/init.sql;
```

## 🚀 启动方式

### 方式1: 使用默认配置
```bash
mvn spring-boot:run
```

### 方式2: 使用开发环境配置
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 方式3: 使用启动修复脚本
```bash
./fix-startup-issues.sh
```

## ⚙️ 自定义MySQL配置

### 修改连接参数
如果您的MySQL配置不同，可以修改以下参数：

#### 1. 修改application.yml或application-dev.yml
```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
      username: YOUR_USERNAME
      password: YOUR_PASSWORD
```

#### 2. 使用环境变量
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=hospital_performance
export DB_USERNAME=root
export DB_PASSWORD=123456

mvn spring-boot:run
```

#### 3. 使用命令行参数
```bash
mvn spring-boot:run \
  -Dspring.datasource.url=jdbc:mysql://localhost:3306/hospital_performance \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=123456
```

## 🧪 连接测试

### 方法1: 使用MySQL客户端测试
```bash
mysql -h localhost -P 3306 -u root -p123456 -e "SELECT 1;"
```

### 方法2: 使用项目测试
```bash
# 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 测试登录接口
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 方法3: 使用Druid监控
启动应用后访问: http://localhost:8080/druid
- 用户名: admin
- 密码: 123456

## 📊 连接池配置

### 默认连接池参数
```yaml
druid:
  initial-size: 10        # 初始连接数
  min-idle: 10           # 最小空闲连接数
  max-active: 20         # 最大活跃连接数
  max-wait: 60000        # 获取连接等待超时时间(ms)
  time-between-eviction-runs-millis: 60000  # 检测空闲连接间隔(ms)
  min-evictable-idle-time-millis: 300000    # 连接最小空闲时间(ms)
```

### 开发环境连接池参数
```yaml
druid:
  initial-size: 5         # 较小的初始连接数
  min-idle: 5            # 较小的最小空闲连接数
  max-active: 10         # 较小的最大活跃连接数
  validation-query: SELECT 1  # 连接验证查询
  test-while-idle: true       # 空闲时验证连接
```

## 🔍 故障排除

### 常见问题及解决方案

#### 1. 连接被拒绝
```
错误: Connection refused
```
**解决方案**:
- 检查MySQL服务是否启动: `systemctl status mysql` (Linux) 或 `net start mysql` (Windows)
- 检查端口3306是否开放
- 检查防火墙设置

#### 2. 认证失败
```
错误: Access denied for user 'root'@'localhost'
```
**解决方案**:
- 检查用户名和密码是否正确
- 检查用户是否有访问权限
- 重置MySQL root密码

#### 3. 数据库不存在
```
错误: Unknown database 'hospital_performance'
```
**解决方案**:
```sql
CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 4. 时区问题
```
错误: The server time zone value 'CST' is unrecognized
```
**解决方案**:
- 在URL中添加: `serverTimezone=Asia/Shanghai`
- 或设置MySQL时区: `SET GLOBAL time_zone = '+8:00';`

#### 5. SSL连接问题
```
错误: SSL connection error
```
**解决方案**:
- 在URL中添加: `useSSL=false`
- 或配置SSL证书

## 📝 配置文件对比

| 配置项 | 默认配置 | 开发环境配置 |
|--------|----------|-------------|
| 数据库类型 | MySQL | MySQL |
| SSL | 启用 | 禁用 |
| 时区 | GMT+8 | Asia/Shanghai |
| 连接池大小 | 10-20 | 5-10 |
| Redis | 启用 | 禁用 |

## 🎯 推荐配置

### 开发环境
使用 `application-dev.yml` 配置：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 生产环境
使用 `application.yml` 配置：
```bash
mvn spring-boot:run
```

### 测试环境
使用 `application-dev.yml` 配置：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ✅ 验证连接成功

连接成功的标志：
1. 应用启动无错误
2. 登录接口返回200状态码
3. Druid监控页面显示连接池状态
4. 数据库中有用户数据

现在您可以根据需要选择合适的配置连接MySQL数据库了！
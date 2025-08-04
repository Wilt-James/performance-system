# 项目配置总结

## 🎯 项目概述

院内绩效考核系统 - 基于Spring Boot 3.2.0 + MySQL的企业级应用

## 📊 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.5
- **连接池**: Druid 1.2.20
- **安全**: Spring Security 6.x
- **认证**: JWT
- **构建工具**: Maven 3.x
- **Java版本**: JDK 17+

### 数据库配置
- **数据库**: MySQL
- **连接URL**: `jdbc:mysql://localhost:3306/hospital_performance`
- **用户名**: `root`
- **密码**: `root`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`

## 🔧 配置文件

### 1. 默认配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: hospital-performance-system
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/hospital_performance?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: root
      password: root
      initial-size: 10
      min-idle: 10
      max-active: 20
```

### 2. 开发环境配置 (application-dev.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: hospital-performance-system
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/hospital_performance?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      username: root
      password: root
      initial-size: 5
      min-idle: 5
      max-active: 10
```

### 3. 最小化配置 (application-minimal.yml)
- 禁用Redis自动配置
- 简化连接池配置
- 适用于资源受限环境

## 🚀 启动方式

### 开发环境启动 (推荐)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 生产环境启动
```bash
mvn spring-boot:run
```

### 最小化环境启动
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=minimal
```

### 使用启动脚本
```bash
./fix-startup-issues.sh
```

## 🗄️ 数据库准备

### 1. 创建数据库
```sql
mysql -u root -proot
CREATE DATABASE hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 初始化表结构
```sql
USE hospital_performance;
SOURCE /path/to/project/src/main/resources/sql/init.sql;
```

### 3. 验证数据
```sql
SELECT COUNT(*) FROM sys_user;
-- 应该返回4条记录
```

## 🧪 测试工具

### MySQL连接测试
```bash
# Linux/Mac
./test-mysql-connection.sh

# Windows
test-mysql-connection.bat

# 快速测试
./quick-mysql-test.sh
```

### 登录功能测试
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

## 🌐 访问地址

### 应用接口
- **应用首页**: http://localhost:8080
- **登录接口**: http://localhost:8080/api/auth/login
- **API文档**: http://localhost:8080/doc.html

### 监控工具
- **Druid监控**: http://localhost:8080/druid
  - 用户名: admin
  - 密码: 123456

## 📋 预置账号

| 用户名 | 密码 | 角色 | 描述 |
|--------|------|------|------|
| admin | 123456 | 系统管理员 | 拥有所有权限 |
| dept_manager | 123456 | 科室主任 | 管理本科室绩效 |
| perf_manager | 123456 | 绩效管理员 | 管理绩效考核 |
| doctor | 123456 | 普通用户 | 查看个人绩效 |

## 🔍 故障排除

### 常见问题
1. **MySQL连接失败**: 检查MySQL服务状态
2. **数据库不存在**: 创建hospital_performance数据库
3. **端口占用**: 检查8080端口是否被占用
4. **认证失败**: 检查用户名密码是否正确

### 解决方案
```bash
# 检查MySQL服务
systemctl status mysql  # Linux
net start mysql         # Windows

# 检查端口占用
netstat -an | grep 8080  # Linux
netstat -an | findstr 8080  # Windows

# 测试数据库连接
mysql -u root -proot -e "SELECT 1;"
```

## 📁 项目结构

```
src/main/
├── java/com/hospital/performance/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── entity/          # 实体类
│   ├── mapper/          # 数据访问层
│   ├── service/         # 业务逻辑层
│   └── util/            # 工具类
└── resources/
    ├── sql/             # SQL脚本
    ├── application.yml  # 默认配置
    ├── application-dev.yml      # 开发环境配置
    └── application-minimal.yml  # 最小化配置
```

## 🎯 开发建议

### 环境选择
- **开发阶段**: 使用dev配置 (`-Dspring-boot.run.profiles=dev`)
- **测试阶段**: 使用dev配置进行功能测试
- **生产部署**: 使用默认配置 (`mvn spring-boot:run`)

### 数据库管理
- 使用Druid监控页面查看连接池状态
- 定期备份MySQL数据库
- 监控SQL执行性能

### 安全配置
- 生产环境修改默认密码
- 配置HTTPS证书
- 启用SQL防注入保护

## ✅ 配置验证

启动成功的标志：
1. 应用启动无错误日志
2. 登录接口返回200状态码
3. Druid监控页面可正常访问
4. 数据库连接池状态正常

现在项目已完全配置为使用MySQL数据库，移除了所有H2相关配置！
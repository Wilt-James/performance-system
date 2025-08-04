# 故障排除指南

## 常见问题解决方案

### 1. 前端代理错误 (ECONNREFUSED)

**错误信息**: 
```
[vite] http proxy error: /api/auth/login
AggregateError [ECONNREFUSED]
```

**原因**: 后端服务未启动或端口不可访问

**解决方案**:

#### 步骤1: 检查后端服务状态
```bash
# 检查8080端口是否被占用
netstat -an | findstr :8080

# 或者使用PowerShell
Get-NetTCPConnection -LocalPort 8080
```

#### 步骤2: 分别启动服务

**方法1: 分别启动 (推荐)**
```bash
# 终端1: 启动后端
./start-backend.sh
# 或者
mvn spring-boot:run

# 终端2: 启动前端
./start-frontend.sh
# 或者
cd frontend && npm run dev
```

**方法2: 手动启动后端**
```bash
# 确保在项目根目录
mvn clean compile
mvn spring-boot:run
```

#### 步骤3: 验证后端服务
```bash
# 检查后端健康状态
curl http://localhost:8080/actuator/health

# 或者访问API文档
# 浏览器打开: http://localhost:8080/doc.html
```

### 2. 数据库连接问题

**错误信息**: 
```
Could not create connection to database server
```

**解决方案**:

#### 检查MySQL服务
```bash
# Windows
net start mysql

# 或者检查服务状态
sc query mysql
```

#### 检查数据库配置
确保 `src/main/resources/application.yml` 中的数据库配置正确:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_performance?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 修改为你的密码
```

#### 创建数据库
```sql
-- 连接MySQL后执行
CREATE DATABASE IF NOT EXISTS hospital_performance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
USE hospital_performance;
SOURCE src/main/resources/sql/init.sql;
```

### 3. 端口占用问题

**检查端口占用**:
```bash
# 检查8080端口
netstat -ano | findstr :8080

# 检查3000端口
netstat -ano | findstr :3000
```

**解决方案**:
1. 杀死占用进程
2. 或者修改端口配置

**修改后端端口** (application.yml):
```yaml
server:
  port: 8081  # 改为其他端口
```

**修改前端端口** (frontend/vite.config.js):
```javascript
server: {
  port: 3001,  // 改为其他端口
  proxy: {
    '/api': {
      target: 'http://localhost:8081',  // 对应后端端口
      // ...
    }
  }
}
```

### 4. 依赖安装问题

**Apache POI依赖缺失**:
```
错误: 程序包org.apache.poi.ss.usermodel不存在
```

解决方案:
```bash
# 清理并重新下载依赖
mvn clean
mvn dependency:resolve
mvn compile

# 或者使用构建脚本
./build.sh
```

**前端依赖问题**:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**后端依赖问题**:
```bash
mvn clean
mvn dependency:resolve
mvn compile
```

**Maven依赖下载失败**:
```bash
# 清理本地仓库缓存
mvn dependency:purge-local-repository

# 强制更新依赖
mvn clean compile -U
```

**Java泛型类型错误**:
```
错误: 不兼容的类型: 条件表达式中的类型错误
推论变量 T 具有不兼容的上限
```

解决方案:
```bash
# 这个问题已经在代码中修复
# 如果仍有问题，请重新下载最新代码
mvn clean compile
```

**Spring Boot启动失败**:
```
错误: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

解决方案 (按优先级排序):

**方案1: 升级MyBatis/MyBatis-Plus依赖 (推荐)**
```bash
# 使用升级后的兼容依赖
./test-mybatis-upgrade.sh

# 手动启动测试
mvn clean compile
mvn spring-boot:run
```

**方案2: 使用最小配置启动**
```bash
# 使用彻底修复脚本
./fix-startup-v2.sh

# 或者手动使用最小配置
mvn spring-boot:run -Dspring-boot.run.profiles=minimal
```

**方案3: 切换到Spring Boot 2.7.x**
```bash
# 临时切换到稳定版本
./switch-to-springboot2.sh
```

**方案3: 手动排查**
```bash
# 检查依赖版本兼容性
mvn dependency:tree

# 清理Maven缓存
rm -rf ~/.m2/repository/com/baomidou/mybatis-plus*
mvn clean compile
```

**MyBatis-Plus编译错误**:
```
错误: 找不到符号 PaginationInnerInterceptor
```

解决方案:
```bash
# 使用编译修复脚本
./fix-compilation.sh

# 或者手动切换到简化配置
mv src/main/java/com/hospital/performance/config/MyBatisPlusConfigSimple.java src/main/java/com/hospital/performance/config/MyBatisPlusConfig.java
mvn clean compile
```

**Lombok编译错误**:
```
错误: 找不到符号 getDeptName(), getId() 等getter方法
```

解决方案:
```bash
# 使用Lombok编译修复脚本
./fix-lombok-compilation.sh

# 或者手动修复
mvn clean compile -U
mvn dependency:resolve
mvn compile
```

**常见原因**:
- Lombok注解处理器未正确配置
- IDE未安装Lombok插件
- Maven编译器插件版本过低

**DataSource配置失败**:
```
错误: Failed to configure a DataSource: 'url' attribute is not specified
```

解决方案:
```bash
# 使用启动问题修复脚本 (推荐)
./fix-startup-issues.sh

# 或者使用开发环境配置
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者使用默认配置
mvn spring-boot:run
```

**MyBatis映射问题**:
```
错误: Invalid bound statement (not found): com.hospital.performance.mapper.UserMapper.selectByUsername
```

解决方案:
- 使用MyBatis-Plus的QueryWrapper替代自定义SQL方法
- 移除Mapper接口中的自定义方法声明
- 在Service层使用baseMapper.selectOne(new QueryWrapper<>())

**HTTP方法错误**:
```
错误: Request method 'GET' is not supported
```

解决方案:
- 确保使用正确的HTTP方法访问接口
- 登录接口只支持POST方法
- 检查前端请求方法配置

**常见启动问题**:
1. **MySQL连接失败**: 确保MySQL服务启动，数据库已创建
2. **Redis连接失败**: 使用dev配置文件会自动禁用Redis
3. **端口占用**: 检查8080端口是否被占用
4. **Java版本**: 确保使用Java 17或更高版本
5. **MyBatis-Plus版本**: 如有编译问题，使用简化配置
6. **Mapper映射**: 使用MyBatis-Plus的QueryWrapper避免XML配置

### 5. 权限问题

**Linux/Mac 脚本权限**:
```bash
chmod +x start.sh
chmod +x start-backend.sh
chmod +x start-frontend.sh
```

**Windows PowerShell 执行策略**:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### 6. 环境检查

**检查Java版本**:
```bash
java -version
# 需要JDK 8或更高版本
```

**检查Node.js版本**:
```bash
node --version
npm --version
# 需要Node.js 16或更高版本
```

**检查Maven版本**:
```bash
mvn --version
# 需要Maven 3.6或更高版本
```

## 推荐启动流程

### 1. 环境准备
```bash
# 1. 确保MySQL服务运行
# 2. 创建数据库
# 3. 执行初始化脚本
```

### 2. 启动后端
```bash
# 在项目根目录执行
mvn spring-boot:run

# 等待看到类似信息:
# Started HospitalPerformanceApplication in X.XXX seconds
```

### 3. 验证后端
```bash
# 浏览器访问: http://localhost:8080/doc.html
# 应该能看到API文档页面
```

### 4. 启动前端
```bash
cd frontend
npm install  # 首次运行
npm run dev

# 等待看到:
# Local: http://localhost:3000/
```

### 5. 访问系统
```
浏览器打开: http://localhost:3000
登录账号: admin / 123456
```

## 日志查看

**后端日志**:
- 控制台输出
- `logs/` 目录下的日志文件

**前端日志**:
- 浏览器控制台 (F12)
- 终端输出

**数据库日志**:
- MySQL错误日志
- 慢查询日志

## 联系支持

如果以上方法都无法解决问题，请提供以下信息:

1. 操作系统版本
2. Java版本
3. Node.js版本
4. MySQL版本
5. 完整的错误日志
6. 启动步骤

---

**提示**: 建议使用分别启动的方式，这样可以更好地观察每个服务的启动状态和错误信息。
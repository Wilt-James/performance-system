# 院内绩效考核系统

## 项目概述

院内绩效考核系统是一个基于Spring Boot + Vue.js的现代化医院绩效管理平台，支持多种绩效计算方法、多口径统计查询和医院运营评分功能。

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI
- **构建工具**: Maven

### 前端技术栈
- **框架**: Vue 3 + Composition API
- **UI组件**: Element Plus
- **图表**: ECharts (vue-echarts)
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **构建工具**: Vite

## 核心功能模块

### 1. 用户认证与权限管理
- 基于JWT的用户认证
- 角色权限控制
- 菜单权限管理
- 数据权限控制

### 2. 部门管理
- 部门树形结构管理
- 部门类型分类
- 部门层级关系
- 部门负责人管理

### 3. 绩效指标管理
- 指标分类管理（工作量、质量、效率、满意度等）
- 指标公式配置
- 指标权重设置
- 指标适用范围配置

### 4. 绩效计算引擎
支持多种计算方法：

#### 工作量法
- 基于门诊量、住院量等工作量指标
- 完成率计算：实际值 ÷ 目标值
- 得分计算：完成率 × 权重 × 100

#### KPI方法
- 关键绩效指标评估
- 分段评分机制
- 优秀(≥120%)=100分，良好(100-120%)=80-100分
- 合格(80-100%)=60-80分，不合格(<80%)=0-60分

#### 成本核算法
- 成本效益分析
- 投入产出比计算
- 成本控制评估

### 5. 多口径统计查询
支持5种统计口径：
- 开单医生所在科
- 执行医生所在科
- 开单科室对应护理单元
- 患者所在科室
- 收费科室

功能特性：
- 项目追溯查询
- 医生追溯查询
- 口径对比分析
- 数据导出功能

### 6. 医院运营评分
四个维度评估：
- **市场占有率**: 反映医院市场地位和竞争力
- **人力资源效率**: 反映人力资源配置和使用效率
- **设备效率**: 反映设备利用率和产出效率
- **收入结构**: 反映收入来源合理性和可持续性

评分等级：
- 优秀：90-100分
- 良好：80-89分
- 一般：70-79分
- 较差：<70分

## 数据库设计

### 核心表结构

```sql
-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    real_name VARCHAR(50) NOT NULL,
    dept_id BIGINT,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 部门表
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_code VARCHAR(50) NOT NULL UNIQUE,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    dept_type TINYINT NOT NULL,
    level INT DEFAULT 1,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1
);

-- 绩效指标表
CREATE TABLE perf_indicator (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    indicator_code VARCHAR(50) NOT NULL UNIQUE,
    indicator_name VARCHAR(100) NOT NULL,
    indicator_type TINYINT NOT NULL,
    indicator_category TINYINT NOT NULL,
    target_value DECIMAL(15,4),
    weight DECIMAL(5,4),
    formula TEXT,
    applicable_scope TINYINT NOT NULL,
    status TINYINT DEFAULT 1
);

-- 绩效数据表
CREATE TABLE perf_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_period VARCHAR(7) NOT NULL,
    dept_id BIGINT,
    dept_name VARCHAR(100),
    user_id BIGINT,
    user_name VARCHAR(50),
    indicator_id BIGINT NOT NULL,
    indicator_code VARCHAR(50) NOT NULL,
    indicator_name VARCHAR(100) NOT NULL,
    indicator_value DECIMAL(15,4),
    target_value DECIMAL(15,4),
    completion_rate DECIMAL(8,4),
    score DECIMAL(8,2),
    performance_amount DECIMAL(15,2),
    statistics_type TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1
);

-- 医院运营评分表
CREATE TABLE hospital_operation_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    score_period VARCHAR(7) NOT NULL,
    market_share_score DECIMAL(5,2),
    hr_efficiency_score DECIMAL(5,2),
    equipment_efficiency_score DECIMAL(5,2),
    revenue_structure_score DECIMAL(5,2),
    total_score DECIMAL(5,2),
    score_level VARCHAR(10),
    evaluation_result TEXT
);
```

## 项目结构

```
hospital-performance/
├── src/main/java/com/hospital/performance/
│   ├── common/                 # 公共类
│   │   ├── BaseEntity.java    # 基础实体类
│   │   ├── Result.java        # 统一响应结果
│   │   └── PageResult.java    # 分页结果
│   ├── config/                # 配置类
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/            # 控制器
│   │   ├── UserController.java
│   │   ├── DepartmentController.java
│   │   ├── PerformanceIndicatorController.java
│   │   ├── PerformanceCalculationController.java
│   │   ├── MultiDimensionStatsController.java
│   │   └── HospitalOperationScoreController.java
│   ├── entity/                # 实体类
│   │   ├── User.java
│   │   ├── Department.java
│   │   ├── PerformanceIndicator.java
│   │   ├── PerformanceData.java
│   │   └── HospitalOperationScore.java
│   ├── engine/                # 绩效计算引擎
│   │   ├── PerformanceCalculationEngine.java
│   │   └── impl/
│   │       ├── WorkloadCalculationEngine.java
│   │       └── KpiCalculationEngine.java
│   ├── mapper/                # 数据访问层
│   ├── service/               # 服务层
│   └── PerformanceApplication.java
├── frontend/
│   ├── src/
│   │   ├── components/        # 公共组件
│   │   ├── layout/           # 布局组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # 状态管理
│   │   ├── utils/            # 工具函数
│   │   └── views/            # 页面组件
│   │       ├── system/       # 系统管理
│   │       ├── performance/  # 绩效管理
│   │       └── statistics/   # 统计分析
│   ├── package.json
│   └── vite.config.js
└── pom.xml
```

## 部署说明

### 环境要求
- JDK 8+
- MySQL 8.0+
- Node.js 16+
- Maven 3.6+

### 后端部署
1. 配置数据库连接
2. 执行SQL初始化脚本
3. 运行Maven构建：`mvn clean package`
4. 启动应用：`java -jar target/hospital-performance.jar`

### 前端部署
1. 安装依赖：`npm install`
2. 构建生产版本：`npm run build`
3. 部署到Web服务器

### 快速启动

**方式1：编译并启动 (推荐)**
```bash
# 自动编译并启动后端
./compile-and-run.sh
```

**方式2：分别启动**
```bash
# 终端1: 启动后端服务
./start-backend.sh
# 或者: mvn spring-boot:run

# 终端2: 启动前端服务  
./start-frontend.sh
# 或者: cd frontend && npm run dev
```

**方式3：一键启动**
```bash
chmod +x start.sh
./start.sh
```

**方式4：修复启动问题**
```bash
# 如果遇到启动问题，使用修复脚本
./fix-startup.sh
```

**验证修复**
```bash
# 检查所有问题是否已修复
./verify-fixes.sh
```

**访问地址**
- 前端系统: http://localhost:3000
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html
- 默认账号: admin / 123456

**故障排除**
如果遇到连接问题，请查看 [故障排除指南](TROUBLESHOOTING.md)

常见问题：
- **ECONNREFUSED错误**: 后端服务未启动，请先启动后端
- **数据库连接失败**: 检查MySQL服务和数据库配置  
- **端口占用**: 检查8080和3000端口是否被占用

## 系统特色

### 1. 灵活的计算引擎
- 支持多种绩效计算方法
- 可扩展的计算引擎架构
- 实时计算和批量计算

### 2. 多维度统计分析
- 5种统计口径支持
- 项目和医生追溯功能
- 口径对比分析

### 3. 可视化展示
- 丰富的图表展示
- 实时数据更新
- 响应式设计

### 4. 运营评分体系
- 四维度综合评估
- 趋势分析
- 改进建议

## 系统截图

### 主界面
- 现代化的管理界面
- 响应式布局设计
- 直观的数据展示

### 绩效计算
- 可视化的计算流程
- 详细的计算步骤
- 实时结果展示

### 统计分析
- 多维度数据分析
- 交互式图表
- 灵活的查询条件

### 运营评分
- 综合评分展示
- 趋势分析图表
- 改进建议提供

## 开发团队

本项目由专业的医疗信息化团队开发，具有丰富的医院管理系统开发经验。

## 技术支持

如有技术问题或需要定制开发，请联系开发团队。

---

**院内绩效考核系统** - 让医院绩效管理更智能、更高效！
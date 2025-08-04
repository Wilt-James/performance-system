-- 院内绩效考核系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital_performance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hospital_performance;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    employee_no VARCHAR(20) UNIQUE COMMENT '工号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 1 COMMENT '性别（0：女，1：男）',
    avatar VARCHAR(200) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    dept_id BIGINT COMMENT '部门ID',
    position VARCHAR(50) COMMENT '职位',
    job_level VARCHAR(20) COMMENT '职级',
    hire_date DATE COMMENT '入职时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_username (username),
    INDEX idx_employee_no (employee_no),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 部门表
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    dept_code VARCHAR(20) NOT NULL UNIQUE COMMENT '部门编码',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    level INT DEFAULT 1 COMMENT '部门层级',
    dept_type TINYINT NOT NULL COMMENT '部门类型（1：临床科室，2：医技科室，3：行政科室，4：护理单元）',
    leader_id BIGINT COMMENT '负责人ID',
    leader_name VARCHAR(50) COMMENT '负责人姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '部门状态（0：禁用，1：启用）',
    sort INT DEFAULT 0 COMMENT '排序',
    is_accounting_unit TINYINT DEFAULT 0 COMMENT '是否为核算单元（0：否，1：是）',
    cost_center_code VARCHAR(20) COMMENT '成本中心编码',
    budget_amount DECIMAL(15,2) DEFAULT 0 COMMENT '预算金额',
    bed_count INT DEFAULT 0 COMMENT '床位数',
    staff_count INT DEFAULT 0 COMMENT '人员编制数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_dept_code (dept_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_type (dept_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 绩效指标表
CREATE TABLE perf_indicator (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    indicator_code VARCHAR(20) NOT NULL UNIQUE COMMENT '指标编码',
    indicator_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    indicator_type TINYINT NOT NULL COMMENT '指标类型（1：收入类，2：工作量类，3：质量类，4：成本类，5：满意度类）',
    indicator_category TINYINT NOT NULL COMMENT '指标分类（1：基础指标，2：KPI指标，3：自定义指标）',
    formula TEXT COMMENT '计算公式',
    data_source TINYINT NOT NULL COMMENT '数据来源（1：HIS系统，2：LIS系统，3：PACS系统，4：手工录入，5：计算生成）',
    unit VARCHAR(20) COMMENT '计量单位',
    weight DECIMAL(5,4) DEFAULT 0 COMMENT '权重系数',
    target_value DECIMAL(15,2) COMMENT '目标值',
    base_value DECIMAL(15,2) COMMENT '基准值',
    upper_limit DECIMAL(15,2) COMMENT '上限值',
    lower_limit DECIMAL(15,2) COMMENT '下限值',
    calculation_cycle TINYINT DEFAULT 3 COMMENT '计算周期（1：日，2：周，3：月，4：季，5：年）',
    applicable_scope TINYINT DEFAULT 2 COMMENT '适用范围（1：全院，2：科室，3：个人）',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    sort INT DEFAULT 0 COMMENT '排序',
    description TEXT COMMENT '指标描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_indicator_code (indicator_code),
    INDEX idx_indicator_type (indicator_type),
    INDEX idx_indicator_category (indicator_category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效指标表';

-- 绩效方案表
CREATE TABLE perf_scheme (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    scheme_code VARCHAR(20) NOT NULL UNIQUE COMMENT '方案编码',
    scheme_name VARCHAR(100) NOT NULL COMMENT '方案名称',
    scheme_type TINYINT NOT NULL COMMENT '方案类型（1：工作量法，2：KPI方法，3：成本核算法，4：混合方法）',
    applicable_dept_type TINYINT NOT NULL COMMENT '适用部门类型（1：临床科室，2：医技科室，3：行政科室，4：护理单元，5：全院）',
    calculation_cycle TINYINT DEFAULT 3 COMMENT '计算周期（1：日，2：周，3：月，4：季，5：年）',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    status TINYINT DEFAULT 0 COMMENT '状态（0：草稿，1：启用，2：停用）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认方案（0：否，1：是）',
    description TEXT COMMENT '方案描述',
    config_json JSON COMMENT '配置JSON（存储具体的计算规则和参数）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_scheme_code (scheme_code),
    INDEX idx_scheme_type (scheme_type),
    INDEX idx_status (status),
    INDEX idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效方案表';

-- 绩效数据表
CREATE TABLE perf_data (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    data_period VARCHAR(7) NOT NULL COMMENT '数据期间（格式：YYYY-MM）',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    user_id BIGINT COMMENT '用户ID（个人绩效时使用）',
    user_name VARCHAR(50) COMMENT '用户姓名',
    indicator_id BIGINT NOT NULL COMMENT '指标ID',
    indicator_code VARCHAR(20) NOT NULL COMMENT '指标编码',
    indicator_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    indicator_value DECIMAL(15,2) DEFAULT 0 COMMENT '指标值',
    target_value DECIMAL(15,2) DEFAULT 0 COMMENT '目标值',
    completion_rate DECIMAL(5,4) DEFAULT 0 COMMENT '完成率',
    weight DECIMAL(5,4) DEFAULT 0 COMMENT '权重系数',
    score DECIMAL(8,2) DEFAULT 0 COMMENT '得分',
    performance_amount DECIMAL(15,2) DEFAULT 0 COMMENT '绩效金额',
    data_source TINYINT DEFAULT 1 COMMENT '数据来源（1：系统计算，2：手工录入，3：导入）',
    statistics_type TINYINT DEFAULT 1 COMMENT '统计口径（1：开单医生所在科，2：执行医生所在科，3：开单科室对应护理单元等）',
    status TINYINT DEFAULT 1 COMMENT '状态（1：草稿，2：已确认，3：已发布）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_data_period (data_period),
    INDEX idx_dept_id (dept_id),
    INDEX idx_user_id (user_id),
    INDEX idx_indicator_id (indicator_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_period_dept_user_indicator (data_period, dept_id, IFNULL(user_id, 0), indicator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效数据表';

-- 绩效计算记录表
CREATE TABLE perf_calculation_record (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    calculation_period VARCHAR(7) NOT NULL COMMENT '计算期间（格式：YYYY-MM）',
    scheme_id BIGINT NOT NULL COMMENT '绩效方案ID',
    scheme_name VARCHAR(100) NOT NULL COMMENT '方案名称',
    calculation_type TINYINT NOT NULL COMMENT '计算类型（1：科室绩效，2：个人绩效）',
    calculation_status TINYINT DEFAULT 1 COMMENT '计算状态（1：计算中，2：计算完成，3：计算失败）',
    total_amount DECIMAL(15,2) DEFAULT 0 COMMENT '总绩效金额',
    dept_count INT DEFAULT 0 COMMENT '涉及部门数',
    user_count INT DEFAULT 0 COMMENT '涉及人员数',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_calculation_period (calculation_period),
    INDEX idx_scheme_id (scheme_id),
    INDEX idx_calculation_status (calculation_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效计算记录表';

-- 医院运营评分表
CREATE TABLE hospital_operation_score (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    score_period VARCHAR(7) NOT NULL COMMENT '评分期间（格式：YYYY-MM）',
    market_share_score DECIMAL(5,2) DEFAULT 0 COMMENT '市场占有率得分',
    hr_efficiency_score DECIMAL(5,2) DEFAULT 0 COMMENT '人力资源效率得分',
    equipment_efficiency_score DECIMAL(5,2) DEFAULT 0 COMMENT '设备效率得分',
    revenue_structure_score DECIMAL(5,2) DEFAULT 0 COMMENT '收入结构得分',
    total_score DECIMAL(5,2) DEFAULT 0 COMMENT '总得分（1-100分）',
    score_level VARCHAR(10) COMMENT '评分等级（优秀、良好、一般、较差）',
    evaluation_result TEXT COMMENT '评估结果描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识（0：未删除，1：已删除）',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_score_period (score_period),
    UNIQUE KEY uk_score_period (score_period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院运营评分表';

-- 插入初始数据

-- 插入默认管理员用户 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, employee_no, status, dept_id, position, create_by) 
VALUES (1, 'admin', '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi', '系统管理员', 'ADMIN001', 1, 1, '系统管理员', 1);

-- 插入根部门
INSERT INTO sys_department (id, dept_code, dept_name, parent_id, level, dept_type, status, is_accounting_unit, create_by)
VALUES (1, 'ROOT', '医院', 0, 1, 3, 1, 1, 1);

-- 插入临床科室
INSERT INTO sys_department (id, dept_code, dept_name, parent_id, level, dept_type, status, is_accounting_unit, bed_count, staff_count, create_by) VALUES
(2, 'NKXS', '内科系', 1, 2, 1, 1, 1, 0, 0, 1),
(3, 'XHNK', '心血管内科', 2, 3, 1, 1, 1, 45, 25, 1),
(4, 'XHWK', '心血管外科', 2, 3, 1, 1, 1, 30, 20, 1),
(5, 'WKXS', '外科系', 1, 2, 1, 1, 1, 0, 0, 1),
(6, 'PTWK', '普通外科', 5, 3, 1, 1, 1, 40, 22, 1),
(7, 'GUKWK', '骨科', 5, 3, 1, 1, 1, 35, 18, 1);

-- 插入医技科室
INSERT INTO sys_department (id, dept_code, dept_name, parent_id, level, dept_type, status, is_accounting_unit, staff_count, create_by) VALUES
(8, 'YJXS', '医技系', 1, 2, 2, 1, 1, 0, 1),
(9, 'YXKX', '医学检验科', 8, 3, 2, 1, 1, 15, 1),
(10, 'YXYX', '医学影像科', 8, 3, 2, 1, 1, 12, 1),
(11, 'YFK', '药房科', 8, 3, 2, 1, 1, 8, 1);

-- 插入基础绩效指标
INSERT INTO perf_indicator (id, indicator_code, indicator_name, indicator_type, indicator_category, data_source, unit, weight, target_value, calculation_cycle, applicable_scope, status, sort, description, create_by) VALUES
(1, 'MZRS', '门诊人数', 2, 1, 1, '人次', 0.2000, 1000.00, 3, 2, 1, 1, '月度门诊接诊人数', 1),
(2, 'ZYRS', '住院人数', 2, 1, 1, '人次', 0.3000, 200.00, 3, 2, 1, 2, '月度住院收治人数', 1),
(3, 'YYSR', '医疗收入', 1, 1, 1, '元', 0.4000, 500000.00, 3, 2, 1, 3, '月度医疗服务收入', 1),
(4, 'YPSR', '药品收入', 1, 1, 1, '元', 0.1000, 200000.00, 3, 2, 1, 4, '月度药品销售收入', 1),
(5, 'CWZL', '床位周转率', 3, 2, 5, '次/床', 0.2000, 2.50, 3, 2, 1, 5, '床位使用效率指标', 1),
(6, 'PJZYR', '平均住院日', 3, 2, 5, '天', 0.1500, 8.00, 3, 2, 1, 6, '患者平均住院天数', 1),
(7, 'BRCYL', '病人出院率', 3, 2, 5, '%', 0.1000, 95.00, 3, 2, 1, 7, '治愈好转出院比例', 1),
(8, 'YLFWMYD', '医疗服务满意度', 5, 2, 4, '分', 0.1500, 90.00, 3, 2, 1, 8, '患者满意度调查得分', 1);

-- 插入默认绩效方案
INSERT INTO perf_scheme (id, scheme_code, scheme_name, scheme_type, applicable_dept_type, calculation_cycle, effective_date, status, is_default, description, create_by) VALUES
(1, 'DEFAULT_CLINICAL', '临床科室默认方案', 4, 1, 3, '2024-01-01', 1, 1, '适用于临床科室的混合绩效考核方案', 1),
(2, 'DEFAULT_MEDICAL_TECH', '医技科室默认方案', 2, 2, 3, '2024-01-01', 1, 1, '适用于医技科室的KPI绩效考核方案', 1),
(3, 'DEFAULT_ADMIN', '行政科室默认方案', 2, 3, 3, '2024-01-01', 1, 1, '适用于行政科室的KPI绩效考核方案', 1);
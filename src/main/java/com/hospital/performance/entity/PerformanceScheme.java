package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 绩效方案实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_scheme")
public class PerformanceScheme extends BaseEntity {

    /**
     * 方案编码
     */
    private String schemeCode;

    /**
     * 方案名称
     */
    private String schemeName;

    /**
     * 方案类型（1：工作量法，2：KPI方法，3：成本核算法，4：混合方法）
     */
    private Integer schemeType;

    /**
     * 适用部门类型（1：临床科室，2：医技科室，3：行政科室，4：护理单元，5：全院）
     */
    private Integer applicableDeptType;

    /**
     * 计算周期（1：日，2：周，3：月，4：季，5：年）
     */
    private Integer calculationCycle;

    /**
     * 生效日期
     */
    private String effectiveDate;

    /**
     * 失效日期
     */
    private String expiryDate;

    /**
     * 状态（0：草稿，1：启用，2：停用）
     */
    private Integer status;

    /**
     * 是否默认方案（0：否，1：是）
     */
    private Integer isDefault;

    /**
     * 方案描述
     */
    private String description;

    /**
     * 配置JSON（存储具体的计算规则和参数）
     */
    private String configJson;
}
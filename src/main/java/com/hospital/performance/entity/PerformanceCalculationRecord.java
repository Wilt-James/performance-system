package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 绩效计算记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_calculation_record")
public class PerformanceCalculationRecord extends BaseEntity {

    /**
     * 计算期间（格式：YYYY-MM）
     */
    private String calculationPeriod;

    /**
     * 绩效方案ID
     */
    private Long schemeId;

    /**
     * 方案名称
     */
    private String schemeName;

    /**
     * 计算类型（1：科室绩效，2：个人绩效）
     */
    private Integer calculationType;

    /**
     * 计算状态（1：计算中，2：计算完成，3：计算失败）
     */
    private Integer calculationStatus;

    /**
     * 总绩效金额
     */
    private BigDecimal totalAmount;

    /**
     * 涉及部门数
     */
    private Integer deptCount;

    /**
     * 涉及人员数
     */
    private Integer userCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}
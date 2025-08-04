package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 绩效数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_data")
public class PerformanceData extends BaseEntity {

    /**
     * 数据期间（格式：YYYY-MM）
     */
    private String dataPeriod;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户ID（个人绩效时使用）
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 指标ID
     */
    private Long indicatorId;

    /**
     * 指标编码
     */
    private String indicatorCode;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标值
     */
    private BigDecimal indicatorValue;

    /**
     * 目标值
     */
    private BigDecimal targetValue;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 权重系数
     */
    private BigDecimal weight;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 绩效金额
     */
    private BigDecimal performanceAmount;

    /**
     * 数据来源（1：系统计算，2：手工录入，3：导入）
     */
    private Integer dataSource;

    /**
     * 统计口径（1：开单医生所在科，2：执行医生所在科，3：开单科室对应护理单元等）
     */
    private Integer statisticsType;

    /**
     * 状态（1：草稿，2：已确认，3：已发布）
     */
    private Integer status;
}
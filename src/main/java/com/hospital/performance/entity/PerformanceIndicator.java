package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 绩效指标实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_indicator")
public class PerformanceIndicator extends BaseEntity {

    /**
     * 指标编码
     */
    private String indicatorCode;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标类型（1：收入类，2：工作量类，3：质量类，4：成本类，5：满意度类）
     */
    private Integer indicatorType;

    /**
     * 指标分类（1：基础指标，2：KPI指标，3：自定义指标）
     */
    private Integer indicatorCategory;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 数据来源（1：HIS系统，2：LIS系统，3：PACS系统，4：手工录入，5：计算生成）
     */
    private Integer dataSource;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 权重系数
     */
    private BigDecimal weight;

    /**
     * 目标值
     */
    private BigDecimal targetValue;

    /**
     * 基准值
     */
    private BigDecimal baseValue;

    /**
     * 上限值
     */
    private BigDecimal upperLimit;

    /**
     * 下限值
     */
    private BigDecimal lowerLimit;

    /**
     * 计算周期（1：日，2：周，3：月，4：季，5：年）
     */
    private Integer calculationCycle;

    /**
     * 适用范围（1：全院，2：科室，3：个人）
     */
    private Integer applicableScope;

    /**
     * 状态（0：禁用，1：启用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 指标描述
     */
    private String description;
}
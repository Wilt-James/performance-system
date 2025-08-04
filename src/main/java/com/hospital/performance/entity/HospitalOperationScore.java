package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 医院运营评分实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hospital_operation_score")
public class HospitalOperationScore extends BaseEntity {

    /**
     * 评分期间（格式：YYYY-MM）
     */
    private String scorePeriod;

    /**
     * 市场占有率得分
     */
    private BigDecimal marketShareScore;

    /**
     * 人力资源效率得分
     */
    private BigDecimal hrEfficiencyScore;

    /**
     * 设备效率得分
     */
    private BigDecimal equipmentEfficiencyScore;

    /**
     * 收入结构得分
     */
    private BigDecimal revenueStructureScore;

    /**
     * 总得分（1-100分）
     */
    private BigDecimal totalScore;

    /**
     * 评分等级（优秀、良好、一般、较差）
     */
    private String scoreLevel;

    /**
     * 评估结果描述
     */
    private String evaluationResult;
}
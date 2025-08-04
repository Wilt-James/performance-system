package com.hospital.performance.service;

import com.hospital.performance.entity.PerformanceData;

import java.util.List;
import java.util.Map;

/**
 * 绩效计算服务接口
 */
public interface PerformanceCalculationService {

    /**
     * 执行绩效计算
     * 
     * @param schemeId 绩效方案ID
     * @param period 计算期间 (格式: YYYY-MM)
     * @param deptIds 部门ID列表 (为空时计算所有部门)
     * @param calculationType 计算类型 (1: 科室绩效, 2: 个人绩效)
     * @return 计算记录ID
     */
    Long executeCalculation(Long schemeId, String period, List<Long> deptIds, Integer calculationType);

    /**
     * 获取计算结果
     * 
     * @param calculationRecordId 计算记录ID
     * @return 绩效数据列表
     */
    List<PerformanceData> getCalculationResult(Long calculationRecordId);

    /**
     * 获取计算步骤详情
     * 
     * @param schemeId 绩效方案ID
     * @param targetId 目标ID (部门ID或用户ID)
     * @param period 计算期间
     * @return 计算步骤详情
     */
    Map<String, Object> getCalculationSteps(Long schemeId, Long targetId, String period);

    /**
     * 重新计算
     * 
     * @param calculationRecordId 计算记录ID
     * @return 是否成功
     */
    boolean recalculate(Long calculationRecordId);

    /**
     * 发布计算结果
     * 
     * @param calculationRecordId 计算记录ID
     * @return 是否成功
     */
    boolean publishCalculationResult(Long calculationRecordId);

    /**
     * 获取计算历史记录
     * 
     * @param period 计算期间
     * @param schemeId 绩效方案ID
     * @return 计算记录列表
     */
    List<Map<String, Object>> getCalculationHistory(String period, Long schemeId);

    /**
     * 删除计算记录
     * 
     * @param calculationRecordId 计算记录ID
     * @return 是否成功
     */
    boolean deleteCalculationRecord(Long calculationRecordId);
}
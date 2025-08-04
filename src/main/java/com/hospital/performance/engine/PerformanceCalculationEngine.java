package com.hospital.performance.engine;

import com.hospital.performance.entity.Department;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.entity.PerformanceScheme;
import com.hospital.performance.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 绩效计算引擎接口
 */
public interface PerformanceCalculationEngine {

    /**
     * 计算部门绩效
     * 
     * @param scheme 绩效方案
     * @param department 部门
     * @param period 计算期间 (格式: YYYY-MM)
     * @param baseData 基础数据
     * @return 绩效数据列表
     */
    List<PerformanceData> calculateDepartmentPerformance(
            PerformanceScheme scheme, 
            Department department, 
            String period, 
            Map<String, Object> baseData);

    /**
     * 计算个人绩效
     * 
     * @param scheme 绩效方案
     * @param user 用户
     * @param period 计算期间 (格式: YYYY-MM)
     * @param baseData 基础数据
     * @return 绩效数据列表
     */
    List<PerformanceData> calculateUserPerformance(
            PerformanceScheme scheme, 
            User user, 
            String period, 
            Map<String, Object> baseData);

    /**
     * 验证计算结果
     * 
     * @param performanceData 绩效数据列表
     * @return 验证结果
     */
    boolean validateCalculationResult(List<PerformanceData> performanceData);

    /**
     * 获取计算步骤详情
     * 
     * @param scheme 绩效方案
     * @param targetId 目标ID（部门ID或用户ID）
     * @param period 计算期间
     * @return 计算步骤详情
     */
    Map<String, Object> getCalculationSteps(PerformanceScheme scheme, Long targetId, String period);
}
package com.hospital.performance.engine.impl;

import com.hospital.performance.engine.PerformanceCalculationEngine;
import com.hospital.performance.entity.Department;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.entity.PerformanceIndicator;
import com.hospital.performance.entity.PerformanceScheme;
import com.hospital.performance.entity.User;
import com.hospital.performance.service.PerformanceIndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作量法绩效计算引擎
 */
@Slf4j
@Component("workloadCalculationEngine")
@RequiredArgsConstructor
public class WorkloadCalculationEngine implements PerformanceCalculationEngine {

    private final PerformanceIndicatorService indicatorService;

    @Override
    public List<PerformanceData> calculateDepartmentPerformance(
            PerformanceScheme scheme, 
            Department department, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算部门绩效 - 工作量法: 部门={}, 期间={}", department.getDeptName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取工作量相关指标
        List<PerformanceIndicator> workloadIndicators = indicatorService.getByIndicatorType(2); // 工作量类指标
        
        for (PerformanceIndicator indicator : workloadIndicators) {
            if (indicator.getStatus() != 1) {
                continue; // 跳过禁用的指标
            }
            
            PerformanceData performanceData = new PerformanceData();
            performanceData.setDataPeriod(period);
            performanceData.setDeptId(department.getId());
            performanceData.setDeptName(department.getDeptName());
            performanceData.setIndicatorId(indicator.getId());
            performanceData.setIndicatorCode(indicator.getIndicatorCode());
            performanceData.setIndicatorName(indicator.getIndicatorName());
            performanceData.setTargetValue(indicator.getTargetValue());
            performanceData.setWeight(indicator.getWeight());
            performanceData.setDataSource(1); // 系统计算
            performanceData.setStatisticsType(1); // 默认统计口径
            performanceData.setStatus(2); // 已确认
            
            // 从基础数据中获取实际值
            BigDecimal actualValue = getActualValue(baseData, indicator.getIndicatorCode(), department.getId());
            performanceData.setIndicatorValue(actualValue);
            
            // 计算完成率
            BigDecimal completionRate = calculateCompletionRate(actualValue, indicator.getTargetValue());
            performanceData.setCompletionRate(completionRate);
            
            // 计算得分 (工作量法: 完成率 * 权重 * 100)
            BigDecimal score = completionRate.multiply(indicator.getWeight()).multiply(BigDecimal.valueOf(100));
            performanceData.setScore(score);
            
            // 计算绩效金额 (这里需要根据实际业务规则调整)
            BigDecimal performanceAmount = calculatePerformanceAmount(score, department, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("部门绩效计算完成 - 工作量法: 部门={}, 指标数={}", department.getDeptName(), results.size());
        return results;
    }

    @Override
    public List<PerformanceData> calculateUserPerformance(
            PerformanceScheme scheme, 
            User user, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算个人绩效 - 工作量法: 用户={}, 期间={}", user.getRealName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取个人适用的工作量指标
        List<PerformanceIndicator> personalIndicators = indicatorService.getByApplicableScope(3); // 个人适用
        
        for (PerformanceIndicator indicator : personalIndicators) {
            if (indicator.getStatus() != 1 || indicator.getIndicatorType() != 2) {
                continue; // 跳过禁用的指标或非工作量指标
            }
            
            PerformanceData performanceData = new PerformanceData();
            performanceData.setDataPeriod(period);
            performanceData.setDeptId(user.getDeptId());
            performanceData.setUserId(user.getId());
            performanceData.setUserName(user.getRealName());
            performanceData.setIndicatorId(indicator.getId());
            performanceData.setIndicatorCode(indicator.getIndicatorCode());
            performanceData.setIndicatorName(indicator.getIndicatorName());
            performanceData.setTargetValue(indicator.getTargetValue());
            performanceData.setWeight(indicator.getWeight());
            performanceData.setDataSource(1); // 系统计算
            performanceData.setStatisticsType(1); // 默认统计口径
            performanceData.setStatus(2); // 已确认
            
            // 从基础数据中获取个人实际值
            BigDecimal actualValue = getPersonalActualValue(baseData, indicator.getIndicatorCode(), user.getId());
            performanceData.setIndicatorValue(actualValue);
            
            // 计算完成率
            BigDecimal completionRate = calculateCompletionRate(actualValue, indicator.getTargetValue());
            performanceData.setCompletionRate(completionRate);
            
            // 计算得分
            BigDecimal score = completionRate.multiply(indicator.getWeight()).multiply(BigDecimal.valueOf(100));
            performanceData.setScore(score);
            
            // 计算绩效金额
            BigDecimal performanceAmount = calculatePersonalPerformanceAmount(score, user, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("个人绩效计算完成 - 工作量法: 用户={}, 指标数={}", user.getRealName(), results.size());
        return results;
    }

    @Override
    public boolean validateCalculationResult(List<PerformanceData> performanceData) {
        if (performanceData == null || performanceData.isEmpty()) {
            return false;
        }
        
        for (PerformanceData data : performanceData) {
            // 验证必要字段
            if (data.getIndicatorValue() == null || 
                data.getScore() == null || 
                data.getPerformanceAmount() == null) {
                return false;
            }
            
            // 验证数值范围
            if (data.getScore().compareTo(BigDecimal.ZERO) < 0 || 
                data.getPerformanceAmount().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Map<String, Object> getCalculationSteps(PerformanceScheme scheme, Long targetId, String period) {
        Map<String, Object> steps = new HashMap<>();
        
        steps.put("calculationMethod", "工作量法");
        steps.put("description", "基于工作量完成情况计算绩效，公式：完成率 × 权重 × 100");
        
        List<Map<String, Object>> stepDetails = new ArrayList<>();
        
        Map<String, Object> step1 = new HashMap<>();
        step1.put("step", 1);
        step1.put("name", "获取工作量数据");
        step1.put("description", "从HIS系统获取门诊人数、住院人数等工作量指标数据");
        stepDetails.add(step1);
        
        Map<String, Object> step2 = new HashMap<>();
        step2.put("step", 2);
        step2.put("name", "计算完成率");
        step2.put("description", "完成率 = 实际值 ÷ 目标值");
        stepDetails.add(step2);
        
        Map<String, Object> step3 = new HashMap<>();
        step3.put("step", 3);
        step3.put("name", "计算加权得分");
        step3.put("description", "得分 = 完成率 × 权重 × 100");
        stepDetails.add(step3);
        
        Map<String, Object> step4 = new HashMap<>();
        step4.put("step", 4);
        step4.put("name", "计算绩效金额");
        step4.put("description", "根据得分和绩效系数计算最终绩效金额");
        stepDetails.add(step4);
        
        steps.put("steps", stepDetails);
        steps.put("targetId", targetId);
        steps.put("period", period);
        
        return steps;
    }

    /**
     * 从基础数据中获取实际值
     */
    private BigDecimal getActualValue(Map<String, Object> baseData, String indicatorCode, Long deptId) {
        // 这里应该从实际的数据源获取数据
        // 为了演示，使用模拟数据
        String key = indicatorCode + "_" + deptId;
        Object value = baseData.get(key);
        
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        
        // 模拟数据
        switch (indicatorCode) {
            case "MZRS":
                return BigDecimal.valueOf(800 + (Math.random() * 400)); // 800-1200
            case "ZYRS":
                return BigDecimal.valueOf(150 + (Math.random() * 100)); // 150-250
            case "YYSR":
                return BigDecimal.valueOf(400000 + (Math.random() * 200000)); // 40-60万
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 从基础数据中获取个人实际值
     */
    private BigDecimal getPersonalActualValue(Map<String, Object> baseData, String indicatorCode, Long userId) {
        String key = indicatorCode + "_USER_" + userId;
        Object value = baseData.get(key);
        
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        
        // 模拟个人数据
        switch (indicatorCode) {
            case "MZRS":
                return BigDecimal.valueOf(50 + (Math.random() * 30)); // 50-80
            case "ZYRS":
                return BigDecimal.valueOf(10 + (Math.random() * 10)); // 10-20
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 计算完成率
     */
    private BigDecimal calculateCompletionRate(BigDecimal actualValue, BigDecimal targetValue) {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return actualValue.divide(targetValue, 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算部门绩效金额
     */
    private BigDecimal calculatePerformanceAmount(BigDecimal score, Department department, PerformanceIndicator indicator) {
        // 这里应该根据实际的绩效分配规则计算
        // 为了演示，使用简单的计算方式
        BigDecimal baseAmount = BigDecimal.valueOf(1000); // 基础绩效金额
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算个人绩效金额
     */
    private BigDecimal calculatePersonalPerformanceAmount(BigDecimal score, User user, PerformanceIndicator indicator) {
        // 个人绩效金额计算
        BigDecimal baseAmount = BigDecimal.valueOf(500); // 个人基础绩效金额
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
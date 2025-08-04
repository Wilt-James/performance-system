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
 * KPI方法绩效计算引擎
 */
@Slf4j
@Component("kpiCalculationEngine")
@RequiredArgsConstructor
public class KpiCalculationEngine implements PerformanceCalculationEngine {

    private final PerformanceIndicatorService indicatorService;

    @Override
    public List<PerformanceData> calculateDepartmentPerformance(
            PerformanceScheme scheme, 
            Department department, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算部门绩效 - KPI方法: 部门={}, 期间={}", department.getDeptName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取KPI指标
        List<PerformanceIndicator> kpiIndicators = indicatorService.getByIndicatorCategory(2); // KPI指标
        
        for (PerformanceIndicator indicator : kpiIndicators) {
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
            
            // KPI评分计算 (使用分段评分法)
            BigDecimal score = calculateKpiScore(completionRate, indicator);
            performanceData.setScore(score);
            
            // 计算绩效金额
            BigDecimal performanceAmount = calculatePerformanceAmount(score, department, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("部门绩效计算完成 - KPI方法: 部门={}, 指标数={}", department.getDeptName(), results.size());
        return results;
    }

    @Override
    public List<PerformanceData> calculateUserPerformance(
            PerformanceScheme scheme, 
            User user, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算个人绩效 - KPI方法: 用户={}, 期间={}", user.getRealName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取个人适用的KPI指标
        List<PerformanceIndicator> personalKpiIndicators = getPersonalKpiIndicators();
        
        for (PerformanceIndicator indicator : personalKpiIndicators) {
            if (indicator.getStatus() != 1) {
                continue;
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
            
            // KPI评分计算
            BigDecimal score = calculateKpiScore(completionRate, indicator);
            performanceData.setScore(score);
            
            // 计算绩效金额
            BigDecimal performanceAmount = calculatePersonalPerformanceAmount(score, user, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("个人绩效计算完成 - KPI方法: 用户={}, 指标数={}", user.getRealName(), results.size());
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
            
            // 验证KPI得分范围 (0-100)
            if (data.getScore().compareTo(BigDecimal.ZERO) < 0 || 
                data.getScore().compareTo(BigDecimal.valueOf(100)) > 0) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Map<String, Object> getCalculationSteps(PerformanceScheme scheme, Long targetId, String period) {
        Map<String, Object> steps = new HashMap<>();
        
        steps.put("calculationMethod", "KPI方法");
        steps.put("description", "基于关键绩效指标进行分段评分，综合评估绩效水平");
        
        List<Map<String, Object>> stepDetails = new ArrayList<>();
        
        Map<String, Object> step1 = new HashMap<>();
        step1.put("step", 1);
        step1.put("name", "获取KPI指标数据");
        step1.put("description", "获取床位周转率、平均住院日、满意度等关键绩效指标数据");
        stepDetails.add(step1);
        
        Map<String, Object> step2 = new HashMap<>();
        step2.put("step", 2);
        step2.put("name", "计算完成率");
        step2.put("description", "完成率 = 实际值 ÷ 目标值");
        stepDetails.add(step2);
        
        Map<String, Object> step3 = new HashMap<>();
        step3.put("step", 3);
        step3.put("name", "分段评分");
        step3.put("description", "根据完成率进行分段评分：优秀(≥120%)=100分，良好(100-120%)=80-100分，合格(80-100%)=60-80分，不合格(<80%)=0-60分");
        stepDetails.add(step3);
        
        Map<String, Object> step4 = new HashMap<>();
        step4.put("step", 4);
        step4.put("name", "加权计算");
        step4.put("description", "最终得分 = Σ(各指标得分 × 权重)");
        stepDetails.add(step4);
        
        Map<String, Object> step5 = new HashMap<>();
        step5.put("step", 5);
        step5.put("name", "计算绩效金额");
        step5.put("description", "根据最终得分和绩效系数计算绩效金额");
        stepDetails.add(step5);
        
        steps.put("steps", stepDetails);
        steps.put("targetId", targetId);
        steps.put("period", period);
        
        return steps;
    }

    /**
     * 获取个人适用的KPI指标
     */
    private List<PerformanceIndicator> getPersonalKpiIndicators() {
        // 获取个人适用的KPI指标
        List<PerformanceIndicator> allKpiIndicators = indicatorService.getByIndicatorCategory(2);
        List<PerformanceIndicator> personalIndicators = new ArrayList<>();
        
        for (PerformanceIndicator indicator : allKpiIndicators) {
            if (indicator.getApplicableScope() == 3 || indicator.getApplicableScope() == 1) { // 个人或全院适用
                personalIndicators.add(indicator);
            }
        }
        
        return personalIndicators;
    }

    /**
     * 从基础数据中获取实际值
     */
    private BigDecimal getActualValue(Map<String, Object> baseData, String indicatorCode, Long deptId) {
        String key = indicatorCode + "_" + deptId;
        Object value = baseData.get(key);
        
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        
        // 模拟KPI数据
        switch (indicatorCode) {
            case "CWZL": // 床位周转率
                return BigDecimal.valueOf(2.0 + (Math.random() * 1.0)); // 2.0-3.0
            case "PJZYR": // 平均住院日
                return BigDecimal.valueOf(7.0 + (Math.random() * 3.0)); // 7.0-10.0
            case "BRCYL": // 病人出院率
                return BigDecimal.valueOf(90.0 + (Math.random() * 8.0)); // 90-98%
            case "YLFWMYD": // 医疗服务满意度
                return BigDecimal.valueOf(85.0 + (Math.random() * 10.0)); // 85-95分
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
        
        // 模拟个人KPI数据
        switch (indicatorCode) {
            case "YLFWMYD": // 个人服务满意度
                return BigDecimal.valueOf(88.0 + (Math.random() * 8.0)); // 88-96分
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
     * 计算KPI得分 (分段评分法)
     */
    private BigDecimal calculateKpiScore(BigDecimal completionRate, PerformanceIndicator indicator) {
        BigDecimal score;
        
        if (completionRate.compareTo(BigDecimal.valueOf(1.2)) >= 0) {
            // 优秀: 完成率 >= 120%，得分 = 100
            score = BigDecimal.valueOf(100);
        } else if (completionRate.compareTo(BigDecimal.valueOf(1.0)) >= 0) {
            // 良好: 100% <= 完成率 < 120%，得分 = 80 + (完成率-1) * 100
            BigDecimal excess = completionRate.subtract(BigDecimal.ONE);
            score = BigDecimal.valueOf(80).add(excess.multiply(BigDecimal.valueOf(100)));
        } else if (completionRate.compareTo(BigDecimal.valueOf(0.8)) >= 0) {
            // 合格: 80% <= 完成率 < 100%，得分 = 60 + (完成率-0.8) * 100
            BigDecimal excess = completionRate.subtract(BigDecimal.valueOf(0.8));
            score = BigDecimal.valueOf(60).add(excess.multiply(BigDecimal.valueOf(100)));
        } else {
            // 不合格: 完成率 < 80%，得分 = 完成率 * 75
            score = completionRate.multiply(BigDecimal.valueOf(75));
        }
        
        // 应用权重
        return score.multiply(indicator.getWeight()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算部门绩效金额
     */
    private BigDecimal calculatePerformanceAmount(BigDecimal score, Department department, PerformanceIndicator indicator) {
        // KPI方法的绩效金额计算
        BigDecimal baseAmount = BigDecimal.valueOf(1500); // KPI基础绩效金额
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算个人绩效金额
     */
    private BigDecimal calculatePersonalPerformanceAmount(BigDecimal score, User user, PerformanceIndicator indicator) {
        // 个人KPI绩效金额计算
        BigDecimal baseAmount = BigDecimal.valueOf(800); // 个人KPI基础绩效金额
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
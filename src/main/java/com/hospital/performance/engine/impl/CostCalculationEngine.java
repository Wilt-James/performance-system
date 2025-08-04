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
 * 成本核算法绩效计算引擎
 */
@Slf4j
@Component("costCalculationEngine")
@RequiredArgsConstructor
public class CostCalculationEngine implements PerformanceCalculationEngine {

    private final PerformanceIndicatorService indicatorService;

    @Override
    public List<PerformanceData> calculateDepartmentPerformance(
            PerformanceScheme scheme, 
            Department department, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算部门绩效 - 成本核算法: 部门={}, 期间={}", department.getDeptName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取成本相关指标
        List<PerformanceIndicator> costIndicators = indicatorService.getByIndicatorCategory(3); // 成本指标
        
        for (PerformanceIndicator indicator : costIndicators) {
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
            
            // 从基础数据中获取成本数据
            Map<String, BigDecimal> costData = getCostData(baseData, indicator.getIndicatorCode(), department.getId());
            
            // 计算成本效益指标
            BigDecimal actualValue = calculateCostEfficiencyValue(costData, indicator);
            performanceData.setIndicatorValue(actualValue);
            
            // 计算完成率
            BigDecimal completionRate = calculateCompletionRate(actualValue, indicator.getTargetValue(), indicator.getIndicatorCode());
            performanceData.setCompletionRate(completionRate);
            
            // 成本核算法评分计算
            BigDecimal score = calculateCostScore(completionRate, costData, indicator);
            performanceData.setScore(score);
            
            // 计算绩效金额
            BigDecimal performanceAmount = calculatePerformanceAmount(score, costData, department, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("部门绩效计算完成 - 成本核算法: 部门={}, 指标数={}", department.getDeptName(), results.size());
        return results;
    }

    @Override
    public List<PerformanceData> calculateUserPerformance(
            PerformanceScheme scheme, 
            User user, 
            String period, 
            Map<String, Object> baseData) {
        
        log.info("开始计算个人绩效 - 成本核算法: 用户={}, 期间={}", user.getRealName(), period);
        
        List<PerformanceData> results = new ArrayList<>();
        
        // 获取个人适用的成本指标
        List<PerformanceIndicator> personalCostIndicators = getPersonalCostIndicators();
        
        for (PerformanceIndicator indicator : personalCostIndicators) {
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
            
            // 从基础数据中获取个人成本数据
            Map<String, BigDecimal> costData = getPersonalCostData(baseData, indicator.getIndicatorCode(), user.getId());
            
            // 计算个人成本效益指标
            BigDecimal actualValue = calculateCostEfficiencyValue(costData, indicator);
            performanceData.setIndicatorValue(actualValue);
            
            // 计算完成率
            BigDecimal completionRate = calculateCompletionRate(actualValue, indicator.getTargetValue(), indicator.getIndicatorCode());
            performanceData.setCompletionRate(completionRate);
            
            // 成本核算法评分计算
            BigDecimal score = calculateCostScore(completionRate, costData, indicator);
            performanceData.setScore(score);
            
            // 计算绩效金额
            BigDecimal performanceAmount = calculatePersonalPerformanceAmount(score, costData, user, indicator);
            performanceData.setPerformanceAmount(performanceAmount);
            
            results.add(performanceData);
        }
        
        log.info("个人绩效计算完成 - 成本核算法: 用户={}, 指标数={}", user.getRealName(), results.size());
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
            
            // 验证成本核算得分范围 (0-100)
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
        
        steps.put("calculationMethod", "成本核算法");
        steps.put("description", "基于成本效益分析进行绩效评估，综合考虑投入产出比和成本控制效果");
        
        List<Map<String, Object>> stepDetails = new ArrayList<>();
        
        Map<String, Object> step1 = new HashMap<>();
        step1.put("step", 1);
        step1.put("name", "获取成本数据");
        step1.put("description", "从财务系统获取人力成本、设备成本、材料成本等数据");
        stepDetails.add(step1);
        
        Map<String, Object> step2 = new HashMap<>();
        step2.put("step", 2);
        step2.put("name", "计算成本效益比");
        step2.put("description", "成本效益比 = 收入 ÷ 总成本");
        stepDetails.add(step2);
        
        Map<String, Object> step3 = new HashMap<>();
        step3.put("step", 3);
        step3.put("name", "成本控制评估");
        step3.put("description", "评估成本控制效果，计算成本节约率");
        stepDetails.add(step3);
        
        Map<String, Object> step4 = new HashMap<>();
        step4.put("step", 4);
        step4.put("name", "综合评分");
        step4.put("description", "综合成本效益比和成本控制效果计算最终得分");
        stepDetails.add(step4);
        
        Map<String, Object> step5 = new HashMap<>();
        step5.put("step", 5);
        step5.put("name", "计算绩效金额");
        step5.put("description", "根据成本节约和效益提升计算绩效奖励金额");
        stepDetails.add(step5);
        
        steps.put("steps", stepDetails);
        steps.put("targetId", targetId);
        steps.put("period", period);
        
        return steps;
    }

    /**
     * 获取个人适用的成本指标
     */
    private List<PerformanceIndicator> getPersonalCostIndicators() {
        List<PerformanceIndicator> allCostIndicators = indicatorService.getByIndicatorCategory(3);
        List<PerformanceIndicator> personalIndicators = new ArrayList<>();
        
        for (PerformanceIndicator indicator : allCostIndicators) {
            if (indicator.getApplicableScope() == 3 || indicator.getApplicableScope() == 1) { // 个人或全院适用
                personalIndicators.add(indicator);
            }
        }
        
        return personalIndicators;
    }

    /**
     * 获取成本数据
     */
    private Map<String, BigDecimal> getCostData(Map<String, Object> baseData, String indicatorCode, Long deptId) {
        Map<String, BigDecimal> costData = new HashMap<>();
        
        // 模拟成本数据
        switch (indicatorCode) {
            case "CBXYL": // 成本效益率
                costData.put("totalRevenue", BigDecimal.valueOf(500000)); // 总收入
                costData.put("totalCost", BigDecimal.valueOf(400000)); // 总成本
                costData.put("laborCost", BigDecimal.valueOf(200000)); // 人力成本
                costData.put("equipmentCost", BigDecimal.valueOf(100000)); // 设备成本
                costData.put("materialCost", BigDecimal.valueOf(100000)); // 材料成本
                break;
            case "CBKZL": // 成本控制率
                costData.put("budgetCost", BigDecimal.valueOf(420000)); // 预算成本
                costData.put("actualCost", BigDecimal.valueOf(400000)); // 实际成本
                break;
            case "RJCB": // 人均成本
                costData.put("totalCost", BigDecimal.valueOf(400000)); // 总成本
                costData.put("patientCount", BigDecimal.valueOf(800)); // 患者数量
                break;
            default:
                costData.put("totalRevenue", BigDecimal.valueOf(500000));
                costData.put("totalCost", BigDecimal.valueOf(400000));
        }
        
        return costData;
    }

    /**
     * 获取个人成本数据
     */
    private Map<String, BigDecimal> getPersonalCostData(Map<String, Object> baseData, String indicatorCode, Long userId) {
        Map<String, BigDecimal> costData = new HashMap<>();
        
        // 模拟个人成本数据
        switch (indicatorCode) {
            case "GRCBXYL": // 个人成本效益率
                costData.put("personalRevenue", BigDecimal.valueOf(25000)); // 个人创收
                costData.put("personalCost", BigDecimal.valueOf(20000)); // 个人成本
                break;
            default:
                costData.put("personalRevenue", BigDecimal.valueOf(25000));
                costData.put("personalCost", BigDecimal.valueOf(20000));
        }
        
        return costData;
    }

    /**
     * 计算成本效益指标值
     */
    private BigDecimal calculateCostEfficiencyValue(Map<String, BigDecimal> costData, PerformanceIndicator indicator) {
        switch (indicator.getIndicatorCode()) {
            case "CBXYL": // 成本效益率
                BigDecimal totalRevenue = costData.get("totalRevenue");
                BigDecimal totalCost = costData.get("totalCost");
                return totalRevenue.divide(totalCost, 4, RoundingMode.HALF_UP);
                
            case "CBKZL": // 成本控制率
                BigDecimal budgetCost = costData.get("budgetCost");
                BigDecimal actualCost = costData.get("actualCost");
                BigDecimal savings = budgetCost.subtract(actualCost);
                return savings.divide(budgetCost, 4, RoundingMode.HALF_UP);
                
            case "RJCB": // 人均成本
                BigDecimal cost = costData.get("totalCost");
                BigDecimal patientCount = costData.get("patientCount");
                return cost.divide(patientCount, 2, RoundingMode.HALF_UP);
                
            case "GRCBXYL": // 个人成本效益率
                BigDecimal personalRevenue = costData.get("personalRevenue");
                BigDecimal personalCost = costData.get("personalCost");
                return personalRevenue.divide(personalCost, 4, RoundingMode.HALF_UP);
                
            default:
                return BigDecimal.ONE;
        }
    }

    /**
     * 计算完成率
     */
    private BigDecimal calculateCompletionRate(BigDecimal actualValue, BigDecimal targetValue, String indicatorCode) {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // 对于成本类指标，值越低越好
        if (indicatorCode.contains("CB") && !indicatorCode.contains("XYL")) {
            // 成本控制类指标：完成率 = 目标值 ÷ 实际值
            return targetValue.divide(actualValue, 4, RoundingMode.HALF_UP);
        } else {
            // 效益类指标：完成率 = 实际值 ÷ 目标值
            return actualValue.divide(targetValue, 4, RoundingMode.HALF_UP);
        }
    }

    /**
     * 计算成本核算得分
     */
    private BigDecimal calculateCostScore(BigDecimal completionRate, Map<String, BigDecimal> costData, PerformanceIndicator indicator) {
        BigDecimal baseScore;
        
        // 基础得分计算
        if (completionRate.compareTo(BigDecimal.valueOf(1.2)) >= 0) {
            // 优秀: 完成率 >= 120%，得分 = 100
            baseScore = BigDecimal.valueOf(100);
        } else if (completionRate.compareTo(BigDecimal.valueOf(1.0)) >= 0) {
            // 良好: 100% <= 完成率 < 120%，得分 = 80 + (完成率-1) * 100
            BigDecimal excess = completionRate.subtract(BigDecimal.ONE);
            baseScore = BigDecimal.valueOf(80).add(excess.multiply(BigDecimal.valueOf(100)));
        } else if (completionRate.compareTo(BigDecimal.valueOf(0.8)) >= 0) {
            // 合格: 80% <= 完成率 < 100%，得分 = 60 + (完成率-0.8) * 100
            BigDecimal excess = completionRate.subtract(BigDecimal.valueOf(0.8));
            baseScore = BigDecimal.valueOf(60).add(excess.multiply(BigDecimal.valueOf(100)));
        } else {
            // 不合格: 完成率 < 80%，得分 = 完成率 * 75
            baseScore = completionRate.multiply(BigDecimal.valueOf(75));
        }
        
        // 成本控制奖励
        BigDecimal costControlBonus = calculateCostControlBonus(costData);
        
        // 最终得分 = 基础得分 + 成本控制奖励
        BigDecimal finalScore = baseScore.add(costControlBonus);
        
        // 应用权重
        return finalScore.multiply(indicator.getWeight()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算成本控制奖励
     */
    private BigDecimal calculateCostControlBonus(Map<String, BigDecimal> costData) {
        BigDecimal budgetCost = costData.get("budgetCost");
        BigDecimal actualCost = costData.get("actualCost");
        
        if (budgetCost != null && actualCost != null && actualCost.compareTo(budgetCost) < 0) {
            // 成本节约率
            BigDecimal savingsRate = budgetCost.subtract(actualCost).divide(budgetCost, 4, RoundingMode.HALF_UP);
            // 奖励分数 = 节约率 * 10 (最高10分奖励)
            return savingsRate.multiply(BigDecimal.valueOf(10)).min(BigDecimal.valueOf(10));
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * 计算部门绩效金额
     */
    private BigDecimal calculatePerformanceAmount(BigDecimal score, Map<String, BigDecimal> costData, 
                                                 Department department, PerformanceIndicator indicator) {
        // 成本核算法的绩效金额计算
        BigDecimal baseAmount = BigDecimal.valueOf(2000); // 成本核算基础绩效金额
        
        // 成本节约奖励
        BigDecimal costSavings = costData.get("budgetCost") != null && costData.get("actualCost") != null ?
                costData.get("budgetCost").subtract(costData.get("actualCost")) : BigDecimal.ZERO;
        
        BigDecimal savingsBonus = costSavings.multiply(BigDecimal.valueOf(0.1)); // 节约金额的10%作为奖励
        
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .add(savingsBonus);
    }

    /**
     * 计算个人绩效金额
     */
    private BigDecimal calculatePersonalPerformanceAmount(BigDecimal score, Map<String, BigDecimal> costData, 
                                                         User user, PerformanceIndicator indicator) {
        // 个人成本核算绩效金额计算
        BigDecimal baseAmount = BigDecimal.valueOf(1000); // 个人成本核算基础绩效金额
        
        return score.multiply(baseAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
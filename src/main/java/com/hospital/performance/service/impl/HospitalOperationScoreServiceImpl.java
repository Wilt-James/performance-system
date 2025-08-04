package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.HospitalOperationScore;
import com.hospital.performance.mapper.HospitalOperationScoreMapper;
import com.hospital.performance.service.HospitalOperationScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 医院运营评分服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalOperationScoreServiceImpl extends ServiceImpl<HospitalOperationScoreMapper, HospitalOperationScore> 
        implements HospitalOperationScoreService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HospitalOperationScore calculateOperationScore(String period) {
        log.info("开始计算医院运营评分: 期间={}", period);
        
        // 检查是否已存在该期间的评分
        HospitalOperationScore existingScore = getByPeriod(period);
        if (existingScore != null) {
            log.info("该期间已存在评分记录，将更新现有记录");
        }
        
        // 获取基础数据
        Map<String, Object> baseData = getBaseDataForScoring(period);
        
        // 计算各维度得分
        BigDecimal marketShareScore = calculateMarketShareScore(baseData);
        BigDecimal hrEfficiencyScore = calculateHrEfficiencyScore(baseData);
        BigDecimal equipmentEfficiencyScore = calculateEquipmentEfficiencyScore(baseData);
        BigDecimal revenueStructureScore = calculateRevenueStructureScore(baseData);
        
        // 计算总得分 (各维度权重相等，各占25%)
        BigDecimal totalScore = marketShareScore.multiply(BigDecimal.valueOf(0.25))
                .add(hrEfficiencyScore.multiply(BigDecimal.valueOf(0.25)))
                .add(equipmentEfficiencyScore.multiply(BigDecimal.valueOf(0.25)))
                .add(revenueStructureScore.multiply(BigDecimal.valueOf(0.25)))
                .setScale(2, RoundingMode.HALF_UP);
        
        // 确定评分等级
        String scoreLevel = determineScoreLevel(totalScore);
        
        // 生成评估结果描述
        String evaluationResult = generateEvaluationResult(totalScore, marketShareScore, 
                hrEfficiencyScore, equipmentEfficiencyScore, revenueStructureScore);
        
        // 保存或更新评分记录
        HospitalOperationScore score = existingScore != null ? existingScore : new HospitalOperationScore();
        score.setScorePeriod(period);
        score.setMarketShareScore(marketShareScore);
        score.setHrEfficiencyScore(hrEfficiencyScore);
        score.setEquipmentEfficiencyScore(equipmentEfficiencyScore);
        score.setRevenueStructureScore(revenueStructureScore);
        score.setTotalScore(totalScore);
        score.setScoreLevel(scoreLevel);
        score.setEvaluationResult(evaluationResult);
        
        if (existingScore != null) {
            this.updateById(score);
        } else {
            this.save(score);
        }
        
        log.info("医院运营评分计算完成: 期间={}, 总得分={}", period, totalScore);
        return score;
    }

    @Override
    public List<HospitalOperationScore> getScoreHistory(String startPeriod, String endPeriod) {
        LambdaQueryWrapper<HospitalOperationScore> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(startPeriod)) {
            wrapper.ge(HospitalOperationScore::getScorePeriod, startPeriod);
        }
        
        if (StringUtils.isNotBlank(endPeriod)) {
            wrapper.le(HospitalOperationScore::getScorePeriod, endPeriod);
        }
        
        wrapper.orderByDesc(HospitalOperationScore::getScorePeriod);
        
        return this.list(wrapper);
    }

    @Override
    public List<HospitalOperationScore> getScoreData(String period) {
        LambdaQueryWrapper<HospitalOperationScore> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(period)) {
            wrapper.eq(HospitalOperationScore::getScorePeriod, period);
        }
        
        wrapper.orderByDesc(HospitalOperationScore::getScorePeriod);
        
        return this.list(wrapper);
    }

    @Override
    public Page<HospitalOperationScore> pageScores(Page<HospitalOperationScore> page, String startPeriod, String endPeriod) {
        LambdaQueryWrapper<HospitalOperationScore> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(startPeriod)) {
            wrapper.ge(HospitalOperationScore::getScorePeriod, startPeriod);
        }
        
        if (StringUtils.isNotBlank(endPeriod)) {
            wrapper.le(HospitalOperationScore::getScorePeriod, endPeriod);
        }
        
        wrapper.orderByDesc(HospitalOperationScore::getScorePeriod);
        
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> getScoreTrend(String startPeriod, String endPeriod) {
        List<HospitalOperationScore> scores = getScoreHistory(startPeriod, endPeriod);
        
        Map<String, Object> trendData = new HashMap<>();
        List<String> periods = new ArrayList<>();
        List<BigDecimal> totalScores = new ArrayList<>();
        List<BigDecimal> marketShareScores = new ArrayList<>();
        List<BigDecimal> hrEfficiencyScores = new ArrayList<>();
        List<BigDecimal> equipmentEfficiencyScores = new ArrayList<>();
        List<BigDecimal> revenueStructureScores = new ArrayList<>();
        
        for (HospitalOperationScore score : scores) {
            periods.add(score.getScorePeriod());
            totalScores.add(score.getTotalScore());
            marketShareScores.add(score.getMarketShareScore());
            hrEfficiencyScores.add(score.getHrEfficiencyScore());
            equipmentEfficiencyScores.add(score.getEquipmentEfficiencyScore());
            revenueStructureScores.add(score.getRevenueStructureScore());
        }
        
        trendData.put("periods", periods);
        trendData.put("totalScores", totalScores);
        trendData.put("marketShareScores", marketShareScores);
        trendData.put("hrEfficiencyScores", hrEfficiencyScores);
        trendData.put("equipmentEfficiencyScores", equipmentEfficiencyScores);
        trendData.put("revenueStructureScores", revenueStructureScores);
        
        return trendData;
    }

    @Override
    public Map<String, Object> getScoreAnalysis(String period) {
        HospitalOperationScore score = getByPeriod(period);
        if (score == null) {
            throw new RuntimeException("该期间的评分记录不存在");
        }
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("score", score);
        
        // 各维度分析
        List<Map<String, Object>> dimensionAnalysis = new ArrayList<>();
        
        dimensionAnalysis.add(createDimensionAnalysis("市场占有率", score.getMarketShareScore(), 
                "反映医院在区域内的市场地位和竞争力"));
        dimensionAnalysis.add(createDimensionAnalysis("人力资源效率", score.getHrEfficiencyScore(), 
                "反映医院人力资源的配置和使用效率"));
        dimensionAnalysis.add(createDimensionAnalysis("设备效率", score.getEquipmentEfficiencyScore(), 
                "反映医院设备的利用率和产出效率"));
        dimensionAnalysis.add(createDimensionAnalysis("收入结构", score.getRevenueStructureScore(), 
                "反映医院收入来源的合理性和可持续性"));
        
        analysis.put("dimensionAnalysis", dimensionAnalysis);
        
        return analysis;
    }

    @Override
    public Map<String, Object> getScoreComparison(String currentPeriod, String comparePeriod) {
        HospitalOperationScore currentScore = getByPeriod(currentPeriod);
        HospitalOperationScore compareScore = getByPeriod(comparePeriod);
        
        if (currentScore == null || compareScore == null) {
            throw new RuntimeException("评分记录不存在");
        }
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("currentScore", currentScore);
        comparison.put("compareScore", compareScore);
        
        // 计算变化
        Map<String, Object> changes = new HashMap<>();
        changes.put("totalScoreChange", currentScore.getTotalScore().subtract(compareScore.getTotalScore()));
        changes.put("marketShareScoreChange", currentScore.getMarketShareScore().subtract(compareScore.getMarketShareScore()));
        changes.put("hrEfficiencyScoreChange", currentScore.getHrEfficiencyScore().subtract(compareScore.getHrEfficiencyScore()));
        changes.put("equipmentEfficiencyScoreChange", currentScore.getEquipmentEfficiencyScore().subtract(compareScore.getEquipmentEfficiencyScore()));
        changes.put("revenueStructureScoreChange", currentScore.getRevenueStructureScore().subtract(compareScore.getRevenueStructureScore()));
        
        comparison.put("changes", changes);
        
        return comparison;
    }

    @Override
    public List<Map<String, Object>> getImprovementSuggestions(String period) {
        HospitalOperationScore score = getByPeriod(period);
        if (score == null) {
            throw new RuntimeException("该期间的评分记录不存在");
        }
        
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // 根据各维度得分提供改进建议
        if (score.getMarketShareScore().compareTo(BigDecimal.valueOf(70)) < 0) {
            suggestions.add(createSuggestion("市场占有率", "加强市场推广，提升服务质量，扩大患者群体"));
        }
        
        if (score.getHrEfficiencyScore().compareTo(BigDecimal.valueOf(70)) < 0) {
            suggestions.add(createSuggestion("人力资源效率", "优化人员配置，加强培训，提高工作效率"));
        }
        
        if (score.getEquipmentEfficiencyScore().compareTo(BigDecimal.valueOf(70)) < 0) {
            suggestions.add(createSuggestion("设备效率", "提高设备利用率，加强设备维护，优化检查流程"));
        }
        
        if (score.getRevenueStructureScore().compareTo(BigDecimal.valueOf(70)) < 0) {
            suggestions.add(createSuggestion("收入结构", "优化收入结构，减少对药品收入的依赖，提高医疗服务收入占比"));
        }
        
        return suggestions;
    }

    @Override
    public String exportScoreReport(String period) {
        // 这里应该实现实际的报告导出逻辑
        // 为了演示，返回模拟的文件路径
        return "/exports/operation_score_report_" + period + ".pdf";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recalculateScore(String period) {
        try {
            calculateOperationScore(period);
            return true;
        } catch (Exception e) {
            log.error("重新计算评分失败: period={}", period, e);
            return false;
        }
    }

    /**
     * 根据期间获取评分记录
     */
    private HospitalOperationScore getByPeriod(String period) {
        LambdaQueryWrapper<HospitalOperationScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HospitalOperationScore::getScorePeriod, period);
        return this.getOne(wrapper);
    }

    /**
     * 获取评分基础数据
     */
    private Map<String, Object> getBaseDataForScoring(String period) {
        // 这里应该从各个业务系统获取实际数据
        // 为了演示，使用模拟数据
        Map<String, Object> baseData = new HashMap<>();
        
        // 市场数据
        baseData.put("totalPatients", 15000); // 总患者数
        baseData.put("marketPatients", 50000); // 市场总患者数
        baseData.put("competitorCount", 5); // 竞争对手数量
        
        // 人力资源数据
        baseData.put("totalStaff", 800); // 总员工数
        baseData.put("totalRevenue", 50000000); // 总收入
        baseData.put("staffProductivity", 62500); // 人均产出
        
        // 设备数据
        baseData.put("equipmentUtilization", 0.75); // 设备利用率
        baseData.put("equipmentRevenue", 8000000); // 设备产出收入
        
        // 收入结构数据
        baseData.put("medicalServiceRevenue", 30000000); // 医疗服务收入
        baseData.put("drugRevenue", 15000000); // 药品收入
        baseData.put("materialRevenue", 5000000); // 材料收入
        
        return baseData;
    }

    /**
     * 计算市场占有率得分
     */
    private BigDecimal calculateMarketShareScore(Map<String, Object> baseData) {
        Integer totalPatients = (Integer) baseData.get("totalPatients");
        Integer marketPatients = (Integer) baseData.get("marketPatients");
        
        // 市场占有率 = 医院患者数 / 市场总患者数
        BigDecimal marketShare = BigDecimal.valueOf(totalPatients)
                .divide(BigDecimal.valueOf(marketPatients), 4, RoundingMode.HALF_UP);
        
        // 转换为得分 (假设30%市场占有率为满分100分)
        BigDecimal score = marketShare.divide(BigDecimal.valueOf(0.3), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        // 限制得分范围在0-100之间
        return score.min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
    }

    /**
     * 计算人力资源效率得分
     */
    private BigDecimal calculateHrEfficiencyScore(Map<String, Object> baseData) {
        Integer totalStaff = (Integer) baseData.get("totalStaff");
        Integer totalRevenue = (Integer) baseData.get("totalRevenue");
        
        // 人均产出
        BigDecimal perCapitaOutput = BigDecimal.valueOf(totalRevenue)
                .divide(BigDecimal.valueOf(totalStaff), 2, RoundingMode.HALF_UP);
        
        // 转换为得分 (假设人均产出70000为满分100分)
        BigDecimal score = perCapitaOutput.divide(BigDecimal.valueOf(70000), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return score.min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
    }

    /**
     * 计算设备效率得分
     */
    private BigDecimal calculateEquipmentEfficiencyScore(Map<String, Object> baseData) {
        Double equipmentUtilization = (Double) baseData.get("equipmentUtilization");
        
        // 设备利用率转换为得分
        BigDecimal score = BigDecimal.valueOf(equipmentUtilization * 100);
        
        return score.min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
    }

    /**
     * 计算收入结构得分
     */
    private BigDecimal calculateRevenueStructureScore(Map<String, Object> baseData) {
        Integer medicalServiceRevenue = (Integer) baseData.get("medicalServiceRevenue");
        Integer totalRevenue = (Integer) baseData.get("totalRevenue");
        
        // 医疗服务收入占比
        BigDecimal serviceRevenueRatio = BigDecimal.valueOf(medicalServiceRevenue)
                .divide(BigDecimal.valueOf(totalRevenue), 4, RoundingMode.HALF_UP);
        
        // 转换为得分 (假设医疗服务收入占比70%为满分100分)
        BigDecimal score = serviceRevenueRatio.divide(BigDecimal.valueOf(0.7), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return score.min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
    }

    /**
     * 确定评分等级
     */
    private String determineScoreLevel(BigDecimal totalScore) {
        if (totalScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "优秀";
        } else if (totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "良好";
        } else if (totalScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "一般";
        } else {
            return "较差";
        }
    }

    /**
     * 生成评估结果描述
     */
    private String generateEvaluationResult(BigDecimal totalScore, BigDecimal marketShareScore, 
                                          BigDecimal hrEfficiencyScore, BigDecimal equipmentEfficiencyScore, 
                                          BigDecimal revenueStructureScore) {
        StringBuilder result = new StringBuilder();
        result.append("医院运营综合得分为").append(totalScore).append("分，");
        
        String level = determineScoreLevel(totalScore);
        result.append("评级为").append(level).append("。");
        
        // 分析各维度表现
        result.append("其中：");
        result.append("市场占有率得分").append(marketShareScore).append("分，");
        result.append("人力资源效率得分").append(hrEfficiencyScore).append("分，");
        result.append("设备效率得分").append(equipmentEfficiencyScore).append("分，");
        result.append("收入结构得分").append(revenueStructureScore).append("分。");
        
        return result.toString();
    }

    /**
     * 创建维度分析
     */
    private Map<String, Object> createDimensionAnalysis(String dimension, BigDecimal score, String description) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("dimension", dimension);
        analysis.put("score", score);
        analysis.put("description", description);
        analysis.put("level", score.compareTo(BigDecimal.valueOf(80)) >= 0 ? "优秀" : 
                     score.compareTo(BigDecimal.valueOf(70)) >= 0 ? "良好" : 
                     score.compareTo(BigDecimal.valueOf(60)) >= 0 ? "一般" : "较差");
        return analysis;
    }

    /**
     * 创建改进建议
     */
    private Map<String, Object> createSuggestion(String dimension, String suggestion) {
        Map<String, Object> suggestionMap = new HashMap<>();
        suggestionMap.put("dimension", dimension);
        suggestionMap.put("suggestion", suggestion);
        suggestionMap.put("priority", "高");
        return suggestionMap;
    }
}
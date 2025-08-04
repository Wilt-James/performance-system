package com.hospital.performance.service.impl;

import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.service.MultiDimensionStatsService;
import com.hospital.performance.service.PerformanceDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 多口径统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiDimensionStatsServiceImpl implements MultiDimensionStatsService {

    private final PerformanceDataService performanceDataService;

    @Override
    public List<PerformanceData> queryMultiDimensionStats(String period, Integer statisticsType, 
                                                         List<Long> deptIds, List<Long> indicatorIds, List<Long> userIds) {
        log.info("执行多口径统计查询: period={}, statisticsType={}", period, statisticsType);
        
        return performanceDataService.getMultiDimensionStats(period, statisticsType, deptIds, indicatorIds);
    }

    @Override
    public List<Map<String, Object>> traceByItem(String period, Long indicatorId, Long deptId, Long userId) {
        log.info("按项目追溯查询: period={}, indicatorId={}, deptId={}, userId={}", period, indicatorId, deptId, userId);
        
        List<Map<String, Object>> traceResults = new ArrayList<>();
        
        // 模拟追溯数据
        Map<String, Object> trace1 = new HashMap<>();
        trace1.put("traceId", "T001");
        trace1.put("patientName", "张三");
        trace1.put("patientId", "P001");
        trace1.put("itemCode", "CT001");
        trace1.put("itemName", "CT检查");
        trace1.put("doctorName", "李医生");
        trace1.put("deptName", "放射科");
        trace1.put("amount", new BigDecimal("300.00"));
        trace1.put("executeTime", "2024-01-15 10:30:00");
        trace1.put("statisticsType", 1);
        traceResults.add(trace1);
        
        Map<String, Object> trace2 = new HashMap<>();
        trace2.put("traceId", "T002");
        trace2.put("patientName", "李四");
        trace2.put("patientId", "P002");
        trace2.put("itemCode", "MRI001");
        trace2.put("itemName", "MRI检查");
        trace2.put("doctorName", "王医生");
        trace2.put("deptName", "放射科");
        trace2.put("amount", new BigDecimal("800.00"));
        trace2.put("executeTime", "2024-01-15 14:20:00");
        trace2.put("statisticsType", 1);
        traceResults.add(trace2);
        
        return traceResults;
    }

    @Override
    public List<Map<String, Object>> traceByDoctor(String period, Long doctorId, Long indicatorId) {
        log.info("按医生追溯查询: period={}, doctorId={}, indicatorId={}", period, doctorId, indicatorId);
        
        List<Map<String, Object>> traceResults = new ArrayList<>();
        
        // 模拟医生追溯数据
        Map<String, Object> trace1 = new HashMap<>();
        trace1.put("traceId", "D001");
        trace1.put("doctorName", "张医生");
        trace1.put("doctorId", doctorId);
        trace1.put("patientCount", 45);
        trace1.put("totalAmount", new BigDecimal("15000.00"));
        trace1.put("avgAmount", new BigDecimal("333.33"));
        trace1.put("deptName", "心血管内科");
        trace1.put("workDate", "2024-01-15");
        traceResults.add(trace1);
        
        return traceResults;
    }

    @Override
    public Map<String, Object> compareStatisticsTypes(String period, Long indicatorId, List<Integer> statisticsTypes) {
        log.info("统计口径对比: period={}, indicatorId={}, statisticsTypes={}", period, indicatorId, statisticsTypes);
        
        Map<String, Object> comparison = new HashMap<>();
        
        List<Map<String, Object>> comparisonData = new ArrayList<>();
        
        for (Integer statisticsType : statisticsTypes) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("statisticsType", statisticsType);
            typeData.put("typeName", getStatisticsTypeName(statisticsType));
            
            // 模拟不同口径的数据
            switch (statisticsType) {
                case 1:
                    typeData.put("totalValue", new BigDecimal("50000.00"));
                    typeData.put("count", 150);
                    break;
                case 2:
                    typeData.put("totalValue", new BigDecimal("48000.00"));
                    typeData.put("count", 145);
                    break;
                case 3:
                    typeData.put("totalValue", new BigDecimal("52000.00"));
                    typeData.put("count", 155);
                    break;
                default:
                    typeData.put("totalValue", new BigDecimal("50000.00"));
                    typeData.put("count", 150);
            }
            
            comparisonData.add(typeData);
        }
        
        comparison.put("period", period);
        comparison.put("indicatorId", indicatorId);
        comparison.put("comparisonData", comparisonData);
        
        return comparison;
    }

    @Override
    public List<Map<String, Object>> getDeptPerformanceDistribution(String period, Integer statisticsType, Integer deptType) {
        log.info("获取部门绩效分布: period={}, statisticsType={}, deptType={}", period, statisticsType, deptType);
        
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        // 模拟部门绩效分布数据
        String[] deptNames = {"心血管内科", "心血管外科", "普通外科", "骨科", "医学检验科", "医学影像科"};
        BigDecimal[] amounts = {
            new BigDecimal("25000.00"), new BigDecimal("22000.00"), new BigDecimal("28000.00"),
            new BigDecimal("20000.00"), new BigDecimal("15000.00"), new BigDecimal("18000.00")
        };
        
        for (int i = 0; i < deptNames.length; i++) {
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("deptId", (long) (i + 3));
            deptData.put("deptName", deptNames[i]);
            deptData.put("performanceAmount", amounts[i]);
            deptData.put("percentage", amounts[i].divide(new BigDecimal("128000.00"), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
            deptData.put("rank", i + 1);
            distribution.add(deptData);
        }
        
        return distribution;
    }

    @Override
    public Map<String, Object> getPerformanceTrend(Long targetId, String targetType, String startPeriod, 
                                                  String endPeriod, List<Long> indicatorIds) {
        log.info("获取绩效趋势: targetId={}, targetType={}, startPeriod={}, endPeriod={}", 
                targetId, targetType, startPeriod, endPeriod);
        
        List<PerformanceData> trendData = performanceDataService.getPerformanceTrend(targetId, targetType, startPeriod, endPeriod);
        
        Map<String, Object> trend = new HashMap<>();
        
        // 按期间分组数据
        Map<String, List<PerformanceData>> periodData = new HashMap<>();
        for (PerformanceData data : trendData) {
            periodData.computeIfAbsent(data.getDataPeriod(), k -> new ArrayList<>()).add(data);
        }
        
        List<String> periods = new ArrayList<>(periodData.keySet());
        Collections.sort(periods);
        
        List<Map<String, Object>> trendSeries = new ArrayList<>();
        
        // 为每个指标创建趋势序列
        Set<String> indicatorCodes = new HashSet<>();
        for (PerformanceData data : trendData) {
            indicatorCodes.add(data.getIndicatorCode());
        }
        
        for (String indicatorCode : indicatorCodes) {
            Map<String, Object> series = new HashMap<>();
            series.put("indicatorCode", indicatorCode);
            
            List<BigDecimal> values = new ArrayList<>();
            for (String period : periods) {
                List<PerformanceData> periodDataList = periodData.get(period);
                BigDecimal value = periodDataList.stream()
                        .filter(d -> indicatorCode.equals(d.getIndicatorCode()))
                        .map(PerformanceData::getPerformanceAmount)
                        .findFirst()
                        .orElse(BigDecimal.ZERO);
                values.add(value);
            }
            
            series.put("values", values);
            trendSeries.add(series);
        }
        
        trend.put("periods", periods);
        trend.put("trendSeries", trendSeries);
        trend.put("targetId", targetId);
        trend.put("targetType", targetType);
        
        return trend;
    }

    @Override
    public String exportStatsData(String period, Integer statisticsType, List<Long> deptIds, 
                                 List<Long> indicatorIds, List<Long> userIds) {
        log.info("导出统计数据: period={}, statisticsType={}", period, statisticsType);
        
        // 这里应该实现实际的导出逻辑
        // 为了演示，返回模拟的文件路径
        String fileName = String.format("multi_dimension_stats_%s_%d_%d.xlsx", 
                period, statisticsType, System.currentTimeMillis());
        
        return "/exports/" + fileName;
    }

    @Override
    public Map<Integer, String> getStatisticsTypeDescriptions() {
        Map<Integer, String> descriptions = new HashMap<>();
        descriptions.put(1, "开单医生所在科：按开单医生所属科室进行统计");
        descriptions.put(2, "执行医生所在科：按执行医生所属科室进行统计");
        descriptions.put(3, "开单科室对应护理单元：按开单科室对应的护理单元进行统计");
        descriptions.put(4, "患者所在科室：按患者住院或就诊科室进行统计");
        descriptions.put(5, "收费科室：按实际收费科室进行统计");
        return descriptions;
    }

    /**
     * 获取统计口径名称
     */
    private String getStatisticsTypeName(Integer statisticsType) {
        Map<Integer, String> typeNames = new HashMap<>();
        typeNames.put(1, "开单医生所在科");
        typeNames.put(2, "执行医生所在科");
        typeNames.put(3, "开单科室对应护理单元");
        typeNames.put(4, "患者所在科室");
        typeNames.put(5, "收费科室");
        return typeNames.getOrDefault(statisticsType, "未知口径");
    }
}
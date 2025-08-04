package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.PerformanceCalculationRecord;

import java.util.List;
import java.util.Map;

/**
 * 绩效计算记录服务接口
 */
public interface PerformanceCalculationRecordService extends IService<PerformanceCalculationRecord> {

    /**
     * 获取计算历史记录
     */
    List<Map<String, Object>> getCalculationHistory(String period, Long schemeId);

    /**
     * 根据期间和方案查询计算记录
     */
    List<PerformanceCalculationRecord> getByPeriodAndScheme(String period, Long schemeId);

    /**
     * 获取最新的计算记录
     */
    PerformanceCalculationRecord getLatestRecord(String period, Long schemeId, Integer calculationType);

    /**
     * 统计计算记录
     */
    Map<String, Object> getCalculationStatistics(String period);
}
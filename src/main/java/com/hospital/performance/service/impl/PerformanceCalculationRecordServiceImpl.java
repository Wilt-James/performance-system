package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.PerformanceCalculationRecord;
import com.hospital.performance.mapper.PerformanceCalculationRecordMapper;
import com.hospital.performance.service.PerformanceCalculationRecordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绩效计算记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class PerformanceCalculationRecordServiceImpl extends ServiceImpl<PerformanceCalculationRecordMapper, PerformanceCalculationRecord> 
        implements PerformanceCalculationRecordService {

    @Override
    public List<Map<String, Object>> getCalculationHistory(String period, Long schemeId) {
        LambdaQueryWrapper<PerformanceCalculationRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(period)) {
            wrapper.eq(PerformanceCalculationRecord::getCalculationPeriod, period);
        }
        
        if (schemeId != null) {
            wrapper.eq(PerformanceCalculationRecord::getSchemeId, schemeId);
        }
        
        wrapper.orderByDesc(PerformanceCalculationRecord::getCreateTime);
        
        List<PerformanceCalculationRecord> records = this.list(wrapper);
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (PerformanceCalculationRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("calculationPeriod", record.getCalculationPeriod());
            item.put("schemeId", record.getSchemeId());
            item.put("schemeName", record.getSchemeName());
            item.put("calculationType", record.getCalculationType());
            item.put("calculationStatus", record.getCalculationStatus());
            item.put("totalAmount", record.getTotalAmount());
            item.put("deptCount", record.getDeptCount());
            item.put("userCount", record.getUserCount());
            item.put("startTime", record.getStartTime());
            item.put("endTime", record.getEndTime());
            item.put("errorMessage", record.getErrorMessage());
            item.put("createTime", record.getCreateTime());
            history.add(item);
        }
        
        return history;
    }

    @Override
    public List<PerformanceCalculationRecord> getByPeriodAndScheme(String period, Long schemeId) {
        LambdaQueryWrapper<PerformanceCalculationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceCalculationRecord::getCalculationPeriod, period)
                .eq(PerformanceCalculationRecord::getSchemeId, schemeId)
                .orderByDesc(PerformanceCalculationRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public PerformanceCalculationRecord getLatestRecord(String period, Long schemeId, Integer calculationType) {
        LambdaQueryWrapper<PerformanceCalculationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceCalculationRecord::getCalculationPeriod, period)
                .eq(PerformanceCalculationRecord::getSchemeId, schemeId)
                .eq(PerformanceCalculationRecord::getCalculationType, calculationType)
                .eq(PerformanceCalculationRecord::getCalculationStatus, 2) // 计算完成
                .orderByDesc(PerformanceCalculationRecord::getCreateTime)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public Map<String, Object> getCalculationStatistics(String period) {
        LambdaQueryWrapper<PerformanceCalculationRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(period)) {
            wrapper.eq(PerformanceCalculationRecord::getCalculationPeriod, period);
        }
        
        List<PerformanceCalculationRecord> records = this.list(wrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态的记录数
        long totalCount = records.size();
        long completedCount = records.stream().filter(r -> r.getCalculationStatus() == 2).count();
        long failedCount = records.stream().filter(r -> r.getCalculationStatus() == 3).count();
        long runningCount = records.stream().filter(r -> r.getCalculationStatus() == 1).count();
        
        statistics.put("totalCount", totalCount);
        statistics.put("completedCount", completedCount);
        statistics.put("failedCount", failedCount);
        statistics.put("runningCount", runningCount);
        
        // 计算成功率
        double successRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0;
        statistics.put("successRate", Math.round(successRate * 100.0) / 100.0);
        
        return statistics;
    }
}
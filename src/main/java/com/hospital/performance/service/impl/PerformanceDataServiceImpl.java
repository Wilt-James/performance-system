package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.mapper.PerformanceDataMapper;
import com.hospital.performance.service.PerformanceDataService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 绩效数据服务实现类
 */
@Service
@RequiredArgsConstructor
public class PerformanceDataServiceImpl extends ServiceImpl<PerformanceDataMapper, PerformanceData> 
        implements PerformanceDataService {

    @Override
    public List<PerformanceData> getByCalculationRecord(Long calculationRecordId) {
        // 这里需要根据实际的关联关系查询
        // 为了演示，使用简单的查询方式
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceData::getStatus, 2) // 已确认状态
                .orderByDesc(PerformanceData::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<PerformanceData> getByPeriodAndDept(String period, Long deptId) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceData::getDataPeriod, period)
                .eq(PerformanceData::getDeptId, deptId)
                .orderByAsc(PerformanceData::getIndicatorCode);
        return this.list(wrapper);
    }

    @Override
    public List<PerformanceData> getByPeriodAndUser(String period, Long userId) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceData::getDataPeriod, period)
                .eq(PerformanceData::getUserId, userId)
                .orderByAsc(PerformanceData::getIndicatorCode);
        return this.list(wrapper);
    }

    @Override
    public Page<PerformanceData> pagePerformanceData(Page<PerformanceData> page, String period, 
                                                    Long deptId, Long userId, Integer status) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(period)) {
            wrapper.eq(PerformanceData::getDataPeriod, period);
        }
        
        if (deptId != null) {
            wrapper.eq(PerformanceData::getDeptId, deptId);
        }
        
        if (userId != null) {
            wrapper.eq(PerformanceData::getUserId, userId);
        }
        
        if (status != null) {
            wrapper.eq(PerformanceData::getStatus, status);
        }
        
        wrapper.orderByDesc(PerformanceData::getDataPeriod)
                .orderByAsc(PerformanceData::getDeptName)
                .orderByAsc(PerformanceData::getIndicatorCode);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCalculationRecord(Long calculationRecordId) {
        // 这里需要根据实际的关联关系删除
        // 为了演示，使用简单的删除方式
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceData::getStatus, 1); // 删除草稿状态的数据
        return this.remove(wrapper);
    }

    @Override
    public List<PerformanceData> getMultiDimensionStats(String period, Integer statisticsType, 
                                                       List<Long> deptIds, List<Long> indicatorIds) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(PerformanceData::getDataPeriod, period);
        
        if (statisticsType != null) {
            wrapper.eq(PerformanceData::getStatisticsType, statisticsType);
        }
        
        if (deptIds != null && !deptIds.isEmpty()) {
            wrapper.in(PerformanceData::getDeptId, deptIds);
        }
        
        if (indicatorIds != null && !indicatorIds.isEmpty()) {
            wrapper.in(PerformanceData::getIndicatorId, indicatorIds);
        }
        
        wrapper.orderByAsc(PerformanceData::getDeptName)
                .orderByAsc(PerformanceData::getIndicatorCode);
        
        return this.list(wrapper);
    }

    @Override
    public List<PerformanceData> getPerformanceTrend(Long targetId, String targetType, 
                                                    String startPeriod, String endPeriod) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.ge(PerformanceData::getDataPeriod, startPeriod)
                .le(PerformanceData::getDataPeriod, endPeriod);
        
        if ("dept".equals(targetType)) {
            wrapper.eq(PerformanceData::getDeptId, targetId)
                    .isNull(PerformanceData::getUserId);
        } else if ("user".equals(targetType)) {
            wrapper.eq(PerformanceData::getUserId, targetId);
        }
        
        wrapper.orderByAsc(PerformanceData::getDataPeriod)
                .orderByAsc(PerformanceData::getIndicatorCode);
        
        return this.list(wrapper);
    }

    @Override
    public List<PerformanceData> exportPerformanceData(String period, List<Long> deptIds, 
                                                      List<Long> userIds, List<Long> indicatorIds) {
        LambdaQueryWrapper<PerformanceData> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(period)) {
            wrapper.eq(PerformanceData::getDataPeriod, period);
        }
        
        if (deptIds != null && !deptIds.isEmpty()) {
            wrapper.in(PerformanceData::getDeptId, deptIds);
        }
        
        if (userIds != null && !userIds.isEmpty()) {
            wrapper.in(PerformanceData::getUserId, userIds);
        }
        
        if (indicatorIds != null && !indicatorIds.isEmpty()) {
            wrapper.in(PerformanceData::getIndicatorId, indicatorIds);
        }
        
        wrapper.eq(PerformanceData::getStatus, 3) // 只导出已发布的数据
                .orderByAsc(PerformanceData::getDataPeriod)
                .orderByAsc(PerformanceData::getDeptName)
                .orderByAsc(PerformanceData::getUserName)
                .orderByAsc(PerformanceData::getIndicatorCode);
        
        return this.list(wrapper);
    }
}
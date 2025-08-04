package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.PerformanceData;

import java.util.List;

/**
 * 绩效数据服务接口
 */
public interface PerformanceDataService extends IService<PerformanceData> {

    /**
     * 根据计算记录ID查询绩效数据
     */
    List<PerformanceData> getByCalculationRecord(Long calculationRecordId);

    /**
     * 根据期间和部门查询绩效数据
     */
    List<PerformanceData> getByPeriodAndDept(String period, Long deptId);

    /**
     * 根据期间和用户查询绩效数据
     */
    List<PerformanceData> getByPeriodAndUser(String period, Long userId);

    /**
     * 分页查询绩效数据
     */
    Page<PerformanceData> pagePerformanceData(Page<PerformanceData> page, String period, 
                                            Long deptId, Long userId, Integer status);

    /**
     * 删除计算记录相关的绩效数据
     */
    boolean deleteByCalculationRecord(Long calculationRecordId);

    /**
     * 多口径统计查询
     */
    List<PerformanceData> getMultiDimensionStats(String period, Integer statisticsType, 
                                                List<Long> deptIds, List<Long> indicatorIds);

    /**
     * 获取绩效趋势数据
     */
    List<PerformanceData> getPerformanceTrend(Long targetId, String targetType, 
                                            String startPeriod, String endPeriod);

    /**
     * 导出绩效数据
     */
    List<PerformanceData> exportPerformanceData(String period, List<Long> deptIds, 
                                               List<Long> userIds, List<Long> indicatorIds);
}
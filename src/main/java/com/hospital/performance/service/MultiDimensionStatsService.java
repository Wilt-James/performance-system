package com.hospital.performance.service;

import com.hospital.performance.entity.PerformanceData;

import java.util.List;
import java.util.Map;

/**
 * 多口径统计服务接口
 */
public interface MultiDimensionStatsService {

    /**
     * 多口径统计查询
     * 
     * @param period 统计期间
     * @param statisticsType 统计口径类型
     * @param deptIds 部门ID列表
     * @param indicatorIds 指标ID列表
     * @param userIds 用户ID列表
     * @return 统计结果
     */
    List<PerformanceData> queryMultiDimensionStats(String period, Integer statisticsType, 
                                                  List<Long> deptIds, List<Long> indicatorIds, List<Long> userIds);

    /**
     * 按项目追溯查询
     * 
     * @param period 统计期间
     * @param indicatorId 指标ID
     * @param deptId 部门ID
     * @param userId 用户ID
     * @return 追溯结果
     */
    List<Map<String, Object>> traceByItem(String period, Long indicatorId, Long deptId, Long userId);

    /**
     * 按医生追溯查询
     * 
     * @param period 统计期间
     * @param doctorId 医生ID
     * @param indicatorId 指标ID
     * @return 追溯结果
     */
    List<Map<String, Object>> traceByDoctor(String period, Long doctorId, Long indicatorId);

    /**
     * 统计口径对比
     * 
     * @param period 统计期间
     * @param indicatorId 指标ID
     * @param statisticsTypes 统计口径类型列表
     * @return 对比结果
     */
    Map<String, Object> compareStatisticsTypes(String period, Long indicatorId, List<Integer> statisticsTypes);

    /**
     * 获取部门绩效分布
     * 
     * @param period 统计期间
     * @param statisticsType 统计口径类型
     * @param deptType 部门类型
     * @return 分布结果
     */
    List<Map<String, Object>> getDeptPerformanceDistribution(String period, Integer statisticsType, Integer deptType);

    /**
     * 获取绩效趋势分析
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型 (dept/user)
     * @param startPeriod 开始期间
     * @param endPeriod 结束期间
     * @param indicatorIds 指标ID列表
     * @return 趋势分析结果
     */
    Map<String, Object> getPerformanceTrend(Long targetId, String targetType, String startPeriod, 
                                           String endPeriod, List<Long> indicatorIds);

    /**
     * 导出统计数据
     * 
     * @param period 统计期间
     * @param statisticsType 统计口径类型
     * @param deptIds 部门ID列表
     * @param indicatorIds 指标ID列表
     * @param userIds 用户ID列表
     * @return 导出文件路径
     */
    String exportStatsData(String period, Integer statisticsType, List<Long> deptIds, 
                          List<Long> indicatorIds, List<Long> userIds);

    /**
     * 获取统计口径说明
     * 
     * @return 统计口径说明
     */
    Map<Integer, String> getStatisticsTypeDescriptions();
}
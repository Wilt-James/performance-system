package com.hospital.performance.controller;

import com.hospital.performance.common.Result;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.service.MultiDimensionStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 多口径统计控制器
 */
@RestController
@RequestMapping("/api/statistics/multi-dimension")
@RequiredArgsConstructor
public class MultiDimensionStatsController {

    private final MultiDimensionStatsService statsService;

    /**
     * 多口径统计查询
     */
    @PostMapping("/query")
    public Result<List<PerformanceData>> queryMultiDimensionStats(@RequestBody StatsQueryRequest request) {
        try {
            List<PerformanceData> result = statsService.queryMultiDimensionStats(
                    request.getPeriod(),
                    request.getStatisticsType(),
                    request.getDeptIds(),
                    request.getIndicatorIds(),
                    request.getUserIds()
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取多维度统计数据 (GET方式)
     */
    @GetMapping("/data")
    public Result<List<PerformanceData>> getMultiDimensionData(
            @RequestParam String period,
            @RequestParam Integer statisticsType,
            @RequestParam(required = false) List<Long> deptIds,
            @RequestParam(required = false) List<Long> indicatorIds) {
        try {
            List<PerformanceData> result = statsService.queryMultiDimensionStats(
                    period,
                    statisticsType,
                    deptIds,
                    indicatorIds,
                    null
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按项目追溯查询
     */
    @GetMapping("/trace-by-item")
    public Result<List<Map<String, Object>>> traceByItem(
            @RequestParam String period,
            @RequestParam Long indicatorId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long userId) {
        try {
            List<Map<String, Object>> result = statsService.traceByItem(period, indicatorId, deptId, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按医生追溯查询
     */
    @GetMapping("/trace-by-doctor")
    public Result<List<Map<String, Object>>> traceByDoctor(
            @RequestParam String period,
            @RequestParam Long doctorId,
            @RequestParam(required = false) Long indicatorId) {
        try {
            List<Map<String, Object>> result = statsService.traceByDoctor(period, doctorId, indicatorId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取统计口径对比
     */
    @GetMapping("/compare-statistics-types")
    public Result<Map<String, Object>> compareStatisticsTypes(
            @RequestParam String period,
            @RequestParam Long indicatorId,
            @RequestParam List<Integer> statisticsTypes) {
        try {
            Map<String, Object> result = statsService.compareStatisticsTypes(period, indicatorId, statisticsTypes);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取部门绩效分布
     */
    @GetMapping("/dept-performance-distribution")
    public Result<List<Map<String, Object>>> getDeptPerformanceDistribution(
            @RequestParam String period,
            @RequestParam Integer statisticsType,
            @RequestParam(required = false) Integer deptType) {
        try {
            List<Map<String, Object>> result = statsService.getDeptPerformanceDistribution(period, statisticsType, deptType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取绩效趋势分析
     */
    @GetMapping("/performance-trend")
    public Result<Map<String, Object>> getPerformanceTrend(
            @RequestParam Long targetId,
            @RequestParam String targetType,
            @RequestParam String startPeriod,
            @RequestParam String endPeriod,
            @RequestParam(required = false) List<Long> indicatorIds) {
        try {
            Map<String, Object> result = statsService.getPerformanceTrend(targetId, targetType, startPeriod, endPeriod, indicatorIds);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出统计数据
     */
    @PostMapping("/export")
    public Result<String> exportStatsData(@RequestBody StatsQueryRequest request) {
        try {
            String filePath = statsService.exportStatsData(
                    request.getPeriod(),
                    request.getStatisticsType(),
                    request.getDeptIds(),
                    request.getIndicatorIds(),
                    request.getUserIds()
            );
            return Result.success("导出成功", filePath);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计查询请求对象
     */
    public static class StatsQueryRequest {
        private String period;
        private Integer statisticsType;
        private List<Long> deptIds;
        private List<Long> indicatorIds;
        private List<Long> userIds;

        // Getters and Setters
        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public Integer getStatisticsType() {
            return statisticsType;
        }

        public void setStatisticsType(Integer statisticsType) {
            this.statisticsType = statisticsType;
        }

        public List<Long> getDeptIds() {
            return deptIds;
        }

        public void setDeptIds(List<Long> deptIds) {
            this.deptIds = deptIds;
        }

        public List<Long> getIndicatorIds() {
            return indicatorIds;
        }

        public void setIndicatorIds(List<Long> indicatorIds) {
            this.indicatorIds = indicatorIds;
        }

        public List<Long> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<Long> userIds) {
            this.userIds = userIds;
        }
    }
}
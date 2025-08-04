package com.hospital.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.performance.common.PageResult;
import com.hospital.performance.common.Result;
import com.hospital.performance.entity.HospitalOperationScore;
import com.hospital.performance.service.HospitalOperationScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医院运营评分控制器
 */
@RestController
@RequestMapping("/api/statistics/operation-score")
@RequiredArgsConstructor
public class HospitalOperationScoreController {

    private final HospitalOperationScoreService operationScoreService;

    /**
     * 计算医院运营评分
     */
    @PostMapping("/calculate")
    public Result<HospitalOperationScore> calculateOperationScore(@RequestParam String period) {
        try {
            HospitalOperationScore score = operationScoreService.calculateOperationScore(period);
            return Result.success("评分计算完成", score);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询评分记录
     */
    @GetMapping("/page")
    public Result<PageResult<HospitalOperationScore>> pageScores(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String startPeriod,
            @RequestParam(required = false) String endPeriod) {
        
        Page<HospitalOperationScore> page = new Page<>(current, size);
        Page<HospitalOperationScore> result = operationScoreService.pageScores(page, startPeriod, endPeriod);
        
        PageResult<HospitalOperationScore> pageResult = PageResult.build(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 获取评分历史
     */
    @GetMapping("/history")
    public Result<List<HospitalOperationScore>> getScoreHistory(
            @RequestParam(required = false) String startPeriod,
            @RequestParam(required = false) String endPeriod) {
        try {
            List<HospitalOperationScore> history = operationScoreService.getScoreHistory(startPeriod, endPeriod);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取运营评分数据
     */
    @GetMapping("/data")
    public Result<List<HospitalOperationScore>> getOperationScoreData(
            @RequestParam(required = false) String period) {
        try {
            List<HospitalOperationScore> data = operationScoreService.getScoreData(period);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评分趋势数据
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getScoreTrend(
            @RequestParam String startPeriod,
            @RequestParam String endPeriod) {
        try {
            Map<String, Object> trendData = operationScoreService.getScoreTrend(startPeriod, endPeriod);
            return Result.success(trendData);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评分详细分析
     */
    @GetMapping("/analysis/{period}")
    public Result<Map<String, Object>> getScoreAnalysis(@PathVariable String period) {
        try {
            Map<String, Object> analysis = operationScoreService.getScoreAnalysis(period);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评分对比分析
     */
    @GetMapping("/comparison")
    public Result<Map<String, Object>> getScoreComparison(
            @RequestParam String currentPeriod,
            @RequestParam String comparePeriod) {
        try {
            Map<String, Object> comparison = operationScoreService.getScoreComparison(currentPeriod, comparePeriod);
            return Result.success(comparison);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取改进建议
     */
    @GetMapping("/suggestions/{period}")
    public Result<List<Map<String, Object>>> getImprovementSuggestions(@PathVariable String period) {
        try {
            List<Map<String, Object>> suggestions = operationScoreService.getImprovementSuggestions(period);
            return Result.success(suggestions);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出评分报告
     */
    @GetMapping("/export/{period}")
    public Result<String> exportScoreReport(@PathVariable String period) {
        try {
            String filePath = operationScoreService.exportScoreReport(period);
            return Result.success("报告导出成功", filePath);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重新计算评分
     */
    @PostMapping("/recalculate")
    public Result<String> recalculateScore(@RequestParam String period) {
        try {
            boolean success = operationScoreService.recalculateScore(period);
            return success ? Result.success("重新计算成功") : Result.error("重新计算失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
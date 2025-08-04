package com.hospital.performance.controller;

import com.hospital.performance.common.Result;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.service.PerformanceCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 绩效计算控制器
 */
@RestController
@RequestMapping("/api/performance/calculation")
@RequiredArgsConstructor
public class PerformanceCalculationController {

    private final PerformanceCalculationService calculationService;

    /**
     * 执行绩效计算
     */
    @PostMapping("/execute")
    public Result<Long> executeCalculation(@RequestBody CalculationRequest request) {
        try {
            Long recordId = calculationService.executeCalculation(
                    request.getSchemeId(),
                    request.getPeriod(),
                    request.getDeptIds(),
                    request.getCalculationType()
            );
            return Result.success("计算任务已启动", recordId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取计算结果
     */
    @GetMapping("/result/{calculationRecordId}")
    public Result<List<PerformanceData>> getCalculationResult(@PathVariable Long calculationRecordId) {
        try {
            List<PerformanceData> result = calculationService.getCalculationResult(calculationRecordId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取计算步骤详情
     */
    @GetMapping("/steps")
    public Result<Map<String, Object>> getCalculationSteps(
            @RequestParam Long schemeId,
            @RequestParam Long targetId,
            @RequestParam String period) {
        try {
            Map<String, Object> steps = calculationService.getCalculationSteps(schemeId, targetId, period);
            return Result.success(steps);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重新计算
     */
    @PostMapping("/recalculate/{calculationRecordId}")
    public Result<String> recalculate(@PathVariable Long calculationRecordId) {
        try {
            boolean success = calculationService.recalculate(calculationRecordId);
            return success ? Result.success("重新计算成功") : Result.error("重新计算失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布计算结果
     */
    @PostMapping("/publish/{calculationRecordId}")
    public Result<String> publishCalculationResult(@PathVariable Long calculationRecordId) {
        try {
            boolean success = calculationService.publishCalculationResult(calculationRecordId);
            return success ? Result.success("发布成功") : Result.error("发布失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取计算历史记录
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getCalculationHistory(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long schemeId) {
        try {
            List<Map<String, Object>> history = calculationService.getCalculationHistory(period, schemeId);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除计算记录
     */
    @DeleteMapping("/{calculationRecordId}")
    public Result<String> deleteCalculationRecord(@PathVariable Long calculationRecordId) {
        try {
            boolean success = calculationService.deleteCalculationRecord(calculationRecordId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 计算请求对象
     */
    public static class CalculationRequest {
        private Long schemeId;
        private String period;
        private List<Long> deptIds;
        private Integer calculationType;

        // Getters and Setters
        public Long getSchemeId() {
            return schemeId;
        }

        public void setSchemeId(Long schemeId) {
            this.schemeId = schemeId;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public List<Long> getDeptIds() {
            return deptIds;
        }

        public void setDeptIds(List<Long> deptIds) {
            this.deptIds = deptIds;
        }

        public Integer getCalculationType() {
            return calculationType;
        }

        public void setCalculationType(Integer calculationType) {
            this.calculationType = calculationType;
        }
    }
}
package com.hospital.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.performance.common.PageResult;
import com.hospital.performance.common.Result;
import com.hospital.performance.entity.PerformanceIndicator;
import com.hospital.performance.service.PerformanceIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绩效指标管理控制器
 */
@RestController
@RequestMapping("/api/performance/indicator")
@RequiredArgsConstructor
public class PerformanceIndicatorController {

    private final PerformanceIndicatorService indicatorService;

    /**
     * 分页查询指标列表
     */
    @GetMapping("/page")
    public Result<PageResult<PerformanceIndicator>> pageIndicators(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer indicatorType,
            @RequestParam(required = false) Integer indicatorCategory,
            @RequestParam(required = false) Integer status) {
        
        Page<PerformanceIndicator> page = new Page<>(current, size);
        Page<PerformanceIndicator> result = indicatorService.pageIndicators(page, keyword, indicatorType, indicatorCategory, status);
        
        PageResult<PerformanceIndicator> pageResult = PageResult.build(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 获取指标列表（不分页）
     */
    @GetMapping("/list")
    public Result<List<PerformanceIndicator>> getIndicatorList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer indicatorType,
            @RequestParam(required = false) Integer indicatorCategory,
            @RequestParam(required = false) Integer status) {
        
        List<PerformanceIndicator> indicators = indicatorService.getIndicatorList(keyword, indicatorType, indicatorCategory, status);
        return Result.success(indicators);
    }

    /**
     * 根据ID查询指标详情
     */
    @GetMapping("/{id}")
    public Result<PerformanceIndicator> getIndicatorById(@PathVariable Long id) {
        PerformanceIndicator indicator = indicatorService.getById(id);
        if (indicator == null) {
            return Result.notFound("指标不存在");
        }
        return Result.success(indicator);
    }

    /**
     * 根据指标类型查询指标列表
     */
    @GetMapping("/type/{indicatorType}")
    public Result<List<PerformanceIndicator>> getIndicatorsByType(@PathVariable Integer indicatorType) {
        List<PerformanceIndicator> indicators = indicatorService.getByIndicatorType(indicatorType);
        return Result.success(indicators);
    }

    /**
     * 根据指标分类查询指标列表
     */
    @GetMapping("/category/{indicatorCategory}")
    public Result<List<PerformanceIndicator>> getIndicatorsByCategory(@PathVariable Integer indicatorCategory) {
        List<PerformanceIndicator> indicators = indicatorService.getByIndicatorCategory(indicatorCategory);
        return Result.success(indicators);
    }

    /**
     * 根据适用范围查询指标列表
     */
    @GetMapping("/scope/{applicableScope}")
    public Result<List<PerformanceIndicator>> getIndicatorsByScope(@PathVariable Integer applicableScope) {
        List<PerformanceIndicator> indicators = indicatorService.getByApplicableScope(applicableScope);
        return Result.success(indicators);
    }

    /**
     * 创建指标
     */
    @PostMapping
    public Result<String> createIndicator(@Validated @RequestBody PerformanceIndicator indicator) {
        try {
            boolean success = indicatorService.createIndicator(indicator);
            return success ? Result.success("指标创建成功") : Result.error("指标创建失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新指标
     */
    @PutMapping("/{id}")
    public Result<String> updateIndicator(@PathVariable Long id, @Validated @RequestBody PerformanceIndicator indicator) {
        try {
            indicator.setId(id);
            boolean success = indicatorService.updateIndicator(indicator);
            return success ? Result.success("指标更新成功") : Result.error("指标更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除指标
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteIndicator(@PathVariable Long id) {
        try {
            boolean success = indicatorService.deleteIndicator(id);
            return success ? Result.success("指标删除成功") : Result.error("指标删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查指标编码是否存在
     */
    @GetMapping("/check-code")
    public Result<Boolean> checkIndicatorCode(
            @RequestParam String indicatorCode,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = indicatorService.existsByIndicatorCode(indicatorCode, excludeId);
        return Result.success(!exists);
    }

    /**
     * 验证指标公式
     */
    @PostMapping("/validate-formula")
    public Result<Boolean> validateFormula(@RequestParam String formula) {
        boolean valid = indicatorService.validateFormula(formula);
        return Result.success(valid);
    }

    /**
     * 启用/禁用指标
     */
    @PostMapping("/{id}/toggle-status")
    public Result<String> toggleIndicatorStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = indicatorService.toggleIndicatorStatus(id, status);
            String message = status == 1 ? "指标启用成功" : "指标禁用成功";
            return success ? Result.success(message) : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量导入指标
     */
    @PostMapping("/batch-import")
    public Result<String> batchImportIndicators(@RequestBody List<PerformanceIndicator> indicators) {
        try {
            boolean success = indicatorService.batchImportIndicators(indicators);
            return success ? Result.success("批量导入成功") : Result.error("批量导入失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
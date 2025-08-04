package com.hospital.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.performance.common.PageResult;
import com.hospital.performance.common.Result;
import com.hospital.performance.entity.PerformanceScheme;
import com.hospital.performance.service.PerformanceSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绩效方案管理控制器
 */
@RestController
@RequestMapping("/api/performance/scheme")
@RequiredArgsConstructor
public class PerformanceSchemeController {

    private final PerformanceSchemeService schemeService;

    /**
     * 分页查询方案列表
     */
    @GetMapping("/page")
    public Result<PageResult<PerformanceScheme>> pageSchemes(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer schemeType,
            @RequestParam(required = false) Integer status) {
        
        Page<PerformanceScheme> page = new Page<>(current, size);
        Page<PerformanceScheme> result = schemeService.pageSchemes(page, keyword, schemeType, status);
        
        PageResult<PerformanceScheme> pageResult = PageResult.build(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 获取方案列表（不分页）
     */
    @GetMapping("/list")
    public Result<List<PerformanceScheme>> getSchemeList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer schemeType,
            @RequestParam(required = false) Integer status) {
        
        List<PerformanceScheme> schemes = schemeService.getSchemeList(keyword, schemeType, status);
        return Result.success(schemes);
    }

    /**
     * 根据ID查询方案详情
     */
    @GetMapping("/{id}")
    public Result<PerformanceScheme> getSchemeById(@PathVariable Long id) {
        PerformanceScheme scheme = schemeService.getById(id);
        if (scheme == null) {
            return Result.notFound("方案不存在");
        }
        return Result.success(scheme);
    }

    /**
     * 根据方案类型查询方案列表
     */
    @GetMapping("/type/{schemeType}")
    public Result<List<PerformanceScheme>> getSchemesByType(@PathVariable Integer schemeType) {
        List<PerformanceScheme> schemes = schemeService.getBySchemeType(schemeType);
        return Result.success(schemes);
    }

    /**
     * 根据适用部门类型查询方案列表
     */
    @GetMapping("/dept-type/{applicableDeptType}")
    public Result<List<PerformanceScheme>> getSchemesByDeptType(@PathVariable Integer applicableDeptType) {
        List<PerformanceScheme> schemes = schemeService.getByApplicableDeptType(applicableDeptType);
        return Result.success(schemes);
    }

    /**
     * 获取默认方案
     */
    @GetMapping("/default/{applicableDeptType}")
    public Result<PerformanceScheme> getDefaultScheme(@PathVariable Integer applicableDeptType) {
        PerformanceScheme scheme = schemeService.getDefaultScheme(applicableDeptType);
        return Result.success(scheme);
    }

    /**
     * 创建方案
     */
    @PostMapping
    public Result<String> createScheme(@Validated @RequestBody PerformanceScheme scheme) {
        try {
            boolean success = schemeService.createScheme(scheme);
            return success ? Result.success("方案创建成功") : Result.error("方案创建失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新方案
     */
    @PutMapping("/{id}")
    public Result<String> updateScheme(@PathVariable Long id, @Validated @RequestBody PerformanceScheme scheme) {
        try {
            scheme.setId(id);
            boolean success = schemeService.updateScheme(scheme);
            return success ? Result.success("方案更新成功") : Result.error("方案更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除方案
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteScheme(@PathVariable Long id) {
        try {
            boolean success = schemeService.deleteScheme(id);
            return success ? Result.success("方案删除成功") : Result.error("方案删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查方案编码是否存在
     */
    @GetMapping("/check-code")
    public Result<Boolean> checkSchemeCode(
            @RequestParam String schemeCode,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = schemeService.existsBySchemeCode(schemeCode, excludeId);
        return Result.success(!exists);
    }

    /**
     * 启用/禁用方案
     */
    @PostMapping("/{id}/toggle-status")
    public Result<String> toggleSchemeStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = schemeService.toggleSchemeStatus(id, status);
            String message = status == 1 ? "方案启用成功" : "方案禁用成功";
            return success ? Result.success(message) : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 设置默认方案
     */
    @PostMapping("/{id}/set-default")
    public Result<String> setDefaultScheme(@PathVariable Long id) {
        try {
            boolean success = schemeService.setDefaultScheme(id);
            return success ? Result.success("设置默认方案成功") : Result.error("设置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
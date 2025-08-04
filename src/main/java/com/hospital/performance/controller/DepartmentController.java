package com.hospital.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.performance.common.PageResult;
import com.hospital.performance.common.Result;
import com.hospital.performance.entity.Department;
import com.hospital.performance.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 查询部门树
     */
    @GetMapping("/tree")
    public Result<List<Department>> getDeptTree(@RequestParam(required = false) Long parentId) {
        List<Department> deptTree = departmentService.getDeptTree(parentId);
        return Result.success(deptTree);
    }

    /**
     * 分页查询部门列表
     */
    @GetMapping("/page")
    public Result<PageResult<Department>> pageDepartments(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer deptType,
            @RequestParam(required = false) Integer status) {
        
        Page<Department> page = new Page<>(current, size);
        Page<Department> result = departmentService.pageDepartments(page, keyword, deptType, status);
        
        PageResult<Department> pageResult = PageResult.build(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询部门详情
     */
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return Result.notFound("部门不存在");
        }
        return Result.success(department);
    }

    /**
     * 根据部门类型查询部门列表
     */
    @GetMapping("/type/{deptType}")
    public Result<List<Department>> getDepartmentsByType(@PathVariable Integer deptType) {
        List<Department> departments = departmentService.getByDeptType(deptType);
        return Result.success(departments);
    }

    /**
     * 创建部门
     */
    @PostMapping
    public Result<String> createDepartment(@Validated @RequestBody Department department) {
        try {
            boolean success = departmentService.createDepartment(department);
            return success ? Result.success("部门创建成功") : Result.error("部门创建失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    public Result<String> updateDepartment(@PathVariable Long id, @Validated @RequestBody Department department) {
        try {
            department.setId(id);
            boolean success = departmentService.updateDepartment(department);
            return success ? Result.success("部门更新成功") : Result.error("部门更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDepartment(@PathVariable Long id) {
        try {
            boolean success = departmentService.deleteDepartment(id);
            return success ? Result.success("部门删除成功") : Result.error("部门删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查部门编码是否存在
     */
    @GetMapping("/check-code")
    public Result<Boolean> checkDeptCode(
            @RequestParam String deptCode,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = departmentService.existsByDeptCode(deptCode, excludeId);
        return Result.success(!exists);
    }

    /**
     * 获取部门路径
     */
    @GetMapping("/{id}/path")
    public Result<String> getDeptPath(@PathVariable Long id) {
        String path = departmentService.getDeptPath(id);
        return Result.success(path);
    }

    /**
     * 查询子部门
     */
    @GetMapping("/{id}/children")
    public Result<List<Department>> getChildren(@PathVariable Long id) {
        List<Department> children = departmentService.getChildrenByParentId(id);
        return Result.success(children);
    }
}
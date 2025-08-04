package com.hospital.performance.controller;

import com.hospital.performance.common.Result;
import com.hospital.performance.entity.Department;
import com.hospital.performance.entity.User;
import com.hospital.performance.service.DepartmentService;
import com.hospital.performance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final DepartmentService departmentService;
    private final UserService userService;

    /**
     * 获取部门列表
     */
    @GetMapping("/dept/list")
    public Result<List<Department>> getDeptList() {
        List<Department> departments = departmentService.list();
        return Result.success(departments);
    }

    /**
     * 获取部门树
     */
    @GetMapping("/dept/tree")
    public Result<List<Department>> getDeptTree(@RequestParam(required = false) Long parentId) {
        List<Department> deptTree = departmentService.getDeptTree(parentId);
        return Result.success(deptTree);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/user/list")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 根据部门ID获取用户列表
     */
    @GetMapping("/user/list/{deptId}")
    public Result<List<User>> getUserListByDept(@PathVariable Long deptId) {
        List<User> users = userService.getUsersByDeptId(deptId);
        return Result.success(users);
    }
}
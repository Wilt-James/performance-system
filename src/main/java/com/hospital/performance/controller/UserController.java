package com.hospital.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.performance.common.PageResult;
import com.hospital.performance.common.Result;
import com.hospital.performance.entity.User;
import com.hospital.performance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> pageUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(current, size);
        Page<User> result = userService.pageUsers(page, keyword, deptId, status);
        
        PageResult<User> pageResult = PageResult.build(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        // 清空密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<String> createUser(@Validated @RequestBody User user) {
        try {
            boolean success = userService.createUser(user);
            return success ? Result.success("用户创建成功") : Result.error("用户创建失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Long id, @Validated @RequestBody User user) {
        try {
            user.setId(id);
            boolean success = userService.updateUser(user);
            return success ? Result.success("用户更新成功") : Result.error("用户更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? Result.success("用户删除成功") : Result.error("用户删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            boolean success = userService.resetPassword(id, newPassword);
            return success ? Result.success("密码重置成功") : Result.error("密码重置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public Result<String> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            boolean success = userService.changePassword(id, oldPassword, newPassword);
            return success ? Result.success("密码修改成功") : Result.error("密码修改失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
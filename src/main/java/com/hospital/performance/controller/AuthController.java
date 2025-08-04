package com.hospital.performance.controller;

import com.hospital.performance.common.Result;
import com.hospital.performance.entity.User;
import com.hospital.performance.service.UserService;
import com.hospital.performance.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 处理错误的GET请求到登录接口
     */
    @GetMapping("/login")
    public Result<String> loginGetNotSupported() {
        return Result.error("登录接口仅支持POST方法，请使用POST请求并在请求体中提供用户名和密码");
    }

    /**
     * 用户登录 (仅支持POST方法)
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // 查询用户
            User user = userService.getByUsername(request.getUsername());
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == 0) {
                return Result.error("用户已被禁用");
            }

            // 生成Token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", buildUserInfo(user));

            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // 解析Token
            String actualToken = token.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(actualToken);
            
            if (username == null) {
                return Result.unauthorized("Token无效");
            }

            // 查询用户信息
            User user = userService.getByUsername(username);
            if (user == null) {
                return Result.unauthorized("用户不存在");
            }

            return Result.success(buildUserInfo(user));
        } catch (Exception e) {
            return Result.unauthorized("Token解析失败");
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String newToken = jwtUtil.refreshToken(actualToken);
            
            if (newToken == null) {
                return Result.unauthorized("Token刷新失败");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);

            return Result.success("Token刷新成功", data);
        } catch (Exception e) {
            return Result.error("Token刷新失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 这里可以实现Token黑名单机制
        return Result.success("登出成功");
    }

    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("employeeNo", user.getEmployeeNo());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("gender", user.getGender());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("position", user.getPosition());
        userInfo.put("jobLevel", user.getJobLevel());
        return userInfo;
    }

    /**
     * 登录请求对象
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
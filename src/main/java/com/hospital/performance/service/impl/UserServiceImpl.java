package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.User;
import com.hospital.performance.mapper.UserMapper;
import com.hospital.performance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>()
                .eq("username", username)
                .eq("deleted", 0));
    }

    @Override
    public User getByEmployeeNo(String employeeNo) {
        return baseMapper.selectOne(new QueryWrapper<User>()
                .eq("employee_no", employeeNo)
                .eq("deleted", 0));
    }

    @Override
    public Page<User> pageUsers(Page<User> page, String keyword, Long deptId, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getEmployeeNo, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        if (deptId != null) {
            wrapper.eq(User::getDeptId, deptId);
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查工号是否已存在
        if (StringUtils.isNotBlank(user.getEmployeeNo()) && getByEmployeeNo(user.getEmployeeNo()) != null) {
            throw new RuntimeException("工号已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return this.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        User userByUsername = getByUsername(user.getUsername());
        if (userByUsername != null && !userByUsername.getId().equals(user.getId())) {
            throw new RuntimeException("用户名已被其他用户使用");
        }
        
        // 检查工号是否被其他用户使用
        if (StringUtils.isNotBlank(user.getEmployeeNo())) {
            User userByEmployeeNo = getByEmployeeNo(user.getEmployeeNo());
            if (userByEmployeeNo != null && !userByEmployeeNo.getId().equals(user.getId())) {
                throw new RuntimeException("工号已被其他用户使用");
            }
        }
        
        // 如果密码有变化，需要重新加密
        if (StringUtils.isNotBlank(user.getPassword()) && !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        
        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    public List<User> getUsersByDeptId(Long deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (deptId != null) {
            wrapper.eq(User::getDeptId, deptId);
        }
        
        wrapper.eq(User::getDeleted, 0)
                .orderByAsc(User::getEmployeeNo)
                .orderByDesc(User::getCreateTime);
        
        return this.list(wrapper);
    }
}
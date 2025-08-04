package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.Department;
import com.hospital.performance.mapper.DepartmentMapper;
import com.hospital.performance.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public Department getByDeptCode(String deptCode) {
        return baseMapper.selectByDeptCode(deptCode);
    }

    @Override
    public List<Department> getDeptTree(Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        return baseMapper.selectDeptTree(parentId);
    }

    @Override
    public List<Department> getChildrenByParentId(Long parentId) {
        return baseMapper.selectChildrenByParentId(parentId);
    }

    @Override
    public List<Department> getByDeptType(Integer deptType) {
        return baseMapper.selectByDeptType(deptType);
    }

    @Override
    public Page<Department> pageDepartments(Page<Department> page, String keyword, Integer deptType, Integer status) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Department::getDeptName, keyword)
                    .or().like(Department::getDeptCode, keyword)
                    .or().like(Department::getLeaderName, keyword));
        }
        
        if (deptType != null) {
            wrapper.eq(Department::getDeptType, deptType);
        }
        
        if (status != null) {
            wrapper.eq(Department::getStatus, status);
        }
        
        wrapper.orderByAsc(Department::getSort)
                .orderByDesc(Department::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDepartment(Department department) {
        // 检查部门编码是否已存在
        if (existsByDeptCode(department.getDeptCode(), null)) {
            throw new RuntimeException("部门编码已存在");
        }
        
        // 设置部门层级
        if (department.getParentId() != null && department.getParentId() > 0) {
            Department parent = this.getById(department.getParentId());
            if (parent != null) {
                department.setLevel(parent.getLevel() + 1);
            }
        } else {
            department.setParentId(0L);
            department.setLevel(1);
        }
        
        return this.save(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(Department department) {
        Department existingDept = this.getById(department.getId());
        if (existingDept == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 检查部门编码是否被其他部门使用
        if (existsByDeptCode(department.getDeptCode(), department.getId())) {
            throw new RuntimeException("部门编码已被其他部门使用");
        }
        
        // 不能将部门设置为自己的子部门
        if (department.getParentId() != null && department.getParentId().equals(department.getId())) {
            throw new RuntimeException("不能将部门设置为自己的子部门");
        }
        
        // 更新部门层级
        if (department.getParentId() != null && department.getParentId() > 0) {
            Department parent = this.getById(department.getParentId());
            if (parent != null) {
                department.setLevel(parent.getLevel() + 1);
            }
        } else {
            department.setParentId(0L);
            department.setLevel(1);
        }
        
        return this.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDepartment(Long id) {
        Department department = this.getById(id);
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 检查是否有子部门
        if (hasChildren(id)) {
            throw new RuntimeException("该部门下有子部门，无法删除");
        }
        
        // 这里可以添加检查是否有用户关联的逻辑
        
        return this.removeById(id);
    }

    @Override
    public boolean hasChildren(Long deptId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, deptId);
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean existsByDeptCode(String deptCode, Long excludeId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDeptCode, deptCode);
        if (excludeId != null) {
            wrapper.ne(Department::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    public String getDeptPath(Long deptId) {
        if (deptId == null) {
            return "";
        }
        
        List<String> pathList = new ArrayList<>();
        Department current = this.getById(deptId);
        
        while (current != null && current.getParentId() != 0) {
            pathList.add(0, current.getDeptName());
            current = this.getById(current.getParentId());
        }
        
        if (current != null) {
            pathList.add(0, current.getDeptName());
        }
        
        return pathList.stream().collect(Collectors.joining(" > "));
    }
}
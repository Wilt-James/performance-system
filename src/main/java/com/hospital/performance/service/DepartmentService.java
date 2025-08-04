package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.Department;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 根据部门编码查询部门
     */
    Department getByDeptCode(String deptCode);

    /**
     * 查询部门树
     */
    List<Department> getDeptTree(Long parentId);

    /**
     * 查询子部门列表
     */
    List<Department> getChildrenByParentId(Long parentId);

    /**
     * 根据部门类型查询部门列表
     */
    List<Department> getByDeptType(Integer deptType);

    /**
     * 分页查询部门列表
     */
    Page<Department> pageDepartments(Page<Department> page, String keyword, Integer deptType, Integer status);

    /**
     * 创建部门
     */
    boolean createDepartment(Department department);

    /**
     * 更新部门
     */
    boolean updateDepartment(Department department);

    /**
     * 删除部门
     */
    boolean deleteDepartment(Long id);

    /**
     * 检查部门是否有子部门
     */
    boolean hasChildren(Long deptId);

    /**
     * 检查部门编码是否存在
     */
    boolean existsByDeptCode(String deptCode, Long excludeId);

    /**
     * 获取部门的完整路径
     */
    String getDeptPath(Long deptId);
}
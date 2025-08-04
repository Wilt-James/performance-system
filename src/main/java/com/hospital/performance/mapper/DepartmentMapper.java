package com.hospital.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.performance.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 根据部门编码查询部门
     */
    Department selectByDeptCode(@Param("deptCode") String deptCode);

    /**
     * 查询子部门列表
     */
    List<Department> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 查询部门树
     */
    List<Department> selectDeptTree(@Param("parentId") Long parentId);

    /**
     * 根据部门类型查询部门列表
     */
    List<Department> selectByDeptType(@Param("deptType") Integer deptType);
}
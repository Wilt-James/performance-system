package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.PerformanceScheme;

import java.util.List;

/**
 * 绩效方案服务接口
 */
public interface PerformanceSchemeService extends IService<PerformanceScheme> {

    /**
     * 根据方案编码查询方案
     */
    PerformanceScheme getBySchemeCode(String schemeCode);

    /**
     * 根据方案类型查询方案列表
     */
    List<PerformanceScheme> getBySchemeType(Integer schemeType);

    /**
     * 根据适用部门类型查询方案列表
     */
    List<PerformanceScheme> getByApplicableDeptType(Integer applicableDeptType);

    /**
     * 分页查询方案列表
     */
    Page<PerformanceScheme> pageSchemes(Page<PerformanceScheme> page, String keyword, 
                                       Integer schemeType, Integer status);

    /**
     * 获取方案列表（不分页）
     */
    List<PerformanceScheme> getSchemeList(String keyword, Integer schemeType, Integer status);

    /**
     * 创建方案
     */
    boolean createScheme(PerformanceScheme scheme);

    /**
     * 更新方案
     */
    boolean updateScheme(PerformanceScheme scheme);

    /**
     * 删除方案
     */
    boolean deleteScheme(Long id);

    /**
     * 检查方案编码是否存在
     */
    boolean existsBySchemeCode(String schemeCode, Long excludeId);

    /**
     * 启用/禁用方案
     */
    boolean toggleSchemeStatus(Long id, Integer status);

    /**
     * 设置默认方案
     */
    boolean setDefaultScheme(Long id);

    /**
     * 获取默认方案
     */
    PerformanceScheme getDefaultScheme(Integer applicableDeptType);
}
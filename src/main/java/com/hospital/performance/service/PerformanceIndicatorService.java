package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.PerformanceIndicator;

import java.util.List;

/**
 * 绩效指标服务接口
 */
public interface PerformanceIndicatorService extends IService<PerformanceIndicator> {

    /**
     * 根据指标编码查询指标
     */
    PerformanceIndicator getByIndicatorCode(String indicatorCode);

    /**
     * 根据指标类型查询指标列表
     */
    List<PerformanceIndicator> getByIndicatorType(Integer indicatorType);

    /**
     * 根据指标分类查询指标列表
     */
    List<PerformanceIndicator> getByIndicatorCategory(Integer indicatorCategory);

    /**
     * 根据适用范围查询指标列表
     */
    List<PerformanceIndicator> getByApplicableScope(Integer applicableScope);

    /**
     * 分页查询指标列表
     */
    Page<PerformanceIndicator> pageIndicators(Page<PerformanceIndicator> page, String keyword, 
                                            Integer indicatorType, Integer indicatorCategory, Integer status);

    /**
     * 获取指标列表（不分页）
     */
    List<PerformanceIndicator> getIndicatorList(String keyword, Integer indicatorType, 
                                              Integer indicatorCategory, Integer status);

    /**
     * 创建指标
     */
    boolean createIndicator(PerformanceIndicator indicator);

    /**
     * 更新指标
     */
    boolean updateIndicator(PerformanceIndicator indicator);

    /**
     * 删除指标
     */
    boolean deleteIndicator(Long id);

    /**
     * 检查指标编码是否存在
     */
    boolean existsByIndicatorCode(String indicatorCode, Long excludeId);

    /**
     * 批量导入指标
     */
    boolean batchImportIndicators(List<PerformanceIndicator> indicators);

    /**
     * 验证指标公式
     */
    boolean validateFormula(String formula);

    /**
     * 启用/禁用指标
     */
    boolean toggleIndicatorStatus(Long id, Integer status);
}
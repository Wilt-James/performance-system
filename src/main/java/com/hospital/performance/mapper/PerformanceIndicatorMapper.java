package com.hospital.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.performance.entity.PerformanceIndicator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 绩效指标Mapper接口
 */
@Mapper
public interface PerformanceIndicatorMapper extends BaseMapper<PerformanceIndicator> {

    /**
     * 根据指标编码查询指标
     */
    PerformanceIndicator selectByIndicatorCode(@Param("indicatorCode") String indicatorCode);

    /**
     * 根据指标类型查询指标列表
     */
    List<PerformanceIndicator> selectByIndicatorType(@Param("indicatorType") Integer indicatorType);

    /**
     * 根据指标分类查询指标列表
     */
    List<PerformanceIndicator> selectByIndicatorCategory(@Param("indicatorCategory") Integer indicatorCategory);

    /**
     * 根据适用范围查询指标列表
     */
    List<PerformanceIndicator> selectByApplicableScope(@Param("applicableScope") Integer applicableScope);
}
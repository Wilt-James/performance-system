package com.hospital.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.performance.entity.PerformanceData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 绩效数据Mapper接口
 */
@Mapper
public interface PerformanceDataMapper extends BaseMapper<PerformanceData> {
}
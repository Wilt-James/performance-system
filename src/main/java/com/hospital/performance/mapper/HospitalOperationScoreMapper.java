package com.hospital.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.performance.entity.HospitalOperationScore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医院运营评分Mapper接口
 */
@Mapper
public interface HospitalOperationScoreMapper extends BaseMapper<HospitalOperationScore> {
}
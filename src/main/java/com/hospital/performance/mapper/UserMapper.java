package com.hospital.performance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.performance.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 使用MyBatis-Plus提供的基础方法，无需自定义方法
}
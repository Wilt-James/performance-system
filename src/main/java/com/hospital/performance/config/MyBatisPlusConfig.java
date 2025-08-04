package com.hospital.performance.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus简化配置类 - 避免版本兼容性问题
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 这里可以从SecurityContext中获取当前用户ID
                this.strictInsertFill(metaObject, "createBy", Long.class, 1L);
                this.strictInsertFill(metaObject, "updateBy", Long.class, 1L);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 这里可以从SecurityContext中获取当前用户ID
                this.strictUpdateFill(metaObject, "updateBy", Long.class, 1L);
            }
        };
    }
}
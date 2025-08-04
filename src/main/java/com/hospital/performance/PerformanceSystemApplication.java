package com.hospital.performance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 院内绩效考核系统启动类
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@MapperScan("com.hospital.performance.mapper")
public class PerformanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceSystemApplication.class, args);
    }
}
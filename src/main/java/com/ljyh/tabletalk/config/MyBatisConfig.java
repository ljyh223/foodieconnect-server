package com.ljyh.tabletalk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类 - 专门用于Mapper扫描
 */
@Configuration
@MapperScan("com.ljyh.tabletalk.mapper")
public class MyBatisConfig {
    // 这个类专门用于配置Mapper扫描
}
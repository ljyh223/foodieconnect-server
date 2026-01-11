package com.ljyh.foodieconnect.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置类
 * 配置推荐系统的缓存策略
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {
    
    /**
     * Redis模板配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = jackson2JsonRedisSerializer();
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        
        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Jackson序列化器配置
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(new JavaTimeModule());
        
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }
    
    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 用户推荐结果缓存 - 30分钟过期
        cacheConfigurations.put("userRecommendations", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 协同过滤推荐缓存 - 30分钟过期
        cacheConfigurations.put("collaborativeRecommendations", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 社交推荐缓存 - 30分钟过期
        cacheConfigurations.put("socialRecommendations", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 混合推荐缓存 - 30分钟过期
        cacheConfigurations.put("hybridRecommendations", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 用户相似度缓存 - 24小时过期
        cacheConfigurations.put("userSimilarity", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 用户餐厅访问历史缓存 - 1小时过期
        cacheConfigurations.put("userRestaurantVisits", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 热门用户缓存 - 6小时过期
        cacheConfigurations.put("popularUsers", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(6))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        // 推荐指标缓存 - 24小时过期
        cacheConfigurations.put("recommendationMetrics", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(jackson2JsonRedisSerializer()))
                .disableCachingNullValues());
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultCacheConfiguration())
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
    
    /**
     * 默认缓存配置
     */
    private RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer()))
            .disableCachingNullValues();
    }
}
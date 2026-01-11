package com.ljyh.foodieconnect.config;

import com.ljyh.foodieconnect.recommendation.CollaborativeFilteringAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 推荐算法配置类
 * 管理推荐系统的各种参数配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "recommendation")
public class RecommendationProperties {
    
    /**
     * 协同过滤配置
     */
    private Collaborative collaborative = new Collaborative();
    
    /**
     * 社交推荐配置
     */
    private Social social = new Social();
    
    /**
     * 混合推荐配置
     */
    private Hybrid hybrid = new Hybrid();
    
    /**
     * 缓存配置
     */
    private Cache cache = new Cache();
    
    /**
     * 性能配置
     */
    private Performance performance = new Performance();
    
    /**
     * 协同过滤配置内部类
     */
    @Data
    public static class Collaborative {
        // 相似度阈值
        private double similarityThreshold = 0.3;
        
        // 默认相似度计算方法
        private CollaborativeFilteringAlgorithm.SimilarityMethod defaultSimilarityMethod = 
            CollaborativeFilteringAlgorithm.SimilarityMethod.COSINE;
        
        // 权重配置
        private double similarityWeight = 0.6;
        private double restaurantWeight = 0.3;
        private double socialWeight = 0.1;
        
        // 推荐数量限制
        private int maxRecommendationsPerUser = 50;
        private int defaultRecommendationCount = 10;
        
        // 数据过滤配置
        private int minCommonRestaurants = 1;
        private int minUserVisits = 3;
    }
    
    /**
     * 社交推荐配置内部类
     */
    @Data
    public static class Social {
        // 社交距离权重配置
        private double firstDegreeWeight = 1.0;
        private double secondDegreeWeight = 0.5;
        private double thirdDegreeWeight = 0.25;
        
        // 权重配置
        private double similarityWeight = 0.7;
        private double socialDistanceWeight = 0.3;
        
        // 共同关注奖励
        private double mutualFollowBonus = 0.2;
        private int maxMutualFollowBonus = 10;
        
        // 推荐数量限制
        private int maxFirstDegreeRecommendations = 20;
        private int maxSecondDegreeRecommendations = 30;
    }
    
    /**
     * 混合推荐配置内部类
     */
    @Data
    public static class Hybrid {
        // 默认策略
        private String defaultStrategy = "WEIGHTED";
        
        // 加权策略权重
        private double collaborativeWeight = 0.6;
        private double socialWeight = 0.4;
        
        // 分层策略比例
        private double socialRatio = 0.6;
        private double collaborativeRatio = 0.4;
        
        // 切换策略阈值
        private int minRestaurantVisitsForSwitching = 5;
        private int minFollowingCountForSwitching = 3;
        
        // 多样性配置
        private double diversityThreshold = 0.3;
        private int maxRecommendationsPerAlgorithm = 10;
    }
    
    /**
     * 缓存配置内部类
     */
    @Data
    public static class Cache {
        // 过期时间配置（分钟）
        private int userRecommendationsExpiration = 30;
        private int collaborativeRecommendationsExpiration = 30;
        private int socialRecommendationsExpiration = 30;
        private int hybridRecommendationsExpiration = 30;
        private int userSimilarityExpiration = 1440; // 24小时
        private int userRestaurantVisitsExpiration = 60; // 1小时
        private int popularUsersExpiration = 360; // 6小时
        private int recommendationMetricsExpiration = 1440; // 24小时
        
        // 缓存键前缀
        private String userRecommendationsPrefix = "user_recommendations:";
        private String collaborativeRecommendationsPrefix = "collaborative_recommendations:";
        private String socialRecommendationsPrefix = "social_recommendations:";
        private String hybridRecommendationsPrefix = "hybrid_recommendations:";
        private String userSimilarityPrefix = "user_similarity:";
        private String userRestaurantVisitsPrefix = "user_restaurant_visits:";
        private String popularUsersPrefix = "popular_users:";
        
        // 缓存开关
        private boolean enableUserRecommendationsCache = true;
        private boolean enableSimilarityCache = true;
        private boolean enableMetricsCache = true;
    }
    
    /**
     * 性能配置内部类
     */
    @Data
    public static class Performance {
        // 批处理配置
        private int batchSize = 100;
        private int maxBatchSize = 500;
        
        // 异步处理配置
        private boolean enableAsyncProcessing = true;
        private int asyncThreadPoolSize = 10;
        private int asyncQueueCapacity = 1000;
        
        // 定时任务配置
        private boolean enableScheduledTasks = true;
        private String similarityCalculationCron = "0 0 */6 * * ?"; // 每6小时执行一次
        private String cleanupCron = "0 0 2 * * ?"; // 每天凌晨2点执行清理
        
        // 数据清理配置
        private int recommendationRetentionDays = 30;
        private int similarityCacheRetentionDays = 7;
        private int metricsRetentionDays = 90;
        
        // 性能监控配置
        private boolean enablePerformanceMonitoring = true;
        private long slowQueryThresholdMs = 1000; // 慢查询阈值1秒
        private int maxRecommendationGenerationTimeMs = 5000; // 最大推荐生成时间5秒
    }
    
    /**
     * 获取协同过滤配置
     */
    public Collaborative getCollaborative() {
        return collaborative;
    }
    
    /**
     * 获取社交推荐配置
     */
    public Social getSocial() {
        return social;
    }
    
    /**
     * 获取混合推荐配置
     */
    public Hybrid getHybrid() {
        return hybrid;
    }
    
    /**
     * 获取缓存配置
     */
    public Cache getCache() {
        return cache;
    }
    
    /**
     * 获取性能配置
     */
    public Performance getPerformance() {
        return performance;
    }
    
    /**
     * 验证配置参数
     */
    public boolean validate() {
        // 验证权重配置
        if (collaborative.getSimilarityWeight() + collaborative.getRestaurantWeight() + 
            collaborative.getSocialWeight() != 1.0) {
            return false;
        }
        
        if (social.getSimilarityWeight() + social.getSocialDistanceWeight() != 1.0) {
            return false;
        }
        
        if (hybrid.getCollaborativeWeight() + hybrid.getSocialWeight() != 1.0) {
            return false;
        }
        
        // 验证阈值配置
        if (collaborative.getSimilarityThreshold() < 0 || collaborative.getSimilarityThreshold() > 1) {
            return false;
        }
        
        // 验证数量配置
        if (collaborative.getMaxRecommendationsPerUser() <= 0 || 
            collaborative.getDefaultRecommendationCount() <= 0) {
            return false;
        }
        
        return true;
    }
}
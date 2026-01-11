package com.ljyh.foodieconnect.recommendation;

import com.ljyh.foodieconnect.dto.UserRecommendationScore;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.mapper.UserFollowMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.mapper.UserRestaurantVisitMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 混合推荐策略实现
 * 整合协同过滤和社交推荐两种算法的结果，通过加权融合的方式提供更准确和多样化的推荐
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HybridRecommendationStrategy {
    
    private final CollaborativeFilteringAlgorithm collaborativeFilteringAlgorithm;
    private final SocialRecommendationAlgorithm socialRecommendationAlgorithm;
    private final UserFollowMapper userFollowMapper;
    private final UserRestaurantVisitMapper userRestaurantVisitMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 混合策略类型枚举
     */
    public enum HybridStrategy {
        WEIGHTED,    // 加权混合
        SWITCHING,   // 切换混合
        CASCADING    // 分层混合
    }
    
    /**
     * 为目标用户生成混合推荐
     */
    public List<UserRecommendationScore> generateRecommendations(Long userId, int limit, HybridStrategy strategy) {
        log.info("开始为用户 {} 生成{}混合推荐，推荐数量: {}", userId, strategy, limit);
        
        // 检查缓存
        String cacheKey = String.format("hybrid_recommendations:%s:%s:%d", strategy, userId, limit);
        List<UserRecommendationScore> cachedResult = (List<UserRecommendationScore>) 
            redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResult != null) {
            log.info("从缓存获取用户 {} 的{}混合推荐结果", userId, strategy);
            return cachedResult;
        }
        
        List<UserRecommendationScore> result;
        
        switch (strategy) {
            case WEIGHTED:
                result = weightedHybridStrategy(userId, limit);
                break;
            case SWITCHING:
                result = switchingHybridStrategy(userId, limit);
                break;
            case CASCADING:
                result = cascadingHybridStrategy(userId, limit);
                break;
            default:
                result = weightedHybridStrategy(userId, limit);
        }
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        
        log.info("为用户 {} 生成了 {} 个{}混合推荐", userId, result.size(), strategy);
        return result;
    }
    
    /**
     * 加权混合推荐策略
     * 结合协同过滤和社交推荐的分数，按权重融合
     */
    private List<UserRecommendationScore> weightedHybridStrategy(Long userId, int limit) {
        log.debug("执行加权混合策略，用户ID: {}", userId);
        
        // 获取两种算法的推荐结果
        List<UserRecommendationScore> collaborativeScores = 
            collaborativeFilteringAlgorithm.generateRecommendations(userId, limit * 2);
        List<UserRecommendationScore> socialScores = 
            socialRecommendationAlgorithm.generateRecommendations(userId, limit * 2);
        
        // 动态权重配置（根据用户数据调整）
        WeightConfig weightConfig = calculateDynamicWeights(userId);
        double collaborativeWeight = weightConfig.getCollaborativeWeight();
        double socialWeight = weightConfig.getSocialWeight();
        
        Map<Long, UserRecommendationScore> mergedScores = new HashMap<>();
        
        // 处理协同过滤结果
        for (UserRecommendationScore score : collaborativeScores) {
            double weightedScore = score.getScore().doubleValue() * collaborativeWeight;
            score.setScore(BigDecimal.valueOf(weightedScore));
            score.setAlgorithmType("hybrid_weighted");
            mergedScores.put(score.getUserId(), score);
        }
        
        // 处理社交推荐结果
        for (UserRecommendationScore score : socialScores) {
            double weightedScore = score.getScore().doubleValue() * socialWeight;
            
            if (mergedScores.containsKey(score.getUserId())) {
                // 合并分数
                UserRecommendationScore existingScore = mergedScores.get(score.getUserId());
                double mergedScore = existingScore.getScore().doubleValue() + weightedScore;
                existingScore.setScore(BigDecimal.valueOf(mergedScore));
                
                // 保留更详细的推荐理由
                if (score.getRecommendationReason() != null && 
                    score.getRecommendationReason().length() > existingScore.getRecommendationReason().length()) {
                    existingScore.setRecommendationReason(score.getRecommendationReason());
                }
            } else {
                score.setScore(BigDecimal.valueOf(weightedScore));
                score.setAlgorithmType("hybrid_weighted");
                mergedScores.put(score.getUserId(), score);
            }
        }
        
        // 按分数排序并限制数量
        return mergedScores.values().stream()
            .sorted()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 切换混合推荐策略
     * 根据用户数据量动态选择主要算法
     */
    private List<UserRecommendationScore> switchingHybridStrategy(Long userId, int limit) {
        log.debug("执行切换混合策略，用户ID: {}", userId);
        
        // 评估用户数据丰富度
        UserDataRichness richness = evaluateUserDataRichness(userId);
        
        List<UserRecommendationScore> result;
        
        if (richness.getRestaurantVisitCount() >= 10 && richness.getFollowingCount() >= 5) {
            // 数据充足，使用加权混合
            log.debug("用户 {} 数据充足，使用加权混合策略", userId);
            result = weightedHybridStrategy(userId, limit);
        } else if (richness.getRestaurantVisitCount() >= 5) {
            // 餐厅数据较多，主要使用协同过滤
            log.debug("用户 {} 餐厅数据较多，主要使用协同过滤", userId);
            result = collaborativeFilteringAlgorithm.generateRecommendations(userId, limit);
            // 标记算法类型
            result.forEach(score -> score.setAlgorithmType("hybrid_switching_collaborative"));
        } else if (richness.getFollowingCount() >= 3) {
            // 社交数据较多，主要使用社交推荐
            log.debug("用户 {} 社交数据较多，主要使用社交推荐", userId);
            result = socialRecommendationAlgorithm.generateRecommendations(userId, limit);
            // 标记算法类型
            result.forEach(score -> score.setAlgorithmType("hybrid_switching_social"));
        } else {
            // 数据不足，返回热门用户（这里简化处理）
            log.debug("用户 {} 数据不足，返回简化推荐", userId);
            result = getPopularUsersFallback(userId, limit);
        }
        
        return result;
    }
    
    /**
     * 分层混合推荐策略
     * 优先使用社交推荐，不足时用协同过滤补充
     */
    private List<UserRecommendationScore> cascadingHybridStrategy(Long userId, int limit) {
        log.debug("执行分层混合策略，用户ID: {}", userId);
        
        List<UserRecommendationScore> result = new ArrayList<>();
        
        // 第一层：社交推荐（60%）
        List<UserRecommendationScore> socialScores = 
            socialRecommendationAlgorithm.generateRecommendations(userId, limit);
        int socialLimit = (int) (limit * 0.6);
        result.addAll(socialScores.stream()
            .limit(socialLimit)
            .collect(Collectors.toList()));
        
        // 第二层：协同过滤（补充剩余部分）
        if (result.size() < limit) {
            Set<Long> excludedUserIds = result.stream()
                .map(UserRecommendationScore::getUserId)
                .collect(Collectors.toSet());
            
            List<UserRecommendationScore> collaborativeScores = 
                collaborativeFilteringAlgorithm.generateRecommendations(userId, limit * 2)
                    .stream()
                    .filter(score -> !excludedUserIds.contains(score.getUserId()))
                    .collect(Collectors.toList());
            
            int collaborativeLimit = limit - result.size();
            result.addAll(collaborativeScores.stream()
                .limit(collaborativeLimit)
                .collect(Collectors.toList()));
        }
        
        // 第三层：热门用户（兜底）
        if (result.size() < limit) {
            Set<Long> excludedUserIds = result.stream()
                .map(UserRecommendationScore::getUserId)
                .collect(Collectors.toSet());
            
            List<UserRecommendationScore> popularUsers = getPopularUsersFallback(userId, limit)
                .stream()
                .filter(score -> !excludedUserIds.contains(score.getUserId()))
                .collect(Collectors.toList());
            
            int popularLimit = limit - result.size();
            result.addAll(popularUsers.stream()
                .limit(popularLimit)
                .collect(Collectors.toList()));
        }
        
        // 标记算法类型
        result.forEach(score -> score.setAlgorithmType("hybrid_cascading"));
        
        return result;
    }
    
    /**
     * 评估用户数据丰富度（完整实现）
     */
    private UserDataRichness evaluateUserDataRichness(Long userId) {
        // 从数据库获取实际数据
        int restaurantVisitCount = userRestaurantVisitMapper.getVisitCount(userId);
        int visitedRestaurantsCount = userRestaurantVisitMapper.getVisitedRestaurantsCount(userId);
        int followingCount = userFollowMapper.getFollowingCount(userId);
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 计算数据质量分数
        double dataQuality = calculateDataQuality(userId);
        
        // 计算活跃度分数
        double activityScore = calculateUserActivityScore(userId);
        
        return UserDataRichness.builder()
            .userId(userId)
            .restaurantVisitCount(restaurantVisitCount)
            .visitedRestaurantsCount(visitedRestaurantsCount)
            .followingCount(followingCount)
            .followersCount(followersCount)
            .dataQuality(dataQuality)
            .activityScore(activityScore)
            .build();
    }
    
    /**
     * 计算数据质量分数
     */
    private double calculateDataQuality(Long userId) {
        // 获取用户最近的访问记录
        List<com.ljyh.foodieconnect.entity.UserRestaurantVisit> recentVisits =
            userRestaurantVisitMapper.findByUserIdAndDateRange(userId,
                new java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)); // 最近30天
        
        if (recentVisits.isEmpty()) {
            return 0.0;
        }
        
        // 计算有评分的访问比例
        long ratedVisits = recentVisits.stream()
            .filter(visit -> visit.getRating() != null)
            .count();
        
        double ratingRatio = (double) ratedVisits / recentVisits.size();
        
        // 计算不同访问类型的多样性
        long uniqueTypes = recentVisits.stream()
            .map(com.ljyh.foodieconnect.entity.UserRestaurantVisit::getVisitType)
            .distinct()
            .count();
        
        double typeDiversity = Math.min(uniqueTypes / 4.0, 1.0); // 4种类型为满分
        
        return (ratingRatio * 0.6 + typeDiversity * 0.4);
    }
    
    /**
     * 计算用户活跃度分数
     */
    private double calculateUserActivityScore(Long userId) {
        // 获取最近7天、30天、90天的访问次数
        long sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        long ninetyDaysAgo = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000;
        
        List<com.ljyh.foodieconnect.entity.UserRestaurantVisit> sevenDaysVisits =
            userRestaurantVisitMapper.findByUserIdAndDateRange(userId, new java.util.Date(sevenDaysAgo));
        List<com.ljyh.foodieconnect.entity.UserRestaurantVisit> thirtyDaysVisits =
            userRestaurantVisitMapper.findByUserIdAndDateRange(userId, new java.util.Date(thirtyDaysAgo));
        List<com.ljyh.foodieconnect.entity.UserRestaurantVisit> ninetyDaysVisits =
            userRestaurantVisitMapper.findByUserIdAndDateRange(userId, new java.util.Date(ninetyDaysAgo));
        
        // 计算活跃度分数
        double sevenDayScore = Math.min(sevenDaysVisits.size() / 10.0, 1.0); // 10次为满分
        double thirtyDayScore = Math.min(thirtyDaysVisits.size() / 30.0, 1.0); // 30次为满分
        double ninetyDayScore = Math.min(ninetyDaysVisits.size() / 60.0, 1.0); // 60次为满分
        
        return (sevenDayScore * 0.5 + thirtyDayScore * 0.3 + ninetyDayScore * 0.2);
    }
    
    /**
     * 热门用户兜底策略（完整实现）
     */
    private List<UserRecommendationScore> getPopularUsersFallback(Long userId, int limit) {
        log.debug("为用户 {} 执行热门用户兜底策略", userId);
        
        // 获取目标用户的偏好，用于个性化热门用户推荐
        Map<Long, Double> userPreferences = getUserPreferences(userId);
        
        // 获取热门用户列表
        List<Long> activeUserIds = userRestaurantVisitMapper.getActiveUserIds(30); // 最近30天活跃用户
        
        // 排除自己和已关注的用户
        Set<Long> excludedUserIds = new HashSet<>();
        excludedUserIds.add(userId);
        excludedUserIds.addAll(userFollowMapper.getFollowingIds(userId));
        
        List<UserRecommendationScore> popularUsers = new ArrayList<>();
        
        for (Long activeUserId : activeUserIds) {
            if (excludedUserIds.contains(activeUserId)) {
                continue;
            }
            
            // 计算用户热度分数
            double popularityScore = calculateUserPopularity(activeUserId);
            
            // 计算与目标用户的相似度
            double similarityScore = calculateUserSimilarityForFallback(userId, activeUserId, userPreferences);
            
            // 综合分数
            double finalScore = popularityScore * 0.7 + similarityScore * 0.3;
            
            // 获取用户信息
            User user = userMapper.selectById(activeUserId);
            if (user == null) {
                continue;
            }
            
            // 生成推荐理由
            String reason = generatePopularUserReason(activeUserId, popularityScore);
            
            UserRecommendationScore score = UserRecommendationScore.builder()
                .userId(activeUserId)
                .userName(user.getDisplayName())
                .userAvatar(user.getAvatarUrl())
                .score(BigDecimal.valueOf(finalScore))
                .algorithmType("popular_fallback")
                .recommendationReason(reason)
                .build();
            
            popularUsers.add(score);
        }
        
        // 按分数排序并限制数量
        return popularUsers.stream()
            .sorted()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取用户偏好
     */
    private Map<Long, Double> getUserPreferences(Long userId) {
        List<com.ljyh.foodieconnect.entity.UserRestaurantVisit> visits =
            userRestaurantVisitMapper.findByUserId(userId);
        
        Map<Long, Double> preferences = new HashMap<>();
        
        for (com.ljyh.foodieconnect.entity.UserRestaurantVisit visit : visits) {
            Long restaurantId = visit.getRestaurantId();
            Double rating = visit.getRating() != null ? visit.getRating().doubleValue() : 3.0;
            preferences.merge(restaurantId, rating, Double::sum);
        }
        
        return preferences;
    }
    
    /**
     * 计算用户热度分数
     */
    private double calculateUserPopularity(Long userId) {
        // 获取粉丝数
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 获取访问次数
        int visitCount = userRestaurantVisitMapper.getVisitCount(userId);
        
        // 获取访问的餐厅数量
        int restaurantCount = userRestaurantVisitMapper.getVisitedRestaurantsCount(userId);
        
        // 计算热度分数
        double followersScore = Math.min(followersCount / 100.0, 1.0); // 100粉丝为满分
        double visitScore = Math.min(visitCount / 50.0, 1.0); // 50次访问为满分
        double restaurantScore = Math.min(restaurantCount / 20.0, 1.0); // 20个餐厅为满分
        
        return (followersScore * 0.5 + visitScore * 0.3 + restaurantScore * 0.2);
    }
    
    /**
     * 计算用户相似度（用于兜底策略）
     */
    private double calculateUserSimilarityForFallback(Long userId1, Long userId2,
                                                 Map<Long, Double> user1Preferences) {
        Map<Long, Double> user2Preferences = getUserPreferences(userId2);
        
        if (user1Preferences.isEmpty() || user2Preferences.isEmpty()) {
            return 0.0;
        }
        
        // 计算余弦相似度
        Set<Long> commonRestaurants = new HashSet<>(user1Preferences.keySet());
        commonRestaurants.retainAll(user2Preferences.keySet());
        
        if (commonRestaurants.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        for (Long restaurantId : commonRestaurants) {
            dotProduct += user1Preferences.get(restaurantId) * user2Preferences.get(restaurantId);
        }
        
        double norm1 = Math.sqrt(user1Preferences.values().stream()
            .mapToDouble(x -> x * x).sum());
        double norm2 = Math.sqrt(user2Preferences.values().stream()
            .mapToDouble(x -> x * x).sum());
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 生成热门用户推荐理由
     */
    private String generatePopularUserReason(Long userId, double popularityScore) {
        User user = userMapper.selectById(userId);
        String userName = user != null ? user.getDisplayName() : "某用户";
        
        if (popularityScore >= 0.8) {
            return String.format("%s是平台活跃用户，有很多餐厅体验分享", userName);
        } else if (popularityScore >= 0.6) {
            return String.format("%s经常分享餐厅体验，值得关注", userName);
        } else {
            return String.format("%s在社区中比较活跃", userName);
        }
    }
    
    /**
     * 计算动态权重
     */
    private WeightConfig calculateDynamicWeights(Long userId) {
        UserDataRichness richness = evaluateUserDataRichness(userId);
        
        // 根据用户数据丰富度调整权重
        double collaborativeWeight;
        double socialWeight;
        
        if (richness.getRestaurantVisitCount() >= 20 && richness.getVisitedRestaurantsCount() >= 10) {
            // 餐厅数据丰富，协同过滤权重更高
            collaborativeWeight = 0.7;
            socialWeight = 0.3;
        } else if (richness.getFollowingCount() >= 20 && richness.getFollowersCount() >= 10) {
            // 社交数据丰富，社交推荐权重更高
            collaborativeWeight = 0.4;
            socialWeight = 0.6;
        } else {
            // 数据一般，平衡权重
            collaborativeWeight = 0.6;
            socialWeight = 0.4;
        }
        
        return WeightConfig.builder()
            .collaborativeWeight(collaborativeWeight)
            .socialWeight(socialWeight)
            .build();
    }
    
    /**
     * 推荐结果多样性优化
     */
    public List<UserRecommendationScore> diversifyRecommendations(
            List<UserRecommendationScore> recommendations, 
            double diversityThreshold) {
        
        if (recommendations.isEmpty()) {
            return recommendations;
        }
        
        List<UserRecommendationScore> diversifiedResult = new ArrayList<>();
        Set<String> usedAlgorithms = new HashSet<>();
        
        for (UserRecommendationScore score : recommendations) {
            String algorithmType = score.getAlgorithmType();
            
            // 计算算法多样性
            double algorithmDiversity = usedAlgorithms.isEmpty() ? 1.0 : 
                1.0 - (usedAlgorithms.contains(algorithmType) ? 0.5 : 0.0);
            
            if (algorithmDiversity >= diversityThreshold || diversifiedResult.isEmpty()) {
                diversifiedResult.add(score);
                usedAlgorithms.add(algorithmType);
                
                if (diversifiedResult.size() >= 10) { // 限制推荐数量
                    break;
                }
            }
        }
        
        return diversifiedResult;
    }
    
    /**
     * 用户数据丰富度内部类
     */
    @lombok.Builder
    @lombok.Data
    private static class UserDataRichness {
        private Long userId;
        private int restaurantVisitCount;
        private int visitedRestaurantsCount;
        private int followingCount;
        private int followersCount;
        private double dataQuality;
        private double activityScore;
    }
    
    /**
     * 权重配置内部类
     */
    @lombok.Builder
    @lombok.Data
    private static class WeightConfig {
        private double collaborativeWeight;
        private double socialWeight;
    }
}
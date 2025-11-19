package com.ljyh.tabletalk.recommendation;

import com.ljyh.tabletalk.dto.UserRecommendationScore;
import com.ljyh.tabletalk.entity.Restaurant;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.entity.UserFollow;
import com.ljyh.tabletalk.entity.UserRestaurantVisit;
import com.ljyh.tabletalk.mapper.RestaurantMapper;
import com.ljyh.tabletalk.mapper.UserFollowMapper;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.mapper.UserRestaurantVisitMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 社交推荐算法实现
 * 基于用户关注关系网络，优先推荐关注用户中具有相似餐厅品味的人
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SocialRecommendationAlgorithm {
    
    private final UserFollowMapper userFollowMapper;
    private final UserRestaurantVisitMapper userRestaurantVisitMapper;
    private final UserMapper userMapper;
    private final RestaurantMapper restaurantMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 社交网络信息类
     */
    public static class SocialNetworkInfo {
        private List<Long> firstDegreeFollows;      // 一度关注列表
        private List<Long> secondDegreeFollows;     // 二度关注列表
        private Map<Long, List<Long>> mutualFollows; // 共同关注信息
        
        // getters and setters
        public List<Long> getFirstDegreeFollows() { return firstDegreeFollows; }
        public void setFirstDegreeFollows(List<Long> firstDegreeFollows) { this.firstDegreeFollows = firstDegreeFollows; }
        
        public List<Long> getSecondDegreeFollows() { return secondDegreeFollows; }
        public void setSecondDegreeFollows(List<Long> secondDegreeFollows) { this.secondDegreeFollows = secondDegreeFollows; }
        
        public Map<Long, List<Long>> getMutualFollows() { return mutualFollows; }
        public void setMutualFollows(Map<Long, List<Long>> mutualFollows) { this.mutualFollows = mutualFollows; }
    }
    
    /**
     * 为目标用户生成社交推荐
     */
    public List<UserRecommendationScore> generateRecommendations(Long userId, int limit) {
        log.info("开始为用户 {} 生成社交推荐，推荐数量: {}", userId, limit);
        
        // 检查缓存
        String cacheKey = "social_recommendations:" + userId + ":" + limit;
        List<UserRecommendationScore> cachedResult = (List<UserRecommendationScore>) 
            redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResult != null) {
            log.info("从缓存获取用户 {} 的社交推荐结果", userId);
            return cachedResult;
        }
        
        // 获取用户的社交网络信息
        SocialNetworkInfo socialNetwork = getSocialNetworkInfo(userId);
        
        if (socialNetwork.getFirstDegreeFollows().isEmpty() && 
            socialNetwork.getSecondDegreeFollows().isEmpty()) {
            log.warn("用户 {} 没有社交关系，无法生成社交推荐", userId);
            return new ArrayList<>();
        }
        
        // 获取目标用户的餐厅偏好向量
        Map<Long, Double> targetUserPreferences = buildUserPreferenceVector(userId);
        
        if (targetUserPreferences.isEmpty()) {
            log.warn("用户 {} 没有餐厅偏好数据", userId);
            return new ArrayList<>();
        }
        
        // 获取已排除的用户ID集合
        Set<Long> excludedUserIds = getExcludedUserIds(userId);
        
        // 计算推荐分数
        List<UserRecommendationScore> recommendations = new ArrayList<>();
        
        // 处理一度关注用户
        recommendations.addAll(processFirstDegreeFollows(userId, socialNetwork, targetUserPreferences, excludedUserIds));
        
        // 处理二度关注用户
        recommendations.addAll(processSecondDegreeFollows(userId, socialNetwork, targetUserPreferences, excludedUserIds));
        
        // 按分数排序并限制数量
        List<UserRecommendationScore> result = recommendations.stream()
            .sorted()
            .limit(limit)
            .collect(Collectors.toList());
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        
        log.info("为用户 {} 生成了 {} 个社交推荐", userId, result.size());
        return result;
    }
    
    /**
     * 获取用户的社交网络信息
     */
    private SocialNetworkInfo getSocialNetworkInfo(Long userId) {
        SocialNetworkInfo info = new SocialNetworkInfo();
        
        // 获取一度关注列表
        List<Long> followingList = userFollowMapper.getFollowingIds(userId);
        info.setFirstDegreeFollows(followingList);
        
        // 获取二度关注列表
        Set<Long> secondDegreeFollows = new HashSet<>();
        Map<Long, List<Long>> mutualFollows = new HashMap<>();
        
        for (Long followId : followingList) {
            List<Long> followsOfFollow = userFollowMapper.getFollowingIds(followId);
            
            for (Long secondDegreeFollow : followsOfFollow) {
                if (!secondDegreeFollow.equals(userId) && !followingList.contains(secondDegreeFollow)) {
                    secondDegreeFollows.add(secondDegreeFollow);
                    
                    // 记录共同关注信息
                    mutualFollows.computeIfAbsent(secondDegreeFollow, k -> new ArrayList<>()).add(followId);
                }
            }
        }
        
        info.setSecondDegreeFollows(new ArrayList<>(secondDegreeFollows));
        info.setMutualFollows(mutualFollows);
        
        log.debug("用户 {} 社交网络信息 - 一度关注: {}, 二度关注: {}", 
                  userId, followingList.size(), secondDegreeFollows.size());
        
        return info;
    }
    
    /**
     * 构建用户偏好向量（完整实现）
     */
    private Map<Long, Double> buildUserPreferenceVector(Long userId) {
        List<UserRestaurantVisit> visits = userRestaurantVisitMapper.findByUserId(userId);
        
        Map<Long, Double> preferenceVector = new HashMap<>();
        Map<Long, Integer> restaurantVisitCounts = new HashMap<>();
        
        // 统计每个餐厅的访问次数和总评分
        for (UserRestaurantVisit visit : visits) {
            Long restaurantId = visit.getRestaurantId();
            Double rating = visit.getRating() != null ? visit.getRating().doubleValue() : 3.0;
            Integer visitCount = visit.getVisitCount() != null ? visit.getVisitCount() : 1;
            
            restaurantVisitCounts.merge(restaurantId, visitCount, Integer::sum);
            
            // 获取访问类型权重
            double typeWeight = getVisitTypeWeight(visit.getVisitType());
            
            // 计算加权评分
            double weightedRating = rating * typeWeight;
            preferenceVector.merge(restaurantId, weightedRating, Double::sum);
        }
        
        // 计算最终的偏好分数
        for (Map.Entry<Long, Double> entry : preferenceVector.entrySet()) {
            Long restaurantId = entry.getKey();
            double totalRating = entry.getValue();
            int totalVisits = restaurantVisitCounts.get(restaurantId);
            
            // 考虑餐厅热度调整
            double restaurantPopularity = calculateRestaurantPopularity(restaurantId);
            
            // 考虑时间衰减
            double timeDecay = calculateTimeDecay(userId, restaurantId);
            
            // 综合计算偏好分数
            double preferenceScore = (totalRating / totalVisits) * 0.6 + 
                                  restaurantPopularity * 0.2 + 
                                  timeDecay * 0.2;
            
            entry.setValue(preferenceScore);
        }
        
        return preferenceVector;
    }
    
    /**
     * 获取访问类型权重
     */
    private double getVisitTypeWeight(UserRestaurantVisit.VisitType visitType) {
        switch (visitType) {
            case REVIEW: return 1.0;        // 评论权重最高
            case RECOMMENDATION: return 0.9; // 推荐权重次之
            case FAVORITE: return 0.8;      // 收藏权重中等
            case CHECK_IN: return 0.6;      // 签到权重较低
            default: return 0.5;
        }
    }
    
    /**
     * 计算餐厅热度
     */
    private double calculateRestaurantPopularity(Long restaurantId) {
        // 获取餐厅的总访问人数和平均评分
        int totalVisitors = userRestaurantVisitMapper.getUniqueVisitorsCount(restaurantId);
        Double avgRating = userRestaurantVisitMapper.getAverageRatingForRestaurant(restaurantId);
        
        // 计算热度分数
        double visitorScore = Math.min(totalVisitors / 100.0, 1.0); // 100人访问为满分
        double ratingScore = (avgRating != null ? avgRating : 3.0) / 5.0;
        
        return (visitorScore * 0.6 + ratingScore * 0.4);
    }
    
    /**
     * 计算时间衰减因子
     */
    private double calculateTimeDecay(Long userId, Long restaurantId) {
        UserRestaurantVisit visit = userRestaurantVisitMapper.findByUserIdAndRestaurantId(userId, restaurantId);
        if (visit == null || visit.getLastVisitTime() == null) {
            return 0.5; // 默认值
        }
        
        long currentTime = System.currentTimeMillis();
        long visitTime = visit.getLastVisitTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long daysDiff = (currentTime - visitTime) / (24 * 60 * 60 * 1000);
        
        // 时间衰减函数：越近的访问权重越高
        if (daysDiff <= 7) {
            return 1.0; // 一周内
        } else if (daysDiff <= 30) {
            return 0.8; // 一个月内
        } else if (daysDiff <= 90) {
            return 0.6; // 三个月内
        } else {
            return 0.4; // 三个月以上
        }
    }
    
    /**
     * 处理一度关注用户（完整实现）
     */
    private List<UserRecommendationScore> processFirstDegreeFollows(Long userId, SocialNetworkInfo socialNetwork, 
                                                               Map<Long, Double> targetUserPreferences, 
                                                               Set<Long> excludedUserIds) {
        List<UserRecommendationScore> recommendations = new ArrayList<>();
        
        for (Long firstDegreeFollow : socialNetwork.getFirstDegreeFollows()) {
            if (excludedUserIds.contains(firstDegreeFollow)) {
                continue;
            }
            
            // 计算餐厅相似度
            double similarity = calculateUserSimilarity(targetUserPreferences, firstDegreeFollow);
            
            // 计算社交距离权重
            double socialWeight = calculateSocialDistanceWeight(1);
            
            // 计算用户活跃度权重
            double activityWeight = calculateUserActivityWeight(firstDegreeFollow);
            
            // 计算用户影响力权重
            double influenceWeight = calculateUserInfluenceWeight(firstDegreeFollow);
            
            // 计算共同餐厅质量权重
            double commonRestaurantWeight = calculateCommonRestaurantQuality(userId, firstDegreeFollow);
            
            // 计算推荐分数（多维度综合）
            double score = calculateComprehensiveScore(similarity, socialWeight, activityWeight, 
                                                   influenceWeight, commonRestaurantWeight, 1);
            
            // 生成推荐理由
            String reason = generateSocialReason(userId, firstDegreeFollow, 1, null);
            
            // 获取用户信息
            User user = userMapper.selectById(firstDegreeFollow);
            
            // 获取共同餐厅信息
            List<Restaurant> commonRestaurants = getCommonRestaurants(userId, firstDegreeFollow);
            
            UserRecommendationScore recommendationScore = UserRecommendationScore.builder()
                .userId(firstDegreeFollow)
                .userName(user != null ? user.getDisplayName() : "未知用户")
                .userAvatar(user != null ? user.getAvatarUrl() : null)
                .score(BigDecimal.valueOf(score))
                .algorithmType("social")
                .similarity(similarity)
                .socialDistance(1)
                .activityScore(activityWeight)
                .influenceScore(influenceWeight)
                .recommendationReason(reason)
                .commonRestaurants(commonRestaurants)
                .build();
            
            recommendations.add(recommendationScore);
        }
        
        return recommendations;
    }
    
    /**
     * 处理二度关注用户（完整实现）
     */
    private List<UserRecommendationScore> processSecondDegreeFollows(Long userId, SocialNetworkInfo socialNetwork, 
                                                                 Map<Long, Double> targetUserPreferences, 
                                                                 Set<Long> excludedUserIds) {
        List<UserRecommendationScore> recommendations = new ArrayList<>();
        
        for (Long secondDegreeFollow : socialNetwork.getSecondDegreeFollows()) {
            if (excludedUserIds.contains(secondDegreeFollow)) {
                continue;
            }
            
            // 计算餐厅相似度
            double similarity = calculateUserSimilarity(targetUserPreferences, secondDegreeFollow);
            
            // 计算社交距离权重
            double socialWeight = calculateSocialDistanceWeight(2);
            
            // 考虑共同关注数量和质量
            List<Long> mutualFollows = socialNetwork.getMutualFollows().get(secondDegreeFollow);
            int mutualFollowCount = mutualFollows != null ? mutualFollows.size() : 0;
            double mutualFollowBonus = calculateMutualFollowBonus(mutualFollows);
            
            // 计算用户活跃度权重
            double activityWeight = calculateUserActivityWeight(secondDegreeFollow);
            
            // 计算用户影响力权重
            double influenceWeight = calculateUserInfluenceWeight(secondDegreeFollow);
            
            // 计算共同餐厅质量权重
            double commonRestaurantWeight = calculateCommonRestaurantQuality(userId, secondDegreeFollow);
            
            // 计算推荐分数（多维度综合）
            double score = calculateComprehensiveScore(similarity, socialWeight, activityWeight, 
                                                   influenceWeight, commonRestaurantWeight, 2) + mutualFollowBonus;
            
            // 生成推荐理由
            String reason = generateSocialReason(userId, secondDegreeFollow, 2, mutualFollows);
            
            // 获取用户信息
            User user = userMapper.selectById(secondDegreeFollow);
            
            // 获取共同餐厅信息
            List<Restaurant> commonRestaurants = getCommonRestaurants(userId, secondDegreeFollow);
            
            UserRecommendationScore recommendationScore = UserRecommendationScore.builder()
                .userId(secondDegreeFollow)
                .userName(user != null ? user.getDisplayName() : "未知用户")
                .userAvatar(user != null ? user.getAvatarUrl() : null)
                .score(BigDecimal.valueOf(score))
                .algorithmType("social")
                .similarity(similarity)
                .socialDistance(2)
                .mutualFollowsCount(mutualFollowCount)
                .activityScore(activityWeight)
                .influenceScore(influenceWeight)
                .recommendationReason(reason)
                .commonRestaurants(commonRestaurants)
                .build();
            
            recommendations.add(recommendationScore);
        }
        
        return recommendations;
    }
    
    /**
     * 计算用户餐厅相似度
     */
    private double calculateUserSimilarity(Map<Long, Double> targetUserPreferences, Long candidateUserId) {
        Map<Long, Double> candidatePreferences = buildUserPreferenceVector(candidateUserId);
        
        if (targetUserPreferences.isEmpty() || candidatePreferences.isEmpty()) {
            return 0.0;
        }
        
        // 使用余弦相似度计算
        Set<Long> commonRestaurants = new HashSet<>(targetUserPreferences.keySet());
        commonRestaurants.retainAll(candidatePreferences.keySet());
        
        if (commonRestaurants.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long restaurantId : commonRestaurants) {
            double rating1 = targetUserPreferences.get(restaurantId);
            double rating2 = candidatePreferences.get(restaurantId);
            
            dotProduct += rating1 * rating2;
        }
        
        for (Double rating : targetUserPreferences.values()) {
            norm1 += rating * rating;
        }
        
        for (Double rating : candidatePreferences.values()) {
            norm2 += rating * rating;
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 计算社交距离权重
     */
    private double calculateSocialDistanceWeight(int socialDistance) {
        switch (socialDistance) {
            case 1: // 一度关注
                return 1.0;
            case 2: // 二度关注
                return 0.5;
            case 3: // 三度关注
                return 0.25;
            default:
                return 0.1;
        }
    }
    
    /**
     * 获取已排除的用户ID集合
     */
    private Set<Long> getExcludedUserIds(Long userId) {
        Set<Long> excludedUserIds = new HashSet<>();
        
        // 排除自己
        excludedUserIds.add(userId);
        
        // 排除已关注的用户
        List<Long> followingIds = userFollowMapper.getFollowingIds(userId);
        excludedUserIds.addAll(followingIds);
        
        return excludedUserIds;
    }
    
    /**
     * 生成社交推荐理由
     */
    private String generateSocialReason(Long targetUserId, Long recommendedUserId, 
                                   int socialDistance, List<Long> mutualFollows) {
        User recommendedUser = userMapper.selectById(recommendedUserId);
        String recommendedUserName = recommendedUser != null ? 
            recommendedUser.getDisplayName() : "某用户";
        
        switch (socialDistance) {
            case 1:
                return String.format("您关注了%s，且你们的餐厅品味相似", recommendedUserName);
                 
            case 2:
                if (mutualFollows != null && !mutualFollows.isEmpty()) {
                    String mutualFollowNames = mutualFollows.stream()
                        .limit(2)
                        .map(this::getUserName)
                        .collect(Collectors.joining("、"));
                   
                    if (mutualFollows.size() > 2) {
                        return String.format("您和%s都关注了%s等%d人，且餐厅品味相似", 
                                          recommendedUserName, mutualFollowNames, mutualFollows.size());
                    } else {
                        return String.format("您和%s都关注了%s，且餐厅品味相似", 
                                          recommendedUserName, mutualFollowNames);
                    }
                } else {
                    return String.format("您关注的人关注了%s，且你们的餐厅品味相似", recommendedUserName);
                }
                 
            default:
                return String.format("%s在您的社交网络中，且餐厅品味相似", recommendedUserName);
        }
    }
    
    /**
     * 获取用户名称
     */
    private String getUserName(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getDisplayName() : "某用户";
    }
    
    /**
     * 计算用户活跃度权重
     */
    private double calculateUserActivityWeight(Long userId) {
        // 获取用户的餐厅访问数量
        int visitedRestaurantsCount = userRestaurantVisitMapper.getVisitedRestaurantsCount(userId);
        
        // 获取用户的总访问次数
        int totalVisitCount = userRestaurantVisitMapper.getVisitCount(userId);
        
        // 获取用户的关注数和粉丝数
        int followingCount = userFollowMapper.getFollowingCount(userId);
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 计算活跃度分数
        double restaurantActivity = Math.min(visitedRestaurantsCount / 50.0, 1.0); // 50个餐厅为满分
        double visitFrequency = Math.min(totalVisitCount / 200.0, 1.0); // 200次访问为满分
        double socialActivity = Math.min((followingCount + followersCount) / 100.0, 1.0); // 100个关注/粉丝为满分
        
        return (restaurantActivity * 0.4 + visitFrequency * 0.3 + socialActivity * 0.3);
    }
    
    /**
     * 计算用户影响力权重
     */
    private double calculateUserInfluenceWeight(Long userId) {
        // 获取用户的粉丝数
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 获取用户发布的评论数（如果有评论表）
        // 这里简化处理，使用餐厅访问数作为替代指标
        int reviewCount = userRestaurantVisitMapper.getVisitCount(userId);
        
        // 获取用户推荐的餐厅数量
        List<UserRestaurantVisit> recommendationVisits = userRestaurantVisitMapper
            .findByUserIdAndVisitType(userId, UserRestaurantVisit.VisitType.RECOMMENDATION.name());
        int recommendationCount = recommendationVisits.size();
        
        // 计算影响力分数
        double followersScore = Math.min(followersCount / 1000.0, 1.0); // 1000粉丝为满分
        double reviewScore = Math.min(reviewCount / 100.0, 1.0); // 100评论为满分
        double recommendationScore = Math.min(recommendationCount / 50.0, 1.0); // 50推荐为满分
        
        return (followersScore * 0.5 + reviewScore * 0.3 + recommendationScore * 0.2);
    }
    
    /**
     * 计算共同关注奖励分数
     */
    private double calculateMutualFollowBonus(List<Long> mutualFollows) {
        if (mutualFollows == null || mutualFollows.isEmpty()) {
            return 0.0;
        }
        
        // 计算共同关注用户的影响力
        double totalInfluence = 0.0;
        for (Long mutualFollowId : mutualFollows) {
            double influence = calculateUserInfluenceWeight(mutualFollowId);
            totalInfluence += influence;
        }
        
        // 平均影响力
        double avgInfluence = totalInfluence / mutualFollows.size();
        
        // 共同关注数量奖励（最多0.3）
        double countBonus = Math.min(mutualFollows.size() / 10.0, 0.3);
        
        // 影响力奖励（最多0.2）
        double influenceBonus = Math.min(avgInfluence * 0.2, 0.2);
        
        return countBonus + influenceBonus;
    }
    
    /**
     * 计算共同餐厅质量权重
     */
    private double calculateCommonRestaurantQuality(Long userId1, Long userId2) {
        List<UserRestaurantVisit> commonVisits = userRestaurantVisitMapper
            .findCommonVisitedRestaurants(userId1, userId2);
        
        if (commonVisits.isEmpty()) {
            return 0.0;
        }
        
        double totalQuality = 0.0;
        Set<Long> uniqueRestaurants = new HashSet<>();
        
        for (UserRestaurantVisit visit : commonVisits) {
            Long restaurantId = visit.getRestaurantId();
            uniqueRestaurants.add(restaurantId);
            
            // 获取餐厅信息
            Restaurant restaurant = restaurantMapper.selectById(restaurantId);
            if (restaurant == null) {
                continue;
            }
            
            // 餐厅评分权重
            double rating = visit.getRating() != null ? visit.getRating().doubleValue() : 3.0;
            double ratingWeight = rating / 5.0;
            
            // 餐厅热度权重
            double popularity = calculateRestaurantPopularity(restaurantId);
            
            // 访问类型权重
            double typeWeight = getVisitTypeWeight(visit.getVisitType());
            
            // 综合质量分数
            double quality = ratingWeight * 0.5 + popularity * 0.3 + typeWeight * 0.2;
            totalQuality += quality;
        }
        
        // 归一化处理
        return uniqueRestaurants.size() > 0 ? totalQuality / uniqueRestaurants.size() : 0.0;
    }
    
    /**
     * 计算综合推荐分数
     */
    private double calculateComprehensiveScore(double similarity, double socialWeight,
                                          double activityWeight, double influenceWeight,
                                          double commonRestaurantWeight, int socialDistance) {
        // 根据社交距离调整权重
        double similarityWeight = socialDistance == 1 ? 0.35 : 0.25;
        double socialDistanceWeight = socialDistance == 1 ? 0.25 : 0.20;
        double activityWeightFactor = 0.20;
        double influenceWeightFactor = 0.15;
        double restaurantWeightFactor = 0.05;
        
        // 综合计算
        double comprehensiveScore = similarity * similarityWeight +
                                 socialWeight * socialDistanceWeight +
                                 activityWeight * activityWeightFactor +
                                 influenceWeight * influenceWeightFactor +
                                 commonRestaurantWeight * restaurantWeightFactor;
        
        // 确保分数在0-1范围内
        return Math.max(0.0, Math.min(1.0, comprehensiveScore));
    }
    
    /**
     * 获取共同餐厅列表
     */
    private List<Restaurant> getCommonRestaurants(Long userId1, Long userId2) {
        List<UserRestaurantVisit> commonVisits = userRestaurantVisitMapper
            .findCommonVisitedRestaurants(userId1, userId2);
        
        return commonVisits.stream()
            .map(visit -> restaurantMapper.selectById(visit.getRestaurantId()))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }
}
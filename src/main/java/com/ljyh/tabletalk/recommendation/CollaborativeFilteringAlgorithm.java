package com.ljyh.tabletalk.recommendation;

import com.ljyh.tabletalk.dto.UserRecommendationScore;
import com.ljyh.tabletalk.entity.Restaurant;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.entity.UserRestaurantVisit;
import com.ljyh.tabletalk.entity.UserSimilarityCache;
import com.ljyh.tabletalk.mapper.RestaurantMapper;
import com.ljyh.tabletalk.mapper.UserFollowMapper;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.mapper.UserRestaurantVisitMapper;
import com.ljyh.tabletalk.mapper.UserSimilarityCacheMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐算法实现
 * 基于用户-餐厅交互矩阵，通过计算用户间的相似度来推荐具有相似餐厅偏好的用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborativeFilteringAlgorithm {
    
    private final UserRestaurantVisitMapper userRestaurantVisitMapper;
    private final UserSimilarityCacheMapper userSimilarityCacheMapper;
    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;
    private final RestaurantMapper restaurantMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 相似度计算方法枚举
    public enum SimilarityMethod {
        COSINE,          // 余弦相似度
        PEARSON,         // 皮尔逊相关系数
        ADJUSTED_COSINE  // 调整余弦相似度
    }
    
    /**
     * 为目标用户生成协同过滤推荐
     */
    public List<UserRecommendationScore> generateRecommendations(Long userId, int limit) {
        log.info("开始为用户 {} 生成协同过滤推荐，推荐数量: {}", userId, limit);
        
        // 检查缓存
        String cacheKey = "collaborative_recommendations:" + userId + ":" + limit;
        List<UserRecommendationScore> cachedResult = (List<UserRecommendationScore>) 
            redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResult != null) {
            log.info("从缓存获取用户 {} 的推荐结果", userId);
            return cachedResult;
        }
        
        // 构建用户-餐厅交互矩阵
        Map<Long, Map<Long, Double>> userRestaurantMatrix = buildUserRestaurantMatrix(userId);
        
        if (userRestaurantMatrix.isEmpty() || userRestaurantMatrix.size() == 1) {
            log.warn("用户 {} 数据不足，无法生成协同过滤推荐", userId);
            return new ArrayList<>();
        }
        
        // 获取目标用户的访问向量
        Map<Long, Double> targetUserVector = userRestaurantMatrix.get(userId);
        if (targetUserVector == null) {
            log.warn("用户 {} 在交互矩阵中不存在", userId);
            return new ArrayList<>();
        }
        
        // 计算与其他用户的相似度
        Map<Long, Double> userSimilarities = new HashMap<>();
        SimilarityMethod method = SimilarityMethod.COSINE; // 默认使用余弦相似度
        
        for (Map.Entry<Long, Map<Long, Double>> entry : userRestaurantMatrix.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            
            Map<Long, Double> otherUserVector = entry.getValue();
            double similarity = calculateSimilarity(targetUserVector, otherUserVector, method);
            
            if (similarity >= 0.3) { // 相似度阈值
                userSimilarities.put(otherUserId, similarity);
            }
        }
        
        if (userSimilarities.isEmpty()) {
            log.warn("用户 {} 没有找到相似用户", userId);
            return new ArrayList<>();
        }
        
        // 获取已排除的用户ID集合
        Set<Long> excludedUserIds = getExcludedUserIds(userId);
        
        // 计算推荐分数
        List<UserRecommendationScore> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> similarityEntry : userSimilarities.entrySet()) {
            Long candidateUserId = similarityEntry.getKey();
            Double similarity = similarityEntry.getValue();
            
            if (excludedUserIds.contains(candidateUserId)) {
                continue;
            }
            
            // 计算推荐分数
            double score = calculateRecommendationScore(userId, candidateUserId, similarity);
            
            // 获取共同访问的餐厅
            List<UserRestaurantVisit> commonVisits = userRestaurantVisitMapper
                .findCommonVisitedRestaurants(userId, candidateUserId);
            
            // 生成推荐理由
            String reason = generateRecommendationReason(userId, candidateUserId, commonVisits);
            
            // 获取用户信息
            User user = userMapper.selectById(candidateUserId);
            
            UserRecommendationScore recommendationScore = UserRecommendationScore.builder()
                .userId(candidateUserId)
                .userName(user != null ? user.getDisplayName() : "未知用户")
                .userAvatar(user != null ? user.getAvatarUrl() : null)
                .score(BigDecimal.valueOf(score))
                .algorithmType("collaborative")
                .similarity(similarity)
                .recommendationReason(reason)
                .commonRestaurants(convertToRestaurants(commonVisits))
                .build();
            
            recommendations.add(recommendationScore);
        }
        
        // 按分数排序并限制数量
        List<UserRecommendationScore> result = recommendations.stream()
            .sorted()
            .limit(limit)
            .collect(Collectors.toList());
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        
        log.info("为用户 {} 生成了 {} 个协同过滤推荐", userId, result.size());
        return result;
    }
    
    /**
     * 构建用户-餐厅交互矩阵
     */
    private Map<Long, Map<Long, Double>> buildUserRestaurantMatrix(Long userId) {
        log.debug("构建用户-餐厅交互矩阵，目标用户ID: {}", userId);
        
        // 获取目标用户的访问历史
        List<UserRestaurantVisit> targetUserVisits = userRestaurantVisitMapper.findByUserId(userId);
        
        if (targetUserVisits.isEmpty()) {
            log.warn("用户 {} 没有餐厅访问记录", userId);
            return new HashMap<>();
        }
        
        // 获取目标用户访问的餐厅ID集合
        Set<Long> targetRestaurantIds = targetUserVisits.stream()
            .map(UserRestaurantVisit::getRestaurantId)
            .collect(Collectors.toSet());
        
        // 获取访问过相同餐厅的其他用户
        List<UserRestaurantVisit> relatedVisits = userRestaurantVisitMapper
            .findByRestaurantIds(targetRestaurantIds);
        
        // 构建用户-餐厅交互矩阵
        Map<Long, Map<Long, Double>> userRestaurantMatrix = new HashMap<>();
        
        for (UserRestaurantVisit visit : relatedVisits) {
            Long currentUserId = visit.getUserId();
            Long restaurantId = visit.getRestaurantId();
            
            // 计算综合评分
            double rating = calculateCompositeRating(visit);
            
            userRestaurantMatrix
                .computeIfAbsent(currentUserId, k -> new HashMap<>())
                .put(restaurantId, rating);
        }
        
        log.debug("用户-餐厅交互矩阵构建完成，涉及用户数: {}, 餐厅数: {}", 
                  userRestaurantMatrix.size(), targetRestaurantIds.size());
        
        return userRestaurantMatrix;
    }
    
    /**
     * 计算综合评分
     * 考虑评分、访问次数和访问类型
     */
    private double calculateCompositeRating(UserRestaurantVisit visit) {
        double baseRating = visit.getRating() != null ? visit.getRating().doubleValue() : 3.0;
        int visitCount = visit.getVisitCount() != null ? visit.getVisitCount() : 1;
        UserRestaurantVisit.VisitType visitType = visit.getVisitType();
        
        // 根据访问类型调整权重
        double typeWeight = getTypeWeight(visitType);
        
        // 访问次数权重（最多5次）
        double countWeight = Math.min(visitCount / 5.0, 1.0);
        
        // 综合评分 = 基础评分 * 类型权重 * (1 + 访问次数权重 * 0.2)
        return baseRating * typeWeight * (1.0 + countWeight * 0.2);
    }
    
    /**
     * 获取访问类型权重
     */
    private double getTypeWeight(UserRestaurantVisit.VisitType visitType) {
        switch (visitType) {
            case REVIEW: return 1.0;        // 评论权重最高
            case RECOMMENDATION: return 0.9; // 推荐权重次之
            case FAVORITE: return 0.8;      // 收藏权重中等
            case CHECK_IN: return 0.6;      // 签到权重较低
            default: return 0.5;
        }
    }
    
    /**
     * 计算用户间的余弦相似度
     */
    public double calculateSimilarity(Map<Long, Double> user1Vector, 
                                    Map<Long, Double> user2Vector, 
                                    SimilarityMethod method) {
        switch (method) {
            case COSINE:
                return calculateCosineSimilarity(user1Vector, user2Vector);
            case PEARSON:
                return calculatePearsonCorrelation(user1Vector, user2Vector);
            case ADJUSTED_COSINE:
                return calculateAdjustedCosineSimilarity(user1Vector, user2Vector);
            default:
                return calculateCosineSimilarity(user1Vector, user2Vector);
        }
    }
    
    /**
     * 计算用户间的余弦相似度
     */
    private double calculateCosineSimilarity(Map<Long, Double> user1Vector, 
                                         Map<Long, Double> user2Vector) {
        // 找到共同访问的餐厅
        Set<Long> commonRestaurants = new HashSet<>(user1Vector.keySet());
        commonRestaurants.retainAll(user2Vector.keySet());
        
        if (commonRestaurants.isEmpty()) {
            return 0.0;
        }
        
        // 计算点积
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long restaurantId : commonRestaurants) {
            double rating1 = user1Vector.get(restaurantId);
            double rating2 = user2Vector.get(restaurantId);
            
            dotProduct += rating1 * rating2;
        }
        
        // 计算向量的模
        for (Double rating : user1Vector.values()) {
            norm1 += rating * rating;
        }
        
        for (Double rating : user2Vector.values()) {
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
     * 计算用户间的皮尔逊相关系数
     */
    private double calculatePearsonCorrelation(Map<Long, Double> user1Vector, 
                                           Map<Long, Double> user2Vector) {
        // 找到共同访问的餐厅
        Set<Long> commonRestaurants = new HashSet<>(user1Vector.keySet());
        commonRestaurants.retainAll(user2Vector.keySet());
        
        if (commonRestaurants.size() < 2) {
            return 0.0;
        }
        
        // 计算平均评分
        double avg1 = user1Vector.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avg2 = user2Vector.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // 计算皮尔逊相关系数
        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;
        
        for (Long restaurantId : commonRestaurants) {
            double rating1 = user1Vector.get(restaurantId);
            double rating2 = user2Vector.get(restaurantId);
            
            double diff1 = rating1 - avg1;
            double diff2 = rating2 - avg2;
            
            numerator += diff1 * diff2;
            denominator1 += diff1 * diff1;
            denominator2 += diff2 * diff2;
        }
        
        denominator1 = Math.sqrt(denominator1);
        denominator2 = Math.sqrt(denominator2);
        
        if (denominator1 == 0 || denominator2 == 0) {
            return 0.0;
        }
        
        return numerator / (denominator1 * denominator2);
    }
    
    /**
     * 计算调整余弦相似度（完整实现）
     */
    private double calculateAdjustedCosineSimilarity(Map<Long, Double> user1Vector, 
                                                  Map<Long, Double> user2Vector) {
        // 获取所有共同访问的餐厅
        Set<Long> commonRestaurants = new HashSet<>(user1Vector.keySet());
        commonRestaurants.retainAll(user2Vector.keySet());
        
        if (commonRestaurants.isEmpty()) {
            return 0.0;
        }
        
        // 计算每个餐厅的平均评分（从数据库获取实际数据）
        Map<Long, Double> restaurantAvgRatings = new HashMap<>();
        for (Long restaurantId : commonRestaurants) {
            Double avgRating = userRestaurantVisitMapper.getAverageRatingForRestaurant(restaurantId);
            restaurantAvgRatings.put(restaurantId, avgRating != null ? avgRating : 3.0);
        }
        
        // 计算调整余弦相似度
        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;
        
        for (Long restaurantId : commonRestaurants) {
            double rating1 = user1Vector.get(restaurantId);
            double rating2 = user2Vector.get(restaurantId);
            double restaurantAvg = restaurantAvgRatings.get(restaurantId);
            
            // 使用餐厅平均评分进行调整
            double adjustedRating1 = rating1 - restaurantAvg;
            double adjustedRating2 = rating2 - restaurantAvg;
            
            numerator += adjustedRating1 * adjustedRating2;
            denominator1 += adjustedRating1 * adjustedRating1;
            denominator2 += adjustedRating2 * adjustedRating2;
        }
        
        denominator1 = Math.sqrt(denominator1);
        denominator2 = Math.sqrt(denominator2);
        
        if (denominator1 == 0 || denominator2 == 0) {
            return 0.0;
        }
        
        return numerator / (denominator1 * denominator2);
    }
    
    /**
     * 计算推荐分数（完整实现）
     */
    private double calculateRecommendationScore(Long targetUserId, Long candidateUserId, double similarity) {
        // 获取共同访问的餐厅
        List<UserRestaurantVisit> commonVisits = userRestaurantVisitMapper
            .findCommonVisitedRestaurants(targetUserId, candidateUserId);
        
        if (commonVisits.isEmpty()) {
            return 0.0;
        }
        
        // 计算相似度权重（基础权重）
        double similarityWeight = similarity;
        
        // 计算共同餐厅的权重
        double restaurantWeight = calculateRestaurantWeight(commonVisits);
        
        // 计算时间衰减权重（最近的访问权重更高）
        double timeWeight = calculateTimeWeight(commonVisits);
        
        // 计算社交权重（考虑候选用户的活跃度和影响力）
        double socialWeight = calculateSocialWeight(candidateUserId);
        
        // 计算多样性权重（鼓励推荐不同类型的用户）
        double diversityWeight = calculateDiversityWeight(targetUserId, candidateUserId);
        
        // 综合计算推荐分数（权重可根据配置调整）
        double finalScore = similarityWeight * 0.4 + 
                           restaurantWeight * 0.25 + 
                           timeWeight * 0.15 + 
                           socialWeight * 0.15 + 
                           diversityWeight * 0.05;
        
        // 确保分数在0-1范围内
        return Math.max(0.0, Math.min(1.0, finalScore));
    }
    
    /**
     * 计算餐厅权重
     */
    private double calculateRestaurantWeight(List<UserRestaurantVisit> commonVisits) {
        if (commonVisits.isEmpty()) {
            return 0.0;
        }
        
        double totalWeight = 0.0;
        Set<Long> uniqueRestaurants = new HashSet<>();
        
        for (UserRestaurantVisit visit : commonVisits) {
            Long restaurantId = visit.getRestaurantId();
            uniqueRestaurants.add(restaurantId);
            
            // 获取餐厅的详细信息
            Restaurant restaurant = restaurantMapper.selectById(restaurantId);
            if (restaurant == null) {
                continue;
            }
            
            // 基础评分权重
            double rating = visit.getRating() != null ? visit.getRating().doubleValue() : 3.0;
            double ratingWeight = rating / 5.0;
            
            // 访问次数权重
            int visitCount = visit.getVisitCount() != null ? visit.getVisitCount() : 1;
            double visitCountWeight = Math.min(visitCount / 5.0, 1.0);
            
            // 餐厅热度权重（基于评分和访问人数）
            double restaurantPopularity = calculateRestaurantPopularity(restaurantId);
            
            // 访问类型权重
            double typeWeight = getTypeWeight(visit.getVisitType());
            
            // 综合权重
            double visitWeight = ratingWeight * 0.4 + 
                               visitCountWeight * 0.3 + 
                               restaurantPopularity * 0.2 + 
                               typeWeight * 0.1;
            
            totalWeight += visitWeight;
        }
        
        // 归一化权重，考虑餐厅数量
        return Math.min(totalWeight / uniqueRestaurants.size(), 1.0);
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
     * 计算时间权重
     */
    private double calculateTimeWeight(List<UserRestaurantVisit> visits) {
        if (visits.isEmpty()) {
            return 0.0;
        }
        
        long currentTime = System.currentTimeMillis();
        long oneMonthAgo = currentTime - (30L * 24 * 60 * 60 * 1000); // 30天前
        long threeMonthsAgo = currentTime - (90L * 24 * 60 * 60 * 1000); // 90天前
        
        double totalWeight = 0.0;
        int validVisits = 0;
        
        for (UserRestaurantVisit visit : visits) {
            if (visit.getLastVisitTime() == null) {
                continue;
            }
            
            long visitTime = visit.getLastVisitTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            
            if (visitTime >= oneMonthAgo) {
                // 最近一个月的访问，权重最高
                totalWeight += 1.0;
            } else if (visitTime >= threeMonthsAgo) {
                // 1-3个月前的访问，权重中等
                totalWeight += 0.6;
            } else {
                // 3个月前的访问，权重较低
                totalWeight += 0.3;
            }
            
            validVisits++;
        }
        
        return validVisits > 0 ? totalWeight / validVisits : 0.0;
    }
    
    /**
     * 计算多样性权重
     */
    private double calculateDiversityWeight(Long targetUserId, Long candidateUserId) {
        // 获取目标用户已关注用户的类型分布
        List<Long> followingIds = userFollowMapper.getFollowingIds(targetUserId);
        
        if (followingIds.isEmpty()) {
            return 1.0; // 如果没有关注任何人，多样性权重最高
        }
        
        // 获取候选用户的特征
        User candidateUser = userMapper.selectById(candidateUserId);
        if (candidateUser == null) {
            return 0.5;
        }
        
        // 计算候选用户与已关注用户的差异性
        double diversityScore = 0.0;
        int comparisonCount = 0;
        
        for (Long followingId : followingIds) {
            User followingUser = userMapper.selectById(followingId);
            if (followingUser == null) {
                continue;
            }
            
            // 基于用户特征的差异性计算
            double userDiversity = calculateUserDiversity(candidateUser, followingUser);
            diversityScore += userDiversity;
            comparisonCount++;
        }
        
        if (comparisonCount == 0) {
            return 1.0;
        }
        
        // 归一化多样性分数
        double avgDiversity = diversityScore / comparisonCount;
        return Math.max(0.0, Math.min(1.0, avgDiversity));
    }
    
    /**
     * 计算两个用户之间的差异性
     */
    private double calculateUserDiversity(User user1, User user2) {
        double diversity = 0.0;
        
        // 基于用户活跃度的差异性
        int activity1 = getUserActivityLevel(user1.getId());
        int activity2 = getUserActivityLevel(user2.getId());
        double activityDiversity = Math.abs(activity1 - activity2) / 100.0;
        
        // 基于用户类型的差异性（如果有用户类型字段）
        // 这里简化处理，实际可以根据更多用户特征计算
        
        diversity = activityDiversity;
        
        return Math.max(0.0, Math.min(1.0, diversity));
    }
    
    /**
     * 获取用户活跃度等级
     */
    private int getUserActivityLevel(Long userId) {
        // 综合考虑用户的餐厅访问数、评论数、关注数等
        int visitedCount = userRestaurantVisitMapper.getVisitedRestaurantsCount(userId);
        int followingCount = userFollowMapper.getFollowingCount(userId);
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 计算活跃度分数
        return visitedCount + followingCount + followersCount;
    }
    
    /**
     * 计算社交权重
     */
    private double calculateSocialWeight(Long userId) {
        // 获取用户关注数和粉丝数
        int followingCount = userFollowMapper.getFollowingCount(userId);
        int followersCount = userFollowMapper.getFollowersCount(userId);
        
        // 获取用户访问餐厅数量
        int visitedRestaurantsCount = userRestaurantVisitMapper.getVisitedRestaurantsCount(userId);
        
        // 计算活跃度分数
        double socialActivity = Math.log1p(followingCount + followersCount) / 10.0;
        double restaurantActivity = Math.log1p(visitedRestaurantsCount) / 10.0;
        
        return Math.min(socialActivity + restaurantActivity, 1.0);
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
     * 生成协同过滤推荐理由
     */
    private String generateRecommendationReason(Long targetUserId, Long recommendedUserId, 
                                         List<UserRestaurantVisit> commonVisits) {
        if (commonVisits.isEmpty()) {
            return "系统推荐";
        }
        
        // 统计餐厅类型
        Map<String, Long> restaurantTypes = commonVisits.stream()
            .collect(Collectors.groupingBy(
                visit -> getRestaurantType(visit.getRestaurantId()),
                Collectors.counting()
            ));
        
        // 找出最常见的餐厅类型
        String mostCommonType = restaurantTypes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("餐厅");
        
        // 获取餐厅名称
        List<String> restaurantNames = commonVisits.stream()
            .limit(3) // 最多显示3个餐厅
            .map(visit -> getRestaurantName(visit.getRestaurantId()))
            .collect(Collectors.toList());
        
        // 获取推荐用户名称
        User recommendedUser = userMapper.selectById(recommendedUserId);
        String recommendedUserName = recommendedUser != null ? 
            recommendedUser.getDisplayName() : "某用户";
        
        // 生成推荐理由
        StringBuilder reason = new StringBuilder();
        reason.append("您和").append(recommendedUserName).append("都喜欢");
        
        if (restaurantTypes.size() == 1) {
            reason.append(mostCommonType);
        } else {
            reason.append("相似的餐厅类型");
        }
        
        if (!restaurantNames.isEmpty()) {
            reason.append("，如").append(String.join("、", restaurantNames));
        }
        
        reason.append("，可能有相似的口味偏好");
        
        return reason.toString();
    }
    
    /**
     * 获取餐厅类型
     */
    private String getRestaurantType(Long restaurantId) {
        Restaurant restaurant = restaurantMapper.selectById(restaurantId);
        return restaurant != null ? restaurant.getType() : "餐厅";
    }
    
    /**
     * 获取餐厅名称
     */
    private String getRestaurantName(Long restaurantId) {
        Restaurant restaurant = restaurantMapper.selectById(restaurantId);
        return restaurant != null ? restaurant.getName() : "某餐厅";
    }
    
    /**
     * 转换为餐厅对象列表
     */
    private List<Restaurant> convertToRestaurants(List<UserRestaurantVisit> visits) {
        return visits.stream()
            .map(visit -> restaurantMapper.selectById(visit.getRestaurantId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.UserRecommendationScore;
import com.ljyh.tabletalk.entity.UserRecommendation;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.UserRecommendationMapper;
import com.ljyh.tabletalk.mapper.UserRecommendationMapper.UserRecommendationWithUserInfo;
import com.ljyh.tabletalk.mapper.UserRecommendationMapper.AlgorithmStats;
import com.ljyh.tabletalk.recommendation.HybridRecommendationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户推荐服务类
 * 整合各种推荐算法，为用户提供智能推荐
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRecommendationService {
    
    private final HybridRecommendationStrategy hybridRecommendationStrategy;
    private final UserRecommendationMapper userRecommendationMapper;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 获取用户推荐列表
     */
    public List<UserRecommendationScore> getUserRecommendations(Long userId, int limit, String algorithm) {
        log.info("获取用户 {} 的推荐列表，算法: {}, 限制: {}", userId, algorithm, limit);
        
        try {
            // 参数验证
            if (limit <= 0 || limit > 50) {
                throw new BusinessException("INVALID_LIMIT", "推荐数量限制应在1-50之间");
            }
            
            // 根据算法类型生成推荐
            HybridRecommendationStrategy.HybridStrategy strategy = parseStrategy(algorithm);
            List<UserRecommendationScore> recommendations = 
                hybridRecommendationStrategy.generateRecommendations(userId, limit, strategy);
            
            // 保存推荐结果到数据库
            saveRecommendations(userId, recommendations, algorithm);
            
            log.info("为用户 {} 生成了 {} 个推荐", userId, recommendations.size());
            return recommendations;
            
        } catch (Exception e) {
            log.error("获取用户 {} 推荐列表失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("RECOMMENDATION_FAILED", "获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记推荐状态
     */
    @Transactional
    public void markRecommendationStatus(Long userId, Long recommendationId, Boolean isInterested, String feedback) {
        log.info("标记推荐状态 - 用户ID: {}, 推荐ID: {}, 感兴趣: {}, 反馈: {}", 
                 userId, recommendationId, isInterested, feedback);
        
        try {
            // 查找推荐记录
            UserRecommendation recommendation = userRecommendationMapper.selectById(recommendationId);
            if (recommendation == null) {
                throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐记录不存在");
            }
            
            // 验证权限
            if (!recommendation.getUserId().equals(userId)) {
                throw new BusinessException("PERMISSION_DENIED", "无权限修改该推荐");
            }
            
            // 更新状态
            recommendation.setIsViewed(true);
            recommendation.setIsInterested(isInterested);
            recommendation.setFeedback(feedback);
            recommendation.setUpdatedAt(LocalDateTime.now());
            
            userRecommendationMapper.updateById(recommendation);
            
            // 清除相关缓存
            clearUserRecommendationCache(userId);
            
            log.info("推荐状态标记成功");
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("标记推荐状态失败: {}", e.getMessage(), e);
            throw new BusinessException("MARK_RECOMMENDATION_FAILED", "标记推荐状态失败");
        }
    }
    
    /**
     * 获取用户推荐统计信息
     */
    public RecommendationStats getUserRecommendationStats(Long userId) {
        log.info("获取用户 {} 的推荐统计信息", userId);
        
        try {
            // 获取总推荐数
            int totalRecommendations = userRecommendationMapper.countByUserId(userId);
            
            // 获取已查看数
            int viewedCount = userRecommendationMapper.countByUserIdAndViewed(userId, true);
            
            // 获取感兴趣数
            int interestedCount = userRecommendationMapper.countByUserIdAndInterested(userId, true);
            
            // 计算点击率和转化率
            double clickThroughRate = totalRecommendations > 0 ? 
                (double) viewedCount / totalRecommendations : 0.0;
            double conversionRate = viewedCount > 0 ? 
                (double) interestedCount / viewedCount : 0.0;
            
            RecommendationStats stats = RecommendationStats.builder()
                .totalRecommendations(totalRecommendations)
                .viewedCount(viewedCount)
                .interestedCount(interestedCount)
                .clickThroughRate(clickThroughRate)
                .conversionRate(conversionRate)
                .build();
            
            log.info("用户 {} 推荐统计信息: {}", userId, stats);
            return stats;
            
        } catch (Exception e) {
            log.error("获取用户 {} 推荐统计信息失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("GET_STATS_FAILED", "获取统计信息失败");
        }
    }
    
    /**
     * 清除过期推荐记录
     */
    @Transactional
    public void cleanExpiredRecommendations() {
        log.info("开始清除过期推荐记录");
        
        try {
            // 删除30天前的推荐记录
            LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
            int deletedCount = userRecommendationMapper.deleteByCreatedAtBefore(expireTime);
            
            log.info("清除了 {} 条过期推荐记录", deletedCount);
            
        } catch (Exception e) {
            log.error("清除过期推荐记录失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 解析推荐策略
     */
    private HybridRecommendationStrategy.HybridStrategy parseStrategy(String algorithm) {
        if (algorithm == null || algorithm.isEmpty()) {
            return HybridRecommendationStrategy.HybridStrategy.WEIGHTED;
        }
        
        try {
            return HybridRecommendationStrategy.HybridStrategy.valueOf(algorithm.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("未知的推荐算法: {}, 使用默认加权策略", algorithm);
            return HybridRecommendationStrategy.HybridStrategy.WEIGHTED;
        }
    }
    
    /**
     * 保存推荐结果到数据库
     */
    @Transactional
    private void saveRecommendations(Long userId, List<UserRecommendationScore> recommendations, String algorithm) {
        if (recommendations.isEmpty()) {
            return;
        }
        
        try {
            for (UserRecommendationScore score : recommendations) {
                // 检查是否已存在相同的推荐
                UserRecommendation existingRecommendation = userRecommendationMapper
                    .findByUserIdAndRecommendedUserIdAndAlgorithm(userId, score.getUserId(), algorithm);
                
                if (existingRecommendation == null) {
                    // 创建新推荐记录
                    UserRecommendation recommendation = new UserRecommendation();
                    recommendation.setUserId(userId);
                    recommendation.setRecommendedUserId(score.getUserId());
                    recommendation.setAlgorithmType(algorithm);
                    recommendation.setRecommendationScore(score.getScore());
                    recommendation.setRecommendationReason(score.getRecommendationReason());
                    recommendation.setIsViewed(false);
                    recommendation.setIsInterested(null);
                    
                    userRecommendationMapper.insert(recommendation);
                } else {
                    // 更新现有推荐记录
                    existingRecommendation.setRecommendationScore(score.getScore());
                    existingRecommendation.setRecommendationReason(score.getRecommendationReason());
                    existingRecommendation.setUpdatedAt(LocalDateTime.now());
                    
                    userRecommendationMapper.updateById(existingRecommendation);
                }
            }
            
            log.debug("保存了 {} 条推荐记录到数据库", recommendations.size());
            
        } catch (Exception e) {
            log.error("保存推荐记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响推荐结果返回
        }
    }
    
    /**
     * 清除用户推荐缓存
     */
    private void clearUserRecommendationCache(Long userId) {
        try {
            // 清除所有相关的推荐缓存
            String[] cachePatterns = {
                "collaborative_recommendations:" + userId + ":*",
                "social_recommendations:" + userId + ":*",
                "hybrid_recommendations:*:" + userId + ":*"
            };
            
            for (String pattern : cachePatterns) {
                redisTemplate.delete(redisTemplate.keys(pattern));
            }
            
            log.debug("清除了用户 {} 的推荐缓存", userId);
            
        } catch (Exception e) {
            log.warn("清除用户 {} 推荐缓存失败: {}", userId, e.getMessage());
        }
    }
    
    /**
     * 获取用户推荐列表（分页）
     */
    public List<UserRecommendationWithUserInfo> getUserRecommendationsWithPagination(Long userId, int page, int size) {
        log.info("获取用户 {} 的推荐列表，页码: {}, 每页大小: {}", userId, page, size);
        
        try {
            // 参数验证
            if (page < 0 || size <= 0 || size > 20) {
                throw new BusinessException("INVALID_PAGINATION", "页码和每页大小参数无效");
            }
            
            int offset = page * size;
            List<UserRecommendationWithUserInfo> recommendations =
                userRecommendationMapper.findByUserIdWithPagination(userId, offset, size);
            
            log.info("获取到用户 {} 的 {} 条推荐记录", userId, recommendations.size());
            return recommendations;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户 {} 推荐列表失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("GET_RECOMMENDATIONS_FAILED", "获取推荐列表失败");
        }
    }
    
    /**
     * 获取用户未查看的推荐列表
     */
    public List<UserRecommendationWithUserInfo> getUnviewedRecommendations(Long userId, int limit) {
        log.info("获取用户 {} 的未查看推荐列表，限制: {}", userId, limit);
        
        try {
            // 参数验证
            if (limit <= 0 || limit > 50) {
                throw new BusinessException("INVALID_LIMIT", "推荐数量限制应在1-50之间");
            }
            
            List<UserRecommendationWithUserInfo> recommendations =
                userRecommendationMapper.findUnviewedByUserId(userId, limit);
            
            log.info("获取到用户 {} 的 {} 条未查看推荐", userId, recommendations.size());
            return recommendations;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户 {} 未查看推荐失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("GET_UNVIEWED_FAILED", "获取未查看推荐失败");
        }
    }
    
    /**
     * 批量标记推荐为已查看
     */
    @Transactional
    public void batchMarkAsViewed(Long userId, List<Long> recommendationIds) {
        log.info("批量标记推荐为已查看 - 用户ID: {}, 推荐ID数量: {}", userId, recommendationIds.size());
        
        try {
            // 验证所有推荐记录都属于该用户
            for (Long recommendationId : recommendationIds) {
                UserRecommendation recommendation = userRecommendationMapper.selectById(recommendationId);
                if (recommendation == null) {
                    throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐记录不存在: " + recommendationId);
                }
                if (!recommendation.getUserId().equals(userId)) {
                    throw new BusinessException("PERMISSION_DENIED", "无权限修改推荐记录: " + recommendationId);
                }
            }
            
            // 批量更新
            int updatedCount = userRecommendationMapper.batchUpdateViewedStatus(recommendationIds, true);
            
            // 清除相关缓存
            clearUserRecommendationCache(userId);
            
            log.info("成功标记 {} 条推荐为已查看", updatedCount);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量标记推荐为已查看失败: {}", e.getMessage(), e);
            throw new BusinessException("BATCH_UPDATE_FAILED", "批量更新失败");
        }
    }
    
    /**
     * 获取用户推荐算法统计信息
     */
    public List<AlgorithmStats> getUserAlgorithmStats(Long userId) {
        log.info("获取用户 {} 的推荐算法统计信息", userId);
        
        try {
            List<AlgorithmStats> stats = userRecommendationMapper.getAlgorithmStatsByUserId(userId);
            log.info("获取到用户 {} 的 {} 条算法统计信息", userId, stats.size());
            return stats;
            
        } catch (Exception e) {
            log.error("获取用户 {} 算法统计信息失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("GET_ALGORITHM_STATS_FAILED", "获取算法统计信息失败");
        }
    }
    
    /**
     * 获取全局推荐算法统计信息
     */
    public List<AlgorithmStats> getGlobalAlgorithmStats(int days) {
        log.info("获取全局推荐算法统计信息，天数: {}", days);
        
        try {
            // 参数验证
            if (days <= 0 || days > 365) {
                throw new BusinessException("INVALID_DAYS", "天数应在1-365之间");
            }
            
            List<AlgorithmStats> stats = userRecommendationMapper.getGlobalAlgorithmStats(days);
            log.info("获取到全局 {} 条算法统计信息", stats.size());
            return stats;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取全局算法统计信息失败: {}", e.getMessage(), e);
            throw new BusinessException("GET_GLOBAL_STATS_FAILED", "获取全局统计信息失败");
        }
    }
    
    /**
     * 获取用户已推荐的推荐用户ID列表（用于避免重复推荐）
     */
    public List<Long> getRecentlyRecommendedUserIds(Long userId, int days) {
        log.info("获取用户 {} 最近 {} 天已推荐的推荐用户ID列表", userId, days);
        
        try {
            // 参数验证
            if (days <= 0 || days > 90) {
                throw new BusinessException("INVALID_DAYS", "天数应在1-90之间");
            }
            
            List<Long> recommendedUserIds = userRecommendationMapper.getRecommendedUserIds(userId, days);
            log.info("获取到用户 {} 最近 {} 天已推荐的 {} 个推荐用户", userId, days, recommendedUserIds.size());
            return recommendedUserIds;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户 {} 已推荐用户ID列表失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("GET_RECOMMENDED_USERS_FAILED", "获取已推荐用户列表失败");
        }
    }
    
    /**
     * 获取用户推荐详情
     */
    public UserRecommendationWithUserInfo getRecommendationDetail(Long userId, Long recommendationId) {
        log.info("获取用户 {} 的推荐详情，推荐ID: {}", userId, recommendationId);
        
        try {
            // 查找推荐记录
            UserRecommendation recommendation = userRecommendationMapper.selectById(recommendationId);
            if (recommendation == null) {
                throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐记录不存在");
            }
            
            // 验证权限
            if (!recommendation.getUserId().equals(userId)) {
                throw new BusinessException("PERMISSION_DENIED", "无权限查看该推荐");
            }
            
            // 获取带用户信息的推荐详情
            List<UserRecommendationWithUserInfo> recommendations =
                userRecommendationMapper.findByUserIdWithPagination(userId, 0, 1);
            
            if (!recommendations.isEmpty()) {
                return recommendations.get(0);
            } else {
                // 如果没有找到，创建一个基本的推荐详情
                UserRecommendationWithUserInfo detail = new UserRecommendationWithUserInfo();
                detail.setId(recommendation.getId());
                detail.setUserId(recommendation.getUserId());
                detail.setRecommendedUserId(recommendation.getRecommendedUserId());
                detail.setAlgorithmType(recommendation.getAlgorithmType());
                detail.setRecommendationScore(recommendation.getRecommendationScore());
                detail.setRecommendationReason(recommendation.getRecommendationReason());
                detail.setIsViewed(recommendation.getIsViewed());
                detail.setIsInterested(recommendation.getIsInterested());
                detail.setFeedback(recommendation.getFeedback());
                detail.setCreatedAt(recommendation.getCreatedAt());
                detail.setUpdatedAt(recommendation.getUpdatedAt());
                return detail;
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取推荐详情失败: {}", e.getMessage(), e);
            throw new BusinessException("GET_RECOMMENDATION_DETAIL_FAILED", "获取推荐详情失败");
        }
    }
    
    /**
     * 删除用户推荐记录
     */
    @Transactional
    public void deleteRecommendation(Long userId, Long recommendationId) {
        log.info("删除用户推荐记录 - 用户ID: {}, 推荐ID: {}", userId, recommendationId);
        
        try {
            // 查找推荐记录
            UserRecommendation recommendation = userRecommendationMapper.selectById(recommendationId);
            if (recommendation == null) {
                throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐记录不存在");
            }
            
            // 验证权限
            if (!recommendation.getUserId().equals(userId)) {
                throw new BusinessException("PERMISSION_DENIED", "无权限删除该推荐");
            }
            
            // 删除推荐记录
            userRecommendationMapper.deleteById(recommendationId);
            
            // 清除相关缓存
            clearUserRecommendationCache(userId);
            
            log.info("成功删除推荐记录: {}", recommendationId);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除推荐记录失败: {}", e.getMessage(), e);
            throw new BusinessException("DELETE_RECOMMENDATION_FAILED", "删除推荐记录失败");
        }
    }
    
    /**
     * 清除用户所有推荐记录
     */
    @Transactional
    public void clearAllUserRecommendations(Long userId) {
        log.info("清除用户 {} 的所有推荐记录", userId);
        
        try {
            // 验证用户存在
            userService.getUserById(userId);
            
            // 删除用户所有推荐记录
            int deletedCount = userRecommendationMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRecommendation>()
                    .eq("user_id", userId)
            );
            
            // 清除相关缓存
            clearUserRecommendationCache(userId);
            
            log.info("成功清除用户 {} 的 {} 条推荐记录", userId, deletedCount);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("清除用户 {} 所有推荐记录失败: {}", userId, e.getMessage(), e);
            throw new BusinessException("CLEAR_RECOMMENDATIONS_FAILED", "清除推荐记录失败");
        }
    }
    
    /**
     * 预热推荐缓存
     */
    public void warmupRecommendationCache(Long userId) {
        log.info("预热用户 {} 的推荐缓存", userId);
        
        try {
            // 验证用户存在
            userService.getUserById(userId);
            
            // 预热各种算法的推荐缓存
            HybridRecommendationStrategy.HybridStrategy[] strategies = {
                HybridRecommendationStrategy.HybridStrategy.WEIGHTED,
                HybridRecommendationStrategy.HybridStrategy.SWITCHING,
                HybridRecommendationStrategy.HybridStrategy.CASCADING
            };
            
            for (HybridRecommendationStrategy.HybridStrategy strategy : strategies) {
                try {
                    List<UserRecommendationScore> recommendations =
                        hybridRecommendationStrategy.generateRecommendations(userId, 10, strategy);
                    log.debug("预热策略 {} 的推荐缓存，生成 {} 个推荐", strategy.name(), recommendations.size());
                } catch (Exception e) {
                    log.warn("预热策略 {} 的推荐缓存失败: {}", strategy.name(), e.getMessage());
                }
            }
            
            log.info("完成用户 {} 的推荐缓存预热", userId);
            
        } catch (Exception e) {
            log.error("预热用户 {} 推荐缓存失败: {}", userId, e.getMessage(), e);
            // 不抛出异常，因为预热失败不影响主要功能
        }
    }
    
    /**
     * 推荐统计信息内部类
     */
    @lombok.Builder
    @lombok.Data
    public static class RecommendationStats {
        private int totalRecommendations;      // 总推荐数
        private int viewedCount;              // 已查看数
        private int interestedCount;           // 感兴趣数
        private double clickThroughRate;      // 点击率
        private double conversionRate;         // 转化率
    }
}
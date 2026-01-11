package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.dto.UserRecommendationRequest;
import com.ljyh.foodieconnect.entity.Restaurant;
import com.ljyh.foodieconnect.entity.UserRestaurantRecommendation;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.RestaurantMapper;
import com.ljyh.foodieconnect.mapper.UserRestaurantRecommendationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户推荐餐厅服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService extends ServiceImpl<UserRestaurantRecommendationMapper, UserRestaurantRecommendation> {
    
    private final UserRestaurantRecommendationMapper recommendationMapper;
    private final RestaurantMapper restaurantMapper;
    
    /**
     * 推荐餐厅
     */
    @Transactional
    public UserRestaurantRecommendation recommendRestaurant(Long userId, UserRecommendationRequest request) {
        // 检查餐厅是否存在
        Restaurant restaurant = restaurantMapper.selectById(request.getRestaurantId());
        if (restaurant == null) {
            throw new BusinessException("RESTAURANT_NOT_FOUND", "餐厅不存在");
        }
        
        // 检查是否已推荐该餐厅
        if (recommendationMapper.existsByUserIdAndRestaurantId(userId, request.getRestaurantId())) {
            throw new BusinessException("ALREADY_RECOMMENDED", "已经推荐过该餐厅");
        }
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setUserId(userId);
        recommendation.setRestaurantId(request.getRestaurantId());
        recommendation.setReason(request.getReason());
        recommendation.setRating(request.getRating());
        
        recommendationMapper.insert(recommendation);
        log.info("用户 {} 推荐了餐厅: {}", userId, request.getRestaurantId());
        
        return recommendation;
    }
    
    /**
     * 更新推荐
     */
    @Transactional
    public UserRestaurantRecommendation updateRecommendation(Long userId, Long recommendationId, UserRecommendationRequest request) {
        UserRestaurantRecommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐不存在");
        }
        
        // 检查是否是当前用户的推荐
        if (!recommendation.getUserId().equals(userId)) {
            throw new BusinessException("PERMISSION_DENIED", "无权限修改该推荐");
        }
        
        // 检查餐厅是否存在
        Restaurant restaurant = restaurantMapper.selectById(request.getRestaurantId());
        if (restaurant == null) {
            throw new BusinessException("RESTAURANT_NOT_FOUND", "餐厅不存在");
        }
        
        recommendation.setRestaurantId(request.getRestaurantId());
        recommendation.setReason(request.getReason());
        recommendation.setRating(request.getRating());
        
        recommendationMapper.updateById(recommendation);
        log.info("用户 {} 更新了餐厅推荐: {}", userId, request.getRestaurantId());
        
        return recommendation;
    }
    
    /**
     * 删除推荐
     */
    @Transactional
    public void deleteRecommendation(Long userId, Long recommendationId) {
        UserRestaurantRecommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new BusinessException("RECOMMENDATION_NOT_FOUND", "推荐不存在");
        }
        
        // 检查是否是当前用户的推荐
        if (!recommendation.getUserId().equals(userId)) {
            throw new BusinessException("PERMISSION_DENIED", "无权限删除该推荐");
        }
        
        recommendationMapper.deleteById(recommendationId);
        log.info("用户 {} 删除了餐厅推荐: {}", userId, recommendationId);
    }
    
    /**
     * 获取用户推荐餐厅列表
     */
    public Page<UserRestaurantRecommendation> getUserRecommendations(Long userId, int page, int size) {
        Page<UserRestaurantRecommendation> pageParam = new Page<>(page, size);
        return recommendationMapper.findByUserIdPage(pageParam, userId);
    }
    
    /**
     * 获取餐厅推荐列表
     */
    public Page<UserRestaurantRecommendation> getRestaurantRecommendations(Long restaurantId, int page, int size) {
        Page<UserRestaurantRecommendation> pageParam = new Page<>(page, size);
        return recommendationMapper.findByRestaurantIdPage(pageParam, restaurantId);
    }
    
    /**
     * 获取热门推荐餐厅
     */
    public List<UserRestaurantRecommendation> getPopularRecommendations(int limit) {
        return recommendationMapper.findPopularRecommendations(limit);
    }
    
    /**
     * 获取用户推荐餐厅的平均评分
     */
    public Double getUserAverageRating(Long userId) {
        return recommendationMapper.getAverageRatingByUserId(userId);
    }
    
    /**
     * 获取餐厅的平均用户评分
     */
    public Double getRestaurantAverageRating(Long restaurantId) {
        return recommendationMapper.getAverageRatingByRestaurantId(restaurantId);
    }
}
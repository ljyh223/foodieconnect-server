package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.Review;
import com.ljyh.tabletalk.entity.ReviewImage;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.ReviewImageMapper;
import com.ljyh.tabletalk.mapper.ReviewMapper;
import com.ljyh.tabletalk.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService extends ServiceImpl<ReviewMapper, Review> {
    
    private final ReviewMapper reviewMapper;
    private final RestaurantService restaurantService;
    private final ReviewImageMapper reviewImageMapper;
    private final UserMapper userMapper;
    
    /**
     * 发表评论
     */
    @Transactional
    public Review createReview(Long restaurantId, Long userId, Integer rating, String comment, List<String> imageUrls) {
        // 检查用户是否已评论过该餐厅
        if (reviewMapper.existsByRestaurantIdAndUserId(restaurantId, userId)) {
            throw new BusinessException("RESTAURANT_REVIEW_EXISTS", "您已对该餐厅发表过评论");
        }
        
        // 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new BusinessException("INVALID_RATING", "评分必须在1-5之间");
        }
        
        // 创建评论
        Review review = new Review();
        review.setRestaurantId(restaurantId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        
        reviewMapper.insert(review);
        log.info("用户 {} 对餐厅 {} 发表评论，评分: {}", userId, restaurantId, rating);
        
        // 保存评论图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            saveReviewImages(review.getId(), imageUrls);
        }
        
        // 查询并设置关联数据
        review = reviewMapper.selectById(review.getId());
        review.setImages(reviewImageMapper.selectByReviewId(review.getId()));
        
        // 查询并设置用户信息
        User user = userMapper.selectById(userId);
        if (user != null) {
            review.setUserName(user.getDisplayName());
            review.setUserAvatar(user.getAvatarUrl());
        }
        
        // 更新餐厅评分
        restaurantService.updateRestaurantRating(restaurantId);
        
        return review;
    }
    
    /**
     * 获取餐厅评论列表
     */
    public Page<Review> getRestaurantReviews(Long restaurantId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        return reviewMapper.findByRestaurantId(pageParam, restaurantId);
    }
    
    /**
     * 获取用户评论列表
     */
    public Page<Review> getUserReviews(Long userId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        return reviewMapper.findByUserId(pageParam, userId);
    }
    
    /**
     * 获取餐厅平均评分
     */
    public Double getRestaurantAverageRating(Long restaurantId) {
        return reviewMapper.calculateAverageRating(restaurantId);
    }
    
    /**
     * 获取餐厅评论数量
     */
    public Integer getRestaurantReviewCount(Long restaurantId) {
        return reviewMapper.countByRestaurantId(restaurantId);
    }
    
    /**
     * 根据ID获取评论
     */
    public Review getReviewById(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("REVIEW_NOT_FOUND", "评论不存在");
        }
        return review;
    }
    
    /**
     * 更新评论
     */
    @Transactional
    public Review updateReview(Long id, Long userId, Integer rating, String comment) {
        Review review = getReviewById(id);
        
        // 检查权限
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权修改此评论");
        }
        
        // 验证评分范围
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new BusinessException("INVALID_RATING", "评分必须在1-5之间");
        }
        
        // 更新评论
        if (rating != null) {
            review.setRating(rating);
        }
        if (comment != null) {
            review.setComment(comment);
        }
        
        reviewMapper.updateById(review);
        log.info("用户 {} 更新评论 {}", userId, id);
        
        // 更新餐厅评分
        restaurantService.updateRestaurantRating(review.getRestaurantId());
        
        return review;
    }
    
    /**
     * 删除评论
     */
    @Transactional
    public void deleteReview(Long id, Long userId) {
        Review review = getReviewById(id);
        
        // 检查权限
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权删除此评论");
        }
        
        Long restaurantId = review.getRestaurantId();
        reviewMapper.deleteById(id);
        log.info("用户 {} 删除评论 {}", userId, id);
        
        // 更新餐厅评分
        restaurantService.updateRestaurantRating(restaurantId);
    }
    
    /**
     * 计算餐厅平均评分
     */
    public Double calculateAverageRating(Long restaurantId) {
        return reviewMapper.calculateAverageRating(restaurantId);
    }
    
    /**
     * 统计餐厅评论数量
     */
    public Integer countReviewsByRestaurantId(Long restaurantId) {
        return reviewMapper.countByRestaurantId(restaurantId);
    }
    
    /**
     * 检查用户是否已评论过餐厅
     */
    public boolean hasUserReviewedRestaurant(Long restaurantId, Long userId) {
        return reviewMapper.existsByRestaurantIdAndUserId(restaurantId, userId);
    }
    
    /**
     * 保存评论图片
     */
    private void saveReviewImages(Long reviewId, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            ReviewImage reviewImage = new ReviewImage();
            reviewImage.setReviewId(reviewId);
            reviewImage.setImageUrl(imageUrls.get(i));
            reviewImage.setSortOrder(i + 1);
            reviewImageMapper.insert(reviewImage);
        }
        log.info("为评论 {} 保存了 {} 张图片", reviewId, imageUrls.size());
    }
}
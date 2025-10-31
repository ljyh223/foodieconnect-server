package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.Review;
import com.ljyh.tabletalk.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 评论控制器
 */
@Tag(name = "评论管理", description = "餐厅评论相关接口")
@RestController
@RequestMapping("/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Operation(summary = "获取餐厅评论列表", description = "分页获取指定餐厅的评论列表")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Review>>> getRestaurantReviews(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<Review> reviews = reviewService.getRestaurantReviews(restaurantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "发表评论", description = "对指定餐厅发表评论，支持上传多张图片")
    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "评分(1-5)") @RequestParam Integer rating,
            @Parameter(description = "评论内容") @RequestParam String comment,
            @Parameter(description = "图片URL列表，多个URL用逗号分隔") @RequestParam(required = false) String imageUrls) {
        
        // 处理图片URL列表
        List<String> imageUrlList = null;
        if (imageUrls != null && !imageUrls.trim().isEmpty()) {
            imageUrlList = Stream.of(imageUrls.split(","))
                    .map(String::trim)
                    .filter(url -> !url.isEmpty())
                    .toList();
        }
        
        Review review = reviewService.createReview(restaurantId, userId, rating, comment, imageUrlList);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
    
    @Operation(summary = "更新评论", description = "更新指定评论")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Review>> updateReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "评论ID") @PathVariable Long reviewId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "评分(1-5)") @RequestParam(required = false) Integer rating,
            @Parameter(description = "评论内容") @RequestParam(required = false) String comment) {
        
        Review review = reviewService.updateReview(reviewId, userId, rating, comment);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
    
    @Operation(summary = "删除评论", description = "删除指定评论")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "评论ID") @PathVariable Long reviewId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取评论详情", description = "根据ID获取评论详情")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Review>> getReviewById(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "评论ID") @PathVariable Long reviewId) {
        
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
    
    @Operation(summary = "获取用户评论", description = "获取指定用户的评论列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<Review>>> getUserReviews(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<Review> reviews = reviewService.getUserReviews(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "检查用户是否已评论", description = "检查用户是否已评论过指定餐厅")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUserReviewed(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        boolean hasReviewed = reviewService.hasUserReviewedRestaurant(restaurantId, userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("hasReviewed", hasReviewed)));
    }
    
    @Operation(summary = "获取餐厅评分统计", description = "获取餐厅的平均评分和评论数量")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurantStats(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {
        
        Double averageRating = reviewService.calculateAverageRating(restaurantId);
        Integer reviewCount = reviewService.countReviewsByRestaurantId(restaurantId);
        
        Map<String, Object> stats = Map.of(
            "averageRating", averageRating != null ? averageRating : 0.0,
            "reviewCount", reviewCount != null ? reviewCount : 0
        );
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
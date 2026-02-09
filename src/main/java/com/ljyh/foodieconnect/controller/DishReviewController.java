package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.DishReviewRequest;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.dto.DishReviewStatsResponse;
import com.ljyh.foodieconnect.entity.DishReview;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.service.DishReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户端菜品评价控制器
 */
@Tag(name = "用户菜品评价", description = "用户端菜品评价相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class DishReviewController {

    private final DishReviewService dishReviewService;
    private final UserMapper userMapper;

    @Operation(summary = "创建菜品评价", description = "用户对特定菜品进行评价")
    @PostMapping("/restaurants/{restaurantId}/menu-items/{itemId}/reviews")
    public ResponseEntity<ApiResponse<DishReviewResponse>> createReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Valid @RequestBody DishReviewRequest request) {

        // 从SecurityContext中获取当前登录用户的ID
        Long userId = getCurrentUserId();

        DishReviewResponse response = dishReviewService.createReview(itemId, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取菜品评价列表", description = "分页获取指定菜品的评价列表")
    @GetMapping("/restaurants/{restaurantId}/menu-items/{itemId}/reviews")
    public ResponseEntity<ApiResponse<Page<DishReviewResponse>>> getReviews(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "latest") String sortBy) {

        Page<DishReviewResponse> reviews = dishReviewService.getMenuItemReviews(itemId, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @Operation(summary = "获取用户的菜品评价", description = "获取当前用户在指定餐厅的所有菜品评价")
    @GetMapping("/restaurants/{restaurantId}/menu-items/reviews/my")
    public ResponseEntity<ApiResponse<Page<DishReviewResponse>>> getMyReviews(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "筛选菜品ID") @RequestParam(required = false) Long menuItemId) {

        Long userId = getCurrentUserId();
        Page<DishReviewResponse> reviews = dishReviewService.getUserReviews(userId, restaurantId, page, size, menuItemId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @Operation(summary = "更新菜品评价", description = "修改自己的评价")
    @PutMapping("/restaurants/{restaurantId}/menu-items/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<DishReviewResponse>> updateReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Valid @RequestBody DishReviewRequest request) {

        Long userId = getCurrentUserId();
        DishReviewResponse response = dishReviewService.updateReview(reviewId, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "删除菜品评价", description = "删除自己的评价")
    @DeleteMapping("/restaurants/{restaurantId}/menu-items/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {

        Long userId = getCurrentUserId();
        dishReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "检查是否已评价", description = "检查当前用户是否已评价该菜品")
    @GetMapping("/restaurants/{restaurantId}/menu-items/{itemId}/reviews/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserReviewed(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "菜品ID") @PathVariable Long itemId) {

        Long userId = getCurrentUserId();
        DishReview review = dishReviewService.checkUserReview(itemId, userId);

        Map<String, Object> result = new HashMap<>();
        if (review != null) {
            result.put("hasReviewed", true);
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("id", review.getId());
            reviewData.put("rating", review.getRating());
            reviewData.put("comment", review.getComment());
            result.put("review", reviewData);
        } else {
            result.put("hasReviewed", false);
            result.put("review", null);
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "获取菜品评分统计", description = "获取菜品的评分统计信息")
    @GetMapping("/restaurants/{restaurantId}/menu-items/{itemId}/reviews/stats")
    public ResponseEntity<ApiResponse<DishReviewStatsResponse>> getReviewStats(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "菜品ID") @PathVariable Long itemId) {

        DishReviewStatsResponse stats = dishReviewService.getReviewStats(itemId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "获取菜品评价详情", description = "获取单条评价的详细信息")
    @GetMapping("/restaurants/{restaurantId}/menu-items/{itemId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<DishReviewResponse>> getReviewById(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {

        DishReviewResponse response = dishReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.ljyh.foodieconnect.exception.BusinessException("UNAUTHORIZED", "请先登录");
        }

        String userEmail = authentication.getName();
        Optional<User> userOptional = userMapper.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new com.ljyh.foodieconnect.exception.BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        return userOptional.get().getId();
    }
}

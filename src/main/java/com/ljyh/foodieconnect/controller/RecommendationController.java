package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.UserRecommendationRequest;
import com.ljyh.foodieconnect.entity.UserRestaurantRecommendation;
import com.ljyh.foodieconnect.service.AuthService;
import com.ljyh.foodieconnect.service.RecommendationService;
import com.ljyh.foodieconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐餐厅控制器
 */
@Tag(name = "推荐餐厅管理", description = "用户推荐餐厅相关接口")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    private final AuthService authService;
    private final UserService userService;
    
    @Operation(summary = "推荐餐厅", description = "用户推荐餐厅")
    @PostMapping
    public ResponseEntity<ApiResponse<UserRestaurantRecommendation>> recommendRestaurant(
            @Valid @RequestBody UserRecommendationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        UserRestaurantRecommendation recommendation = recommendationService.recommendRestaurant(userId, request);
        return ResponseEntity.ok(ApiResponse.success(recommendation));
    }
    
    @Operation(summary = "更新推荐", description = "更新用户的餐厅推荐")
    @PutMapping("/{recommendationId}")
    public ResponseEntity<ApiResponse<UserRestaurantRecommendation>> updateRecommendation(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId,
            @Valid @RequestBody UserRecommendationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        UserRestaurantRecommendation recommendation = recommendationService.updateRecommendation(userId, recommendationId, request);
        return ResponseEntity.ok(ApiResponse.success(recommendation));
    }
    
    @Operation(summary = "删除推荐", description = "删除用户的餐厅推荐")
    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<ApiResponse<Void>> deleteRecommendation(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        recommendationService.deleteRecommendation(userId, recommendationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "获取我的推荐列表", description = "获取当前用户的推荐餐厅列表")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<UserRestaurantRecommendation>>> getMyRecommendations(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        Page<UserRestaurantRecommendation> recommendations = recommendationService.getUserRecommendations(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取用户推荐列表", description = "获取指定用户的推荐餐厅列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<UserRestaurantRecommendation>>> getUserRecommendations(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<UserRestaurantRecommendation> recommendations = recommendationService.getUserRecommendations(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取餐厅推荐列表", description = "获取指定餐厅的推荐列表")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<Page<UserRestaurantRecommendation>>> getRestaurantRecommendations(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<UserRestaurantRecommendation> recommendations = recommendationService.getRestaurantRecommendations(restaurantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取热门推荐", description = "获取热门推荐餐厅列表")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<UserRestaurantRecommendation>>> getPopularRecommendations(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        
        List<UserRestaurantRecommendation> recommendations = recommendationService.getPopularRecommendations(limit);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取用户平均评分", description = "获取当前用户的推荐餐厅平均评分")
    @GetMapping("/my-average-rating")
    public ResponseEntity<ApiResponse<Double>> getMyAverageRating(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        Double averageRating = recommendationService.getUserAverageRating(userId);
        return ResponseEntity.ok(ApiResponse.success(averageRating));
    }
    
    @Operation(summary = "获取餐厅平均评分", description = "获取指定餐厅的用户平均评分")
    @GetMapping("/restaurant/{restaurantId}/average-rating")
    public ResponseEntity<ApiResponse<Double>> getRestaurantAverageRating(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {
        
        Double averageRating = recommendationService.getRestaurantAverageRating(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(averageRating));
    }
}
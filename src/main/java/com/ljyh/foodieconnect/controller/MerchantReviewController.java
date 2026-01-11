package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.entity.Review;
import com.ljyh.foodieconnect.entity.StaffReview;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.ReviewService;
import com.ljyh.foodieconnect.service.StaffReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家评价控制器
 */
@Tag(name = "商家评价", description = "商家端评价相关接口")
@RestController
@RequestMapping("/merchant/reviews")
@RequiredArgsConstructor
public class MerchantReviewController {
    
    private final ReviewService reviewService;
    private final StaffReviewService staffReviewService;
    private final MerchantAuthService merchantAuthService;
    
    @Operation(summary = "获取评价概览", description = "获取餐厅和店员评价的概览信息")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviewOverview() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        
        // 获取餐厅评价统计
        Double restaurantAvgRating = reviewService.getRestaurantAverageRating(restaurantId);
        Integer restaurantReviewCount = reviewService.getRestaurantReviewCount(restaurantId);
        
        // 获取餐厅最新评价
        Page<Review> latestRestaurantReviews = reviewService.getRestaurantReviews(restaurantId, 0, 5);
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("restaurant", Map.of(
            "averageRating", restaurantAvgRating,
            "reviewCount", restaurantReviewCount,
            "latestReviews", latestRestaurantReviews.getRecords()
        ));
        
        return ResponseEntity.ok(ApiResponse.success(overview));
    }
    
    @Operation(summary = "获取餐厅评价列表", description = "分页获取餐厅的评价列表")
    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<Page<Review>>> getRestaurantReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        Page<Review> reviews = reviewService.getRestaurantReviews(restaurantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "获取店员评价列表", description = "分页获取指定店员的评价列表")
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<Page<StaffReview>>> getStaffReviews(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<StaffReview> reviews = staffReviewService.getStaffReviews(staffId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
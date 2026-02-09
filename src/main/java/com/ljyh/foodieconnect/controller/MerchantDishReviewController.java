package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.service.DishReviewService;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商家端菜品评价控制器
 */
@Tag(name = "商家菜品评价", description = "商家端菜品评价相关接口")
@RestController
@RequestMapping("/merchant/menu-items")
@RequiredArgsConstructor
public class MerchantDishReviewController {

    private final DishReviewService dishReviewService;
    private final MerchantAuthService merchantAuthService;

    @Operation(summary = "获取菜品评价列表", description = "商家查看自己餐厅某菜品的评价")
    @GetMapping("/{itemId}/reviews")
    public ResponseEntity<ApiResponse<Page<DishReviewResponse>>> getItemReviews(
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "评分筛选") @RequestParam(required = false) Integer rating) {

        // 获取商家当前餐厅ID
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();

        Page<DishReviewResponse> reviews = dishReviewService.getMerchantItemReviews(restaurantId, itemId, page, size, rating);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @Operation(summary = "获取评价概览", description = "获取商家所有菜品的评价概览")
    @GetMapping("/reviews/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviewOverview() {

        // 获取商家当前餐厅ID
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();

        Map<String, Object> overview = dishReviewService.getMerchantReviewOverview(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @Operation(summary = "获取所有菜品评价", description = "商家查看自己餐厅所有菜品的评价")
    @GetMapping("/reviews/all")
    public ResponseEntity<ApiResponse<Page<DishReviewResponse>>> getAllReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "评分筛选") @RequestParam(required = false) Integer rating) {

        // 获取商家当前餐厅ID
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();

        // itemId为null表示查询所有菜品的评价
        Page<DishReviewResponse> reviews = dishReviewService.getMerchantItemReviews(restaurantId, null, page, size, rating);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}

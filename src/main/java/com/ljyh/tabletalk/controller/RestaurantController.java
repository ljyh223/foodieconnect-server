package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.Restaurant;
import com.ljyh.tabletalk.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 餐厅控制器
 */
@Tag(name = "餐厅管理", description = "餐厅信息相关接口")
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    
    @Operation(summary = "获取餐厅列表", description = "分页获取餐厅列表，支持按类型和关键词筛选")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Restaurant>>> getRestaurants(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "餐厅类型") @RequestParam(required = false) String type,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        Page<Restaurant> restaurants = restaurantService.getRestaurants(page, size, type, keyword);
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }
    
    @Operation(summary = "获取餐厅详情", description = "根据ID获取餐厅详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurantDetail(
            @Parameter(description = "餐厅ID") @PathVariable Long id) {
        
        Map<String, Object> restaurantDetail = restaurantService.getRestaurantDetail(id);
        return ResponseEntity.ok(ApiResponse.success(restaurantDetail));
    }
    
    @Operation(summary = "搜索餐厅", description = "根据关键词搜索餐厅")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Restaurant>>> searchRestaurants(
            @Parameter(description = "搜索关键词") @RequestParam String q,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<Restaurant> restaurants = restaurantService.searchRestaurants(q, page, size);
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }
    
    @Operation(summary = "按类型获取餐厅", description = "根据餐厅类型获取餐厅列表")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<Restaurant>>> getRestaurantsByType(
            @Parameter(description = "餐厅类型") @PathVariable String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<Restaurant> restaurants = restaurantService.getRestaurantsByType(type, page, size);
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }
    
    @Operation(summary = "获取热门餐厅", description = "获取评分最高的热门餐厅")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Restaurant>>> getPopularRestaurants(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        
        List<Restaurant> restaurants = restaurantService.getPopularRestaurants(limit);
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }
    
    @Operation(summary = "获取所有餐厅类型", description = "获取所有可用的餐厅类型")
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getAllRestaurantTypes() {
        List<String> types = restaurantService.getAllRestaurantTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }
    
    @Operation(summary = "按评分范围获取餐厅", description = "根据评分范围获取餐厅列表")
    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<Page<Restaurant>>> getRestaurantsByRatingRange(
            @Parameter(description = "最低评分") @RequestParam Double minRating,
            @Parameter(description = "最高评分") @RequestParam Double maxRating,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<Restaurant> restaurants = restaurantService.getRestaurantsByRatingRange(minRating, maxRating, page, size);
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }
}
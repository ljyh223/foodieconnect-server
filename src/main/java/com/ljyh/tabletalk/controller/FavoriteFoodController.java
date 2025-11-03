package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.FavoriteFoodRequest;
import com.ljyh.tabletalk.entity.UserFavoriteFood;
import com.ljyh.tabletalk.service.AuthService;
import com.ljyh.tabletalk.service.FavoriteFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 喜好食物控制器
 */
@Tag(name = "喜好食物管理", description = "用户喜好食物相关接口")
@RestController
@RequestMapping("/api/users/favorite-foods")
@RequiredArgsConstructor
public class FavoriteFoodController {
    
    private final FavoriteFoodService favoriteFoodService;
    private final AuthService authService;
    
    @Operation(summary = "添加喜好食物", description = "为当前用户添加喜好食物")
    @PostMapping
    public ResponseEntity<ApiResponse<UserFavoriteFood>> addFavoriteFood(
            @Valid @RequestBody FavoriteFoodRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = authService.getCurrentUser(email).getId();
        UserFavoriteFood favoriteFood = favoriteFoodService.addFavoriteFood(userId, request);
        return ResponseEntity.ok(ApiResponse.success(favoriteFood));
    }
    
    @Operation(summary = "删除喜好食物", description = "删除用户的喜好食物")
    @DeleteMapping("/{foodId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavoriteFood(
            @Parameter(description = "喜好食物ID") @PathVariable Long foodId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = authService.getCurrentUser(email).getId();
        favoriteFoodService.deleteFavoriteFood(userId, foodId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "获取用户喜好食物列表", description = "获取当前用户的喜好食物列表")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserFavoriteFood>>> getUserFavoriteFoods(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = authService.getCurrentUser(email).getId();
        Page<UserFavoriteFood> favoriteFoods = favoriteFoodService.getUserFavoriteFoodsPage(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(favoriteFoods));
    }
    
    @Operation(summary = "根据食物类型获取喜好食物", description = "根据食物类型获取当前用户的喜好食物列表")
    @GetMapping("/type/{foodType}")
    public ResponseEntity<ApiResponse<Page<UserFavoriteFood>>> getUserFavoriteFoodsByType(
            @Parameter(description = "食物类型") @PathVariable String foodType,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = authService.getCurrentUser(email).getId();
        Page<UserFavoriteFood> favoriteFoods = favoriteFoodService.getUserFavoriteFoodsByTypePage(userId, foodType, page, size);
        return ResponseEntity.ok(ApiResponse.success(favoriteFoods));
    }
}
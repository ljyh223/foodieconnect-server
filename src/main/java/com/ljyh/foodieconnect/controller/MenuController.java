package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.entity.MenuItem;
import com.ljyh.foodieconnect.entity.MenuCategory;
import com.ljyh.foodieconnect.service.MenuItemService;
import com.ljyh.foodieconnect.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品控制器（用户端）
 */
@Tag(name = "菜品管理", description = "用户端菜品相关接口")
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;
    private final MenuCategoryService menuCategoryService;

    @Operation(summary = "获取餐厅菜品列表", description = "分页获取指定餐厅的菜品列表")
    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<ApiResponse<Page<MenuItem>>> getRestaurantMenuItems(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {

        Page<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurantForUser(restaurantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @Operation(summary = "获取菜品详情", description = "根据ID获取菜品详细信息")
    @GetMapping("/menu-items/{id}")
    public ResponseEntity<ApiResponse<MenuItem>> getMenuItemDetail(
            @Parameter(description = "菜品ID") @PathVariable Long id) {

        MenuItem menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(ApiResponse.success(menuItem));
    }

    @Operation(summary = "搜索菜品", description = "全局搜索菜品")
    @GetMapping("/menu-items/search")
    public ResponseEntity<ApiResponse<List<MenuItem>>> searchMenuItems(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {

        List<MenuItem> menuItems = menuItemService.searchMenuItemsGlobally(keyword);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @Operation(summary = "获取推荐菜品", description = "获取指定餐厅的推荐菜品")
    @GetMapping("/restaurants/{restaurantId}/menu-items/recommended")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getRecommendedMenuItems(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {

        List<MenuItem> menuItems = menuItemService.getMenuItemMapper().findRecommendedByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @Operation(summary = "按分类获取菜品", description = "获取指定餐厅指定分类的菜品")
    @GetMapping("/restaurants/{restaurantId}/menu-items/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItemsByCategory(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId,
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {

        List<MenuItem> menuItems = menuItemService.getMenuItemMapper()
                .findByRestaurantIdAndCategoryId(restaurantId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @Operation(summary = "获取餐厅分类列表", description = "获取指定餐厅的所有菜品分类")
    @GetMapping("/restaurants/{restaurantId}/menu-categories")
    public ResponseEntity<ApiResponse<List<MenuCategory>>> getRestaurantCategories(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {

        List<MenuCategory> categories = menuCategoryService.getCategoriesByRestaurantForUser(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}

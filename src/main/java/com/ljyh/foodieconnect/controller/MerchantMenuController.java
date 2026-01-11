package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.MenuCategoryRequest;
import com.ljyh.foodieconnect.dto.MenuItemRequest;
import com.ljyh.foodieconnect.entity.MenuCategory;
import com.ljyh.foodieconnect.entity.MenuItem;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.MenuCategoryService;
import com.ljyh.foodieconnect.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家菜单管理控制器
 */
@Tag(name = "商家菜单管理", description = "商家端菜单分类和菜品管理接口")
@RestController
@RequestMapping("/merchant/menu")
@RequiredArgsConstructor
public class MerchantMenuController {
    
    private final MenuCategoryService menuCategoryService;
    private final MenuItemService menuItemService;
    private final MerchantAuthService merchantAuthService;
    
    // ==================== 菜单分类管理 ====================
    
    @Operation(summary = "获取分类列表", description = "获取当前餐厅的所有菜单分类")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<MenuCategory>>> getCategories() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuCategory> categories = menuCategoryService.getCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @Operation(summary = "获取所有分类", description = "获取当前餐厅的所有菜单分类（包括禁用的）")
    @GetMapping("/categories/all")
    public ResponseEntity<ApiResponse<List<MenuCategory>>> getAllCategories() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuCategory> categories = menuCategoryService.getAllCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @Operation(summary = "创建分类", description = "创建新的菜单分类")
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<MenuCategory>> createCategory(
            @Valid @RequestBody MenuCategoryRequest request) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        MenuCategory category = menuCategoryService.createCategory(restaurantId, request);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @Operation(summary = "更新分类", description = "更新指定的菜单分类")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<MenuCategory>> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Valid @RequestBody MenuCategoryRequest request) {
        
        MenuCategory category = menuCategoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @Operation(summary = "删除分类", description = "删除指定的菜单分类")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        
        menuCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "切换分类状态", description = "启用或禁用菜单分类")
    @PutMapping("/categories/{categoryId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "是否启用") @RequestParam Boolean isActive) {
        
        menuCategoryService.toggleCategoryStatus(categoryId, isActive);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    // ==================== 菜品管理 ====================
    
    @Operation(summary = "获取菜品列表", description = "获取当前餐厅的所有菜品")
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItems() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuItem> items = menuItemService.getMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "获取所有菜品", description = "获取当前餐厅的所有菜品（包括不可用的）")
    @GetMapping("/items/all")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getAllMenuItems() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuItem> items = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "根据分类获取菜品", description = "获取指定分类下的所有菜品")
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItemsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuItem> items = menuItemService.getMenuItemsByCategory(restaurantId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "获取推荐菜品", description = "获取当前餐厅的推荐菜品")
    @GetMapping("/items/recommended")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getRecommendedMenuItems() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuItem> items = menuItemService.getRecommendedMenuItems(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "分页获取菜品", description = "分页获取当前餐厅的菜品")
    @GetMapping("/items/page")
    public ResponseEntity<ApiResponse<Page<MenuItem>>> getMenuItemsPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        Page<MenuItem> items = menuItemService.getMenuItemsPage(restaurantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "搜索菜品", description = "根据关键词搜索菜品")
    @GetMapping("/items/search")
    public ResponseEntity<ApiResponse<List<MenuItem>>> searchMenuItems(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MenuItem> items = menuItemService.searchMenuItems(restaurantId, keyword);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @Operation(summary = "创建菜品", description = "创建新的菜品")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<MenuItem>> createMenuItem(
            @Valid @RequestBody MenuItemRequest request) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        MenuItem item = menuItemService.createMenuItem(restaurantId, request);
        return ResponseEntity.ok(ApiResponse.success(item));
    }
    
    @Operation(summary = "更新菜品", description = "更新指定的菜品信息")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItem(
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequest request) {
        
        MenuItem item = menuItemService.updateMenuItem(itemId, request);
        return ResponseEntity.ok(ApiResponse.success(item));
    }
    
    @Operation(summary = "删除菜品", description = "删除指定的菜品")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @Parameter(description = "菜品ID") @PathVariable Long itemId) {
        
        menuItemService.deleteMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "切换菜品状态", description = "启用或禁用菜品")
    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleMenuItemStatus(
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Parameter(description = "是否可用") @RequestParam Boolean isAvailable) {
        
        menuItemService.toggleMenuItemStatus(itemId, isAvailable);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "设置推荐菜品", description = "设置或取消菜品的推荐状态")
    @PutMapping("/items/{itemId}/recommended")
    public ResponseEntity<ApiResponse<Void>> toggleRecommendedStatus(
            @Parameter(description = "菜品ID") @PathVariable Long itemId,
            @Parameter(description = "是否推荐") @RequestParam Boolean isRecommended) {
        
        menuItemService.toggleRecommendedStatus(itemId, isRecommended);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
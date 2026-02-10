package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.dto.MenuItemRequest;
import com.ljyh.foodieconnect.entity.MenuItem;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 菜单项服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemService extends ServiceImpl<MenuItemMapper, MenuItem> {
    
    private final MenuItemMapper menuItemMapper;
    private final MerchantAuthService merchantAuthService;
    
    /**
     * 获取餐厅的所有菜品
     */
    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        return menuItemMapper.findByRestaurantId(restaurantId);
    }
    
    /**
     * 获取餐厅的所有菜品（包括不可用的）
     */
    public List<MenuItem> getAllMenuItemsByRestaurant(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.STAFF);
        
        return menuItemMapper.findAllByRestaurantId(restaurantId);
    }
    
    /**
     * 根据分类获取菜品
     */
    public List<MenuItem> getMenuItemsByCategory(Long restaurantId, Long categoryId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        return menuItemMapper.findByRestaurantIdAndCategoryId(restaurantId, categoryId);
    }
    
    /**
     * 获取推荐菜品
     */
    public List<MenuItem> getRecommendedMenuItems(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        return menuItemMapper.findRecommendedByRestaurantId(restaurantId);
    }
    
    /**
     * 分页获取菜品
     */
    public Page<MenuItem> getMenuItemsPage(Long restaurantId, int page, int size) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        Page<MenuItem> pageParam = new Page<>(page, size);
        return menuItemMapper.findByRestaurantIdPage(pageParam, restaurantId);
    }
    
    /**
     * 搜索菜品
     */
    public List<MenuItem> searchMenuItems(Long restaurantId, String keyword) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        return menuItemMapper.searchByKeyword(restaurantId, keyword);
    }
    
    /**
     * 创建菜品
     */
    @Transactional
    public MenuItem createMenuItem(Long restaurantId, MenuItemRequest request) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 检查菜品名称是否已存在
        MenuItem existingItem = menuItemMapper.findByRestaurantIdAndName(restaurantId, request.getName());
        if (existingItem != null) {
            throw new BusinessException("MENU_ITEM_NAME_EXISTS", "菜品名称已存在");
        }
        
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurantId(restaurantId);
        menuItem.setCategoryId(request.getCategoryId());
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setOriginalPrice(request.getOriginalPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        menuItem.setIsRecommended(request.getIsRecommended() != null ? request.getIsRecommended() : false);
        menuItem.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 
                             menuItemMapper.getMaxSortOrder(restaurantId, request.getCategoryId()) + 1);
        menuItem.setNutritionInfo(request.getNutritionInfo());
        menuItem.setAllergenInfo(request.getAllergenInfo());
        menuItem.setSpiceLevel(request.getSpiceLevel());
        menuItem.setPreparationTime(request.getPreparationTime());
        menuItem.setCalories(request.getCalories());
        
        menuItemMapper.insert(menuItem);
        log.info("创建菜品成功: {}", menuItem.getName());
        
        return menuItem;
    }
    
    /**
     * 更新菜品
     */
    @Transactional
    public MenuItem updateMenuItem(Long itemId, MenuItemRequest request) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(menuItem.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 检查菜品名称是否已存在（排除当前菜品）
        MenuItem existingItem = menuItemMapper.findByRestaurantIdAndName(
            menuItem.getRestaurantId(), request.getName());
        if (existingItem != null && !existingItem.getId().equals(itemId)) {
            throw new BusinessException("MENU_ITEM_NAME_EXISTS", "菜品名称已存在");
        }
        
        menuItem.setCategoryId(request.getCategoryId());
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setOriginalPrice(request.getOriginalPrice());
        menuItem.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) {
            menuItem.setIsAvailable(request.getIsAvailable());
        }
        if (request.getIsRecommended() != null) {
            menuItem.setIsRecommended(request.getIsRecommended());
        }
        if (request.getSortOrder() != null) {
            menuItem.setSortOrder(request.getSortOrder());
        }
        menuItem.setNutritionInfo(request.getNutritionInfo());
        menuItem.setAllergenInfo(request.getAllergenInfo());
        menuItem.setSpiceLevel(request.getSpiceLevel());
        menuItem.setPreparationTime(request.getPreparationTime());
        menuItem.setCalories(request.getCalories());
        
        menuItemMapper.updateById(menuItem);
        log.info("更新菜品成功: {}", menuItem.getName());
        
        return menuItem;
    }
    
    /**
     * 删除菜品
     */
    @Transactional
    public void deleteMenuItem(Long itemId) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(menuItem.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        menuItemMapper.deleteById(itemId);
        log.info("删除菜品成功: {}", menuItem.getName());
    }
    
    /**
     * 启用/禁用菜品
     */
    @Transactional
    public void toggleMenuItemStatus(Long itemId, Boolean isAvailable) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(menuItem.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        menuItem.setIsAvailable(isAvailable);
        menuItemMapper.updateById(menuItem);
        
        log.info("{}菜品: {}", isAvailable ? "启用" : "禁用", menuItem.getName());
    }
    
    /**
     * 设置推荐菜品
     */
    @Transactional
    public void toggleRecommendedStatus(Long itemId, Boolean isRecommended) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(menuItem.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        menuItem.setIsRecommended(isRecommended);
        menuItemMapper.updateById(menuItem);
        
        log.info("{}推荐菜品: {}", isRecommended ? "设置" : "取消", menuItem.getName());
    }

    /**
     * 更新菜品评分
     *
     * @param menuItemId 菜品ID
     */
    @Transactional
    public void updateMenuItemRating(Long menuItemId) {
        // 获取菜品信息
        MenuItem menuItem = menuItemMapper.selectById(menuItemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }

        // 计算平均评分和评价数量（使用原生SQL查询，避免循环依赖）
        Double averageRating = menuItemMapper.calculateAverageRating(menuItemId);
        Integer reviewCount = menuItemMapper.countReviews(menuItemId);

        // 更新菜品信息
        menuItem.setRating(averageRating != null ? BigDecimal.valueOf(averageRating) : BigDecimal.ZERO);
        menuItem.setReviewCount(reviewCount != null ? reviewCount : 0);

        menuItemMapper.updateById(menuItem);
        log.info("更新菜品评分: {} -> 评分: {}, 评价数: {}", menuItem.getName(), menuItem.getRating(), menuItem.getReviewCount());
    }

    // ========== 商家端方法（需要验证菜品归属） ==========

    /**
     * 获取菜品详情（商家端）
     * 验证菜品是否属于指定商家的餐厅
     */
    public MenuItem getMenuItemByIdForMerchant(Long itemId, Long restaurantId) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        // 验证菜品是否属于该商家的餐厅
        if (!menuItem.getRestaurantId().equals(restaurantId)) {
            throw new BusinessException("MENU_ITEM_NOT_BELONG_TO_RESTAURANT", "该菜品不属于您的餐厅");
        }
        return menuItem;
    }

    // ========== 用户端方法（不需要商家认证） ==========

    /**
     * 获取菜品详情（用户端）
     */
    public MenuItem getMenuItemById(Long itemId) {
        MenuItem menuItem = menuItemMapper.selectById(itemId);
        if (menuItem == null) {
            throw new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在");
        }
        return menuItem;
    }

    /**
     * 获取餐厅菜品列表（用户端，分页）
     */
    public Page<MenuItem> getMenuItemsByRestaurantForUser(Long restaurantId, int page, int size) {
        Page<MenuItem> pageParam = new Page<>(page, size);
        return menuItemMapper.findByRestaurantIdPage(pageParam, restaurantId);
    }

    /**
     * 全局搜索菜品（用户端）
     */
    public List<MenuItem> searchMenuItemsGlobally(String keyword) {
        return menuItemMapper.globalSearch(keyword);
    }

    /**
     * 全局搜索菜品（用户端，分页）
     */
    public Page<MenuItem> searchMenuItemsGloballyPage(String keyword, int page, int size) {
        Page<MenuItem> pageParam = new Page<>(page, size);
        return menuItemMapper.globalSearchPage(pageParam, keyword);
    }

    /**
     * 获取MenuItemMapper（供控制器使用）
     */
    public MenuItemMapper getMenuItemMapper() {
        return menuItemMapper;
    }
}
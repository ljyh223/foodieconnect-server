package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.dto.MenuCategoryRequest;
import com.ljyh.tabletalk.entity.MenuCategory;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.MenuCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单分类服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuCategoryService extends ServiceImpl<MenuCategoryMapper, MenuCategory> {
    
    private final MenuCategoryMapper menuCategoryMapper;
    private final MerchantAuthService merchantAuthService;
    
    /**
     * 获取餐厅的所有分类
     */
    public List<MenuCategory> getCategoriesByRestaurant(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        
        return menuCategoryMapper.findByRestaurantId(restaurantId);
    }
    
    /**
     * 获取餐厅的所有分类（包括禁用的）
     */
    public List<MenuCategory> getAllCategoriesByRestaurant(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.STAFF);
        
        return menuCategoryMapper.findAllByRestaurantId(restaurantId);
    }
    
    /**
     * 创建分类
     */
    @Transactional
    public MenuCategory createCategory(Long restaurantId, MenuCategoryRequest request) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 检查分类名称是否已存在
        MenuCategory existingCategory = menuCategoryMapper.findByRestaurantIdAndName(restaurantId, request.getName());
        if (existingCategory != null) {
            throw new BusinessException("CATEGORY_NAME_EXISTS", "分类名称已存在");
        }
        
        MenuCategory category = new MenuCategory();
        category.setRestaurantId(restaurantId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 
                           menuCategoryMapper.getMaxSortOrder(restaurantId) + 1);
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        menuCategoryMapper.insert(category);
        log.info("创建菜单分类成功: {}", category.getName());
        
        return category;
    }
    
    /**
     * 更新分类
     */
    @Transactional
    public MenuCategory updateCategory(Long categoryId, MenuCategoryRequest request) {
        MenuCategory category = menuCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("CATEGORY_NOT_FOUND", "分类不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(category.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 检查分类名称是否已存在（排除当前分类）
        MenuCategory existingCategory = menuCategoryMapper.findByRestaurantIdAndName(
            category.getRestaurantId(), request.getName());
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            throw new BusinessException("CATEGORY_NAME_EXISTS", "分类名称已存在");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        
        menuCategoryMapper.updateById(category);
        log.info("更新菜单分类成功: {}", category.getName());
        
        return category;
    }
    
    /**
     * 删除分类
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        MenuCategory category = menuCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("CATEGORY_NOT_FOUND", "分类不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(category.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        menuCategoryMapper.deleteById(categoryId);
        log.info("删除菜单分类成功: {}", category.getName());
    }
    
    /**
     * 启用/禁用分类
     */
    @Transactional
    public void toggleCategoryStatus(Long categoryId, Boolean isActive) {
        MenuCategory category = menuCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("CATEGORY_NOT_FOUND", "分类不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(category.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        category.setIsActive(isActive);
        menuCategoryMapper.updateById(category);
        
        log.info("{}菜单分类: {}", isActive ? "启用" : "禁用", category.getName());
    }
}
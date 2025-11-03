package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.dto.FavoriteFoodRequest;
import com.ljyh.tabletalk.entity.UserFavoriteFood;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.UserFavoriteFoodMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户喜好食物服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteFoodService extends ServiceImpl<UserFavoriteFoodMapper, UserFavoriteFood> {
    
    private final UserFavoriteFoodMapper userFavoriteFoodMapper;
    
    /**
     * 添加喜好食物
     */
    @Transactional
    public UserFavoriteFood addFavoriteFood(Long userId, FavoriteFoodRequest request) {
        // 检查是否已添加该喜好食物
        if (userFavoriteFoodMapper.existsByUserIdAndFoodName(userId, request.getFoodName())) {
            throw new BusinessException("FAVORITE_FOOD_EXISTS", "该喜好食物已存在");
        }
        
        UserFavoriteFood favoriteFood = new UserFavoriteFood();
        favoriteFood.setUserId(userId);
        favoriteFood.setFoodName(request.getFoodName());
        favoriteFood.setFoodType(request.getFoodType());
        
        userFavoriteFoodMapper.insert(favoriteFood);
        log.info("用户 {} 添加喜好食物: {}", userId, request.getFoodName());
        
        return favoriteFood;
    }
    
    /**
     * 删除喜好食物
     */
    @Transactional
    public void deleteFavoriteFood(Long userId, Long foodId) {
        int result = userFavoriteFoodMapper.deleteByUserIdAndFoodId(userId, foodId);
        if (result == 0) {
            throw new BusinessException("FAVORITE_FOOD_NOT_FOUND", "喜好食物不存在");
        }
        
        log.info("用户 {} 删除喜好食物: {}", userId, foodId);
    }
    
    /**
     * 获取用户喜好食物列表
     */
    public List<UserFavoriteFood> getUserFavoriteFoods(Long userId) {
        return userFavoriteFoodMapper.findByUserId(userId);
    }
    
    /**
     * 分页获取用户喜好食物列表
     */
    public Page<UserFavoriteFood> getUserFavoriteFoodsPage(Long userId, int page, int size) {
        Page<UserFavoriteFood> pageParam = new Page<>(page, size);
        return userFavoriteFoodMapper.findByUserIdPage(pageParam, userId);
    }
    
    /**
     * 根据食物类型获取用户喜好食物列表
     */
    public List<UserFavoriteFood> getUserFavoriteFoodsByType(Long userId, String foodType) {
        return userFavoriteFoodMapper.findByUserIdAndFoodType(userId, foodType);
    }
    
    /**
     * 根据食物类型分页获取用户喜好食物列表
     */
    public Page<UserFavoriteFood> getUserFavoriteFoodsByTypePage(Long userId, String foodType, int page, int size) {
        Page<UserFavoriteFood> pageParam = new Page<>(page, size);
        return userFavoriteFoodMapper.findByUserIdAndFoodTypePage(pageParam, userId, foodType);
    }
}
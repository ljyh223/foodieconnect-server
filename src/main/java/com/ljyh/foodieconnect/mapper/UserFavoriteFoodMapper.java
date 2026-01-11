package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.UserFavoriteFood;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户喜好食物Mapper接口
 */
public interface UserFavoriteFoodMapper extends BaseMapper<UserFavoriteFood> {
    
    /**
     * 根据用户ID获取喜好食物列表
     */
    @Select("SELECT * FROM user_favorite_foods WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserFavoriteFood> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID分页获取喜好食物列表
     */
    @Select("SELECT * FROM user_favorite_foods WHERE user_id = #{userId} ORDER BY created_at DESC")
    Page<UserFavoriteFood> findByUserIdPage(Page<UserFavoriteFood> page, @Param("userId") Long userId);
    
    /**
     * 根据食物类型获取喜好食物列表
     */
    @Select("SELECT * FROM user_favorite_foods WHERE user_id = #{userId} AND food_type = #{foodType} ORDER BY created_at DESC")
    List<UserFavoriteFood> findByUserIdAndFoodType(@Param("userId") Long userId, @Param("foodType") String foodType);
    
    /**
     * 根据食物类型分页获取喜好食物列表
     */
    @Select("SELECT * FROM user_favorite_foods WHERE user_id = #{userId} AND food_type = #{foodType} ORDER BY created_at DESC")
    Page<UserFavoriteFood> findByUserIdAndFoodTypePage(Page<UserFavoriteFood> page, @Param("userId") Long userId, @Param("foodType") String foodType);
    
    /**
     * 检查用户是否已添加该喜好食物
     */
    @Select("SELECT EXISTS(SELECT 1 FROM user_favorite_foods WHERE user_id = #{userId} AND food_name = #{foodName})")
    boolean existsByUserIdAndFoodName(@Param("userId") Long userId, @Param("foodName") String foodName);
    
    /**
     * 删除用户的喜好食物
     */
    @Select("DELETE FROM user_favorite_foods WHERE user_id = #{userId} AND id = #{foodId}")
    int deleteByUserIdAndFoodId(@Param("userId") Long userId, @Param("foodId") Long foodId);
}
package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.Review;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评论Mapper接口
 */
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 根据餐厅ID分页查询评论（包含图片）
     */
    Page<Review> findByRestaurantId(Page<Review> page, @Param("restaurantId") Long restaurantId);
    
    /**
     * 根据用户ID查询评论（包含图片）
     */
    Page<Review> findByUserId(Page<Review> page, @Param("userId") Long userId);
    
    /**
     * 检查用户是否已评论过该餐厅
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE restaurant_id = #{restaurantId} AND user_id = #{userId}")
    boolean existsByRestaurantIdAndUserId(@Param("restaurantId") Long restaurantId, @Param("userId") Long userId);
    
    /**
     * 计算餐厅平均评分
     */
    @Select("SELECT AVG(rating) FROM reviews WHERE restaurant_id = #{restaurantId}")
    Double calculateAverageRating(@Param("restaurantId") Long restaurantId);
    
    /**
     * 统计餐厅评论数量
     */
    @Select("SELECT COUNT(*) FROM reviews WHERE restaurant_id = #{restaurantId}")
    Integer countByRestaurantId(@Param("restaurantId") Long restaurantId);
}
package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.UserRestaurantRecommendation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户推荐餐厅Mapper接口
 */
public interface UserRestaurantRecommendationMapper extends BaseMapper<UserRestaurantRecommendation> {
    
    /**
     * 根据用户ID获取推荐餐厅列表
     */
    @Select("SELECT r.*, u.name as restaurant_name, u.type as restaurant_type, u.image_url as restaurant_image_url " +
            "FROM user_restaurant_recommendations r " +
            "LEFT JOIN restaurants u ON r.restaurant_id = u.id " +
            "WHERE r.user_id = #{userId} ORDER BY r.created_at DESC")
    Page<UserRestaurantRecommendation> findByUserIdPage(Page<UserRestaurantRecommendation> page, @Param("userId") Long userId);
    
    /**
     * 根据餐厅ID获取推荐列表
     */
    @Select("SELECT r.*, u.display_name, u.avatar_url " +
            "FROM user_restaurant_recommendations r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.restaurant_id = #{restaurantId} ORDER BY r.created_at DESC")
    Page<UserRestaurantRecommendation> findByRestaurantIdPage(Page<UserRestaurantRecommendation> page, @Param("restaurantId") Long restaurantId);
    
    /**
     * 检查用户是否已推荐该餐厅
     */
    @Select("SELECT EXISTS(SELECT 1 FROM user_restaurant_recommendations WHERE user_id = #{userId} AND restaurant_id = #{restaurantId})")
    boolean existsByUserIdAndRestaurantId(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);
    
    /**
     * 获取热门推荐餐厅列表（按推荐人数排序）
     */
    @Select("SELECT restaurant_id, COUNT(*) as recommendation_count " +
            "FROM user_restaurant_recommendations " +
            "GROUP BY restaurant_id " +
            "ORDER BY recommendation_count DESC " +
            "LIMIT #{limit}")
    List<UserRestaurantRecommendation> findPopularRecommendations(@Param("limit") int limit);
    
    /**
     * 获取用户推荐餐厅的平均评分
     */
    @Select("SELECT AVG(rating) FROM user_restaurant_recommendations WHERE user_id = #{userId} AND rating IS NOT NULL")
    Double getAverageRatingByUserId(@Param("userId") Long userId);
    
    /**
     * 获取餐厅的平均用户评分
     */
    @Select("SELECT AVG(rating) FROM user_restaurant_recommendations WHERE restaurant_id = #{restaurantId} AND rating IS NOT NULL")
    Double getAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);
}
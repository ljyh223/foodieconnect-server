package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.entity.MenuItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单项Mapper接口
 */
public interface MenuItemMapper extends BaseMapper<MenuItem> {
    
    /**
     * 根据餐厅ID查找菜品列表
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} AND is_available = true ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID查找所有菜品（包括不可用的）
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据分类ID查找菜品列表
     */
    @Select("SELECT * FROM menu_items WHERE category_id = #{categoryId} AND is_available = true ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据餐厅ID和分类ID查找菜品列表
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} AND category_id = #{categoryId} ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> findByRestaurantIdAndCategoryId(@Param("restaurantId") Long restaurantId, 
                                                @Param("categoryId") Long categoryId);
    
    /**
     * 根据餐厅ID查找推荐菜品
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} AND is_recommended = true AND is_available = true ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> findRecommendedByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID和名称查找菜品
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} AND name = #{name}")
    MenuItem findByRestaurantIdAndName(@Param("restaurantId") Long restaurantId, @Param("name") String name);
    
    /**
     * 分页查询餐厅菜品
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} ORDER BY sort_order ASC, created_at DESC")
    Page<MenuItem> findByRestaurantIdPage(Page<MenuItem> page, @Param("restaurantId") Long restaurantId);
    
    /**
     * 根据关键词搜索菜品
     */
    @Select("SELECT * FROM menu_items WHERE restaurant_id = #{restaurantId} AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) ORDER BY sort_order ASC, created_at DESC")
    List<MenuItem> searchByKeyword(@Param("restaurantId") Long restaurantId, @Param("keyword") String keyword);
    
    /**
     * 获取分类的最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM menu_items WHERE restaurant_id = #{restaurantId} AND category_id = #{categoryId}")
    Integer getMaxSortOrder(@Param("restaurantId") Long restaurantId, @Param("categoryId") Long categoryId);
}
package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.MenuCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单分类Mapper接口
 */
public interface MenuCategoryMapper extends BaseMapper<MenuCategory> {
    
    /**
     * 根据餐厅ID查找分类列表
     */
    @Select("SELECT * FROM menu_categories WHERE restaurant_id = #{restaurantId} AND is_active = true ORDER BY sort_order ASC, created_at DESC")
    List<MenuCategory> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID查找所有分类（包括禁用的）
     */
    @Select("SELECT * FROM menu_categories WHERE restaurant_id = #{restaurantId} ORDER BY sort_order ASC, created_at DESC")
    List<MenuCategory> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID和名称查找分类
     */
    @Select("SELECT * FROM menu_categories WHERE restaurant_id = #{restaurantId} AND name = #{name}")
    MenuCategory findByRestaurantIdAndName(@Param("restaurantId") Long restaurantId, @Param("name") String name);
    
    /**
     * 获取分类的最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM menu_categories WHERE restaurant_id = #{restaurantId}")
    Integer getMaxSortOrder(@Param("restaurantId") Long restaurantId);
}
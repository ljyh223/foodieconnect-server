package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.entity.Restaurant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 餐厅Mapper接口
 */
public interface RestaurantMapper extends BaseMapper<Restaurant> {
    
    /**
     * 根据类型分页查询餐厅
     */
    @Select("SELECT * FROM restaurants WHERE type = #{type} AND is_open = true ORDER BY rating DESC")
    Page<Restaurant> findByType(Page<Restaurant> page, @Param("type") String type);
    
    /**
     * 根据关键词搜索餐厅
     */
    @Select("SELECT * FROM restaurants WHERE (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) AND is_open = true ORDER BY rating DESC")
    Page<Restaurant> searchByKeyword(Page<Restaurant> page, @Param("keyword") String keyword);
    
    /**
     * 获取热门餐厅
     */
    @Select("SELECT * FROM restaurants WHERE is_open = true ORDER BY rating DESC, review_count DESC LIMIT #{limit}")
    List<Restaurant> findPopularRestaurants(@Param("limit") int limit);
    
    /**
     * 根据评分范围查询餐厅
     */
    @Select("SELECT * FROM restaurants WHERE rating BETWEEN #{minRating} AND #{maxRating} AND is_open = true ORDER BY rating DESC")
    Page<Restaurant> findByRatingRange(Page<Restaurant> page, @Param("minRating") Double minRating, @Param("maxRating") Double maxRating);
}
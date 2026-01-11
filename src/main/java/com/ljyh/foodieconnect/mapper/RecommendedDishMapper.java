package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.RecommendedDish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 推荐菜品Mapper接口
 */
public interface RecommendedDishMapper extends BaseMapper<RecommendedDish> {
    
    /**
     * 根据餐厅ID查询推荐菜品
     */
    @Select("SELECT * FROM recommended_dishes WHERE restaurant_id = #{restaurantId} ORDER BY created_at DESC")
    List<RecommendedDish> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据餐厅ID列表批量查询推荐菜品
     */
    @Select({
        "<script>",
        "SELECT * FROM recommended_dishes WHERE restaurant_id IN",
        "<foreach collection='restaurantIds' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "ORDER BY restaurant_id, created_at DESC",
        "</script>"
    })
    List<RecommendedDish> findByRestaurantIds(@Param("restaurantIds") List<Long> restaurantIds);
}
package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.Staff;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 店员Mapper接口
 */
public interface StaffMapper extends BaseMapper<Staff> {
    
    /**
     * 根据餐厅ID查询店员列表
     */
    @Select("SELECT * FROM staff WHERE restaurant_id = #{restaurantId} ORDER BY rating DESC")
    List<Staff> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据状态查询店员
     */
    @Select("SELECT * FROM staff WHERE status = #{status} ORDER BY rating DESC")
    List<Staff> findByStatus(@Param("status") String status);
    
    /**
     * 查询在线店员
     */
    @Select("SELECT * FROM staff WHERE status = 'ONLINE' ORDER BY rating DESC")
    List<Staff> findOnlineStaff();
    
    /**
     * 根据餐厅ID和状态查询店员
     */
    @Select("SELECT * FROM staff WHERE restaurant_id = #{restaurantId} AND status = #{status} ORDER BY rating DESC")
    List<Staff> findByRestaurantIdAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") String status);

    /**
     * 获取餐厅评分最高的员工
     */
    @Select("SELECT * FROM staff WHERE restaurant_id = #{restaurantId} AND rating IS NOT NULL ORDER BY rating DESC LIMIT 1")
    Staff findHighestRatedStaff(@Param("restaurantId") Long restaurantId);

    /**
     * 获取餐厅评分最低的员工
     */
    @Select("SELECT * FROM staff WHERE restaurant_id = #{restaurantId} AND rating IS NOT NULL ORDER BY rating ASC LIMIT 1")
    Staff findLowestRatedStaff(@Param("restaurantId") Long restaurantId);

    /**
     * 计算餐厅所有员工的平均评分
     */
    @Select("SELECT COALESCE(AVG(rating), 0) FROM staff WHERE restaurant_id = #{restaurantId} AND rating IS NOT NULL")
    Double calculateAverageRatingByRestaurant(@Param("restaurantId") Long restaurantId);
}
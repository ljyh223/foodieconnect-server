package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.MerchantStatistics;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家统计Mapper接口
 */
public interface MerchantStatisticsMapper extends BaseMapper<MerchantStatistics> {
    
    /**
     * 根据餐厅ID和日期范围查询统计数据
     */
    @Select("SELECT * FROM merchant_statistics WHERE restaurant_id = #{restaurantId} AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date DESC")
    List<MerchantStatistics> findByRestaurantIdAndDateRange(@Param("restaurantId") Long restaurantId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
    
    /**
     * 根据餐厅ID和日期查询统计
     */
    @Select("SELECT * FROM merchant_statistics WHERE restaurant_id = #{restaurantId} AND stat_date = #{statDate}")
    MerchantStatistics findByRestaurantIdAndDate(@Param("restaurantId") Long restaurantId,
                                           @Param("statDate") LocalDate statDate);
    
    /**
     * 获取餐厅最近N天的统计数据
     */
    @Select("SELECT * FROM merchant_statistics WHERE restaurant_id = #{restaurantId} ORDER BY stat_date DESC LIMIT #{limit}")
    List<MerchantStatistics> findRecentByRestaurantId(@Param("restaurantId") Long restaurantId,
                                                   @Param("limit") Integer limit);
    
    /**
     * 获取餐厅月度统计数据
     */
    @Select("SELECT " +
            "    YEAR(stat_date) as year, " +
            "    MONTH(stat_date) as month, " +
            "    SUM(total_orders) as total_orders, " +
            "    SUM(total_revenue) as total_revenue, " +
            "    AVG(average_order_value) as average_order_value, " +
            "    SUM(total_customers) as total_customers, " +
            "    SUM(new_customers) as new_customers, " +
            "    SUM(returning_customers) as returning_customers, " +
            "    AVG(average_rating) as average_rating, " +
            "    SUM(total_reviews) as total_reviews " +
            "FROM merchant_statistics " +
            "WHERE restaurant_id = #{restaurantId} " +
            "    AND YEAR(stat_date) = #{year} " +
            "    AND MONTH(stat_date) = #{month} " +
            "GROUP BY YEAR(stat_date), MONTH(stat_date)")
    MerchantStatistics getMonthlyStatistics(@Param("restaurantId") Long restaurantId,
                                       @Param("year") Integer year,
                                       @Param("month") Integer month);
    
    /**
     * 获取餐厅年度统计数据
     */
    @Select("SELECT " +
            "    YEAR(stat_date) as year, " +
            "    SUM(total_orders) as total_orders, " +
            "    SUM(total_revenue) as total_revenue, " +
            "    AVG(average_order_value) as average_order_value, " +
            "    SUM(total_customers) as total_customers, " +
            "    SUM(new_customers) as new_customers, " +
            "    SUM(returning_customers) as returning_customers, " +
            "    AVG(average_rating) as average_rating, " +
            "    SUM(total_reviews) as total_reviews " +
            "FROM merchant_statistics " +
            "WHERE restaurant_id = #{restaurantId} " +
            "    AND YEAR(stat_date) = #{year} " +
            "GROUP BY YEAR(stat_date)")
    MerchantStatistics getYearlyStatistics(@Param("restaurantId") Long restaurantId,
                                       @Param("year") Integer year);
}
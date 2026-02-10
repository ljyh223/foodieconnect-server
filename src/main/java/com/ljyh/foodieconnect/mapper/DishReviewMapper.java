package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.DishReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品评价Mapper接口
 */
@Mapper
public interface DishReviewMapper extends BaseMapper<DishReview> {

    /**
     * 根据菜品ID分页查询评价（包含图片和用户信息）
     */
    Page<DishReview> findByMenuItemId(Page<DishReview> page, @Param("menuItemId") Long menuItemId);

    /**
     * 根据用户ID分页查询评价
     */
    Page<DishReview> findByUserId(Page<DishReview> page, @Param("userId") Long userId);

    /**
     * 根据餐厅ID和菜品ID分页查询评价（商家使用）
     */
    Page<DishReview> findByRestaurantIdAndItemId(Page<DishReview> page,
                                                   @Param("restaurantId") Long restaurantId,
                                                   @Param("menuItemId") Long menuItemId,
                                                   @Param("rating") Integer rating);

    /**
     * 检查用户是否已评价过该菜品
     */
    @Select("SELECT COUNT(*) FROM dish_reviews WHERE menu_item_id = #{menuItemId} AND user_id = #{userId}")
    boolean existsByMenuItemIdAndUserId(@Param("menuItemId") Long menuItemId, @Param("userId") Long userId);

    /**
     * 根据菜品ID和用户ID查询评价
     */
    @Select("SELECT * FROM dish_reviews WHERE menu_item_id = #{menuItemId} AND user_id = #{userId} LIMIT 1")
    DishReview findByMenuItemIdAndUserId(@Param("menuItemId") Long menuItemId, @Param("userId") Long userId);

    /**
     * 计算菜品平均评分
     */
    @Select("SELECT AVG(rating) FROM dish_reviews WHERE menu_item_id = #{menuItemId}")
    Double calculateAverageRating(@Param("menuItemId") Long menuItemId);

    /**
     * 统计菜品评价数量
     */
    @Select("SELECT COUNT(*) FROM dish_reviews WHERE menu_item_id = #{menuItemId}")
    Integer countByMenuItemId(@Param("menuItemId") Long menuItemId);

    /**
     * 获取菜品评分分布
     */
    List<RatingDistribution> getRatingDistribution(@Param("menuItemId") Long menuItemId);

    /**
     * 获取商家餐厅菜品的评价概览
     */
    List<ItemReviewStats> getItemReviewStats(@Param("restaurantId") Long restaurantId);

    /**
     * 获取指定日期的营收统计
     */
    DailyRevenueStats getRevenueStatsByDate(@Param("restaurantId") Long restaurantId,
                                             @Param("date") String date);

    /**
     * 获取日期范围内的每日营收统计
     */
    List<DailyRevenueStats> getDailyRevenueStatsByDateRange(@Param("restaurantId") Long restaurantId,
                                                              @Param("startDate") String startDate,
                                                              @Param("endDate") String endDate);

    /**
     * 每日营收统计结果类
     */
    class DailyRevenueStats {
        private String date;
        private java.math.BigDecimal revenue;
        private Integer orderCount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public java.math.BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(java.math.BigDecimal revenue) {
            this.revenue = revenue;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }
    }

    /**
     * 评分分布结果类
     */
    class RatingDistribution {
        private Integer rating;
        private Integer count;

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    /**
     * 菜品评价统计结果类
     */
    class ItemReviewStats {
        private Long menuItemId;
        private String itemName;
        private Double averageRating;
        private Integer reviewCount;

        public Long getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(Long menuItemId) {
            this.menuItemId = menuItemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }

        public Integer getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(Integer reviewCount) {
            this.reviewCount = reviewCount;
        }
    }
}

package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.UserRestaurantVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户餐厅访问历史Mapper接口
 */
@Mapper
public interface UserRestaurantVisitMapper extends BaseMapper<UserRestaurantVisit> {
    
    /**
     * 根据用户ID查询访问历史
     */
    @Select("SELECT * FROM user_restaurant_visits WHERE user_id = #{userId} ORDER BY last_visit_time DESC")
    List<UserRestaurantVisit> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据餐厅ID集合查询访问历史
     */
    @Select("<script>" +
            "SELECT * FROM user_restaurant_visits " +
            "WHERE restaurant_id IN " +
            "<foreach collection='restaurantIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "ORDER BY last_visit_time DESC" +
            "</script>")
    List<UserRestaurantVisit> findByRestaurantIds(@Param("restaurantIds") Set<Long> restaurantIds);
    
    /**
     * 查询两个用户共同访问的餐厅
     */
    @Select("SELECT DISTINCT r1.* FROM user_restaurant_visits r1 " +
            "INNER JOIN user_restaurant_visits r2 ON r1.restaurant_id = r2.restaurant_id " +
            "WHERE r1.user_id = #{user1Id} AND r2.user_id = #{user2Id} " +
            "ORDER BY r1.last_visit_time DESC")
    List<UserRestaurantVisit> findCommonVisitedRestaurants(@Param("user1Id") Long user1Id, 
                                                       @Param("user2Id") Long user2Id);
    
    /**
     * 根据餐厅ID查询访问过该餐厅的用户
     */
    @Select("SELECT * FROM user_restaurant_visits WHERE restaurant_id = #{restaurantId} ORDER BY last_visit_time DESC")
    List<UserRestaurantVisit> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 查询访问过指定餐厅的用户ID列表
     */
    @Select("SELECT DISTINCT user_id FROM user_restaurant_visits WHERE restaurant_id = #{restaurantId}")
    List<Long> findUsersByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 统计用户访问的餐厅数量
     */
    @Select("SELECT COUNT(DISTINCT restaurant_id) FROM user_restaurant_visits WHERE user_id = #{userId}")
    int getVisitedRestaurantsCount(@Param("userId") Long userId);
    
    /**
     * 统计用户访问次数
     */
    @Select("SELECT COUNT(*) FROM user_restaurant_visits WHERE user_id = #{userId}")
    int getVisitCount(@Param("userId") Long userId);
    
    /**
     * 获取活跃用户ID列表
     */
    @Select("SELECT DISTINCT user_id FROM user_restaurant_visits " +
            "WHERE last_visit_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "ORDER BY last_visit_time DESC")
    List<Long> getActiveUserIds(@Param("days") int days);
    
    /**
     * 根据访问类型查询用户访问历史
     */
    @Select("SELECT * FROM user_restaurant_visits " +
            "WHERE user_id = #{userId} AND visit_type = #{visitType} " +
            "ORDER BY last_visit_time DESC")
    List<UserRestaurantVisit> findByUserIdAndVisitType(@Param("userId") Long userId, 
                                                    @Param("visitType") String visitType);
    
    /**
     * 批量插入访问记录
     */
    int insertBatch(@Param("visits") List<UserRestaurantVisit> visits);
    
    /**
     * 更新访问记录
     */
    int updateVisitCount(@Param("userId") Long userId,
                       @Param("restaurantId") Long restaurantId,
                       @Param("increment") int increment);
    
    /**
     * 获取餐厅的平均评分
     */
    @Select("SELECT AVG(rating) FROM user_restaurant_visits WHERE restaurant_id = #{restaurantId} AND rating IS NOT NULL")
    Double getAverageRatingForRestaurant(@Param("restaurantId") Long restaurantId);
    
    /**
     * 获取访问过餐厅的唯一用户数量
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM user_restaurant_visits WHERE restaurant_id = #{restaurantId}")
    int getUniqueVisitorsCount(@Param("restaurantId") Long restaurantId);
    
    /**
     * 获取用户在指定时间范围内的访问记录
     */
    @Select("SELECT * FROM user_restaurant_visits " +
            "WHERE user_id = #{userId} AND last_visit_time >= #{startDate} " +
            "ORDER BY last_visit_time DESC")
    List<UserRestaurantVisit> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                     @Param("startDate") java.util.Date startDate);
    
    /**
     * 获取用户对餐厅的详细访问信息
     */
    @Select("SELECT * FROM user_restaurant_visits " +
            "WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    UserRestaurantVisit findByUserIdAndRestaurantId(@Param("userId") Long userId,
                                                   @Param("restaurantId") Long restaurantId);
    
    /**
     * 获取餐厅的热门用户（访问次数最多的用户）
     */
    @Select("SELECT user_id, COUNT(*) as visit_count FROM user_restaurant_visits " +
            "WHERE restaurant_id = #{restaurantId} " +
            "GROUP BY user_id ORDER BY visit_count DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> getTopUsersForRestaurant(@Param("restaurantId") Long restaurantId,
                                                                @Param("limit") int limit);
    
    /**
     * 获取用户的访问时间分布统计
     */
    @Select("SELECT DAYOFWEEK(last_visit_time) as day_of_week, COUNT(*) as visit_count " +
            "FROM user_restaurant_visits WHERE user_id = #{userId} " +
            "GROUP BY DAYOFWEEK(last_visit_time) ORDER BY day_of_week")
    List<java.util.Map<String, Object>> getUserVisitTimeDistribution(@Param("userId") Long userId);
    
    /**
     * 获取用户最喜欢的餐厅类型
     */
    @Select("SELECT r.type, COUNT(*) as visit_count FROM user_restaurant_visits urv " +
            "INNER JOIN restaurants r ON urv.restaurant_id = r.id " +
            "WHERE urv.user_id = #{userId} " +
            "GROUP BY r.type ORDER BY visit_count DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> getUserFavoriteRestaurantTypes(@Param("userId") Long userId,
                                                                     @Param("limit") int limit);
}
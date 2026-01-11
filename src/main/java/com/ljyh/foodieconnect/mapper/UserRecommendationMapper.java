package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.UserRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户推荐结果Mapper接口
 */
@Mapper
public interface UserRecommendationMapper extends BaseMapper<UserRecommendation> {
    
    /**
     * 根据用户ID、推荐用户ID和算法类型查询推荐记录
     */
    @Select("SELECT * FROM user_recommendations " +
            "WHERE user_id = #{userId} " +
            "AND recommended_user_id = #{recommendedUserId} " +
            "AND algorithm_type = #{algorithmType}")
    UserRecommendation findByUserIdAndRecommendedUserIdAndAlgorithm(@Param("userId") Long userId, 
                                                             @Param("recommendedUserId") Long recommendedUserId, 
                                                             @Param("algorithmType") String algorithmType);
    
    /**
     * 统计用户的推荐总数
     */
    @Select("SELECT COUNT(*) FROM user_recommendations WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户已查看的推荐数量
     */
    @Select("SELECT COUNT(*) FROM user_recommendations " +
            "WHERE user_id = #{userId} AND is_viewed = #{isViewed}")
    int countByUserIdAndViewed(@Param("userId") Long userId, @Param("isViewed") Boolean isViewed);
    
    /**
     * 统计用户感兴趣的推荐数量
     */
    @Select("SELECT COUNT(*) FROM user_recommendations " +
            "WHERE user_id = #{userId} AND is_interested = #{isInterested}")
    int countByUserIdAndInterested(@Param("userId") Long userId, @Param("isInterested") Boolean isInterested);
    
    /**
     * 获取用户的推荐列表
     */
    @Select("SELECT r.*, u.display_name as recommended_user_name, u.avatar_url as recommended_user_avatar " +
            "FROM user_recommendations r " +
            "LEFT JOIN users u ON r.recommended_user_id = u.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.recommendation_score DESC, r.created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<UserRecommendationWithUserInfo> findByUserIdWithPagination(@Param("userId") Long userId, 
                                                              @Param("offset") int offset, 
                                                              @Param("limit") int limit);
    
    /**
     * 获取用户未查看的推荐列表
     */
    @Select("SELECT r.*, u.display_name as recommended_user_name, u.avatar_url as recommended_user_avatar " +
            "FROM user_recommendations r " +
            "LEFT JOIN users u ON r.recommended_user_id = u.id " +
            "WHERE r.user_id = #{userId} AND r.is_viewed = false " +
            "ORDER BY r.recommendation_score DESC, r.created_at DESC " +
            "LIMIT #{limit}")
    List<UserRecommendationWithUserInfo> findUnviewedByUserId(@Param("userId") Long userId, 
                                                          @Param("limit") int limit);
    
    /**
     * 获取用户已推荐的推荐用户ID列表
     */
    @Select("SELECT DISTINCT recommended_user_id FROM user_recommendations " +
            "WHERE user_id = #{userId} " +
            "AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    List<Long> getRecommendedUserIds(@Param("userId") Long userId, @Param("days") int days);
    
    /**
     * 删除指定时间之前的推荐记录
     */
    @Delete("DELETE FROM user_recommendations WHERE created_at < #{expireTime}")
    int deleteByCreatedAtBefore(@Param("expireTime") LocalDateTime expireTime);
    
    /**
     * 批量更新推荐状态
     */
    int batchUpdateViewedStatus(@Param("recommendationIds") List<Long> recommendationIds, 
                               @Param("isViewed") Boolean isViewed);
    
    /**
     * 获取推荐算法统计信息
     */
    @Select("SELECT " +
            "algorithm_type, " +
            "COUNT(*) as total_count, " +
            "AVG(recommendation_score) as avg_score, " +
            "SUM(CASE WHEN is_viewed = true THEN 1 ELSE 0 END) as viewed_count, " +
            "SUM(CASE WHEN is_interested = true THEN 1 ELSE 0 END) as interested_count " +
            "FROM user_recommendations " +
            "WHERE user_id = #{userId} " +
            "GROUP BY algorithm_type")
    List<AlgorithmStats> getAlgorithmStatsByUserId(@Param("userId") Long userId);
    
    /**
     * 获取全局推荐算法统计信息
     */
    @Select("SELECT " +
            "algorithm_type, " +
            "COUNT(*) as total_count, " +
            "AVG(recommendation_score) as avg_score, " +
            "SUM(CASE WHEN is_viewed = true THEN 1 ELSE 0 END) as viewed_count, " +
            "SUM(CASE WHEN is_interested = true THEN 1 ELSE 0 END) as interested_count " +
            "FROM user_recommendations " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY algorithm_type")
    List<AlgorithmStats> getGlobalAlgorithmStats(@Param("days") int days);
    
    /**
     * 用户推荐结果与用户信息组合类
     */
    class UserRecommendationWithUserInfo extends UserRecommendation {
        private String recommendedUserName;
        private String recommendedUserAvatar;
        
        public String getRecommendedUserName() { return recommendedUserName; }
        public void setRecommendedUserName(String recommendedUserName) { this.recommendedUserName = recommendedUserName; }
        
        public String getRecommendedUserAvatar() { return recommendedUserAvatar; }
        public void setRecommendedUserAvatar(String recommendedUserAvatar) { this.recommendedUserAvatar = recommendedUserAvatar; }
    }
    
    /**
     * 算法统计信息类
     */
    class AlgorithmStats {
        private String algorithmType;
        private Long totalCount;
        private Double avgScore;
        private Long viewedCount;
        private Long interestedCount;
        
        public String getAlgorithmType() { return algorithmType; }
        public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
        
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public Double getAvgScore() { return avgScore; }
        public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }
        
        public Long getViewedCount() { return viewedCount; }
        public void setViewedCount(Long viewedCount) { this.viewedCount = viewedCount; }
        
        public Long getInterestedCount() { return interestedCount; }
        public void setInterestedCount(Long interestedCount) { this.interestedCount = interestedCount; }
    }
}
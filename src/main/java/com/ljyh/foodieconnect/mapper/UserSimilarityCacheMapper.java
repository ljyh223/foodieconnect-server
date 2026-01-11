package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.UserSimilarityCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

/**
 * 用户相似度缓存Mapper接口
 */
@Mapper
public interface UserSimilarityCacheMapper extends BaseMapper<UserSimilarityCache> {
    
    /**
     * 根据用户对和算法类型查询相似度缓存
     */
    @Select("SELECT * FROM user_similarity_cache " +
            "WHERE user1_id = #{user1Id} AND user2_id = #{user2Id} AND algorithm_type = #{algorithmType} " +
            "UNION " +
            "SELECT * FROM user_similarity_cache " +
            "WHERE user1_id = #{user2Id} AND user2_id = #{user1Id} AND algorithm_type = #{algorithmType} " +
            "LIMIT 1")
    UserSimilarityCache findByUserPairAndAlgorithm(@Param("user1Id") Long user1Id, 
                                              @Param("user2Id") Long user2Id, 
                                              @Param("algorithmType") String algorithmType);
    
    /**
     * 查询指定用户的所有相似度缓存
     */
    @Select("SELECT * FROM user_similarity_cache " +
            "WHERE user1_id = #{userId} OR user2_id = #{userId} " +
            "ORDER BY similarity_score DESC")
    List<UserSimilarityCache> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询高相似度用户
     */
    @Select("SELECT * FROM user_similarity_cache " +
            "WHERE (user1_id = #{userId} OR user2_id = #{userId}) " +
            "AND similarity_score >= #{threshold} " +
            "ORDER BY similarity_score DESC " +
            "LIMIT #{limit}")
    List<UserSimilarityCache> findHighSimilarityUsers(@Param("userId") Long userId, 
                                                    @Param("threshold") double threshold, 
                                                    @Param("limit") int limit);
    
    /**
     * 插入或更新相似度缓存
     */
    @Insert("INSERT INTO user_similarity_cache " +
            "(user1_id, user2_id, similarity_score, common_restaurants_count, algorithm_type, last_calculated) " +
            "VALUES (#{user1Id}, #{user2Id}, #{similarityScore}, #{commonRestaurantsCount}, #{algorithmType}, NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "similarity_score = VALUES(similarity_score), " +
            "common_restaurants_count = VALUES(common_restaurants_count), " +
            "last_calculated = NOW()")
    boolean insertOrUpdate(UserSimilarityCache similarityCache);
    
    /**
     * 批量插入相似度缓存
     */
    int insertBatch(@Param("caches") List<UserSimilarityCache> caches);
    
    /**
     * 删除过期的相似度缓存
     */
    @Select("DELETE FROM user_similarity_cache WHERE last_calculated < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteExpiredCache(@Param("days") int days);
    
    /**
     * 统计用户相似度缓存数量
     */
    @Select("SELECT COUNT(*) FROM user_similarity_cache WHERE user1_id = #{userId} OR user2_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户相似度统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "AVG(similarity_score) as avg_similarity, " +
            "MAX(similarity_score) as max_similarity, " +
            "MIN(similarity_score) as min_similarity " +
            "FROM user_similarity_cache " +
            "WHERE (user1_id = #{userId} OR user2_id = #{userId}) " +
            "AND algorithm_type = #{algorithmType}")
    SimilarityStats getSimilarityStats(@Param("userId") Long userId, 
                                      @Param("algorithmType") String algorithmType);
    
    /**
     * 相似度统计信息内部类
     */
    class SimilarityStats {
        private Integer totalCount;
        private Double avgSimilarity;
        private Double maxSimilarity;
        private Double minSimilarity;
        
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        
        public Double getAvgSimilarity() { return avgSimilarity; }
        public void setAvgSimilarity(Double avgSimilarity) { this.avgSimilarity = avgSimilarity; }
        
        public Double getMaxSimilarity() { return maxSimilarity; }
        public void setMaxSimilarity(Double maxSimilarity) { this.maxSimilarity = maxSimilarity; }
        
        public Double getMinSimilarity() { return minSimilarity; }
        public void setMinSimilarity(Double minSimilarity) { this.minSimilarity = minSimilarity; }
    }
}
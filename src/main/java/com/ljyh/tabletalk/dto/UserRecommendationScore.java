package com.ljyh.tabletalk.dto;

import com.ljyh.tabletalk.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户推荐分数DTO类
 * 用于存储用户推荐结果和分数信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationScore implements Comparable<UserRecommendationScore> {
    
    /**
     * 推荐用户ID
     */
    private Long userId;
    
    /**
     * 推荐用户名
     */
    private String userName;
    
    /**
     * 推荐用户头像
     */
    private String userAvatar;
    
    /**
     * 推荐分数
     */
    private BigDecimal score;
    
    /**
     * 推荐算法类型
     */
    private String algorithmType;
    
    /**
     * 用户相似度
     */
    private Double similarity;
    
    /**
     * 社交距离(1:一度关注, 2:二度关注)
     */
    private Integer socialDistance;
    
    /**
     * 共同关注数量
     */
    private Integer mutualFollowsCount;
    
    /**
     * 推荐理由
     */
    private String recommendationReason;
    
    /**
     * 共同访问的餐厅列表
     */
    private List<Restaurant> commonRestaurants;
    
    /**
     * 共同餐厅类型
     */
    private List<String> commonRestaurantTypes;
    
    /**
     * 用户活跃度分数
     */
    private Double activityScore;
    
    /**
     * 用户影响力分数
     */
    private Double influenceScore;
    
    @Override
    public int compareTo(UserRecommendationScore other) {
        // 降序排列，分数高的排在前面
        return other.getScore().compareTo(this.getScore());
    }
}
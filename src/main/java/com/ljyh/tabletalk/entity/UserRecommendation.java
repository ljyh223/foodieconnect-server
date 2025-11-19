package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户推荐结果实体类
 * 存储系统生成的用户推荐结果，支持用户反馈和推荐效果分析
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_recommendations")
public class UserRecommendation extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 被推荐用户ID
     */
    private Long userId;
    
    /**
     * 推荐用户ID
     */
    private Long recommendedUserId;
    
    /**
     * 推荐算法类型(collaborative, social, hybrid)
     */
    private String algorithmType;
    
    /**
     * 推荐分数(0.0000-1.0000)
     */
    private BigDecimal recommendationScore;
    
    /**
     * 推荐理由
     */
    private String recommendationReason;
    
    /**
     * 是否已查看
     */
    private Boolean isViewed;
    
    /**
     * 是否感兴趣(NULL:未操作, TRUE:感兴趣, FALSE:不感兴趣)
     */
    private Boolean isInterested;
    
    /**
     * 用户反馈
     */
    private String feedback;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
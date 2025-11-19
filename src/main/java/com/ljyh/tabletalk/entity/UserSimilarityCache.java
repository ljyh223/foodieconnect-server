package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户相似度缓存实体类
 * 用于缓存用户间的相似度计算结果，提高推荐算法性能
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_similarity_cache")
public class UserSimilarityCache {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户1 ID
     */
    private Long user1Id;
    
    /**
     * 用户2 ID
     */
    private Long user2Id;
    
    /**
     * 相似度分数(0.0000-1.0000)
     */
    private BigDecimal similarityScore;
    
    /**
     * 共同访问餐厅数量
     */
    private Integer commonRestaurantsCount;
    
    /**
     * 算法类型(cosine, pearson, adjusted_cosine)
     */
    private String algorithmType;
    
    /**
     * 最后计算时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastCalculated;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户推荐餐厅实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_restaurant_recommendations")
public class UserRestaurantRecommendation extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 推荐理由
     */
    private String reason;
    
    /**
     * 用户评分(1-5)
     */
    private BigDecimal rating;
    
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
package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户餐厅访问历史记录实体类
 * 用于记录用户与餐厅的各种交互行为，为推荐算法提供数据基础
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_restaurant_visits")
public class UserRestaurantVisit extends BaseEntity {
    
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
     * 访问类型：REVIEW(评论), RECOMMENDATION(推荐), FAVORITE(收藏), CHECK_IN(签到)
     */
    private VisitType visitType;
    
    /**
     * 用户评分(1-5)
     */
    private BigDecimal rating;
    
    /**
     * 访问次数
     */
    private Integer visitCount;
    
    /**
     * 最后访问时间
     */
    private LocalDateTime lastVisitTime;
    
    /**
     * 访问类型枚举
     */
    public enum VisitType {
        REVIEW("评论"),
        RECOMMENDATION("推荐"),
        FAVORITE("收藏"),
        CHECK_IN("签到");
        
        private final String description;
        
        VisitType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
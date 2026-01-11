package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户喜好食物实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_favorite_foods")
public class UserFavoriteFood extends BaseEntity {
    
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
     * 食物名称
     */
    private String foodName;
    
    /**
     * 食物类型(如:川菜,粤菜,日料等)
     */
    private String foodType;
}
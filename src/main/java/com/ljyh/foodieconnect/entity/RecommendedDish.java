package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 推荐菜品实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recommended_dishes")
public class RecommendedDish extends BaseEntity {
    
    /**
     * 菜品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 菜品名称
     */
    private String dishName;
    
    /**
     * 菜品描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 图片URL
     */
    private String imageUrl;
}
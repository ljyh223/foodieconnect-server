package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 菜单项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu_items")
public class MenuItem extends BaseEntity {
    
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
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 菜品名称
     */
    private String name;
    
    /**
     * 菜品描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 是否可用
     */
    private Boolean isAvailable;
    
    /**
     * 是否推荐
     */
    private Boolean isRecommended;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 营养信息(JSON格式)
     */
    private String nutritionInfo;
    
    /**
     * 过敏原信息(JSON格式)
     */
    private String allergenInfo;
    
    /**
     * 辣度等级
     */
    private SpiceLevel spiceLevel;
    
    /**
     * 制作时间(分钟)
     */
    private Integer preparationTime;
    
    /**
     * 卡路里
     */
    private Integer calories;
    
    /**
     * 辣度等级枚举
     */
    public enum SpiceLevel {
        NONE("不辣"),
        MILD("微辣"),
        MEDIUM("中辣"),
        HOT("特辣");
        
        private final String description;
        
        SpiceLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
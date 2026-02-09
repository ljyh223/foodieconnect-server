package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 菜品评价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dish_reviews")
public class DishReview extends BaseEntity {

    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品ID
     */
    private Long menuItemId;

    /**
     * 餐厅ID
     */
    private Long restaurantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评分（1-5）
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 用户名称（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户头像（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String userAvatar;

    /**
     * 菜品名称（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String itemName;

    /**
     * 菜品价格（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private BigDecimal itemPrice;

    /**
     * 菜品图片（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String itemImage;

    /**
     * 评价图片列表（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private List<DishReviewImage> images;

    /**
     * 评价图片URL列表（非数据库字段，用于响应）
     */
    @TableField(exist = false)
    private List<String> imageUrls;
}

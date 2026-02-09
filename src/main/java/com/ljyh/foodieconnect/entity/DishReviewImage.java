package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品评价图片实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dish_review_images")
public class DishReviewImage extends BaseEntity {

    /**
     * 图片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品评价ID
     */
    private Long dishReviewId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}

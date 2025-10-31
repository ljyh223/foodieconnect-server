package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_images")
public class ReviewImage extends BaseEntity {
    
    /**
     * 图片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 评论ID
     */
    private Long reviewId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 图片排序
     */
    private Integer sortOrder;
}
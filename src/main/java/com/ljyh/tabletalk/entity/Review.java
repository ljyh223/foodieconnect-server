package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reviews")
public class Review extends BaseEntity {
    
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评分
     */
    private Integer rating;
    
    /**
     * 评论内容
     */
    private String comment;
    
    /**
     * 评论图片列表（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private List<ReviewImage> images;
}
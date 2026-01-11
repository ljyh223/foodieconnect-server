package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 店员评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("staff_reviews")
public class StaffReview extends BaseEntity {
    
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 店员ID
     */
    private Long staffId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评价内容
     */
    private String content;
    
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
}
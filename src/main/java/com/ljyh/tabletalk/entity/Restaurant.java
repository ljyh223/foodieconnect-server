package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 餐厅实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("restaurants")
public class Restaurant extends BaseEntity {
    
    /**
     * 餐厅ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅名称
     */
    private String name;
    
    /**
     * 餐厅类型
     */
    private String type;
    
    /**
     * 距离
     */
    private String distance;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 营业时间
     */
    private String hours;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评论数量
     */
    private Integer reviewCount;
    
    /**
     * 是否营业
     */
    private Boolean isOpen;
    
    /**
     * 头像标识
     */
    private String avatar;
}
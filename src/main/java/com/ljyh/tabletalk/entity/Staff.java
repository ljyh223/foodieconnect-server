package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ljyh.tabletalk.enums.StaffStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 店员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("staff")
public class Staff extends BaseEntity {
    
    /**
     * 店员ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 店员姓名
     */
    private String name;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 状态
     */
    private StaffStatus status;
    
    /**
     * 工作经验
     */
    private String experience;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
}
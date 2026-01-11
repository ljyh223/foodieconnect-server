package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商家统计实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_statistics")
public class MerchantStatistics extends BaseEntity {
    
    /**
     * 统计ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 统计日期
     */
    private LocalDate statDate;
    
    /**
     * 总订单数
     */
    private Integer totalOrders;
    
    /**
     * 总营业额
     */
    private BigDecimal totalRevenue;
    
    /**
     * 平均订单价值
     */
    private BigDecimal averageOrderValue;
    
    /**
     * 总顾客数
     */
    private Integer totalCustomers;
    
    /**
     * 新顾客数
     */
    private Integer newCustomers;
    
    /**
     * 回头客数
     */
    private Integer returningCustomers;
    
    /**
     * 平均评分
     */
    private BigDecimal averageRating;
    
    /**
     * 总评价数
     */
    private Integer totalReviews;
    
    /**
     * 高峰时段(小时)
     */
    private Integer peakHour;
    
    /**
     * 高峰时段订单数
     */
    private Integer peakHourOrders;
}
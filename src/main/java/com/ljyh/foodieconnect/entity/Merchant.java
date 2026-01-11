package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchants")
public class Merchant extends BaseEntity {
    
    /**
     * 商家ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 商家用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码哈希
     */
    private String passwordHash;
    
    /**
     * 商家姓名
     */
    private String name;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 商家角色
     */
    private MerchantRole role;
    
    /**
     * 账户状态
     */
    private MerchantStatus status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;
    
    /**
     * 商家角色枚举
     */
    public enum MerchantRole {
        ADMIN("管理员"),
        MANAGER("经理"),
        STAFF("店员");
        
        private final String description;
        
        MerchantRole(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 商家状态枚举
     */
    public enum MerchantStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        BANNED("已禁用");
        
        private final String description;
        
        MerchantStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
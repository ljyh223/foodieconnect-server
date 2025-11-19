package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ljyh.tabletalk.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 显示名称
     */
    private String displayName;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 密码哈希
     */
    private String passwordHash;
    
    /**
     * 用户状态
     */
    @TableField("status")
    private UserStatus status;
    
}
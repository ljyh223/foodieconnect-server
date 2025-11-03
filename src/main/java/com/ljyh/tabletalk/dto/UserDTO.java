package com.ljyh.tabletalk.dto;

import com.ljyh.tabletalk.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
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
     * 用户状态
     */
    private UserStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
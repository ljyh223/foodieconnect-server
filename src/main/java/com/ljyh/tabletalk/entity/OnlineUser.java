package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 在线用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("online_users")
public class OnlineUser {
    
    /**
     * 在线用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 聊天室ID
     */
    private Long roomId;
    
    /**
     * WebSocket会话ID
     */
    private String sessionId;
    
    /**
     * 连接时间
     */
    private LocalDateTime connectedAt;
    
    /**
     * 最后活动时间
     */
    private LocalDateTime lastActiveAt;
    
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
package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 聊天室成员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_room_members")
public class ChatRoomMember extends BaseEntity {
    
    /**
     * 成员ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 聊天室ID
     */
    private Long roomId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
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
    
    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;
    
    /**
     * 是否在线
     */
    private Boolean isOnline;
    
    /**
     * 成员角色
     */
    private String role;
}
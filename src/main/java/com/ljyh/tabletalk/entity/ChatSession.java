package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天会话实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_sessions")
public class ChatSession extends BaseEntity {
    
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 店员ID
     */
    private Long staffId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话状态
     */
    private ChatSessionStatus status;
    
    /**
     * 最后一条消息
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private String lastMessageTime;
    
    /**
     * 未读消息数量
     */
    private Integer unreadCount;
}
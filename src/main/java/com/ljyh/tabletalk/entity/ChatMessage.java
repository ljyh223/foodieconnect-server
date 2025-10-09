package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.enums.SenderType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_messages")
public class ChatMessage extends BaseEntity {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话ID
     */
    private Long sessionId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者类型
     */
    private SenderType senderType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private MessageType messageType;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
}
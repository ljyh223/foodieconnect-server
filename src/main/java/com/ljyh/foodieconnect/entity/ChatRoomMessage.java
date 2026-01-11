package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ljyh.foodieconnect.enums.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天室消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_room_messages")
public class ChatRoomMessage extends BaseEntity {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 聊天室ID
     */
    private Long roomId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型
     */
    private MessageType messageType;
    
    /**
     * 发送者名称（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String senderName;
    
    /**
     * 发送者头像（非数据库字段，仅用于查询结果展示）
     */
    @TableField(exist = false)
    private String senderAvatar;
}
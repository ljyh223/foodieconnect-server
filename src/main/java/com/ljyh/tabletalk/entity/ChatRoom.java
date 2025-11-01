package com.ljyh.tabletalk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 餐厅聊天室实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_rooms")
public class ChatRoom extends BaseEntity {
    
    /**
     * 聊天室ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 聊天室名称
     */
    private String name;
    
    /**
     * 验证码
     */
    private String verificationCode;
    
    /**
     * 聊天室状态
     */
    private ChatSessionStatus status;
    
    /**
     * 最后一条消息
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private java.time.LocalDateTime lastMessageTime;
    
    /**
     * 在线用户数量
     */
    private Integer onlineUserCount;
}
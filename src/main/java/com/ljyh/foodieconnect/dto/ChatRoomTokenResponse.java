package com.ljyh.foodieconnect.dto;

import lombok.Data;

/**
 * 聊天室临时令牌响应
 */
@Data
public class ChatRoomTokenResponse {
    
    /**
     * 聊天室信息（不包含敏感信息）
     */
    private ChatRoomDTO chatRoom;
    
    /**
     * 临时JWT令牌（用于WebSocket连接）
     */
    private String tempToken;
    
    /**
     * 令牌过期时间（毫秒）
     */
    private Long expiresIn;
    
    public ChatRoomTokenResponse(ChatRoomDTO chatRoom, String tempToken, Long expiresIn) {
        this.chatRoom = chatRoom;
        this.tempToken = tempToken;
        this.expiresIn = expiresIn;
    }
}
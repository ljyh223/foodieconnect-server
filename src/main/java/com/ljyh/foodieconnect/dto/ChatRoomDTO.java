package com.ljyh.foodieconnect.dto;

import com.ljyh.foodieconnect.enums.ChatSessionStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天室DTO，不包含敏感信息
 */
@Data
public class ChatRoomDTO {
    
    /**
     * 聊天室ID
     */
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
    private LocalDateTime lastMessageTime;
    
    /**
     * 在线用户数量
     */
    private Integer onlineUserCount;
    
    /**
     * 从实体类创建DTO
     */
    public static ChatRoomDTO fromEntity(com.ljyh.foodieconnect.entity.ChatRoom chatRoom) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(chatRoom.getId());
        dto.setRestaurantId(chatRoom.getRestaurantId());
        dto.setName(chatRoom.getName());
        dto.setStatus(chatRoom.getStatus());
        dto.setLastMessage(chatRoom.getLastMessage());
        dto.setLastMessageTime(chatRoom.getLastMessageTime());
        dto.setOnlineUserCount(chatRoom.getOnlineUserCount());
        return dto;
    }
}
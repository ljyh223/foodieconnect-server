package com.ljyh.tabletalk.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatRoomMessage;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 聊天室WebSocket消息处理器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatRoomWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    
    /**
     * 处理发送聊天室消息
     */
    @MessageMapping("/chat-room.sendMessage")
    public void sendMessage(@Payload ChatRoomMessageRequest request, StompHeaderAccessor headerAccessor) {
        try {
            log.info("收到聊天室WebSocket消息: {}", request);
            
            // 从token中获取用户ID
            String token = headerAccessor.getFirstNativeHeader("Authorization").toString();
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = null;
            try {
                String userEmail = jwtService.extractUsername(token);
                User user = userMapper.findByEmail(userEmail).orElse(null);
                if (user != null) {
                    userId = user.getId();
                }
            } catch (Exception e) {
                log.error("解析JWT token失败: {}", e.getMessage());
            }
            
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝发送消息");
                return;
            }
            
            // 发送消息
            ChatRoomMessage message = chatRoomService.sendMessage(request.getRoomId(), userId, request.getContent());
            
            // 设置发送者信息
            User user = userMapper.selectById(userId);
            if (user != null) {
                message.setSenderName(user.getDisplayName());
                message.setSenderAvatar(user.getAvatarUrl());
            }
            
            // 构建响应消息
            ChatRoomMessageResponse response = new ChatRoomMessageResponse();
            response.setId(message.getId());
            response.setRoomId(message.getRoomId());
            response.setSenderId(message.getSenderId());
            response.setContent(message.getContent());
            response.setMessageType(message.getMessageType());
            response.setSenderName(message.getSenderName());
            response.setSenderAvatar(message.getSenderAvatar());
            response.setTimestamp(LocalDateTime.now());
            
            // 发送消息到聊天室
            String destination = "/topic/chat-room/" + request.getRoomId();
            messagingTemplate.convertAndSend(destination, ApiResponse.success(response));
            
            log.info("聊天室WebSocket消息发送成功: {}", response);
            
        } catch (Exception e) {
            log.error("聊天室WebSocket消息处理失败: {}", e.getMessage(), e);
            // 发送错误消息给发送者
            try {
                Long senderId = extractUserIdFromToken(headerAccessor);
                if (senderId != null) {
                    String userDestination = "/user/" + senderId + "/queue/errors";
                    messagingTemplate.convertAndSend(userDestination, 
                        ApiResponse.error("MESSAGE_SEND_FAILED", "消息发送失败: " + e.getMessage()));
                }
            } catch (Exception ex) {
                log.error("发送错误消息失败: {}", ex.getMessage());
            }
        }
    }
    
    /**
     * 处理用户加入聊天室
     */
    @MessageMapping("/chat-room.join")
    public void joinRoom(@Payload Map<String, Object> payload, StompHeaderAccessor headerAccessor) {
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());
            Long userId = extractUserIdFromToken(headerAccessor);
            
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝加入聊天室");
                return;
            }
            
            log.info("用户 {} 加入聊天室 {}", userId, roomId);
            
            // 设置用户为在线状态
            chatRoomService.setUserOnline(roomId, userId);
            
            // 发送加入成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(userDestination, 
                ApiResponse.success(Map.of("type", "ROOM_JOINED", "roomId", roomId)));
            
        } catch (Exception e) {
            log.error("加入聊天室失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理用户离开聊天室
     */
    @MessageMapping("/chat-room.leave")
    public void leaveRoom(@Payload Map<String, Object> payload, StompHeaderAccessor headerAccessor) {
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());
            Long userId = extractUserIdFromToken(headerAccessor);
            
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝离开聊天室");
                return;
            }
            
            log.info("用户 {} 离开聊天室 {}", userId, roomId);
            
            // 离开聊天室
            chatRoomService.leaveRoom(roomId, userId);
            
            // 发送离开成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(userDestination, 
                ApiResponse.success(Map.of("type", "ROOM_LEFT", "roomId", roomId)));
            
        } catch (Exception e) {
            log.error("离开聊天室失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 从token中提取用户ID
     */
    private Long extractUserIdFromToken(StompHeaderAccessor headerAccessor) {
        try {
            Object authHeader = headerAccessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                return null;
            }
            
            String token = authHeader.toString();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String userEmail = jwtService.extractUsername(token);
            User user = userMapper.findByEmail(userEmail).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            log.error("提取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 聊天室消息请求类
     */
    public static class ChatRoomMessageRequest {
        private Long roomId;
        private String content;
        
        // getters and setters
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    /**
     * 聊天室消息响应类
     */
    public static class ChatRoomMessageResponse {
        private Long id;
        private Long roomId;
        private Long senderId;
        private String content;
        private MessageType messageType;
        private String senderName;
        private String senderAvatar;
        private LocalDateTime timestamp;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public MessageType getMessageType() { return messageType; }
        public void setMessageType(MessageType messageType) { this.messageType = messageType; }
        
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        
        public String getSenderAvatar() { return senderAvatar; }
        public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
package com.ljyh.tabletalk.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatMessage;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.enums.SenderType;
import com.ljyh.tabletalk.service.ChatService;
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
 * WebSocket消息处理器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    
    /**
     * 处理发送消息
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, StompHeaderAccessor headerAccessor) {
        try {
            log.info("收到WebSocket消息: {}", request);
            
            // 验证会话和权限
            ChatMessage message = chatService.sendMessage(
                request.getSessionId(),
                request.getSenderId(),
                request.getSenderType(),
                request.getContent(),
                request.getMessageType()
            );
            
            // 构建响应消息
            ChatMessageResponse response = new ChatMessageResponse();
            response.setId(message.getId());
            response.setSessionId(message.getSessionId());
            response.setSenderId(message.getSenderId());
            response.setSenderType(message.getSenderType());
            response.setContent(message.getContent());
            response.setMessageType(message.getMessageType());
            response.setTimestamp(LocalDateTime.now());
            response.setSentByUser(message.getSenderType() == SenderType.USER);
            
            // 发送消息到会话
            String destination = "/topic/chat/" + request.getSessionId();
            messagingTemplate.convertAndSend(destination, ApiResponse.success(response));
            
            log.info("WebSocket消息发送成功: {}", response);
            
        } catch (Exception e) {
            log.error("WebSocket消息处理失败: {}", e.getMessage(), e);
            // 发送错误消息给发送者
            String userDestination = "/user/" + request.getSenderId() + "/queue/errors";
            messagingTemplate.convertAndSend(userDestination, 
                ApiResponse.error("MESSAGE_SEND_FAILED", "消息发送失败: " + e.getMessage()));
        }
    }
    
    /**
     * 处理用户加入会话
     */
    @MessageMapping("/chat.joinSession")
    public void joinSession(@Payload Map<String, Object> payload, StompHeaderAccessor headerAccessor) {
        try {
            Long sessionId = Long.valueOf(payload.get("sessionId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            log.info("用户 {} 加入聊天会话 {}", userId, sessionId);
            
            // 标记消息为已读
            chatService.markMessagesAsRead(sessionId, userId);
            
            // 发送加入成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(userDestination, 
                ApiResponse.success(Map.of("type", "SESSION_JOINED", "sessionId", sessionId)));
            
        } catch (Exception e) {
            log.error("加入会话失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理用户离开会话
     */
    @MessageMapping("/chat.leaveSession")
    public void leaveSession(@Payload Map<String, Object> payload, StompHeaderAccessor headerAccessor) {
        try {
            Long sessionId = Long.valueOf(payload.get("sessionId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            log.info("用户 {} 离开聊天会话 {}", userId, sessionId);
            
            // 发送离开成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(userDestination, 
                ApiResponse.success(Map.of("type", "SESSION_LEFT", "sessionId", sessionId)));
            
        } catch (Exception e) {
            log.error("离开会话失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 聊天消息请求类
     */
    public static class ChatMessageRequest {
        private Long sessionId;
        private Long senderId;
        private SenderType senderType;
        private String content;
        private MessageType messageType = MessageType.TEXT;
        
        // getters and setters
        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public SenderType getSenderType() { return senderType; }
        public void setSenderType(SenderType senderType) { this.senderType = senderType; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public MessageType getMessageType() { return messageType; }
        public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    }
    
    /**
     * 聊天消息响应类
     */
    public static class ChatMessageResponse {
        private Long id;
        private Long sessionId;
        private Long senderId;
        private SenderType senderType;
        private String content;
        private MessageType messageType;
        private LocalDateTime timestamp;
        private Boolean isSentByUser;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public SenderType getSenderType() { return senderType; }
        public void setSenderType(SenderType senderType) { this.senderType = senderType; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public MessageType getMessageType() { return messageType; }
        public void setMessageType(MessageType messageType) { this.messageType = messageType; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Boolean getSentByUser() { return isSentByUser; }
        public void setSentByUser(Boolean sentByUser) { isSentByUser = sentByUser; }
    }
}
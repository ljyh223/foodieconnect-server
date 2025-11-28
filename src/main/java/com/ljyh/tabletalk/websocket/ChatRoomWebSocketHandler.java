package com.ljyh.tabletalk.websocket;

import com.ljyh.tabletalk.entity.ChatRoomMessage;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.protobuf.ChatProtos;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.JwtService;
import com.ljyh.tabletalk.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private final UserMapper userMapper;
    private final OnlineUserService onlineUserService;
    
    /**
     * 处理发送聊天室消息
     */
    @MessageMapping("/chat-room.sendMessage")
    public void sendMessage(@Payload ChatProtos.WebSocketMessage webSocketMessage, StompHeaderAccessor headerAccessor) {
        try {
            log.info("收到聊天室WebSocket protobuf消息，类型: {}", webSocketMessage.getType());
            
            // 解包消息
            ChatProtos.SendMessageRequest request = ProtobufMessageConverter.unwrapWebSocketMessage(
                webSocketMessage, ChatProtos.SendMessageRequest.class);
            
            if (request == null) {
                log.warn("无法解析发送消息请求");
                sendErrorResponse(headerAccessor, "无法解析消息请求");
                return;
            }
            
            // 从token中获取用户ID
            Long userId = getUserIdFromHeaderAccessor(headerAccessor);
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝发送消息");
                sendErrorResponse(headerAccessor, "用户未认证");
                return;
            }
            
            // 更新用户最后活动时间
            onlineUserService.updateLastActiveTime(headerAccessor.getSessionId());
            
            // 发送消息
            ChatRoomMessage message = chatRoomService.sendMessage(request.getRoomId(), userId, request.getContent());
            
            // 设置发送者信息
            User senderUser = userMapper.selectById(userId);
            if (senderUser != null) {
                message.setSenderName(senderUser.getDisplayName());
                message.setSenderAvatar(senderUser.getAvatarUrl());
            }
            
            // 构建protobuf响应消息
            ChatProtos.ChatMessage chatMessage = buildChatMessage(message);
            
            // 发送protobuf消息到聊天室
            String destination = "/topic/chat-room/" + request.getRoomId();
            ChatProtos.ChatResponse response = ProtobufMessageConverter.createChatMessageResponse(chatMessage);
            messagingTemplate.convertAndSend(destination, response);
            
            log.info("聊天室WebSocket protobuf消息发送成功，房间ID: {}, 用户ID: {}", request.getRoomId(), userId);
            
        } catch (Exception e) {
            log.error("聊天室WebSocket消息处理失败: {}", e.getMessage(), e);
            sendErrorResponse(headerAccessor, "消息发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理用户加入聊天室
     */
    @MessageMapping("/chat-room.join")
    public void joinRoom(@Payload ChatProtos.WebSocketMessage webSocketMessage, StompHeaderAccessor headerAccessor) {
        try {
            log.info("收到加入聊天室protobuf消息，类型: {}", webSocketMessage.getType());
            
            // 解包消息
            ChatProtos.JoinRoomRequest request = ProtobufMessageConverter.unwrapWebSocketMessage(
                webSocketMessage, ChatProtos.JoinRoomRequest.class);
            
            if (request == null) {
                log.warn("无法解析加入房间请求");
                sendErrorResponse(headerAccessor, "无法解析加入房间请求");
                return;
            }
            
            Long roomId = request.getRoomId();
            Long userId = getUserIdFromHeaderAccessor(headerAccessor);
            
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝加入聊天室");
                sendErrorResponse(headerAccessor, "用户未认证");
                return;
            }
            
            log.info("用户 {} 加入聊天室 {}", userId, roomId);
            
            // 设置用户为在线状态
            chatRoomService.setUserOnline(roomId, userId);
            
            // 发送protobuf加入成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            ChatProtos.ChatResponse response = ProtobufMessageConverter.createJoinRoomResponse(roomId, "成功加入聊天室");
            messagingTemplate.convertAndSend(userDestination, response);
            
        } catch (Exception e) {
            log.error("加入聊天室失败: {}", e.getMessage(), e);
            sendErrorResponse(headerAccessor, "加入聊天室失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理用户离开聊天室
     */
    @MessageMapping("/chat-room.leave")
    public void leaveRoom(@Payload ChatProtos.WebSocketMessage webSocketMessage, StompHeaderAccessor headerAccessor) {
        try {
            log.info("收到离开聊天室protobuf消息，类型: {}", webSocketMessage.getType());
            
            // 解包消息
            ChatProtos.LeaveRoomRequest request = ProtobufMessageConverter.unwrapWebSocketMessage(
                webSocketMessage, ChatProtos.LeaveRoomRequest.class);
            
            if (request == null) {
                log.warn("无法解析离开房间请求");
                sendErrorResponse(headerAccessor, "无法解析离开房间请求");
                return;
            }
            
            Long roomId = request.getRoomId();
            Long userId = getUserIdFromHeaderAccessor(headerAccessor);
            
            if (userId == null) {
                log.warn("无法获取用户ID，拒绝离开聊天室");
                sendErrorResponse(headerAccessor, "用户未认证");
                return;
            }
            
            log.info("用户 {} 离开聊天室 {}", userId, roomId);
            
            // 从在线用户表中移除
            onlineUserService.removeOnlineUserByUserIdAndRoomId(userId, roomId);
            
            // 离开聊天室
            chatRoomService.leaveRoom(roomId, userId);
            
            // 发送protobuf离开成功消息
            String userDestination = "/user/" + userId + "/queue/notifications";
            ChatProtos.ChatResponse response = ProtobufMessageConverter.createLeaveRoomResponse(roomId, "成功离开聊天室");
            messagingTemplate.convertAndSend(userDestination, response);
            
        } catch (Exception e) {
            log.error("离开聊天室失败: {}", e.getMessage(), e);
            sendErrorResponse(headerAccessor, "离开聊天室失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理WebSocket连接事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            log.info("WebSocket连接建立: {}", headerAccessor.getSessionId());
            
            // 尝试从token中获取用户信息
            JwtService.TempTokenInfo tokenInfo = getTokenInfoFromHeaderAccessor(headerAccessor);
            if (tokenInfo != null) {
                log.info("用户 {} 建立WebSocket连接，房间ID: {}", tokenInfo.getUserId(), tokenInfo.getRoomId());
                
                // 将用户信息存储在session中，供后续消息处理使用
                headerAccessor.getSessionAttributes().put("userId", tokenInfo.getUserId());
                headerAccessor.getSessionAttributes().put("roomId", tokenInfo.getRoomId());
                
                // 添加到在线用户表
                onlineUserService.addOnlineUser(tokenInfo.getUserId(), tokenInfo.getRoomId(), headerAccessor.getSessionId());
                
                // 更新聊天室在线状态
                chatRoomService.setUserOnline(tokenInfo.getRoomId(), tokenInfo.getUserId());
            } else {
                log.warn("WebSocket连接未提供有效的认证信息");
            }
        } catch (Exception e) {
            log.error("处理WebSocket连接失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理WebSocket断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            log.info("WebSocket连接断开: {}", headerAccessor.getSessionId());
            
            // 从session中获取用户信息
            Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
            Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
            
            if (userId != null && roomId != null) {
                log.info("用户 {} 断开WebSocket连接，房间ID: {}", userId, roomId);
                
                // 从在线用户表中移除
                onlineUserService.removeOnlineUserBySessionId(headerAccessor.getSessionId());
                
                // 更新聊天室离线状态
                chatRoomService.setUserOffline(roomId, userId);
            } else {
                // 如果session中没有，尝试从token中获取
                JwtService.TempTokenInfo tokenInfo = getTokenInfoFromHeaderAccessor(headerAccessor);
                if (tokenInfo != null) {
                    log.info("用户 {} 断开WebSocket连接，房间ID: {}", tokenInfo.getUserId(), tokenInfo.getRoomId());
                    
                    // 从在线用户表中移除
                    onlineUserService.removeOnlineUserBySessionId(headerAccessor.getSessionId());
                    
                    // 更新聊天室离线状态
                    chatRoomService.setUserOffline(tokenInfo.getRoomId(), tokenInfo.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("处理WebSocket断开连接失败: {}", e.getMessage(), e);
        }
    }
    
    private Long getUserIdFromHeaderAccessor(StompHeaderAccessor headerAccessor) {
        try {
            // 首先尝试从session中获取
            Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
            if (userIdObj != null) {
                return (Long) userIdObj;
            }
            
            // 如果session中没有，尝试从token中获取
            JwtService.TempTokenInfo tokenInfo = getTokenInfoFromHeaderAccessor(headerAccessor);
            return tokenInfo != null ? tokenInfo.getUserId() : null;
        } catch (Exception e) {
            log.error("提取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    private JwtService.TempTokenInfo getTokenInfoFromHeaderAccessor(StompHeaderAccessor headerAccessor) {
        try {
            // 如果session中有，直接返回
            Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
            Object roomIdObj = headerAccessor.getSessionAttributes().get("roomId");
            if (userIdObj != null && roomIdObj != null) {
                // 从session中重建token信息
                User user = userMapper.selectById((Long) userIdObj);
                if (user != null) {
                    return new JwtService.TempTokenInfo(
                        (Long) userIdObj,
                        user.getEmail(),
                        user.getDisplayName(),
                        (Long) roomIdObj
                    );
                }
            }

            // 如果session中没有，尝试从token中获取
            Object authHeaderObj = headerAccessor.getFirstNativeHeader("Authorization");
            if (authHeaderObj == null) {
                return null;
            }

            String token = authHeaderObj.toString();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            return jwtService.validateTempToken(token);
        } catch (Exception e) {
            log.error("提取token信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 构建protobuf聊天消息
     */
    private ChatProtos.ChatMessage buildChatMessage(ChatRoomMessage message) {
        ChatProtos.MessageType messageType = ChatProtos.MessageType.TEXT;
        if (message.getMessageType() != null) {
            switch (message.getMessageType()) {
                case IMAGE:
                    messageType = ChatProtos.MessageType.IMAGE;
                    break;
                case SYSTEM:
                    messageType = ChatProtos.MessageType.SYSTEM;
                    break;
                default:
                    messageType = ChatProtos.MessageType.TEXT;
            }
        }

        return ChatProtos.ChatMessage.newBuilder()
                .setId(message.getId() != null ? message.getId() : 0)
                .setRoomId(message.getRoomId() != null ? message.getRoomId() : 0)
                .setSenderId(message.getSenderId() != null ? message.getSenderId() : 0)
                .setContent(message.getContent() != null ? message.getContent() : "")
                .setMessageType(messageType)
                .setSenderName(message.getSenderName() != null ? message.getSenderName() : "")
                .setSenderAvatar(message.getSenderAvatar() != null ? message.getSenderAvatar() : "")
                .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(StompHeaderAccessor headerAccessor, String errorMessage) {
        try {
            Long userId = getUserIdFromHeaderAccessor(headerAccessor);
            if (userId != null) {
                String userDestination = "/user/" + userId + "/queue/errors";
                ChatProtos.ChatResponse errorResponse = ProtobufMessageConverter.createErrorResponse(errorMessage);
                messagingTemplate.convertAndSend(userDestination, errorResponse);
            }
        } catch (Exception ex) {
            log.error("发送错误消息失败: {}", ex.getMessage());
        }
    }
}
package com.ljyh.foodieconnect.websocket;

import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.ChatRoomMessage;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.protobuf.ChatProtos;
import com.ljyh.foodieconnect.service.ChatRoomService;
import com.ljyh.foodieconnect.service.JwtService;
import com.ljyh.foodieconnect.service.JwtMerchantService;
import com.ljyh.foodieconnect.service.OnlineUserService;
import com.ljyh.foodieconnect.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryChatWebSocketHandler extends AbstractWebSocketHandler {

    private final ChatRoomService chatRoomService;
    private final JwtService jwtService;
    private final JwtMerchantService jwtMerchantService;
    private final UserMapper userMapper;
    private final OnlineUserService onlineUserService;

    private final ConcurrentHashMap<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Object tokenInfo = extractTokenInfo(session);
            
            if (tokenInfo != null) {
                // 有token的情况，按原有逻辑处理
                Long userId;
                Long roomId;
                String userType;
                
                if (tokenInfo instanceof JwtService.TempTokenInfo) {
                    // 用户token
                    JwtService.TempTokenInfo userTokenInfo = (JwtService.TempTokenInfo) tokenInfo;
                    userId = userTokenInfo.getUserId();
                    roomId = userTokenInfo.getRoomId();
                    userType = "用户";
                } else if (tokenInfo instanceof JwtMerchantService.TempTokenInfo) {
                    // 商家token
                    JwtMerchantService.TempTokenInfo merchantTokenInfo = (JwtMerchantService.TempTokenInfo) tokenInfo;
                    userId = merchantTokenInfo.getMerchantId();
                    roomId = merchantTokenInfo.getRoomId();
                    userType = "商家";
                } else {
                    log.warn("未知的token信息类型: {}", tokenInfo.getClass().getName());
                    return;
                }
                
                // 将用户信息存储在session中
                session.getAttributes().put("userId", userId);
                session.getAttributes().put("roomId", roomId);
                session.getAttributes().put("userType", "REGISTERED");
                
                // 添加到房间会话集合
                roomSessions.computeIfAbsent(roomId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(session);
                
                // 更新在线状态
                onlineUserService.addOnlineUser(userId, roomId, session.getId());
                chatRoomService.setUserOnline(roomId, userId);
                
                log.info("{} {} 建立WebSocket连接，房间ID: {}", userType, userId, roomId);
            } else {
                // 无token的情况，检查是否为观察者
                boolean isObserver = isObserverConnection(session);
                String observerType = getObserverType(session);
                
                if (isObserver) {
                    // 解析房间ID
                    Long roomId = extractRoomIdFromUrl(session);
                    if (roomId == null) {
                        log.warn("观察者连接缺少roomId参数");
                        session.close(CloseStatus.NOT_ACCEPTABLE);
                        return;
                    }
                    
                    // 生成临时观察者ID
                    Long observerId = generateObserverId();
                    String userType = "观察者";
                    
                    // 将观察者信息存储在session中
                    session.getAttributes().put("userId", observerId);
                    session.getAttributes().put("roomId", roomId);
                    session.getAttributes().put("userType", "OBSERVER");
                    session.getAttributes().put("observerType", observerType);
                    
                    // 添加到房间会话集合
                    roomSessions.computeIfAbsent(roomId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(session);
                    
                    // 直接从数据库获取聊天室信息，不调用joinRoomAsObserver
                    // 因为joinRoomAsObserver的第一个参数是restaurantId，不是roomId
                    ChatRoom chatRoom = chatRoomService.getById(roomId);
                    if (chatRoom == null) {
                        log.warn("聊天室不存在: {}", roomId);
                        session.close(CloseStatus.NOT_ACCEPTABLE);
                        return;
                    }
                    
                    // 更新在线状态
                    onlineUserService.addOnlineUser(observerId, roomId, session.getId());
                    
                    log.info("{} {} 建立WebSocket连接，房间ID: {}，观察者类型: {}", userType, observerId, roomId, observerType);
                } else {
                    log.warn("WebSocket连接未提供有效的认证信息，且不是观察者连接");
                    session.close(CloseStatus.NOT_ACCEPTABLE);
                }
            }
        } catch (Exception e) {
            log.error("binary websocket connect error: {}", e.getMessage(), e);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败: {}", ex.getMessage(), ex);
            }
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            onlineUserService.updateLastActiveTime(session.getId());
            ByteBuffer buffer = message.getPayload();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            ChatProtos.WebSocketMessage ws = ChatProtos.WebSocketMessage.parseFrom(data);
            String type = ws.getType();
            if ("SEND_MESSAGE".equals(type)) {
                ChatProtos.SendMessageRequest req = ChatProtos.SendMessageRequest.parseFrom(ws.getPayload());
                Long userId = getUserId(session);
                if (userId == null) {
                    sendError(session, "unauthorized");
                    return;
                }
                ChatRoomMessage saved = chatRoomService.sendMessage(req.getRoomId(), userId, req.getContent());
                User sender = userMapper.selectById(userId);
                if (sender != null) {
                    saved.setSenderName(sender.getDisplayName());
                    saved.setSenderAvatar(sender.getAvatarUrl());
                }
                ChatProtos.ChatMessage chatMsg = buildChatMessage(saved);
                ChatProtos.ChatResponse resp = ProtobufMessageConverter.createChatMessageResponse(chatMsg);
                broadcastToRoom(req.getRoomId(), new BinaryMessage(resp.toByteArray()));
            } else if ("JOIN_ROOM".equals(type)) {
                ChatProtos.JoinRoomRequest req = ChatProtos.JoinRoomRequest.parseFrom(ws.getPayload());
                Long userId = getUserId(session);
                if (userId == null) {
                    sendError(session, "unauthorized");
                    return;
                }
                chatRoomService.setUserOnline(req.getRoomId(), userId);
                roomSessions.computeIfAbsent(req.getRoomId(), k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(session);
                ChatProtos.ChatResponse resp = ProtobufMessageConverter.createJoinRoomResponse(req.getRoomId(), "joined");
                session.sendMessage(new BinaryMessage(resp.toByteArray()));
            } else if ("LEAVE_ROOM".equals(type)) {
                ChatProtos.LeaveRoomRequest req = ChatProtos.LeaveRoomRequest.parseFrom(ws.getPayload());
                Long userId = getUserId(session);
                if (userId == null) {
                    sendError(session, "unauthorized");
                    return;
                }
                onlineUserService.removeOnlineUserByUserIdAndRoomId(userId, req.getRoomId());
                chatRoomService.leaveRoom(req.getRoomId(), userId);
                removeFromRoom(req.getRoomId(), session);
                ChatProtos.ChatResponse resp = ProtobufMessageConverter.createLeaveRoomResponse(req.getRoomId(), "left");
                session.sendMessage(new BinaryMessage(resp.toByteArray()));
            } else {
                sendError(session, "unknown type");
            }
        } catch (Exception e) {
            log.error("binary websocket handle error: {}", e.getMessage(), e);
            try {
                sendError(session, "error: " + e.getMessage());
            } catch (Exception ignore) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            Long userId = getUserId(session);
            Long roomId = getRoomId(session);
            if (roomId != null) {
                removeFromRoom(roomId, session);
            }
            if (userId != null && roomId != null) {
                onlineUserService.removeOnlineUserBySessionId(session.getId());
                chatRoomService.setUserOffline(roomId, userId);
            }
        } catch (Exception e) {
            log.error("binary websocket close error: {}", e.getMessage(), e);
        }
    }

    private void broadcastToRoom(Long roomId, BinaryMessage message) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions == null) return;
        for (WebSocketSession s : sessions) {
            try {
                if (s.isOpen()) s.sendMessage(message);
            } catch (Exception ignore) {}
        }
    }

    private void removeFromRoom(Long roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) roomSessions.remove(roomId);
        }
    }

    private Long getUserId(WebSocketSession session) {
        Object v = session.getAttributes().get("userId");
        if (v instanceof Long) return (Long) v;
        
        Object tokenInfo = extractTokenInfo(session);
        if (tokenInfo == null) return null;
        
        if (tokenInfo instanceof JwtService.TempTokenInfo) {
            // 用户token
            JwtService.TempTokenInfo userTokenInfo = (JwtService.TempTokenInfo) tokenInfo;
            return userTokenInfo.getUserId();
        } else if (tokenInfo instanceof JwtMerchantService.TempTokenInfo) {
            // 商家token
            JwtMerchantService.TempTokenInfo merchantTokenInfo = (JwtMerchantService.TempTokenInfo) tokenInfo;
            return merchantTokenInfo.getMerchantId();
        }
        
        return null;
    }

    private Long getRoomId(WebSocketSession session) {
        Object v = session.getAttributes().get("roomId");
        if (v instanceof Long) return (Long) v;
        
        Object tokenInfo = extractTokenInfo(session);
        if (tokenInfo == null) return null;
        
        if (tokenInfo instanceof JwtService.TempTokenInfo) {
            // 用户token
            JwtService.TempTokenInfo userTokenInfo = (JwtService.TempTokenInfo) tokenInfo;
            return userTokenInfo.getRoomId();
        } else if (tokenInfo instanceof JwtMerchantService.TempTokenInfo) {
            // 商家token
            JwtMerchantService.TempTokenInfo merchantTokenInfo = (JwtMerchantService.TempTokenInfo) tokenInfo;
            return merchantTokenInfo.getRoomId();
        }
        
        return null;
    }

    private Object extractTokenInfo(WebSocketSession session) {
        try {
            String header = null;
            if (session.getHandshakeHeaders() != null && session.getHandshakeHeaders().containsKey("Authorization")) {
                header = session.getHandshakeHeaders().getFirst("Authorization");
            }
            if (header == null && session.getUri() != null && session.getUri().getQuery() != null) {
                String q = session.getUri().getQuery();
                for (String kv : q.split("&")) {
                    String[] p = kv.split("=");
                    if (p.length == 2 && "token".equalsIgnoreCase(p[0])) {
                        header = p[1];
                        break;
                    }
                }
            }
            if (header == null) return null;
            
            String token = header;
            if (token.startsWith("Bearer ")) token = token.substring(7);
            
            // 尝试先作为用户token验证
            try {
                return jwtService.validateTempToken(token);
            } catch (Exception e) {
                // 用户token验证失败，尝试作为商家token验证
                try {
                    return jwtMerchantService.validateTempToken(token);
                } catch (Exception ex) {
                    log.warn("临时令牌验证失败: {}", ex.getMessage());
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("提取token信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    private ChatProtos.ChatMessage buildChatMessage(ChatRoomMessage message) {
        return ChatProtos.ChatMessage.newBuilder()
                .setId(message.getId() != null ? message.getId() : 0)
                .setRoomId(message.getRoomId() != null ? message.getRoomId() : 0)
                .setSenderId(message.getSenderId() != null ? message.getSenderId() : 0)
                .setContent(message.getContent() != null ? message.getContent() : "")
                .setMessageType(ChatProtos.MessageType.TEXT)
                .setSenderName(message.getSenderName() != null ? message.getSenderName() : "")
                .setSenderAvatar(message.getSenderAvatar() != null ? message.getSenderAvatar() : "")
                .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    private void sendError(WebSocketSession session, String errorMessage) throws Exception {
        ChatProtos.ChatResponse resp = ProtobufMessageConverter.createErrorResponse(errorMessage);
        session.sendMessage(new BinaryMessage(resp.toByteArray()));
    }

    /**
     * 判断是否是观察者连接
     */
    private boolean isObserverConnection(WebSocketSession session) {
        if (session.getUri() == null) return false;
        
        // 检查URL中是否包含observer参数或者没有token参数
        String query = session.getUri().getQuery();
        if (query == null) return true; // 没有查询参数，视为观察者
        
        // 如果有observer参数，或者没有token参数，视为观察者
        boolean hasObserverParam = query.contains("observer=") || query.contains("observer");
        boolean hasTokenParam = query.contains("token=");
        
        return hasObserverParam || !hasTokenParam;
    }

    /**
     * 获取观察者类型
     */
    private String getObserverType(WebSocketSession session) {
        if (session.getUri() == null) return "default";
        
        String query = session.getUri().getQuery();
        if (query == null) return "default";
        
        for (String kv : query.split("&")) {
            String[] p = kv.split("=");
            if (p.length >= 2 && "observer".equalsIgnoreCase(p[0])) {
                return p[1];
            }
        }
        return "default";
    }

    /**
     * 从URL中提取房间ID
     */
    private Long extractRoomIdFromUrl(WebSocketSession session) {
        if (session.getUri() == null) return null;
        
        String path = session.getUri().getPath();
        if (path == null) return null;
        
        // 从路径中提取房间ID，例如 /ws/chat/123 -> 123
        String[] parts = path.split("/");
        if (parts.length < 2) return null;
        
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            log.warn("无效的房间ID: {}", parts[parts.length - 1]);
            return null;
        }
    }

    /**
     * 生成临时观察者ID
     */
    private Long generateObserverId() {
        // 使用负数表示临时观察者ID，避免与真实用户ID冲突
        return -System.nanoTime();
    }
}

package com.ljyh.foodieconnect.websocket;

import com.ljyh.foodieconnect.protobuf.ChatProtos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Protobuf消息转换器
 * 用于在WebSocket消息和protobuf之间进行转换
 */
@Slf4j
public class ProtobufMessageConverter extends AbstractMessageConverter {

    private static final MimeType PROTOBUF_MIME_TYPE = new MimeType("application", "x-protobuf");

    public ProtobufMessageConverter() {
        super(PROTOBUF_MIME_TYPE);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // 支持WebSocketMessage和ChatResponse的转换
        return ChatProtos.WebSocketMessage.class.isAssignableFrom(clazz) ||
               ChatProtos.ChatResponse.class.isAssignableFrom(clazz);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        try {
            Object payload = message.getPayload();
            
            if (payload instanceof byte[]) {
                byte[] data = (byte[]) payload;
                
                if (targetClass.equals(ChatProtos.WebSocketMessage.class)) {
                    return ChatProtos.WebSocketMessage.parseFrom(data);
                } else if (targetClass.equals(ChatProtos.ChatResponse.class)) {
                    return ChatProtos.ChatResponse.parseFrom(data);
                }
            } else if (payload instanceof String) {
                // 如果是字符串，尝试解析为protobuf
                String text = (String) payload;
                byte[] data = text.getBytes(StandardCharsets.UTF_8);
                
                if (targetClass.equals(ChatProtos.WebSocketMessage.class)) {
                    return ChatProtos.WebSocketMessage.parseFrom(data);
                } else if (targetClass.equals(ChatProtos.ChatResponse.class)) {
                    return ChatProtos.ChatResponse.parseFrom(data);
                }
            }
            
        } catch (IOException e) {
            log.error("Failed to parse protobuf message", e);
            throw new MessageConversionException("Failed to parse protobuf message", e);
        }
        
        return null;
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        try {
            if (payload instanceof ChatProtos.WebSocketMessage) {
                return ((ChatProtos.WebSocketMessage) payload).toByteArray();
            } else if (payload instanceof ChatProtos.ChatResponse) {
                return ((ChatProtos.ChatResponse) payload).toByteArray();
            } else if (payload instanceof ChatProtos.SendMessageRequest) {
                return ((ChatProtos.SendMessageRequest) payload).toByteArray();
            } else if (payload instanceof ChatProtos.JoinRoomRequest) {
                return ((ChatProtos.JoinRoomRequest) payload).toByteArray();
            } else if (payload instanceof ChatProtos.LeaveRoomRequest) {
                return ((ChatProtos.LeaveRoomRequest) payload).toByteArray();
            }
        } catch (Exception e) {
            log.error("Failed to serialize protobuf message", e);
            throw new MessageConversionException("Failed to serialize protobuf message", e);
        }
        
        return payload;
    }

    /**
     * 创建发送消息请求
     */
    public static ChatProtos.SendMessageRequest createSendMessageRequest(Long roomId, String content) {
        return ChatProtos.SendMessageRequest.newBuilder()
                .setRoomId(roomId)
                .setContent(content)
                .build();
    }

    /**
     * 创建加入房间请求
     */
    public static ChatProtos.JoinRoomRequest createJoinRoomRequest(Long roomId) {
        return ChatProtos.JoinRoomRequest.newBuilder()
                .setRoomId(roomId)
                .build();
    }

    /**
     * 创建离开房间请求
     */
    public static ChatProtos.LeaveRoomRequest createLeaveRoomRequest(Long roomId) {
        return ChatProtos.LeaveRoomRequest.newBuilder()
                .setRoomId(roomId)
                .build();
    }

    /**
     * 创建聊天消息响应
     */
    public static ChatProtos.ChatResponse createChatMessageResponse(ChatProtos.ChatMessage message) {
        return ChatProtos.ChatResponse.newBuilder()
                .setSuccess(true)
                .setMessage(message)
                .build();
    }

    /**
     * 创建加入房间响应
     */
    public static ChatProtos.ChatResponse createJoinRoomResponse(Long roomId, String message) {
        ChatProtos.JoinRoomResponse joinResponse = ChatProtos.JoinRoomResponse.newBuilder()
                .setRoomId(roomId)
                .setMessage(message)
                .build();
        
        return ChatProtos.ChatResponse.newBuilder()
                .setSuccess(true)
                .setJoinResponse(joinResponse)
                .build();
    }

    /**
     * 创建离开房间响应
     */
    public static ChatProtos.ChatResponse createLeaveRoomResponse(Long roomId, String message) {
        ChatProtos.LeaveRoomResponse leaveResponse = ChatProtos.LeaveRoomResponse.newBuilder()
                .setRoomId(roomId)
                .setMessage(message)
                .build();
        
        return ChatProtos.ChatResponse.newBuilder()
                .setSuccess(true)
                .setLeaveResponse(leaveResponse)
                .build();
    }

    /**
     * 创建错误响应
     */
    public static ChatProtos.ChatResponse createErrorResponse(String errorMessage) {
        return ChatProtos.ChatResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(errorMessage)
                .build();
    }

    /**
     * 包装WebSocket消息
     */
    public static ChatProtos.WebSocketMessage wrapWebSocketMessage(String type, com.google.protobuf.Message payload) {
        return ChatProtos.WebSocketMessage.newBuilder()
                .setType(type)
                .setPayload(payload.toByteString())
                .build();
    }

    /**
     * 解包WebSocket消息
     */
    public static <T extends com.google.protobuf.Message> T unwrapWebSocketMessage(
            ChatProtos.WebSocketMessage message, Class<T> payloadClass) {
        try {
            if (payloadClass.equals(ChatProtos.SendMessageRequest.class)) {
                return payloadClass.cast(ChatProtos.SendMessageRequest.parseFrom(message.getPayload()));
            } else if (payloadClass.equals(ChatProtos.JoinRoomRequest.class)) {
                return payloadClass.cast(ChatProtos.JoinRoomRequest.parseFrom(message.getPayload()));
            } else if (payloadClass.equals(ChatProtos.LeaveRoomRequest.class)) {
                return payloadClass.cast(ChatProtos.LeaveRoomRequest.parseFrom(message.getPayload()));
            }
        } catch (Exception e) {
            log.error("Failed to unwrap WebSocket message", e);
        }
        return null;
    }
}
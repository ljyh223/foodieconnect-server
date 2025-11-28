package com.ljyh.tabletalk.websocket;

import com.ljyh.tabletalk.protobuf.ChatProtos;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Protobuf消息测试类
 */
@SpringBootTest
public class ProtobufMessageTest {

    @Test
    public void testSendMessageRequestSerialization() {
        // 创建发送消息请求
        ChatProtos.SendMessageRequest request = ProtobufMessageConverter.createSendMessageRequest(1L, "Hello, World!");
        
        // 序列化
        byte[] serialized = request.toByteArray();
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
        
        // 反序列化
        try {
            ChatProtos.SendMessageRequest deserialized = ChatProtos.SendMessageRequest.parseFrom(serialized);
            assertEquals(1L, deserialized.getRoomId());
            assertEquals("Hello, World!", deserialized.getContent());
        } catch (Exception e) {
            fail("反序列化失败: " + e.getMessage());
        }
    }

    @Test
    public void testWebSocketMessageWrapper() {
        // 创建发送消息请求
        ChatProtos.SendMessageRequest request = ProtobufMessageConverter.createSendMessageRequest(1L, "Test Message");
        
        // 包装为WebSocket消息
        ChatProtos.WebSocketMessage webSocketMessage = ProtobufMessageConverter.wrapWebSocketMessage("SEND_MESSAGE", request);
        
        assertEquals("SEND_MESSAGE", webSocketMessage.getType());
        assertTrue(webSocketMessage.getPayload().size() > 0);
        
        // 解包WebSocket消息
        ChatProtos.SendMessageRequest unwrapped = ProtobufMessageConverter.unwrapWebSocketMessage(
            webSocketMessage, ChatProtos.SendMessageRequest.class);
        
        assertNotNull(unwrapped);
        assertEquals(1L, unwrapped.getRoomId());
        assertEquals("Test Message", unwrapped.getContent());
    }

    @Test
    public void testChatMessageResponse() {
        // 创建聊天消息
        ChatProtos.ChatMessage chatMessage = ChatProtos.ChatMessage.newBuilder()
                .setId(100L)
                .setRoomId(1L)
                .setSenderId(50L)
                .setContent("Test message content")
                .setMessageType(ChatProtos.MessageType.TEXT)
                .setSenderName("Test User")
                .setSenderAvatar("avatar.jpg")
                .setTimestamp("2025-11-26T13:00:00")
                .build();
        
        // 创建响应
        ChatProtos.ChatResponse response = ProtobufMessageConverter.createChatMessageResponse(chatMessage);
        
        assertTrue(response.getSuccess());
        assertEquals("", response.getErrorMessage()); // 错误消息为空表示没有错误
        assertTrue(response.hasMessage());
        assertEquals(100L, response.getMessage().getId());
        assertEquals("Test message content", response.getMessage().getContent());
    }

    @Test
    public void testErrorResponse() {
        ChatProtos.ChatResponse errorResponse = ProtobufMessageConverter.createErrorResponse("Test error message");
        
        assertFalse(errorResponse.getSuccess());
        assertEquals("Test error message", errorResponse.getErrorMessage());
        assertFalse(errorResponse.hasMessage());
    }

    @Test
    public void testJoinAndLeaveRoomRequests() {
        // 测试加入房间请求
        ChatProtos.JoinRoomRequest joinRequest = ProtobufMessageConverter.createJoinRoomRequest(1L);
        assertEquals(1L, joinRequest.getRoomId());
        
        // 测试离开房间请求
        ChatProtos.LeaveRoomRequest leaveRequest = ProtobufMessageConverter.createLeaveRoomRequest(1L);
        assertEquals(1L, leaveRequest.getRoomId());
        
        // 测试加入房间响应
        ChatProtos.ChatResponse joinResponse = ProtobufMessageConverter.createJoinRoomResponse(1L, "Joined successfully");
        assertTrue(joinResponse.getSuccess());
        assertTrue(joinResponse.hasJoinResponse());
        assertEquals(1L, joinResponse.getJoinResponse().getRoomId());
        assertEquals("Joined successfully", joinResponse.getJoinResponse().getMessage());
        
        // 测试离开房间响应
        ChatProtos.ChatResponse leaveResponse = ProtobufMessageConverter.createLeaveRoomResponse(1L, "Left successfully");
        assertTrue(leaveResponse.getSuccess());
        assertTrue(leaveResponse.hasLeaveResponse());
        assertEquals(1L, leaveResponse.getLeaveResponse().getRoomId());
        assertEquals("Left successfully", leaveResponse.getLeaveResponse().getMessage());
    }
}
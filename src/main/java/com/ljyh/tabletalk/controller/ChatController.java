package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatMessage;
import com.ljyh.tabletalk.entity.ChatSession;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.enums.SenderType;
import com.ljyh.tabletalk.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@Tag(name = "聊天管理", description = "即时聊天相关接口")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @Operation(summary = "创建聊天会话", description = "创建用户与店员之间的聊天会话")
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<ChatSession>> createSession(
            @Parameter(description = "餐厅ID") @RequestParam Long restaurantId,
            @Parameter(description = "店员ID") @RequestParam Long staffId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        ChatSession session = chatService.createSession(restaurantId, staffId, userId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
    
    @Operation(summary = "获取用户聊天会话列表", description = "获取指定用户的所有聊天会话")
    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<ApiResponse<List<ChatSession>>> getUserSessions(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        List<ChatSession> sessions = chatService.getUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    @Operation(summary = "获取店员聊天会话列表", description = "获取指定店员的所有聊天会话")
    @GetMapping("/sessions/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<ChatSession>>> getStaffSessions(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        List<ChatSession> sessions = chatService.getStaffSessions(staffId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
    
    @Operation(summary = "发送消息", description = "在指定会话中发送消息")
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<ChatMessage>> sendMessage(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "发送者ID") @RequestParam Long senderId,
            @Parameter(description = "发送者类型") @RequestParam SenderType senderType,
            @Parameter(description = "消息内容") @RequestParam String content,
            @Parameter(description = "消息类型") @RequestParam(defaultValue = "TEXT") MessageType messageType) {
        
        ChatMessage message = chatService.sendMessage(sessionId, senderId, senderType, content, messageType);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    @Operation(summary = "获取会话消息列表", description = "分页获取指定会话的消息列表")
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessage>>> getSessionMessages(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        
        Page<ChatMessage> messages = chatService.getSessionMessages(sessionId, page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "标记消息为已读", description = "标记指定会话中的所有消息为已读")
    @PutMapping("/sessions/{sessionId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessagesAsRead(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        chatService.markMessagesAsRead(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "关闭聊天会话", description = "关闭指定的聊天会话")
    @PutMapping("/sessions/{sessionId}/close")
    public ResponseEntity<ApiResponse<Void>> closeSession(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        chatService.closeSession(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取会话未读消息数量", description = "获取指定会话的未读消息数量")
    @GetMapping("/sessions/{sessionId}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadMessageCount(
            @Parameter(description = "会话ID") @PathVariable Long sessionId) {
        
        int unreadCount = chatService.getUnreadMessageCount(sessionId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", unreadCount)));
    }
    
    @Operation(summary = "获取用户总未读消息数量", description = "获取用户在所有会话中的总未读消息数量")
    @GetMapping("/user/{userId}/total-unread")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserTotalUnreadCount(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        int totalUnreadCount = chatService.getUserTotalUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalUnreadCount", totalUnreadCount)));
    }
}
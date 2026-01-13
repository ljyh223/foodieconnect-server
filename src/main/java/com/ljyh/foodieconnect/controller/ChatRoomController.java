package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.ChatRoomDTO;
import com.ljyh.foodieconnect.dto.ChatRoomTokenResponse;
import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.ChatRoomMember;
import com.ljyh.foodieconnect.entity.ChatRoomMessage;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.service.ChatRoomService;
import com.ljyh.foodieconnect.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 聊天室控制器
 */
@Tag(name = "聊天室管理", description = "餐厅聊天室相关接口")
@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    
    @Operation(summary = "验证聊天室验证码并获取临时令牌", description = "验证聊天室验证码并返回用于WebSocket连接的临时JWT令牌")
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<ChatRoomTokenResponse>> verifyAndJoinRoom(
            @Parameter(description = "餐厅ID") @RequestParam Long restaurantId,
            @Parameter(description = "验证码") @RequestParam String verificationCode) {
        
        // 从SecurityContext中获取当前登录用户的ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录后再验证聊天室"));
        }
        
        // 从认证信息中获取用户email，然后查询用户ID
        String userEmail = authentication.getName();
        Optional<User> userOptional = userMapper.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
        }
        
        User user = userOptional.get();
        Long userId = user.getId();
        
        // 验证验证码并加入聊天室
        ChatRoom chatRoom = chatRoomService.joinRoomByVerificationCode(restaurantId, verificationCode, userId);
        
        // 生成临时JWT令牌（用于WebSocket连接）
        String tempToken = jwtService.generateTempToken(user, chatRoom.getId());
        
        // 创建安全的聊天室DTO，不包含敏感信息
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.fromEntity(chatRoom);
        
        // 创建响应
        ChatRoomTokenResponse response = new ChatRoomTokenResponse(
            chatRoomDTO,
            tempToken,
            jwtService.getExpirationTime()
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "获取聊天室信息", description = "根据ID获取聊天室信息")
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomDTO>> getRoomById(
            @Parameter(description = "聊天室ID") @PathVariable Long roomId) {
        
        ChatRoom chatRoom = chatRoomService.getById(roomId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        // 返回安全的聊天室DTO，不包含敏感信息
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.fromEntity(chatRoom);
        return ResponseEntity.ok(ApiResponse.success(chatRoomDTO));
    }
    
    @Operation(summary = "获取餐厅聊天室", description = "根据餐厅ID获取聊天室信息")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<ChatRoomDTO>> getRoomByRestaurantId(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {
        
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        // 返回安全的聊天室DTO，不包含敏感信息
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.fromEntity(chatRoom);
        return ResponseEntity.ok(ApiResponse.success(chatRoomDTO));
    }
    
    @Operation(summary = "获取聊天室消息列表", description = "分页获取指定聊天室的消息列表")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatRoomMessage>>> getRoomMessages(
            @Parameter(description = "聊天室ID") @PathVariable Long roomId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        
        Page<ChatRoomMessage> messages = chatRoomService.getRoomMessages(roomId, page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "获取聊天室成员列表", description = "获取指定聊天室的成员列表")
    @GetMapping("/{roomId}/members")
    public ResponseEntity<ApiResponse<List<ChatRoomMember>>> getRoomMembers(
            @Parameter(description = "聊天室ID") @PathVariable Long roomId) {
        
        List<ChatRoomMember> members = chatRoomService.getRoomMembers(roomId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
    
    @Operation(summary = "离开聊天室", description = "用户离开指定聊天室")
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @Parameter(description = "聊天室ID") @PathVariable Long roomId) {
        
        // 从SecurityContext中获取当前登录用户的ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录"));
        }
        
        // 从认证信息中获取用户email，然后查询用户ID
        String userEmail = authentication.getName();
        Optional<User> userOptional = userMapper.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
        }
        
        Long userId = userOptional.get().getId();
        
        chatRoomService.leaveRoom(roomId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
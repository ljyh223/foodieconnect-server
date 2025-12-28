package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatRoom;
import com.ljyh.tabletalk.entity.ChatRoomMember;
import com.ljyh.tabletalk.entity.ChatRoomMessage;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家聊天室控制器
 */
@Tag(name = "商家聊天室管理", description = "商家端聊天室相关接口")
@RestController
@RequestMapping("/merchant/chat-rooms")
@RequiredArgsConstructor
public class MerchantChatRoomController {
    
    private final ChatRoomService chatRoomService;
    private final MerchantAuthService merchantAuthService;
    private final RestaurantService restaurantService;
    

    
    @Operation(summary = "获取聊天室信息", description = "获取商家餐厅的聊天室信息")
    @GetMapping
    public ResponseEntity<ApiResponse<ChatRoom>> getChatRoom() {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("CHAT_ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(chatRoom));
    }
    

    
    @Operation(summary = "获取聊天室消息列表", description = "分页获取餐厅聊天室的消息列表")
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<Page<ChatRoomMessage>>> getRoomMessages(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 获取餐厅聊天室
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("CHAT_ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        Page<ChatRoomMessage> messages = chatRoomService.getRoomMessages(chatRoom.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "获取聊天室成员列表", description = "获取餐厅聊天室的成员列表")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<ChatRoomMember>>> getRoomMembers() {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 获取餐厅聊天室
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("CHAT_ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        List<ChatRoomMember> members = chatRoomService.getRoomMembers(chatRoom.getId());
        return ResponseEntity.ok(ApiResponse.success(members));
    }
    
    @Operation(summary = "离开聊天室", description = "商家离开餐厅聊天室")
    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRoom() {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long merchantId = currentMerchant.getId();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 获取餐厅聊天室
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("CHAT_ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        chatRoomService.leaveRoom(chatRoom.getId(), merchantId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
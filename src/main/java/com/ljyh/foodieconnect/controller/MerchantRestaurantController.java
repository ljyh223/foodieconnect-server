package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.RestaurantUpdateRequest;
import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.entity.Restaurant;
import com.ljyh.foodieconnect.service.ChatRoomService;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商家餐厅管理控制器
 */
@Tag(name = "商家餐厅管理", description = "商家端餐厅信息管理接口")
@RestController
@RequestMapping("/merchant/restaurants")
@RequiredArgsConstructor
public class MerchantRestaurantController {
    
    private final RestaurantService restaurantService;
    private final MerchantAuthService merchantAuthService;
    private final ChatRoomService chatRoomService;
    
    @Operation(summary = "获取餐厅信息", description = "获取当前商家管理的餐厅详细信息")
    @GetMapping
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurant() {
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(restaurant));
    }
    
    @Operation(summary = "更新餐厅信息", description = "更新餐厅的基本信息")
    @PutMapping
    public ResponseEntity<ApiResponse<Restaurant>> updateRestaurant(
            @Valid @RequestBody RestaurantUpdateRequest request) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 验证权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        restaurant.setName(request.getName());
        restaurant.setType(request.getType());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setHours(request.getHours());
        restaurant.setImageUrl(request.getImageUrl());
        if (request.getIsOpen() != null) {
            restaurant.setIsOpen(request.getIsOpen());
        }
        
        restaurantService.updateById(restaurant);
        
        return ResponseEntity.ok(ApiResponse.success(restaurant));
    }
    
    @Operation(summary = "更新餐厅营业状态", description = "更新餐厅的营业状态")
    @PutMapping("/status")
    public ResponseEntity<ApiResponse<Void>> updateRestaurantStatus(
            @Parameter(description = "是否营业") @RequestParam(required = false) Boolean isOpen) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 验证权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        
        // 如果没有提供isOpen参数，则切换当前状态
        if (isOpen == null) {
            isOpen = !restaurant.getIsOpen();
        }
        
        restaurant.setIsOpen(isOpen);
        
        restaurantService.updateById(restaurant);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "更新餐厅图片", description = "更新餐厅的展示图片")
    @PutMapping("/image")
    public ResponseEntity<ApiResponse<String>> updateRestaurantImage(
            @Parameter(description = "图片URL") @RequestParam String imageUrl) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 验证权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        restaurant.setImageUrl(imageUrl);
        
        restaurantService.updateById(restaurant);
        
        return ResponseEntity.ok(ApiResponse.success(imageUrl));
    }
    
    @Operation(summary = "获取餐厅详情（用户视角）", description = "获取餐厅的详细信息，包括推荐菜品和聊天室信息")
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<Object>> getRestaurantDetail() {
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        Object restaurantDetail = restaurantService.getRestaurantDetail(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(restaurantDetail));
    }
    
    @Operation(summary = "更新餐厅评分", description = "手动更新餐厅评分（管理员功能）")
    @PutMapping("/rating")
    public ResponseEntity<ApiResponse<Void>> updateRestaurantRating(
            @Parameter(description = "新评分") @RequestParam Double rating) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 验证权限 - 只有管理员可以更新评分
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        restaurantService.updateRestaurantRating(restaurantId);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取当前聊天室验证码", description = "获取餐厅聊天室的当前进入验证码")
    @GetMapping("/chat-room/verification-code")
    public ResponseEntity<ApiResponse<Object>> getChatRoomVerificationCode() {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        Long restaurantId = currentMerchant.getRestaurantId();
        
        // 验证权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        ChatRoom chatRoom = chatRoomService.getRestaurantChatRoom(restaurantId);
        if (chatRoom == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("CHAT_ROOM_NOT_FOUND", "聊天室不存在"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(chatRoom));
    }
}
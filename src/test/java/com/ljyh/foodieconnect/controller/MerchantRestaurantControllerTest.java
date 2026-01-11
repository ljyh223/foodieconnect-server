package com.ljyh.foodieconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.foodieconnect.dto.RestaurantUpdateRequest;
import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.entity.Restaurant;
import com.ljyh.foodieconnect.service.ChatRoomService;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantRestaurantControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private RestaurantService restaurantService;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @Mock
    private ChatRoomService chatRoomService;
    
    @InjectMocks
    private MerchantRestaurantController merchantRestaurantController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantRestaurantController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        // 模拟当前商家信息
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.MANAGER);
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
    }
    
    @Test
    void testGetRestaurantSuccess() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setType("中餐");
        restaurant.setDescription("一家测试餐厅");
        restaurant.setAddress("测试地址");
        restaurant.setPhone("13800138000");
        restaurant.setHours("10:00-22:00");
        restaurant.setIsOpen(true);
        
        // 模拟服务调用
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(restaurant);
        
        // 执行测试
        mockMvc.perform(get("/merchant/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.type").value("中餐"));
    }
    
    @Test
    void testUpdateRestaurantSuccess() throws Exception {
        // 准备测试数据
        RestaurantUpdateRequest updateRequest = new RestaurantUpdateRequest();
        updateRequest.setName("更新后的餐厅");
        updateRequest.setType("西餐");
        updateRequest.setDescription("更新后的餐厅描述");
        updateRequest.setAddress("更新后的地址");
        updateRequest.setPhone("13900139000");
        updateRequest.setHours("09:00-23:00");
        updateRequest.setIsOpen(true);
        updateRequest.setImageUrl("http://example.com/image.jpg");
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setType("中餐");
        restaurant.setDescription("一家测试餐厅");
        restaurant.setAddress("测试地址");
        restaurant.setPhone("13800138000");
        restaurant.setHours("10:00-22:00");
        restaurant.setIsOpen(false);
        
        // 模拟服务调用
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(restaurant);
        when(restaurantService.updateById(any(Restaurant.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(put("/merchant/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("更新后的餐厅"))
                .andExpect(jsonPath("$.data.type").value("西餐"))
                .andExpect(jsonPath("$.data.isOpen").value(true));
    }
    
    @Test
    void testUpdateRestaurantStatusSuccess() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setIsOpen(false);
        
        // 模拟服务调用
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(restaurant);
        when(restaurantService.updateById(any(Restaurant.class))).thenReturn(true);
        
        // 执行测试 - 开启餐厅
        mockMvc.perform(put("/merchant/restaurants/status")
                .param("isOpen", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testUpdateRestaurantStatusToggle() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setIsOpen(false);
        
        // 模拟服务调用
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(restaurant);
        when(restaurantService.updateById(any(Restaurant.class))).thenReturn(true);
        
        // 执行测试 - 切换状态
        mockMvc.perform(put("/merchant/restaurants/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testUpdateRestaurantImageSuccess() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setImageUrl("http://example.com/old-image.jpg");
        
        String newImageUrl = "http://example.com/new-image.jpg";
        
        // 模拟服务调用
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(restaurant);
        when(restaurantService.updateById(any(Restaurant.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(put("/merchant/restaurants/image")
                .param("imageUrl", newImageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(newImageUrl));
    }
    
    @Test
    void testGetRestaurantDetailSuccess() throws Exception {
        // 准备测试数据
        Map<String, Object> restaurantDetail = new HashMap<>();
        restaurantDetail.put("id", 1L);
        restaurantDetail.put("name", "测试餐厅");
        restaurantDetail.put("type", "中餐");
        
        // 模拟服务调用
        when(restaurantService.getRestaurantDetail(anyLong())).thenReturn(restaurantDetail);
        
        // 执行测试
        mockMvc.perform(get("/merchant/restaurants/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.type").value("中餐"));
    }
    
    @Test
    void testGetChatRoomVerificationCodeSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setVerificationCode("123456");
        
        // 模拟服务调用
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(chatRoom);
        
        // 执行测试
        mockMvc.perform(get("/merchant/restaurants/chat-room/verification-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.verificationCode").value("123456"));
    }
    
    @Test
    void testGetChatRoomVerificationCodeNotFound() throws Exception {
        // 模拟服务调用返回null
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(null);
        
        // 执行测试
        mockMvc.perform(get("/merchant/restaurants/chat-room/verification-code"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_ROOM_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("聊天室不存在"));
    }
}

package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.ChatRoom;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.JwtMerchantService;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MerchantChatRoomControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private MerchantAuthService merchantAuthService;

    @Mock
    private JwtMerchantService jwtMerchantService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private MerchantChatRoomController merchantChatRoomController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(merchantChatRoomController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testJoinRoomSuccess() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(100L);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(200L);
        chatRoom.setRestaurantId(100L);
        chatRoom.setName("餐厅聊天室");
        chatRoom.setStatus(ChatSessionStatus.ACTIVE);

        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        when(chatRoomService.joinRoomAsObserver(anyLong(), anyLong())).thenReturn(chatRoom);

        // 执行测试
        mockMvc.perform(post("/merchant/chat-rooms/join")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(200))
                .andExpect(jsonPath("$.data.name").value("餐厅聊天室"));
    }

    @Test
    void testVerifyAndGetTempTokenSuccess() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(100L);
        merchant.setUsername("merchant@test.com");
        merchant.setName("测试商家");

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(200L);
        chatRoom.setRestaurantId(100L);
        chatRoom.setName("餐厅聊天室");
        chatRoom.setStatus(ChatSessionStatus.ACTIVE);
        chatRoom.setVerificationCode("123456");
        chatRoom.setVerificationCodeGeneratedAt(LocalDateTime.now());

        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(chatRoom);
        when(jwtMerchantService.generateTempToken(any(Merchant.class), anyLong())).thenReturn("temp-token-123");

        // 执行测试
        mockMvc.perform(get("/merchant/chat-rooms/verify")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.chatRoom.id").value(200))
                .andExpect(jsonPath("$.data.tempToken").value("temp-token-123"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600000));
    }

    @Test
    void testVerifyAndGetTempTokenNotFound() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(100L);

        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/merchant/chat-rooms/verify")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CHAT_ROOM_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("聊天室不存在"));
    }
}

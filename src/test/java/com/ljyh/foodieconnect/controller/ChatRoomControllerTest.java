package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.ChatRoomTokenResponse;
import com.ljyh.tabletalk.entity.ChatRoom;
import com.ljyh.tabletalk.entity.ChatRoomMember;
import com.ljyh.tabletalk.entity.ChatRoomMessage;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.service.ChatRoomService;
import com.ljyh.tabletalk.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChatRoomControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private ChatRoomService chatRoomService;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private ChatRoomController chatRoomController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        
        // 模拟认证信息
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // 模拟用户查询
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setAvatarUrl("http://example.com/avatar.jpg");
        
        when(userMapper.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    }
    
    @Test
    void testJoinRoomSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setName("Test Restaurant Chat");
        chatRoom.setVerificationCode("123456");
        
        // 模拟服务调用
        when(chatRoomService.joinRoomByVerificationCode(anyLong(), anyString(), anyLong())).thenReturn(chatRoom);
        
        // 执行测试
        mockMvc.perform(post("/chat-rooms/join")
                .param("restaurantId", "1")
                .param("verificationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.restaurantId").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Restaurant Chat"));
    }
    
    @Test
    void testJoinRoomAsObserverSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setName("Test Restaurant Chat");
        
        // 模拟服务调用
        when(chatRoomService.joinRoomAsObserver(anyLong(), anyLong())).thenReturn(chatRoom);
        
        // 执行测试
        mockMvc.perform(post("/chat-rooms/join-as-observer")
                .param("restaurantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.restaurantId").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Restaurant Chat"));
    }
    
    @Test
    void testVerifyAndJoinRoomSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setName("Test Restaurant Chat");
        
        // 模拟服务调用
        when(chatRoomService.joinRoomByVerificationCode(anyLong(), anyString(), anyLong())).thenReturn(chatRoom);
        when(jwtService.generateTempToken(any(User.class), anyLong())).thenReturn("test-temp-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/verify")
                .param("restaurantId", "1")
                .param("verificationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.chatRoom.id").value(1L))
                .andExpect(jsonPath("$.data.tempToken").value("test-temp-token"));
    }
    
    @Test
    void testGetRoomByIdSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setName("Test Restaurant Chat");
        
        // 模拟服务调用
        when(chatRoomService.getById(anyLong())).thenReturn(chatRoom);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Restaurant Chat"));
    }
    
    @Test
    void testGetRoomByIdNotFound() throws Exception {
        // 模拟服务调用返回null
        when(chatRoomService.getById(anyLong())).thenReturn(null);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ROOM_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("聊天室不存在"));
    }
    
    @Test
    void testGetRoomByRestaurantIdSuccess() throws Exception {
        // 准备测试数据
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(1L);
        chatRoom.setName("Test Restaurant Chat");
        
        // 模拟服务调用
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(chatRoom);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.restaurantId").value(1L));
    }
    
    @Test
    void testGetRoomByRestaurantIdNotFound() throws Exception {
        // 模拟服务调用返回null
        when(chatRoomService.getRestaurantChatRoom(anyLong())).thenReturn(null);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/restaurant/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ROOM_NOT_FOUND"));
    }
    
    @Test
    void testSendMessageSuccess() throws Exception {
        // 准备测试数据
        ChatRoomMessage message = new ChatRoomMessage();
        message.setId(1L);
        message.setRoomId(1L);
        message.setSenderId(1L);
        message.setContent("Hello, World!");
        
        // 模拟服务调用
        when(chatRoomService.sendMessage(anyLong(), anyLong(), anyString())).thenReturn(message);
        
        // 执行测试
        mockMvc.perform(post("/chat-rooms/1/messages")
                .param("content", "Hello, World!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Hello, World!"))
                .andExpect(jsonPath("$.data.senderName").value("Test User"))
                .andExpect(jsonPath("$.data.senderAvatar").value("http://example.com/avatar.jpg"));
    }
    
    @Test
    void testGetRoomMessagesSuccess() throws Exception {
        // 准备测试数据
        Page<ChatRoomMessage> page = new Page<>();
        page.setCurrent(0);
        page.setSize(50);
        page.setTotal(1);
        
        ChatRoomMessage message = new ChatRoomMessage();
        message.setId(1L);
        message.setRoomId(1L);
        message.setContent("Test message");
        
        page.setRecords(List.of(message));
        
        // 模拟服务调用
        when(chatRoomService.getRoomMessages(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/1/messages")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].content").value("Test message"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetRoomMembersSuccess() throws Exception {
        // 准备测试数据
        ChatRoomMember member = new ChatRoomMember();
        member.setId(1L);
        member.setRoomId(1L);
        member.setUserId(1L);
        
        // 模拟服务调用
        when(chatRoomService.getRoomMembers(anyLong())).thenReturn(List.of(member));
        
        // 执行测试
        mockMvc.perform(get("/chat-rooms/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(1L));
    }
    
    @Test
    void testLeaveRoomSuccess() throws Exception {
        // 模拟服务调用
        doNothing().when(chatRoomService).leaveRoom(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(post("/chat-rooms/1/leave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testUnauthorized() throws Exception {
        // 清除认证信息
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        
        // 执行测试
        mockMvc.perform(post("/chat-rooms/join")
                .param("restaurantId", "1")
                .param("verificationCode", "123456"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }
}
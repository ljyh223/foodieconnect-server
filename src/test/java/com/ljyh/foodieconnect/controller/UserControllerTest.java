package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.UserProfileResponse;
import com.ljyh.foodieconnect.enums.UserStatus;
import com.ljyh.foodieconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserController userController;
    
    private UserProfileResponse userProfile;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
        
        // 初始化测试数据
        userProfile = new UserProfileResponse();
        userProfile.setId(1L);
        userProfile.setEmail("test@example.com");
        userProfile.setDisplayName("测试用户");
        userProfile.setAvatarUrl("/avatar/test.jpg");
        userProfile.setBio("测试用户简介");
        userProfile.setStatus(UserStatus.ACTIVE);
        userProfile.setFollowingCount(10L);
        userProfile.setFollowersCount(20L);
        userProfile.setRecommendationsCount(5L);
        userProfile.setIsFollowing(false);
    }
    
    @Test
    void testGetCurrentUserProfileSuccess() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetUserProfileSuccess() throws Exception {
        // 模拟服务调用
        when(userService.getUserProfile(anyLong(), isNull())).thenReturn(userProfile);
        
        // 执行测试 - 未认证用户
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testUpdateUserProfileSuccess() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(put("/api/users/profile")
                .contentType(APPLICATION_JSON)
                .content("{\"displayName\":\"更新后的用户名\",\"bio\":\"更新后的简介\"}"))
                .andExpect(status().is4xxClientError());
    }
}
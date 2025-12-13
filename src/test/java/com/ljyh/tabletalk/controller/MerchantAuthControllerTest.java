package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.MerchantLoginRequest;
import com.ljyh.tabletalk.dto.MerchantLoginResponse;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.service.MerchantAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantAuthControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantAuthController merchantAuthController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantAuthController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testLoginSuccess() throws Exception {
        // 准备测试数据
        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("testmerchant");
        loginRequest.setPassword("password123");
        
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername("testmerchant");
        merchant.setName("Test Merchant");
        merchant.setEmail("test@example.com");
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.ADMIN);
        merchant.setPhone("13800138000");
        
        MerchantAuthService.MerchantLoginResult loginResult = 
            new MerchantAuthService.MerchantLoginResult("test-token", merchant);
        
        // 模拟服务调用
        when(merchantAuthService.login(anyString(), anyString())).thenReturn(loginResult);
        
        // 执行测试
        mockMvc.perform(post("/merchant/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.merchantId").value(1L))
                .andExpect(jsonPath("$.data.username").value("testmerchant"))
                .andExpect(jsonPath("$.data.name").value("Test Merchant"));
    }
    
    @Test
    void testLoginFailure() throws Exception {
        // 准备测试数据
        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setUsername("invalidmerchant");
        loginRequest.setPassword("invalidpassword");
        
        // 模拟服务调用抛出异常
        when(merchantAuthService.login(anyString(), anyString()))
            .thenThrow(new com.ljyh.tabletalk.exception.BusinessException("LOGIN_FAILED", "用户名或密码错误"));
        
        // 执行测试
        mockMvc.perform(post("/merchant/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("LOGIN_FAILED"))
                .andExpect(jsonPath("$.error.message").value("用户名或密码错误"));
    }
}

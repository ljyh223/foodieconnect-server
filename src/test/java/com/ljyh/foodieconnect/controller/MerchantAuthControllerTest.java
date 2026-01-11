package com.ljyh.foodieconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.foodieconnect.dto.MerchantLoginRequest;
import com.ljyh.foodieconnect.dto.MerchantLoginResponse;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
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
            .thenThrow(new com.ljyh.foodieconnect.exception.BusinessException("LOGIN_FAILED", "用户名或密码错误"));
        
        // 执行测试
        mockMvc.perform(post("/merchant/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("LOGIN_FAILED"))
                .andExpect(jsonPath("$.error.message").value("用户名或密码错误"));
    }
    
    @Test
    void testRegisterSuccess() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername("newmerchant");
        merchant.setName("New Merchant");
        merchant.setEmail("new@example.com");
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.STAFF);
        
        // 模拟服务调用
        when(merchantAuthService.register(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyLong(), any(Merchant.MerchantRole.class))).thenReturn(merchant);
        
        // 执行测试
        mockMvc.perform(post("/merchant/auth/register")
                .param("username", "newmerchant")
                .param("email", "new@example.com")
                .param("password", "password123")
                .param("name", "New Merchant")
                .param("phone", "13800138000")
                .param("restaurantId", "1")
                .param("role", "STAFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("newmerchant"))
                .andExpect(jsonPath("$.data.role").value("STAFF"));
    }
    
    @Test
    void testChangePasswordSuccess() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername("testmerchant");
        merchant.setName("Test Merchant");
        
        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        
        // 执行测试
        mockMvc.perform(put("/merchant/auth/change-password")
                .param("oldPassword", "oldpassword")
                .param("newPassword", "newpassword123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetProfileSuccess() throws Exception {
        // 准备测试数据
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setUsername("testmerchant");
        merchant.setName("Test Merchant");
        merchant.setEmail("test@example.com");
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.ADMIN);
        
        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        
        // 执行测试
        mockMvc.perform(get("/merchant/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testmerchant"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }
    
    @Test
    void testLogoutSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(post("/merchant/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

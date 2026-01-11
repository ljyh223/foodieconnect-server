package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.LoginRequest;
import com.ljyh.foodieconnect.dto.LoginResponse;
import com.ljyh.foodieconnect.dto.RegisterRequest;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController authController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testRegisterSuccess() throws Exception {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setDisplayName("Test User");
        
        // 模拟服务调用
        when(authService.register(any(RegisterRequest.class))).thenReturn(userDTO);
        
        // 执行测试
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.displayName").value("Test User"));
    }
    
    @Test
    void testRegisterFailure() throws Exception {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");
        
        // 模拟服务调用
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new BusinessException("REGISTER_FAILED", "用户已存在"));
        
        // 执行测试
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("REGISTER_FAILED"))
                .andExpect(jsonPath("$.error.message").value("用户已存在"));
    }
    
    @Test
    void testLoginSuccess() throws Exception {
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("test-access-token");
        loginResponse.setRefreshToken("test-refresh-token");
        loginResponse.setTokenType("Bearer");
        
        // 模拟服务调用
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);
        
        // 执行测试
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("test-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("test-refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }
    
    @Test
    void testLoginFailure() throws Exception {
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid@example.com");
        loginRequest.setPassword("invalidpassword");
        
        // 模拟服务调用
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BusinessException("LOGIN_FAILED", "用户名或密码错误"));
        
        // 执行测试
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("LOGIN_FAILED"))
                .andExpect(jsonPath("$.error.message").value("用户名或密码错误"));
    }
    
    @Test
    void testRefreshTokenSuccess() throws Exception {
        // 准备测试数据
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        loginResponse.setTokenType("Bearer");
        
        // 模拟服务调用
        when(authService.refreshToken(anyString())).thenReturn(loginResponse);
        
        // 执行测试
        mockMvc.perform(post("/auth/refresh")
                .header("Authorization", "Bearer test-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }
    
    @Test
    void testGetCurrentUserSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setDisplayName("Test User");
        
        // 模拟服务调用
        when(authService.getCurrentUser(anyString())).thenReturn(userDTO);
        
        // 执行测试
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.displayName").value("Test User"));
    }
    
    @Test
    void testGetCurrentUserWithExpiredToken() throws Exception {
        // 模拟服务调用抛出过期令牌异常
        when(authService.getCurrentUser(anyString()))
            .thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"));
        
        // 执行测试
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.error.message").value("令牌已过期，请重新登录"));
    }
    
    @Test
    void testGetCurrentUserWithInvalidToken() throws Exception {
        // 模拟服务调用抛出无效令牌异常
        when(authService.getCurrentUser(anyString()))
            .thenThrow(new IllegalArgumentException("Invalid token"));
        
        // 执行测试
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.error.message").value("无效的令牌"));
    }
    
    @Test
    void testExtractToken() throws Exception {
        // 测试提取令牌方法的异常情况
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Invalid-Header"))
                .andExpect(status().isBadRequest());
    }
}
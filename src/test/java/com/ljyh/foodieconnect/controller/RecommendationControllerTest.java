package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.UserRecommendationRequest;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.entity.UserRestaurantRecommendation;
import com.ljyh.tabletalk.service.RecommendationService;
import com.ljyh.tabletalk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private RecommendationService recommendationService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private RecommendationController recommendationController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testRecommendRestaurantSuccess() throws Exception {
        // 准备测试数据
        UserRecommendationRequest request = new UserRecommendationRequest();
        request.setRestaurantId(1L);
        request.setRating(new BigDecimal("5.0"));
        request.setReason("这家餐厅非常棒！");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setDisplayName("Test User");
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(1L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("5.0"));
        recommendation.setReason("这家餐厅非常棒！");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(recommendationService.recommendRestaurant(anyLong(), any(UserRecommendationRequest.class))).thenReturn(recommendation);
        
        // 执行测试
        mockMvc.perform(post("/api/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.rating").value(5.0))
                .andExpect(jsonPath("$.data.reason").value("这家餐厅非常棒！"));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdateRecommendationSuccess() throws Exception {
        // 准备测试数据
        UserRecommendationRequest request = new UserRecommendationRequest();
        request.setRestaurantId(1L);
        request.setRating(new BigDecimal("4.0"));
        request.setReason("这家餐厅不错！");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(1L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("4.0"));
        recommendation.setReason("这家餐厅不错！");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(recommendationService.updateRecommendation(anyLong(), anyLong(), any(UserRecommendationRequest.class))).thenReturn(recommendation);
        
        // 执行测试
        mockMvc.perform(put("/api/recommendations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(4.0))
                .andExpect(jsonPath("$.data.reason").value("这家餐厅不错！"));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testDeleteRecommendationSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(recommendationService).deleteRecommendation(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(delete("/api/recommendations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetMyRecommendationsSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        Page<UserRestaurantRecommendation> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(1L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("5.0"));
        
        page.setRecords(List.of(recommendation));
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(recommendationService.getUserRecommendations(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/my")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].rating").value(5.0))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetUserRecommendationsSuccess() throws Exception {
        // 准备测试数据
        Page<UserRestaurantRecommendation> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(2L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("5.0"));
        
        page.setRecords(List.of(recommendation));
        
        // 模拟服务调用
        when(recommendationService.getUserRecommendations(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/user/2")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].rating").value(5.0))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetRestaurantRecommendationsSuccess() throws Exception {
        // 准备测试数据
        Page<UserRestaurantRecommendation> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(1L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("5.0"));
        
        page.setRecords(List.of(recommendation));
        
        // 模拟服务调用
        when(recommendationService.getRestaurantRecommendations(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/restaurant/1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].rating").value(5.0))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetPopularRecommendationsSuccess() throws Exception {
        // 准备测试数据
        UserRestaurantRecommendation recommendation = new UserRestaurantRecommendation();
        recommendation.setId(1L);
        recommendation.setUserId(1L);
        recommendation.setRestaurantId(1L);
        recommendation.setRating(new BigDecimal("5.0"));
        
        // 模拟服务调用
        when(recommendationService.getPopularRecommendations(anyInt())).thenReturn(List.of(recommendation));
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/popular")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].rating").value(5.0));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetMyAverageRatingSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(recommendationService.getUserAverageRating(anyLong())).thenReturn(4.5);
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/my-average-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(4.5));
    }
    
    @Test
    void testGetRestaurantAverageRatingSuccess() throws Exception {
        // 模拟服务调用
        when(recommendationService.getRestaurantAverageRating(anyLong())).thenReturn(4.2);
        
        // 执行测试
        mockMvc.perform(get("/api/recommendations/restaurant/1/average-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(4.2));
    }
}
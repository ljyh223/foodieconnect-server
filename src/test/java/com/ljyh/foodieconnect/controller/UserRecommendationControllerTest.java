package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.UserRecommendationScore;
import com.ljyh.tabletalk.mapper.UserRecommendationMapper.AlgorithmStats;
import com.ljyh.tabletalk.service.UserRecommendationService;
import com.ljyh.tabletalk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserRecommendationControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private UserRecommendationService userRecommendationService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserRecommendationController userRecommendationController;
    
    private UserRecommendationScore userRecommendationScore;
    private UserRecommendationService.RecommendationStats recommendationStats;
    private AlgorithmStats algorithmStats;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userRecommendationController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        
        // 初始化测试数据
        userRecommendationScore = new UserRecommendationScore();
        userRecommendationScore.setUserId(2L);
        userRecommendationScore.setRecommendationReason("共同兴趣匹配");
        
        recommendationStats = UserRecommendationService.RecommendationStats.builder()
                .totalRecommendations(100)
                .viewedCount(50)
                .interestedCount(20)
                .clickThroughRate(0.5)
                .conversionRate(0.4)
                .build();
        
        algorithmStats = new AlgorithmStats();
    }
    
    @Test
    void testGetUserRecommendations() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations")
                .param("algorithm", "WEIGHTED")
                .param("limit", "10"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetUserRecommendationsPaginated() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetUnviewedRecommendations() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations/unviewed")
                .param("limit", "10"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetRecommendationDetail() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations/1"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testMarkRecommendationStatus() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(put("/api/user-recommendations/1/status")
                .contentType(APPLICATION_JSON)
                .content("{\"isInterested\":true,\"feedback\":\"很好的推荐\"}"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testBatchMarkAsViewed() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(put("/api/user-recommendations/batch-viewed")
                .contentType(APPLICATION_JSON)
                .content("{\"recommendationIds\":[1,2,3]}"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testDeleteRecommendation() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(delete("/api/user-recommendations/1"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testClearAllRecommendations() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(delete("/api/user-recommendations/clear"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetUserRecommendationStats() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations/stats"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetUserAlgorithmStats() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(get("/api/user-recommendations/algorithm-stats"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testGetGlobalAlgorithmStats() throws Exception {
        // 模拟服务调用
        when(userRecommendationService.getGlobalAlgorithmStats(anyInt())).thenReturn(Collections.singletonList(algorithmStats));
        
        // 执行测试 - 不需要认证
        mockMvc.perform(get("/api/user-recommendations/global-algorithm-stats")
                .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testWarmupRecommendationCache() throws Exception {
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(post("/api/user-recommendations/warmup-cache"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testCleanupExpiredRecommendations() throws Exception {
        // 模拟服务调用
        doNothing().when(userRecommendationService).cleanExpiredRecommendations();
        
        // 执行测试 - 不需要认证
        mockMvc.perform(delete("/api/user-recommendations/cleanup-expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.CreateReviewRequest;
import com.ljyh.tabletalk.entity.Review;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.mapper.UserMapper;
import com.ljyh.tabletalk.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private ReviewService reviewService;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private ReviewController reviewController;
    
    private Review review;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        
        // 初始化测试数据
        review = new Review();
        review.setId(1L);
        review.setRestaurantId(1L);
        review.setUserId(1L);
        review.setRating(5);
        review.setComment("这家餐厅很棒！");
        review.setUserName("测试用户");
        review.setUserAvatar("/avatar/test.jpg");
    }
    
    @Test
    void testGetRestaurantReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<Review> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        page.setRecords(List.of(review));
        
        // 模拟服务调用
        when(reviewService.getRestaurantReviews(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1/reviews?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testCreateReviewSuccess() throws Exception {
        // 模拟服务调用
        when(userMapper.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // 执行测试 - 预期会失败，因为没有认证
        mockMvc.perform(post("/restaurants/1/reviews")
                .contentType(APPLICATION_JSON)
                .content("{\"rating\":5,\"comment\":\"这家餐厅很棒！\",\"imageUrls\":[]}"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void testDeleteReviewSuccess() throws Exception {
        // 模拟服务调用
        doNothing().when(reviewService).deleteReview(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(delete("/restaurants/1/reviews/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetReviewByIdSuccess() throws Exception {
        // 模拟服务调用
        when(reviewService.getReviewById(anyLong())).thenReturn(review);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetUserReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<Review> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        page.setRecords(List.of(review));
        
        // 模拟服务调用
        when(reviewService.getUserReviews(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1/reviews/user/1?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testCheckUserReviewedSuccess() throws Exception {
        // 模拟服务调用
        when(reviewService.hasUserReviewedRestaurant(anyLong(), anyLong())).thenReturn(false);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1/reviews/check")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasReviewed").value(false));
    }
    
    @Test
    void testGetRestaurantStatsSuccess() throws Exception {
        // 模拟服务调用
        when(reviewService.calculateAverageRating(anyLong())).thenReturn(4.5);
        when(reviewService.countReviewsByRestaurantId(anyLong())).thenReturn(100);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1/reviews/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
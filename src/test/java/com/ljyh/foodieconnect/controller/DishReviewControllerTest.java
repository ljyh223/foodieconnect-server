package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.foodieconnect.dto.DishReviewRequest;
import com.ljyh.foodieconnect.dto.DishReviewResponse;
import com.ljyh.foodieconnect.dto.DishReviewStatsResponse;
import com.ljyh.foodieconnect.entity.DishReview;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.service.DishReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户端菜品评价控制器测试类
 */
class DishReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DishReviewService dishReviewService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DishReviewController dishReviewController;

    private ObjectMapper objectMapper;
    private User testUser;
    private DishReview testReview;
    private DishReviewResponse testResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dishReviewController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // 初始化测试数据 - 用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("测试用户");
        testUser.setAvatarUrl("/avatar/test.jpg");

        // 初始化测试数据 - 评价
        testReview = new DishReview();
        testReview.setId(1L);
        testReview.setMenuItemId(1L);
        testReview.setRestaurantId(1L);
        testReview.setUserId(1L);
        testReview.setRating(5);
        testReview.setComment("很好吃！");
        testReview.setCreatedAt(LocalDateTime.now());

        // 初始化测试数据 - 响应
        testResponse = new DishReviewResponse();
        testResponse.setId(1L);
        testResponse.setMenuItemId(1L);
        testResponse.setItemName("宫保鸡丁");
        testResponse.setItemPrice(new BigDecimal("38.00"));
        testResponse.setItemImage("/uploads/dish1.jpg");
        testResponse.setUserId(1L);
        testResponse.setUserName("测试用户");
        testResponse.setUserAvatar("/avatar/test.jpg");
        testResponse.setRating(5);
        testResponse.setComment("很好吃！");
        testResponse.setImages(Arrays.asList("/uploads/review1.jpg"));

        // 设置认证上下文
        setupAuthentication();
    }

    private void setupAuthentication() {
        when(userMapper.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@example.com", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCreateReviewSuccess() throws Exception {
        // 准备请求数据
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(5);
        request.setComment("很好吃！");
        request.setImages(Arrays.asList("/uploads/review1.jpg"));

        // 模拟服务调用
        when(dishReviewService.createReview(eq(1L), any(DishReviewRequest.class), eq(1L)))
                .thenReturn(testResponse);

        // 执行测试
        mockMvc.perform(post("/user/restaurants/1/menu-items/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.comment").value("很好吃！"));
    }

    @Test
    void testCreateReviewUnauthorized() throws Exception {
        // 清除认证
        SecurityContextHolder.clearContext();

        // 准备请求数据
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(5);
        request.setComment("很好吃！");

        // 执行测试 - 应该返回错误
        mockMvc.perform(post("/user/restaurants/1/menu-items/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testCreateReviewInvalidRating() throws Exception {
        // 准备无效的请求数据
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(6); // 无效评分
        request.setComment("很好吃！");

        // 执行测试 - 应该返回验证错误
        mockMvc.perform(post("/user/restaurants/1/menu-items/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 1);
        page.setRecords(Arrays.asList(testResponse));

        // 模拟服务调用
        when(dishReviewService.getMenuItemReviews(eq(1L), eq(0), eq(20), eq("latest")))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].id").value(1L))
                .andExpect(jsonPath("$.data.records[0].rating").value(5));
    }

    @Test
    void testGetReviewsDefaultParameters() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 0);

        // 模拟服务调用
        when(dishReviewService.getMenuItemReviews(eq(1L), eq(0), eq(20), eq("latest")))
                .thenReturn(page);

        // 执行测试 - 使用默认参数
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetMyReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 1);
        page.setRecords(Arrays.asList(testResponse));

        // 模拟服务调用
        when(dishReviewService.getUserReviews(eq(1L), eq(1L), eq(0), eq(20), isNull()))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/reviews/my")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].id").value(1L));
    }

    @Test
    void testGetMyReviewsWithFilters() throws Exception {
        // 准备测试数据
        Page<DishReviewResponse> page = new Page<>(0, 20, 1);
        page.setRecords(Arrays.asList(testResponse));

        // 模拟服务调用
        when(dishReviewService.getUserReviews(eq(1L), eq(1L), eq(0), eq(20), eq(2L)))
                .thenReturn(page);

        // 执行测试 - 带筛选条件
        mockMvc.perform(get("/user/restaurants/1/menu-items/reviews/my")
                        .param("page", "0")
                        .param("size", "20")
                        .param("menuItemId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateReviewSuccess() throws Exception {
        // 准备请求数据
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(4);
        request.setComment("还不错");

        // 更新响应
        DishReviewResponse updatedResponse = new DishReviewResponse();
        updatedResponse.setId(1L);
        updatedResponse.setRating(4);
        updatedResponse.setComment("还不错");

        // 模拟服务调用
        when(dishReviewService.updateReview(eq(1L), any(DishReviewRequest.class), eq(1L)))
                .thenReturn(updatedResponse);

        // 执行测试
        mockMvc.perform(put("/user/restaurants/1/menu-items/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(4))
                .andExpect(jsonPath("$.data.comment").value("还不错"));
    }

    @Test
    void testUpdateReviewUnauthorized() throws Exception {
        // 清除认证
        SecurityContextHolder.clearContext();

        // 准备请求数据
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(4);
        request.setComment("还不错");

        // 执行测试 - 应该返回错误
        mockMvc.perform(put("/user/restaurants/1/menu-items/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteReviewSuccess() throws Exception {
        // 模拟服务调用
        org.mockito.Mockito.doNothing().when(dishReviewService).deleteReview(eq(1L), eq(1L));

        // 执行测试
        mockMvc.perform(delete("/user/restaurants/1/menu-items/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteReviewUnauthorized() throws Exception {
        // 清除认证
        SecurityContextHolder.clearContext();

        // 执行测试 - 应该返回错误
        mockMvc.perform(delete("/user/restaurants/1/menu-items/reviews/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testCheckUserReviewedHasReview() throws Exception {
        // 模拟服务调用 - 已评价
        when(dishReviewService.checkUserReview(eq(1L), eq(1L))).thenReturn(testReview);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasReviewed").value(true))
                .andExpect(jsonPath("$.data.review.id").value(1L))
                .andExpect(jsonPath("$.data.review.rating").value(5));
    }

    @Test
    void testCheckUserReviewedNoReview() throws Exception {
        // 模拟服务调用 - 未评价
        when(dishReviewService.checkUserReview(eq(1L), eq(1L))).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasReviewed").value(false))
                .andExpect(jsonPath("$.data.review").isEmpty());
    }

    @Test
    void testGetReviewStatsSuccess() throws Exception {
        // 准备统计数据
        DishReviewStatsResponse stats = new DishReviewStatsResponse();
        stats.setAverageRating(BigDecimal.valueOf(4.5));
        stats.setTotalReviews(100);

        Map<Integer, Integer> distribution = new HashMap<>();
        distribution.put(5, 60);
        distribution.put(4, 25);
        distribution.put(3, 10);
        distribution.put(2, 3);
        distribution.put(1, 2);
        stats.setRatingDistribution(distribution);

        // 模拟服务调用
        when(dishReviewService.getReviewStats(eq(1L))).thenReturn(stats);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").value(4.5))
                .andExpect(jsonPath("$.data.totalReviews").value(100))
                .andExpect(jsonPath("$.data.ratingDistribution.5").value(60))
                .andExpect(jsonPath("$.data.ratingDistribution.4").value(25));
    }

    @Test
    void testGetReviewStatsNoReviews() throws Exception {
        // 准备空统计数据
        DishReviewStatsResponse stats = new DishReviewStatsResponse();
        stats.setAverageRating(BigDecimal.ZERO);
        stats.setTotalReviews(0);
        stats.setRatingDistribution(new HashMap<>());

        // 模拟服务调用
        when(dishReviewService.getReviewStats(eq(1L))).thenReturn(stats);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").value(0))
                .andExpect(jsonPath("$.data.totalReviews").value(0));
    }

    @Test
    void testGetReviewByIdSuccess() throws Exception {
        // 模拟服务调用
        when(dishReviewService.getReviewById(eq(1L))).thenReturn(testResponse);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.comment").value("很好吃！"));
    }

    @Test
    void testGetReviewByIdNotFound() throws Exception {
        // 模拟服务调用 - 抛出异常
        when(dishReviewService.getReviewById(eq(999L)))
                .thenThrow(new com.ljyh.foodieconnect.exception.BusinessException("REVIEW_NOT_FOUND", "评价不存在"));

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews/999"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateReviewWithEmptyComment() throws Exception {
        // 准备请求数据 - 评论为空（允许）
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(5);
        request.setComment(null); // 评论可选

        // 模拟服务调用
        when(dishReviewService.createReview(eq(1L), any(DishReviewRequest.class), eq(1L)))
                .thenReturn(testResponse);

        // 执行测试
        mockMvc.perform(post("/user/restaurants/1/menu-items/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCreateReviewWithImages() throws Exception {
        // 准备请求数据 - 包含多张图片
        DishReviewRequest request = new DishReviewRequest();
        request.setRating(5);
        request.setComment("很好吃！");
        request.setImages(Arrays.asList("/uploads/img1.jpg", "/uploads/img2.jpg", "/uploads/img3.jpg"));

        // 模拟服务调用
        when(dishReviewService.createReview(eq(1L), any(DishReviewRequest.class), eq(1L)))
                .thenReturn(testResponse);

        // 执行测试
        mockMvc.perform(post("/user/restaurants/1/menu-items/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetReviewsPagination() throws Exception {
        // 准备测试数据 - 第2页
        Page<DishReviewResponse> page = new Page<>(1, 10, 25);
        page.setRecords(Arrays.asList(testResponse));

        // 模拟服务调用
        when(dishReviewService.getMenuItemReviews(eq(1L), eq(1), eq(10), eq("latest")))
                .thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/user/restaurants/1/menu-items/1/reviews")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(25));
    }
}

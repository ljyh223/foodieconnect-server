package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.Review;
import com.ljyh.tabletalk.entity.StaffReview;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.ReviewService;
import com.ljyh.tabletalk.service.StaffReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MerchantReviewControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private ReviewService reviewService;
    
    @Mock
    private StaffReviewService staffReviewService;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantReviewController merchantReviewController;
    
    private Review review;
    private StaffReview staffReview;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantReviewController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        
        // 模拟当前商家信息
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.ADMIN);
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
        
        // 初始化测试数据
        review = new Review();
        review.setId(1L);
        review.setRestaurantId(1L);
        review.setUserId(1L);
        review.setRating(5);
        review.setComment("这家餐厅很棒！");
        review.setUserName("测试用户");
        review.setUserAvatar("/avatar/test.jpg");
        
        staffReview = new StaffReview();
        staffReview.setId(1L);
        staffReview.setStaffId(1L);
        staffReview.setUserId(1L);
        staffReview.setRating(java.math.BigDecimal.valueOf(5.0));
        staffReview.setContent("这个店员服务很好！");
        staffReview.setUserName("测试用户");
        staffReview.setUserAvatar("/avatar/test.jpg");
    }

    @Test
    void testGetReviewOverviewSuccess() throws Exception {
        // 模拟服务调用
        when(reviewService.getRestaurantAverageRating(anyLong())).thenReturn(4.5);
        when(reviewService.getRestaurantReviewCount(anyLong())).thenReturn(100);
        
        Page<Review> page = new Page<>();
        page.setCurrent(0);
        page.setSize(5);
        page.setTotal(1);
        page.setRecords(List.of(review));
        
        when(reviewService.getRestaurantReviews(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/merchant/reviews/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurant.averageRating").value(4.5))
                .andExpect(jsonPath("$.data.restaurant.reviewCount").value(100));
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
        mockMvc.perform(get("/merchant/reviews/restaurant?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].id").value(1L))
                .andExpect(jsonPath("$.data.records[0].rating").value(5));
    }

    @Test
    void testGetStaffReviewsSuccess() throws Exception {
        // 准备测试数据
        Page<StaffReview> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        page.setRecords(List.of(staffReview));
        
        // 模拟服务调用
        when(staffReviewService.getStaffReviews(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/merchant/reviews/staff/1?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].id").value(1L))
                .andExpect(jsonPath("$.data.records[0].rating").value(5));
    }
}

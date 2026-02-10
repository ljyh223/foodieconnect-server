package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.OverviewStatsResponse;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.mapper.DishReviewMapper;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.StatisticsOverviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StatisticsController单元测试
 */
class StatisticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatisticsOverviewService statisticsOverviewService;

    @Mock
    private MerchantAuthService merchantAuthService;

    @InjectMocks
    private StatisticsController statisticsController;

    private Merchant mockMerchant;
    private OverviewStatsResponse mockStatsResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();

        // 准备模拟商家数据
        mockMerchant = new Merchant();
        mockMerchant.setId(1L);
        mockMerchant.setRestaurantId(1L);

        // 准备模拟统计数据
        mockStatsResponse = OverviewStatsResponse.builder()
                .today(OverviewStatsResponse.TodayStats.builder()
                        .revenue(new BigDecimal("2345.00"))
                        .dishAverageRating(new BigDecimal("4.5"))
                        .staffAverageRating(new BigDecimal("4.2"))
                        .build())
                .monthly(OverviewStatsResponse.MonthlyStats.builder()
                        .year(2026)
                        .month(2)
                        .dailyData(List.of(
                                OverviewStatsResponse.DailyData.builder()
                                        .date(LocalDate.of(2026, 2, 1))
                                        .revenue(new BigDecimal("1500.00"))
                                        .orderCount(45)
                                        .build(),
                                OverviewStatsResponse.DailyData.builder()
                                        .date(LocalDate.of(2026, 2, 2))
                                        .revenue(new BigDecimal("1800.00"))
                                        .orderCount(52)
                                        .build()
                        ))
                        .build())
                .staffRatings(OverviewStatsResponse.StaffRatings.builder()
                        .highest(OverviewStatsResponse.StaffInfo.builder()
                                .staffId(3L)
                                .name("张三")
                                .rating(new BigDecimal("4.8"))
                                .avatarUrl("/uploads/staff1.jpg")
                                .build())
                        .lowest(OverviewStatsResponse.StaffInfo.builder()
                                .staffId(5L)
                                .name("李四")
                                .rating(new BigDecimal("3.2"))
                                .avatarUrl("/uploads/staff2.jpg")
                                .build())
                        .build())
                .build();
    }

    @Test
    void testGetOverviewStatsSuccess() throws Exception {
        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(mockMerchant);
        when(statisticsOverviewService.getOverviewStats(eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(mockStatsResponse);

        // 执行测试
        mockMvc.perform(get("/merchant/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.today.revenue").value(2345.00))
                .andExpect(jsonPath("$.data.today.dishAverageRating").value(4.5))
                .andExpect(jsonPath("$.data.today.staffAverageRating").value(4.2))
                .andExpect(jsonPath("$.data.monthly.year").value(2026))
                .andExpect(jsonPath("$.data.monthly.month").value(2))
                .andExpect(jsonPath("$.data.monthly.dailyData.length()").value(2))
                .andExpect(jsonPath("$.data.staffRatings.highest.staffId").value(3))
                .andExpect(jsonPath("$.data.staffRatings.highest.name").value("张三"))
                .andExpect(jsonPath("$.data.staffRatings.highest.rating").value(4.8))
                .andExpect(jsonPath("$.data.staffRatings.lowest.staffId").value(5))
                .andExpect(jsonPath("$.data.staffRatings.lowest.name").value("李四"))
                .andExpect(jsonPath("$.data.staffRatings.lowest.rating").value(3.2));

        // 验证服务调用
        verify(merchantAuthService, times(1)).getCurrentMerchant();
        verify(statisticsOverviewService, times(1)).getOverviewStats(1L, null, null, null);
    }

    @Test
    void testGetOverviewStatsWithCustomDate() throws Exception {
        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(mockMerchant);
        when(statisticsOverviewService.getOverviewStats(eq(1L), eq(LocalDate.of(2026, 2, 15)), eq(2026), eq(2)))
                .thenReturn(mockStatsResponse);

        // 执行测试 - 指定日期
        mockMvc.perform(get("/merchant/statistics/overview")
                .param("date", "2026-02-15")
                .param("year", "2026")
                .param("month", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.today.revenue").value(2345.00));

        // 验证服务调用
        verify(merchantAuthService, times(1)).getCurrentMerchant();
        verify(statisticsOverviewService, times(1)).getOverviewStats(
                eq(1L), eq(LocalDate.of(2026, 2, 15)), eq(2026), eq(2));
    }

    @Test
    void testGetOverviewStatsWithEmptyData() throws Exception {
        // 准备空数据响应
        OverviewStatsResponse emptyResponse = OverviewStatsResponse.builder()
                .today(OverviewStatsResponse.TodayStats.builder()
                        .revenue(BigDecimal.ZERO)
                        .dishAverageRating(BigDecimal.ZERO)
                        .staffAverageRating(BigDecimal.ZERO)
                        .build())
                .monthly(OverviewStatsResponse.MonthlyStats.builder()
                        .year(2026)
                        .month(2)
                        .dailyData(List.of())
                        .build())
                .staffRatings(OverviewStatsResponse.StaffRatings.builder()
                        .highest(null)
                        .lowest(null)
                        .build())
                .build();

        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(mockMerchant);
        when(statisticsOverviewService.getOverviewStats(eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(emptyResponse);

        // 执行测试
        mockMvc.perform(get("/merchant/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.today.revenue").value(0))
                .andExpect(jsonPath("$.data.today.dishAverageRating").value(0))
                .andExpect(jsonPath("$.data.monthly.dailyData.length()").value(0))
                .andExpect(jsonPath("$.data.staffRatings.highest").isEmpty())
                .andExpect(jsonPath("$.data.staffRatings.lowest").isEmpty());

        // 验证服务调用
        verify(statisticsOverviewService, times(1)).getOverviewStats(1L, null, null, null);
    }

    @Test
    void testGetOverviewStatsWithDifferentRestaurant() throws Exception {
        // 准备不同餐厅的商家
        Merchant merchant2 = new Merchant();
        merchant2.setId(2L);
        merchant2.setRestaurantId(2L);

        // 准备不同餐厅的响应数据
        OverviewStatsResponse response2 = OverviewStatsResponse.builder()
                .today(OverviewStatsResponse.TodayStats.builder()
                        .revenue(new BigDecimal("5000.00"))
                        .dishAverageRating(new BigDecimal("4.8"))
                        .staffAverageRating(new BigDecimal("4.5"))
                        .build())
                .monthly(OverviewStatsResponse.MonthlyStats.builder()
                        .year(2026)
                        .month(2)
                        .dailyData(List.of())
                        .build())
                .staffRatings(OverviewStatsResponse.StaffRatings.builder()
                        .build())
                .build();

        // 模拟服务调用
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant2);
        when(statisticsOverviewService.getOverviewStats(eq(2L), isNull(), isNull(), isNull()))
                .thenReturn(response2);

        // 执行测试
        mockMvc.perform(get("/merchant/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.today.revenue").value(5000.00))
                .andExpect(jsonPath("$.data.today.dishAverageRating").value(4.8));

        // 验证服务调用
        verify(statisticsOverviewService, times(1)).getOverviewStats(2L, null, null, null);
    }
}

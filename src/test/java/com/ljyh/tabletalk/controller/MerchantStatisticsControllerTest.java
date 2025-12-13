package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.MerchantStatistics;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.MerchantStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantStatisticsControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private MerchantStatisticsService merchantStatisticsService;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantStatisticsController merchantStatisticsController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantStatisticsController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // 模拟当前商家信息
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.ADMIN);
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
    }
    
    @Test
    void testGetTodayStatisticsSuccess() throws Exception {
        // 准备测试数据
        LocalDate today = LocalDate.now();
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setId(1L);
        statistics.setRestaurantId(1L);
        statistics.setStatDate(today);
        statistics.setTotalCustomers(100);
        statistics.setTotalOrders(50);
        statistics.setTotalRevenue(java.math.BigDecimal.valueOf(5000.0));
        statistics.setAverageOrderValue(java.math.BigDecimal.valueOf(100.0));
        
        // 模拟服务调用
        when(merchantStatisticsService.getStatisticsByDate(anyLong(), any(LocalDate.class))).thenReturn(statistics);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").value(100))
                .andExpect(jsonPath("$.data.totalOrders").value(50))
                .andExpect(jsonPath("$.data.totalRevenue").value(5000.0))
                .andExpect(jsonPath("$.data.averageOrderValue").value(100.0));
    }
    
    @Test
    void testGetStatisticsByDateSuccess() throws Exception {
        // 准备测试数据
        LocalDate date = LocalDate.now().minusDays(1);
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setId(1L);
        statistics.setRestaurantId(1L);
        statistics.setStatDate(date);
        statistics.setTotalCustomers(80);
        statistics.setTotalOrders(40);
        
        // 模拟服务调用
        when(merchantStatisticsService.getStatisticsByDate(anyLong(), any(LocalDate.class))).thenReturn(statistics);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/date")
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").value(80))
                .andExpect(jsonPath("$.data.totalOrders").value(40));
    }
    
    @Test
    void testGetStatisticsByDateRangeSuccess() throws Exception {
        // 准备测试数据
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<MerchantStatistics> statisticsList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            MerchantStatistics statistics = new MerchantStatistics();
            statistics.setId((long) i);
            statistics.setRestaurantId(1L);
            statistics.setStatDate(startDate.plusDays(i));
            statistics.setTotalCustomers(50 + i * 10);
            statistics.setTotalOrders(20 + i * 5);
            statisticsList.add(statistics);
        }
        
        // 模拟服务调用
        when(merchantStatisticsService.getStatisticsByDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(statisticsList);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(8))
                .andExpect(jsonPath("$.data[0].totalCustomers").value(50))
                .andExpect(jsonPath("$.data[7].totalCustomers").value(120));
    }
    
    @Test
    void testGetRecentStatisticsSuccess() throws Exception {
        // 准备测试数据
        List<MerchantStatistics> statisticsList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            MerchantStatistics statistics = new MerchantStatistics();
            statistics.setId((long) i);
            statistics.setRestaurantId(1L);
            statistics.setStatDate(LocalDate.now().minusDays(6 - i));
            statistics.setTotalCustomers(60 + i * 5);
            statisticsList.add(statistics);
        }
        
        // 模拟服务调用
        when(merchantStatisticsService.getRecentStatistics(anyLong(), anyInt())).thenReturn(statisticsList);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/recent")
                .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(7))
                .andExpect(jsonPath("$.data[0].totalCustomers").value(60))
                .andExpect(jsonPath("$.data[6].totalCustomers").value(90));
    }
    
    @Test
    void testGetMonthlyStatisticsSuccess() throws Exception {
        // 准备测试数据
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setId(1L);
        statistics.setRestaurantId(1L);
        statistics.setStatDate(LocalDate.of(year, month, 1));
        statistics.setTotalCustomers(2000);
        statistics.setTotalOrders(1000);
        statistics.setTotalRevenue(java.math.BigDecimal.valueOf(100000.0));
        
        // 模拟服务调用
        when(merchantStatisticsService.getMonthlyStatistics(anyLong(), anyInt(), anyInt())).thenReturn(statistics);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/monthly")
                .param("year", String.valueOf(year))
                .param("month", String.valueOf(month)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").value(2000))
                .andExpect(jsonPath("$.data.totalOrders").value(1000))
                .andExpect(jsonPath("$.data.totalRevenue").value(100000.0));
    }
    
    @Test
    void testGetYearlyStatisticsSuccess() throws Exception {
        // 准备测试数据
        int year = LocalDate.now().getYear();
        
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setId(1L);
        statistics.setRestaurantId(1L);
        statistics.setStatDate(LocalDate.of(year, 1, 1));
        statistics.setTotalCustomers(24000);
        statistics.setTotalOrders(12000);
        statistics.setTotalRevenue(java.math.BigDecimal.valueOf(1200000.0));
        
        // 模拟服务调用
        when(merchantStatisticsService.getYearlyStatistics(anyLong(), anyInt())).thenReturn(statistics);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/yearly")
                .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").value(24000))
                .andExpect(jsonPath("$.data.totalOrders").value(12000))
                .andExpect(jsonPath("$.data.totalRevenue").value(1200000.0));
    }
    
    @Test
    void testGetCurrentMonthStatisticsSuccess() throws Exception {
        // 准备测试数据
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setId(1L);
        statistics.setRestaurantId(1L);
        statistics.setStatDate(LocalDate.of(year, month, 1));
        statistics.setTotalCustomers(1500);
        statistics.setTotalOrders(750);
        
        // 模拟服务调用
        when(merchantStatisticsService.getMonthlyStatistics(anyLong(), anyInt(), anyInt())).thenReturn(statistics);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/current-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").value(1500))
                .andExpect(jsonPath("$.data.totalOrders").value(750));
    }
    
    @Test
    void testCreateOrUpdateStatisticsSuccess() throws Exception {
        // 准备测试数据
        LocalDate date = LocalDate.now();
        MerchantStatistics statistics = new MerchantStatistics();
        statistics.setRestaurantId(1L);
        statistics.setStatDate(date);
        statistics.setTotalCustomers(100);
        statistics.setTotalOrders(50);
        statistics.setTotalRevenue(java.math.BigDecimal.valueOf(5000.0));
        
        MerchantStatistics result = new MerchantStatistics();
        result.setId(1L);
        result.setRestaurantId(1L);
        result.setStatDate(date);
        result.setTotalCustomers(100);
        result.setTotalOrders(50);
        result.setTotalRevenue(java.math.BigDecimal.valueOf(5000.0));
        
        // 模拟服务调用
        when(merchantStatisticsService.createOrUpdateStatistics(any(MerchantStatistics.class))).thenReturn(result);
        
        // 执行测试
        mockMvc.perform(post("/merchant/statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statistics)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.totalCustomers").value(100))
                .andExpect(jsonPath("$.data.totalOrders").value(50));
    }
    
    @Test
    void testDeleteStatisticsSuccess() throws Exception {
        // 准备测试数据
        LocalDate date = LocalDate.now();
        
        // 执行测试
        mockMvc.perform(delete("/merchant/statistics/date")
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetStatisticsOverviewSuccess() throws Exception {
        // 准备测试数据
        LocalDate now = LocalDate.now();
        
        // 今日统计
        MerchantStatistics today = new MerchantStatistics();
        today.setStatDate(now);
        today.setTotalCustomers(100);
        today.setTotalOrders(50);
        
        // 本月统计
        MerchantStatistics currentMonth = new MerchantStatistics();
        currentMonth.setStatDate(LocalDate.of(now.getYear(), now.getMonthValue(), 1));
        currentMonth.setTotalCustomers(2000);
        currentMonth.setTotalOrders(1000);
        
        // 最近7天统计
        List<MerchantStatistics> recent7Days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            MerchantStatistics stats = new MerchantStatistics();
            stats.setStatDate(now.minusDays(6 - i));
            stats.setTotalCustomers(50 + i * 10);
            recent7Days.add(stats);
        }
        
        // 本年统计
        MerchantStatistics currentYear = new MerchantStatistics();
        currentYear.setStatDate(LocalDate.of(now.getYear(), 1, 1));
        currentYear.setTotalCustomers(24000);
        currentYear.setTotalOrders(12000);
        
        // 模拟服务调用
        when(merchantStatisticsService.getStatisticsByDate(anyLong(), any(LocalDate.class))).thenReturn(today);
        when(merchantStatisticsService.getMonthlyStatistics(anyLong(), anyInt(), anyInt())).thenReturn(currentMonth);
        when(merchantStatisticsService.getRecentStatistics(anyLong(), anyInt())).thenReturn(recent7Days);
        when(merchantStatisticsService.getYearlyStatistics(anyLong(), anyInt())).thenReturn(currentYear);
        
        // 执行测试
        mockMvc.perform(get("/merchant/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.today.totalCustomers").value(100))
                .andExpect(jsonPath("$.data.currentMonth.totalCustomers").value(2000))
                .andExpect(jsonPath("$.data.recent7Days.length()").value(7))
                .andExpect(jsonPath("$.data.currentYear.totalCustomers").value(24000));
    }
}

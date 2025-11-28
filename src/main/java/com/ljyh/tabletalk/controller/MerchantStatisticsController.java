package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.MerchantStatistics;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.MerchantStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家统计控制器
 */
@Tag(name = "商家统计", description = "商家端营业数据统计接口")
@RestController
@RequestMapping("/merchant/statistics")
@RequiredArgsConstructor
public class MerchantStatisticsController {
    
    private final MerchantStatisticsService merchantStatisticsService;
    private final MerchantAuthService merchantAuthService;
    
    @Operation(summary = "获取今日统计", description = "获取今日的营业统计数据")
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getTodayStatistics() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        LocalDate today = LocalDate.now();
        
        MerchantStatistics statistics = merchantStatisticsService.getStatisticsByDate(restaurantId, today);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取指定日期统计", description = "获取指定日期的营业统计数据")
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getStatisticsByDate(
            @Parameter(description = "统计日期") @RequestParam LocalDate date) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        MerchantStatistics statistics = merchantStatisticsService.getStatisticsByDate(restaurantId, date);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取日期范围统计", description = "获取指定日期范围内的营业统计数据")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<MerchantStatistics>>> getStatisticsByDateRange(
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MerchantStatistics> statistics = merchantStatisticsService.getStatisticsByDateRange(
            restaurantId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取最近统计", description = "获取最近N天的营业统计数据")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<MerchantStatistics>>> getRecentStatistics(
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") Integer days) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<MerchantStatistics> statistics = merchantStatisticsService.getRecentStatistics(restaurantId, days);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取月度统计", description = "获取指定月份的营业统计数据")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getMonthlyStatistics(
            @Parameter(description = "年份") @RequestParam Integer year,
            @Parameter(description = "月份") @RequestParam Integer month) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        MerchantStatistics statistics = merchantStatisticsService.getMonthlyStatistics(restaurantId, year, month);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取年度统计", description = "获取指定年份的营业统计数据")
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getYearlyStatistics(
            @Parameter(description = "年份") @RequestParam Integer year) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        MerchantStatistics statistics = merchantStatisticsService.getYearlyStatistics(restaurantId, year);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取本月统计", description = "获取当前月份的营业统计数据")
    @GetMapping("/current-month")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getCurrentMonthStatistics() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        LocalDate now = LocalDate.now();
        
        MerchantStatistics statistics = merchantStatisticsService.getMonthlyStatistics(restaurantId, now.getYear(), now.getMonthValue());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "获取本年统计", description = "获取当前年份的营业统计数据")
    @GetMapping("/current-year")
    public ResponseEntity<ApiResponse<MerchantStatistics>> getCurrentYearStatistics() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        LocalDate now = LocalDate.now();
        
        MerchantStatistics statistics = merchantStatisticsService.getYearlyStatistics(restaurantId, now.getYear());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @Operation(summary = "创建或更新统计", description = "创建或更新指定日期的统计数据（管理员功能）")
    @PostMapping
    public ResponseEntity<ApiResponse<MerchantStatistics>> createOrUpdateStatistics(
            @RequestBody MerchantStatistics statistics) {
        
        // 验证餐厅访问权限
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        statistics.setRestaurantId(restaurantId);
        
        MerchantStatistics result = merchantStatisticsService.createOrUpdateStatistics(statistics);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @Operation(summary = "删除统计", description = "删除指定日期的统计数据（管理员功能）")
    @DeleteMapping("/date")
    public ResponseEntity<ApiResponse<Void>> deleteStatistics(
            @Parameter(description = "统计日期") @RequestParam LocalDate date) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        merchantStatisticsService.deleteStatistics(restaurantId, date);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取统计概览", description = "获取营业数据的概览信息")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Object>> getStatisticsOverview() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        LocalDate now = LocalDate.now();
        
        // 获取今日统计
        MerchantStatistics today = merchantStatisticsService.getStatisticsByDate(restaurantId, now);
        
        // 获取本月统计
        MerchantStatistics currentMonth = merchantStatisticsService.getMonthlyStatistics(restaurantId, now.getYear(), now.getMonthValue());
        
        // 获取最近7天统计
        List<MerchantStatistics> recent7Days = merchantStatisticsService.getRecentStatistics(restaurantId, 7);
        
        // 获取本年统计
        MerchantStatistics currentYear = merchantStatisticsService.getYearlyStatistics(restaurantId, now.getYear());
        
        Object overview = java.util.Map.of(
            "today", today,
            "currentMonth", currentMonth,
            "currentYear", currentYear,
            "recent7Days", recent7Days
        );
        
        return ResponseEntity.ok(ApiResponse.success(overview));
    }
}
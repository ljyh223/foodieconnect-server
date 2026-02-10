package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.OverviewStatsResponse;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.StatisticsOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 统计数据控制器（商家端）
 */
@Tag(name = "统计数据管理", description = "商家统计数据相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant/statistics")
public class StatisticsController {

    private final StatisticsOverviewService statisticsOverviewService;
    private final MerchantAuthService merchantAuthService;

    @Operation(summary = "获取统计概览", description = "获取今日总结、月度数据和员工评分数据")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<OverviewStatsResponse>> getOverviewStats(
            @Parameter(description = "指定日期（格式: yyyy-MM-dd），不传则默认为今天")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @Parameter(description = "指定年份，不传则默认为当前年份")
            @RequestParam(required = false) Integer year,

            @Parameter(description = "指定月份，不传则默认为当前月份")
            @RequestParam(required = false) Integer month) {

        // 从认证上下文获取餐厅ID
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();

        OverviewStatsResponse stats = statisticsOverviewService.getOverviewStats(
                restaurantId, date, year, month);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

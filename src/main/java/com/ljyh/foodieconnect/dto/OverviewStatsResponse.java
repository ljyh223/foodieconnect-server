package com.ljyh.foodieconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 统计概览响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewStatsResponse {

    /**
     * 今日总结
     */
    private TodayStats today;

    /**
     * 月度数据
     */
    private MonthlyStats monthly;

    /**
     * 员工评分
     */
    private StaffRatings staffRatings;

    /**
     * 今日统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayStats {
        /**
         * 今日营收
         */
        private BigDecimal revenue;

        /**
         * 菜品平均评分
         */
        private BigDecimal dishAverageRating;

        /**
         * 员工平均评分
         */
        private BigDecimal staffAverageRating;
    }

    /**
     * 月度统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStats {
        /**
         * 年份
         */
        private Integer year;

        /**
         * 月份
         */
        private Integer month;

        /**
         * 每日数据列表
         */
        private List<DailyData> dailyData;
    }

    /**
     * 每日数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyData {
        /**
         * 日期
         */
        private LocalDate date;

        /**
         * 营收
         */
        private BigDecimal revenue;

        /**
         * 订单数
         */
        private Integer orderCount;
    }

    /**
     * 员工评分数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffRatings {
        /**
         * 最高评分员工
         */
        private StaffInfo highest;

        /**
         * 最低评分员工
         */
        private StaffInfo lowest;
    }

    /**
     * 员工信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffInfo {
        /**
         * 员工ID
         */
        private Long staffId;

        /**
         * 姓名
         */
        private String name;

        /**
         * 评分
         */
        private BigDecimal rating;

        /**
         * 头像URL
         */
        private String avatarUrl;
    }
}

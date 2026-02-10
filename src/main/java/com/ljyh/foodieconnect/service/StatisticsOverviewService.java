package com.ljyh.foodieconnect.service;

import com.ljyh.foodieconnect.dto.OverviewStatsResponse;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.entity.Staff;
import com.ljyh.foodieconnect.mapper.DishReviewMapper;
import com.ljyh.foodieconnect.mapper.MenuItemMapper;
import com.ljyh.foodieconnect.mapper.StaffMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计概览服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsOverviewService {

    private final DishReviewMapper dishReviewMapper;
    private final MenuItemMapper menuItemMapper;
    private final StaffMapper staffMapper;
    private final MerchantAuthService merchantAuthService;

    /**
     * 获取统计概览
     *
     * @param restaurantId 餐厅ID
     * @param date 指定日期（可选，默认为今天）
     * @param year 指定年份（可选，默认为当前年份）
     * @param month 指定月份（可选，默认为当前月份）
     * @return 统计概览数据
     */
    public OverviewStatsResponse getOverviewStats(Long restaurantId, LocalDate date, Integer year, Integer month) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限 - 需要管理者权限
        merchantAuthService.validateRole(Merchant.MerchantRole.STAFF);

        // 使用指定日期或默认为今天
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Integer targetYear = year != null ? year : targetDate.getYear();
        Integer targetMonth = month != null ? month : targetDate.getMonthValue();

        log.info("获取统计概览: 餐厅ID={}, 日期={}, 年={}, 月={}", restaurantId, targetDate, targetYear, targetMonth);

        // 1. 获取今日统计数据
        OverviewStatsResponse.TodayStats todayStats = getTodayStats(restaurantId, targetDate);

        // 2. 获取月度统计数据
        OverviewStatsResponse.MonthlyStats monthlyStats = getMonthlyStats(restaurantId, targetYear, targetMonth);

        // 3. 获取员工评分数据
        OverviewStatsResponse.StaffRatings staffRatings = getStaffRatings(restaurantId);

        return OverviewStatsResponse.builder()
                .today(todayStats)
                .monthly(monthlyStats)
                .staffRatings(staffRatings)
                .build();
    }

    /**
     * 获取今日统计数据
     */
    private OverviewStatsResponse.TodayStats getTodayStats(Long restaurantId, LocalDate date) {
        // 1. 获取今日营收（从评价数据统计）
        String dateStr = date.toString();
        DishReviewMapper.DailyRevenueStats revenueStats =
                dishReviewMapper.getRevenueStatsByDate(restaurantId, dateStr);

        BigDecimal revenue = revenueStats != null ? revenueStats.getRevenue() : BigDecimal.ZERO;

        // 2. 获取菜品平均评分（只计算在售菜品）
        Double dishRating = menuItemMapper.calculateAverageRatingByRestaurant(restaurantId);
        BigDecimal dishAverageRating = dishRating != null ? BigDecimal.valueOf(dishRating) : BigDecimal.ZERO;

        // 3. 获取员工平均评分
        Double staffRating = staffMapper.calculateAverageRatingByRestaurant(restaurantId);
        BigDecimal staffAverageRating = staffRating != null ? BigDecimal.valueOf(staffRating) : BigDecimal.ZERO;

        return OverviewStatsResponse.TodayStats.builder()
                .revenue(revenue)
                .dishAverageRating(dishAverageRating)
                .staffAverageRating(staffAverageRating)
                .build();
    }

    /**
     * 获取月度统计数据
     */
    private OverviewStatsResponse.MonthlyStats getMonthlyStats(Long restaurantId, Integer year, Integer month) {
        // 计算该月的起止日期
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 获取该月的每日数据
        List<DishReviewMapper.DailyRevenueStats> dailyRevenueList =
                dishReviewMapper.getDailyRevenueStatsByDateRange(
                        restaurantId,
                        startDate.toString(),
                        endDate.toString()
                );

        // 转换为响应格式
        List<OverviewStatsResponse.DailyData> dailyDataList = new ArrayList<>();
        for (DishReviewMapper.DailyRevenueStats stats : dailyRevenueList) {
            dailyDataList.add(OverviewStatsResponse.DailyData.builder()
                    .date(LocalDate.parse(stats.getDate()))
                    .revenue(stats.getRevenue())
                    .orderCount(stats.getOrderCount())
                    .build());
        }

        return OverviewStatsResponse.MonthlyStats.builder()
                .year(year)
                .month(month)
                .dailyData(dailyDataList)
                .build();
    }

    /**
     * 获取员工评分数据
     */
    private OverviewStatsResponse.StaffRatings getStaffRatings(Long restaurantId) {
        // 获取评分最高的员工
        Staff highestStaff = staffMapper.findHighestRatedStaff(restaurantId);
        OverviewStatsResponse.StaffInfo highest = null;
        if (highestStaff != null && highestStaff.getRating() != null) {
            highest = OverviewStatsResponse.StaffInfo.builder()
                    .staffId(highestStaff.getId())
                    .name(highestStaff.getName())
                    .rating(highestStaff.getRating())
                    .avatarUrl(highestStaff.getAvatarUrl())
                    .build();
        }

        // 获取评分最低的员工
        Staff lowestStaff = staffMapper.findLowestRatedStaff(restaurantId);
        OverviewStatsResponse.StaffInfo lowest = null;
        if (lowestStaff != null && lowestStaff.getRating() != null) {
            lowest = OverviewStatsResponse.StaffInfo.builder()
                    .staffId(lowestStaff.getId())
                    .name(lowestStaff.getName())
                    .rating(lowestStaff.getRating())
                    .avatarUrl(lowestStaff.getAvatarUrl())
                    .build();
        }

        return OverviewStatsResponse.StaffRatings.builder()
                .highest(highest)
                .lowest(lowest)
                .build();
    }
}

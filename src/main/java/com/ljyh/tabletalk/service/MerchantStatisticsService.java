package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.MerchantStatistics;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.MerchantStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家统计服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantStatisticsService extends ServiceImpl<MerchantStatisticsMapper, MerchantStatistics> {
    
    private final MerchantStatisticsMapper merchantStatisticsMapper;
    private final MerchantAuthService merchantAuthService;
    
    /**
     * 获取指定日期范围的统计数据
     */
    public List<MerchantStatistics> getStatisticsByDateRange(Long restaurantId, LocalDate startDate, LocalDate endDate) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return merchantStatisticsMapper.findByRestaurantIdAndDateRange(restaurantId, startDate, endDate);
    }
    
    /**
     * 获取指定日期的统计数据
     */
    public MerchantStatistics getStatisticsByDate(Long restaurantId, LocalDate date) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        MerchantStatistics statistics = merchantStatisticsMapper.findByRestaurantIdAndDate(restaurantId, date);
        if (statistics == null) {
            // 如果没有统计数据，返回默认值
            statistics = new MerchantStatistics();
            statistics.setRestaurantId(restaurantId);
            statistics.setStatDate(date);
            statistics.setTotalOrders(0);
            statistics.setTotalRevenue(java.math.BigDecimal.ZERO);
            statistics.setAverageOrderValue(java.math.BigDecimal.ZERO);
            statistics.setTotalCustomers(0);
            statistics.setNewCustomers(0);
            statistics.setReturningCustomers(0);
            statistics.setAverageRating(java.math.BigDecimal.ZERO);
            statistics.setTotalReviews(0);
        }
        
        return statistics;
    }
    
    /**
     * 获取最近N天的统计数据
     */
    public List<MerchantStatistics> getRecentStatistics(Long restaurantId, Integer limit) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return merchantStatisticsMapper.findRecentByRestaurantId(restaurantId, limit);
    }
    
    /**
     * 获取月度统计数据
     */
    public MerchantStatistics getMonthlyStatistics(Long restaurantId, Integer year, Integer month) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        MerchantStatistics statistics = merchantStatisticsMapper.getMonthlyStatistics(restaurantId, year, month);
        if (statistics == null) {
            // 如果没有统计数据，返回默认值
            statistics = new MerchantStatistics();
            statistics.setRestaurantId(restaurantId);
            statistics.setTotalOrders(0);
            statistics.setTotalRevenue(java.math.BigDecimal.ZERO);
            statistics.setAverageOrderValue(java.math.BigDecimal.ZERO);
            statistics.setTotalCustomers(0);
            statistics.setNewCustomers(0);
            statistics.setReturningCustomers(0);
            statistics.setAverageRating(java.math.BigDecimal.ZERO);
            statistics.setTotalReviews(0);
        }
        
        return statistics;
    }
    
    /**
     * 获取年度统计数据
     */
    public MerchantStatistics getYearlyStatistics(Long restaurantId, Integer year) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        MerchantStatistics statistics = merchantStatisticsMapper.getYearlyStatistics(restaurantId, year);
        if (statistics == null) {
            // 如果没有统计数据，返回默认值
            statistics = new MerchantStatistics();
            statistics.setRestaurantId(restaurantId);
            statistics.setTotalOrders(0);
            statistics.setTotalRevenue(java.math.BigDecimal.ZERO);
            statistics.setAverageOrderValue(java.math.BigDecimal.ZERO);
            statistics.setTotalCustomers(0);
            statistics.setNewCustomers(0);
            statistics.setReturningCustomers(0);
            statistics.setAverageRating(java.math.BigDecimal.ZERO);
            statistics.setTotalReviews(0);
        }
        
        return statistics;
    }
    
    /**
     * 创建或更新统计数据
     */
    @Transactional
    public MerchantStatistics createOrUpdateStatistics(MerchantStatistics statistics) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(statistics.getRestaurantId());
        // 验证角色权限 - 只有系统或管理员可以更新统计数据
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        MerchantStatistics existing = merchantStatisticsMapper.findByRestaurantIdAndDate(
            statistics.getRestaurantId(), statistics.getStatDate());
        
        if (existing != null) {
            // 更新现有统计
            existing.setTotalOrders(statistics.getTotalOrders());
            existing.setTotalRevenue(statistics.getTotalRevenue());
            existing.setAverageOrderValue(statistics.getAverageOrderValue());
            existing.setTotalCustomers(statistics.getTotalCustomers());
            existing.setNewCustomers(statistics.getNewCustomers());
            existing.setReturningCustomers(statistics.getReturningCustomers());
            existing.setAverageRating(statistics.getAverageRating());
            existing.setTotalReviews(statistics.getTotalReviews());
            existing.setPeakHour(statistics.getPeakHour());
            existing.setPeakHourOrders(statistics.getPeakHourOrders());
            
            merchantStatisticsMapper.updateById(existing);
            log.info("更新统计数据成功: 餐厅ID={}, 日期={}", 
                     statistics.getRestaurantId(), statistics.getStatDate());
            
            return existing;
        } else {
            // 创建新统计
            merchantStatisticsMapper.insert(statistics);
            log.info("创建统计数据成功: 餐厅ID={}, 日期={}", 
                     statistics.getRestaurantId(), statistics.getStatDate());
            
            return statistics;
        }
    }
    
    /**
     * 删除统计数据
     */
    @Transactional
    public void deleteStatistics(Long restaurantId, LocalDate date) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限 - 只有管理员可以删除统计数据
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        MerchantStatistics statistics = merchantStatisticsMapper.findByRestaurantIdAndDate(restaurantId, date);
        if (statistics == null) {
            throw new BusinessException("STATISTICS_NOT_FOUND", "统计数据不存在");
        }
        
        merchantStatisticsMapper.deleteById(statistics.getId());
        log.info("删除统计数据成功: 餐厅ID={}, 日期={}", restaurantId, date);
    }
}
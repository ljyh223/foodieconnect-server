package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.entity.StaffReview;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.StaffMapper;
import com.ljyh.tabletalk.mapper.StaffReviewMapper;
import com.ljyh.tabletalk.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店员评价服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffReviewService extends ServiceImpl<StaffReviewMapper, StaffReview> {
    
    private final StaffReviewMapper staffReviewMapper;
    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final UserMapper userMapper;
    
    /**
     * 发表店员评价
     */
    @Transactional
    public StaffReview createStaffReview(Long staffId, Long userId, BigDecimal rating, String content) {
        // 检查用户是否已评价过该店员
        if (staffReviewMapper.existsByStaffIdAndUserId(staffId, userId)) {
            throw new BusinessException("STAFF_REVIEW_EXISTS", "您已对该店员发表过评价");
        }
        
        // 验证评分范围
        if (rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
            throw new BusinessException("INVALID_RATING", "评分必须在1-5之间");
        }
        
        // 创建评价
        StaffReview staffReview = new StaffReview();
        staffReview.setStaffId(staffId);
        staffReview.setUserId(userId);
        staffReview.setRating(rating);
        staffReview.setContent(content);
        
        staffReviewMapper.insert(staffReview);
        log.info("用户 {} 对店员 {} 发表评价，评分: {}", userId, staffId, rating);
        
        // 更新店员评分
        updateStaffRating(staffId);
        
        return staffReview;
    }
    
    /**
     * 获取店员评价列表
     */
    public Page<StaffReview> getStaffReviews(Long staffId, int page, int size) {
        Page<StaffReview> pageParam = new Page<>(page, size);
        return staffReviewMapper.findByStaffId(pageParam, staffId);
    }
    
    /**
     * 获取用户对店员的评价列表
     */
    public List<StaffReview> getUserStaffReviews(Long userId) {
        return staffReviewMapper.findByUserId(userId);
    }
    
    /**
     * 根据ID获取店员评价
     */
    public StaffReview getStaffReviewById(Long id) {
        StaffReview staffReview = staffReviewMapper.selectById(id);
        if (staffReview == null) {
            throw new BusinessException("STAFF_REVIEW_NOT_FOUND", "店员评价不存在");
        }
        return staffReview;
    }
    
    /**
     * 更新店员评分
     */
    @Transactional
    public void updateStaffRating(Long staffId) {
        Double averageRating = staffReviewMapper.calculateAverageRating(staffId);
        if (averageRating != null) {
            Staff staff = staffMapper.selectById(staffId);
            if (staff != null) {
                staff.setRating(BigDecimal.valueOf(averageRating));
                staffMapper.updateById(staff);
                log.info("更新店员 {} 的平均评分: {}", staffId, averageRating);
            }
        }
    }
    
    /**
     * 计算店员平均评分
     */
    public Double calculateAverageRating(Long staffId) {
        return staffReviewMapper.calculateAverageRating(staffId);
    }
    
    /**
     * 统计店员评价数量
     */
    public Integer countReviewsByStaffId(Long staffId) {
        return staffReviewMapper.countByStaffId(staffId);
    }
    
    /**
     * 检查用户是否已评价过店员
     */
    public boolean hasUserReviewedStaff(Long staffId, Long userId) {
        return staffReviewMapper.existsByStaffIdAndUserId(staffId, userId);
    }
}
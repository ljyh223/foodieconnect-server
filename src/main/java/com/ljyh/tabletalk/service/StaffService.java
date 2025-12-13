package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.enums.StaffStatus;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.StaffMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店员服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService extends ServiceImpl<StaffMapper, Staff> {
    
    private final StaffMapper staffMapper;
    
    /**
     * 根据餐厅ID获取店员列表
     */
    public List<Staff> getStaffByRestaurantId(Long restaurantId) {
        return staffMapper.findByRestaurantId(restaurantId);
    }
    
    /**
     * 根据ID获取店员详情
     */
    public Staff getStaffById(Long id) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new BusinessException("STAFF_NOT_FOUND", "店员不存在");
        }
        return staff;
    }
    
    /**
     * 获取在线店员列表
     */
    public List<Staff> getOnlineStaff() {
        return staffMapper.findOnlineStaff();
    }
    
    /**
     * 根据状态获取店员列表
     */
    public List<Staff> getStaffByStatus(StaffStatus status) {
        return staffMapper.findByStatus(status.name());
    }
    
    /**
     * 更新店员状态
     */
    @Transactional
    public void updateStaffStatus(Long staffId, StaffStatus status) {
        Staff staff = getStaffById(staffId);
        staff.setStatus(status);
        staffMapper.updateById(staff);
        log.info("更新店员状态: {} -> {}", staff.getName(), status);
    }
    
    /**
     * 检查店员是否在线
     */
    public boolean isStaffOnline(Long staffId) {
        Staff staff = getStaffById(staffId);
        return staff.getStatus() == StaffStatus.ONLINE;
    }
    
    /**
     * 获取餐厅在线店员
     */
    public List<Staff> getOnlineStaffByRestaurantId(Long restaurantId) {
        return staffMapper.findByRestaurantIdAndStatus(restaurantId, StaffStatus.ONLINE.name());
    }
    
    /**
     * 更新店员评分
     */
    @Transactional
    public void updateStaffRating(Long staffId, Double newRating) {
        Staff staff = getStaffById(staffId);
        
        // 计算新的平均评分（这里简化处理，实际可能需要更复杂的逻辑）
        Double currentRating = staff.getRating() != null ? staff.getRating().doubleValue() : 0.0;
        
        // 简单的平均计算（实际可能需要考虑评分数量）
        Double updatedRating = (currentRating + newRating) / 2;
        staff.setRating(java.math.BigDecimal.valueOf(updatedRating));
        
        staffMapper.updateById(staff);
        log.info("更新店员评分: {} -> {}", staff.getName(), updatedRating);
    }
    
    /**
     * 获取热门店员（按评分排序）
     */
    public List<Staff> getPopularStaff(int limit) {
        List<Staff> allStaff = staffMapper.selectList(null);
        return allStaff.stream()
                .sorted((s1, s2) -> {
                    Double rating1 = s1.getRating() != null ? s1.getRating().doubleValue() : 0.0;
                    Double rating2 = s2.getRating() != null ? s2.getRating().doubleValue() : 0.0;
                    return Double.compare(rating2, rating1);
                })
                .limit(limit)
                .toList();
    }
    
    /**
     * 创建店员
     */
    @Transactional
    public Staff createStaff(Staff staff) {
        staff.setStatus(StaffStatus.ONLINE); // 默认状态为在线
        staffMapper.insert(staff);
        log.info("创建店员成功: {}", staff.getName());
        return staff;
    }
    
    /**
     * 更新店员信息
     */
    @Transactional
    public Staff updateStaff(Long staffId, Staff staff) {
        Staff existingStaff = getStaffById(staffId);
        
        // 更新店员信息
        existingStaff.setName(staff.getName());
        existingStaff.setPosition(staff.getPosition());
        existingStaff.setExperience(staff.getExperience());
        existingStaff.setAvatarUrl(staff.getAvatarUrl());
        
        staffMapper.updateById(existingStaff);
        log.info("更新店员信息成功: {}", existingStaff.getName());
        return existingStaff;
    }
    
    /**
     * 删除店员
     */
    @Transactional
    public void deleteStaff(Long staffId) {
        Staff staff = getStaffById(staffId);
        staffMapper.deleteById(staffId);
        log.info("删除店员成功: {}", staff.getName());
    }
}
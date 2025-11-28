package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.dto.StaffScheduleRequest;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.StaffSchedule;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.StaffScheduleMapper;
import com.ljyh.tabletalk.mapper.StaffMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 店员排班服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffScheduleService extends ServiceImpl<StaffScheduleMapper, StaffSchedule> {
    
    private final StaffScheduleMapper staffScheduleMapper;
    private final StaffMapper staffMapper;
    private final MerchantAuthService merchantAuthService;
    
    /**
     * 获取餐厅的所有排班
     */
    public List<StaffSchedule> getSchedulesByRestaurant(Long restaurantId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return staffScheduleMapper.findByRestaurantId(restaurantId);
    }
    
    /**
     * 获取店员的所有排班
     */
    public List<StaffSchedule> getSchedulesByStaff(Long restaurantId, Long staffId) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.STAFF);
        
        // 验证店员属于该餐厅
        com.ljyh.tabletalk.entity.Staff staff = staffMapper.selectById(staffId);
        if (staff == null || !staff.getRestaurantId().equals(restaurantId)) {
            throw new BusinessException("STAFF_NOT_FOUND", "店员不存在或不属于该餐厅");
        }
        
        return staffScheduleMapper.findByStaffId(staffId);
    }
    
    /**
     * 获取指定日期范围的排班
     */
    public List<StaffSchedule> getSchedulesByDateRange(Long restaurantId, LocalDate startDate, LocalDate endDate) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return staffScheduleMapper.findByRestaurantIdAndDateRange(restaurantId, startDate, endDate);
    }
    
    /**
     * 获取指定日期的排班
     */
    public List<StaffSchedule> getSchedulesByDate(Long restaurantId, LocalDate date) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return staffScheduleMapper.findByRestaurantIdAndDate(restaurantId, date);
    }
    
    /**
     * 创建排班
     */
    @Transactional
    public StaffSchedule createSchedule(Long restaurantId, StaffScheduleRequest request) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 验证店员属于该餐厅
        com.ljyh.tabletalk.entity.Staff staff = staffMapper.selectById(request.getStaffId());
        if (staff == null || !staff.getRestaurantId().equals(restaurantId)) {
            throw new BusinessException("STAFF_NOT_FOUND", "店员不存在或不属于该餐厅");
        }
        
        // 检查时间冲突
        Integer conflictCount = staffScheduleMapper.checkTimeConflict(
            request.getStaffId(), request.getShiftDate(), 
            request.getStartTime(), request.getEndTime(), null);
        if (conflictCount > 0) {
            throw new BusinessException("SCHEDULE_CONFLICT", "排班时间冲突");
        }
        
        StaffSchedule schedule = new StaffSchedule();
        schedule.setStaffId(request.getStaffId());
        schedule.setRestaurantId(restaurantId);
        schedule.setShiftDate(request.getShiftDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setShiftType(request.getShiftType());
        schedule.setStatus(StaffSchedule.ScheduleStatus.SCHEDULED);
        schedule.setNotes(request.getNotes());
        
        staffScheduleMapper.insert(schedule);
        log.info("创建排班成功: 店员ID={}, 日期={}", request.getStaffId(), request.getShiftDate());
        
        return schedule;
    }
    
    /**
     * 更新排班
     */
    @Transactional
    public StaffSchedule updateSchedule(Long scheduleId, StaffScheduleRequest request) {
        StaffSchedule schedule = staffScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("SCHEDULE_NOT_FOUND", "排班不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(schedule.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        // 验证店员属于该餐厅
        com.ljyh.tabletalk.entity.Staff staff = staffMapper.selectById(request.getStaffId());
        if (staff == null || !staff.getRestaurantId().equals(schedule.getRestaurantId())) {
            throw new BusinessException("STAFF_NOT_FOUND", "店员不存在或不属于该餐厅");
        }
        
        // 检查时间冲突（排除当前排班）
        Integer conflictCount = staffScheduleMapper.checkTimeConflict(
            request.getStaffId(), request.getShiftDate(), 
            request.getStartTime(), request.getEndTime(), scheduleId);
        if (conflictCount > 0) {
            throw new BusinessException("SCHEDULE_CONFLICT", "排班时间冲突");
        }
        
        schedule.setStaffId(request.getStaffId());
        schedule.setShiftDate(request.getShiftDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setShiftType(request.getShiftType());
        schedule.setNotes(request.getNotes());
        
        staffScheduleMapper.updateById(schedule);
        log.info("更新排班成功: 排班ID={}, 店员ID={}", scheduleId, request.getStaffId());
        
        return schedule;
    }
    
    /**
     * 删除排班
     */
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        StaffSchedule schedule = staffScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("SCHEDULE_NOT_FOUND", "排班不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(schedule.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.ADMIN);
        
        staffScheduleMapper.deleteById(scheduleId);
        log.info("删除排班成功: 排班ID={}", scheduleId);
    }
    
    /**
     * 更新排班状态
     */
    @Transactional
    public void updateScheduleStatus(Long scheduleId, StaffSchedule.ScheduleStatus status) {
        StaffSchedule schedule = staffScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("SCHEDULE_NOT_FOUND", "排班不存在");
        }
        
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(schedule.getRestaurantId());
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        schedule.setStatus(status);
        staffScheduleMapper.updateById(schedule);
        
        log.info("更新排班状态成功: 排班ID={}, 状态={}", scheduleId, status);
    }
    
    /**
     * 批量创建排班
     */
    @Transactional
    public List<StaffSchedule> batchCreateSchedules(Long restaurantId, List<StaffScheduleRequest> requests) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return requests.stream()
                .map(request -> createSchedule(restaurantId, request))
                .toList();
    }
    
    /**
     * 获取指定日期的排班店员数量
     */
    public Integer getStaffCountByDate(Long restaurantId, LocalDate date) {
        // 验证餐厅访问权限
        merchantAuthService.validateRestaurantAccess(restaurantId);
        // 验证角色权限
        merchantAuthService.validateRole(Merchant.MerchantRole.MANAGER);
        
        return staffScheduleMapper.getStaffCountByDate(restaurantId, date);
    }
}
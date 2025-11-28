package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.StaffScheduleRequest;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.entity.StaffSchedule;
import com.ljyh.tabletalk.enums.StaffStatus;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.StaffScheduleService;
import com.ljyh.tabletalk.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家店员管理控制器
 */
@Tag(name = "商家店员管理", description = "商家端店员和排班管理接口")
@RestController
@RequestMapping("/merchant/staff")
@RequiredArgsConstructor
public class MerchantStaffController {
    
    private final StaffService staffService;
    private final StaffScheduleService staffScheduleService;
    private final MerchantAuthService merchantAuthService;
    
    // ==================== 店员管理 ====================
    
    @Operation(summary = "获取店员列表", description = "获取当前餐厅的所有店员")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Staff>>> getStaff() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<Staff> staffList = staffService.getStaffByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
    
    @Operation(summary = "获取店员详情", description = "获取指定店员的详细信息")
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Staff>> getStaffById(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        Staff staff = staffService.getStaffById(staffId);
        // 验证店员属于当前商家的餐厅
        merchantAuthService.validateRestaurantAccess(staff.getRestaurantId());
        
        return ResponseEntity.ok(ApiResponse.success(staff));
    }
    
    @Operation(summary = "根据状态获取店员", description = "根据状态获取店员列表")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Staff>>> getStaffByStatus(
            @Parameter(description = "店员状态") @PathVariable StaffStatus status) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<Staff> staffList = staffService.getStaffByStatus(status);
        
        // 过滤出属于当前餐厅的店员
        List<Staff> filteredStaff = staffList.stream()
                .filter(staff -> staff.getRestaurantId().equals(restaurantId))
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(filteredStaff));
    }
    
    @Operation(summary = "获取在线店员", description = "获取当前在线的店员列表")
    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<Staff>>> getOnlineStaff() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<Staff> onlineStaff = staffService.getOnlineStaffByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(onlineStaff));
    }
    
    @Operation(summary = "更新店员状态", description = "更新指定店员的状态")
    @PutMapping("/{staffId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStaffStatus(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Parameter(description = "新的状态") @RequestParam StaffStatus status) {
        
        Staff staff = staffService.getStaffById(staffId);
        // 验证店员属于当前商家的餐厅
        merchantAuthService.validateRestaurantAccess(staff.getRestaurantId());
        // 验证权限
        merchantAuthService.validateRole(com.ljyh.tabletalk.entity.Merchant.MerchantRole.MANAGER);
        
        staffService.updateStaffStatus(staffId, status);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "更新店员评分", description = "更新指定店员的评分")
    @PutMapping("/{staffId}/rating")
    public ResponseEntity<ApiResponse<Void>> updateStaffRating(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Parameter(description = "新的评分") @RequestParam Double rating) {
        
        Staff staff = staffService.getStaffById(staffId);
        // 验证店员属于当前商家的餐厅
        merchantAuthService.validateRestaurantAccess(staff.getRestaurantId());
        // 验证权限
        merchantAuthService.validateRole(com.ljyh.tabletalk.entity.Merchant.MerchantRole.MANAGER);
        
        staffService.updateStaffRating(staffId, rating);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    // ==================== 排班管理 ====================
    
    @Operation(summary = "获取排班列表", description = "获取当前餐厅的所有排班")
    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<List<StaffSchedule>>> getSchedules() {
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<StaffSchedule> schedules = staffScheduleService.getSchedulesByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @Operation(summary = "获取店员排班", description = "获取指定店员的所有排班")
    @GetMapping("/{staffId}/schedules")
    public ResponseEntity<ApiResponse<List<StaffSchedule>>> getStaffSchedules(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<StaffSchedule> schedules = staffScheduleService.getSchedulesByStaff(restaurantId, staffId);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @Operation(summary = "获取日期范围排班", description = "获取指定日期范围内的排班")
    @GetMapping("/schedules/range")
    public ResponseEntity<ApiResponse<List<StaffSchedule>>> getSchedulesByDateRange(
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<StaffSchedule> schedules = staffScheduleService.getSchedulesByDateRange(
            restaurantId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @Operation(summary = "获取指定日期排班", description = "获取指定日期的所有排班")
    @GetMapping("/schedules/date")
    public ResponseEntity<ApiResponse<List<StaffSchedule>>> getSchedulesByDate(
            @Parameter(description = "排班日期") @RequestParam LocalDate date) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<StaffSchedule> schedules = staffScheduleService.getSchedulesByDate(restaurantId, date);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @Operation(summary = "创建排班", description = "为店员创建新的排班")
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<StaffSchedule>> createSchedule(
            @Valid @RequestBody StaffScheduleRequest request) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        StaffSchedule schedule = staffScheduleService.createSchedule(restaurantId, request);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }
    
    @Operation(summary = "更新排班", description = "更新指定的排班信息")
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<StaffSchedule>> updateSchedule(
            @Parameter(description = "排班ID") @PathVariable Long scheduleId,
            @Valid @RequestBody StaffScheduleRequest request) {
        
        StaffSchedule schedule = staffScheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }
    
    @Operation(summary = "删除排班", description = "删除指定的排班")
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @Parameter(description = "排班ID") @PathVariable Long scheduleId) {
        
        staffScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "更新排班状态", description = "更新排班的状态（已完成/缺勤）")
    @PutMapping("/schedules/{scheduleId}/status")
    public ResponseEntity<ApiResponse<Void>> updateScheduleStatus(
            @Parameter(description = "排班ID") @PathVariable Long scheduleId,
            @Parameter(description = "排班状态") @RequestParam StaffSchedule.ScheduleStatus status) {
        
        staffScheduleService.updateScheduleStatus(scheduleId, status);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "批量创建排班", description = "批量创建多个排班")
    @PostMapping("/schedules/batch")
    public ResponseEntity<ApiResponse<List<StaffSchedule>>> batchCreateSchedules(
            @RequestBody List<StaffScheduleRequest> requests) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        List<StaffSchedule> schedules = staffScheduleService.batchCreateSchedules(restaurantId, requests);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @Operation(summary = "获取日期排班人数", description = "获取指定日期的排班店员数量")
    @GetMapping("/schedules/count")
    public ResponseEntity<ApiResponse<Integer>> getStaffCountByDate(
            @Parameter(description = "排班日期") @RequestParam LocalDate date) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        Integer count = staffScheduleService.getStaffCountByDate(restaurantId, date);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
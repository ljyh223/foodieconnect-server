package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.enums.StaffStatus;
import com.ljyh.tabletalk.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店员控制器
 */
@Tag(name = "店员管理", description = "店员信息相关接口")
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {
    
    private final StaffService staffService;
    
    @Operation(summary = "获取餐厅店员列表", description = "获取指定餐厅的所有店员列表")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<Staff>>> getStaffByRestaurantId(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {
        
        List<Staff> staffList = staffService.getStaffByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
    
    @Operation(summary = "获取店员详情", description = "根据ID获取店员详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Staff>> getStaffById(
            @Parameter(description = "店员ID") @PathVariable Long id) {
        
        Staff staff = staffService.getStaffById(id);
        return ResponseEntity.ok(ApiResponse.success(staff));
    }
    
    @Operation(summary = "获取在线店员列表", description = "获取所有在线状态的店员")
    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<Staff>>> getOnlineStaff() {
        List<Staff> staffList = staffService.getOnlineStaff();
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
    
    @Operation(summary = "根据状态获取店员列表", description = "根据状态获取店员列表")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Staff>>> getStaffByStatus(
            @Parameter(description = "店员状态") @PathVariable StaffStatus status) {
        
        List<Staff> staffList = staffService.getStaffByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
    
    @Operation(summary = "更新店员状态", description = "更新指定店员的状态")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStaffStatus(
            @Parameter(description = "店员ID") @PathVariable Long id,
            @Parameter(description = "新的状态") @RequestParam StaffStatus status) {
        
        staffService.updateStaffStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "检查店员是否在线", description = "检查指定店员是否在线")
    @GetMapping("/{id}/online")
    public ResponseEntity<ApiResponse<Boolean>> isStaffOnline(
            @Parameter(description = "店员ID") @PathVariable Long id) {
        
        boolean isOnline = staffService.isStaffOnline(id);
        return ResponseEntity.ok(ApiResponse.success(isOnline));
    }
    
    @Operation(summary = "获取餐厅在线店员", description = "获取指定餐厅的在线店员列表")
    @GetMapping("/restaurant/{restaurantId}/online")
    public ResponseEntity<ApiResponse<List<Staff>>> getOnlineStaffByRestaurantId(
            @Parameter(description = "餐厅ID") @PathVariable Long restaurantId) {
        
        List<Staff> staffList = staffService.getOnlineStaffByRestaurantId(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
    
    @Operation(summary = "更新店员评分", description = "更新指定店员的评分")
    @PutMapping("/{id}/rating")
    public ResponseEntity<ApiResponse<Void>> updateStaffRating(
            @Parameter(description = "店员ID") @PathVariable Long id,
            @Parameter(description = "新的评分") @RequestParam Double rating) {
        
        staffService.updateStaffRating(id, rating);
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取热门店员", description = "获取评分最高的热门店员")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Staff>>> getPopularStaff(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        
        List<Staff> staffList = staffService.getPopularStaff(limit);
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }
}
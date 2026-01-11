package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.enums.StaffStatus;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家店员管理控制器
 */
@Tag(name = "商家店员管理", description = "商家端店员管理接口")
@RestController
@RequestMapping("/merchant/staff")
@RequiredArgsConstructor
public class MerchantStaffController {
    
    private final StaffService staffService;
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
    
    @Operation(summary = "创建店员", description = "创建新的店员")
    @PostMapping
    public ResponseEntity<ApiResponse<Staff>> createStaff(
            @Valid @RequestBody Staff staff) {
        
        Long restaurantId = merchantAuthService.getCurrentMerchant().getRestaurantId();
        // 设置餐厅ID
        staff.setRestaurantId(restaurantId);
        
        Staff createdStaff = staffService.createStaff(staff);
        return ResponseEntity.ok(ApiResponse.success(createdStaff));
    }
    
    @Operation(summary = "更新店员信息", description = "更新指定店员的信息")
    @PutMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Staff>> updateStaff(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Valid @RequestBody Staff staff) {
        
        Staff existingStaff = staffService.getStaffById(staffId);
        // 验证店员属于当前商家的餐厅
        merchantAuthService.validateRestaurantAccess(existingStaff.getRestaurantId());
        
        Staff updatedStaff = staffService.updateStaff(staffId, staff);
        return ResponseEntity.ok(ApiResponse.success(updatedStaff));
    }
    
    @Operation(summary = "删除店员", description = "删除指定店员")
    @DeleteMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        Staff staff = staffService.getStaffById(staffId);
        // 验证店员属于当前商家的餐厅
        merchantAuthService.validateRestaurantAccess(staff.getRestaurantId());
        
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
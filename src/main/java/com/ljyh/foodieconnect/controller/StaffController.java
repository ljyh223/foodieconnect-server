package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.CreateStaffReviewRequest;
import com.ljyh.foodieconnect.entity.Staff;
import com.ljyh.foodieconnect.entity.StaffReview;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.mapper.UserMapper;
import com.ljyh.foodieconnect.service.StaffReviewService;
import com.ljyh.foodieconnect.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 店员控制器
 */
@Tag(name = "店员管理", description = "店员信息相关接口")
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {
    
    private final StaffService staffService;
    private final StaffReviewService staffReviewService;
    private final UserMapper userMapper;
    
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
    
    @Operation(summary = "发表店员评价", description = "对指定店员发表评价")
    @PostMapping("/{staffId}/reviews")
    public ResponseEntity<ApiResponse<StaffReview>> createStaffReview(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Valid @RequestBody CreateStaffReviewRequest request) {
        
        // 从SecurityContext中获取当前登录用户的ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录后再发表评价"));
        }
        
        // 从认证信息中获取用户email，然后查询用户ID
        String userEmail = authentication.getName();
        Optional<User> userOptional = userMapper.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
        }
        
        Long userId = userOptional.get().getId();
        
        StaffReview staffReview = staffReviewService.createStaffReview(staffId, userId, request.getRating(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(staffReview));
    }
    
    @Operation(summary = "获取店员评价列表", description = "分页获取指定店员的评价列表")
    @GetMapping("/{staffId}/reviews")
    public ResponseEntity<ApiResponse<Page<StaffReview>>> getStaffReviews(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Page<StaffReview> reviews = staffReviewService.getStaffReviews(staffId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "获取店员评价详情", description = "根据ID获取店员评价详情")
    @GetMapping("/{staffId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<StaffReview>> getStaffReviewById(
            @Parameter(description = "店员ID") @PathVariable Long staffId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        
        StaffReview review = staffReviewService.getStaffReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
    
    @Operation(summary = "检查用户是否已评价店员", description = "检查用户是否已评价过指定店员")
    @GetMapping("/{staffId}/reviews/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUserReviewedStaff(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        // 从SecurityContext中获取当前登录用户的ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("UNAUTHORIZED", "请先登录"));
        }
        
        // 从认证信息中获取用户email，然后查询用户ID
        String userEmail = authentication.getName();
        Optional<User> userOptional = userMapper.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("USER_NOT_FOUND", "用户不存在"));
        }
        
        Long userId = userOptional.get().getId();
        boolean hasReviewed = staffReviewService.hasUserReviewedStaff(staffId, userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("hasReviewed", hasReviewed)));
    }
    
    @Operation(summary = "获取店员评分统计", description = "获取店员的平均评分和评价数量")
    @GetMapping("/{staffId}/reviews/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStaffReviewStats(
            @Parameter(description = "店员ID") @PathVariable Long staffId) {
        
        Double averageRating = staffReviewService.calculateAverageRating(staffId);
        Integer reviewCount = staffReviewService.countReviewsByStaffId(staffId);
        
        Map<String, Object> stats = Map.of(
            "averageRating", averageRating != null ? averageRating : 0.0,
            "reviewCount", reviewCount != null ? reviewCount : 0
        );
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.UserRecommendationScore;
import com.ljyh.foodieconnect.mapper.UserRecommendationMapper.UserRecommendationWithUserInfo;
import com.ljyh.foodieconnect.mapper.UserRecommendationMapper.AlgorithmStats;
import com.ljyh.foodieconnect.service.UserRecommendationService;
import com.ljyh.foodieconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * 用户推荐控制器
 * 处理基于算法的用户推荐功能
 */
@Slf4j
@Tag(name = "用户推荐管理", description = "基于算法的用户推荐相关接口")
@RestController
@RequestMapping("/api/user-recommendations")
@RequiredArgsConstructor
public class UserRecommendationController {
    
    private final UserRecommendationService userRecommendationService;
    private final UserService userService;
    
    @Operation(summary = "获取用户推荐列表", description = "根据指定算法获取用户推荐列表")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRecommendationScore>>> getUserRecommendations(
            @Parameter(description = "推荐算法类型：WEIGHTED, SWITCHING, CASCADE") 
            @RequestParam(defaultValue = "WEIGHTED") String algorithm,
            @Parameter(description = "推荐数量限制") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        List<UserRecommendationScore> recommendations = 
            userRecommendationService.getUserRecommendations(userId, limit, algorithm);
        
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取用户推荐列表（分页）", description = "获取用户推荐列表，支持分页")
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<List<UserRecommendationWithUserInfo>>> getUserRecommendationsPaginated(
            @Parameter(description = "页码，从0开始") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        List<UserRecommendationWithUserInfo> recommendations = 
            userRecommendationService.getUserRecommendationsWithPagination(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取未查看的推荐", description = "获取用户未查看的推荐列表")
    @GetMapping("/unviewed")
    public ResponseEntity<ApiResponse<List<UserRecommendationWithUserInfo>>> getUnviewedRecommendations(
            @Parameter(description = "推荐数量限制") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        List<UserRecommendationWithUserInfo> recommendations = 
            userRecommendationService.getUnviewedRecommendations(userId, limit);
        
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }
    
    @Operation(summary = "获取推荐详情", description = "获取指定推荐记录的详细信息")
    @GetMapping("/{recommendationId}")
    public ResponseEntity<ApiResponse<UserRecommendationWithUserInfo>> getRecommendationDetail(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        UserRecommendationWithUserInfo recommendation = 
            userRecommendationService.getRecommendationDetail(userId, recommendationId);
        
        return ResponseEntity.ok(ApiResponse.success(recommendation));
    }
    
    @Operation(summary = "标记推荐状态", description = "标记推荐记录的查看状态和兴趣状态")
    @PutMapping("/{recommendationId}/status")
    public ResponseEntity<ApiResponse<Void>> markRecommendationStatus(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId,
            @Valid @RequestBody RecommendationStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        userRecommendationService.markRecommendationStatus(
            userId, recommendationId, request.getIsInterested(), request.getFeedback());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "批量标记为已查看", description = "批量将推荐记录标记为已查看状态")
    @PutMapping("/batch-viewed")
    public ResponseEntity<ApiResponse<Void>> batchMarkAsViewed(
            @Valid @RequestBody BatchViewedRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        userRecommendationService.batchMarkAsViewed(userId, request.getRecommendationIds());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "删除推荐记录", description = "删除指定的推荐记录")
    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<ApiResponse<Void>> deleteRecommendation(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        userRecommendationService.deleteRecommendation(userId, recommendationId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "清除所有推荐", description = "清除用户的所有推荐记录")
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearAllRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        userRecommendationService.clearAllUserRecommendations(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "获取推荐统计信息", description = "获取用户的推荐统计信息")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserRecommendationService.RecommendationStats>> getUserRecommendationStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        UserRecommendationService.RecommendationStats stats = 
            userRecommendationService.getUserRecommendationStats(userId);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @Operation(summary = "获取算法统计信息", description = "获取用户的推荐算法统计信息")
    @GetMapping("/algorithm-stats")
    public ResponseEntity<ApiResponse<List<AlgorithmStats>>> getUserAlgorithmStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        List<AlgorithmStats> stats = userRecommendationService.getUserAlgorithmStats(userId);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @Operation(summary = "获取全局算法统计", description = "获取全局推荐算法统计信息")
    @GetMapping("/global-algorithm-stats")
    public ResponseEntity<ApiResponse<List<AlgorithmStats>>> getGlobalAlgorithmStats(
            @Parameter(description = "统计天数") 
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days) {
        
        List<AlgorithmStats> stats = userRecommendationService.getGlobalAlgorithmStats(days);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @Operation(summary = "预热推荐缓存", description = "为用户预热推荐缓存")
    @PostMapping("/warmup-cache")
    public ResponseEntity<ApiResponse<Void>> warmupRecommendationCache(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        
        userRecommendationService.warmupRecommendationCache(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "清除过期推荐", description = "清除过期的推荐记录（管理员功能）")
    @DeleteMapping("/cleanup-expired")
    public ResponseEntity<ApiResponse<Void>> cleanupExpiredRecommendations() {
        userRecommendationService.cleanExpiredRecommendations();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 推荐状态请求DTO
     */
    public static class RecommendationStatusRequest {
        private Boolean isInterested;
        private String feedback;
        
        public Boolean getIsInterested() { return isInterested; }
        public void setIsInterested(Boolean isInterested) { this.isInterested = isInterested; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }
    
    /**
     * 批量查看请求DTO
     */
    public static class BatchViewedRequest {
        private List<Long> recommendationIds;
        
        public List<Long> getRecommendationIds() { return recommendationIds; }
        public void setRecommendationIds(List<Long> recommendationIds) { this.recommendationIds = recommendationIds; }
    }
}
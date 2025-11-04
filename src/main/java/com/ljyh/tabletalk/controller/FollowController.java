package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.entity.UserFollow;
import com.ljyh.tabletalk.service.AuthService;
import com.ljyh.tabletalk.service.FollowService;
import com.ljyh.tabletalk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关注控制器
 */
@Tag(name = "关注管理", description = "用户关注相关接口")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;
    private final AuthService authService;
    private final UserService userService;
    
    @Operation(summary = "关注用户", description = "关注指定用户")
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @Parameter(description = "被关注用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long followerId = userService.getUserByEmail(email).getId();
        followService.followUser(followerId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "取消关注", description = "取消关注指定用户")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @Parameter(description = "被关注用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long followerId = userService.getUserByEmail(email).getId();
        followService.unfollowUser(followerId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @Operation(summary = "获取关注列表", description = "获取当前用户的关注列表")
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<Page<UserFollow>>> getFollowingList(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        Page<UserFollow> followingList = followService.getFollowingList(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followingList));
    }
    
    @Operation(summary = "获取粉丝列表", description = "获取当前用户的粉丝列表")
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<Page<UserFollow>>> getFollowersList(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();
        Page<UserFollow> followersList = followService.getFollowersList(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followersList));
    }
    
    @Operation(summary = "获取指定用户的关注列表", description = "获取指定用户的关注列表")
    @GetMapping("/users/{userId}/following")
    public ResponseEntity<ApiResponse<Page<UserFollow>>> getUserFollowingList(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<UserFollow> followingList = followService.getFollowingList(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followingList));
    }
    
    @Operation(summary = "获取指定用户的粉丝列表", description = "获取指定用户的粉丝列表")
    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<UserFollow>>> getUserFollowersList(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Page<UserFollow> followersList = followService.getFollowersList(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followersList));
    }
    
    @Operation(summary = "检查是否已关注", description = "检查当前用户是否已关注指定用户")
    @GetMapping("/check/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowing(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long followerId = userService.getUserByEmail(email).getId();
        boolean isFollowing = followService.isFollowing(followerId, userId);
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }
    
    @Operation(summary = "获取共同关注", description = "获取当前用户与指定用户的共同关注列表")
    @GetMapping("/mutual/{userId}")
    public ResponseEntity<ApiResponse<List<UserFollow>>> getMutualFollowing(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Long currentUserId = userService.getUserByEmail(email).getId();
        List<UserFollow> mutualFollowing = followService.getMutualFollowing(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(mutualFollowing));
    }
}
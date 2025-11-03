package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.dto.UserProfileResponse;
import com.ljyh.tabletalk.service.AuthService;
import com.ljyh.tabletalk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户信息相关接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;
    
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        UserDTO currentUser = authService.getCurrentUser(email);
        UserProfileResponse profile = userService.getUserProfile(currentUser.getId(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @Operation(summary = "获取用户信息", description = "根据ID获取用户详细信息")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long currentUserId = null;
        if (userDetails != null) {
            try {
                UserDTO currentUser = authService.getCurrentUser(userDetails.getUsername());
                currentUserId = currentUser.getId();
            } catch (Exception e) {
                // 如果获取当前用户失败，则设置为null
                currentUserId = null;
            }
        }
        
        UserProfileResponse profile = userService.getUserProfile(userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @Valid @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        UserDTO currentUser = authService.getCurrentUser(email);
        UserDTO updatedUser = userService.updateUser(currentUser.getId(), userDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }
}
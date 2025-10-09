package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.LoginRequest;
import com.ljyh.tabletalk.dto.LoginResponse;
import com.ljyh.tabletalk.dto.RegisterRequest;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "用户注册", description = "新用户注册接口")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestHeader("Authorization") String authorization) {
        String refreshToken = extractToken(authorization);
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = extractToken(authorization);
            UserDTO user = authService.getCurrentUser(token);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("TOKEN_EXPIRED", "令牌已过期，请重新登录"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("INVALID_TOKEN", "无效的令牌"));
        }
    }
    
    /**
     * 从Authorization头中提取令牌
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("无效的Authorization头");
    }
}
package com.ljyh.tabletalk.controller;

import com.ljyh.tabletalk.dto.ApiResponse;
import com.ljyh.tabletalk.dto.MerchantLoginRequest;
import com.ljyh.tabletalk.dto.MerchantLoginResponse;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.service.MerchantAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商家认证控制器
 */
@Tag(name = "商家认证", description = "商家登录、注册相关接口")
@RestController
@RequestMapping("/merchant/auth")
@RequiredArgsConstructor
public class MerchantAuthController {
    
    private final MerchantAuthService merchantAuthService;
    
    @Operation(summary = "商家登录", description = "商家用户登录获取访问令牌")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MerchantLoginResponse>> login(
            @Valid @RequestBody MerchantLoginRequest request) {
        
        MerchantAuthService.MerchantLoginResult loginResult = merchantAuthService.login(request.getUsername(), request.getPassword());
        Merchant merchant = loginResult.getMerchant();
        
        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(loginResult.getToken());
        response.setMerchantId(merchant.getId());
        response.setName(merchant.getName());
        response.setUsername(merchant.getUsername());
        response.setEmail(merchant.getEmail());
        response.setRestaurantId(merchant.getRestaurantId());
        response.setRole(merchant.getRole().name());
        response.setRoleDescription(merchant.getRole().getDescription());
        response.setPhone(merchant.getPhone());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "商家注册", description = "注册新的商家账户")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Merchant>> register(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "邮箱") @RequestParam String email,
            @Parameter(description = "密码") @RequestParam String password,
            @Parameter(description = "姓名") @RequestParam String name,
            @Parameter(description = "电话") @RequestParam(required = false) String phone,
            @Parameter(description = "餐厅ID") @RequestParam Long restaurantId,
            @Parameter(description = "角色") @RequestParam(defaultValue = "STAFF") String role) {
        
        Merchant.MerchantRole merchantRole;
        try {
            merchantRole = Merchant.MerchantRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_ROLE", "无效的角色"));
        }
        
        Merchant merchant = merchantAuthService.register(username, email, password, name, 
                                                phone, restaurantId, merchantRole);
        
        return ResponseEntity.ok(ApiResponse.success(merchant));
    }
    
    @Operation(summary = "修改密码", description = "商家修改登录密码")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        
        Merchant currentMerchant = merchantAuthService.getCurrentMerchant();
        merchantAuthService.changePassword(currentMerchant.getUsername(), oldPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @Operation(summary = "获取当前商家信息", description = "获取当前登录商家的详细信息")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Merchant>> getCurrentMerchant() {
        Merchant merchant = merchantAuthService.getCurrentMerchant();
        return ResponseEntity.ok(ApiResponse.success(merchant));
    }
    
    @Operation(summary = "商家登出", description = "商家退出登录")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT是无状态的，客户端删除token即可
        return ResponseEntity.ok(ApiResponse.success());
    }
}
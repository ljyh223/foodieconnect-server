package com.ljyh.foodieconnect.controller;

import com.ljyh.foodieconnect.dto.ApiResponse;
import com.ljyh.foodieconnect.dto.MerchantLoginRequest;
import com.ljyh.foodieconnect.dto.MerchantLoginResponse;
import com.ljyh.foodieconnect.dto.MerchantRegisterRequest;
import com.ljyh.foodieconnect.dto.MerchantRegisterResponse;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.service.MerchantAuthService;
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
    public ResponseEntity<ApiResponse<MerchantRegisterResponse>> register(
            @Valid @RequestBody MerchantRegisterRequest request) {

        Merchant.MerchantRole merchantRole = convertRole(request.getRole());
        Merchant merchant = merchantAuthService.register(request, merchantRole);
        MerchantRegisterResponse response = buildRegisterResponse(merchant);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 转换角色枚举
     */
    private Merchant.MerchantRole convertRole(MerchantRegisterRequest.MerchantRole requestRole) {
        return Merchant.MerchantRole.valueOf(requestRole.name());
    }

    /**
     * 构建注册响应DTO
     */
    private MerchantRegisterResponse buildRegisterResponse(Merchant merchant) {
        MerchantRegisterResponse response = new MerchantRegisterResponse();
        response.setMerchantId(merchant.getId());
        response.setUsername(merchant.getUsername());
        response.setEmail(merchant.getEmail());
        response.setName(merchant.getName());
        response.setPhone(merchant.getPhone());
        response.setRestaurantId(merchant.getRestaurantId());
        response.setRole(merchant.getRole().name());
        response.setRoleDescription(merchant.getRole().getDescription());
        response.setStatus(merchant.getStatus().name());
        response.setCreatedAt(merchant.getCreatedAt().toString());
        return response;
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
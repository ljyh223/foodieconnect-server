package com.ljyh.tabletalk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商家登录请求DTO
 */
@Data
@Schema(description = "商家登录请求")
public class MerchantLoginRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin_chuanweixuan")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String password;
}
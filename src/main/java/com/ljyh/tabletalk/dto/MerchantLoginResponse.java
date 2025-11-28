package com.ljyh.tabletalk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商家登录响应DTO
 */
@Data
@Schema(description = "商家登录响应")
public class MerchantLoginResponse {
    
    @Schema(description = "JWT令牌")
    private String token;
    
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "商家ID")
    private Long merchantId;
    
    @Schema(description = "商家姓名")
    private String name;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "餐厅ID")
    private Long restaurantId;
    
    @Schema(description = "角色")
    private String role;
    
    @Schema(description = "角色描述")
    private String roleDescription;
    
    @Schema(description = "电话")
    private String phone;
}
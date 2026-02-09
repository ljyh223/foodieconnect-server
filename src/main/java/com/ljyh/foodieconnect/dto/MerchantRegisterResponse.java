package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商家注册响应DTO
 */
@Data
@Schema(description = "商家注册响应")
public class MerchantRegisterResponse {

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "商家姓名")
    private String name;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "餐厅ID")
    private Long restaurantId;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "角色描述")
    private String roleDescription;

    @Schema(description = "账户状态")
    private String status;

    @Schema(description = "创建时间")
    private String createdAt;
}

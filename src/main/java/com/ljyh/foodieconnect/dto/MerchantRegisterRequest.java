package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家注册请求DTO
 */
@Data
@Schema(description = "商家注册请求")
public class MerchantRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "merchant_chuanweixuan", required = true)
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    @Schema(description = "邮箱", example = "merchant@example.com", required = true)
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    @Schema(description = "密码", example = "M123456", required = true)
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 100, message = "姓名长度必须在2-100个字符之间")
    @Schema(description = "商家姓名", example = "张经理", required = true)
    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    // ===== 餐厅信息 =====
    @NotBlank(message = "餐厅名称不能为空")
    @Size(min = 2, max = 100, message = "餐厅名称长度必须在2-100个字符之间")
    @Schema(description = "餐厅名称", example = "川味轩", required = true)
    private String restaurantName;

    @NotBlank(message = "餐厅类型不能为空")
    @Size(max = 50, message = "餐厅类型长度不能超过50个字符")
    @Schema(description = "餐厅类型（如：中餐、西餐、日料等）", example = "中餐", required = true)
    private String restaurantType;

    @NotBlank(message = "餐厅地址不能为空")
    @Size(max = 255, message = "餐厅地址长度不能超过255个字符")
    @Schema(description = "餐厅地址", example = "北京市朝阳区", required = true)
    private String restaurantAddress;

    @Schema(description = "餐厅图片URL")
    private String restaurantImage;
}

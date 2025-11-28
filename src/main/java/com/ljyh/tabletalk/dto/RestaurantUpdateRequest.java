package com.ljyh.tabletalk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 餐厅更新请求DTO
 */
@Data
@Schema(description = "餐厅更新请求")
public class RestaurantUpdateRequest {
    
    @NotBlank(message = "餐厅名称不能为空")
    @Schema(description = "餐厅名称", example = "川味轩")
    private String name;
    
    @NotBlank(message = "餐厅类型不能为空")
    @Schema(description = "餐厅类型", example = "川菜")
    private String type;
    
    @Schema(description = "餐厅描述", example = "正宗川菜，麻辣鲜香，环境优雅")
    private String description;
    
    @NotBlank(message = "地址不能为空")
    @Schema(description = "地址", example = "市中心街道123号")
    private String address;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "(021) 1234-5678")
    private String phone;
    
    @Schema(description = "营业时间", example = "10:00 - 22:00")
    private String hours;
    
    @Schema(description = "餐厅图片URL")
    private String imageUrl;
    
    @Schema(description = "是否营业", example = "true")
    private Boolean isOpen;
}
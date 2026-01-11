package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 喜好食物请求DTO
 */
@Data
@Schema(description = "喜好食物请求")
public class FavoriteFoodRequest {
    
    @NotBlank(message = "食物名称不能为空")
    @Size(max = 100, message = "食物名称长度不能超过100个字符")
    @Schema(description = "食物名称", example = "宫保鸡丁", required = true)
    private String foodName;
    
    @Size(max = 50, message = "食物类型长度不能超过50个字符")
    @Schema(description = "食物类型", example = "川菜")
    private String foodType;
}
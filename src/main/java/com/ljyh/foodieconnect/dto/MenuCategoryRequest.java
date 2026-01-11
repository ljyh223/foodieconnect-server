package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 菜单分类请求DTO
 */
@Data
@Schema(description = "菜单分类请求")
public class MenuCategoryRequest {
    
    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "热菜")
    private String name;
    
    @Schema(description = "分类描述", example = "川菜热菜系列")
    private String description;
    
    @Min(value = 0, message = "排序顺序不能小于0")
    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isActive;
}
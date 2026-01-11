package com.ljyh.foodieconnect.dto;

import com.ljyh.foodieconnect.entity.MenuItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 菜单项请求DTO
 */
@Data
@Schema(description = "菜单项请求")
public class MenuItemRequest {
    
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    
    @NotBlank(message = "菜品名称不能为空")
    @Schema(description = "菜品名称", example = "宫保鸡丁")
    private String name;
    
    @Schema(description = "菜品描述", example = "经典川菜，麻辣鲜香，鸡肉嫩滑，花生酥脆")
    private String description;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "价格", example = "48.00")
    private BigDecimal price;
    
    @Schema(description = "原价", example = "58.00")
    private BigDecimal originalPrice;
    
    @Schema(description = "图片URL")
    private String imageUrl;
    
    @Schema(description = "是否可用", example = "true")
    private Boolean isAvailable;
    
    @Schema(description = "是否推荐", example = "true")
    private Boolean isRecommended;
    
    @Min(value = 0, message = "排序顺序不能小于0")
    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "营养信息(JSON格式)")
    private String nutritionInfo;
    
    @Schema(description = "过敏原信息(JSON格式)")
    private String allergenInfo;
    
    @Schema(description = "辣度等级", allowableValues = {"NONE", "MILD", "MEDIUM", "HOT"})
    private MenuItem.SpiceLevel spiceLevel;
    
    @Min(value = 1, message = "制作时间不能小于1分钟")
    @Schema(description = "制作时间(分钟)", example = "15")
    private Integer preparationTime;
    
    @Min(value = 0, message = "卡路里不能小于0")
    @Schema(description = "卡路里", example = "320")
    private Integer calories;
}
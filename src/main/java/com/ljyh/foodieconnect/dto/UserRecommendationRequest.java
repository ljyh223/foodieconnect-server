package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户推荐餐厅请求DTO
 */
@Data
@Schema(description = "用户推荐餐厅请求")
public class UserRecommendationRequest {
    
    @NotNull(message = "餐厅ID不能为空")
    @Schema(description = "餐厅ID", example = "1", required = true)
    private Long restaurantId;
    
    @Schema(description = "推荐理由", example = "这家餐厅的菜品非常正宗，环境也很好")
    private String reason;
    
    @DecimalMin(value = "1.0", message = "评分不能低于1.0")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    @Schema(description = "用户评分(1-5)", example = "4.5")
    private BigDecimal rating;
}
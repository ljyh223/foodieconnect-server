package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建店员评价请求DTO
 */
@Data
@Schema(description = "创建店员评价请求")
public class CreateStaffReviewRequest {
    
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能低于1.0")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    @Schema(description = "评分", example = "4.5", required = true)
    private BigDecimal rating;
    
    @NotBlank(message = "评价内容不能为空")
    @Schema(description = "评价内容", example = "服务态度很好，推荐菜品很专业", required = true)
    private String content;
}
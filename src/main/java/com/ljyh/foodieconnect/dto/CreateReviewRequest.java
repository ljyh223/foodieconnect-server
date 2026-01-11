package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建评论请求DTO
 */
@Data
@Schema(description = "创建评论请求")
public class CreateReviewRequest {
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能低于1")
    @Max(value = 5, message = "评分不能高于5")
    @Schema(description = "评分", example = "5", required = true)
    private Integer rating;
    
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", example = "这家餐厅很棒！", required = true)
    private String comment;
    
    @Schema(description = "图片URL列表", example = "[\"/uploads/image1.jpg\", \"/uploads/image2.jpg\"]")
    private List<String> imageUrls;
}
package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 菜品评价请求DTO
 */
@Data
@Schema(description = "菜品评价请求")
public class DishReviewRequest {

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    @Schema(description = "评分（1-5）", example = "5", required = true)
    private Integer rating;

    @Size(max = 500, message = "评论内容不能超过500字符")
    @Schema(description = "评论内容", example = "很好吃！")
    private String comment;

    @Schema(description = "图片URL列表", example = "[\"/uploads/image1.jpg\", \"/uploads/image2.jpg\"]")
    private List<String> images;
}

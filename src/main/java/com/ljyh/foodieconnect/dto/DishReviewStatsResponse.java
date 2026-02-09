package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 菜品评分统计响应DTO
 */
@Data
@Schema(description = "菜品评分统计响应")
public class DishReviewStatsResponse {

    @Schema(description = "平均评分")
    private BigDecimal averageRating;

    @Schema(description = "总评价数")
    private Integer totalReviews;

    @Schema(description = "评分分布（1-5星的数量）")
    private Map<Integer, Integer> ratingDistribution;
}

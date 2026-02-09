package com.ljyh.foodieconnect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 菜品评价响应DTO
 */
@Data
@Schema(description = "菜品评价响应")
public class DishReviewResponse {

    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "菜品ID")
    private Long menuItemId;

    @Schema(description = "菜品名称")
    private String itemName;

    @Schema(description = "菜品价格")
    private BigDecimal itemPrice;

    @Schema(description = "菜品图片")
    private String itemImage;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "评分（1-5）")
    private Integer rating;

    @Schema(description = "评论内容")
    private String comment;

    @Schema(description = "图片URL列表")
    private List<String> images;

    @Schema(description = "创建时间")
    private String createdAt;
}

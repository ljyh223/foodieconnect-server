package com.ljyh.tabletalk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 关注请求DTO
 */
@Data
@Schema(description = "关注请求")
public class FollowRequest {
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "被关注用户ID", example = "1", required = true)
    private Long userId;
}
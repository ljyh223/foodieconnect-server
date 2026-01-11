package com.ljyh.foodieconnect.dto;

import com.ljyh.foodieconnect.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户个人信息响应DTO
 */
@Data
@Schema(description = "用户个人信息响应")
public class UserProfileResponse {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @Schema(description = "显示名称", example = "张三")
    private String displayName;
    
    @Schema(description = "头像URL", example = "/uploads/avatar.jpg")
    private String avatarUrl;
    
    @Schema(description = "个人简介", example = "这是我的个人简介")
    private String bio;
    
    @Schema(description = "用户状态", example = "ACTIVE")
    private UserStatus status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "喜好食物列表")
    private List<FavoriteFoodDTO> favoriteFoods;
    
    @Schema(description = "关注数量", example = "10")
    private Long followingCount;
    
    @Schema(description = "粉丝数量", example = "5")
    private Long followersCount;
    
    @Schema(description = "推荐餐厅数量", example = "3")
    private Long recommendationsCount;
    
    @Schema(description = "当前用户是否已关注该用户", example = "false")
    private Boolean isFollowing;
    
    /**
     * 喜好食物DTO
     */
    @Data
    @Schema(description = "喜好食物信息")
    public static class FavoriteFoodDTO {
        
        @Schema(description = "喜好食物ID", example = "1")
        private Long id;
        
        @Schema(description = "食物名称", example = "宫保鸡丁")
        private String foodName;
        
        @Schema(description = "食物类型", example = "川菜")
        private String foodType;
    }
}
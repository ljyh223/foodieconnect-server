package com.ljyh.foodieconnect.dto;

import com.ljyh.foodieconnect.entity.UserFollow;
import lombok.Data;

/**
 * 关注关系与用户信息组合DTO
 */
@Data
public class FollowWithUserDTO {

    /**
     * 关注关系信息
     */
    private UserFollow follow;

    /**
     * 被关注用户的信息
     */
    private UserDTO user;
}

package com.ljyh.tabletalk.enums;

/**
 * 用户状态枚举
 */
public enum UserStatus {
    ACTIVE("活跃"),
    INACTIVE("未激活"),
    BANNED("封禁");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
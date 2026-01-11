package com.ljyh.foodieconnect.enums;

/**
 * 发送者类型枚举
 */
public enum SenderType {
    USER("用户"),
    STAFF("店员");

    private final String description;

    SenderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
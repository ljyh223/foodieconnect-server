package com.ljyh.foodieconnect.enums;

/**
 * 店员状态枚举
 */
public enum StaffStatus {
    ONLINE("在线"),
    OFFLINE("离线"),
    BUSY("忙碌");

    private final String description;

    StaffStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
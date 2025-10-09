package com.ljyh.tabletalk.enums;

/**
 * 聊天会话状态枚举
 */
public enum ChatSessionStatus {
    ACTIVE("活跃"),
    CLOSED("已关闭"),
    EXPIRED("已过期");

    private final String description;

    ChatSessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
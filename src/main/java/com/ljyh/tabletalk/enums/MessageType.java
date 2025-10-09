package com.ljyh.tabletalk.enums;

/**
 * 消息类型枚举
 */
public enum MessageType {
    TEXT("文本"),
    IMAGE("图片"),
    SYSTEM("系统");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
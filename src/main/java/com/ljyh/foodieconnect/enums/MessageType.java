package com.ljyh.foodieconnect.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum MessageType {
    TEXT("文本"),
    IMAGE("图片"),
    SYSTEM("系统");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

}
package com.ljyh.foodieconnect.scheduler;

import com.ljyh.foodieconnect.entity.ChatRoom;
import com.ljyh.foodieconnect.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 聊天室验证码定时刷新任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomVerificationCodeScheduler {

    private final ChatRoomService chatRoomService;

    /**
     * 每隔30分钟刷新所有聊天室的验证码
     * 使用cron表达式：0 0/30 * * * ? 表示每30分钟执行一次
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshAllVerificationCodes() {
        log.info("开始刷新所有聊天室的验证码");
        
        // 获取所有聊天室
        List<ChatRoom> chatRooms = chatRoomService.list();
        
        // 刷新每个聊天室的验证码
        for (ChatRoom chatRoom : chatRooms) {
            chatRoomService.refreshVerificationCode(chatRoom);
        }
        
        log.info("所有聊天室验证码刷新完成，共刷新了 {} 个聊天室", chatRooms.size());
    }
}

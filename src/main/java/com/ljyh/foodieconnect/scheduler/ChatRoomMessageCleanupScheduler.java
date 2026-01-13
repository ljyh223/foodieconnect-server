package com.ljyh.foodieconnect.scheduler;

import com.ljyh.foodieconnect.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 聊天室消息清理定时任务
 * 每天凌晨1点删除超过24小时的聊天记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomMessageCleanupScheduler {

    private final ChatRoomService chatRoomService;

    /**
     * 每天凌晨1点执行一次，删除超过24小时的聊天记录
     * cron表达式：0 0 1 * * ? 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupOldMessages() {
        log.info("开始清理超过24小时的聊天记录");
        
        // 删除超过1天的聊天记录（24小时）
        int deletedCount = chatRoomService.deleteOldMessages(1);
        
        log.info("聊天记录清理完成，共删除了 {} 条超过24小时的聊天记录", deletedCount);
    }
}

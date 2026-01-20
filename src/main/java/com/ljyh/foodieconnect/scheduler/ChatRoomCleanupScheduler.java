package com.ljyh.foodieconnect.scheduler;

import com.ljyh.foodieconnect.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 聊天室清理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCleanupScheduler implements ApplicationRunner {

    private final ChatRoomService chatRoomService;

    /**
     * 应用启动时执行，删除超过24小时的聊天记录
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("应用启动，开始清理过久的聊天记录");
        
        try {
            int deletedCount = chatRoomService.deleteOldMessages(1);
            log.info("启动时清理完成，删除了 {} 条超过 1 天的聊天记录", deletedCount);
        } catch (Exception e) {
            log.error("启动时清理聊天记录失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每天凌晨2点执行，删除超过24小时的聊天记录
     * 使用cron表达式：0 0 2 * * ? 表示每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledCleanupOldMessages() {
        log.info("定时任务开始清理过久的聊天记录");
        
        try {
            int deletedCount = chatRoomService.deleteOldMessages(1);
            log.info("定时清理完成，删除了 {} 条超过 1 天的聊天记录", deletedCount);
        } catch (Exception e) {
            log.error("定时清理聊天记录失败: {}", e.getMessage(), e);
        }
    }
}
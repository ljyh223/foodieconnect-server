-- 在线用户表迁移脚本
-- 执行时间：2025-11-02

-- 1. 创建在线用户表
CREATE TABLE `online_users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `session_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `connected_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_room_user`(`room_id`, `user_id`) USING BTREE,
  INDEX `idx_last_active_at`(`last_active_at` ASC) USING BTREE,
  CONSTRAINT `online_users_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `online_users_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- 2. 添加唯一约束，确保同一用户在同一聊天室只有一个在线记录
ALTER TABLE `online_users` ADD UNIQUE INDEX `uk_room_user`(`room_id`, `user_id`) USING BTREE;

-- 3. 添加清理过期在线用户的存储过程（可选）
DELIMITER $$
CREATE PROCEDURE `CleanupExpiredOnlineUsers`(IN minutes_old INT)
BEGIN
    DELETE FROM online_users 
    WHERE last_active_at < DATE_SUB(NOW(), INTERVAL minutes_old MINUTE);
    SELECT ROW_COUNT() AS cleaned_count;
END$$
DELIMITER ;
-- 聊天室表迁移脚本
-- 执行时间：2025-10-31

-- 1. 创建聊天室表
CREATE TABLE `chat_rooms`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '餐厅聊天室',
  `verification_code` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ACTIVE','CLOSED','EXPIRED') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'ACTIVE',
  `last_message` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `last_message_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `online_user_count` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_verification_code`(`verification_code` ASC) USING BTREE,
  CONSTRAINT `chat_rooms_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- 2. 创建聊天室成员表
CREATE TABLE `chat_room_members`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_online` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_room_user`(`room_id`, `user_id`) USING BTREE,
  CONSTRAINT `chat_room_members_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_room_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- 3. 创建聊天室消息表
CREATE TABLE `chat_room_messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `message_type` enum('TEXT','IMAGE','SYSTEM') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'TEXT',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `chat_room_messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_room_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- 4. 为现有餐厅添加默认聊天室
INSERT INTO `chat_rooms` (restaurant_id, name, verification_code, status, online_user_count, created_at, updated_at) VALUES
(1, '川味轩聊天室', '888888', 'ACTIVE', 0, NOW(), NOW()),
(2, '粤香楼聊天室', '666666', 'ACTIVE', 0, NOW(), NOW()),
(3, '湘味馆聊天室', '777777', 'ACTIVE', 0, NOW(), NOW());

-- 5. 添加索引优化查询性能
ALTER TABLE `chat_room_members` ADD INDEX `idx_room_user_online`(`room_id`, `user_id`, `is_online`) USING BTREE;
ALTER TABLE `chat_room_messages` ADD INDEX `idx_room_created_at`(`room_id`, `created_at`) USING BTREE;
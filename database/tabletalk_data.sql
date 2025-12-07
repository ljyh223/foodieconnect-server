/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : tabletalk

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 04/12/2025 11:57:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_room_members
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_members`;
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
  INDEX `idx_room_user`(`room_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_room_user_online`(`room_id` ASC, `user_id` ASC, `is_online` ASC) USING BTREE,
  CONSTRAINT `chat_room_members_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_room_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_room_members
-- ----------------------------
INSERT INTO `chat_room_members` VALUES (2, 1, 3, '2025-11-02 19:53:49', 0, '2025-11-02 19:53:49', '2025-11-04 20:32:29');
INSERT INTO `chat_room_members` VALUES (3, 1, 4, '2025-11-02 19:54:42', 0, '2025-11-02 19:54:42', '2025-11-03 09:24:42');
INSERT INTO `chat_room_members` VALUES (4, 2, 3, '2025-11-03 09:35:51', 0, '2025-11-03 09:35:51', '2025-11-03 10:59:27');
INSERT INTO `chat_room_members` VALUES (5, 1, 5, '2025-11-17 21:12:12', 0, '2025-11-17 21:12:12', '2025-11-27 16:32:39');
INSERT INTO `chat_room_members` VALUES (6, 2, 5, '2025-11-27 16:21:43', 0, '2025-11-27 16:21:43', '2025-11-27 16:27:50');

-- ----------------------------
-- Table structure for chat_room_messages
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_messages`;
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
  INDEX `idx_room_created_at`(`room_id` ASC, `created_at` ASC) USING BTREE,
  CONSTRAINT `chat_room_messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_room_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_room_messages
-- ----------------------------
INSERT INTO `chat_room_messages` VALUES (34, 1, 3, '123', 'TEXT', '2025-11-04 14:22:44', '2025-11-04 14:22:44');
INSERT INTO `chat_room_messages` VALUES (35, 1, 3, '123', 'TEXT', '2025-11-04 20:21:30', '2025-11-04 20:21:30');
INSERT INTO `chat_room_messages` VALUES (36, 1, 3, '123', 'TEXT', '2025-11-04 20:21:31', '2025-11-04 20:21:31');
INSERT INTO `chat_room_messages` VALUES (37, 1, 5, '666', 'TEXT', '2025-11-17 21:28:51', '2025-11-17 21:28:51');
INSERT INTO `chat_room_messages` VALUES (38, 1, 5, '大家好，这是纯WS+protobuf测试！', 'TEXT', '2025-11-27 16:15:50', '2025-11-27 16:15:50');
INSERT INTO `chat_room_messages` VALUES (39, 1, 5, 'hello', 'TEXT', '2025-11-27 16:16:25', '2025-11-27 16:16:25');
INSERT INTO `chat_room_messages` VALUES (40, 1, 5, '123', 'TEXT', '2025-11-27 16:21:27', '2025-11-27 16:21:27');
INSERT INTO `chat_room_messages` VALUES (41, 1, 5, '666', 'TEXT', '2025-11-27 16:21:31', '2025-11-27 16:21:31');

-- ----------------------------
-- Table structure for chat_rooms
-- ----------------------------
DROP TABLE IF EXISTS `chat_rooms`;
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_rooms
-- ----------------------------
INSERT INTO `chat_rooms` VALUES (1, 1, '川味轩聊天室', '888888', 'ACTIVE', '666', '2025-11-27 16:21:31', 0, '2025-11-01 11:01:55', '2025-11-27 16:21:30');
INSERT INTO `chat_rooms` VALUES (2, 2, '粤香楼聊天室', '666666', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2025-11-01 11:01:55', '2025-11-01 11:01:55');
INSERT INTO `chat_rooms` VALUES (3, 3, '湘味馆聊天室', '777777', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2025-11-01 11:01:55', '2025-11-01 11:01:55');

-- ----------------------------
-- Table structure for menu_categories
-- ----------------------------
DROP TABLE IF EXISTS `menu_categories`;
CREATE TABLE `menu_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_menu_categories_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu_categories
-- ----------------------------
INSERT INTO `menu_categories` VALUES (1, 1, '热菜', '川菜热菜系列', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (2, 1, '凉菜', '川菜凉菜系列', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (3, 1, '汤品', '川菜汤品系列', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (4, 2, '粤菜', '粤菜系列', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (5, 2, '点心', '粤式点心', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (6, 2, '汤品', '粤式汤品', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (7, 3, '湘菜', '湘菜系列', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (8, 3, '凉菜', '湘菜凉菜系列', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');

-- ----------------------------
-- Table structure for menu_items
-- ----------------------------
DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE `menu_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '菜品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL',
  `is_available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可用',
  `is_recommended` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否推荐',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `nutrition_info` json NULL COMMENT '营养信息(JSON格式)',
  `allergen_info` json NULL COMMENT '过敏原信息(JSON格式)',
  `spice_level` enum('NONE','MILD','MEDIUM','HOT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NONE' COMMENT '辣度等级',
  `preparation_time` int NULL DEFAULT NULL COMMENT '制作时间(分钟)',
  `calories` int NULL DEFAULT NULL COMMENT '卡路里',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_available`(`is_available` ASC) USING BTREE,
  INDEX `idx_is_recommended`(`is_recommended` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_menu_items_category` FOREIGN KEY (`category_id`) REFERENCES `menu_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_menu_items_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单菜品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu_items
-- ----------------------------
INSERT INTO `menu_items` VALUES (2, 1, 1, '麻婆豆腐', '麻辣鲜香，豆腐嫩滑，肉末鲜美', 32.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'HOT', 10, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (3, 1, 1, '水煮鱼', '鲜嫩鱼片，麻辣鲜香，蔬菜丰富', 68.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'HOT', 20, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (4, 1, 2, '夫妻肺片', '麻辣爽口，牛肉片嫩滑', 42.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'MEDIUM', 8, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (5, 1, 3, '酸辣汤', '酸辣开胃，营养丰富', 28.00, NULL, '/uploads/cfa73ef5-829e-41e5-8de6-c644e13e3fe0.jpg', 1, 0, 0, NULL, NULL, 'MILD', 12, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (6, 2, 1, '白切鸡', '鲜嫩多汁，原汁原味，配姜葱蓉', 68.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 15, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (7, 2, 1, '烧鹅', '皮脆肉嫩，汁多味美', 88.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 25, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (8, 2, 2, '叉烧包', '香甜软糯，馅料丰富', 28.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 10, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (9, 2, 2, '虾饺', '皮薄馅大，鲜美可口', 32.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 12, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (10, 2, 3, '老火靓汤', '慢火熬制，营养丰富', 48.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 30, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (11, 3, 1, '剁椒鱼头', '香辣鲜美，鱼肉嫩滑', 78.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'HOT', 25, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (12, 3, 1, '农家小炒肉', '香辣下饭，肉质鲜美', 52.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'MEDIUM', 15, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (13, 3, 2, '凉拌黄瓜', '清爽开胃，简单美味', 18.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 5, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (14, 1, 1, '牛肉面', '666', 40.00, 45.00, '/uploads/fe2706e9-ad0b-4c35-b51c-2a3e3664a9fa.jpg', 0, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-11-28 17:15:54', '2025-11-28 17:15:54');
INSERT INTO `menu_items` VALUES (15, 1, 1, '面', '天天', 45.00, 55.00, '/uploads/fa6f93f0-d6ca-47d0-838d-b96426fe05da.jpg', 1, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-11-28 17:23:01', '2025-11-28 17:23:01');
INSERT INTO `menu_items` VALUES (16, 1, 1, '666', '777', 40.00, 55.00, '/uploads/119a0ffc-ea3d-4f6e-9749-fd658fc0b29d.jpg', 1, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-12-02 15:31:33', '2025-12-02 15:31:33');

-- ----------------------------
-- Table structure for merchant_statistics
-- ----------------------------
DROP TABLE IF EXISTS `merchant_statistics`;
CREATE TABLE `merchant_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_orders` int NULL DEFAULT 0 COMMENT '总订单数',
  `total_revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '总营业额',
  `average_order_value` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平均订单价值',
  `total_customers` int NULL DEFAULT 0 COMMENT '总顾客数',
  `new_customers` int NULL DEFAULT 0 COMMENT '新顾客数',
  `returning_customers` int NULL DEFAULT 0 COMMENT '回头客数',
  `average_rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '平均评分',
  `total_reviews` int NULL DEFAULT 0 COMMENT '总评价数',
  `peak_hour` int NULL DEFAULT NULL COMMENT '高峰时段(小时)',
  `peak_hour_orders` int NULL DEFAULT 0 COMMENT '高峰时段订单数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_restaurant_date`(`restaurant_id` ASC, `stat_date` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
  CONSTRAINT `fk_merchant_statistics_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchant_statistics
-- ----------------------------

-- ----------------------------
-- Table structure for merchants
-- ----------------------------
DROP TABLE IF EXISTS `merchants`;
CREATE TABLE `merchants`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `restaurant_id` bigint NOT NULL COMMENT '关联的餐厅ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家用户名',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `role` enum('ADMIN','MANAGER','STAFF') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STAFF' COMMENT '商家角色',
  `status` enum('ACTIVE','INACTIVE','BANNED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_merchants_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchants
-- ----------------------------
INSERT INTO `merchants` VALUES (1, 1, 'admin_chuanweixuan', 'admin@chuanweixuan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', '川味轩管理员', '13800138001', 'ADMIN', 'ACTIVE', '2025-12-02 15:06:01', '2025-11-21 19:05:33', '2025-11-27 11:29:42');
INSERT INTO `merchants` VALUES (2, 2, 'admin_yuexianglou', 'admin@yuexianglou.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', '粤香楼管理员', '13800138002', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:43');
INSERT INTO `merchants` VALUES (3, 3, 'admin_xiangweiguan', 'admin@xiangweiguan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', '湘味馆管理员', '13800138003', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:45');

-- ----------------------------
-- Table structure for online_users
-- ----------------------------
DROP TABLE IF EXISTS `online_users`;
CREATE TABLE `online_users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `session_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `connected_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_user`(`room_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_room_user`(`room_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_last_active_at`(`last_active_at` ASC) USING BTREE,
  CONSTRAINT `online_users_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `online_users_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of online_users
-- ----------------------------

-- ----------------------------
-- Table structure for recommended_dishes
-- ----------------------------
DROP TABLE IF EXISTS `recommended_dishes`;
CREATE TABLE `recommended_dishes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `dish_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  CONSTRAINT `recommended_dishes_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recommended_dishes
-- ----------------------------
INSERT INTO `recommended_dishes` VALUES (1, 1, '宫保鸡丁', '经典川菜，麻辣鲜香', 48.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (2, 1, '麻婆豆腐', '麻辣鲜香，豆腐嫩滑', 32.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (3, 2, '白切鸡', '鲜嫩多汁，原汁原味', 68.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (4, 2, '叉烧包', '香甜软糯，馅料丰富', 28.00, NULL, '2025-10-05 14:54:32');

-- ----------------------------
-- Table structure for restaurants
-- ----------------------------
DROP TABLE IF EXISTS `restaurants`;
CREATE TABLE `restaurants`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `distance` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `address` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `hours` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rating` decimal(3, 2) NULL DEFAULT 0.00,
  `review_count` int NULL DEFAULT 0,
  `is_open` tinyint(1) NULL DEFAULT 1,
  `avatar` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_is_open`(`is_open` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of restaurants
-- ----------------------------
INSERT INTO `restaurants` VALUES (1, '川味轩', '川菜', '500m', '正宗川菜，麻辣鲜香，环境优雅', '市中心街道123号', '17340307464', '10:00 - 22:00', 4.50, 10, 1, '', '/uploads/bd0209d2-5ef8-4fa5-838c-767017f7657b.jpg', '2025-10-05 14:54:32', '2025-12-02 15:06:26');
INSERT INTO `restaurants` VALUES (2, '粤香楼', '粤菜', '800m', '精致粤菜，清淡鲜美', '商业区456号', '(021) 2345-6789', '11:00 - 21:30', 4.50, 10, 1, '', NULL, '2025-10-05 14:54:32', '2025-11-02 21:32:37');
INSERT INTO `restaurants` VALUES (3, '湘味馆', '湘菜', '1.2km', '地道湘菜，香辣可口', '美食街789号', '(021) 3456-7890', '10:30 - 22:30', 4.50, 10, 1, '', NULL, '2025-10-05 14:54:32', '2025-11-02 21:32:37');

-- ----------------------------
-- Table structure for review_images
-- ----------------------------
DROP TABLE IF EXISTS `review_images`;
CREATE TABLE `review_images`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `review_id` bigint NOT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `sort_order` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_id`(`review_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `review_images_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of review_images
-- ----------------------------
INSERT INTO `review_images` VALUES (1, 1, '/uploads/2b4706b9-14bf-4849-a851-6c2f3cffd75c.jpg', 1, '2025-10-05 14:54:32', '2025-10-31 21:31:55');
INSERT INTO `review_images` VALUES (2, 1, '/uploads/2b4706b9-14bf-4849-a851-6c2f3cffd75c.jpg', 2, '2025-10-05 14:54:32', '2025-10-31 21:31:53');
INSERT INTO `review_images` VALUES (3, 2, '/uploads/2b4706b9-14bf-4849-a851-6c2f3cffd75c.jpg', 1, '2025-10-05 14:54:32', '2025-10-31 21:31:52');
INSERT INTO `review_images` VALUES (4, 4, '/uploads/35b9f019-58a8-4bab-812b-45177b1bf0b1.jpg', 1, '2025-10-31 21:42:16', '2025-10-31 21:42:16');
INSERT INTO `review_images` VALUES (5, 4, '/uploads/3bca51fb-f40b-4045-831d-5ba7e7a00eb4.jpg', 2, '2025-10-31 21:42:16', '2025-10-31 21:42:16');
INSERT INTO `review_images` VALUES (12, 7, '/uploads/35b9f019-58a8-4bab-812b-45177b1bf0b1.jpg', 1, '2025-10-31 21:46:17', '2025-10-31 21:46:17');
INSERT INTO `review_images` VALUES (13, 7, '/uploads/3bca51fb-f40b-4045-831d-5ba7e7a00eb4.jpg', 2, '2025-10-31 21:46:17', '2025-10-31 21:46:17');
INSERT INTO `review_images` VALUES (14, 7, '/uploads/35b9f019-58a8-4bab-812b-45177b1bf0b1.jpg', 3, '2025-10-31 21:46:17', '2025-10-31 21:46:17');
INSERT INTO `review_images` VALUES (15, 7, '/uploads/3bca51fb-f40b-4045-831d-5ba7e7a00eb4.jpg', 4, '2025-10-31 21:46:17', '2025-10-31 21:46:17');
INSERT INTO `review_images` VALUES (16, 12, '/uploads/8851ec4d-e844-45f0-b4a5-03f865c30b8a.jpg', 1, '2025-11-01 09:55:26', '2025-11-01 09:55:26');
INSERT INTO `review_images` VALUES (17, 12, '/uploads/ca09d87f-dd47-4fe1-bf2c-57a8199b795f.jpg', 2, '2025-11-01 09:55:26', '2025-11-01 09:55:26');

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` int NULL DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_chk_1` CHECK ((`rating` >= 1) and (`rating` <= 5))
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reviews
-- ----------------------------
INSERT INTO `reviews` VALUES (1, 1, 1, 5, '菜品非常好吃，服务也很周到，强烈推荐！', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` VALUES (2, 1, 2, 4, '环境不错，菜品口味正宗，就是有点辣', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` VALUES (3, 2, 1, 5, '粤菜很正宗，服务态度很好', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` VALUES (4, 1, 3, 4, 'hello flutter', '2025-10-31 21:42:16', '2025-10-31 21:42:16');
INSERT INTO `reviews` VALUES (7, 2, 3, 4, 'hello flutter', '2025-10-31 21:46:17', '2025-10-31 21:46:17');
INSERT INTO `reviews` VALUES (12, 3, 3, 5, '123', '2025-11-01 09:55:26', '2025-11-01 09:55:26');

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `position` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ONLINE','OFFLINE','BUSY') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'OFFLINE',
  `experience` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rating` decimal(3, 2) NULL DEFAULT 0.00,
  `avatar_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES (1, 1, '小张', '服务员', 'ONLINE', '3年', 4.95, '/uploads/90308afc-b21d-497b-9bdd-d6ef502a13b8.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:20');
INSERT INTO `staff` VALUES (2, 1, '小李', '领班', 'ONLINE', '5年', 4.80, '/uploads/d3638019-194e-445b-afab-4fe96908a022.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:41');
INSERT INTO `staff` VALUES (3, 2, '小王', '服务员', 'ONLINE', '2年', 4.70, 'uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:48');

-- ----------------------------
-- Table structure for staff_languages
-- ----------------------------
DROP TABLE IF EXISTS `staff_languages`;
CREATE TABLE `staff_languages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `language` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  CONSTRAINT `staff_languages_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_languages
-- ----------------------------
INSERT INTO `staff_languages` VALUES (1, 1, '中文');
INSERT INTO `staff_languages` VALUES (2, 1, '英文');
INSERT INTO `staff_languages` VALUES (3, 2, '中文');
INSERT INTO `staff_languages` VALUES (4, 3, '中文');
INSERT INTO `staff_languages` VALUES (5, 3, '日语');

-- ----------------------------
-- Table structure for staff_reviews
-- ----------------------------
DROP TABLE IF EXISTS `staff_reviews`;
CREATE TABLE `staff_reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` decimal(3, 2) NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `staff_reviews_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `staff_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_reviews
-- ----------------------------
INSERT INTO `staff_reviews` VALUES (1, 1, 1, 5.00, '服务态度很好，推荐菜品很专业', '2025-10-05 14:54:32');
INSERT INTO `staff_reviews` VALUES (2, 1, 2, 4.50, '服务周到，解答问题很耐心', '2025-10-05 14:54:32');

-- ----------------------------
-- Table structure for staff_schedules
-- ----------------------------
DROP TABLE IF EXISTS `staff_schedules`;
CREATE TABLE `staff_schedules`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `staff_id` bigint NOT NULL COMMENT '店员ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `shift_date` date NOT NULL COMMENT '排班日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `shift_type` enum('MORNING','AFTERNOON','EVENING','FULL_DAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班次类型',
  `status` enum('SCHEDULED','COMPLETED','ABSENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SCHEDULED' COMMENT '状态',
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_shift_date`(`shift_date` ASC) USING BTREE,
  CONSTRAINT `fk_staff_schedules_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_staff_schedules_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '店员排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_schedules
-- ----------------------------

-- ----------------------------
-- Table structure for staff_skills
-- ----------------------------
DROP TABLE IF EXISTS `staff_skills`;
CREATE TABLE `staff_skills`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `skill_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  CONSTRAINT `staff_skills_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_skills
-- ----------------------------
INSERT INTO `staff_skills` VALUES (1, 1, '菜品推荐');
INSERT INTO `staff_skills` VALUES (2, 1, '过敏咨询');
INSERT INTO `staff_skills` VALUES (3, 1, '口味调整');
INSERT INTO `staff_skills` VALUES (4, 2, '订座服务');
INSERT INTO `staff_skills` VALUES (5, 2, '菜品推荐');
INSERT INTO `staff_skills` VALUES (6, 3, '菜品推荐');

-- ----------------------------
-- Table structure for user_favorite_foods
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite_foods`;
CREATE TABLE `user_favorite_foods`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物名称',
  `food_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食物类型(如:川菜,粤菜,日料等)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_food`(`user_id` ASC, `food_name` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_food_type`(`food_type` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_foods_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户喜好食物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite_foods
-- ----------------------------
INSERT INTO `user_favorite_foods` VALUES (1, 1, '宫保鸡丁', '川菜', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (2, 1, '麻婆豆腐', '川菜', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (3, 2, '白切鸡', '粤菜', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (4, 2, '叉烧包', '粤菜', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (5, 3, '剁椒鱼头', '湘菜', '2025-11-03 21:29:50');

-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `following_id` bigint NOT NULL COMMENT '被关注者ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_follower_id`(`follower_id` ASC) USING BTREE,
  INDEX `idx_following_id`(`following_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_follows_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_follows_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户关注关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_follows
-- ----------------------------
INSERT INTO `user_follows` VALUES (1, 1, 2, '2025-11-03 21:29:50');
INSERT INTO `user_follows` VALUES (2, 1, 3, '2025-11-03 21:29:50');
INSERT INTO `user_follows` VALUES (3, 2, 3, '2025-11-03 21:29:50');
INSERT INTO `user_follows` VALUES (4, 3, 1, '2025-11-03 21:29:50');

-- ----------------------------
-- Table structure for user_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `user_recommendations`;
CREATE TABLE `user_recommendations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `recommended_user_id` bigint NOT NULL COMMENT '推荐用户ID',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐算法类型(COLLABORATIVE, SOCIAL, HYBRID)',
  `recommendation_score` decimal(5, 4) NOT NULL COMMENT '推荐分数',
  `recommendation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐理由',
  `is_viewed` tinyint(1) NULL DEFAULT 0 COMMENT '是否已查看',
  `is_interested` tinyint(1) NULL DEFAULT NULL COMMENT '是否感兴趣(1:感兴趣, 0:不感兴趣)',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户反馈',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_recommended_algorithm`(`user_id` ASC, `recommended_user_id` ASC, `algorithm_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_recommended_user_id`(`recommended_user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_is_viewed`(`is_viewed` ASC) USING BTREE,
  INDEX `idx_recommendation_score`(`recommendation_score` ASC) USING BTREE,
  INDEX `idx_algorithm_type`(`algorithm_type` ASC) USING BTREE,
  CONSTRAINT `fk_user_recommendations_recommended_user` FOREIGN KEY (`recommended_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户推荐结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_recommendations
-- ----------------------------
INSERT INTO `user_recommendations` VALUES (1, 1, 4, 'COLLABORATIVE', 0.7500, '您和用户4都喜欢访问川味轩和粤香楼', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (2, 1, 5, 'SOCIAL', 0.8000, '您关注的人也关注了用户5', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (3, 2, 5, 'HYBRID', 0.7200, '基于协同过滤和社交关系综合推荐', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (4, 3, 1, 'COLLABORATIVE', 0.6800, '您和用户1有相似的餐厅访问偏好', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (5, 4, 2, 'SOCIAL', 0.6500, '您的好友关注了用户2', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (6, 3, 2, 'WEIGHTED', 0.5105, '您和李四都喜欢湘菜，如湘味馆，可能有相似的口味偏好', 0, NULL, NULL, '2025-11-19 10:57:54', '2025-11-19 11:09:00');
INSERT INTO `user_recommendations` VALUES (7, 3, 5, 'WEIGHTED', 0.3726, '您和alex6都喜欢粤菜，如粤香楼，可能有相似的口味偏好', 0, NULL, NULL, '2025-11-19 10:57:54', '2025-11-19 11:09:00');
INSERT INTO `user_recommendations` VALUES (8, 5, 1, 'WEIGHTED', 0.4628, '您和张三都喜欢粤菜，如粤香楼，可能有相似的口味偏好', 0, NULL, NULL, '2025-11-27 15:22:40', '2025-12-02 15:08:10');
INSERT INTO `user_recommendations` VALUES (10, 5, 3, 'WEIGHTED', 0.4628, '您和ljyhove都喜欢粤菜，如粤香楼，可能有相似的口味偏好', 0, NULL, NULL, '2025-11-27 15:22:40', '2025-12-02 15:08:10');

-- ----------------------------
-- Table structure for user_restaurant_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_recommendations`;
CREATE TABLE `user_restaurant_recommendations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐理由',
  `rating` decimal(3, 2) NULL DEFAULT NULL COMMENT '用户评分(1-5)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_restaurant`(`user_id` ASC, `restaurant_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  CONSTRAINT `fk_recommendations_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating_range` CHECK ((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0)))
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户推荐餐厅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_restaurant_recommendations
-- ----------------------------
INSERT INTO `user_restaurant_recommendations` VALUES (1, 1, 1, '这家川菜非常正宗，麻辣鲜香，环境也很好', 4.80, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` VALUES (2, 2, 2, '粤菜做得很地道，特别是白切鸡，鲜嫩多汁', 4.60, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` VALUES (3, 3, 3, '湘菜口味很正宗，剁椒鱼头必点', 4.70, '2025-11-03 21:29:50', '2025-11-03 21:29:50');

-- ----------------------------
-- Table structure for user_restaurant_visits
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_visits`;
CREATE TABLE `user_restaurant_visits`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CHECK_IN' COMMENT '访问类型：REVIEW, RECOMMENDATION, FAVORITE, CHECK_IN',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `visit_count` int NULL DEFAULT 1 COMMENT '访问次数',
  `last_visit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
  `rating` decimal(3, 2) NULL DEFAULT NULL COMMENT '用户评分(1-5)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_restaurant_date_type`(`user_id` ASC, `restaurant_id` ASC, `visit_date` ASC, `visit_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_visit_date`(`visit_date` ASC) USING BTREE,
  INDEX `idx_last_visit_time`(`last_visit_time` ASC) USING BTREE,
  INDEX `idx_visit_type`(`visit_type` ASC) USING BTREE,
  CONSTRAINT `fk_user_visits_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_visits_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_visits_rating_range` CHECK ((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0)))
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户餐厅访问历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_restaurant_visits
-- ----------------------------
INSERT INTO `user_restaurant_visits` VALUES (1, 1, 1, 'CHECK_IN', '2025-11-15', 3, '2025-11-19 09:59:11', 4.50, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (2, 1, 2, 'CHECK_IN', '2025-11-10', 1, '2025-11-19 09:59:11', 4.00, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (3, 2, 1, 'CHECK_IN', '2025-11-12', 2, '2025-11-19 09:59:11', 4.20, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (4, 2, 3, 'CHECK_IN', '2025-11-08', 1, '2025-11-19 09:59:11', 3.80, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (5, 3, 2, 'CHECK_IN', '2025-11-14', 2, '2025-11-19 09:59:11', 4.60, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (6, 3, 3, 'CHECK_IN', '2025-11-11', 1, '2025-11-19 09:59:11', 4.10, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (7, 4, 1, 'CHECK_IN', '2025-11-13', 1, '2025-11-19 09:59:11', 3.90, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` VALUES (8, 5, 2, 'CHECK_IN', '2025-11-16', 1, '2025-11-19 09:59:11', 4.30, '2025-11-18 21:23:42', '2025-11-18 21:23:42');

-- ----------------------------
-- Table structure for user_similarity_cache
-- ----------------------------
DROP TABLE IF EXISTS `user_similarity_cache`;
CREATE TABLE `user_similarity_cache`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `similar_user_id` bigint NOT NULL COMMENT '相似用户ID',
  `similarity_score` decimal(5, 4) NOT NULL COMMENT '相似度分数(0-1)',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '算法类型(COSINE, PEARSON, ADJUSTED_COSINE)',
  `calculated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_similar_algorithm`(`user_id` ASC, `similar_user_id` ASC, `algorithm_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_similar_user_id`(`similar_user_id` ASC) USING BTREE,
  INDEX `idx_similarity_score`(`similarity_score` ASC) USING BTREE,
  INDEX `idx_algorithm_type`(`algorithm_type` ASC) USING BTREE,
  INDEX `idx_expires_at`(`expires_at` ASC) USING BTREE,
  CONSTRAINT `fk_user_similarity_similar_user` FOREIGN KEY (`similar_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_similarity_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_similarity_score_range` CHECK ((`similarity_score` >= 0.0) and (`similarity_score` <= 1.0))
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户相似度缓存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_similarity_cache
-- ----------------------------
INSERT INTO `user_similarity_cache` VALUES (1, 1, 2, 0.7500, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` VALUES (2, 1, 3, 0.6200, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` VALUES (3, 2, 3, 0.6800, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` VALUES (4, 1, 2, 0.7200, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` VALUES (5, 1, 3, 0.5900, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` VALUES (6, 2, 3, 0.6500, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `display_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '个人简介',
  `password_hash` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE','BANNED') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'user1@example.com', '张三', '/uploads/90308afc-b21d-497b-9bdd-d6ef502a13b8.jpg', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2025-11-01 10:40:28');
INSERT INTO `users` VALUES (2, 'user2@example.com', '李四', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2025-11-01 10:40:33');
INSERT INTO `users` VALUES (3, '3439426154@qq.com', 'ljyhove', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$tW.lIzX2M7YX5ZmrFIgxDODxbL3j47hebNdwFyTOOYvNsj.en502i', 'ACTIVE', '2025-10-08 18:36:39', '2025-11-01 10:40:36');
INSERT INTO `users` VALUES (4, 'ljyh223@163.com', '邓浩晨', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$.nbrKg00ypVCLT43n6JprePTveGAy8scoSB9R4kJB66OE7IYjXtV2', 'ACTIVE', '2025-11-02 17:50:37', '2025-11-03 08:49:15');
INSERT INTO `users` VALUES (5, 'alex@gmail.com', 'alex6', '/uploads/e29b4472-cc3c-4e6a-842d-df91a7f0ac0c.jpg', 'good everyday', '$2a$10$D7hOAiJnQZ8GUQvWQGyGF.mP8.G4PzFyPIWpG6dYdCB45YhglEDom', 'ACTIVE', '2025-11-17 20:46:32', '2025-11-17 20:46:32');
INSERT INTO `users` VALUES (6, 'ale1x@gmail.com', 'alex', '/uploads/e29b4472-cc3c-4e6a-842d-df91a7f0ac0c.jpg', NULL, '$2a$10$n0TG29xAwAJgKOLYF7lJkuaF6cZ4/54xHf3cVJ8jcNiiMsw1tjem6', 'ACTIVE', '2025-11-17 21:01:48', '2025-11-19 10:33:03');

-- ----------------------------
-- Procedure structure for CleanupExpiredOnlineUsers
-- ----------------------------
DROP PROCEDURE IF EXISTS `CleanupExpiredOnlineUsers`;
delimiter ;;
CREATE PROCEDURE `CleanupExpiredOnlineUsers`(IN minutes_old INT)
BEGIN
    DELETE FROM online_users 
    WHERE last_active_at < DATE_SUB(NOW(), INTERVAL minutes_old MINUTE);
    SELECT ROW_COUNT() AS cleaned_count;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for safe_drop_constraint
-- ----------------------------
DROP PROCEDURE IF EXISTS `safe_drop_constraint`;
delimiter ;;
CREATE PROCEDURE `safe_drop_constraint`(IN table_name VARCHAR(100), IN constraint_name VARCHAR(100))
BEGIN
    DECLARE constraint_count INT;
    
    -- 检查约束是否存在
    SELECT COUNT(*) INTO constraint_count
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = table_name
    AND CONSTRAINT_NAME = constraint_name;
    
    -- 如果约束存在，则删除
    IF constraint_count > 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', table_name, ' DROP ', constraint_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('已删除约束: ', constraint_name) AS message;
    ELSE
        SELECT CONCAT('约束不存在，跳过: ', constraint_name) AS message;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for safe_drop_index
-- ----------------------------
DROP PROCEDURE IF EXISTS `safe_drop_index`;
delimiter ;;
CREATE PROCEDURE `safe_drop_index`(IN table_name VARCHAR(100), IN index_name VARCHAR(100))
BEGIN
    DECLARE index_count INT;
    
    -- 检查索引是否存在
    SELECT COUNT(*) INTO index_count
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = table_name
    AND INDEX_NAME = index_name;
    
    -- 如果索引存在，则删除
    IF index_count > 0 THEN
        SET @sql = CONCAT('DROP INDEX ', index_name, ' ON ', table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('已删除索引: ', index_name) AS message;
    ELSE
        SELECT CONCAT('索引不存在，跳过: ', index_name) AS message;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;

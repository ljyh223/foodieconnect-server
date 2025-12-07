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
INSERT INTO `chat_room_messages` VALUES (38, 1, 5, 'Hello everyone, this is a pure WebSocket + Protobuf test!', 'TEXT', '2025-11-27 16:15:50', '2025-11-27 16:15:50');
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
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'Restaurant Chat Room',
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
INSERT INTO `chat_rooms` VALUES (1, 1, 'Chuanwei Xuan Chat Room', '888888', 'ACTIVE', '666', '2025-11-27 16:21:31', 0, '2025-11-01 11:01:55', '2025-11-27 16:21:30');
INSERT INTO `chat_rooms` VALUES (2, 2, 'Yuexiang Lou Chat Room', '666666', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2025-11-01 11:01:55', '2025-11-01 11:01:55');
INSERT INTO `chat_rooms` VALUES (3, 3, 'Xiangwei Guan Chat Room', '777777', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2025-11-01 11:01:55', '2025-11-01 11:01:55');
-- ----------------------------
-- Table structure for menu_categories
-- ----------------------------
DROP TABLE IF EXISTS `menu_categories`;
CREATE TABLE `menu_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Category Name',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Category Description',
  `sort_order` int NULL DEFAULT 0 COMMENT 'Sort Order',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Is Active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_menu_categories_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Menu Category Table' ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Records of menu_categories
-- ----------------------------
INSERT INTO `menu_categories` VALUES (1, 1, 'Hot Dishes', 'Sichuan hot dishes series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (2, 1, 'Cold Dishes', 'Sichuan cold dishes series', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (3, 1, 'Soups', 'Sichuan soup series', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (4, 2, 'Cantonese Dishes', 'Cantonese cuisine series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (5, 2, 'Dim Sum', 'Cantonese dim sum', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (6, 2, 'Soups', 'Cantonese soups', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (7, 3, 'Hunan Dishes', 'Hunan cuisine series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` VALUES (8, 3, 'Cold Dishes', 'Hunan cold dishes series', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
-- ----------------------------
-- Table structure for menu_items
-- ----------------------------
DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE `menu_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Dish ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `category_id` bigint NOT NULL COMMENT 'Category ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Dish Name',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Dish Description',
  `price` decimal(10, 2) NOT NULL COMMENT 'Price',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT 'Original Price',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Image URL',
  `is_available` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Is Available',
  `is_recommended` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Is Recommended',
  `sort_order` int NULL DEFAULT 0 COMMENT 'Sort Order',
  `nutrition_info` json NULL COMMENT 'Nutrition Info (JSON format)',
  `allergen_info` json NULL COMMENT 'Allergen Info (JSON format)',
  `spice_level` enum('NONE','MILD','MEDIUM','HOT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NONE' COMMENT 'Spice Level',
  `preparation_time` int NULL DEFAULT NULL COMMENT 'Preparation Time (minutes)',
  `calories` int NULL DEFAULT NULL COMMENT 'Calories',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_available`(`is_available` ASC) USING BTREE,
  INDEX `idx_is_recommended`(`is_recommended` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_menu_items_category` FOREIGN KEY (`category_id`) REFERENCES `menu_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_menu_items_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Menu Dish Table' ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Records of menu_items
-- ----------------------------
INSERT INTO `menu_items` VALUES (2, 1, 1, 'Mapo Tofu', 'Spicy and numbing, tender tofu with savory minced meat', 32.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'HOT', 10, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (3, 1, 1, 'Sliced Fish in Chili Oil', 'Tender fish slices with rich vegetables, spicy and aromatic', 68.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'HOT', 20, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (4, 1, 2, 'Fuqi Feipian (Beef Offal)', 'Spicy and refreshing, tender beef slices', 42.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'MEDIUM', 8, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (5, 1, 3, 'Hot & Sour Soup', 'Tangy and spicy, nutritious and appetizing', 28.00, NULL, '/uploads/cfa73ef5-829e-41e5-8de6-c644e13e3fe0.jpg', 1, 0, 0, NULL, NULL, 'MILD', 12, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (6, 2, 1, 'White-Cut Chicken', 'Juicy and tender, served with ginger-scallion sauce', 68.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 15, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (7, 2, 1, 'Roast Goose', 'Crispy skin, tender meat, full of flavor', 88.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 25, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (8, 2, 2, 'BBQ Pork Buns', 'Sweet and soft with rich filling', 28.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'NONE', 10, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (9, 2, 2, 'Shrimp Dumplings', 'Thin skin, generous filling, delicious and fresh', 32.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 12, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (10, 2, 3, 'Slow-Cooked Cantonese Soup', 'Slow-simmered, rich in nutrients', 48.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 30, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (11, 3, 1, 'Steamed Fish Head with Chopped Chili', 'Spicy, aromatic, and tender fish meat', 78.00, NULL, NULL, 1, 1, 0, NULL, NULL, 'HOT', 25, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (12, 3, 1, 'Country-Style Stir-Fried Pork', 'Spicy and savory, great with rice', 52.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'MEDIUM', 15, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (13, 3, 2, 'Cucumber Salad', 'Refreshing and simple', 18.00, NULL, NULL, 1, 0, 0, NULL, NULL, 'NONE', 5, NULL, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_items` VALUES (14, 1, 1, 'Beef Noodles', '666', 40.00, 45.00, '/uploads/fe2706e9-ad0b-4c35-b51c-2a3e3664a9fa.jpg', 0, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-11-28 17:15:54', '2025-11-28 17:15:54');
INSERT INTO `menu_items` VALUES (15, 1, 1, 'Noodles', 'Everyday', 45.00, 55.00, '/uploads/fa6f93f0-d6ca-47d0-838d-b96426fe05da.jpg', 1, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-11-28 17:23:01', '2025-11-28 17:23:01');
INSERT INTO `menu_items` VALUES (16, 1, 1, '666', '777', 40.00, 55.00, '/uploads/119a0ffc-ea3d-4f6e-9749-fd658fc0b29d.jpg', 1, 0, 0, NULL, NULL, 'NONE', 10, 100, '2025-12-02 15:31:33', '2025-12-02 15:31:33');
-- ----------------------------
-- Table structure for merchant_statistics
-- ----------------------------
DROP TABLE IF EXISTS `merchant_statistics`;
CREATE TABLE `merchant_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Stat ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `stat_date` date NOT NULL COMMENT 'Stat Date',
  `total_orders` int NULL DEFAULT 0 COMMENT 'Total Orders',
  `total_revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Total Revenue',
  `average_order_value` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Average Order Value',
  `total_customers` int NULL DEFAULT 0 COMMENT 'Total Customers',
  `new_customers` int NULL DEFAULT 0 COMMENT 'New Customers',
  `returning_customers` int NULL DEFAULT 0 COMMENT 'Returning Customers',
  `average_rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT 'Average Rating',
  `total_reviews` int NULL DEFAULT 0 COMMENT 'Total Reviews',
  `peak_hour` int NULL DEFAULT NULL COMMENT 'Peak Hour (hour)',
  `peak_hour_orders` int NULL DEFAULT 0 COMMENT 'Peak Hour Orders',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_restaurant_date`(`restaurant_id` ASC, `stat_date` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
  CONSTRAINT `fk_merchant_statistics_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Merchant Statistics Table' ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Records of merchant_statistics
-- ----------------------------
-- ----------------------------
-- Table structure for merchants
-- ----------------------------
DROP TABLE IF EXISTS `merchants`;
CREATE TABLE `merchants`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Merchant ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Associated Restaurant ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Merchant Username',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Email',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Password Hash',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Merchant Name',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Contact Phone',
  `role` enum('ADMIN','MANAGER','STAFF') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STAFF' COMMENT 'Merchant Role',
  `status` enum('ACTIVE','INACTIVE','BANNED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'Account Status',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT 'Last Login Time',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_merchants_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Merchants Table' ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Records of merchants
-- ----------------------------
INSERT INTO `merchants` VALUES (1, 1, 'admin_chuanweixuan', 'admin@chuanweixuan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Chuanwei Xuan Admin', '13800138001', 'ADMIN', 'ACTIVE', '2025-12-02 15:06:01', '2025-11-21 19:05:33', '2025-11-27 11:29:42');
INSERT INTO `merchants` VALUES (2, 2, 'admin_yuexianglou', 'admin@yuexianglou.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Yuexiang Lou Admin', '13800138002', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:43');
INSERT INTO `merchants` VALUES (3, 3, 'admin_xiangweiguan', 'admin@xiangweiguan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Xiangwei Guan Admin', '13800138003', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:45');
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
INSERT INTO `recommended_dishes` VALUES (1, 1, 'Kung Pao Chicken', 'Classic Sichuan dish, spicy and aromatic', 48.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (2, 1, 'Mapo Tofu', 'Spicy and numbing, tender tofu', 32.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (3, 2, 'White-Cut Chicken', 'Juicy and tender, original flavor', 68.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` VALUES (4, 2, 'BBQ Pork Buns', 'Sweet and soft with rich filling', 28.00, NULL, '2025-10-05 14:54:32');
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
INSERT INTO `restaurants` VALUES (1, 'Chuanwei Xuan', 'Sichuan Cuisine', '500m', 'Authentic Sichuan cuisine, spicy and aromatic, elegant environment', 'No.123 Downtown Street', '17340307464', '10:00 - 22:00', 4.50, 10, 1, '', '/uploads/bd0209d2-5ef8-4fa5-838c-767017f7657b.jpg', '2025-10-05 14:54:32', '2025-12-02 15:06:26');
INSERT INTO `restaurants` VALUES (2, 'Yuexiang Lou', 'Cantonese Cuisine', '800m', 'Refined Cantonese cuisine, light and fresh', 'No.456 Commercial District', '(021) 2345-6789', '11:00 - 21:30', 4.50, 10, 1, '', NULL, '2025-10-05 14:54:32', '2025-11-02 21:32:37');
INSERT INTO `restaurants` VALUES (3, 'Xiangwei Guan', 'Hunan Cuisine', '1.2km', 'Authentic Hunan cuisine, spicy and flavorful', 'No.789 Food Street', '(021) 3456-7890', '10:30 - 22:30', 4.50, 10, 1, '', NULL, '2025-10-05 14:54:32', '2025-11-02 21:32:37');
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
INSERT INTO `reviews` VALUES (1, 1, 1, 5, 'Food is delicious, service is excellent. Highly recommended!', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` VALUES (2, 1, 2, 4, 'Nice ambiance, authentic flavor, just a bit too spicy', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` VALUES (3, 2, 1, 5, 'Very authentic Cantonese cuisine, and excellent service', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
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
INSERT INTO `staff` VALUES (1, 1, 'Xiao Zhang', 'Waiter', 'ONLINE', '3 years', 4.95, '/uploads/90308afc-b21d-497b-9bdd-d6ef502a13b8.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:20');
INSERT INTO `staff` VALUES (2, 1, 'Xiao Li', 'Team Leader', 'ONLINE', '5 years', 4.80, '/uploads/d3638019-194e-445b-afab-4fe96908a022.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:41');
INSERT INTO `staff` VALUES (3, 2, 'Xiao Wang', 'Waiter', 'ONLINE', '2 years', 4.70, 'uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', '2025-10-05 14:54:32', '2025-11-01 10:01:48');
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
INSERT INTO `staff_languages` VALUES (1, 1, 'Chinese');
INSERT INTO `staff_languages` VALUES (2, 1, 'English');
INSERT INTO `staff_languages` VALUES (3, 2, 'Chinese');
INSERT INTO `staff_languages` VALUES (4, 3, 'Chinese');
INSERT INTO `staff_languages` VALUES (5, 3, 'Japanese');
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
INSERT INTO `staff_reviews` VALUES (1, 1, 1, 5.00, 'Excellent service attitude, very professional dish recommendations', '2025-10-05 14:54:32');
INSERT INTO `staff_reviews` VALUES (2, 1, 2, 4.50, 'Attentive service and patient in answering questions', '2025-10-05 14:54:32');
-- ----------------------------
-- Table structure for staff_schedules
-- ----------------------------
DROP TABLE IF EXISTS `staff_schedules`;
CREATE TABLE `staff_schedules`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Schedule ID',
  `staff_id` bigint NOT NULL COMMENT 'Staff ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `shift_date` date NOT NULL COMMENT 'Shift Date',
  `start_time` time NOT NULL COMMENT 'Start Time',
  `end_time` time NOT NULL COMMENT 'End Time',
  `shift_type` enum('MORNING','AFTERNOON','EVENING','FULL_DAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Shift Type',
  `status` enum('SCHEDULED','COMPLETED','ABSENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SCHEDULED' COMMENT 'Status',
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Notes',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  INDEX `idx_shift_date`(`shift_date` ASC) USING BTREE,
  CONSTRAINT `fk_staff_schedules_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_staff_schedules_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Staff Schedule Table' ROW_FORMAT = DYNAMIC;
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
INSERT INTO `staff_skills` VALUES (1, 1, 'Dish Recommendation');
INSERT INTO `staff_skills` VALUES (2, 1, 'Allergy Consultation');
INSERT INTO `staff_skills` VALUES (3, 1, 'Flavor Adjustment');
INSERT INTO `staff_skills` VALUES (4, 2, 'Reservation Service');
INSERT INTO `staff_skills` VALUES (5, 2, 'Dish Recommendation');
INSERT INTO `staff_skills` VALUES (6, 3, 'Dish Recommendation');
-- ----------------------------
-- Table structure for user_favorite_foods
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite_foods`;
CREATE TABLE `user_favorite_foods`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Food Name',
  `food_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Food Type (e.g., Sichuan, Cantonese, Japanese)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_food`(`user_id` ASC, `food_name` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_food_type`(`food_type` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_foods_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Favorite Foods Table' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of user_favorite_foods
-- ----------------------------
INSERT INTO `user_favorite_foods` VALUES (1, 1, 'Kung Pao Chicken', 'Sichuan', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (2, 1, 'Mapo Tofu', 'Sichuan', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (3, 2, 'White-Cut Chicken', 'Cantonese', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (4, 2, 'BBQ Pork Buns', 'Cantonese', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` VALUES (5, 3, 'Steamed Fish Head with Chopped Chili', 'Hunan', '2025-11-03 21:29:50');
-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT 'Follower ID',
  `following_id` bigint NOT NULL COMMENT 'Following ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_follower_id`(`follower_id` ASC) USING BTREE,
  INDEX `idx_following_id`(`following_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_follows_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_follows_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Follow Relationships Table' ROW_FORMAT = Dynamic;
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
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `recommended_user_id` bigint NOT NULL COMMENT 'Recommended User ID',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Recommendation Algorithm Type (COLLABORATIVE, SOCIAL, HYBRID)',
  `recommendation_score` decimal(5, 4) NOT NULL COMMENT 'Recommendation Score',
  `recommendation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Recommendation Reason',
  `is_viewed` tinyint(1) NULL DEFAULT 0 COMMENT 'Has Been Viewed',
  `is_interested` tinyint(1) NULL DEFAULT NULL COMMENT 'Is Interested (1: Yes, 0: No)',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'User Feedback',
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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Recommendation Results Table' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of user_recommendations
-- ----------------------------
INSERT INTO `user_recommendations` VALUES (1, 1, 4, 'COLLABORATIVE', 0.7500, 'You and user4 both like Chuanwei Xuan and Yuexiang Lou', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (2, 1, 5, 'SOCIAL', 0.8000, 'People you follow also follow user5', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (3, 2, 5, 'HYBRID', 0.7200, 'Recommended based on collaborative filtering and social relationships', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (4, 3, 1, 'COLLABORATIVE', 0.6800, 'You and user1 have similar restaurant preferences', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (5, 4, 2, 'SOCIAL', 0.6500, 'Your friend follows user2', 0, NULL, NULL, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_recommendations` VALUES (6, 3, 2, 'WEIGHTED', 0.5105, 'You and Lisi both enjoy Hunan cuisine such as Xiangwei Guan, indicating similar taste preferences', 0, NULL, NULL, '2025-11-19 10:57:54', '2025-11-19 11:09:00');
INSERT INTO `user_recommendations` VALUES (7, 3, 5, 'WEIGHTED', 0.3726, 'You and alex6 both enjoy Cantonese cuisine such as Yuexiang Lou, indicating similar taste preferences', 0, NULL, NULL, '2025-11-19 10:57:54', '2025-11-19 11:09:00');
INSERT INTO `user_recommendations` VALUES (8, 5, 1, 'WEIGHTED', 0.4628, 'You and Zhangsan both enjoy Cantonese cuisine such as Yuexiang Lou, indicating similar taste preferences', 0, NULL, NULL, '2025-11-27 15:22:40', '2025-12-02 15:08:10');
INSERT INTO `user_recommendations` VALUES (10, 5, 3, 'WEIGHTED', 0.4628, 'You and ljyhove both enjoy Cantonese cuisine such as Yuexiang Lou, indicating similar taste preferences', 0, NULL, NULL, '2025-11-27 15:22:40', '2025-12-02 15:08:10');
-- ----------------------------
-- Table structure for user_restaurant_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_recommendations`;
CREATE TABLE `user_restaurant_recommendations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Recommendation Reason',
  `rating` decimal(3, 2) NULL DEFAULT NULL COMMENT 'User Rating (1-5)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_restaurant`(`user_id` ASC, `restaurant_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_restaurant_id`(`restaurant_id` ASC) USING BTREE,
  CONSTRAINT `fk_recommendations_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating_range` CHECK ((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0)))
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Recommended Restaurants Table' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of user_restaurant_recommendations
-- ----------------------------
INSERT INTO `user_restaurant_recommendations` VALUES (1, 1, 1, 'This Sichuan restaurant is very authentic, spicy and aromatic, with a great environment', 4.80, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` VALUES (2, 2, 2, 'Their Cantonese cuisine is authentic, especially the white-cut chicken—tender and juicy', 4.60, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` VALUES (3, 3, 3, 'Authentic Hunan flavors; the Steamed Fish Head with Chopped Chili is a must-try', 4.70, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
-- ----------------------------
-- Table structure for user_restaurant_visits
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_visits`;
CREATE TABLE `user_restaurant_visits`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CHECK_IN' COMMENT 'Visit Type: REVIEW, RECOMMENDATION, FAVORITE, CHECK_IN',
  `visit_date` date NOT NULL COMMENT 'Visit Date',
  `visit_count` int NULL DEFAULT 1 COMMENT 'Visit Count',
  `last_visit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Visit Time',
  `rating` decimal(3, 2) NULL DEFAULT NULL COMMENT 'User Rating (1-5)',
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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Restaurant Visit History Table' ROW_FORMAT = Dynamic;
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
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `similar_user_id` bigint NOT NULL COMMENT 'Similar User ID',
  `similarity_score` decimal(5, 4) NOT NULL COMMENT 'Similarity Score (0-1)',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Algorithm Type (COSINE, PEARSON, ADJUSTED_COSINE)',
  `calculated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Calculated At',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT 'Expires At',
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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User Similarity Cache Table' ROW_FORMAT = Dynamic;
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
  `bio` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT 'Bio',
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
INSERT INTO `users` VALUES (1, 'user1@example.com', 'Zhang San', '/uploads/90308afc-b21d-497b-9bdd-d6ef502a13b8.jpg', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2025-11-01 10:40:28');
INSERT INTO `users` VALUES (2, 'user2@example.com', 'Li Si', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2025-11-01 10:40:33');
INSERT INTO `users` VALUES (3, '3439426154@qq.com', 'ljyhove', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$tW.lIzX2M7YX5ZmrFIgxDODxbL3j47hebNdwFyTOOYvNsj.en502i', 'ACTIVE', '2025-10-08 18:36:39', '2025-11-01 10:40:36');
INSERT INTO `users` VALUES (4, 'ljyh223@163.com', 'Deng Haochen', '/uploads/4e7a0d20-7328-4121-a057-17f47f71c6b7.jpg', NULL, '$2a$10$.nbrKg00ypVCLT43n6JprePTveGAy8scoSB9R4kJB66OE7IYjXtV2', 'ACTIVE', '2025-11-02 17:50:37', '2025-11-03 08:49:15');
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
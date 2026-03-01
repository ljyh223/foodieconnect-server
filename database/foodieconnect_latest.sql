/*
 Navicat Premium Dump SQL

 Source Server         : me
 Source Server Type    : MySQL
 Source Server Version : 90600 (9.6.0)
 Source Host           : localhost:3306
 Source Schema         : foodieconnect

 Target Server Type    : MySQL
 Target Server Version : 90600 (9.6.0)
 File Encoding         : 65001

 Date: 01/03/2026 15:52:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_room_members
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_members`;
CREATE TABLE `chat_room_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_online` tinyint(1) DEFAULT '1',
  `role` enum('MEMBER','OBSERVER') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'MEMBER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_room_id` (`room_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_room_user` (`room_id`,`user_id`) USING BTREE,
  KEY `idx_room_user_online` (`room_id`,`user_id`,`is_online`) USING BTREE,
  CONSTRAINT `chat_room_members_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of chat_room_members
-- ----------------------------
BEGIN;
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (8, 1, 5, '2025-12-15 22:03:25', 0, 'MEMBER', '2025-12-15 22:03:25', '2025-12-28 20:04:47');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (92, 1, -41549387268000, '2025-12-28 21:53:09', 0, 'OBSERVER', '2025-12-28 21:53:09', '2025-12-28 21:53:08');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (93, 1, -41549708471600, '2025-12-28 21:53:09', 0, 'OBSERVER', '2025-12-28 21:53:09', '2025-12-28 21:53:09');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (94, 1, -41549797070500, '2025-12-28 21:53:09', 0, 'OBSERVER', '2025-12-28 21:53:09', '2025-12-28 21:53:09');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (95, 1, -41550323793100, '2025-12-28 21:53:10', 0, 'OBSERVER', '2025-12-28 21:53:10', '2025-12-28 21:53:09');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (96, 1, -41550587271500, '2025-12-28 21:53:10', 0, 'OBSERVER', '2025-12-28 21:53:10', '2025-12-28 21:53:09');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (97, 1, -41550939265000, '2025-12-28 21:53:10', 0, 'OBSERVER', '2025-12-28 21:53:10', '2025-12-28 21:53:10');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (98, 1, -41551147310700, '2025-12-28 21:53:10', 0, 'OBSERVER', '2025-12-28 21:53:10', '2025-12-28 21:53:10');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (99, 1, -41551537670600, '2025-12-28 21:53:11', 0, 'OBSERVER', '2025-12-28 21:53:11', '2025-12-28 21:53:10');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (100, 1, -41551885650700, '2025-12-28 21:53:11', 0, 'OBSERVER', '2025-12-28 21:53:11', '2025-12-28 21:53:11');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (101, 1, -41606430954800, '2025-12-28 21:54:06', 0, 'OBSERVER', '2025-12-28 21:54:06', '2025-12-28 21:54:06');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (102, 1, -41606811226200, '2025-12-28 21:54:06', 0, 'OBSERVER', '2025-12-28 21:54:06', '2025-12-28 21:54:06');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (103, 1, -41607373226200, '2025-12-28 21:54:07', 0, 'OBSERVER', '2025-12-28 21:54:07', '2025-12-28 21:54:06');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (104, 1, -41607826818100, '2025-12-28 21:54:07', 0, 'OBSERVER', '2025-12-28 21:54:07', '2025-12-28 21:54:07');
INSERT INTO `chat_room_members` (`id`, `room_id`, `user_id`, `joined_at`, `is_online`, `role`, `created_at`, `updated_at`) VALUES (105, 1, -41608263323600, '2025-12-28 21:54:08', 0, 'OBSERVER', '2025-12-28 21:54:08', '2025-12-28 21:54:07');
COMMIT;

-- ----------------------------
-- Table structure for chat_room_messages
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_messages`;
CREATE TABLE `chat_room_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `message_type` enum('TEXT','IMAGE','SYSTEM') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'TEXT',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_room_id` (`room_id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  KEY `idx_room_created_at` (`room_id`,`created_at`) USING BTREE,
  CONSTRAINT `chat_room_messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_room_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of chat_room_messages
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for chat_rooms
-- ----------------------------
DROP TABLE IF EXISTS `chat_rooms`;
CREATE TABLE `chat_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'Restaurant Chat Room',
  `verification_code` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ACTIVE','CLOSED','EXPIRED') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'ACTIVE',
  `last_message` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `last_message_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `online_user_count` int DEFAULT '0',
  `verification_code_generated_at` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_verification_code` (`verification_code`) USING BTREE,
  CONSTRAINT `chat_rooms_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of chat_rooms
-- ----------------------------
BEGIN;
INSERT INTO `chat_rooms` (`id`, `restaurant_id`, `name`, `verification_code`, `status`, `last_message`, `last_message_time`, `online_user_count`, `verification_code_generated_at`, `created_at`, `updated_at`) VALUES (1, 1, 'Chuanwei Xuan Chat Room', '303189', 'ACTIVE', '测试完成，这是最后一条消息', '2025-12-28 20:04:44', 0, '2026-02-27 22:00:00', '2025-11-01 11:01:55', '2025-12-28 20:04:44');
INSERT INTO `chat_rooms` (`id`, `restaurant_id`, `name`, `verification_code`, `status`, `last_message`, `last_message_time`, `online_user_count`, `verification_code_generated_at`, `created_at`, `updated_at`) VALUES (2, 2, 'Yuexiang Lou Chat Room', '886657', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2026-02-27 22:00:00', '2025-11-01 11:01:55', '2025-11-01 11:01:55');
INSERT INTO `chat_rooms` (`id`, `restaurant_id`, `name`, `verification_code`, `status`, `last_message`, `last_message_time`, `online_user_count`, `verification_code_generated_at`, `created_at`, `updated_at`) VALUES (3, 3, 'Xiangwei Guan Chat Room', '663852', 'ACTIVE', NULL, '2025-11-01 11:01:55', 0, '2026-02-27 22:00:00', '2025-11-01 11:01:55', '2025-11-01 11:01:55');
COMMIT;

-- ----------------------------
-- Table structure for dish_review_images
-- ----------------------------
DROP TABLE IF EXISTS `dish_review_images`;
CREATE TABLE `dish_review_images` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `dish_review_id` bigint NOT NULL COMMENT '菜品评价ID',
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_dish_review_id` (`dish_review_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_dish_review_images_review` FOREIGN KEY (`dish_review_id`) REFERENCES `dish_reviews` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品评价图片表';

-- ----------------------------
-- Records of dish_review_images
-- ----------------------------
BEGIN;
INSERT INTO `dish_review_images` (`id`, `dish_review_id`, `image_url`, `sort_order`, `created_at`, `updated_at`) VALUES (1, 4, '/uploads/b63a3577-1359-4834-a0b6-f21a52dc48f8.jpg', 1, '2026-02-10 20:43:04', '2026-02-10 20:43:04');
COMMIT;

-- ----------------------------
-- Table structure for dish_reviews
-- ----------------------------
DROP TABLE IF EXISTS `dish_reviews`;
CREATE TABLE `dish_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `menu_item_id` bigint NOT NULL COMMENT '菜品ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` int NOT NULL COMMENT '评分（1-5）',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '评论内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_menu_item_id` (`menu_item_id`),
  KEY `idx_restaurant_id` (`restaurant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_dish_reviews_menu_item` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_dish_reviews_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_dish_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_dish_rating_range` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品评价表';

-- ----------------------------
-- Records of dish_reviews
-- ----------------------------
BEGIN;
INSERT INTO `dish_reviews` (`id`, `menu_item_id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (4, 2, 1, 3, 5, 'Very delicious', '2026-02-10 20:43:04', '2026-02-10 20:43:04');
COMMIT;

-- ----------------------------
-- Table structure for menu_categories
-- ----------------------------
DROP TABLE IF EXISTS `menu_categories`;
CREATE TABLE `menu_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Category Name',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Category Description',
  `sort_order` int DEFAULT '0' COMMENT 'Sort Order',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Is Active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  CONSTRAINT `fk_menu_categories_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Menu Category Table';

-- ----------------------------
-- Records of menu_categories
-- ----------------------------
BEGIN;
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (1, 1, 'Hot Dishes', 'Sichuan hot dishes series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (2, 1, 'Cold Dishes', 'Sichuan cold dishes series', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (3, 1, 'Soups', 'Sichuan soup series', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (4, 2, 'Cantonese Dishes', 'Cantonese cuisine series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (5, 2, 'Dim Sum', 'Cantonese dim sum', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (6, 2, 'Soups', 'Cantonese soups', 3, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (7, 3, 'Hunan Dishes', 'Hunan cuisine series', 1, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
INSERT INTO `menu_categories` (`id`, `restaurant_id`, `name`, `description`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (8, 3, 'Cold Dishes', 'Hunan cold dishes series', 2, 1, '2025-11-21 19:05:33', '2025-11-21 19:05:33');
COMMIT;

-- ----------------------------
-- Table structure for menu_items
-- ----------------------------
DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE `menu_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Dish ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `category_id` bigint NOT NULL COMMENT 'Category ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Dish Name',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Dish Description',
  `price` decimal(10,2) NOT NULL COMMENT 'Price',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT 'Original Price',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Image URL',
  `is_available` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Is Available',
  `is_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Is Recommended',
  `sort_order` int DEFAULT '0' COMMENT 'Sort Order',
  `nutrition_info` json DEFAULT NULL COMMENT 'Nutrition Info (JSON format)',
  `allergen_info` json DEFAULT NULL COMMENT 'Allergen Info (JSON format)',
  `spice_level` enum('NONE','MILD','MEDIUM','HOT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NONE' COMMENT 'Spice Level',
  `preparation_time` int DEFAULT NULL COMMENT 'Preparation Time (minutes)',
  `calories` int DEFAULT NULL COMMENT 'Calories',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '平均评分',
  `review_count` int DEFAULT '0' COMMENT '评价数量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_category_id` (`category_id`) USING BTREE,
  KEY `idx_is_available` (`is_available`) USING BTREE,
  KEY `idx_is_recommended` (`is_recommended`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_menu_items_category` FOREIGN KEY (`category_id`) REFERENCES `menu_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_menu_items_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Menu Dish Table';

-- ----------------------------
-- Records of menu_items
-- ----------------------------
BEGIN;
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (2, 1, 1, 'Mapo Tofu', 'Spicy and numbing, tender tofu with savory minced meat', 32.00, NULL, '/uploads/12.jpg', 1, 1, 0, NULL, NULL, 'HOT', 10, NULL, '2025-11-21 19:05:33', '2026-02-09 19:45:17', 5.00, 1);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (3, 1, 1, 'Sliced Fish in Chili Oil', 'Tender fish slices with rich vegetables, spicy and aromatic', 68.00, NULL, '/uploads/4.png', 1, 0, 0, NULL, NULL, 'HOT', 20, NULL, '2025-11-21 19:05:33', '2026-02-09 19:39:13', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (4, 1, 2, 'Fuqi Feipian (Beef Offal)', 'Spicy and refreshing, tender beef slices', 42.00, NULL, '/uploads/5.png', 1, 0, 0, NULL, NULL, 'MEDIUM', 8, NULL, '2025-11-21 19:05:33', '2026-02-09 19:39:08', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (5, 1, 3, 'Hot & Sour Soup', 'Tangy and spicy, nutritious and appetizing', 28.00, NULL, '/uploads/2.jpg', 1, 0, 0, NULL, NULL, 'MILD', 12, NULL, '2025-11-21 19:05:33', '2026-02-09 19:40:45', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (6, 2, 1, 'White-Cut Chicken', 'Juicy and tender, served with ginger-scallion sauce', 68.00, NULL, '/uploads/11.jpg', 1, 1, 0, NULL, NULL, 'NONE', 15, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:03', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (7, 2, 1, 'Roast Goose', 'Crispy skin, tender meat, full of flavor', 88.00, NULL, '/uploads/3.jpg', 1, 1, 0, NULL, NULL, 'NONE', 25, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:35', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (8, 2, 2, 'BBQ Pork Buns', 'Sweet and soft with rich filling', 28.00, NULL, '/uploads/6.png', 1, 1, 0, NULL, NULL, 'NONE', 10, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:59', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (9, 2, 2, 'Shrimp Dumplings', 'Thin skin, generous filling, delicious and fresh', 32.00, NULL, '/uploads/9.jpg', 1, 0, 0, NULL, NULL, 'NONE', 12, NULL, '2025-11-21 19:05:33', '2026-02-09 19:42:32', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (10, 2, 3, 'Slow-Cooked Cantonese Soup', 'Slow-simmered, rich in nutrients', 48.00, NULL, '/uploads/10.webp', 1, 0, 0, NULL, NULL, 'NONE', 30, NULL, '2025-11-21 19:05:33', '2026-02-09 19:43:27', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (11, 3, 1, 'Steamed Fish Head with Chopped Chili', 'Spicy, aromatic, and tender fish meat', 78.00, NULL, '/uploads/7.png', 1, 1, 0, NULL, NULL, 'HOT', 25, NULL, '2025-11-21 19:05:33', '2026-02-09 19:43:41', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (12, 3, 1, 'Country-Style Stir-Fried Pork', 'Spicy and savory, great with rice', 52.00, NULL, '/uploads/1.jpg', 1, 0, 0, NULL, NULL, 'MEDIUM', 15, NULL, '2025-11-21 19:05:33', '2026-02-09 19:44:03', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (13, 3, 2, 'Cucumber Salad', 'Refreshing and simple', 18.00, NULL, '/uploads/8.png', 1, 0, 0, NULL, NULL, 'NONE', 5, NULL, '2025-11-21 19:05:33', '2026-02-09 19:44:20', 0.00, 0);
INSERT INTO `menu_items` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (17, 1, 1, 'Kung Pao Chicken', 'Classic Sichuan dish, spicy and aromatic', 48.00, NULL, '/uploads/default_dish.png', 1, 1, 0, NULL, NULL, 'NONE', NULL, NULL, '2025-10-05 14:54:32', '2026-02-09 21:15:52', 0.00, 0);
COMMIT;

-- ----------------------------
-- Table structure for menu_items_backup
-- ----------------------------
DROP TABLE IF EXISTS `menu_items_backup`;
CREATE TABLE `menu_items_backup` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'Dish ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `category_id` bigint NOT NULL COMMENT 'Category ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Dish Name',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Dish Description',
  `price` decimal(10,2) NOT NULL COMMENT 'Price',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT 'Original Price',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Image URL',
  `is_available` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Is Available',
  `is_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Is Recommended',
  `sort_order` int DEFAULT '0' COMMENT 'Sort Order',
  `nutrition_info` json DEFAULT NULL COMMENT 'Nutrition Info (JSON format)',
  `allergen_info` json DEFAULT NULL COMMENT 'Allergen Info (JSON format)',
  `spice_level` enum('NONE','MILD','MEDIUM','HOT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NONE' COMMENT 'Spice Level',
  `preparation_time` int DEFAULT NULL COMMENT 'Preparation Time (minutes)',
  `calories` int DEFAULT NULL COMMENT 'Calories',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '平均评分',
  `review_count` int DEFAULT '0' COMMENT '评价数量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of menu_items_backup
-- ----------------------------
BEGIN;
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (2, 1, 1, 'Mapo Tofu', 'Spicy and numbing, tender tofu with savory minced meat', 32.00, NULL, '/uploads/12.jpg', 1, 1, 0, NULL, NULL, 'HOT', 10, NULL, '2025-11-21 19:05:33', '2026-02-09 19:45:17', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (3, 1, 1, 'Sliced Fish in Chili Oil', 'Tender fish slices with rich vegetables, spicy and aromatic', 68.00, NULL, '/uploads/4.png', 1, 0, 0, NULL, NULL, 'HOT', 20, NULL, '2025-11-21 19:05:33', '2026-02-09 19:39:13', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (4, 1, 2, 'Fuqi Feipian (Beef Offal)', 'Spicy and refreshing, tender beef slices', 42.00, NULL, '/uploads/5.png', 1, 0, 0, NULL, NULL, 'MEDIUM', 8, NULL, '2025-11-21 19:05:33', '2026-02-09 19:39:08', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (5, 1, 3, 'Hot & Sour Soup', 'Tangy and spicy, nutritious and appetizing', 28.00, NULL, '/uploads/2.jpg', 1, 0, 0, NULL, NULL, 'MILD', 12, NULL, '2025-11-21 19:05:33', '2026-02-09 19:40:45', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (6, 2, 1, 'White-Cut Chicken', 'Juicy and tender, served with ginger-scallion sauce', 68.00, NULL, '/uploads/11.jpg', 1, 1, 0, NULL, NULL, 'NONE', 15, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:03', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (7, 2, 1, 'Roast Goose', 'Crispy skin, tender meat, full of flavor', 88.00, NULL, '/uploads/3.jpg', 1, 1, 0, NULL, NULL, 'NONE', 25, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:35', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (8, 2, 2, 'BBQ Pork Buns', 'Sweet and soft with rich filling', 28.00, NULL, '/uploads/6.png', 1, 1, 0, NULL, NULL, 'NONE', 10, NULL, '2025-11-21 19:05:33', '2026-02-09 19:41:59', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (9, 2, 2, 'Shrimp Dumplings', 'Thin skin, generous filling, delicious and fresh', 32.00, NULL, '/uploads/9.jpg', 1, 0, 0, NULL, NULL, 'NONE', 12, NULL, '2025-11-21 19:05:33', '2026-02-09 19:42:32', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (10, 2, 3, 'Slow-Cooked Cantonese Soup', 'Slow-simmered, rich in nutrients', 48.00, NULL, '/uploads/10.webp', 1, 0, 0, NULL, NULL, 'NONE', 30, NULL, '2025-11-21 19:05:33', '2026-02-09 19:43:27', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (11, 3, 1, 'Steamed Fish Head with Chopped Chili', 'Spicy, aromatic, and tender fish meat', 78.00, NULL, '/uploads/7.png', 1, 1, 0, NULL, NULL, 'HOT', 25, NULL, '2025-11-21 19:05:33', '2026-02-09 19:43:41', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (12, 3, 1, 'Country-Style Stir-Fried Pork', 'Spicy and savory, great with rice', 52.00, NULL, '/uploads/1.jpg', 1, 0, 0, NULL, NULL, 'MEDIUM', 15, NULL, '2025-11-21 19:05:33', '2026-02-09 19:44:03', 0.00, 0);
INSERT INTO `menu_items_backup` (`id`, `restaurant_id`, `category_id`, `name`, `description`, `price`, `original_price`, `image_url`, `is_available`, `is_recommended`, `sort_order`, `nutrition_info`, `allergen_info`, `spice_level`, `preparation_time`, `calories`, `created_at`, `updated_at`, `rating`, `review_count`) VALUES (13, 3, 2, 'Cucumber Salad', 'Refreshing and simple', 18.00, NULL, '/uploads/8.png', 1, 0, 0, NULL, NULL, 'NONE', 5, NULL, '2025-11-21 19:05:33', '2026-02-09 19:44:20', 0.00, 0);
COMMIT;

-- ----------------------------
-- Table structure for merchant_statistics
-- ----------------------------
DROP TABLE IF EXISTS `merchant_statistics`;
CREATE TABLE `merchant_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Stat ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `stat_date` date NOT NULL COMMENT 'Stat Date',
  `total_orders` int DEFAULT '0' COMMENT 'Total Orders',
  `total_revenue` decimal(10,2) DEFAULT '0.00' COMMENT 'Total Revenue',
  `average_order_value` decimal(10,2) DEFAULT '0.00' COMMENT 'Average Order Value',
  `total_customers` int DEFAULT '0' COMMENT 'Total Customers',
  `new_customers` int DEFAULT '0' COMMENT 'New Customers',
  `returning_customers` int DEFAULT '0' COMMENT 'Returning Customers',
  `average_rating` decimal(3,2) DEFAULT '0.00' COMMENT 'Average Rating',
  `total_reviews` int DEFAULT '0' COMMENT 'Total Reviews',
  `peak_hour` int DEFAULT NULL COMMENT 'Peak Hour (hour)',
  `peak_hour_orders` int DEFAULT '0' COMMENT 'Peak Hour Orders',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_restaurant_date` (`restaurant_id`,`stat_date`) USING BTREE,
  KEY `idx_stat_date` (`stat_date`) USING BTREE,
  CONSTRAINT `fk_merchant_statistics_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Merchant Statistics Table';

-- ----------------------------
-- Records of merchant_statistics
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for merchants
-- ----------------------------
DROP TABLE IF EXISTS `merchants`;
CREATE TABLE `merchants` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Merchant ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Associated Restaurant ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Merchant Username',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Email',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Password Hash',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Merchant Name',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Contact Phone',
  `role` enum('ADMIN','MANAGER','STAFF') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STAFF' COMMENT 'Merchant Role',
  `status` enum('ACTIVE','INACTIVE','BANNED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'Account Status',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT 'Last Login Time',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_email` (`email`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_role` (`role`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  CONSTRAINT `fk_merchants_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='Merchants Table';

-- ----------------------------
-- Records of merchants
-- ----------------------------
BEGIN;
INSERT INTO `merchants` (`id`, `restaurant_id`, `username`, `email`, `password_hash`, `name`, `phone`, `role`, `status`, `last_login_at`, `created_at`, `updated_at`) VALUES (1, 1, 'admin_chuanweixuan', 'admin@chuanweixuan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Chuanwei Xuan Admin', '13800138001', 'ADMIN', 'ACTIVE', '2026-02-10 18:11:33', '2025-11-21 19:05:33', '2025-11-27 11:29:42');
INSERT INTO `merchants` (`id`, `restaurant_id`, `username`, `email`, `password_hash`, `name`, `phone`, `role`, `status`, `last_login_at`, `created_at`, `updated_at`) VALUES (2, 2, 'admin_yuexianglou', 'admin@yuexianglou.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Yuexiang Lou Admin', '13800138002', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:43');
INSERT INTO `merchants` (`id`, `restaurant_id`, `username`, `email`, `password_hash`, `name`, `phone`, `role`, `status`, `last_login_at`, `created_at`, `updated_at`) VALUES (3, 3, 'admin_xiangweiguan', 'admin@xiangweiguan.com', '$2b$10$3/qmFEBWMVCkDCT1MKoYieE.ORhvf1MeKfwByDwhZz5c/Bxm0oULy', 'Xiangwei Guan Admin', '13800138003', 'ADMIN', 'ACTIVE', NULL, '2025-11-21 19:05:33', '2025-11-27 11:29:45');
COMMIT;

-- ----------------------------
-- Table structure for online_users
-- ----------------------------
DROP TABLE IF EXISTS `online_users`;
CREATE TABLE `online_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `session_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `connected_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_room_user` (`room_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_room_id` (`room_id`) USING BTREE,
  KEY `idx_session_id` (`session_id`) USING BTREE,
  KEY `idx_room_user` (`room_id`,`user_id`) USING BTREE,
  KEY `idx_last_active_at` (`last_active_at`) USING BTREE,
  CONSTRAINT `online_users_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `online_users_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of online_users
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for recommended_dishes
-- ----------------------------
DROP TABLE IF EXISTS `recommended_dishes`;
CREATE TABLE `recommended_dishes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `dish_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `price` decimal(10,2) DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  CONSTRAINT `recommended_dishes_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of recommended_dishes
-- ----------------------------
BEGIN;
INSERT INTO `recommended_dishes` (`id`, `restaurant_id`, `dish_name`, `description`, `price`, `image_url`, `created_at`) VALUES (1, 1, 'Kung Pao Chicken', 'Classic Sichuan dish, spicy and aromatic', 48.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` (`id`, `restaurant_id`, `dish_name`, `description`, `price`, `image_url`, `created_at`) VALUES (2, 1, 'Mapo Tofu', 'Spicy and numbing, tender tofu', 32.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` (`id`, `restaurant_id`, `dish_name`, `description`, `price`, `image_url`, `created_at`) VALUES (3, 2, 'White-Cut Chicken', 'Juicy and tender, original flavor', 68.00, NULL, '2025-10-05 14:54:32');
INSERT INTO `recommended_dishes` (`id`, `restaurant_id`, `dish_name`, `description`, `price`, `image_url`, `created_at`) VALUES (4, 2, 'BBQ Pork Buns', 'Sweet and soft with rich filling', 28.00, NULL, '2025-10-05 14:54:32');
COMMIT;

-- ----------------------------
-- Table structure for restaurants
-- ----------------------------
DROP TABLE IF EXISTS `restaurants`;
CREATE TABLE `restaurants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `distance` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `address` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `hours` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT '0.00',
  `review_count` int DEFAULT '0',
  `is_open` tinyint(1) DEFAULT '1',
  `avatar` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_is_open` (`is_open`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of restaurants
-- ----------------------------
BEGIN;
INSERT INTO `restaurants` (`id`, `name`, `type`, `distance`, `description`, `address`, `phone`, `hours`, `rating`, `review_count`, `is_open`, `avatar`, `image_url`, `created_at`, `updated_at`) VALUES (1, 'Chuanwei Xuan', 'Sichuan Cuisine', '500m', 'Authentic Sichuan cuisine, spicy and aromatic, elegant environment', 'No.123 Downtown Street', '17340307464', '10:00 - 22:00', 4.50, 10, 1, '', '/uploads/ChuanweiXuan.png', '2025-10-05 14:54:32', '2026-02-09 19:50:54');
INSERT INTO `restaurants` (`id`, `name`, `type`, `distance`, `description`, `address`, `phone`, `hours`, `rating`, `review_count`, `is_open`, `avatar`, `image_url`, `created_at`, `updated_at`) VALUES (2, 'Yuexiang Lou', 'Cantonese Cuisine', '800m', 'Refined Cantonese cuisine, light and fresh', 'No.456 Commercial District', '(021) 2345-6789', '11:00 - 21:30', 4.50, 10, 1, '', '/uploads/YuexiangLou.jpg', '2025-10-05 14:54:32', '2026-02-09 19:50:43');
INSERT INTO `restaurants` (`id`, `name`, `type`, `distance`, `description`, `address`, `phone`, `hours`, `rating`, `review_count`, `is_open`, `avatar`, `image_url`, `created_at`, `updated_at`) VALUES (3, 'Xiangwei Guan', 'Hunan Cuisine', '1.2km', 'Authentic Hunan cuisine, spicy and flavorful', 'No.789 Food Street', '(021) 3456-7890', '10:30 - 22:30', 4.50, 10, 1, '', '/uploads/XiangweiGuan.jpeg', '2025-10-05 14:54:32', '2026-02-09 19:51:09');
COMMIT;

-- ----------------------------
-- Table structure for review_images
-- ----------------------------
DROP TABLE IF EXISTS `review_images`;
CREATE TABLE `review_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `review_id` bigint NOT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_review_id` (`review_id`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  CONSTRAINT `review_images_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of review_images
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` int DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of reviews
-- ----------------------------
BEGIN;
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (1, 1, 1, 5, 'Food is delicious, service is excellent. Highly recommended!', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (2, 1, 2, 4, 'Nice ambiance, authentic flavor, just a bit too spicy', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (3, 2, 1, 5, 'Very authentic Cantonese cuisine, and excellent service', '2025-10-05 14:54:32', '2025-10-05 14:54:32');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (14, 1, 1, 5, 'Amazing taste!', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (15, 2, 1, 4, 'Nice environment', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (16, 1, 2, 5, 'Love the signature dish', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (17, 3, 2, 3, 'It is okay', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (18, 2, 3, 5, 'Great service', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (19, 3, 3, 4, 'Pretty good', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (20, 3, 4, 5, 'Highly recommend', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
INSERT INTO `reviews` (`id`, `restaurant_id`, `user_id`, `rating`, `comment`, `created_at`, `updated_at`) VALUES (21, 1, 5, 4, 'Will come again', '2026-03-01 15:48:38', '2026-03-01 15:48:38');
COMMIT;

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `position` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ONLINE','OFFLINE','BUSY') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'OFFLINE',
  `experience` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT '0.00',
  `avatar_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of staff
-- ----------------------------
BEGIN;
INSERT INTO `staff` (`id`, `restaurant_id`, `name`, `position`, `status`, `experience`, `rating`, `avatar_url`, `created_at`, `updated_at`) VALUES (1, 1, 'Xiao Zhang', 'Waiter', 'ONLINE', '3 years', 4.83, '/uploads/orangestar.png', '2025-10-05 14:54:32', '2026-02-09 19:53:00');
INSERT INTO `staff` (`id`, `restaurant_id`, `name`, `position`, `status`, `experience`, `rating`, `avatar_url`, `created_at`, `updated_at`) VALUES (2, 1, 'Xiao Li', 'Team Leader', 'ONLINE', '5 years', 5.00, '/uploads/ee.png', '2025-10-05 14:54:32', '2026-02-10 15:47:21');
INSERT INTO `staff` (`id`, `restaurant_id`, `name`, `position`, `status`, `experience`, `rating`, `avatar_url`, `created_at`, `updated_at`) VALUES (3, 2, 'Xiao Wang', 'Waiter', 'ONLINE', '2 years', 5.00, '/uploads/109951166145983724.webp', '2025-10-05 14:54:32', '2026-02-10 15:47:24');
COMMIT;

-- ----------------------------
-- Table structure for staff_languages
-- ----------------------------
DROP TABLE IF EXISTS `staff_languages`;
CREATE TABLE `staff_languages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `language` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_staff_id` (`staff_id`) USING BTREE,
  CONSTRAINT `staff_languages_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of staff_languages
-- ----------------------------
BEGIN;
INSERT INTO `staff_languages` (`id`, `staff_id`, `language`) VALUES (1, 1, 'Chinese');
INSERT INTO `staff_languages` (`id`, `staff_id`, `language`) VALUES (2, 1, 'English');
INSERT INTO `staff_languages` (`id`, `staff_id`, `language`) VALUES (3, 2, 'Chinese');
INSERT INTO `staff_languages` (`id`, `staff_id`, `language`) VALUES (4, 3, 'Chinese');
INSERT INTO `staff_languages` (`id`, `staff_id`, `language`) VALUES (5, 3, 'Japanese');
COMMIT;

-- ----------------------------
-- Table structure for staff_reviews
-- ----------------------------
DROP TABLE IF EXISTS `staff_reviews`;
CREATE TABLE `staff_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` decimal(3,2) NOT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_staff_id` (`staff_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `staff_reviews_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `staff_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of staff_reviews
-- ----------------------------
BEGIN;
INSERT INTO `staff_reviews` (`id`, `staff_id`, `user_id`, `rating`, `content`, `created_at`, `updated_at`) VALUES (1, 1, 1, 5.00, 'Excellent service attitude, very professional dish recommendations', '2025-10-05 14:54:32', '2026-02-10 14:01:35');
INSERT INTO `staff_reviews` (`id`, `staff_id`, `user_id`, `rating`, `content`, `created_at`, `updated_at`) VALUES (2, 1, 2, 4.50, 'Attentive service and patient in answering questions', '2025-10-05 14:54:32', '2026-02-10 14:01:35');
INSERT INTO `staff_reviews` (`id`, `staff_id`, `user_id`, `rating`, `content`, `created_at`, `updated_at`) VALUES (3, 1, 3, 5.00, 'very good', '2026-02-10 14:01:41', '2026-02-10 14:01:41');
COMMIT;

-- ----------------------------
-- Table structure for staff_skills
-- ----------------------------
DROP TABLE IF EXISTS `staff_skills`;
CREATE TABLE `staff_skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL,
  `skill_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_staff_id` (`staff_id`) USING BTREE,
  CONSTRAINT `staff_skills_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of staff_skills
-- ----------------------------
BEGIN;
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (1, 1, 'Dish Recommendation');
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (2, 1, 'Allergy Consultation');
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (3, 1, 'Flavor Adjustment');
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (4, 2, 'Reservation Service');
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (5, 2, 'Dish Recommendation');
INSERT INTO `staff_skills` (`id`, `staff_id`, `skill_name`) VALUES (6, 3, 'Dish Recommendation');
COMMIT;

-- ----------------------------
-- Table structure for user_favorite_foods
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite_foods`;
CREATE TABLE `user_favorite_foods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Food Name',
  `food_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Food Type (e.g., Sichuan, Cantonese, Japanese)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_food` (`user_id`,`food_name`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_food_type` (`food_type`) USING BTREE,
  CONSTRAINT `fk_favorite_foods_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Favorite Foods Table';

-- ----------------------------
-- Records of user_favorite_foods
-- ----------------------------
BEGIN;
INSERT INTO `user_favorite_foods` (`id`, `user_id`, `food_name`, `food_type`, `created_at`) VALUES (1, 1, 'Kung Pao Chicken', 'Sichuan', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` (`id`, `user_id`, `food_name`, `food_type`, `created_at`) VALUES (2, 1, 'Mapo Tofu', 'Sichuan', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` (`id`, `user_id`, `food_name`, `food_type`, `created_at`) VALUES (3, 2, 'White-Cut Chicken', 'Cantonese', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` (`id`, `user_id`, `food_name`, `food_type`, `created_at`) VALUES (4, 2, 'BBQ Pork Buns', 'Cantonese', '2025-11-03 21:29:50');
INSERT INTO `user_favorite_foods` (`id`, `user_id`, `food_name`, `food_type`, `created_at`) VALUES (5, 3, 'Steamed Fish Head with Chopped Chili', 'Hunan', '2025-11-03 21:29:50');
COMMIT;

-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT 'Follower ID',
  `following_id` bigint NOT NULL COMMENT 'Following ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_follower_following` (`follower_id`,`following_id`) USING BTREE,
  KEY `idx_follower_id` (`follower_id`) USING BTREE,
  KEY `idx_following_id` (`following_id`) USING BTREE,
  CONSTRAINT `fk_user_follows_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_follows_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Follow Relationships Table';

-- ----------------------------
-- Records of user_follows
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for user_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `user_recommendations`;
CREATE TABLE `user_recommendations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `recommended_user_id` bigint NOT NULL COMMENT 'Recommended User ID',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Recommendation Algorithm Type (COLLABORATIVE, SOCIAL, HYBRID)',
  `recommendation_score` decimal(5,4) NOT NULL COMMENT 'Recommendation Score',
  `recommendation_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Recommendation Reason',
  `is_viewed` tinyint(1) DEFAULT '0' COMMENT 'Has Been Viewed',
  `is_interested` tinyint(1) DEFAULT NULL COMMENT 'Is Interested (1: Yes, 0: No)',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'User Feedback',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_recommended_algorithm` (`user_id`,`recommended_user_id`,`algorithm_type`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_recommended_user_id` (`recommended_user_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  KEY `idx_is_viewed` (`is_viewed`) USING BTREE,
  KEY `idx_recommendation_score` (`recommendation_score`) USING BTREE,
  KEY `idx_algorithm_type` (`algorithm_type`) USING BTREE,
  CONSTRAINT `fk_user_recommendations_recommended_user` FOREIGN KEY (`recommended_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Recommendation Results Table';

-- ----------------------------
-- Records of user_recommendations
-- ----------------------------
BEGIN;
INSERT INTO `user_recommendations` (`id`, `user_id`, `recommended_user_id`, `algorithm_type`, `recommendation_score`, `recommendation_reason`, `is_viewed`, `is_interested`, `feedback`, `created_at`, `updated_at`) VALUES (14, 5, 3, 'WEIGHTED', 0.3873, '您和ljyhove都喜欢Cantonese Cuisine，如Yuexiang Lou，可能有相似的口味偏好', 1, NULL, NULL, '2026-03-01 15:41:24', '2026-03-01 15:49:42');
INSERT INTO `user_recommendations` (`id`, `user_id`, `recommended_user_id`, `algorithm_type`, `recommendation_score`, `recommendation_reason`, `is_viewed`, `is_interested`, `feedback`, `created_at`, `updated_at`) VALUES (15, 5, 2, 'WEIGHTED', 0.0829, '您和Li Si都关注了Zhang San，且餐厅品味相似', 1, NULL, NULL, '2026-03-01 15:41:24', '2026-03-01 15:49:11');
INSERT INTO `user_recommendations` (`id`, `user_id`, `recommended_user_id`, `algorithm_type`, `recommendation_score`, `recommendation_reason`, `is_viewed`, `is_interested`, `feedback`, `created_at`, `updated_at`) VALUES (16, 5, 1, 'WEIGHTED', 0.3873, '您和Zhang San都喜欢Cantonese Cuisine，如Yuexiang Lou，可能有相似的口味偏好', 0, NULL, NULL, '2026-03-01 15:49:20', '2026-03-01 15:49:42');
COMMIT;

-- ----------------------------
-- Table structure for user_restaurant_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_recommendations`;
CREATE TABLE `user_restaurant_recommendations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Recommendation Reason',
  `rating` decimal(3,2) DEFAULT NULL COMMENT 'User Rating (1-5)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_restaurant` (`user_id`,`restaurant_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  CONSTRAINT `fk_recommendations_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating_range` CHECK (((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0))))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Recommended Restaurants Table';

-- ----------------------------
-- Records of user_restaurant_recommendations
-- ----------------------------
BEGIN;
INSERT INTO `user_restaurant_recommendations` (`id`, `user_id`, `restaurant_id`, `reason`, `rating`, `created_at`, `updated_at`) VALUES (1, 1, 1, 'This Sichuan restaurant is very authentic, spicy and aromatic, with a great environment', 4.80, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` (`id`, `user_id`, `restaurant_id`, `reason`, `rating`, `created_at`, `updated_at`) VALUES (2, 2, 2, 'Their Cantonese cuisine is authentic, especially the white-cut chicken—tender and juicy', 4.60, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` (`id`, `user_id`, `restaurant_id`, `reason`, `rating`, `created_at`, `updated_at`) VALUES (3, 3, 3, 'Authentic Hunan flavors; the Steamed Fish Head with Chopped Chili is a must-try', 4.70, '2025-11-03 21:29:50', '2025-11-03 21:29:50');
INSERT INTO `user_restaurant_recommendations` (`id`, `user_id`, `restaurant_id`, `reason`, `rating`, `created_at`, `updated_at`) VALUES (5, 3, 1, NULL, NULL, '2026-02-10 15:49:19', '2026-02-10 15:49:19');
COMMIT;

-- ----------------------------
-- Table structure for user_restaurant_visits
-- ----------------------------
DROP TABLE IF EXISTS `user_restaurant_visits`;
CREATE TABLE `user_restaurant_visits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `restaurant_id` bigint NOT NULL COMMENT 'Restaurant ID',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CHECK_IN' COMMENT 'Visit Type: REVIEW, RECOMMENDATION, FAVORITE, CHECK_IN',
  `visit_date` date NOT NULL COMMENT 'Visit Date',
  `visit_count` int DEFAULT '1' COMMENT 'Visit Count',
  `last_visit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Visit Time',
  `rating` decimal(3,2) DEFAULT NULL COMMENT 'User Rating (1-5)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_restaurant_date_type` (`user_id`,`restaurant_id`,`visit_date`,`visit_type`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_restaurant_id` (`restaurant_id`) USING BTREE,
  KEY `idx_visit_date` (`visit_date`) USING BTREE,
  KEY `idx_last_visit_time` (`last_visit_time`) USING BTREE,
  KEY `idx_visit_type` (`visit_type`) USING BTREE,
  CONSTRAINT `fk_user_visits_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_visits_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_visits_rating_range` CHECK (((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0))))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Restaurant Visit History Table';

-- ----------------------------
-- Records of user_restaurant_visits
-- ----------------------------
BEGIN;
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (1, 1, 1, 'CHECK_IN', '2025-11-15', 3, '2025-11-19 09:59:11', 4.50, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (2, 1, 2, 'CHECK_IN', '2025-11-10', 1, '2025-11-19 09:59:11', 4.00, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (3, 2, 1, 'CHECK_IN', '2025-11-12', 2, '2025-11-19 09:59:11', 4.20, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (4, 2, 3, 'CHECK_IN', '2025-11-08', 1, '2025-11-19 09:59:11', 3.80, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (5, 3, 2, 'CHECK_IN', '2025-11-14', 2, '2025-11-19 09:59:11', 4.60, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (6, 3, 3, 'CHECK_IN', '2025-11-11', 1, '2025-11-19 09:59:11', 4.10, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (7, 4, 1, 'CHECK_IN', '2025-11-13', 1, '2025-11-19 09:59:11', 3.90, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
INSERT INTO `user_restaurant_visits` (`id`, `user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `last_visit_time`, `rating`, `created_at`, `updated_at`) VALUES (8, 5, 2, 'CHECK_IN', '2025-11-16', 1, '2025-11-19 09:59:11', 4.30, '2025-11-18 21:23:42', '2025-11-18 21:23:42');
COMMIT;

-- ----------------------------
-- Table structure for user_similarity_cache
-- ----------------------------
DROP TABLE IF EXISTS `user_similarity_cache`;
CREATE TABLE `user_similarity_cache` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `similar_user_id` bigint NOT NULL COMMENT 'Similar User ID',
  `similarity_score` decimal(5,4) NOT NULL COMMENT 'Similarity Score (0-1)',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Algorithm Type (COSINE, PEARSON, ADJUSTED_COSINE)',
  `calculated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Calculated At',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT 'Expires At',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_similar_algorithm` (`user_id`,`similar_user_id`,`algorithm_type`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_similar_user_id` (`similar_user_id`) USING BTREE,
  KEY `idx_similarity_score` (`similarity_score`) USING BTREE,
  KEY `idx_algorithm_type` (`algorithm_type`) USING BTREE,
  KEY `idx_expires_at` (`expires_at`) USING BTREE,
  CONSTRAINT `fk_user_similarity_similar_user` FOREIGN KEY (`similar_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_similarity_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_similarity_score_range` CHECK (((`similarity_score` >= 0.0) and (`similarity_score` <= 1.0)))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='User Similarity Cache Table';

-- ----------------------------
-- Records of user_similarity_cache
-- ----------------------------
BEGIN;
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (1, 1, 2, 0.7500, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (2, 1, 3, 0.6200, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (3, 2, 3, 0.6800, 'COSINE', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (4, 1, 2, 0.7200, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (5, 1, 3, 0.5900, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
INSERT INTO `user_similarity_cache` (`id`, `user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `calculated_at`, `expires_at`) VALUES (6, 2, 3, 0.6500, 'PEARSON', '2025-11-18 21:23:42', '2025-11-25 21:23:42');
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `display_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `avatar_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT 'Bio',
  `password_hash` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE','BANNED') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  KEY `idx_email` (`email`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (1, 'user1@example.com', 'Zhang San', '/uploads/orangestar.png', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2026-02-09 19:54:14');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (2, 'user2@example.com', 'Li Si', '/uploads/orangestar.png', NULL, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE', '2025-10-05 14:54:32', '2026-02-09 19:54:16');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (3, '3439426154@qq.com', 'ljyhove', '/uploads/orangestar.png', NULL, '$2a$10$tW.lIzX2M7YX5ZmrFIgxDODxbL3j47hebNdwFyTOOYvNsj.en502i', 'ACTIVE', '2025-10-08 18:36:39', '2026-02-09 19:54:18');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (4, 'ljyh223@163.com', 'Deng Haochen', '/uploads/orangestar.png', NULL, '$2a$10$.nbrKg00ypVCLT43n6JprePTveGAy8scoSB9R4kJB66OE7IYjXtV2', 'ACTIVE', '2025-11-02 17:50:37', '2026-02-09 19:54:19');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (5, 'alex@gmail.com', 'alex6', '/uploads/orangestar.png', 'good everyday', '$2a$10$D7hOAiJnQZ8GUQvWQGyGF.mP8.G4PzFyPIWpG6dYdCB45YhglEDom', 'ACTIVE', '2025-11-17 20:46:32', '2026-02-09 19:54:21');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (6, 'ale1x@gmail.com', 'alex', '/uploads/orangestar.png', NULL, '$2a$10$n0TG29xAwAJgKOLYF7lJkuaF6cZ4/54xHf3cVJ8jcNiiMsw1tjem6', 'ACTIVE', '2025-11-17 21:01:48', '2026-02-09 19:54:31');
INSERT INTO `users` (`id`, `email`, `display_name`, `avatar_url`, `bio`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES (7, 'alex2@gmail.com', 'alex2', '/uploads/orangestar.png', NULL, '$2a$10$3G9HNnPzMvegxcUKM6ouK.glN6QF2GnGWHLEvP38gmWcqSDBfcioq', 'ACTIVE', '2026-02-09 18:01:55', '2026-02-09 19:54:33');
COMMIT;

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

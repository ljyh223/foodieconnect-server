-- 评论图片表迁移脚本
-- 执行时间：2025-10-31

-- 1. 创建评论图片表
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- 2. 从reviews表中迁移现有的图片数据到review_images表（如果有）
INSERT INTO `review_images` (review_id, image_url, sort_order, created_at, updated_at)
SELECT id, image_url, 1, created_at, updated_at
FROM `reviews`
WHERE image_url IS NOT NULL AND image_url != '';

-- 3. 删除reviews表中的image_url字段
ALTER TABLE `reviews` DROP COLUMN `image_url`;

-- 4. 添加一些示例数据（可选）
-- INSERT INTO `review_images` (review_id, image_url, sort_order) VALUES
-- (1, '/uploads/review1_1.jpg', 1),
-- (1, '/uploads/review1_2.jpg', 2),
-- (2, '/uploads/review2_1.jpg', 1);
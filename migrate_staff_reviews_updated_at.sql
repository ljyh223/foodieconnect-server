-- 为 staff_reviews 表添加 updated_at 字段
-- 修复评价功能报错：Unknown column 'updated_at' in 'field list'

ALTER TABLE `staff_reviews`
ADD COLUMN `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
AFTER `created_at`;

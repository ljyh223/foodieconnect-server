-- 修复用户推荐表中字段名不匹配问题
-- 执行时间: 2025-11-19
-- 描述: 将 user_recommendations 表中的字段名重命名，以与实体类和SQL查询保持一致：
--       1. score -> recommendation_score
--       2. recommendation_type -> algorithm_type

-- 1. 先删除所有相关约束和索引
-- 删除检查约束（使用更兼容的语法）
-- 注意：某些MySQL版本不支持 DROP CHECK IF EXISTS，所以我们先尝试删除，忽略错误
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS
     WHERE CONSTRAINT_SCHEMA = DATABASE()
     AND CONSTRAINT_NAME = 'chk_recommendations_score_range') > 0,
    'ALTER TABLE `user_recommendations` DROP CHECK `chk_recommendations_score_range`',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 删除唯一约束
ALTER TABLE `user_recommendations`
DROP INDEX `uk_user_recommended_type`;

-- 删除索引（忽略不存在的索引错误）
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'user_recommendations'
     AND INDEX_NAME = 'idx_score') > 0,
    'DROP INDEX `idx_score` ON `user_recommendations`',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'user_recommendations'
     AND INDEX_NAME = 'idx_recommendation_type') > 0,
    'DROP INDEX `idx_recommendation_type` ON `user_recommendations`',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 修改字段名
ALTER TABLE `user_recommendations`
CHANGE COLUMN `score` `recommendation_score` decimal(5,4) NOT NULL COMMENT '推荐分数';

-- 3. 修改推荐类型字段名
ALTER TABLE `user_recommendations`
CHANGE COLUMN `recommendation_type` `algorithm_type` varchar(50) NOT NULL COMMENT '推荐算法类型(COLLABORATIVE, SOCIAL, HYBRID)';

-- 4. 重新创建索引
CREATE INDEX `idx_recommendation_score` ON `user_recommendations` (`recommendation_score`);
CREATE INDEX `idx_algorithm_type` ON `user_recommendations` (`algorithm_type`);

-- 5. 重新创建唯一约束
ALTER TABLE `user_recommendations`
ADD UNIQUE KEY `uk_user_recommended_algorithm` (`user_id`, `recommended_user_id`, `algorithm_type`);

-- 6. 重新创建检查约束
ALTER TABLE `user_recommendations`
ADD CONSTRAINT `chk_recommendations_score_range` CHECK ((`recommendation_score` >= 0.0) and (`recommendation_score` <= 1.0));

-- 7. 更新示例数据中的字段名（如果存在）
-- 注意：如果示例数据已经插入，需要更新字段名
-- 由于我们已经重命名了字段，这里不需要更新数据
-- 如果在重命名前有数据，MySQL会自动处理

-- 7. 验证修改结果
-- 检查表结构
DESCRIBE `user_recommendations`;

-- 检查索引
SHOW INDEX FROM `user_recommendations` WHERE Key_name = 'idx_recommendation_score';

-- 检查约束
SELECT CONSTRAINT_NAME, CHECK_CLAUSE
FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS
WHERE CONSTRAINT_SCHEMA = DATABASE()
AND CONSTRAINT_NAME = 'chk_recommendations_score_range';

-- 验证数据完整性
SELECT COUNT(*) as total_records,
       COUNT(CASE WHEN recommendation_score IS NOT NULL THEN 1 END) as records_with_score
FROM `user_recommendations`;
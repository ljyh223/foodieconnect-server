-- 修复用户推荐表中字段名不匹配问题（简化版本）
-- 执行时间: 2025-11-19
-- 描述: 将 user_recommendations 表中的字段名重命名，以与实体类和SQL查询保持一致
--       使用更兼容的语法，适用于不同MySQL版本

-- 1. 删除索引和约束（使用更安全的方式）
-- 删除唯一约束（如果存在）
ALTER TABLE `user_recommendations` DROP INDEX `uk_user_recommended_type`;

-- 删除普通索引（如果存在）
DROP INDEX `idx_score` ON `user_recommendations`;
DROP INDEX `idx_recommendation_type` ON `user_recommendations`;

-- 删除检查约束（如果存在）
ALTER TABLE `user_recommendations` DROP CHECK `chk_recommendations_score_range`;

-- 3. 修改字段名
ALTER TABLE `user_recommendations`
CHANGE COLUMN `score` `recommendation_score` decimal(5,4) NOT NULL COMMENT '推荐分数';

ALTER TABLE `user_recommendations`
CHANGE COLUMN `recommendation_type` `algorithm_type` varchar(50) NOT NULL COMMENT '推荐算法类型(COLLABORATIVE, SOCIAL, HYBRID)';

-- 4. 修改推荐理由字段名
ALTER TABLE `user_recommendations`
CHANGE COLUMN `reason` `recommendation_reason` text COMMENT '推荐理由';

-- 5. 重新创建索引
CREATE INDEX `idx_recommendation_score` ON `user_recommendations` (`recommendation_score`);
CREATE INDEX `idx_algorithm_type` ON `user_recommendations` (`algorithm_type`);

-- 6. 重新创建唯一约束
ALTER TABLE `user_recommendations`
ADD UNIQUE KEY `uk_user_recommended_algorithm` (`user_id`, `recommended_user_id`, `algorithm_type`);

-- 7. 重新创建检查约束
ALTER TABLE `user_recommendations`
ADD CONSTRAINT `chk_recommendations_score_range` CHECK ((`recommendation_score` >= 0.0) and (`recommendation_score` <= 1.0));

-- 8. 验证修改结果
-- 检查表结构
DESCRIBE `user_recommendations`;

-- 检查索引
SHOW INDEX FROM `user_recommendations`;

-- 验证数据完整性
SELECT COUNT(*) as total_records, 
       COUNT(CASE WHEN recommendation_score IS NOT NULL THEN 1 END) as records_with_score,
       COUNT(CASE WHEN algorithm_type IS NOT NULL THEN 1 END) as records_with_algorithm_type
FROM `user_recommendations`;
-- 验证字段名修复是否成功
-- 执行时间: 2025-11-19
-- 描述: 验证 user_recommendations 表中的字段名是否已正确修改

-- 1. 检查表结构
DESCRIBE `user_recommendations`;

-- 2. 检查索引
SHOW INDEX FROM `user_recommendations`;

-- 3. 检查约束
SELECT 
    CONSTRAINT_NAME, 
    CONSTRAINT_TYPE,
    CHECK_CLAUSE
FROM 
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
LEFT JOIN 
    INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME
WHERE 
    tc.TABLE_SCHEMA = DATABASE() 
    AND tc.TABLE_NAME = 'user_recommendations';

-- 4. 测试SQL查询（模拟原始错误的查询）
SELECT r.*, u.display_name as recommended_user_name, u.avatar_url as recommended_user_avatar 
FROM user_recommendations r 
LEFT JOIN users u ON r.recommended_user_id = u.id 
WHERE r.user_id = 5 
ORDER BY r.recommendation_score DESC, r.created_at DESC 
LIMIT 0, 10;

-- 5. 验证数据完整性
SELECT 
    COUNT(*) as total_records,
    COUNT(CASE WHEN recommendation_score IS NOT NULL THEN 1 END) as records_with_score,
    COUNT(CASE WHEN algorithm_type IS NOT NULL THEN 1 END) as records_with_algorithm_type
FROM user_recommendations;
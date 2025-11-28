-- 添加推荐算法所需的表结构
-- 执行时间: 2025-11-18
-- 描述: 为推荐算法系统添加必要的数据表

-- 1. 用户餐厅访问历史记录表
DROP TABLE IF EXISTS `user_restaurant_visits`;
CREATE TABLE `user_restaurant_visits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `restaurant_id` bigint NOT NULL COMMENT '餐厅ID',
  `visit_type` varchar(20) DEFAULT 'CHECK_IN' COMMENT '访问类型：REVIEW, RECOMMENDATION, FAVORITE, CHECK_IN',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `visit_count` int DEFAULT 1 COMMENT '访问次数',
  `rating` decimal(3,2) DEFAULT NULL COMMENT '用户评分(1-5)',
  `last_visit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_restaurant_date_type` (`user_id`, `restaurant_id`, `visit_date`, `visit_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_restaurant_id` (`restaurant_id`),
  KEY `idx_visit_date` (`visit_date`),
  KEY `idx_last_visit_time` (`last_visit_time`),
  KEY `idx_visit_type` (`visit_type`),
  CONSTRAINT `fk_user_visits_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_visits_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_visits_rating_range` CHECK ((`rating` is null) or ((`rating` >= 1.0) and (`rating` <= 5.0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户餐厅访问历史记录表';

-- 2. 用户相似度缓存表
DROP TABLE IF EXISTS `user_similarity_cache`;
CREATE TABLE `user_similarity_cache` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `similar_user_id` bigint NOT NULL COMMENT '相似用户ID',
  `similarity_score` decimal(5,4) NOT NULL COMMENT '相似度分数(0-1)',
  `algorithm_type` varchar(50) NOT NULL COMMENT '算法类型(COSINE, PEARSON, ADJUSTED_COSINE)',
  `calculated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_similar_algorithm` (`user_id`, `similar_user_id`, `algorithm_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_similar_user_id` (`similar_user_id`),
  KEY `idx_similarity_score` (`similarity_score`),
  KEY `idx_algorithm_type` (`algorithm_type`),
  KEY `idx_expires_at` (`expires_at`),
  CONSTRAINT `fk_user_similarity_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_similarity_similar_user` FOREIGN KEY (`similar_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_similarity_score_range` CHECK ((`similarity_score` >= 0.0) and (`similarity_score` <= 1.0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户相似度缓存表';

-- 3. 用户推荐结果表
DROP TABLE IF EXISTS `user_recommendations`;
CREATE TABLE `user_recommendations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `recommended_user_id` bigint NOT NULL COMMENT '推荐用户ID',
  `algorithm_type` varchar(50) NOT NULL COMMENT '推荐算法类型(COLLABORATIVE, SOCIAL, HYBRID)',
  `recommendation_score` decimal(5,4) NOT NULL COMMENT '推荐分数',
  `recommendation_reason` text COMMENT '推荐理由',
  `is_viewed` tinyint(1) DEFAULT 0 COMMENT '是否已查看',
  `is_interested` tinyint(1) DEFAULT NULL COMMENT '是否感兴趣(1:感兴趣, 0:不感兴趣)',
  `feedback` text COMMENT '用户反馈',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recommended_algorithm` (`user_id`, `recommended_user_id`, `algorithm_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_recommended_user_id` (`recommended_user_id`),
  KEY `idx_algorithm_type` (`algorithm_type`),
  KEY `idx_recommendation_score` (`recommendation_score`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_viewed` (`is_viewed`),
  CONSTRAINT `fk_user_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_recommendations_recommended_user` FOREIGN KEY (`recommended_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_recommendations_score_range` CHECK ((`recommendation_score` >= 0.0) and (`recommendation_score` <= 1.0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户推荐结果表';

-- 4. 添加一些示例数据用于测试
-- 用户餐厅访问历史示例数据
INSERT INTO `user_restaurant_visits` (`user_id`, `restaurant_id`, `visit_type`, `visit_date`, `visit_count`, `rating`, `last_visit_time`) VALUES
(1, 1, 'CHECK_IN', '2025-11-15', 3, 4.5, '2025-11-15 12:00:00'),
(1, 2, 'REVIEW', '2025-11-10', 1, 4.0, '2025-11-10 18:30:00'),
(2, 1, 'CHECK_IN', '2025-11-12', 2, 4.2, '2025-11-12 19:00:00'),
(2, 3, 'FAVORITE', '2025-11-08', 1, 3.8, '2025-11-08 20:15:00'),
(3, 2, 'CHECK_IN', '2025-11-14', 2, 4.6, '2025-11-14 13:45:00'),
(3, 3, 'REVIEW', '2025-11-11', 1, 4.1, '2025-11-11 12:30:00'),
(4, 1, 'FAVORITE', '2025-11-13', 1, 3.9, '2025-11-13 14:20:00'),
(5, 2, 'CHECK_IN', '2025-11-16', 1, 4.3, '2025-11-16 19:30:00');

-- 用户相似度缓存示例数据
INSERT INTO `user_similarity_cache` (`user_id`, `similar_user_id`, `similarity_score`, `algorithm_type`, `expires_at`) VALUES
(1, 2, 0.7500, 'COSINE', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(1, 3, 0.6200, 'COSINE', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(2, 3, 0.6800, 'COSINE', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(1, 2, 0.7200, 'PEARSON', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(1, 3, 0.5900, 'PEARSON', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(2, 3, 0.6500, 'PEARSON', DATE_ADD(NOW(), INTERVAL 7 DAY));

-- 用户推荐结果示例数据
INSERT INTO `user_recommendations` (`user_id`, `recommended_user_id`, `algorithm_type`, `recommendation_score`, `recommendation_reason`) VALUES
(1, 4, 'COLLABORATIVE', 0.7500, '您和用户4都喜欢访问川味轩和粤香楼'),
(1, 5, 'SOCIAL', 0.8000, '您关注的人也关注了用户5'),
(2, 5, 'HYBRID', 0.7200, '基于协同过滤和社交关系综合推荐'),
(3, 1, 'COLLABORATIVE', 0.6800, '您和用户1有相似的餐厅访问偏好'),
(4, 2, 'SOCIAL', 0.6500, '您的好友关注了用户2');
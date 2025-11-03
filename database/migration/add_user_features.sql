-- 用户功能拓展数据库迁移脚本
-- 创建时间: 2025-11-03

-- 1. 为用户表添加bio字段
ALTER TABLE users ADD COLUMN bio TEXT COMMENT '个人简介' AFTER avatar_url;

-- 2. 创建用户喜好食物表
CREATE TABLE user_favorite_foods (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL COMMENT '用户ID',
  food_name varchar(100) NOT NULL COMMENT '食物名称',
  food_type varchar(50) NULL COMMENT '食物类型(如:川菜,粤菜,日料等)',
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_food (user_id, food_name),
  KEY idx_user_id (user_id),
  KEY idx_food_type (food_type),
  CONSTRAINT fk_favorite_foods_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户喜好食物表';

-- 3. 创建用户关注关系表
CREATE TABLE user_follows (
  id bigint NOT NULL AUTO_INCREMENT,
  follower_id bigint NOT NULL COMMENT '关注者ID',
  following_id bigint NOT NULL COMMENT '被关注者ID',
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_follower_following (follower_id, following_id),
  KEY idx_follower_id (follower_id),
  KEY idx_following_id (following_id),
  CONSTRAINT fk_user_follows_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_user_follows_following FOREIGN KEY (following_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注关系表';

-- 4. 创建用户推荐餐厅表
CREATE TABLE user_restaurant_recommendations (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL COMMENT '用户ID',
  restaurant_id bigint NOT NULL COMMENT '餐厅ID',
  reason text COMMENT '推荐理由',
  rating decimal(3,2) DEFAULT NULL COMMENT '用户评分(1-5)',
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_restaurant (user_id, restaurant_id),
  KEY idx_user_id (user_id),
  KEY idx_restaurant_id (restaurant_id),
  CONSTRAINT fk_recommendations_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_recommendations_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE,
  CONSTRAINT chk_rating_range CHECK (rating IS NULL OR (rating >= 1.0 AND rating <= 5.0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户推荐餐厅表';

-- 5. 添加一些示例数据（可选）
-- 用户喜好食物示例
INSERT INTO user_favorite_foods (user_id, food_name, food_type) VALUES
(1, '宫保鸡丁', '川菜'),
(1, '麻婆豆腐', '川菜'),
(2, '白切鸡', '粤菜'),
(2, '叉烧包', '粤菜'),
(3, '剁椒鱼头', '湘菜');

-- 用户关注关系示例
INSERT INTO user_follows (follower_id, following_id) VALUES
(1, 2),
(1, 3),
(2, 3),
(3, 1);

-- 用户推荐餐厅示例
INSERT INTO user_restaurant_recommendations (user_id, restaurant_id, reason, rating) VALUES
(1, 1, '这家川菜非常正宗，麻辣鲜香，环境也很好', 4.8),
(2, 2, '粤菜做得很地道，特别是白切鸡，鲜嫩多汁', 4.6),
(3, 3, '湘菜口味很正宗，剁椒鱼头必点', 4.7);
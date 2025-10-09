-- TableTalk 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS tabletalk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tabletalk;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    display_name VARCHAR(100),
    avatar_url VARCHAR(500),
    password_hash VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_status (status)
);

-- 餐厅表
CREATE TABLE IF NOT EXISTS restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(100) NOT NULL,
    distance VARCHAR(50),
    description TEXT,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    hours VARCHAR(100),
    rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    is_open BOOLEAN DEFAULT TRUE,
    avatar VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_rating (rating),
    INDEX idx_is_open (is_open)
);

-- 推荐菜品表
CREATE TABLE IF NOT EXISTS recommended_dishes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    dish_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    INDEX idx_restaurant_id (restaurant_id)
);

-- 店员表
CREATE TABLE IF NOT EXISTS staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    status ENUM('ONLINE', 'OFFLINE', 'BUSY') DEFAULT 'OFFLINE',
    experience VARCHAR(50),
    rating DECIMAL(3,2) DEFAULT 0.00,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_status (status)
);

-- 店员技能表
CREATE TABLE IF NOT EXISTS staff_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    INDEX idx_staff_id (staff_id)
);

-- 店员语言表
CREATE TABLE IF NOT EXISTS staff_languages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    INDEX idx_staff_id (staff_id)
);

-- 评论表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- 店员评论表
CREATE TABLE IF NOT EXISTS staff_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating DECIMAL(3,2) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_staff_id (staff_id),
    INDEX idx_user_id (user_id)
);

-- 聊天会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'CLOSED', 'EXPIRED') DEFAULT 'ACTIVE',
    last_message TEXT,
    last_message_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unread_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_staff (user_id, staff_id),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_staff_id (staff_id)
);

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_type ENUM('USER', 'STAFF') NOT NULL,
    content TEXT NOT NULL,
    message_type ENUM('TEXT', 'IMAGE', 'SYSTEM') DEFAULT 'TEXT',
    image_url VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_created_at (created_at)
);

-- 插入测试数据
-- 插入测试用户
INSERT INTO users (email, phone, display_name, password_hash, status) VALUES
('user1@example.com', '13800138001', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE'),
('user2@example.com', '13800138002', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'ACTIVE');

-- 插入测试餐厅
INSERT INTO restaurants (name, type, distance, description, address, phone, hours, rating, review_count, is_open, avatar) VALUES
('川味轩', '川菜', '500m', '正宗川菜，麻辣鲜香，环境优雅', '市中心街道123号', '(021) 1234-5678', '10:00 - 22:00', 4.8, 128, TRUE, '川'),
('粤香楼', '粤菜', '800m', '精致粤菜，清淡鲜美', '商业区456号', '(021) 2345-6789', '11:00 - 21:30', 4.6, 95, TRUE, '粤'),
('湘味馆', '湘菜', '1.2km', '地道湘菜，香辣可口', '美食街789号', '(021) 3456-7890', '10:30 - 22:30', 4.7, 112, TRUE, '湘');

-- 插入推荐菜品
INSERT INTO recommended_dishes (restaurant_id, dish_name, description, price) VALUES
(1, '宫保鸡丁', '经典川菜，麻辣鲜香', 48.00),
(1, '麻婆豆腐', '麻辣鲜香，豆腐嫩滑', 32.00),
(2, '白切鸡', '鲜嫩多汁，原汁原味', 68.00),
(2, '叉烧包', '香甜软糯，馅料丰富', 28.00);

-- 插入店员数据
INSERT INTO staff (restaurant_id, name, position, status, experience, rating, avatar_url) VALUES
(1, '小张', '服务员', 'ONLINE', '3年', 4.9, '张'),
(1, '小李', '领班', 'BUSY', '5年', 4.8, '李'),
(2, '小王', '服务员', 'ONLINE', '2年', 4.7, '王');

-- 插入店员技能
INSERT INTO staff_skills (staff_id, skill_name) VALUES
(1, '菜品推荐'),
(1, '过敏咨询'),
(1, '口味调整'),
(2, '订座服务'),
(2, '菜品推荐'),
(3, '菜品推荐');

-- 插入店员语言
INSERT INTO staff_languages (staff_id, language) VALUES
(1, '中文'),
(1, '英文'),
(2, '中文'),
(3, '中文'),
(3, '日语');

-- 插入评论数据
INSERT INTO reviews (restaurant_id, user_id, rating, comment) VALUES
(1, 1, 5, '菜品非常好吃，服务也很周到，强烈推荐！'),
(1, 2, 4, '环境不错，菜品口味正宗，就是有点辣'),
(2, 1, 5, '粤菜很正宗，服务态度很好');

-- 插入店员评论
INSERT INTO staff_reviews (staff_id, user_id, rating, content) VALUES
(1, 1, 5.0, '服务态度很好，推荐菜品很专业'),
(1, 2, 4.5, '服务周到，解答问题很耐心');

-- 插入聊天会话
INSERT INTO chat_sessions (restaurant_id, staff_id, user_id, status, last_message, unread_count) VALUES
(1, 1, 1, 'ACTIVE', '您好！很高兴为您服务', 2);

-- 插入聊天消息
INSERT INTO chat_messages (session_id, sender_id, sender_type, content, message_type) VALUES
(1, 1, 'STAFF', '您好！很高兴为您服务', 'TEXT'),
(1, 1, 'USER', '你好，我想了解一下特色菜品', 'TEXT'),
(1, 1, 'STAFF', '我们店的特色菜品有宫保鸡丁、麻婆豆腐等', 'TEXT');

COMMIT;
-- 移除外键约束
ALTER TABLE restaurants DROP FOREIGN KEY fk_restaurant_chat_room;

-- 移除索引
DROP INDEX idx_restaurants_room_id ON restaurants;

-- 从restaurants表中移除room_id字段
ALTER TABLE restaurants DROP COLUMN room_id;
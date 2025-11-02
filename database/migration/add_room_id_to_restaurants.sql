-- 为restaurants表添加room_id字段
ALTER TABLE restaurants ADD COLUMN room_id BIGINT;

-- 添加外键约束，关联到chat_rooms表
ALTER TABLE restaurants ADD CONSTRAINT fk_restaurant_chat_room 
FOREIGN KEY (room_id) REFERENCES chat_rooms(id);

-- 为room_id字段添加索引，提高查询性能
CREATE INDEX idx_restaurants_room_id ON restaurants(room_id);
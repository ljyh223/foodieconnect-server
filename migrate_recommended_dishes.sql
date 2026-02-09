-- 推荐菜品表冗余消除 - 数据迁移脚本
-- 目的：将 recommended_dishes 表的数据迁移到 menu_items 表，统一使用 is_recommended 字段

-- 备份现有 menu_items 数据
CREATE TABLE IF NOT EXISTS menu_items_backup AS SELECT * FROM menu_items;

-- 步骤1：迁移逻辑 - 将 recommended_dishes 中不存在于 menu_items 的菜品添加到 menu_items
INSERT INTO menu_items (
    restaurant_id,
    category_id,
    name,
    description,
    price,
    image_url,
    is_available,
    is_recommended,
    sort_order,
    created_at
)
SELECT
    rd.restaurant_id,
    -- 如果 menu_items 中已有该餐厅的菜品，使用第一个分类ID，否则创建默认分类
    COALESCE(
        (SELECT MIN(id) FROM menu_categories WHERE restaurant_id = rd.restaurant_id),
        0
    ),
    rd.dish_name,
    rd.description,
    rd.price,
    COALESCE(rd.image_url, '/uploads/default_dish.png'), -- 如果没有图片，使用默认图
    1, -- is_available
    1, -- is_recommended
    0, -- sort_order
    rd.created_at
FROM recommended_dishes rd
WHERE NOT EXISTS (
    -- 排除已存在的菜品（根据餐厅ID和名称判断）
    SELECT 1 FROM menu_items mi
    WHERE mi.restaurant_id = rd.restaurant_id
    AND mi.name = rd.dish_name
);

-- 步骤2：对于已存在于 menu_items 的推荐菜品，更新推荐状态
UPDATE menu_items mi
INNER JOIN recommended_dishes rd ON mi.restaurant_id = rd.restaurant_id AND mi.name = rd.dish_name
SET mi.is_recommended = 1,
    mi.image_url = COALESCE(mi.image_url, rd.image_url, '/uploads/default_dish.png')
WHERE mi.name = rd.dish_name;

-- 验证查询：检查迁移后的推荐菜品数量
-- SELECT COUNT(*) as recommended_count FROM menu_items WHERE is_recommended = 1;

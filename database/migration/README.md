# 数据库迁移说明

## 概述
本目录包含数据库迁移脚本，用于在不删除现有数据库的情况下更新数据库结构。

## 迁移文件

### add_review_images_table.sql
- **目的**: 为评论系统添加多图片支持
- **执行时间**: 2025-10-31
- **影响**: 
  - 创建新的 `review_images` 表
  - 从 `reviews` 表迁移现有图片数据
  - 删除 `reviews` 表中的 `image_url` 字段

## 执行方法

### 方法1: 使用MySQL命令行
```bash
mysql -u root -p tabletalk < migration/add_review_images_table.sql
```

### 方法2: 使用MySQL Workbench
1. 打开MySQL Workbench
2. 连接到tabletalk数据库
3. 打开SQL编辑器
4. 复制并执行 `add_review_images_table.sql` 中的内容

### 方法3: 使用phpMyAdmin
1. 登录phpMyAdmin
2. 选择tabletalk数据库
3. 点击"SQL"选项卡
4. 上传或粘贴 `add_review_images_table.sql` 的内容
5. 点击执行

## 回滚计划
如果需要回滚此迁移，可以执行以下SQL：
```sql
-- 1. 添加回image_url字段
ALTER TABLE `reviews` ADD COLUMN `image_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL;

-- 2. 从review_images表迁移数据（只取第一张图片）
UPDATE `reviews` r 
SET r.image_url = (
    SELECT ri.image_url 
    FROM review_images ri 
    WHERE ri.review_id = r.id 
    ORDER BY ri.sort_order ASC 
    LIMIT 1
);

-- 3. 删除review_images表
DROP TABLE IF EXISTS `review_images`;
```

## 注意事项
1. 执行迁移前请备份数据库
2. 确保应用程序已停止运行
3. 迁移完成后重启应用程序
4. 验证功能是否正常工作
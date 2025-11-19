-- 移除用户表中的手机号字段
-- 执行时间: 2025-11-17
-- 描述: 移除users表中的phone字段，因为系统改为只使用邮箱进行登录和注册

-- 移除phone字段
ALTER TABLE users DROP COLUMN phone;
-- 数据库迁移脚本：移除店员排班相关表
-- 执行时间：2025-12-13

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 删除店员排班表
-- ----------------------------
DROP TABLE IF EXISTS `staff_schedules`;

SET FOREIGN_KEY_CHECKS = 1;

-- 迁移说明：
-- 1. 删除了店员排班表 staff_schedules
-- 2. 该表用于存储店员的排班信息，包括班次类型、时间、状态等
-- 3. 相关的应用代码已同步移除，包括：
--    - StaffScheduleController 中的排班相关接口
--    - StaffScheduleService 服务类
--    - StaffSchedule 实体类
--    - StaffScheduleMapper 数据访问层
--    - StaffScheduleRequest DTO
-- 4. 店员管理功能已保留，仅移除了排班相关功能

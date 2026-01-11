package com.ljyh.foodieconnect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 店员排班实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("staff_schedules")
public class StaffSchedule extends BaseEntity {
    
    /**
     * 排班ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 店员ID
     */
    private Long staffId;
    
    /**
     * 餐厅ID
     */
    private Long restaurantId;
    
    /**
     * 排班日期
     */
    private LocalDate shiftDate;
    
    /**
     * 开始时间
     */
    private LocalTime startTime;
    
    /**
     * 结束时间
     */
    private LocalTime endTime;
    
    /**
     * 班次类型
     */
    private ShiftType shiftType;
    
    /**
     * 状态
     */
    private ScheduleStatus status;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 班次类型枚举
     */
    public enum ShiftType {
        MORNING("早班"),
        AFTERNOON("午班"),
        EVENING("晚班"),
        FULL_DAY("全天");
        
        private final String description;
        
        ShiftType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 排班状态枚举
     */
    public enum ScheduleStatus {
        SCHEDULED("已排班"),
        COMPLETED("已完成"),
        ABSENT("缺勤");
        
        private final String description;
        
        ScheduleStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
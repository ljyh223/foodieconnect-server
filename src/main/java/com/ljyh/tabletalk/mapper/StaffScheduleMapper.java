package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.StaffSchedule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 店员排班Mapper接口
 */
public interface StaffScheduleMapper extends BaseMapper<StaffSchedule> {
    
    /**
     * 根据餐厅ID查找排班列表
     */
    @Select("SELECT * FROM staff_schedules WHERE restaurant_id = #{restaurantId} ORDER BY shift_date ASC, start_time ASC")
    List<StaffSchedule> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据店员ID查找排班列表
     */
    @Select("SELECT * FROM staff_schedules WHERE staff_id = #{staffId} ORDER BY shift_date ASC, start_time ASC")
    List<StaffSchedule> findByStaffId(@Param("staffId") Long staffId);
    
    /**
     * 根据餐厅ID和日期范围查找排班
     */
    @Select("SELECT * FROM staff_schedules WHERE restaurant_id = #{restaurantId} AND shift_date BETWEEN #{startDate} AND #{endDate} ORDER BY shift_date ASC, start_time ASC")
    List<StaffSchedule> findByRestaurantIdAndDateRange(@Param("restaurantId") Long restaurantId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 根据店员ID和日期范围查找排班
     */
    @Select("SELECT * FROM staff_schedules WHERE staff_id = #{staffId} AND shift_date BETWEEN #{startDate} AND #{endDate} ORDER BY shift_date ASC, start_time ASC")
    List<StaffSchedule> findByStaffIdAndDateRange(@Param("staffId") Long staffId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * 根据餐厅ID和日期查找排班
     */
    @Select("SELECT * FROM staff_schedules WHERE restaurant_id = #{restaurantId} AND shift_date = #{shiftDate} ORDER BY start_time ASC")
    List<StaffSchedule> findByRestaurantIdAndDate(@Param("restaurantId") Long restaurantId,
                                              @Param("shiftDate") LocalDate shiftDate);
    
    /**
     * 根据店员ID和日期查找排班
     */
    @Select("SELECT * FROM staff_schedules WHERE staff_id = #{staffId} AND shift_date = #{shiftDate}")
    StaffSchedule findByStaffIdAndDate(@Param("staffId") Long staffId,
                                     @Param("shiftDate") LocalDate shiftDate);
    
    /**
     * 检查排班时间冲突
     */
    @Select("SELECT COUNT(*) FROM staff_schedules " +
            "WHERE staff_id = #{staffId} " +
            "  AND shift_date = #{shiftDate} " +
            "  AND ((start_time <= #{endTime} AND end_time >= #{startTime})) " +
            "  AND id != #{excludeId}")
    Integer checkTimeConflict(@Param("staffId") Long staffId,
                           @Param("shiftDate") LocalDate shiftDate,
                           @Param("startTime") java.time.LocalTime startTime,
                           @Param("endTime") java.time.LocalTime endTime,
                           @Param("excludeId") Long excludeId);
    
    /**
     * 获取指定日期的排班店员数量
     */
    @Select("SELECT COUNT(DISTINCT staff_id) FROM staff_schedules WHERE restaurant_id = #{restaurantId} AND shift_date = #{shiftDate}")
    Integer getStaffCountByDate(@Param("restaurantId") Long restaurantId,
                               @Param("shiftDate") LocalDate shiftDate);
}
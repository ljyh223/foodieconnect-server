package com.ljyh.tabletalk.dto;

import com.ljyh.tabletalk.entity.StaffSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 店员排班请求DTO
 */
@Data
@Schema(description = "店员排班请求")
public class StaffScheduleRequest {
    
    @NotNull(message = "店员ID不能为空")
    @Schema(description = "店员ID", example = "1")
    private Long staffId;
    
    @NotNull(message = "排班日期不能为空")
    @Schema(description = "排班日期", example = "2025-11-25")
    private LocalDate shiftDate;
    
    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "09:00")
    private LocalTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "18:00")
    private LocalTime endTime;
    
    @NotNull(message = "班次类型不能为空")
    @Schema(description = "班次类型", allowableValues = {"MORNING", "AFTERNOON", "EVENING", "FULL_DAY"})
    private StaffSchedule.ShiftType shiftType;
    
    @Schema(description = "备注", example = "正常排班")
    private String notes;
}
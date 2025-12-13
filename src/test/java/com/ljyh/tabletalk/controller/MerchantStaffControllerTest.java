package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ljyh.tabletalk.dto.StaffScheduleRequest;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.Staff;
import com.ljyh.tabletalk.entity.StaffSchedule;
import com.ljyh.tabletalk.enums.StaffStatus;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.StaffScheduleService;
import com.ljyh.tabletalk.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantStaffControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private StaffService staffService;
    
    @Mock
    private StaffScheduleService staffScheduleService;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantStaffController merchantStaffController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantStaffController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // 模拟当前商家信息
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(1L);
        merchant.setRole(Merchant.MerchantRole.MANAGER);
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
    }
    
    // ==================== 店员管理测试 ====================
    
    @Test
    void testGetStaffSuccess() throws Exception {
        // 准备测试数据
        List<Staff> staffList = new ArrayList<>();
        Staff staff1 = new Staff();
        staff1.setId(1L);
        staff1.setName("店员1");
        staff1.setRestaurantId(1L);
        staff1.setStatus(StaffStatus.ONLINE);
        
        Staff staff2 = new Staff();
        staff2.setId(2L);
        staff2.setName("店员2");
        staff2.setRestaurantId(1L);
        staff2.setStatus(StaffStatus.OFFLINE);
        
        staffList.add(staff1);
        staffList.add(staff2);
        
        // 模拟服务调用
        when(staffService.getStaffByRestaurantId(anyLong())).thenReturn(staffList);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("店员1"))
                .andExpect(jsonPath("$.data[1].name").value("店员2"));
    }
    
    @Test
    void testGetStaffByIdSuccess() throws Exception {
        // 准备测试数据
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("店员1");
        staff.setRestaurantId(1L);
        staff.setStatus(StaffStatus.ONLINE);
        
        // 模拟服务调用
        when(staffService.getStaffById(anyLong())).thenReturn(staff);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("店员1"))
                .andExpect(jsonPath("$.data.status").value("ONLINE"));
    }
    
    @Test
    void testGetOnlineStaffSuccess() throws Exception {
        // 准备测试数据
        List<Staff> onlineStaff = new ArrayList<>();
        Staff staff1 = new Staff();
        staff1.setId(1L);
        staff1.setName("店员1");
        staff1.setRestaurantId(1L);
        staff1.setStatus(StaffStatus.ONLINE);
        
        onlineStaff.add(staff1);
        
        // 模拟服务调用
        when(staffService.getOnlineStaffByRestaurantId(anyLong())).thenReturn(onlineStaff);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("ONLINE"));
    }
    
    @Test
    void testUpdateStaffStatusSuccess() throws Exception {
        // 准备测试数据
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("店员1");
        staff.setRestaurantId(1L);
        staff.setStatus(StaffStatus.ONLINE);
        
        // 模拟服务调用
        when(staffService.getStaffById(anyLong())).thenReturn(staff);
        
        // 执行测试
        mockMvc.perform(put("/merchant/staff/1/status")
                .param("status", "OFFLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    // ==================== 排班管理测试 ====================
    
    @Test
    void testGetSchedulesSuccess() throws Exception {
        // 准备测试数据
        List<StaffSchedule> schedules = new ArrayList<>();
        StaffSchedule schedule = new StaffSchedule();
        schedule.setId(1L);
        schedule.setStaffId(1L);
        schedule.setRestaurantId(1L);
        schedule.setShiftDate(LocalDate.now());
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(18, 0));
        schedule.setStatus(StaffSchedule.ScheduleStatus.SCHEDULED);
        
        schedules.add(schedule);
        
        // 模拟服务调用
        when(staffScheduleService.getSchedulesByRestaurant(anyLong())).thenReturn(schedules);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("SCHEDULED"));
    }
    
    @Test
    void testGetStaffSchedulesSuccess() throws Exception {
        // 准备测试数据
        List<StaffSchedule> schedules = new ArrayList<>();
        StaffSchedule schedule = new StaffSchedule();
        schedule.setId(1L);
        schedule.setStaffId(1L);
        schedule.setRestaurantId(1L);
        schedule.setShiftDate(LocalDate.now());
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(18, 0));
        
        schedules.add(schedule);
        
        // 模拟服务调用
        when(staffScheduleService.getSchedulesByStaff(anyLong(), anyLong())).thenReturn(schedules);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/1/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].staffId").value(1L));
    }
    
    @Test
    void testCreateScheduleSuccess() throws Exception {
        // 准备测试数据
        StaffScheduleRequest request = new StaffScheduleRequest();
        request.setStaffId(1L);
        request.setShiftDate(LocalDate.now());
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(18, 0));
        request.setShiftType(StaffSchedule.ShiftType.FULL_DAY);
        
        StaffSchedule schedule = new StaffSchedule();
        schedule.setId(1L);
        schedule.setStaffId(1L);
        schedule.setRestaurantId(1L);
        schedule.setShiftDate(LocalDate.now());
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(18, 0));
        schedule.setStatus(StaffSchedule.ScheduleStatus.SCHEDULED);
        
        // 模拟服务调用
        when(staffScheduleService.createSchedule(anyLong(), any(StaffScheduleRequest.class))).thenReturn(schedule);
        
        // 执行测试
        mockMvc.perform(post("/merchant/staff/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.staffId").value(1L))
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
    }
    
    @Test
    void testUpdateScheduleSuccess() throws Exception {
        // 准备测试数据
        StaffScheduleRequest request = new StaffScheduleRequest();
        request.setStaffId(1L);
        request.setShiftDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(19, 0));
        request.setShiftType(StaffSchedule.ShiftType.AFTERNOON);
        
        StaffSchedule schedule = new StaffSchedule();
        schedule.setId(1L);
        schedule.setStaffId(1L);
        schedule.setRestaurantId(1L);
        schedule.setShiftDate(LocalDate.now());
        schedule.setStartTime(LocalTime.of(10, 0));
        schedule.setEndTime(LocalTime.of(19, 0));
        schedule.setStatus(StaffSchedule.ScheduleStatus.SCHEDULED);
        
        // 模拟服务调用
        when(staffScheduleService.updateSchedule(anyLong(), any(StaffScheduleRequest.class))).thenReturn(schedule);
        
        // 执行测试
        mockMvc.perform(put("/merchant/staff/schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.staffId").value(1L))
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
    }
    
    @Test
    void testDeleteScheduleSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/merchant/staff/schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetStaffCountByDateSuccess() throws Exception {
        // 准备测试数据
        LocalDate date = LocalDate.now();
        int count = 5;
        
        // 模拟服务调用
        when(staffScheduleService.getStaffCountByDate(anyLong(), any(LocalDate.class))).thenReturn(count);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/schedules/count")
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));
    }
}

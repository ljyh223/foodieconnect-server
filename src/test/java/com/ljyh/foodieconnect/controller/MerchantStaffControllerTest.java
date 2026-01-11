package com.ljyh.foodieconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.entity.Staff;
import com.ljyh.foodieconnect.enums.StaffStatus;
import com.ljyh.foodieconnect.service.MerchantAuthService;
import com.ljyh.foodieconnect.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
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
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantStaffController merchantStaffController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantStaffController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
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
                .andExpect(jsonPath("$.data.name").value("店员1"));
    }
    
    @Test
    void testGetStaffByStatusSuccess() throws Exception {
        // 准备测试数据
        List<Staff> staffList = new ArrayList<>();
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("店员1");
        staff.setStatus(StaffStatus.ONLINE);
        staff.setRestaurantId(1L);
        staffList.add(staff);
        
        Staff staff2 = new Staff();
        staff2.setId(2L);
        staff2.setName("店员2");
        staff2.setStatus(StaffStatus.ONLINE);
        staff2.setRestaurantId(2L); // 不同餐厅
        staffList.add(staff2);
        
        // 模拟服务调用
        when(staffService.getStaffByStatus(any(StaffStatus.class))).thenReturn(staffList);
        
        // 执行测试
        mockMvc.perform(get("/merchant/staff/status/ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1)) // 只会返回当前餐厅的1个店员
                .andExpect(jsonPath("$.data[0].status").value("ONLINE"));
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
    
    @Test
    void testUpdateStaffRatingSuccess() throws Exception {
        // 准备测试数据
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("店员1");
        staff.setRestaurantId(1L);
        staff.setRating(BigDecimal.valueOf(4.0));
        
        // 模拟服务调用
        when(staffService.getStaffById(anyLong())).thenReturn(staff);
        
        // 执行测试
        mockMvc.perform(put("/merchant/staff/1/rating")
                .param("rating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testCreateStaffSuccess() throws Exception {
        // 准备测试数据
        Staff request = new Staff();
        request.setName("新店员");
        request.setPosition("服务员");
        request.setStatus(StaffStatus.ONLINE);
        request.setExperience("1年");
        
        Staff createdStaff = new Staff();
        createdStaff.setId(1L);
        createdStaff.setName("新店员");
        createdStaff.setRestaurantId(1L);
        createdStaff.setPosition("服务员");
        createdStaff.setStatus(StaffStatus.ONLINE);
        createdStaff.setExperience("1年");
        
        // 模拟服务调用
        when(staffService.createStaff(any(Staff.class))).thenReturn(createdStaff);
        
        // 执行测试
        mockMvc.perform(post("/merchant/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("新店员"))
                .andExpect(jsonPath("$.data.position").value("服务员"))
                .andExpect(jsonPath("$.data.status").value("ONLINE"))
                .andExpect(jsonPath("$.data.experience").value("1年"));
    }
    
    @Test
    void testUpdateStaffSuccess() throws Exception {
        // 准备测试数据
        Staff existingStaff = new Staff();
        existingStaff.setId(1L);
        existingStaff.setName("旧店员");
        existingStaff.setRestaurantId(1L);
        existingStaff.setPosition("服务员");
        existingStaff.setStatus(StaffStatus.ONLINE);
        
        Staff updateRequest = new Staff();
        updateRequest.setName("更新后的店员");
        updateRequest.setPosition("领班");
        updateRequest.setExperience("2年");
        updateRequest.setRating(BigDecimal.valueOf(4.5));
        
        Staff updatedStaff = new Staff();
        updatedStaff.setId(1L);
        updatedStaff.setName("更新后的店员");
        updatedStaff.setRestaurantId(1L);
        updatedStaff.setPosition("领班");
        updatedStaff.setStatus(StaffStatus.ONLINE);
        updatedStaff.setExperience("2年");
        updatedStaff.setRating(BigDecimal.valueOf(4.5));
        
        // 模拟服务调用
        when(staffService.getStaffById(anyLong())).thenReturn(existingStaff);
        when(staffService.updateStaff(anyLong(), any(Staff.class))).thenReturn(updatedStaff);
        
        // 执行测试
        mockMvc.perform(put("/merchant/staff/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("更新后的店员"))
                .andExpect(jsonPath("$.data.position").value("领班"))
                .andExpect(jsonPath("$.data.experience").value("2年"))
                .andExpect(jsonPath("$.data.rating").value(4.5));
    }
    
    @Test
    void testDeleteStaffSuccess() throws Exception {
        // 准备测试数据
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("店员1");
        staff.setRestaurantId(1L);
        
        // 模拟服务调用
        when(staffService.getStaffById(anyLong())).thenReturn(staff);
        
        // 执行测试
        mockMvc.perform(delete("/merchant/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
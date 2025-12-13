package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.MenuCategoryRequest;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.MenuCategory;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.MenuCategoryService;
import com.ljyh.tabletalk.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MerchantMenuControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private MenuCategoryService menuCategoryService;
    
    @Mock
    private MenuItemService menuItemService;
    
    @Mock
    private MerchantAuthService merchantAuthService;
    
    @InjectMocks
    private MerchantMenuController merchantMenuController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(merchantMenuController).build();
        objectMapper = new ObjectMapper();
        
        // 模拟当前商家信息
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setRestaurantId(1L);
        when(merchantAuthService.getCurrentMerchant()).thenReturn(merchant);
    }
    
    @Test
    void testGetCategoriesSuccess() throws Exception {
        // 准备测试数据
        List<MenuCategory> categories = new ArrayList<>();
        MenuCategory category1 = new MenuCategory();
        category1.setId(1L);
        category1.setName("热菜");
        category1.setRestaurantId(1L);
        category1.setIsActive(true);
        
        MenuCategory category2 = new MenuCategory();
        category2.setId(2L);
        category2.setName("凉菜");
        category2.setRestaurantId(1L);
        category2.setIsActive(true);
        
        categories.add(category1);
        categories.add(category2);
        
        // 模拟服务调用
        when(menuCategoryService.getCategoriesByRestaurant(anyLong())).thenReturn(categories);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("热菜"))
                .andExpect(jsonPath("$.data[1].name").value("凉菜"));
    }
    
    @Test
    void testCreateCategorySuccess() throws Exception {
        // 准备测试数据
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("主食");
        request.setDescription("各种主食");
        request.setSortOrder(1);
        
        MenuCategory createdCategory = new MenuCategory();
        createdCategory.setId(1L);
        createdCategory.setName("主食");
        createdCategory.setDescription("各种主食");
        createdCategory.setRestaurantId(1L);
        createdCategory.setSortOrder(1);
        createdCategory.setIsActive(true);
        
        // 模拟服务调用
        when(menuCategoryService.createCategory(anyLong(), any(MenuCategoryRequest.class))).thenReturn(createdCategory);
        
        // 执行测试
        mockMvc.perform(post("/merchant/menu/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("主食"))
                .andExpect(jsonPath("$.data.description").value("各种主食"));
    }
    
    @Test
    void testUpdateCategorySuccess() throws Exception {
        // 准备测试数据
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("更新后的主食");
        request.setDescription("更新后的各种主食");
        request.setSortOrder(2);
        
        MenuCategory updatedCategory = new MenuCategory();
        updatedCategory.setId(1L);
        updatedCategory.setName("更新后的主食");
        updatedCategory.setDescription("更新后的各种主食");
        updatedCategory.setRestaurantId(1L);
        updatedCategory.setSortOrder(2);
        updatedCategory.setIsActive(true);
        
        // 模拟服务调用
        when(menuCategoryService.updateCategory(anyLong(), any(MenuCategoryRequest.class))).thenReturn(updatedCategory);
        
        // 执行测试
        mockMvc.perform(put("/merchant/menu/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("更新后的主食"))
                .andExpect(jsonPath("$.data.description").value("更新后的各种主食"));
    }
    
    @Test
    void testDeleteCategorySuccess() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/merchant/menu/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testToggleCategoryStatusSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(put("/merchant/menu/categories/1/status")
                .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

package com.ljyh.tabletalk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljyh.tabletalk.dto.MenuCategoryRequest;
import com.ljyh.tabletalk.dto.MenuItemRequest;
import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.entity.MenuCategory;
import com.ljyh.tabletalk.entity.MenuItem;
import com.ljyh.tabletalk.service.MerchantAuthService;
import com.ljyh.tabletalk.service.MenuCategoryService;
import com.ljyh.tabletalk.service.MenuItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    
    // ==================== 菜品管理测试 ====================
    
    @Test
    void testGetItemsSuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item1 = new MenuItem();
        item1.setId(1L);
        item1.setName("宫保鸡丁");
        item1.setPrice(BigDecimal.valueOf(48.00));
        item1.setIsAvailable(true);
        item1.setRestaurantId(1L);
        
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setName("鱼香肉丝");
        item2.setPrice(BigDecimal.valueOf(38.00));
        item2.setIsAvailable(true);
        item2.setRestaurantId(1L);
        
        items.add(item1);
        items.add(item2);
        
        // 模拟服务调用
        when(menuItemService.getMenuItemsByRestaurant(anyLong())).thenReturn(items);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[1].name").value("鱼香肉丝"));
    }
    
    @Test
    void testGetItemsByCategorySuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setName("宫保鸡丁");
        item.setPrice(BigDecimal.valueOf(48.00));
        item.setIsAvailable(true);
        item.setRestaurantId(1L);
        item.setCategoryId(1L);
        
        items.add(item);
        
        // 模拟服务调用
        when(menuItemService.getMenuItemsByCategory(anyLong(), anyLong())).thenReturn(items);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[0].categoryId").value(1L));
    }
    
    @Test
    void testGetRecommendedItemsSuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setName("推荐菜品");
        item.setPrice(BigDecimal.valueOf(58.00));
        item.setIsAvailable(true);
        item.setIsRecommended(true);
        item.setRestaurantId(1L);
        
        items.add(item);
        
        // 模拟服务调用
        when(menuItemService.getRecommendedMenuItems(anyLong())).thenReturn(items);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items/recommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].isRecommended").value(true));
    }
    
    @Test
    void testCreateItemSuccess() throws Exception {
        // 准备测试数据
        MenuItemRequest request = new MenuItemRequest();
        request.setCategoryId(1L);
        request.setName("新菜品");
        request.setDescription("这是一个新菜品");
        request.setPrice(BigDecimal.valueOf(68.00));
        request.setIsAvailable(true);
        request.setIsRecommended(true);
        request.setSortOrder(1);
        request.setPreparationTime(20);
        request.setCalories(400);
        
        MenuItem createdItem = new MenuItem();
        createdItem.setId(1L);
        createdItem.setCategoryId(1L);
        createdItem.setName("新菜品");
        createdItem.setDescription("这是一个新菜品");
        createdItem.setPrice(BigDecimal.valueOf(68.00));
        createdItem.setIsAvailable(true);
        createdItem.setIsRecommended(true);
        createdItem.setRestaurantId(1L);
        
        // 模拟服务调用
        when(menuItemService.createMenuItem(anyLong(), any(MenuItemRequest.class))).thenReturn(createdItem);
        
        // 执行测试
        mockMvc.perform(post("/merchant/menu/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("新菜品"))
                .andExpect(jsonPath("$.data.isRecommended").value(true));
    }
    
    @Test
    void testUpdateItemSuccess() throws Exception {
        // 准备测试数据
        MenuItemRequest request = new MenuItemRequest();
        request.setCategoryId(1L);
        request.setName("更新后的菜品");
        request.setDescription("更新后的菜品描述");
        request.setPrice(BigDecimal.valueOf(78.00));
        request.setIsAvailable(false);
        request.setIsRecommended(false);
        request.setSortOrder(2);
        
        MenuItem updatedItem = new MenuItem();
        updatedItem.setId(1L);
        updatedItem.setCategoryId(1L);
        updatedItem.setName("更新后的菜品");
        updatedItem.setDescription("更新后的菜品描述");
        updatedItem.setPrice(BigDecimal.valueOf(78.00));
        updatedItem.setIsAvailable(false);
        updatedItem.setIsRecommended(false);
        updatedItem.setRestaurantId(1L);
        
        // 模拟服务调用
        when(menuItemService.updateMenuItem(anyLong(), any(MenuItemRequest.class))).thenReturn(updatedItem);
        
        // 执行测试
        mockMvc.perform(put("/merchant/menu/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("更新后的菜品"))
                .andExpect(jsonPath("$.data.isAvailable").value(false))
                .andExpect(jsonPath("$.data.isRecommended").value(false));
    }
    
    @Test
    void testDeleteItemSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/merchant/menu/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testToggleItemStatusSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(put("/merchant/menu/items/1/status")
                .param("isAvailable", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testToggleItemRecommendedSuccess() throws Exception {
        // 执行测试
        mockMvc.perform(put("/merchant/menu/items/1/recommended")
                .param("isRecommended", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testGetAllCategoriesSuccess() throws Exception {
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
        category2.setIsActive(false);
        
        categories.add(category1);
        categories.add(category2);
        
        // 模拟服务调用
        when(menuCategoryService.getAllCategoriesByRestaurant(anyLong())).thenReturn(categories);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("热菜"))
                .andExpect(jsonPath("$.data[1].name").value("凉菜"))
                .andExpect(jsonPath("$.data[1].isActive").value(false));
    }
    
    @Test
    void testGetAllItemsSuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item1 = new MenuItem();
        item1.setId(1L);
        item1.setName("宫保鸡丁");
        item1.setPrice(BigDecimal.valueOf(48.00));
        item1.setIsAvailable(true);
        item1.setRestaurantId(1L);
        
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setName("鱼香肉丝");
        item2.setPrice(BigDecimal.valueOf(38.00));
        item2.setIsAvailable(false);
        item2.setRestaurantId(1L);
        
        items.add(item1);
        items.add(item2);
        
        // 模拟服务调用
        when(menuItemService.getAllMenuItemsByRestaurant(anyLong())).thenReturn(items);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[1].name").value("鱼香肉丝"))
                .andExpect(jsonPath("$.data[1].isAvailable").value(false));
    }
    
    @Test
    void testGetItemsPageSuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item1 = new MenuItem();
        item1.setId(1L);
        item1.setName("宫保鸡丁");
        item1.setPrice(BigDecimal.valueOf(48.00));
        item1.setIsAvailable(true);
        item1.setRestaurantId(1L);
        items.add(item1);
        
        // 创建Page对象
        Page<MenuItem> page = new Page<>(1, 10);
        page.setRecords(items);
        page.setTotal(1L);
        
        // 模拟服务调用
        when(menuItemService.getMenuItemsPage(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items/page")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10));
    }
    
    @Test
    void testSearchItemsSuccess() throws Exception {
        // 准备测试数据
        List<MenuItem> items = new ArrayList<>();
        MenuItem item1 = new MenuItem();
        item1.setId(1L);
        item1.setName("宫保鸡丁");
        item1.setPrice(BigDecimal.valueOf(48.00));
        item1.setIsAvailable(true);
        item1.setRestaurantId(1L);
        items.add(item1);
        
        // 模拟服务调用
        when(menuItemService.searchMenuItems(anyLong(), anyString())).thenReturn(items);
        
        // 执行测试
        mockMvc.perform(get("/merchant/menu/items/search")
                .param("keyword", "宫保"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"));
    }
}

package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.MenuItem;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.MenuItemMapper;
import com.ljyh.foodieconnect.entity.MenuCategory;
import com.ljyh.foodieconnect.service.MenuItemService;
import com.ljyh.foodieconnect.service.MenuCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MenuController单元测试
 */
class MenuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private MenuItemMapper menuItemMapper;

    @Mock
    private MenuCategoryService menuCategoryService;

    @InjectMocks
    private MenuController menuController;

    private MenuItem testMenuItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(menuController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();

        // 准备测试数据
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setRestaurantId(1L);
        testMenuItem.setCategoryId(1L);
        testMenuItem.setName("宫保鸡丁");
        testMenuItem.setDescription("经典川菜，麻辣鲜香");
        testMenuItem.setPrice(new BigDecimal("38.00"));
        testMenuItem.setOriginalPrice(new BigDecimal("45.00"));
        testMenuItem.setImageUrl("http://example.com/image.jpg");
        testMenuItem.setIsAvailable(true);
        testMenuItem.setIsRecommended(true);
        testMenuItem.setSortOrder(1);
        testMenuItem.setSpiceLevel(MenuItem.SpiceLevel.MEDIUM);
        testMenuItem.setPreparationTime(15);
        testMenuItem.setCalories(320);
        testMenuItem.setRating(new BigDecimal("4.5"));
        testMenuItem.setReviewCount(10);
    }

    @Test
    void testGetRestaurantMenuItemsSuccess() throws Exception {
        // 准备测试数据
        Page<MenuItem> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        page.setRecords(List.of(testMenuItem));

        // 模拟服务调用
        when(menuItemService.getMenuItemsByRestaurantForUser(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/restaurants/1/menu-items")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data.records[0].price").value(38.00))
                .andExpect(jsonPath("$.data.total").value(1));

        // 验证服务调用
        verify(menuItemService, times(1)).getMenuItemsByRestaurantForUser(1L, 0, 20);
    }

    @Test
    void testGetRestaurantMenuItemsWithCustomPageSize() throws Exception {
        // 准备测试数据
        Page<MenuItem> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(5);
        page.setRecords(List.of(testMenuItem));

        // 模拟服务调用
        when(menuItemService.getMenuItemsByRestaurantForUser(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/restaurants/2/menu-items")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10));

        // 验证服务调用
        verify(menuItemService, times(1)).getMenuItemsByRestaurantForUser(2L, 1, 10);
    }

    @Test
    void testGetMenuItemDetailSuccess() throws Exception {
        // 模拟服务调用
        when(menuItemService.getMenuItemById(anyLong())).thenReturn(testMenuItem);

        // 执行测试
        mockMvc.perform(get("/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data.description").value("经典川菜，麻辣鲜香"))
                .andExpect(jsonPath("$.data.price").value(38.00))
                .andExpect(jsonPath("$.data.isAvailable").value(true))
                .andExpect(jsonPath("$.data.isRecommended").value(true));

        // 验证服务调用
        verify(menuItemService, times(1)).getMenuItemById(1L);
    }

    @Test
    void testGetMenuItemDetailNotFound() throws Exception {
        // 模拟服务调用抛出异常
        when(menuItemService.getMenuItemById(anyLong()))
                .thenThrow(new BusinessException("MENU_ITEM_NOT_FOUND", "菜品不存在"));

        // 执行测试
        mockMvc.perform(get("/menu-items/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("MENU_ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("菜品不存在"));

        // 验证服务调用
        verify(menuItemService, times(1)).getMenuItemById(999L);
    }

    @Test
    void testSearchMenuItemsSuccess() throws Exception {
        // 准备测试数据
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setRestaurantId(1L);
        item2.setName("宫保虾球");
        item2.setDescription("宫保口味的虾球");
        item2.setPrice(new BigDecimal("48.00"));
        item2.setIsAvailable(true);

        List<MenuItem> menuItems = List.of(testMenuItem, item2);

        // 模拟服务调用
        when(menuItemService.searchMenuItemsGlobally(anyString())).thenReturn(menuItems);

        // 执行测试
        mockMvc.perform(get("/menu-items/search")
                .param("keyword", "宫保"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[1].name").value("宫保虾球"));

        // 验证服务调用
        verify(menuItemService, times(1)).searchMenuItemsGlobally("宫保");
    }

    @Test
    void testSearchMenuItemsEmptyResult() throws Exception {
        // 模拟服务调用
        when(menuItemService.searchMenuItemsGlobally(anyString())).thenReturn(List.of());

        // 执行测试
        mockMvc.perform(get("/menu-items/search")
                .param("keyword", "不存在的菜品"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 验证服务调用
        verify(menuItemService, times(1)).searchMenuItemsGlobally("不存在的菜品");
    }

    @Test
    void testGetRecommendedMenuItemsSuccess() throws Exception {
        // 准备测试数据
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setRestaurantId(1L);
        item2.setName("麻婆豆腐");
        item2.setIsRecommended(true);
        item2.setIsAvailable(true);

        List<MenuItem> recommendedItems = List.of(testMenuItem, item2);

        // 模拟服务调用
        when(menuItemService.getMenuItemMapper()).thenReturn(menuItemMapper);
        when(menuItemMapper.findRecommendedByRestaurantId(anyLong())).thenReturn(recommendedItems);

        // 执行测试
        mockMvc.perform(get("/restaurants/1/menu-items/recommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[1].name").value("麻婆豆腐"))
                .andExpect(jsonPath("$.data[0].isRecommended").value(true))
                .andExpect(jsonPath("$.data[1].isRecommended").value(true));

        // 验证服务调用
        verify(menuItemMapper, times(1)).findRecommendedByRestaurantId(1L);
    }

    @Test
    void testGetRecommendedMenuItemsEmptyResult() throws Exception {
        // 模拟服务调用
        when(menuItemService.getMenuItemMapper()).thenReturn(menuItemMapper);
        when(menuItemMapper.findRecommendedByRestaurantId(anyLong())).thenReturn(List.of());

        // 执行测试
        mockMvc.perform(get("/restaurants/99/menu-items/recommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 验证服务调用
        verify(menuItemMapper, times(1)).findRecommendedByRestaurantId(99L);
    }

    @Test
    void testGetMenuItemsByCategorySuccess() throws Exception {
        // 准备测试数据
        MenuItem item2 = new MenuItem();
        item2.setId(2L);
        item2.setRestaurantId(1L);
        item2.setCategoryId(1L);
        item2.setName("水煮鱼");
        item2.setIsAvailable(true);

        List<MenuItem> categoryItems = List.of(testMenuItem, item2);

        // 模拟服务调用
        when(menuItemService.getMenuItemMapper()).thenReturn(menuItemMapper);
        when(menuItemMapper.findByRestaurantIdAndCategoryId(anyLong(), anyLong())).thenReturn(categoryItems);

        // 执行测试
        mockMvc.perform(get("/restaurants/1/menu-items/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].categoryId").value(1))
                .andExpect(jsonPath("$.data[1].categoryId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.data[1].name").value("水煮鱼"));

        // 验证服务调用
        verify(menuItemMapper, times(1)).findByRestaurantIdAndCategoryId(1L, 1L);
    }

    @Test
    void testGetMenuItemsByCategoryNotFound() throws Exception {
        // 模拟服务调用
        when(menuItemService.getMenuItemMapper()).thenReturn(menuItemMapper);
        when(menuItemMapper.findByRestaurantIdAndCategoryId(anyLong(), anyLong())).thenReturn(List.of());

        // 执行测试
        mockMvc.perform(get("/restaurants/1/menu-items/category/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 验证服务调用
        verify(menuItemMapper, times(1)).findByRestaurantIdAndCategoryId(1L, 999L);
    }

    @Test
    void testGetMenuItemsByCategoryDifferentRestaurant() throws Exception {
        // 准备测试数据
        MenuItem item2 = new MenuItem();
        item2.setId(3L);
        item2.setRestaurantId(2L);
        item2.setCategoryId(2L);
        item2.setName("意式面食");
        item2.setIsAvailable(true);

        List<MenuItem> categoryItems = List.of(item2);

        // 模拟服务调用
        when(menuItemService.getMenuItemMapper()).thenReturn(menuItemMapper);
        when(menuItemMapper.findByRestaurantIdAndCategoryId(anyLong(), anyLong())).thenReturn(categoryItems);

        // 执行测试
        mockMvc.perform(get("/restaurants/2/menu-items/category/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].restaurantId").value(2))
                .andExpect(jsonPath("$.data[0].categoryId").value(2))
                .andExpect(jsonPath("$.data[0].name").value("意式面食"));

        // 验证服务调用
        verify(menuItemMapper, times(1)).findByRestaurantIdAndCategoryId(2L, 2L);
    }

    @Test
    void testSearchMenuItemsWithPartialMatch() throws Exception {
        // 准备测试数据 - 搜索"鸡"应该匹配到"宫保鸡丁"
        List<MenuItem> menuItems = List.of(testMenuItem);

        // 模拟服务调用
        when(menuItemService.searchMenuItemsGlobally(anyString())).thenReturn(menuItems);

        // 执行测试 - 搜索部分关键词
        mockMvc.perform(get("/menu-items/search")
                .param("keyword", "鸡"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("宫保鸡丁"));

        // 验证服务调用
        verify(menuItemService, times(1)).searchMenuItemsGlobally("鸡");
    }

    @Test
    void testGetRestaurantCategoriesSuccess() throws Exception {
        // 准备测试数据
        MenuCategory category1 = new MenuCategory();
        category1.setId(1L);
        category1.setRestaurantId(1L);
        category1.setName("热菜");
        category1.setDescription("各类热炒菜品");
        category1.setSortOrder(1);
        category1.setIsActive(true);

        MenuCategory category2 = new MenuCategory();
        category2.setId(2L);
        category2.setRestaurantId(1L);
        category2.setName("凉菜");
        category2.setDescription("各类凉拌菜品");
        category2.setSortOrder(2);
        category2.setIsActive(true);

        List<MenuCategory> categories = List.of(category1, category2);

        // 模拟服务调用
        when(menuCategoryService.getCategoriesByRestaurantForUser(anyLong())).thenReturn(categories);

        // 执行测试
        mockMvc.perform(get("/restaurants/1/menu-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("热菜"))
                .andExpect(jsonPath("$.data[0].description").value("各类热炒菜品"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("凉菜"))
                .andExpect(jsonPath("$.data[1].description").value("各类凉拌菜品"));

        // 验证服务调用
        verify(menuCategoryService, times(1)).getCategoriesByRestaurantForUser(1L);
    }

    @Test
    void testGetRestaurantCategoriesEmptyResult() throws Exception {
        // 模拟服务调用
        when(menuCategoryService.getCategoriesByRestaurantForUser(anyLong())).thenReturn(List.of());

        // 执行测试
        mockMvc.perform(get("/restaurants/99/menu-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 验证服务调用
        verify(menuCategoryService, times(1)).getCategoriesByRestaurantForUser(99L);
    }
}

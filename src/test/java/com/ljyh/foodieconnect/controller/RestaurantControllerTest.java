package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.entity.Restaurant;
import com.ljyh.foodieconnect.service.RestaurantService;
import com.ljyh.foodieconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RestaurantControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private RestaurantService restaurantService;
    
    @InjectMocks
    private RestaurantController restaurantController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
    }
    
    @Test
    void testGetAllRestaurantsSuccess() throws Exception {
        // 准备测试数据
        Page<Restaurant> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setAddress("测试地址");
        restaurant.setPhone("13800138000");
        restaurant.setIsOpen(true);
        
        page.setRecords(List.of(restaurant));
        
        // 模拟服务调用
        when(restaurantService.getRestaurants(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$").exists());
    }
    
    @Test
    void testGetRestaurantDetailSuccess() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setAddress("测试地址");
        
        // 创建一个HashMap来允许null值
        Map<String, Object> detailMap = new java.util.HashMap<>();
        detailMap.put("restaurant", restaurant);
        detailMap.put("recommendedDishes", new java.util.ArrayList<>());
        
        // 模拟服务调用
        when(restaurantService.getRestaurantDetail(anyLong())).thenReturn(detailMap);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restaurant.name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.restaurant.address").value("测试地址"));
    }
    
    @Test
    void testGetRestaurantsByTypeSuccess() throws Exception {
        // 准备测试数据
        Page<Restaurant> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        restaurant.setType("CHINESE");
        
        page.setRecords(List.of(restaurant));
        
        // 模拟服务调用
        when(restaurantService.getRestaurantsByType(anyString(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/type/CHINESE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.records[0].type").value("CHINESE"));
    }
    
    @Test
    void testSearchRestaurantsSuccess() throws Exception {
        // 准备测试数据
        Page<Restaurant> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        
        page.setRecords(List.of(restaurant));
        
        // 模拟服务调用
        when(restaurantService.searchRestaurants(anyString(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/search")
                .param("q", "测试")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetPopularRestaurantsSuccess() throws Exception {
        // 准备测试数据
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        
        // 模拟服务调用
        when(restaurantService.getPopularRestaurants(anyInt())).thenReturn(List.of(restaurant));
        
        // 执行测试
        mockMvc.perform(get("/restaurants/popular")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("测试餐厅"));
    }
    
    @Test
    void testGetRestaurantsByRatingRangeSuccess() throws Exception {
        // 准备测试数据
        Page<Restaurant> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        page.setTotal(1);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("测试餐厅");
        
        page.setRecords(List.of(restaurant));
        
        // 模拟服务调用
        when(restaurantService.getRestaurantsByRatingRange(anyDouble(), anyDouble(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/restaurants/rating")
                .param("minRating", "4.0")
                .param("maxRating", "5.0")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].name").value("测试餐厅"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetAllRestaurantTypesSuccess() throws Exception {
        // 准备测试数据
        // 模拟服务调用
        when(restaurantService.getAllRestaurantTypes()).thenReturn(List.of("CHINESE", "WESTERN", "JAPANESE"));
        
        // 执行测试
        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("CHINESE"));
    }
}
package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.FavoriteFoodRequest;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.entity.UserFavoriteFood;
import com.ljyh.foodieconnect.service.FavoriteFoodService;
import com.ljyh.foodieconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FavoriteFoodControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private FavoriteFoodService favoriteFoodService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private FavoriteFoodController favoriteFoodController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteFoodController)
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testAddFavoriteFoodSuccess() throws Exception {
        // 准备测试数据
        FavoriteFoodRequest favoriteFoodRequest = new FavoriteFoodRequest();
        favoriteFoodRequest.setFoodName("红烧肉");
        favoriteFoodRequest.setFoodType("MEAT");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setDisplayName("Test User");
        
        UserFavoriteFood favoriteFood = new UserFavoriteFood();
        favoriteFood.setId(1L);
        favoriteFood.setUserId(1L);
        favoriteFood.setFoodName("红烧肉");
        favoriteFood.setFoodType("MEAT");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(favoriteFoodService.addFavoriteFood(anyLong(), any(FavoriteFoodRequest.class))).thenReturn(favoriteFood);
        
        // 执行测试
        mockMvc.perform(post("/api/users/favorite-foods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteFoodRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.foodName").value("红烧肉"))
                .andExpect(jsonPath("$.data.foodType").value("MEAT"));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testDeleteFavoriteFoodSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(favoriteFoodService).deleteFavoriteFood(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(delete("/api/users/favorite-foods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUserFavoriteFoodsSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        Page<UserFavoriteFood> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFavoriteFood favoriteFood = new UserFavoriteFood();
        favoriteFood.setId(1L);
        favoriteFood.setUserId(1L);
        favoriteFood.setFoodName("红烧肉");
        favoriteFood.setFoodType("MEAT");
        
        page.setRecords(List.of(favoriteFood));
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(favoriteFoodService.getUserFavoriteFoodsPage(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/users/favorite-foods")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].foodName").value("红烧肉"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUserFavoriteFoodsByTypeSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        Page<UserFavoriteFood> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFavoriteFood favoriteFood = new UserFavoriteFood();
        favoriteFood.setId(1L);
        favoriteFood.setUserId(1L);
        favoriteFood.setFoodName("红烧肉");
        favoriteFood.setFoodType("MEAT");
        
        page.setRecords(List.of(favoriteFood));
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(favoriteFoodService.getUserFavoriteFoodsByTypePage(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/users/favorite-foods/type/MEAT")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].foodName").value("红烧肉"))
                .andExpect(jsonPath("$.data.records[0].foodType").value("MEAT"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
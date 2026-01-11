package com.ljyh.tabletalk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.entity.UserFollow;
import com.ljyh.tabletalk.service.FollowService;
import com.ljyh.tabletalk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FollowControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private FollowService followService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private FollowController followController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setControllerAdvice(new com.ljyh.tabletalk.exception.GlobalExceptionHandler())
                .build();
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testFollowUserSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(followService).followUser(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(post("/api/follows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testUnfollowUserSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(followService).unfollowUser(anyLong(), anyLong());
        
        // 执行测试
        mockMvc.perform(delete("/api/follows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetFollowingListSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        Page<UserFollow> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(1L);
        follow.setFollowingId(2L);
        
        page.setRecords(List.of(follow));
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(followService.getFollowingList(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/follows/following")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].followingId").value(2))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetFollowersListSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        Page<UserFollow> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(2L);
        follow.setFollowingId(1L);
        
        page.setRecords(List.of(follow));
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(followService.getFollowersList(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/follows/followers")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].followerId").value(2))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetUserFollowingListSuccess() throws Exception {
        // 准备测试数据
        Page<UserFollow> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(2L);
        follow.setFollowingId(3L);
        
        page.setRecords(List.of(follow));
        
        // 模拟服务调用
        when(followService.getFollowingList(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/follows/users/2/following")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].followingId").value(3))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetUserFollowersListSuccess() throws Exception {
        // 准备测试数据
        Page<UserFollow> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        
        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(3L);
        follow.setFollowingId(2L);
        
        page.setRecords(List.of(follow));
        
        // 模拟服务调用
        when(followService.getFollowersList(anyLong(), anyInt(), anyInt())).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/follows/users/2/followers")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].followerId").value(3))
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testCheckFollowingSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(followService.isFollowing(anyLong(), anyLong())).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(get("/api/follows/check/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void testGetMutualFollowingSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        
        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(1L);
        follow.setFollowingId(3L);
        
        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(followService.getMutualFollowing(anyLong(), anyLong())).thenReturn(List.of(follow));
        
        // 执行测试
        mockMvc.perform(get("/api/follows/mutual/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].followingId").value(3));
    }
}
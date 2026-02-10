package com.ljyh.foodieconnect.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.dto.FollowWithUserDTO;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.entity.UserFollow;
import com.ljyh.foodieconnect.service.FollowService;
import com.ljyh.foodieconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 自定义RequestPostProcessor用于设置Principal
 */
class MockPrincipalRequestPostProcessor implements RequestPostProcessor {
    private final Principal principal;

    public MockPrincipalRequestPostProcessor(Principal principal) {
        this.principal = principal;
    }

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setUserPrincipal(principal);
        return request;
    }
}

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
                .setControllerAdvice(new com.ljyh.foodieconnect.exception.GlobalExceptionHandler())
                .build();
    }

    // 静态辅助方法，用于创建 mock Principal
    private static RequestPostProcessor mockPrincipal(String username) {
        return new MockPrincipalRequestPostProcessor(() -> username);
    }

    @Test
    void testFollowUserSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(followService).followUser(anyLong(), anyLong());

        // 执行测试
        mockMvc.perform(post("/api/follows/2")
                .with(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUnfollowUserSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        doNothing().when(followService).unfollowUser(anyLong(), anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/follows/2")
                .with(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
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
                .with(mockPrincipal("test@example.com"))
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].followingId").value(2))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
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
                .with(mockPrincipal("test@example.com"))
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
    void testCheckFollowingSuccess() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(followService.isFollowing(anyLong(), anyLong())).thenReturn(true);

        // 执行测试
        mockMvc.perform(get("/api/follows/check/2")
                .with(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
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
        mockMvc.perform(get("/api/follows/mutual/2")
                .with(mockPrincipal("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].followingId").value(3));
    }

    // ==================== 新增API测试 ====================

    @Test
    void testGetFollowingListWithUsersSuccess() throws Exception {
        // 准备测试数据
        UserDTO currentUserDTO = new UserDTO();
        currentUserDTO.setId(1L);
        currentUserDTO.setEmail("test@example.com");

        // 被关注用户信息
        UserDTO followedUserDTO = new UserDTO();
        followedUserDTO.setId(2L);
        followedUserDTO.setEmail("followed@example.com");
        followedUserDTO.setDisplayName("被关注用户");
        followedUserDTO.setAvatarUrl("/uploads/avatar2.jpg");
        followedUserDTO.setBio("这是被关注用户的简介");

        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(1L);
        follow.setFollowingId(2L);

        FollowWithUserDTO followWithUser = new FollowWithUserDTO();
        followWithUser.setFollow(follow);
        followWithUser.setUser(followedUserDTO);

        Page<FollowWithUserDTO> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        page.setRecords(List.of(followWithUser));

        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(currentUserDTO);
        when(followService.getFollowingListWithUsers(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/follows/following-with-users")
                .with(mockPrincipal("test@example.com"))
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].follow.followingId").value(2))
                .andExpect(jsonPath("$.data.records[0].user.id").value(2))
                .andExpect(jsonPath("$.data.records[0].user.displayName").value("被关注用户"))
                .andExpect(jsonPath("$.data.records[0].user.avatarUrl").value("/uploads/avatar2.jpg"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetFollowersListWithUsersSuccess() throws Exception {
        // 准备测试数据
        UserDTO currentUserDTO = new UserDTO();
        currentUserDTO.setId(1L);
        currentUserDTO.setEmail("test@example.com");

        // 粉丝用户信息
        UserDTO followerUserDTO = new UserDTO();
        followerUserDTO.setId(2L);
        followerUserDTO.setEmail("follower@example.com");
        followerUserDTO.setDisplayName("粉丝用户");
        followerUserDTO.setAvatarUrl("/uploads/avatar2.jpg");

        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(2L);
        follow.setFollowingId(1L);

        FollowWithUserDTO followWithUser = new FollowWithUserDTO();
        followWithUser.setFollow(follow);
        followWithUser.setUser(followerUserDTO);

        Page<FollowWithUserDTO> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        page.setRecords(List.of(followWithUser));

        // 模拟服务调用
        when(userService.getUserByEmail("test@example.com")).thenReturn(currentUserDTO);
        when(followService.getFollowersListWithUsers(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/follows/followers-with-users")
                .with(mockPrincipal("test@example.com"))
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].follow.followerId").value(2))
                .andExpect(jsonPath("$.data.records[0].user.id").value(2))
                .andExpect(jsonPath("$.data.records[0].user.displayName").value("粉丝用户"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetUserFollowingListWithUsersSuccess() throws Exception {
        // 准备测试数据
        UserDTO followedUserDTO = new UserDTO();
        followedUserDTO.setId(3L);
        followedUserDTO.setEmail("followed@example.com");
        followedUserDTO.setDisplayName("被关注用户");

        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(2L);
        follow.setFollowingId(3L);

        FollowWithUserDTO followWithUser = new FollowWithUserDTO();
        followWithUser.setFollow(follow);
        followWithUser.setUser(followedUserDTO);

        Page<FollowWithUserDTO> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        page.setRecords(List.of(followWithUser));

        // 模拟服务调用
        when(followService.getFollowingListWithUsers(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/follows/users/2/following-with-users")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].follow.followingId").value(3))
                .andExpect(jsonPath("$.data.records[0].user.displayName").value("被关注用户"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetUserFollowersListWithUsersSuccess() throws Exception {
        // 准备测试数据
        UserDTO followerUserDTO = new UserDTO();
        followerUserDTO.setId(3L);
        followerUserDTO.setEmail("follower@example.com");
        followerUserDTO.setDisplayName("粉丝用户");

        UserFollow follow = new UserFollow();
        follow.setId(1L);
        follow.setFollowerId(3L);
        follow.setFollowingId(2L);

        FollowWithUserDTO followWithUser = new FollowWithUserDTO();
        followWithUser.setFollow(follow);
        followWithUser.setUser(followerUserDTO);

        Page<FollowWithUserDTO> page = new Page<>();
        page.setCurrent(0);
        page.setSize(20);
        page.setTotal(1);
        page.setRecords(List.of(followWithUser));

        // 模拟服务调用
        when(followService.getFollowersListWithUsers(anyLong(), anyInt(), anyInt())).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/follows/users/2/followers-with-users")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records[0].follow.followerId").value(3))
                .andExpect(jsonPath("$.data.records[0].user.displayName").value("粉丝用户"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.dto.FollowWithUserDTO;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.entity.UserFollow;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.UserFollowMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户关注服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService extends ServiceImpl<UserFollowMapper, UserFollow> {
    
    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;
    
    /**
     * 关注用户
     */
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        // 检查是否尝试关注自己
        if (followerId.equals(followingId)) {
            throw new BusinessException("CANNOT_FOLLOW_SELF", "不能关注自己");
        }
        
        // 检查被关注用户是否存在
        User followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            throw new BusinessException("USER_NOT_FOUND", "被关注用户不存在");
        }
        
        // 检查是否已关注
        if (userFollowMapper.isFollowing(followerId, followingId)) {
            throw new BusinessException("ALREADY_FOLLOWING", "已经关注了该用户");
        }
        
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        
        userFollowMapper.insert(follow);
        log.info("用户 {} 关注了用户 {}", followerId, followingId);
    }
    
    /**
     * 取消关注
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        int result = userFollowMapper.unfollow(followerId, followingId);
        if (result == 0) {
            throw new BusinessException("NOT_FOLLOWING", "未关注该用户");
        }
        
        log.info("用户 {} 取消关注用户 {}", followerId, followingId);
    }
    
    /**
     * 检查是否已关注
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowMapper.isFollowing(followerId, followingId);
    }
    
    /**
     * 获取用户关注列表
     */
    public Page<UserFollow> getFollowingList(Long userId, int page, int size) {
        Page<UserFollow> pageParam = new Page<>(page, size);
        return userFollowMapper.findFollowingPage(pageParam, userId);
    }
    
    /**
     * 获取用户粉丝列表
     */
    public Page<UserFollow> getFollowersList(Long userId, int page, int size) {
        Page<UserFollow> pageParam = new Page<>(page, size);
        return userFollowMapper.findFollowersPage(pageParam, userId);
    }
    
    /**
     * 获取共同关注列表
     */
    public List<UserFollow> getMutualFollowing(Long userId1, Long userId2) {
        return userFollowMapper.findMutualFollowing(userId1, userId2);
    }

    // ========== 优化后的方法：一次性返回关注关系和用户信息 ==========

    /**
     * 获取用户关注列表（包含用户信息）
     */
    public Page<FollowWithUserDTO> getFollowingListWithUsers(Long userId, int page, int size) {
        // 1. 获取关注列表
        Page<UserFollow> followPage = getFollowingList(userId, page, size);

        // 2. 提取所有被关注用户ID
        List<Long> followingIds = followPage.getRecords().stream()
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toList());

        // 3. 批量查询用户信息
        Map<Long, UserDTO> userMap = getUsersByIds(followingIds);

        // 4. 组装结果
        Page<FollowWithUserDTO> resultPage = new Page<>(
                followPage.getCurrent(),
                followPage.getSize(),
                followPage.getTotal()
        );

        List<FollowWithUserDTO> records = followPage.getRecords().stream()
                .map(follow -> {
                    FollowWithUserDTO dto = new FollowWithUserDTO();
                    dto.setFollow(follow);
                    dto.setUser(userMap.get(follow.getFollowingId()));
                    return dto;
                })
                .collect(Collectors.toList());

        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 获取用户粉丝列表（包含用户信息）
     */
    public Page<FollowWithUserDTO> getFollowersListWithUsers(Long userId, int page, int size) {
        // 1. 获取粉丝列表
        Page<UserFollow> followPage = getFollowersList(userId, page, size);

        // 2. 提取所有粉丝ID
        List<Long> followerIds = followPage.getRecords().stream()
                .map(UserFollow::getFollowerId)
                .collect(Collectors.toList());

        // 3. 批量查询用户信息
        Map<Long, UserDTO> userMap = getUsersByIds(followerIds);

        // 4. 组装结果
        Page<FollowWithUserDTO> resultPage = new Page<>(
                followPage.getCurrent(),
                followPage.getSize(),
                followPage.getTotal()
        );

        List<FollowWithUserDTO> records = followPage.getRecords().stream()
                .map(follow -> {
                    FollowWithUserDTO dto = new FollowWithUserDTO();
                    dto.setFollow(follow);
                    dto.setUser(userMap.get(follow.getFollowerId()));
                    return dto;
                })
                .collect(Collectors.toList());

        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 批量获取用户信息（辅助方法）
     */
    private Map<Long, UserDTO> getUsersByIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        this::convertToUserDTO
                ));
    }

    /**
     * 转换User到UserDTO（辅助方法）
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.entity.UserFollow;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.UserFollowMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
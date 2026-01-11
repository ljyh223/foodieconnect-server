package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.dto.LoginRequest;
import com.ljyh.foodieconnect.dto.RegisterRequest;
import com.ljyh.foodieconnect.dto.UserDTO;
import com.ljyh.foodieconnect.dto.UserProfileResponse;
import com.ljyh.foodieconnect.entity.User;
import com.ljyh.foodieconnect.entity.UserFavoriteFood;
import com.ljyh.foodieconnect.enums.UserStatus;
import com.ljyh.foodieconnect.exception.BusinessException;
import com.ljyh.foodieconnect.mapper.UserFavoriteFoodMapper;
import com.ljyh.foodieconnect.mapper.UserFollowMapper;
import com.ljyh.foodieconnect.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserFavoriteFoodMapper userFavoriteFoodMapper;
    private final UserFollowMapper userFollowMapper;
    
    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 检查邮箱是否已存在
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException("USER_EMAIL_EXISTS", "邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setStatus(UserStatus.ACTIVE);
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getEmail());
        
        return convertToDTO(user);
    }
    
    /**
     * 用户登录
     */
    public User login(LoginRequest request) {
        // 根据邮箱查询用户
        Optional<User> userOptional = userMapper.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        User user = userOptional.get();
        
        // 检查用户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("USER_INACTIVE", "用户账户未激活");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "密码错误");
        }
        
        log.info("用户登录成功: {}", user.getEmail());
        return user;
    }
    
    /**
     * 根据ID获取用户
     */
    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return convertToDTO(user);
    }
    
    /**
     * 根据邮箱获取用户
     */
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userMapper.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return convertToDTO(userOptional.get());
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 更新用户信息
        if (userDTO.getDisplayName() != null) {
            user.setDisplayName(userDTO.getDisplayName());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        
        // 不允许用户通过此接口修改自己的状态
        // 状态字段只能由管理员修改
        
        userMapper.updateById(user);
        log.info("用户信息更新成功: {}", user.getEmail());
        
        return convertToDTO(user);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("INVALID_PASSWORD", "旧密码错误");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        log.info("用户密码修改成功: {}", user.getEmail());
    }
    
    /**
     * 获取用户详细信息（包含喜好食物、关注数量等）
     */
    public UserProfileResponse getUserProfile(Long userId, Long currentUserId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // 获取喜好食物
        List<UserFavoriteFood> favoriteFoods = userFavoriteFoodMapper.findByUserId(userId);
        List<UserProfileResponse.FavoriteFoodDTO> favoriteFoodDTOs = favoriteFoods.stream()
                .map(this::convertToFavoriteFoodDTO)
                .collect(java.util.stream.Collectors.toList());
        response.setFavoriteFoods(favoriteFoodDTOs);
        
        // 获取关注数量
        response.setFollowingCount(userMapper.getFollowingCount(userId));
        response.setFollowersCount(userMapper.getFollowersCount(userId));
        response.setRecommendationsCount(userMapper.getRecommendationsCount(userId));
        
        // 检查当前用户是否已关注该用户
        if (currentUserId != null && !currentUserId.equals(userId)) {
            response.setIsFollowing(userFollowMapper.isFollowing(currentUserId, userId));
        } else {
            response.setIsFollowing(false);
        }
        
        return response;
    }
    
    /**
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setBio(user.getBio());
        userDTO.setStatus(user.getStatus());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }
    
    /**
     * 将UserFavoriteFood实体转换为FavoriteFoodDTO
     */
    private UserProfileResponse.FavoriteFoodDTO convertToFavoriteFoodDTO(UserFavoriteFood favoriteFood) {
        UserProfileResponse.FavoriteFoodDTO dto = new UserProfileResponse.FavoriteFoodDTO();
        dto.setId(favoriteFood.getId());
        dto.setFoodName(favoriteFood.getFoodName());
        dto.setFoodType(favoriteFood.getFoodType());
        return dto;
    }
}
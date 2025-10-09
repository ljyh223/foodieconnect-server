package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.dto.LoginRequest;
import com.ljyh.tabletalk.dto.RegisterRequest;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.enums.UserStatus;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 检查邮箱是否已存在
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException("USER_EMAIL_EXISTS", "邮箱已存在");
        }
        
        // 检查手机号是否已存在
        if (request.getPhone() != null && userMapper.existsByPhone(request.getPhone())) {
            throw new BusinessException("USER_PHONE_EXISTS", "手机号已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setPhone(request.getPhone());
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
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        
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
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setStatus(user.getStatus());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }
}
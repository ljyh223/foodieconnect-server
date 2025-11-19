package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.dto.LoginRequest;
import com.ljyh.tabletalk.dto.LoginResponse;
import com.ljyh.tabletalk.dto.RegisterRequest;
import com.ljyh.tabletalk.dto.UserDTO;
import com.ljyh.tabletalk.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    
    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        return userService.register(request);
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 验证用户凭据
        User user = userService.login(request);
        
        // 生成JWT令牌
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(convertToDTO(user));
        response.setExpiresIn(jwtService.getExpirationTime() / 1000);
        
        log.info("用户登录成功，生成JWT令牌: {}", user.getEmail());
        return response;
    }
    
    /**
     * 刷新令牌
     */
    public LoginResponse refreshToken(String refreshToken) {
        // 验证刷新令牌
        String email = jwtService.extractUsername(refreshToken);
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }
        
        // 获取用户信息
        UserDTO userDTO = userService.getUserByEmail(email);
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setDisplayName(userDTO.getDisplayName());
        
        // 生成新的访问令牌
        String newToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setRefreshToken(newRefreshToken);
        response.setUser(userDTO);
        response.setExpiresIn(jwtService.getExpirationTime() / 1000);
        
        log.info("令牌刷新成功: {}", user.getEmail());
        return response;
    }
    
    /**
     * 获取当前用户信息
     */
    public UserDTO getCurrentUser(String token) {
        String email = jwtService.extractUsername(token);
        return userService.getUserByEmail(email);
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
}
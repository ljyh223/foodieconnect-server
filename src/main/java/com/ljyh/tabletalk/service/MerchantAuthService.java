package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.entity.Merchant;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.MerchantMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商家认证服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantAuthService {
    
    @Qualifier("merchantUserDetailsServiceImpl")
    private final MerchantUserDetailsServiceImpl merchantUserDetailsService;
    private final JwtMerchantService jwtMerchantService;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 商家登录
     */
    public MerchantLoginResult login(String username, String password) {
        log.info("商家登录尝试: {}", username);
        
        try {
            // 首先验证商家是否存在且状态正常
            Merchant merchant = merchantMapper.findByUsername(username);
            if (merchant == null) {
                throw new BusinessException("MERCHANT_NOT_FOUND", "商家不存在");
            }
            
            if (merchant.getStatus() != Merchant.MerchantStatus.ACTIVE) {
                throw new BusinessException("MERCHANT_DISABLED", "商家账户已被禁用");
            }
            
            // 直接验证密码，避免使用认证管理器导致的循环依赖
            if (!passwordEncoder.matches(password, merchant.getPasswordHash())) {
                throw new BusinessException("LOGIN_FAILED", "用户名或密码错误");
            }
            
            // 手动创建认证信息
            UserDetails userDetails = merchantUserDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 更新最后登录时间
            merchant.setLastLoginAt(LocalDateTime.now());
            merchantMapper.updateById(merchant);
            
            String token = jwtMerchantService.generateToken(merchant);
            log.info("商家登录成功: {}", username);
            
            return new MerchantLoginResult(token, merchant);
        } catch (BusinessException e) {
            // 如果是业务异常，直接抛出
            throw e;
        } catch (Exception e) {
            log.error("商家登录失败: {}", username, e);
            throw new BusinessException("LOGIN_FAILED", "用户名或密码错误");
        }
    }
    
    /**
     * 商家注册
     */
    public Merchant register(String username, String email, String password, String name, 
                          String phone, Long restaurantId, Merchant.MerchantRole role) {
        log.info("商家注册尝试: {}", username);
        
        // 检查用户名是否已存在
        if (merchantMapper.findByUsername(username) != null) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (merchantMapper.findByEmail(email) != null) {
            throw new BusinessException("EMAIL_EXISTS", "邮箱已存在");
        }
        
        // 创建商家账户
        Merchant merchant = new Merchant();
        merchant.setUsername(username);
        merchant.setEmail(email);
        merchant.setPasswordHash(passwordEncoder.encode(password));
        merchant.setName(name);
        merchant.setPhone(phone);
        merchant.setRestaurantId(restaurantId);
        merchant.setRole(role);
        merchant.setStatus(Merchant.MerchantStatus.ACTIVE);
        
        merchantMapper.insert(merchant);
        log.info("商家注册成功: {}", username);
        
        return merchant;
    }
    
    /**
     * 修改密码
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("商家修改密码: {}", username);
        
        Merchant merchant = merchantMapper.findByUsername(username);
        if (merchant == null) {
            throw new BusinessException("MERCHANT_NOT_FOUND", "商家不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, merchant.getPasswordHash())) {
            throw new BusinessException("INVALID_OLD_PASSWORD", "旧密码错误");
        }
        
        // 更新密码
        merchant.setPasswordHash(passwordEncoder.encode(newPassword));
        merchantMapper.updateById(merchant);
        
        log.info("商家密码修改成功: {}", username);
    }
    
    /**
     * 获取当前登录商家信息
     */
    public Merchant getCurrentMerchant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("用户未认证或认证信息无效");
            throw new BusinessException("NOT_AUTHENTICATED", "未登录");
        }
        
        String username = authentication.getName();
        log.debug("正在获取商家信息，用户名: {}", username);
        
        Merchant merchant = merchantMapper.findByUsername(username);
        if (merchant == null) {
            log.warn("商家不存在，用户名: {}", username);
            throw new BusinessException("MERCHANT_NOT_FOUND", "商家不存在");
        }
        
        log.debug("成功获取商家信息: {}", merchant.getName());
        return merchant;
    }
    
    /**
     * 验证商家是否有权限访问指定餐厅
     */
    public void validateRestaurantAccess(Long restaurantId) {
        Merchant currentMerchant = getCurrentMerchant();
        if (!currentMerchant.getRestaurantId().equals(restaurantId)) {
            throw new BusinessException("ACCESS_DENIED", "无权限访问该餐厅");
        }
    }
    
    /**
     * 验证商家角色权限
     */
    public void validateRole(Merchant.MerchantRole requiredRole) {
        Merchant currentMerchant = getCurrentMerchant();
        
        // 管理员拥有所有权限
        if (currentMerchant.getRole() == Merchant.MerchantRole.ADMIN) {
            return;
        }
        
        // 经理可以访问除管理员功能外的所有功能
        if (currentMerchant.getRole() == Merchant.MerchantRole.MANAGER && 
            requiredRole != Merchant.MerchantRole.ADMIN) {
            return;
        }
        
        // 店员权限最低
        if (currentMerchant.getRole() == Merchant.MerchantRole.STAFF && 
            requiredRole == Merchant.MerchantRole.STAFF) {
            return;
        }
        
        throw new BusinessException("INSUFFICIENT_PERMISSIONS", "权限不足");
    }
    
    /**
     * 商家登录结果类
     */
    @Data
    @AllArgsConstructor
    public static class MerchantLoginResult {
        private String token;
        private Merchant merchant;
    }
}
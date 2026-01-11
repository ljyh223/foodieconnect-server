package com.ljyh.foodieconnect.service;

import com.ljyh.foodieconnect.entity.Merchant;
import com.ljyh.foodieconnect.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 商家用户详情服务实现类
 */
@Slf4j
@Service("merchantUserDetailsServiceImpl")
@RequiredArgsConstructor
public class MerchantUserDetailsServiceImpl implements UserDetailsService {
    
    private final MerchantMapper merchantMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("正在加载商家用户: {}", username);
        
        Merchant merchant = merchantMapper.findByUsername(username);
        if (merchant == null) {
            log.warn("商家用户不存在: {}", username);
            throw new UsernameNotFoundException("商家用户不存在: " + username);
        }
        
        if (merchant.getStatus() != Merchant.MerchantStatus.ACTIVE) {
            log.warn("商家用户已被禁用: {}", username);
            throw new UsernameNotFoundException("商家用户已被禁用: " + username);
        }
        
        // 根据角色设置权限
        String authority = "ROLE_" + merchant.getRole().name();
        
        return User.builder()
                .username(merchant.getUsername())
                .password(merchant.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authority)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
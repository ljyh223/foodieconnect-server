package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.entity.User;
import com.ljyh.tabletalk.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * 用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userMapper.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("用户不存在: " + email);
        }
        
        User user = userOptional.get();
        
        // 构建UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(user.getStatus().name().equals("BANNED"))
                .credentialsExpired(false)
                .disabled(user.getStatus().name().equals("INACTIVE"))
                .build();
    }
}
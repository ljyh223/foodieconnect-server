package com.ljyh.foodieconnect.config;

import com.ljyh.foodieconnect.service.MerchantUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 商户端安全配置类
 * 专门处理商家用户的认证和授权组件
 */
@Configuration
@RequiredArgsConstructor
public class MerchantSecurityConfig {
    
    private final MerchantUserDetailsServiceImpl merchantUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 商户端认证提供者
     */
    @Bean
    @Qualifier("merchantAuthenticationProvider")
    public AuthenticationProvider merchantAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(merchantUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    /**
     * 商户端认证管理器
     * 使用商户认证提供者创建专门的认证管理器
     */
    @Bean
    @Qualifier("merchantAuthenticationManager")
    public AuthenticationManager merchantAuthenticationManager() {
        return authentication -> {
            // 使用商户认证提供者进行认证
            return merchantAuthenticationProvider().authenticate(authentication);
        };
    }
}
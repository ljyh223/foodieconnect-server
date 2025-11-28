package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtService;
import com.ljyh.tabletalk.service.JwtMerchantService;
import com.ljyh.tabletalk.service.MerchantUserDetailsServiceImpl;
import com.ljyh.tabletalk.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 主安全配置类
 * 统一管理所有安全配置，包括用户端和商户端的认证授权
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtService jwtService;
    private final JwtMerchantService jwtMerchantService;
    private final UserDetailsServiceImpl userDetailsService;
    private final MerchantUserDetailsServiceImpl merchantUserDetailsService;
    
    private final AuthenticationProvider userAuthenticationProvider;
    private final AuthenticationProvider merchantAuthenticationProvider;
    
    /**
     * 主安全过滤器链配置
     * 统一处理用户端和商户端的认证授权
     */
    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 用户端公开接口
                .requestMatchers(
                    "/auth/**",
                    "/restaurants/**",
                    "/staff/**",
                    "/uploads/**"
                ).permitAll()
                // 商户端公开接口
                .requestMatchers(
                    "/merchant/auth/**"
                ).permitAll()
                // 通用公开接口
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/api-docs/**",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/actuator/health",
                    "/ws/**"
                ).permitAll()
                // 用户端需要认证的接口
                .requestMatchers(
                    "/chat/**",
                    "/users/**",
                    "/upload/**"
                ).authenticated()
                // 商户端需要认证的接口
                .requestMatchers(
                    "/merchant/**"
                ).authenticated()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            .authenticationProvider(userAuthenticationProvider)
            .authenticationProvider(merchantAuthenticationProvider)
            // 添加JWT过滤器 - 商户端过滤器先执行
            .addFilterBefore(jwtMerchantAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    
    /**
     * 用户端JWT认证过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
    
    /**
     * 商户端JWT认证过滤器
     */
    @Bean
    public JwtMerchantAuthenticationFilter jwtMerchantAuthenticationFilter() {
        return new JwtMerchantAuthenticationFilter(jwtMerchantService, merchantUserDetailsService);
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
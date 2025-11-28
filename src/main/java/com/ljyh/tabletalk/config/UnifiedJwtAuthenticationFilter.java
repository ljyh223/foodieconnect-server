package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtService;
import com.ljyh.tabletalk.service.JwtMerchantService;
import com.ljyh.tabletalk.service.MerchantUserDetailsServiceImpl;
import com.ljyh.tabletalk.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 统一的JWT认证过滤器
 * 根据请求路径决定使用用户端还是商户端的JWT服务
 */
@Slf4j
// @Component - 已禁用，暂时移除所有JWT过滤器
public class UnifiedJwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final JwtMerchantService jwtMerchantService;
    private final UserDetailsServiceImpl userDetailsService;
    private final MerchantUserDetailsServiceImpl merchantUserDetailsService;
    
    public UnifiedJwtAuthenticationFilter(JwtService jwtService,
                                     JwtMerchantService jwtMerchantService,
                                     @Qualifier("userDetailsServiceImpl") UserDetailsServiceImpl userDetailsService,
                                     @Qualifier("merchantUserDetailsServiceImpl") MerchantUserDetailsServiceImpl merchantUserDetailsService) {
        this.jwtService = jwtService;
        this.jwtMerchantService = jwtMerchantService;
        this.userDetailsService = userDetailsService;
        this.merchantUserDetailsService = merchantUserDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String jwt = getJwtFromRequest(request);
        
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 根据请求路径选择认证方式
            if (requestURI.startsWith("/merchant/")) {
                // 商户端认证
                authenticateMerchant(jwt, request);
            } else {
                // 用户端认证
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            log.warn("JWT认证失败: {} - {}", requestURI, e.getMessage());
            // 认证失败时不设置认证上下文，让请求继续处理
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 用户端认证
     */
    private void authenticateUser(String jwt, HttpServletRequest request) {
        try {
            String userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("用户端JWT认证成功: {}", userEmail);
                } else {
                    log.warn("用户端JWT令牌无效: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.warn("用户端JWT认证失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 商户端认证
     */
    private void authenticateMerchant(String jwt, HttpServletRequest request) {
        try {
            String username = jwtMerchantService.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = merchantUserDetailsService.loadUserByUsername(username);
                
                if (jwtMerchantService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("商户端JWT认证成功: {}", username);
                } else {
                    log.warn("商户端JWT令牌无效: {}", username);
                }
            }
        } catch (Exception e) {
            log.warn("商户端JWT认证失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 从请求中获取JWT
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtService;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
// @Component - 已禁用，使用 UnifiedJwtAuthenticationFilter 替代
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtService jwtService,
                               @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 完全跳过所有商家端API请求
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/merchant/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 非商家请求继续处理
        final String authHeader = request.getHeader("Authorization");
        
        // 检查Authorization头是否存在且格式正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        
        try {
            String userEmail = jwtService.extractUsername(jwt);
            
            // 如果用户名不为空且当前上下文中没有认证信息
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                // 验证令牌是否有效
                if (jwtService.isTokenValid(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT认证成功: {}", userEmail);
                } else {
                    log.warn("JWT令牌无效: {}", userEmail);
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            // 不设置认证上下文，让请求继续处理，但用户将不被认证
        } catch (Exception e) {
            log.warn("JWT令牌解析失败: {}", e.getMessage());
            // 不设置认证上下文，让请求继续处理
        }
        
        filterChain.doFilter(request, response);
    }
}
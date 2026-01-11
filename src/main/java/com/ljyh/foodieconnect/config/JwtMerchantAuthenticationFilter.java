package com.ljyh.foodieconnect.config;

import com.ljyh.foodieconnect.service.JwtMerchantService;
import com.ljyh.foodieconnect.service.MerchantUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 商家JWT认证过滤器
 */
@Slf4j
// @Component - 已禁用，使用 UnifiedJwtAuthenticationFilter 替代
public class JwtMerchantAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtMerchantService jwtMerchantService;
    private final MerchantUserDetailsServiceImpl merchantUserDetailsService;
    
    public JwtMerchantAuthenticationFilter(JwtMerchantService jwtMerchantService,
                                      @Qualifier("merchantUserDetailsServiceImpl") MerchantUserDetailsServiceImpl merchantUserDetailsService) {
        this.jwtMerchantService = jwtMerchantService;
        this.merchantUserDetailsService = merchantUserDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
        
        // 只处理商家端API请求
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/merchant/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 对于商户端请求，清除任何可能存在的用户端认证信息
        // 确保商户端请求只使用商户认证
        SecurityContextHolder.clearContext();
        
        String jwt = getJwtFromRequest(request);
        
        // 商家请求必须有token
        if (!StringUtils.hasText(jwt)) {
            log.warn("商户端请求缺少JWT token: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 验证token格式和签名
            if (!jwtMerchantService.validateToken(jwt)) {
                log.warn("商户JWT token无效或已过期: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
            
            String username = jwtMerchantService.extractUsername(jwt);
            
            // 验证用户是否存在且状态正常
            UserDetails userDetails = merchantUserDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("商户端过滤器 - 商家认证成功: {}", username);
        } catch (Exception e) {
            log.warn("商户JWT认证失败: {} - {}", requestURI, e.getMessage());
            // 认证失败时不设置认证上下文，直接继续执行
            // 后续的授权检查会拒绝未认证的请求
        }
        
        filterChain.doFilter(request, response);
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
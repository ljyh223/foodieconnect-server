package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtMerchantService;
import com.ljyh.tabletalk.service.MerchantUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
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
        
        // 强制输出日志，确认过滤器是否执行
        log.info("商户端过滤器 - 开始执行，请求URI: {}", request.getRequestURI());
        
        // 只处理商家端API请求
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/merchant/")) {
            log.debug("商户端过滤器 - 跳过非商户端请求: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        log.info("商户端过滤器 - 处理商户端请求: {}", requestURI);
        
        // 对于商户端请求，清除任何可能存在的用户端认证信息
        // 确保商户端请求只使用商户认证
        SecurityContextHolder.clearContext();
        
        try {
            String jwt = getJwtFromRequest(request);
            log.debug("商户端过滤器 - 请求URI: {}, JWT存在: {}", requestURI, StringUtils.hasText(jwt));
            
            if (StringUtils.hasText(jwt)) {
                // 验证token格式和签名
                if (!jwtMerchantService.validateToken(jwt)) {
                    log.warn("商户JWT token无效或已过期");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                String username = jwtMerchantService.extractUsername(jwt);
                log.debug("商户端过滤器 - 从JWT token中提取用户名: {}", username);
                
                // 验证用户是否存在且状态正常
                UserDetails userDetails;
                try {
                    userDetails = merchantUserDetailsService.loadUserByUsername(username);
                    log.debug("商户端过滤器 - 成功加载商家用户详情: {}", username);
                } catch (Exception e) {
                    log.warn("商户端过滤器 - 商家用户不存在或已被禁用: {} - {}", username, e.getMessage());
                    filterChain.doFilter(request, response);
                    return;
                }
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.info("商户端过滤器 - 商家认证成功: {}", username);
            } else {
                log.debug("商户端过滤器 - 没有找到商户JWT token");
            }
        } catch (io.jsonwebtoken.SignatureException e) {
            log.warn("商户JWT签名无效: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("商户JWT token已过期: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("商户JWT令牌解析失败: {}", e.getMessage());
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
package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtService;
import com.ljyh.tabletalk.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        userDetails = mock(UserDetails.class);
    }

    @Test
    void testDoFilterInternal_SkipsMerchantRequests() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/merchant/chat-rooms");
        request.addHeader("Authorization", "Bearer test-token");

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtService, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ProcessesUserRequests() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/chat/rooms");
        request.addHeader("Authorization", "Bearer valid-user-token");

        // 模拟服务调用
        when(jwtService.extractUsername("valid-user-token")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(jwtService.isTokenValid("valid-user-token")).thenReturn(true);

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtService).extractUsername("valid-user-token");
        verify(userDetailsService).loadUserByUsername("user@test.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/chat/rooms");
        // 没有Authorization头

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidTokenFormat() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/chat/rooms");
        request.addHeader("Authorization", "InvalidTokenFormat");

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }
}

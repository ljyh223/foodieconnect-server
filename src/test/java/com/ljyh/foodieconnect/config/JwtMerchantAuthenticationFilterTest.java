package com.ljyh.tabletalk.config;

import com.ljyh.tabletalk.service.JwtMerchantService;
import com.ljyh.tabletalk.service.MerchantUserDetailsServiceImpl;
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
public class JwtMerchantAuthenticationFilterTest {

    @Mock
    private JwtMerchantService jwtMerchantService;

    @Mock
    private MerchantUserDetailsServiceImpl merchantUserDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtMerchantAuthenticationFilter jwtMerchantAuthenticationFilter;

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
    void testDoFilterInternal_SkipsNonMerchantRequests() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/chat/rooms");
        request.addHeader("Authorization", "Bearer test-token");

        // 执行测试
        jwtMerchantAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtMerchantService, never()).validateToken(anyString());
        verify(merchantUserDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ProcessesMerchantRequestsWithValidToken() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/merchant/chat-rooms");
        request.addHeader("Authorization", "Bearer valid-merchant-token");

        // 模拟服务调用
        when(jwtMerchantService.validateToken("valid-merchant-token")).thenReturn(true);
        when(jwtMerchantService.extractUsername("valid-merchant-token")).thenReturn("merchant@test.com");
        when(merchantUserDetailsService.loadUserByUsername("merchant@test.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);

        // 执行测试
        jwtMerchantAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtMerchantService).validateToken("valid-merchant-token");
        verify(jwtMerchantService).extractUsername("valid-merchant-token");
        verify(merchantUserDetailsService).loadUserByUsername("merchant@test.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_MerchantRequestWithoutToken() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/merchant/chat-rooms");
        // 没有Authorization头

        // 执行测试
        jwtMerchantAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtMerchantService, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidMerchantToken() throws ServletException, IOException {
        // 准备测试数据
        request.setRequestURI("/merchant/chat-rooms");
        request.addHeader("Authorization", "Bearer invalid-merchant-token");

        // 模拟服务调用
        when(jwtMerchantService.validateToken("invalid-merchant-token")).thenReturn(false);

        // 执行测试
        jwtMerchantAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(jwtMerchantService).validateToken("invalid-merchant-token");
        verify(jwtMerchantService, never()).extractUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }
}

package com.ljyh.foodieconnect.config;

import com.ljyh.foodieconnect.websocket.ProtobufMessageConverter;
import com.ljyh.foodieconnect.websocket.BinaryChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * WebSocket配置类
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，用于向客户端发送消息
        config.enableSimpleBroker("/topic", "/queue");
        // 设置应用程序前缀，用于过滤目标地址
        config.setApplicationDestinationPrefixes("/app");
        // 设置用户目的地前缀
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，允许跨域访问，支持两种路径：带context-path和不带context-path
        
        // 不带context-path的端点（兼容旧客户端）
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // 不带context-path的纯WebSocket端点
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
        
        // 带context-path的STOMP端点
        registry.addEndpoint("/api/v1/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // 带context-path的纯WebSocket端点
        registry.addEndpoint("/api/v1/ws/chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // 添加protobuf消息转换器
        messageConverters.add(new ProtobufMessageConverter());
        return true; // 不添加默认转换器
    }

    private final BinaryChatWebSocketHandler binaryChatWebSocketHandler;

    public WebSocketConfig(BinaryChatWebSocketHandler binaryChatWebSocketHandler) {
        this.binaryChatWebSocketHandler = binaryChatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册二进制WebSocket处理器，支持两种路径：带context-path和不带context-path，支持路径变量
        registry.addHandler(binaryChatWebSocketHandler, "/ws/chat-bin/**").setAllowedOriginPatterns("*");
        registry.addHandler(binaryChatWebSocketHandler, "/api/v1/ws/chat-bin/**").setAllowedOriginPatterns("*");
    }
}

package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.ChatMessage;
import com.ljyh.tabletalk.entity.ChatSession;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.enums.SenderType;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.ChatMessageMapper;
import com.ljyh.tabletalk.mapper.ChatSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 聊天服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService extends ServiceImpl<ChatSessionMapper, ChatSession> {
    
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final StaffService staffService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 创建聊天会话
     */
    @Transactional
    public ChatSession createSession(Long restaurantId, Long staffId, Long userId) {
        // 检查店员是否存在且在线
        if (!staffService.isStaffOnline(staffId)) {
            throw new BusinessException("CHAT_STAFF_OFFLINE", "店员不在线");
        }
        
        // 检查是否已存在会话
        Optional<ChatSession> existingSession = chatSessionMapper.findByUserIdAndStaffId(userId, staffId);
        if (existingSession.isPresent()) {
            ChatSession session = existingSession.get();
            if (session.getStatus() == ChatSessionStatus.ACTIVE) {
                return session;
            } else {
                // 重新激活会话
                session.setStatus(ChatSessionStatus.ACTIVE);
                chatSessionMapper.updateById(session);
                return session;
            }
        }
        
        // 创建新会话
        ChatSession session = new ChatSession();
        session.setRestaurantId(restaurantId);
        session.setStaffId(staffId);
        session.setUserId(userId);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setUnreadCount(0);
        
        chatSessionMapper.insert(session);
        log.info("创建聊天会话: 用户 {} 与店员 {}", userId, staffId);
        
        return session;
    }
    
    /**
     * 获取用户聊天会话列表
     */
    public List<ChatSession> getUserSessions(Long userId) {
        return chatSessionMapper.findByUserId(userId);
    }
    
    /**
     * 获取店员聊天会话列表
     */
    public List<ChatSession> getStaffSessions(Long staffId) {
        return chatSessionMapper.findByStaffId(staffId);
    }
    
    /**
     * 发送消息
     */
    @Transactional
    public ChatMessage sendMessage(Long sessionId, Long senderId, SenderType senderType, String content, MessageType messageType) {
        // 检查会话是否存在
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("CHAT_SESSION_NOT_FOUND", "聊天会话不存在");
        }
        
        // 检查会话状态
        if (session.getStatus() != ChatSessionStatus.ACTIVE) {
            throw new BusinessException("CHAT_SESSION_CLOSED", "聊天会话已关闭");
        }
        
        // 验证消息长度
        if (content != null && content.length() > 500) {
            throw new BusinessException("CHAT_MESSAGE_TOO_LONG", "消息过长");
        }
        
        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setIsRead(false);
        
        chatMessageMapper.insert(message);
        log.info("发送消息: 会话 {} 发送者 {} 类型 {}", sessionId, senderId, senderType);
        
        // 更新会话最后一条消息
        String currentTime = LocalDateTime.now().format(FORMATTER);
        if (senderType == SenderType.USER) {
            // 用户发送消息，增加未读计数
            chatSessionMapper.updateLastMessage(sessionId, content, currentTime);
        } else {
            // 店员发送消息，更新最后消息但不增加未读计数
            chatSessionMapper.updateById(session);
            session.setLastMessage(content);
            session.setLastMessageTime(currentTime);
            chatSessionMapper.updateById(session);
        }
        
        return message;
    }
    
    /**
     * 获取会话消息列表
     */
    public Page<ChatMessage> getSessionMessages(Long sessionId, int page, int size) {
        Page<ChatMessage> pageParam = new Page<>(page, size);
        return chatMessageMapper.findBySessionId(pageParam, sessionId);
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public void markMessagesAsRead(Long sessionId, Long userId) {
        // 检查会话是否存在且属于该用户
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权访问此会话");
        }
        
        // 标记消息为已读
        chatMessageMapper.markAllAsRead(sessionId);
        
        // 重置未读计数
        chatSessionMapper.resetUnreadCount(sessionId);
        
        log.info("用户 {} 标记会话 {} 消息为已读", userId, sessionId);
    }
    
    /**
     * 关闭聊天会话
     */
    @Transactional
    public void closeSession(Long sessionId, Long userId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BusinessException("ACCESS_DENIED", "无权关闭此会话");
        }
        
        session.setStatus(ChatSessionStatus.CLOSED);
        chatSessionMapper.updateById(session);
        
        log.info("用户 {} 关闭聊天会话 {}", userId, sessionId);
    }
    
    /**
     * 获取会话未读消息数量
     */
    public int getUnreadMessageCount(Long sessionId) {
        return chatMessageMapper.countUnreadMessages(sessionId);
    }
    
    /**
     * 获取用户总未读消息数量
     */
    public int getUserTotalUnreadCount(Long userId) {
        List<ChatSession> sessions = chatSessionMapper.findByUserId(userId);
        return sessions.stream()
                .mapToInt(ChatSession::getUnreadCount)
                .sum();
    }
}
package com.ljyh.tabletalk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.tabletalk.entity.ChatRoom;
import com.ljyh.tabletalk.entity.ChatRoomMember;
import com.ljyh.tabletalk.entity.ChatRoomMessage;
import com.ljyh.tabletalk.enums.ChatSessionStatus;
import com.ljyh.tabletalk.enums.MessageType;
import com.ljyh.tabletalk.exception.BusinessException;
import com.ljyh.tabletalk.mapper.ChatRoomMapper;
import com.ljyh.tabletalk.mapper.ChatRoomMemberMapper;
import com.ljyh.tabletalk.mapper.ChatRoomMessageMapper;
import com.ljyh.tabletalk.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 聊天室服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService extends ServiceImpl<ChatRoomMapper, ChatRoom> {
    
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomMemberMapper chatRoomMemberMapper;
    private final ChatRoomMessageMapper chatRoomMessageMapper;
    private final UserMapper userMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 通过验证码加入聊天室
     */
    @Transactional
    public ChatRoom joinRoomByVerificationCode(Long restaurantId, String verificationCode, Long userId) {
        // 验证验证码
        ChatRoom chatRoom = chatRoomMapper.findByRestaurantIdAndVerificationCode(restaurantId, verificationCode);
        if (chatRoom == null) {
            throw new BusinessException("INVALID_VERIFICATION_CODE", "验证码无效");
        }
        
        // 检查聊天室状态
        if (chatRoom.getStatus() != ChatSessionStatus.ACTIVE) {
            throw new BusinessException("CHAT_ROOM_INACTIVE", "聊天室未激活");
        }
        
        // 检查是否已经是成员（如果是成员，直接返回聊天室信息，不需要重复添加）
        if (chatRoomMemberMapper.isRoomMember(chatRoom.getId(), userId)) {
            log.info("用户 {} 已经是聊天室 {} 的成员", userId, chatRoom.getId());
            return chatRoom;
        }
        
        // 加入聊天室
        ChatRoomMember member = new ChatRoomMember();
        member.setRoomId(chatRoom.getId());
        member.setUserId(userId);
        member.setJoinedAt(LocalDateTime.now());
        member.setIsOnline(true);
        
        chatRoomMemberMapper.insert(member);
        
        // 更新在线用户数量
        Integer onlineCount = chatRoomMemberMapper.countOnlineMembers(chatRoom.getId());
        chatRoom.setOnlineUserCount(onlineCount + 1);
        chatRoomMapper.updateById(chatRoom);
        
        log.info("用户 {} 通过验证码 {} 加入聊天室 {}", userId, verificationCode, chatRoom.getId());
        
        return chatRoom;
    }
    
    /**
     * 发送聊天室消息
     */
    @Transactional
    public ChatRoomMessage sendMessage(Long roomId, Long senderId, String content) {
        // 验证消息长度
        if (content != null && content.length() > 500) {
            throw new BusinessException("CHAT_MESSAGE_TOO_LONG", "消息过长");
        }
        
        // 检查是否是聊天室成员
        if (!chatRoomMemberMapper.isRoomMember(roomId, senderId)) {
            throw new BusinessException("NOT_ROOM_MEMBER", "您不是聊天室成员");
        }
        
        // 创建消息
        ChatRoomMessage message = new ChatRoomMessage();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(MessageType.TEXT);
        
        chatRoomMessageMapper.insert(message);
        log.info("发送聊天室消息: 房间 {} 发送者 {}", roomId, senderId);
        
        // 更新聊天室最后一条消息
        LocalDateTime currentTime = LocalDateTime.now();
        Integer onlineCount = chatRoomMemberMapper.countOnlineMembers(roomId);
        chatRoomMapper.updateLastMessage(roomId, content, currentTime, onlineCount);
        
        return message;
    }
    
    /**
     * 获取聊天室消息列表
     */
    public Page<ChatRoomMessage> getRoomMessages(Long roomId, int page, int size) {
        Page<ChatRoomMessage> pageParam = new Page<>(page, size);
        
        // 检查是否是聊天室成员
        // 这里可以根据实际需求决定是否需要权限检查
        
        return chatRoomMessageMapper.findByRoomId(pageParam, roomId);
    }
    
    /**
     * 获取聊天室成员列表
     */
    public List<ChatRoomMember> getRoomMembers(Long roomId) {
        return chatRoomMemberMapper.findByRoomId(roomId);
    }
    
    /**
     * 用户离开聊天室
     */
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        // 检查是否是聊天室成员
        if (!chatRoomMemberMapper.isRoomMember(roomId, userId)) {
            throw new BusinessException("NOT_ROOM_MEMBER", "您不是聊天室成员");
        }
        
        // 设置为离线
        chatRoomMemberMapper.updateOnlineStatus(roomId, userId, false);
        
        // 更新在线用户数量
        Integer onlineCount = chatRoomMemberMapper.countOnlineMembers(roomId);
        ChatRoom chatRoom = chatRoomMapper.selectById(roomId);
        chatRoom.setOnlineUserCount(onlineCount - 1);
        chatRoomMapper.updateById(chatRoom);
        
        log.info("用户 {} 离开聊天室 {}", userId, roomId);
    }
    
    /**
     * 获取餐厅聊天室
     */
    public ChatRoom getRestaurantChatRoom(Long restaurantId) {
        return chatRoomMapper.findByRestaurantId(restaurantId);
    }
    
    /**
     * 设置用户为离线（当用户断开连接时调用）
     */
    @Transactional
    public void setUserOffline(Long roomId, Long userId) {
        // 只更新当前用户的在线状态
        chatRoomMemberMapper.updateOnlineStatus(roomId, userId, false);
        
        // 更新在线用户数量
        Integer onlineCount = chatRoomMemberMapper.countOnlineMembers(roomId);
        ChatRoom chatRoom = chatRoomMapper.selectById(roomId);
        chatRoom.setOnlineUserCount(onlineCount);
        chatRoomMapper.updateById(chatRoom);
    }
    
    /**
     * 设置用户为在线（当用户连接时调用）
     */
    @Transactional
    public void setUserOnline(Long roomId, Long userId) {
        // 只更新当前用户的在线状态
        chatRoomMemberMapper.updateOnlineStatus(roomId, userId, true);
        
        // 更新在线用户数量
        Integer onlineCount = chatRoomMemberMapper.countOnlineMembers(roomId);
        ChatRoom chatRoom = chatRoomMapper.selectById(roomId);
        chatRoom.setOnlineUserCount(onlineCount);
        chatRoomMapper.updateById(chatRoom);
    }
}
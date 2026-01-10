package com.ljyh.tabletalk.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private ChatRoomMapper chatRoomMapper;

    @Mock
    private ChatRoomMemberMapper chatRoomMemberMapper;

    @Mock
    private ChatRoomMessageMapper chatRoomMessageMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private ChatRoom chatRoom;
    private ChatRoomMember member;
    private ChatRoomMember observer;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setRestaurantId(100L);
        chatRoom.setVerificationCode("123456");
        chatRoom.setStatus(ChatSessionStatus.ACTIVE);
        chatRoom.setVerificationCodeGeneratedAt(LocalDateTime.now());

        // 正式成员
        member = new ChatRoomMember();
        member.setId(1L);
        member.setRoomId(1L);
        member.setUserId(1L);
        member.setJoinedAt(LocalDateTime.now());
        member.setIsOnline(true);
        member.setRole("MEMBER");

        // 观察者成员
        observer = new ChatRoomMember();
        observer.setId(2L);
        observer.setRoomId(1L);
        observer.setUserId(2L);
        observer.setJoinedAt(LocalDateTime.now());
        observer.setIsOnline(true);
        observer.setRole("OBSERVER");
    }

    @Test
    void testJoinRoomAsObserver() {
        // 模拟服务调用
        when(chatRoomMapper.findByRestaurantId(anyLong())).thenReturn(chatRoom);

        // 执行测试
        ChatRoom result = chatRoomService.joinRoomAsObserver(100L, 2L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testSendMessageByMemberSuccess() {
        // 模拟服务调用
        when(chatRoomMemberMapper.findMemberByRoomIdAndUserId(anyLong(), anyLong())).thenReturn(member);
        when(chatRoomMessageMapper.insert(any(ChatRoomMessage.class))).thenReturn(1);
        when(chatRoomMemberMapper.countOnlineMembers(anyLong())).thenReturn(1);

        // 执行测试
        ChatRoomMessage result = chatRoomService.sendMessage(1L, 1L, "测试消息");

        // 验证结果
        assertNotNull(result);
        assertEquals("测试消息", result.getContent());
        assertEquals(MessageType.TEXT, result.getMessageType());
    }

    @Test
    void testSendMessageByObserverFailed() {
        // 模拟服务调用
        when(chatRoomMemberMapper.findMemberByRoomIdAndUserId(anyLong(), anyLong())).thenReturn(observer);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            chatRoomService.sendMessage(1L, 2L, "测试消息");
        });

        // 验证异常信息
        assertEquals("NO_PERMISSION", exception.getCode());
        assertEquals("您没有发送消息的权限", exception.getMessage());
    }

    @Test
    void testSendMessageByNonMemberFailed() {
        // 模拟服务调用
        when(chatRoomMemberMapper.findMemberByRoomIdAndUserId(anyLong(), anyLong())).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            chatRoomService.sendMessage(1L, 3L, "测试消息");
        });

        // 验证异常信息
        assertEquals("NOT_ROOM_MEMBER", exception.getCode());
        assertEquals("您不是聊天室成员", exception.getMessage());
    }

    @Test
    void testJoinRoomByVerificationCodeSuccess() {
        // 模拟服务调用
        when(chatRoomMapper.findByRestaurantId(anyLong())).thenReturn(chatRoom);
        when(chatRoomMemberMapper.isRoomMember(anyLong(), anyLong())).thenReturn(false);
        when(chatRoomMemberMapper.countOnlineMembers(anyLong())).thenReturn(0);

        // 执行测试
        ChatRoom result = chatRoomService.joinRoomByVerificationCode(100L, "123456", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}

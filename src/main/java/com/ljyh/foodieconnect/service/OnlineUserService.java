package com.ljyh.foodieconnect.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljyh.foodieconnect.entity.OnlineUser;
import com.ljyh.foodieconnect.mapper.OnlineUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 在线用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserService extends ServiceImpl<OnlineUserMapper, OnlineUser> {
    
    private final OnlineUserMapper onlineUserMapper;
    
    /**
     * 添加在线用户
     */
    @Transactional
    public OnlineUser addOnlineUser(Long userId, Long roomId, String sessionId) {
        // 检查是否是观察者（负数ID），如果是则跳过数据库操作
        if (userId < 0) {
            log.info("观察者添加在线用户: 用户ID {}, 聊天室ID {}, 会话ID {}", userId, roomId, sessionId);
            return null; // 观察者不需要返回在线用户对象
        }
        
        // 检查用户是否已经在线
        OnlineUser existingUser = onlineUserMapper.findByUserIdAndRoomId(userId, roomId);
        if (existingUser != null) {
            // 如果已在线，更新会话ID和最后活动时间
            existingUser.setSessionId(sessionId);
            existingUser.setLastActiveAt(LocalDateTime.now());
            onlineUserMapper.updateById(existingUser);
            log.info("更新在线用户会话: 用户ID {}, 聊天室ID {}, 会话ID {}", userId, roomId, sessionId);
            return existingUser;
        }
        
        // 创建新的在线用户记录
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setUserId(userId);
        onlineUser.setRoomId(roomId);
        onlineUser.setSessionId(sessionId);
        onlineUser.setConnectedAt(LocalDateTime.now());
        onlineUser.setLastActiveAt(LocalDateTime.now());
        
        onlineUserMapper.insert(onlineUser);
        log.info("添加在线用户: 用户ID {}, 聊天室ID {}, 会话ID {}", userId, roomId, sessionId);
        
        return onlineUser;
    }
    
    /**
     * 根据会话ID获取在线用户
     */
    public OnlineUser getOnlineUserBySessionId(String sessionId) {
        return onlineUserMapper.findBySessionId(sessionId);
    }
    
    /**
     * 获取聊天室在线用户列表
     */
    public List<OnlineUser> getOnlineUsersByRoomId(Long roomId) {
        return onlineUserMapper.findByRoomId(roomId);
    }
    
    /**
     * 移除在线用户（根据会话ID）
     */
    @Transactional
    public boolean removeOnlineUserBySessionId(String sessionId) {
        int result = onlineUserMapper.deleteBySessionId(sessionId);
        if (result > 0) {
            log.info("移除在线用户: 会话ID {}", sessionId);
            return true;
        }
        log.info("观察者移除在线用户: 会话ID {}", sessionId);
        return false;
    }
    
    /**
     * 移除在线用户（根据用户ID和聊天室ID）
     */
    @Transactional
    public boolean removeOnlineUserByUserIdAndRoomId(Long userId, Long roomId) {
        // 检查是否是观察者（负数ID），如果是则跳过数据库操作
        if (userId < 0) {
            log.info("观察者移除在线用户: 用户ID {}, 聊天室ID {}", userId, roomId);
            return false;
        }
        
        int result = onlineUserMapper.deleteByUserIdAndRoomId(userId, roomId);
        if (result > 0) {
            log.info("移除在线用户: 用户ID {}, 聊天室ID {}", userId, roomId);
            return true;
        }
        return false;
    }
    
    /**
     * 更新用户最后活动时间
     */
    @Transactional
    public void updateLastActiveTime(String sessionId) {
        OnlineUser onlineUser = onlineUserMapper.findBySessionId(sessionId);
        if (onlineUser != null) {
            onlineUser.setLastActiveAt(LocalDateTime.now());
            onlineUserMapper.updateById(onlineUser);
        } else {
            log.info("观察者更新最后活动时间: 会话ID {}", sessionId);
        }
    }
    
    /**
     * 统计聊天室在线用户数量
     */
    public int countOnlineUsersByRoomId(Long roomId) {
        return onlineUserMapper.countByRoomId(roomId);
    }
}
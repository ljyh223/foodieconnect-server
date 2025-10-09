package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.ChatSession;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话Mapper接口
 */
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    
    /**
     * 根据用户ID查询聊天会话列表
     */
    @Select("SELECT cs.*, r.name as restaurantName, s.name as staffName, s.avatar_url as staffAvatar " +
            "FROM chat_sessions cs " +
            "LEFT JOIN restaurants r ON cs.restaurant_id = r.id " +
            "LEFT JOIN staff s ON cs.staff_id = s.id " +
            "WHERE cs.user_id = #{userId} " +
            "ORDER BY cs.last_message_time DESC")
    List<ChatSession> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和店员ID查询会话
     */
    @Select("SELECT * FROM chat_sessions WHERE user_id = #{userId} AND staff_id = #{staffId}")
    Optional<ChatSession> findByUserIdAndStaffId(@Param("userId") Long userId, @Param("staffId") Long staffId);
    
    /**
     * 根据店员ID查询聊天会话列表
     */
    @Select("SELECT cs.*, u.display_name as userName, u.avatar_url as userAvatar " +
            "FROM chat_sessions cs " +
            "LEFT JOIN users u ON cs.user_id = u.id " +
            "WHERE cs.staff_id = #{staffId} " +
            "ORDER BY cs.last_message_time DESC")
    List<ChatSession> findByStaffId(@Param("staffId") Long staffId);
    
    /**
     * 更新最后一条消息
     */
    @Update("UPDATE chat_sessions SET last_message = #{lastMessage}, last_message_time = #{lastMessageTime}, unread_count = unread_count + 1 WHERE id = #{sessionId}")
    int updateLastMessage(@Param("sessionId") Long sessionId, @Param("lastMessage") String lastMessage, @Param("lastMessageTime") String lastMessageTime);
    
    /**
     * 重置未读消息数量
     */
    @Update("UPDATE chat_sessions SET unread_count = 0 WHERE id = #{sessionId}")
    int resetUnreadCount(@Param("sessionId") Long sessionId);
    
    /**
     * 增加未读消息数量
     */
    @Update("UPDATE chat_sessions SET unread_count = unread_count + 1 WHERE id = #{sessionId}")
    int incrementUnreadCount(@Param("sessionId") Long sessionId);
}
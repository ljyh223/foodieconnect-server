package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 根据会话ID分页查询消息
     */
    @Select("SELECT cm.*, " +
            "CASE WHEN cm.sender_type = 'STAFF' THEN s.name ELSE u.display_name END as senderName, " +
            "CASE WHEN cm.sender_type = 'STAFF' THEN s.avatar_url ELSE u.avatar_url END as senderAvatar " +
            "FROM chat_messages cm " +
            "LEFT JOIN staff s ON cm.sender_id = s.id AND cm.sender_type = 'STAFF' " +
            "LEFT JOIN users u ON cm.sender_id = u.id AND cm.sender_type = 'USER' " +
            "WHERE cm.session_id = #{sessionId} " +
            "ORDER BY cm.created_at ASC")
    Page<ChatMessage> findBySessionId(Page<ChatMessage> page, @Param("sessionId") Long sessionId);
    
    /**
     * 根据会话ID查询最新消息
     */
    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} ORDER BY created_at DESC LIMIT 1")
    ChatMessage findLatestBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 标记会话中的所有消息为已读
     */
    @Update("UPDATE chat_messages SET is_read = true WHERE session_id = #{sessionId} AND sender_type = 'STAFF' AND is_read = false")
    int markAllAsRead(@Param("sessionId") Long sessionId);
    
    /**
     * 统计会话中的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_messages WHERE session_id = #{sessionId} AND sender_type = 'STAFF' AND is_read = false")
    int countUnreadMessages(@Param("sessionId") Long sessionId);
    
    /**
     * 根据发送者ID查询消息
     */
    @Select("SELECT * FROM chat_messages WHERE sender_id = #{senderId} AND sender_type = #{senderType} ORDER BY created_at DESC")
    List<ChatMessage> findBySenderIdAndType(@Param("senderId") Long senderId, @Param("senderType") String senderType);
}
package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.tabletalk.entity.ChatRoomMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天室消息Mapper接口
 */
@Mapper
public interface ChatRoomMessageMapper extends BaseMapper<ChatRoomMessage> {
    
    /**
     * 根据聊天室ID分页查询消息（包含发送者信息）
     */
    @Select("SELECT crm.*, u.display_name as senderName, u.avatar_url as senderAvatar " +
            "FROM chat_room_messages crm " +
            "LEFT JOIN users u ON crm.sender_id = u.id " +
            "WHERE crm.room_id = #{roomId} " +
            "ORDER BY crm.created_at DESC")
    Page<ChatRoomMessage> findByRoomId(Page<ChatRoomMessage> page, @Param("roomId") Long roomId);
    
    /**
     * 获取聊天室最新消息列表
     */
    @Select("SELECT crm.*, u.display_name as senderName, u.avatar_url as senderAvatar " +
            "FROM chat_room_messages crm " +
            "LEFT JOIN users u ON crm.sender_id = u.id " +
            "WHERE crm.room_id = #{roomId} " +
            "ORDER BY crm.created_at DESC " +
            "LIMIT #{limit}")
    List<ChatRoomMessage> findLatestMessagesByRoomId(@Param("roomId") Long roomId, @Param("limit") Integer limit);
}
package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.ChatRoomMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天室成员Mapper接口
 */
@Mapper
public interface ChatRoomMemberMapper extends BaseMapper<ChatRoomMember> {
    
    /**
     * 根据聊天室ID查询成员列表（包含用户信息）
     */
    @Select("SELECT crm.*, u.display_name as userName, u.avatar_url as userAvatar " +
            "FROM chat_room_members crm " +
            "LEFT JOIN users u ON crm.user_id = u.id " +
            "WHERE crm.room_id = #{roomId} " +
            "ORDER BY crm.joined_at ASC")
    List<ChatRoomMember> findByRoomId(@Param("roomId") Long roomId);
    
    /**
     * 检查用户是否是聊天室成员
     */
    @Select("SELECT COUNT(*) FROM chat_room_members WHERE room_id = #{roomId} AND user_id = #{userId}")
    boolean isRoomMember(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 更新用户在线状态
     */
    @Update("UPDATE chat_room_members SET is_online = #{isOnline} WHERE room_id = #{roomId} AND user_id = #{userId}")
    void updateOnlineStatus(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isOnline") Boolean isOnline);
    
    /**
     * 获取聊天室在线成员数量
     */
    @Select("SELECT COUNT(*) FROM chat_room_members WHERE room_id = #{roomId} AND is_online = true")
    Integer countOnlineMembers(@Param("roomId") Long roomId);
    
    /**
     * 离开聊天室（设置所有成员为离线）
     */
    @Update("UPDATE chat_room_members SET is_online = false WHERE room_id = #{roomId}")
    void setAllMembersOffline(@Param("roomId") Long roomId);
    
    /**
     * 根据聊天室ID和用户ID查询成员信息
     */
    @Select("SELECT * FROM chat_room_members WHERE room_id = #{roomId} AND user_id = #{userId}")
    ChatRoomMember findMemberByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
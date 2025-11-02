package com.ljyh.tabletalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.tabletalk.entity.OnlineUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 在线用户Mapper接口
 */
@Mapper
public interface OnlineUserMapper extends BaseMapper<OnlineUser> {
    
    /**
     * 根据聊天室ID查询在线用户列表
     */
    @Select("SELECT ou.*, u.display_name as userName, u.avatar_url as userAvatar " +
            "FROM online_users ou " +
            "LEFT JOIN users u ON ou.user_id = u.id " +
            "WHERE ou.room_id = #{roomId}")
    List<OnlineUser> findByRoomId(@Param("roomId") Long roomId);
    
    /**
     * 根据用户ID和聊天室ID查询在线用户
     */
    @Select("SELECT * FROM online_users WHERE user_id = #{userId} AND room_id = #{roomId}")
    OnlineUser findByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);
    
    /**
     * 根据会话ID查询在线用户
     */
    @Select("SELECT ou.*, u.display_name as userName, u.avatar_url as userAvatar " +
            "FROM online_users ou " +
            "LEFT JOIN users u ON ou.user_id = u.id " +
            "WHERE ou.session_id = #{sessionId}")
    OnlineUser findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据用户ID和聊天室ID删除在线用户记录
     */
    @Delete("DELETE FROM online_users WHERE user_id = #{userId} AND room_id = #{roomId}")
    int deleteByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);
    
    /**
     * 根据会话ID删除在线用户记录
     */
    @Delete("DELETE FROM online_users WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 统计聊天室在线用户数量
     */
    @Select("SELECT COUNT(*) FROM online_users WHERE room_id = #{roomId}")
    int countByRoomId(@Param("roomId") Long roomId);
}
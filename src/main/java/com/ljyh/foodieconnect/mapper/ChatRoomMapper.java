package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 聊天室Mapper接口
 */
@Mapper
public interface ChatRoomMapper extends BaseMapper<ChatRoom> {
    
    /**
     * 根据餐厅ID查询聊天室
     */
    @Select("SELECT * FROM chat_rooms WHERE restaurant_id = #{restaurantId}")
    ChatRoom findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * 根据验证码查询聊天室
     */
    @Select("SELECT * FROM chat_rooms WHERE verification_code = #{verificationCode}")
    Optional<ChatRoom> findByVerificationCode(@Param("verificationCode") String verificationCode);
    
    /**
     * 根据餐厅ID和验证码查询聊天室
     */
    @Select("SELECT * FROM chat_rooms WHERE restaurant_id = #{restaurantId} AND verification_code = #{verificationCode}")
    ChatRoom findByRestaurantIdAndVerificationCode(@Param("restaurantId") Long restaurantId, @Param("verificationCode") String verificationCode);
    
    /**
     * 更新聊天室最后一条消息
     */
    @Update("UPDATE chat_rooms SET last_message = #{lastMessage}, last_message_time = #{lastMessageTime}, online_user_count = #{onlineUserCount} WHERE id = #{roomId}")
    void updateLastMessage(@Param("roomId") Long roomId, @Param("lastMessage") String lastMessage,
                        @Param("lastMessageTime") java.time.LocalDateTime lastMessageTime, @Param("onlineUserCount") Integer onlineUserCount);
    
    /**
     * 更新聊天室验证码
     */
    @Update("UPDATE chat_rooms SET verification_code = #{verificationCode}, verification_code_generated_at = #{generatedAt} WHERE id = #{id}")
    void updateVerificationCode(@Param("id") Long id, @Param("verificationCode") String verificationCode,
                              @Param("generatedAt") java.time.LocalDateTime generatedAt);
}
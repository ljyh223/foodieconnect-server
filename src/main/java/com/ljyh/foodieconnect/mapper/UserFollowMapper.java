package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljyh.foodieconnect.entity.UserFollow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户关注关系Mapper接口
 */
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    
    /**
     * 检查是否已关注
     */
    @Select("SELECT EXISTS(SELECT 1 FROM user_follows WHERE follower_id = #{followerId} AND following_id = #{followingId})")
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    /**
     * 获取用户关注列表
     */
    @Select("SELECT f.*, u.display_name, u.avatar_url FROM user_follows f " +
            "LEFT JOIN users u ON f.following_id = u.id " +
            "WHERE f.follower_id = #{userId} ORDER BY f.created_at DESC")
    Page<UserFollow> findFollowingPage(Page<UserFollow> page, @Param("userId") Long userId);
    
    /**
     * 获取用户粉丝列表
     */
    @Select("SELECT f.*, u.display_name, u.avatar_url FROM user_follows f " +
            "LEFT JOIN users u ON f.follower_id = u.id " +
            "WHERE f.following_id = #{userId} ORDER BY f.created_at DESC")
    Page<UserFollow> findFollowersPage(Page<UserFollow> page, @Param("userId") Long userId);
    
    /**
     * 取消关注
     */
    @Select("DELETE FROM user_follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int unfollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    /**
     * 获取共同关注列表
     */
    @Select("SELECT f1.following_id, u.display_name, u.avatar_url FROM user_follows f1 " +
            "INNER JOIN user_follows f2 ON f1.following_id = f2.following_id " +
            "LEFT JOIN users u ON f1.following_id = u.id " +
            "WHERE f1.follower_id = #{userId1} AND f2.follower_id = #{userId2} " +
            "ORDER BY u.display_name")
    List<UserFollow> findMutualFollowing(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * 获取用户关注数量
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_id = #{userId}")
    int getFollowingCount(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝数量
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE following_id = #{userId}")
    int getFollowersCount(@Param("userId") Long userId);
    
    /**
     * 获取用户关注ID列表
     */
    @Select("SELECT following_id FROM user_follows WHERE follower_id = #{userId}")
    List<Long> getFollowingIds(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝ID列表
     */
    @Select("SELECT follower_id FROM user_follows WHERE following_id = #{userId}")
    List<Long> getFollowersIds(@Param("userId") Long userId);
}
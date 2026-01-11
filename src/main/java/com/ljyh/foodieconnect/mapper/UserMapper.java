package com.ljyh.foodieconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljyh.foodieconnect.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 用户Mapper接口
 */
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND status = 'ACTIVE'")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 获取用户关注数量
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_id = #{userId}")
    Long getFollowingCount(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝数量
     */
    @Select("SELECT COUNT(*) FROM user_follows WHERE following_id = #{userId}")
    Long getFollowersCount(@Param("userId") Long userId);
    
    /**
     * 获取用户推荐餐厅数量
     */
    @Select("SELECT COUNT(*) FROM user_restaurant_recommendations WHERE user_id = #{userId}")
    Long getRecommendationsCount(@Param("userId") Long userId);
}